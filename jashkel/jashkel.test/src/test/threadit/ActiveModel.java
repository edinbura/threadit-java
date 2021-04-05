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
package test.threadit;

import au.com.ashkel.javalib.threads.*;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 * Title:        Threading Library
 * Description:  The library provides a framework for an 
 * asynchronous threading model that can be easily used 
 * to build up a system using concurrency as a means of abstraction.
 * Copyright:    Copyright (c) 2006
 * Company:      Ashkel Software
 * @author Ari Edinburg
 * @version 1.0
 */

/**
 * Title:        Threading Library Description:  The library provides a framework for an
 * asynchronous threading model that can be easily used  to build up a system using
 * concurrency as a means of abstraction.
 * Copyright:    Copyright (c) 2006
 * Company:      Ashkel Software
 * @author  Ari Edinburg
 * @version  1.0
 */
public class ActiveModel extends ThreadIt
{
  // -------------------------------------------------------------------------
  // The following constants define the work tasks to be performed.
  // -------------------------------------------------------------------------
  public static final int ATIVE_MODEL_CALC_INFO = 1;
  public static final int ATIVE_MODEL_GET_INFO = 2;
  
  // -------------------------------------------------------------------------
  // The following constants define the status of the work task performed.
  // -------------------------------------------------------------------------
  public static final int ACTIVE_MODEL_SUCCEEDED = 0;
  public static final int ACTIVE_MODEL_FAILED = 1;
  
  // -------------------------------------------------------------------------
  // General Constants
  // -------------------------------------------------------------------------
  /**
   * The periodic rate. 
   */
  public static final int ACTIVE_MODEL_PERIODIC_RATE = 1;

  /**
   * m_Counter is a counter that is only incremented and accessed by the
   * thread associated with this active object.
   */
  private int m_theCounter = 0;
  /**
   * m_SharedCounter is accessed by any thread including the thread associated with
   * this active object. 
   */
  private int m_theSharedCounter = 0;
  /**
   * m_SharedAccess is a monitor or critical section to ensure sequential threaded
   * access to the m_SharedCounter global variable. 
   */
  protected Object  m_theSharedAccess = new Object ();
  
  /**
   * Logger for this class
   */
  private static final Logger m_theLogger = Logger.getLogger (ActiveModel.class);

  /**
   * Method ActiveModel is the constructor for the class. It initialises
   * the instance and provides it with a thread name. 
   * @param theThreadName the name of the thread so that it is easily identifiable.
   */
  public ActiveModel (String theThreadName)
  {
  	super (theThreadName);
  	activeModelInit ();
    log ("Active Model Initialisation");
    log ("Started Thread: " + theThreadName);
  } // ActiveModel
  
  /**
   * Method ActiveModelInit is an internal method to initialise the active
   * object instance. It associates methods with constant message identifiers
   * and initialises the periodic background execution.
   * @return true if the object instance is initialised successfully.
   */
  private boolean activeModelInit ()
  {
    boolean isInitialised = true;

    // Initialise the counters.
    m_theSharedCounter = 0;
    m_theCounter = 0;    
    // Setup all the work methods. 
    try
    {
      setWorkerMethod ("calcInfo", ATIVE_MODEL_CALC_INFO);
      setWorkerMethod ("getInfo", ATIVE_MODEL_GET_INFO);      
      setPeriodicMethod ("backGroundTask");
      // Set the default period. 
      setPeriod (ACTIVE_MODEL_PERIODIC_RATE * 1000);
    } // try
    catch (IOException ex)
    {
      isInitialised = false;
    } // catch
    return isInitialised;
  } // activeModelInit

  /**
   * Method calcInfo demonstrates the execution of work within the thread
   * associated with this active object and the return of the result
   * in the result queue.
   * @param aWorkPackIt that identifies the task to be peformed.
   * @return aWorkPackIt that contains the result. 
   */
  public WorkPackIt calcInfo (WorkPackIt aWorkPackIt)
  {
    int theWorkStatus = 0;

    log ("CalcInfo - workpackit call");
    // Increment the local counter that is thread safe.    
    m_theCounter++;    
    theWorkStatus = ACTIVE_MODEL_SUCCEEDED;
    // Return the result of this request.
    WorkPackIt aPackIt = (WorkPackIt)aWorkPackIt.clone ();
    aPackIt.m_Object = new String ("CALC_INFO=") + Integer.toString (m_theCounter);    
    aPackIt.m_Status = theWorkStatus;
    // Return the work information.
    return aPackIt;
  } // calcInfo

