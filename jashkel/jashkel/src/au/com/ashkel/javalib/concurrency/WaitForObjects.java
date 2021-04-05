/*-------------------------------------------------------------------------*/
/* Copyright (C) 2011 by Ashkel Software                                   */
/* ari@ashkel.com.au                                                       */
/*                                                                         */
/* This file is part of the threadit library.                              */
/*                                                                         */
/* The threadit library is free software; you can redistribute it and/or   */
/* modify it under the terms of The Code Project Open License (CPOL) 1.02  */
/*                                                                         */
/* The threadit library is distributed in the hope that it will be useful, */
/* but WITHOUT ANY WARRANTY; without even the implied warranty of          */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the CPOL       */
/* License for more details.                                               */
/*                                                                         */
/* You should have received a copy of the CPOL License along with this     */
/* software.                                                               */
/*-------------------------------------------------------------------------*/

/*--------------------------------------------------------------------------*/
/* Package declaration.                                                     */
/*--------------------------------------------------------------------------*/
package au.com.ashkel.javalib.concurrency;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * class WaitForObjects provides routines that are similar to the Win32 WaitForMultipleObjects api call.
 * @author Ari Edinburg  
 */
public class WaitForObjects 
{
  /** WAIT_TERMINATED indicates the wait operation has been terminated prior to any completion result. */
  static final public int WAIT_TERMINATED = -1;
  /** WAIT_TIMEDOUT indicates that the operation did not receive one or more signals in time. */
  static final public int WAIT_TIMEDOUT = -2;
  /** WAIT_NOT_INITIALISED indicates that the method call invoked has not been setup for correct operation. */
  static final public int WAIT_NOT_INITIALISED = -3;  
  /** WAIT_SUCCEEDED indicates that the signal has been successfully received. */
  static final public int WAIT_SUCCEEDED = 0;

  /** m_theObjects is the list of waiters that will signal when something has been signalled to specifically to them. */
  private Semaphore[] m_theObjects = null;
  /** m_theReadyQueue is the queue of elements that have been signalled. */  
  private BlockingQueue<Signal>  m_theReadyQueue = new LinkedBlockingQueue<Signal> ();
  /** m_theSignallers is the list of threads that can signal the waiter waiting for a signal. */
  private Hashtable<Semaphore, SemaphoreSignaller> m_theSignallers = new Hashtable<Semaphore, SemaphoreSignaller>(); 

  /**
   * Constructor waitForObjects is an empty implementation.
   */
  public WaitForObjects ()
  {
  } // constructor WaitForMultipleObjects

  /**
   * Constructor waitForObjects is used to specify the list of events that can be signalled
   * to a waiter using one of the methods of this class. 
   */
  public WaitForObjects (Semaphore[] theObjects)
  {
    initWaitForObjects (theObjects);	  
  } // constructor WaitForMultipleObjects

  /**
   * Method initWaitForObjects is used to specify the list of events that can be signalled
   * to a waiter using one of the methods of this class. 
   */
  public void initWaitForObjects (Semaphore[] theObjects)
  {
    // Remove any existing waiters.
    stopWaiting ();	  
    // Now create the support infrastructure for this invocation.
    m_theObjects = new Semaphore[theObjects.length];
    // For every semaphore we need a SemaphoreSignaller.
    for (int theCount = 0; theCount < theObjects.length; theCount++)
    {
      SemaphoreSignaller theThread = new SemaphoreSignaller (theObjects[theCount], theCount);
      m_theSignallers.put (theObjects[theCount], theThread);
      m_theObjects[theCount] = theObjects[theCount];
    } // for
  } // initWaitForMultipleObjects
  
  /**
   * Method signal is a helper provided to signal a waiter based on the semaphore identity 
   * value.
   * @param theSignalId is the semaphore to use to signal the waiter. The identity represents
   * the location of the Semaphore in the waiter list originally setup in a call to 
   * initWaitForObjects or in the constructor.
   * @return true if the semaphore is successfully signalled.
   */
  public boolean signal (int theSignalId)
  {
    boolean isSignalled = false;
    
    if ((m_theObjects != null) && (m_theObjects.length > 0))
    {
      if ((theSignalId >= 0) && (theSignalId < m_theObjects.length))
      {
        Semaphore theSemaphore = m_theObjects[theSignalId];
        theSemaphore.release ();
        isSignalled = true;
      } // if
    } // if 
    return isSignalled;
  } // signal

