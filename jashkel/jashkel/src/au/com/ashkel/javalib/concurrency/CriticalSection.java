/*-------------------------------------------------------------------------*/
/* Copyright (C) 2008 by Ashkel Software                                   */
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

/*--------------------------------------------------------------------------*/
/* Imported classes.                                                        */
/*--------------------------------------------------------------------------*/
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import au.com.ashkel.javalib.threads.ThreadIt;


public class CriticalSection
{
  /** Logger for this class */
  private static final Logger m_theLogger = Logger.getLogger (CriticalSection.class);

  private Semaphore m_theMonitor = null;
  private java.lang.Thread m_theOwningThread = null;
  private long m_theLockCount = 0;

  public CriticalSection()
  {
    m_theOwningThread = null;
    // create the semaphore with fairness setting set to true
    m_theMonitor = new Semaphore (1, true);
  } // constructor CriticalSection

  //  <li> Multiple acquire calls by the same thread will automatically succeed.
  //  <li> The caller of this method will block until the critical section can
  //  be acquired.
  public boolean acquire ()
  {
	int theError = 0;	  
    String theMsg = null;	
    boolean isAcquired = false;
    boolean isSemaphoreAcquireNeeded = false;
    java.lang.Thread theCallingThread = null;

    theCallingThread = Thread.currentThread ();
    synchronized (this)
    {
      if (m_theOwningThread == null )
      {
   	    isSemaphoreAcquireNeeded = true;
      }
      else
      {
        // Cater for the cases where there are owners of the critical section already.
    	if (!m_theOwningThread.equals (theCallingThread))
    	{
   		  isSemaphoreAcquireNeeded = true;
    	}
    	else
    	{
      	  // We are already the current owner.
    	  // Indicate that we are locking the critical section again.
  		  m_theLockCount++;
    	} // if
      } // if
    } // Synchronised
    if (isSemaphoreAcquireNeeded)
    {
	  isAcquired = false;
      // We have to wait to get hold of the semaphore to make this work.
      m_theMonitor.acquireUninterruptibly ();
	  // We can only acquire the critical section if no one owns it.
      synchronized (this)
      {
        if (m_theOwningThread == null )
        {
   	      isAcquired = true;
          m_theOwningThread = theCallingThread;
          m_theLockCount = 0;
        }
        else
        {
          // We have the semaphore but the critical section is owned by another thread.
      	  // This scenario should never happen as the semaphore will only be released and
      	  // the owning thread will be null. The lock will go to the first thread that
      	  // gets the object's monitor.
          theError = 1;
	    } // if
      } // Synchronised
    } // if
    if (theError > 0)
    {
      if (theError == 1)
      {
   	    theMsg = "MAJOR ERROR: Trying to acquire CriticalSection thats is owned by another thread " + new Long(m_theOwningThread.getId()).toString();
   	    theMsg += " and the calling thread id = " + new Long (theCallingThread.getId()).toString ();
   	    m_theLogger.error (theMsg);
      } // if
    } // if
    // Return the critical section acquire result.
    return isAcquired;
  } // acquire

  public void release ()
  {
	boolean isSemaphoreReleaseRequired = false;
	int theError = 0;
	java.lang.Thread theCallingThread = null;

    theCallingThread = Thread.currentThread ();
    synchronized (this)
    {
      // Only the owner of the critical section can release it.
      if (m_theOwningThread != null)
      {
   	    if (m_theOwningThread.equals (theCallingThread))
        {
   		  if (m_theLockCount == 0)
   		  {
   		    // The critical section can be made available.
  		    m_theOwningThread = null;
  		    isSemaphoreReleaseRequired = true;
   		  }
   		  else
   		  {
            // Reduce the lock count.
 		    m_theLockCount--;
   		  } // if
        }
   	    else
   	    {
 	  	  theError = 1;
   	    } // if
      }
      else
      {
   	    theError = 2;
      } // if
    } // Synchronised
    if (theError > 0)
    {
      if (theError == 1)
      {
  	    m_theLogger.error ("Critical Section: caller is not owning thread for release");
      } // if
      if (theError == 2)
      {
   	    m_theLogger.error ("Critical Section: there is no critical section owned for release");
      } // if
    } // if
    if (isSemaphoreReleaseRequired)
    {
	  m_theMonitor.release ();
    } // if
  } // release

  // returns false if interrupted or times out.
  public boolean acquire (long theTimeOut)
  {
	int theError = 0;
    long theTimeLeft = theTimeOut;
    String theMsg = null;
    boolean isAcquired = false;
    boolean isLocked = false;
    boolean isSemaphoreAcquireNeeded = false;
    java.lang.Thread theCallingThread = null;    

    theCallingThread = Thread.currentThread ();
    synchronized (this)
    {
      if (m_theOwningThread == null )
      {
   	    isSemaphoreAcquireNeeded = true;
      }
      else
      {
        // Cater for the cases where there are owners of the critical section already.
    	if (!m_theOwningThread.equals (theCallingThread))
    	{
   		  isSemaphoreAcquireNeeded = true;
    	}
    	else
    	{
      	  // We are already the current owner.
    	  // Indicate that we are locking the critical section again.
  		  m_theLockCount++;
    	} // if
      } // if
    } // Synchronised
    if (isSemaphoreAcquireNeeded)
    {
	  isAcquired = false;
      // We have to wait to get hold of the semaphore to make this work.
      try
	  {
		isLocked = m_theMonitor.tryAcquire (theTimeLeft, TimeUnit.MILLISECONDS);
	  } // try
	  catch (InterruptedException anInterruptedException)
	  {
   	    m_theLogger.debug ("Interrupted during semaphore tryAcquire", anInterruptedException);
	  } // catch
   	  // We can only acquire the critical section if no one owns it.
	  if (isLocked)
	  {
        synchronized (this)
        {
          if (m_theOwningThread == null )
          {
   	        isAcquired = true;
            m_theOwningThread = theCallingThread;
            m_theLockCount = 0;
          }
          else
          {
            // We have the semaphore but the critical section is owned by another thread.
            // This scenario should never happen as the semaphore will only be released and
            // the owning thread will be null. The lock will go to the first thread that
            // gets the object's monitor.
            theError = 1;
	      } // if
        } // Synchronised
	  } // if
    } // if
    if (theError > 0)
    {
      if (theError == 1)
      {
   	    theMsg = "MAJOR ERROR: Trying to acquire CriticalSection thats is owned by another thread " + new Long(m_theOwningThread.getId()).toString();
   	    theMsg += " and the calling thread id = " + new Long (theCallingThread.getId()).toString();
   	    m_theLogger.error (theMsg);
      } // if
    } // if
    // Return the critical section acquire result.
    return isAcquired;
  } // acquire

} // class CriticalSection