  /**
   * Method getInfo demonstrates the execution of work within the thread
   * associated with this active object and the return of the result
   * in the result queue.
   * @param aWorkPackIt that identifies the task to be peformed.
   * @return aWorkPackIt that contains the result. 
   */
  public WorkPackIt getInfo (WorkPackIt aWorkPackIt)
  {
    int theWorkStatus = 0;  	
    
    log ("GetInfo - workpackit call");
    // Increment the local counter that is thread safe. 
    m_theCounter++;    
    theWorkStatus = ACTIVE_MODEL_SUCCEEDED;
    // Return the result of this request.
    WorkPackIt aPackIt = (WorkPackIt)aWorkPackIt.clone ();
    aPackIt.m_Object = new String ("GET_INFO=") + Integer.toString (m_theCounter);
    aPackIt.m_Status = theWorkStatus;
    // Return the work information.
    return aPackIt;
  } // getInfo

  /**
   * Method getInfo provides direct access to shared data. Access to the 
   * data is thread safe.  
   * @return a copy of the latest data from the shared information.
   */
  public String getInfo ()
  {
    int    theLocalCopy = 0;
    String theValue = null;
    
    log ("GetInfo - direct call");
    // Secure access to the shared counter.
    synchronized (m_theSharedAccess)
    {
      theLocalCopy = m_theSharedCounter;
    } // synchronized
    theValue = new String ("DIRECT_INFO=") + Integer.toString (theLocalCopy);
    // Return the result. 
    return theValue;
  } // getInfo
  
  /**
   * Method backGroundTask is executed periodically as part of the thread
   * of execution of the active object instance. The method increments two 
   * counters and returns a result. In the first instance the result
   * indicates that a copy of the latest count is available and in the second 
   * case indicates that the shared counter has been updated. In the first case the 
   * recipient of the notification needs to retrieve the value off the output queue.
   * In the second case, the recipient of the notification needs to call getInfo 
   * to return the latest count value.
   * @param aWorkPackIt is provided by the framework.
   * @return aWorkPackIt that contains the result of the backGroundTask processing.
   */
  public WorkPackIt backGroundTask (WorkPackIt aWorkPackIt)
  {
    WorkDoneCallback theCallback = null;
    int theWorkStatus = ACTIVE_MODEL_SUCCEEDED;
    
    log ("timedWorkerMethod");
    // Increment the counter to show it is only updated within the thread
    // of this instance. 
    m_theCounter++;    
    // Increment the shared counter to show that it can be accessed from other
    // threads too. 
    synchronized (m_theSharedAccess)
    {
      m_theSharedCounter++;
    } // synchronized
    // Return the result.    
    WorkPackIt aPackIt = (WorkPackIt)aWorkPackIt.clone ();
    // If a callback to use has been specified we can use it. The approach is to enable
    // the callback being invoked by setting the m_NotifyWithCallback to
    // true.
    theCallback =  super.getPeriodicMethodCallback ();
    if (theCallback != null)
    {
      aPackIt.m_Callback = theCallback;
      aPackIt.m_NotifyWithCallback = true;
      aPackIt.m_Object = null; //new String ("BKGRND_INFO=") + Integer.toString (m_theCounter);
      aPackIt.m_SendResult = false;
    } // if
    // Return the result.
    aPackIt.m_Status = theWorkStatus;
    return aPackIt;
  } // backGroundTask

  /**
   * Method log is used to send the provided string to the logger.
   * @param theMessage the text to output to the logger.
   */
  private void log (String theMessage)
  {
    if (m_theLogger.isInfoEnabled ())
    {
      m_theLogger.info (theMessage);
    } // if
  } // log

} // ActiveModel