  /**
   * Method waitForObjects allows a caller to wait for one of the objects (signals) upon which it
   * has been configured to wait on until the object is signalled or the timeout on the wait occurs.
   * @param theTimeOut specifies how long the caller is prepared to wait in milliseconds to get 
   * a signal from any one of the objects it is waiting on. 
   * @return the status of the operation if it did not succeed or alternatively indicate the identity
   * of the signal that has been signalled. The following values are returned:
   * WAIT_TIMEDOUT - the wait has exceeded the specified timeout.
   * WAIT_TERMINATED - the wait has been interrupted.
   * WAIT_NOT_INITIALISED - the wait has not been configured by a call to setup the wait list.
   * WAIT_SUCCEEDED + the SignalId indicates that the signal has occurred and indicates the first
   * occurrence of the signal to have occurred.
   */
  public int waitForObjects (long theTimeOut)
  {
    int theResult = WAIT_TIMEDOUT;
    // Wait for the first notification or timeout.
    if ((m_theObjects == null) || (m_theObjects.length == 0))
    {
      return WAIT_NOT_INITIALISED;
    } // if 
    // wait for an item to arrive
    Signal anItem = null;	  
    try
    {
      anItem = m_theReadyQueue.poll (theTimeOut, TimeUnit.MILLISECONDS);
    } // try
    catch (InterruptedException anInterruptedException)
    {
      theResult = WAIT_TERMINATED;
    } // catch
    // Process the returned result.
    if (anItem != null)
    {
      theResult = anItem.getSignal ();
    } // if 
    // Return the identification of the signalled item. 
    return theResult;
  } // waitForObjects

  /**
   * Method waitForAllObjects allows a caller to wait for all of the objects (signals) upon which it
   * has been configured to wait on until each one of the object is signalled or the timeout on the wait occurs.
   * @param theTimeOut specifies how long the caller is prepared to wait in milliseconds to get 
   * a signal from each one of the objects it is waiting on. 
   * @return the status of the operation which is one of:
   * WAIT_TIMEDOUT - the wait has exceeded the specified timeout.
   * WAIT_TERMINATED - the wait has been interrupted.
   * WAIT_NOT_INITIALISED - the wait has not been configured by a call to setup the wait list.
   * WAIT_SUCCEEDED - all objects have been signalled.
   */
  public int waitForAllObjects (long theTimeOut)
  {
    int theResult = WAIT_TIMEDOUT;
    long theRemainingTime = theTimeOut;
    long theTimeNow = 0;
    long theTimeWhen = System.currentTimeMillis ();
    boolean isExit = false;
    HashSet<Integer> m_theWaitingSet = new HashSet<Integer>();    
    
    if ((m_theObjects == null) || (m_theObjects.length == 0))
    {
      return WAIT_NOT_INITIALISED;
    } // if 
    // Create a set of all objects identities to be waited on.
    for (int theCount = 0; theCount < m_theObjects.length; theCount++)
    {
      m_theWaitingSet.add (new Integer (theCount));
    } // for
    do
    {
      // wait for an item to arrive
      Signal anItem = null;   
      try
      {
        anItem = m_theReadyQueue.poll (theRemainingTime, TimeUnit.MILLISECONDS);
      } // try
      catch (InterruptedException anInterruptedException)
      {
        theResult = WAIT_TERMINATED;
        isExit = true;
      } // catch
      // Process the returned result.
      if (anItem != null)
      {
        m_theWaitingSet.remove (new Integer (anItem.getSignal ()));
        if (m_theWaitingSet.isEmpty())
        {
          theResult = WAIT_SUCCEEDED;
          isExit = true;
        }
        else
        {
          theTimeNow = System.currentTimeMillis ();
          theRemainingTime -= theTimeNow - theTimeWhen;
          if (theRemainingTime > 0)
          {
            theTimeNow = System.currentTimeMillis ();
          } 
          else
          {
            // Timeout has occurred.            
            isExit = true;
          } // if 
        } // if
      }
      else
      {
        // Timeout has occurred.
        isExit = true;
      } // if
    } while (!isExit);
    // Return the result.  
    return theResult;
  } // waitForAllObjects
  
  /**
   * Method stopWaiting is called to stop the process that manages the notification of 
   * a signal and hence implement the waitForObjects and waitForAllObjects methods.
   * If the setup of the operation is performed by calls to initWaitForObjects or in the 
   * constructor (by passing a waiter list) then this method must be called to clear
   * the resources in use.
   */
  public void stopWaiting ()
  {
    Semaphore theSemaphore = null;
    SemaphoreSignaller theSignaller = null;
    
    if ((m_theObjects != null) && (m_theSignallers != null))
    {
      // Remove all the threads that were created.
      for (int i = 0; i < m_theObjects.length; i++)
      {
        theSemaphore = m_theObjects[i];
        theSignaller = m_theSignallers.get (theSemaphore);
        if (theSignaller != null)
        {
          theSignaller.stopSignalling ();
        } // if 
      } // for 
      //  Remove all the elements in the list of signallers.
      m_theSignallers.clear ();
      m_theObjects = null;
    } // if 
  } // stopWaiting

  /**
   * Method waitForObjects allows a caller to wait for one of the objects (signals) upon which it
   * has been configured to wait on until the object is signalled or the timeout on the wait occurs.
   * @param theObjects is the list of objects being waited on.  
   * @param theTimeOut specifies how long the caller is prepared to wait in milliseconds to get 
   * a signal from any one of the objects it is waiting on. 
   * @return the status of the operation if it did not succeed or alternatively indicate the identity
   * of the signal that has been signalled. The following values are returned:
   * WAIT_TIMEDOUT - the wait has exceeded the specified timeout.
   * WAIT_TERMINATED - the wait has been interrupted.
   * WAIT_SUCCEEDED + the SignalId indicates that the signal has occurred and indicates the first
   * occurrence of the signal to have occurred.
   * It is expected that this call will be on average more resource intensive that the corresponding call
   * to waitForObjects (long theTimeOut) as the resources have to be established on a call by call
   * basis rather than being reused.
   */
  public int waitForObjects (Semaphore[] theObjects, long theTimeOut)
  {
    int theResult = 0;
    initWaitForObjects (theObjects);
    theResult = waitForObjects (theTimeOut);    
    stopWaiting ();
    return theResult;
  } // waitForObjects

  /**
   * Method waitForAllObjects allows a caller to wait for all of the objects (signals) upon which it
   * has been configured to wait on until each one of the object is signalled or the timeout on the wait occurs.
   * @param theObjects is the list of objects being waited on. 
   * @param theTimeOut specifies how long the caller is prepared to wait in milliseconds to get 
   * a signal from each one of the objects it is waiting on. 
   * @return the status of the operation which is one of:
   * WAIT_TIMEDOUT - the wait has exceeded the specified timeout.
   * WAIT_TERMINATED - the wait has been interrupted.
   * WAIT_SUCCEEDED - all objects have been signalled.
   * It is expected that this call will be on average more resource intensive that the corresponding call
   * to waitForAllObjects (long theTimeOut)as the resources have to be established on a call by call
   * basis rather than being reused.
   */
  public int waitForAllObjects (Semaphore[] theObjects, long theTimeOut)
  {
    int theResult = 0;
    initWaitForObjects (theObjects);
    theResult = waitForAllObjects (theTimeOut);    
    stopWaiting ();
    return theResult;
  } // waitForAllObjects

  /**
   * Inner Class SemaphoreSignaller is used to implement the signalling mechanism from an object
   * to the caller using the waitForObjects and waitForAllObjects. One instance of this thread
   * is created for each object to be signalled. When the object is signalled the thread wakes
   * and queues a message to the waiting thread using the WaitForObjects or WaitForAllObjects.
   * The processing of the signal is then managed in the WaitForObjects or WaitForAllObjects
   * methods.
   */
  class SemaphoreSignaller extends Thread
  {
    protected Semaphore m_theSemaphore;    
    protected int m_theSemaphoreId;

    /**
     * Constructor SemaphoreSignaller is used to signal the object.
     * @param theSemaphore is the semaphore used to signal that the object state change. 
     * @param theSemaphoreId identifies the semaphore location in the list of 
     * semaphore's provided at initialisation.
     */
    public SemaphoreSignaller (Semaphore theSemaphore, int theSemaphoreId)
    {
      m_theSemaphoreId = theSemaphoreId;
      m_theSemaphore = theSemaphore;
      start();
    } // constructor SemaphoreSignaller

    /**
     * Method stopSignalling is called to terminate the thread associated with this Semaphore.
     */
    public void stopSignalling ()
    {
      this.interrupt ();
    } // stop

    /**
     * Method run is the execution of the thread that waits for the signal.
     * Once the signal occurs then a message indicating which semaphore has signalled
     * is sent to the waiting thread using the WaitForObjects or WaitForAllObjects.
     */
    public void run()
    {
      boolean isExit = false;
      do
      {
        try
        {
          if (m_theSemaphore != null)
          {
            m_theSemaphore.acquire ();
          } // if 
        } // try
        catch (InterruptedException anException)
        {
          isExit = true;
        } // catch
        if (!isExit)
        {
          Signal theSignal = new Signal (m_theSemaphoreId);
          m_theReadyQueue.add (theSignal);
        } // if 
      } while (!isExit);
    } // run

  }; // class SemaphoreSignaller

  /**
   * Inner class Signaller is used to specify that an object has been signalled
   * and transfer this information to the caller using the waitForObjects and waitForAllObjects.
   */
  class Signal 
  {
    /**
     *  m_theSignalId is a reference to the location of the Object that has signalled
     *  in a list of such objects.
     */
    protected int m_theSignalId;

    public Signal (int theSignalId)
    {
      m_theSignalId = theSignalId;	
    } // constructor Signal

    public int getSignal ()
    {
      return m_theSignalId;
    } // getSignal

  } // class Signal

} // class WaitForObjects
