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

/**
 * --------------------------------------------------------------------------
 * Title : ThreadIt
 * --------------------------------------------------------------------------
 * Description :
 * Class ThreadIt supports the asynchronous model of an active object that
 * can accept request to do work within a thread of control and return the
 * result of the work processing. Work requests are accepted and stored in a
 * buffer until the thread is able to perform the work associated with
 * the request. Completed work requests (the result of the work) are buffered
 * until the requestor is able to retrieve the result.
 * The model requires the following classes:
 * WorkPackIt    - This class describes the work to be performed.
 * WorkPackIt    - This class describes the results of the work.
 * ThreadIt      - Implements the model that defines how clients are
 *                 able to execute work in separate thread.
 * Derived Class - Defines the processing to perform in response to work
 *                 requests.
 * The class derived from ThreadIt creates work packages using WorkPackIt.
 * These work packages are associated with a given method that the class
 * implements. When the ThreadIt class gets the work package it will invoke
 * the method in it's own thread of control. The method implementation
 * takes the input values from the work packages and peforms the necessary
 * processing. When finished the results are compiled and placed in a
 * given output queue. The next work package in the input queue is
 * retrieved. The model also includes asynchronous notification to the
 * requestor when work is completed. By following simple rules it is
 * very easy for an application to utilise active objects using the
 * model defined here.
 *
 * As an example, class ThreadIt can be used to provide a separate thread of
 * execution to perform processing on behalf of clients with a processing
 * completion notification being sent to the client. This would find use
 * when a User Interface must perform a lengthy process that may tie up
 * the screen response. The processing may be delegated to an instance
 * derived from this class for execution in separate thread thus not
 * impacting on the user response. When work is complete the User Interface
 * is notified and retrieves the work done from the work output queue.
 * --------------------------------------------------------------------------
 * Copyright: Copyright (c) 2008
 * Company:   Ashkel Software
 * @author    Ari Edinburg
 * @version   1.0
 * --------------------------------------------------------------------------
 * Associated Documentation :
 * Java Sun JDK 1.6.0_02
 * --------------------------------------------------------------------------
 * Development History
 * --------------------------------------------------------------------------
 * @version    : 0.00
 * Date        : 1st October 2001
 * Author      : A Edinburg, Ashkel Software
 * Description : Start of Development
 * --------------------------------------------------------------------------
 * version     : 1.00
 * Date        : 23rd October 2001
 * Author      : A Edinburg, Ashkel Software
 * Description : First release.
 * --------------------------------------------------------------------------
 */

/** Package */
package au.com.ashkel.javalib.threads;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Semaphore;
import java.lang.reflect.InvocationTargetException;


/**
 * Class ThreadIt supports the execution of work packages on request from
 * clients in a separate thread of control. When processing is complete,
 * the client is advised by an asynchronous notification mechanism and
 * the client can retrieve the result from the output queue buffer.
 */
public class ThreadIt extends java.lang.Thread
{
  // ===========================================================================
  // ThreadIt Constants
  // ---------------------------------------------------------------------------

  /**
   * THREADIT_INFINITE specifies that there is no work processing time
   * limition associated with the current work package. The work processing
   * may take as long as required.
   */
  public final static long THREADIT_INFINITE = Long.MAX_VALUE;

  /**
   * THREADIT_DEFAULT_PERIOD specifies the default time period for background
   * processing in milliseconds. This is the period in which the background
   * method is called.
   */
  public final static long THREADIT_DEFAULT_PERIOD = 2000;

  /**
   * THREADIT_MAX_WORK_METHODS is the maximum number of work methods that an
   * instance of ThreadIt can provide. Work instructions are thus limited
   * to the range 1 to THREADIT_MAX_WORK_METHODS by implementation.
   */
  static final public int THREADIT_MAX_WORK_METHODS = 50;

  /**
   * THREADIT_PERIOD_TIMER is returned as the m_WorkInstruction value
   * in a WorkDoneIt object when the periodic method executes. This implies
   * that the result of processing of a periodic method does not have
   * a work instruction (as such associated with it).
   */
  static final public long THREADIT_THREADIT_PERIOD_TIMER = -1;

  /**
   * THREADIT_USER_STATUS_START indicates values that apply to the status
   * parameter of the WorkPackIt class. Values above this are user defined.
   * Values below this are used by the ThreadIt class.
   */
  static final public int THREADIT_USER_STATUS_START = 150;

  /**
   * THREADIT_STATUS_START indicates values that apply to the status
   * parameter of the WorkPackIt class. Values above this and between
   * THREADIT_USER_STATUS_START are used by the ThreadIt class.
   */
  static final public int THREADIT_STATUS_START = 10;

  // ===========================================================================
  // ThreadIt Status Constants
  // ---------------------------------------------------------------------------
  // The following constants define status outcomes that are ThreadIt
  // specific and indicate a problem with work processing. They are returned
  // in response to a work package (request for work) as the status field
  // of the response package.
  // ---------------------------------------------------------------------------

  /**
   * THREADIT_NO_RESULT indicates that no work has been done yet in
   * response to a work request. This is an initial value.
   */
  static final public int THREADIT_NO_RESULT = THREADIT_STATUS_START + 1;

  /**
   * THREADIT_INVALID_INSTRUCTION indicates that the WorkPackage work
   * instruction was not valid.
   */
  static final public int THREADIT_INVALID_INSTRUCTION = THREADIT_STATUS_START + 2;

  /**
   * THREADIT_NO_METHOD indicates that there is no mehtod allocated to the
   * given work instruction and thus the work cannot be performed.
   */
  static public int THREADIT_NO_METHOD = THREADIT_STATUS_START + 3;

  /**
   * THREADIT_TIME_OUT indicates that work in process has been aborted
   * as there was a time-out waiting for the work to complete. The work is
   * not complete.
   */
  static final public int THREADIT_TIME_OUT = THREADIT_STATUS_START + 4;

  /**
   * THREADIT_INVALID_DONE_QUEUE indicates that a result needs to be returned
   * using a local queue specified in the WorkPackIt instance but the
   * queue is not specified correctly.
   */
  static final public int THREADIT_INVALID_DONE_QUEUE = THREADIT_STATUS_START + 5;

  /**
   * THREADIT_INVALID_EVENT implies an event has occurred for which the event
   * value is out of range for the currently defined set of events.
   * This constant is not currently used by the Java implementation.
   */
  static final public int THREADIT_INVALID_EVENT = THREADIT_STATUS_START + 6;

  /**
   * THREADIT_NO_EVENT_METHOD indicates that an event has occurred for which
   * there is no event handler method.
   * This constant is not currently used by the Java implementation.
   */
  static final public int THREADIT_NO_EVENT_METHOD = THREADIT_STATUS_START + 7;

  /** COPY_PARAMS is used as input to the checkParams methods used to assist applications
   * in checking the input parameters to a ThreadIt worker method. This indicates that the
   * input CWorkPackIt parameters should be copied to the output CWorkPackIt parameters.
   * This is the default behaviour.
   */
  static public final boolean COPY_PARAMS = true;


  /**
   * Logger for this class
   */
  private static final Logger m_theLogger = Logger.getLogger (ThreadIt.class);

  private static final String PARENT_CATEGORY = "threadit.";
  /** MODULE_NAME Name allocated to this module. This is used for logging and component
   * identification purpose */
  private static final String MODULE_NAME = "ThreadIt";

  private static final int THREADIT_THREADIT_STATUS_OK = THREADIT_STATUS_START + 8;

  private static final int THREADIT_STATUS_PARAM_OBJECT_NULL = THREADIT_STATUS_START + 9;

  private static final int THREADIT_STATUS_OK = THREADIT_STATUS_START + 10;

  private static final int THREADIT_STATUS_PARAM_WORK_PACK_NULL = THREADIT_STATUS_START + 11;

  private static final int THREADIT_WORKDONE_NO_EVENT_METHOD = THREADIT_STATUS_START + 12;

  private static final int THREADIT_WORKDONE_NO_RESULT = THREADIT_STATUS_START + 13;

  private static final int THREADIT_WORKDONE_INVALID_INSTRUCTION = THREADIT_STATUS_START + 14;

  private static final int THREADIT_WORKDONE_NO_METHOD = THREADIT_STATUS_START + 15;

  private static final int THREADIT_WORKDONE_TIME_OUT = THREADIT_STATUS_START + 16;

  private static final int THREADIT_WORKDONE_INVALID_DONE_QUEUE = THREADIT_STATUS_START + 17;

  private static final int THREADIT_WORKDONE_INVALID_EVENT = THREADIT_STATUS_START + 18;

  private static final int THREADIT_PAYLOAD_NOT_EXPECTED = THREADIT_STATUS_START + 19;

  private static final int THREADIT_STATUS_LAST = THREADIT_STATUS_START + 20;



  // ===========================================================================
  // ThreadIt Type Definitions
  // ---------------------------------------------------------------------------
  // Type Definitions for use in the ThreadIt class.
  // ---------------------------------------------------------------------------

  /**
   * Class MethodType is the base class for supporting the various method
   * types that do work on behalf of incoming work packages. The class
   * instance manages a method associated with a work instruction and is
   * able to invoke the method that does work in response to the work
   * instruction. The method invoked to do work accepts a WorkPackIt parameter
   * that specifies the work to be performed and returns a new WorkPackIt
   * result that contains the work performed.
   */
  private class MethodType
  {
    /**
     * m_Method is a reference to the method that is to be invoked to do
     * the work for an incoming work request.
     */
    protected Method m_Method = null;

    /**
     * Method MethodType is the constructor for the class and
     * initialises the instance variables.
     */
    public MethodType ()
    {
      m_Method = null;
    } // method MethodType

    /**
     * Method setMethod is called to set the method that is associated with
     * this method type instance. This is the method to be invoked when
     * the work request is to be processed.<p>
     * aMethod : specifies the method that will be invoked for this instance.
     *           The method must accept a WorkPackIt parameter and return a
     *           new WorkPackIt result that represents the work performed.
     */
    public void setMethod (Method aMethod)
    {
      m_Method = aMethod;
    } // method setMethod

    /**
     * Method getMethod is called to get the method that is associated with
     * this method type instance. This is the method that is invoked when
     * the work request is to be processed. The method returns null if
     * no method has been associated with this instance.
     */
    public Method getMethod ()
    {
      return m_Method;
    } // method getMethod

    /**
     * Method invoke invokes the method associated with this instance.<p>
     * theObject      : is the context in which the method is invoked.
     *                  It can be null for static methods. See Object.invoke.<p>
     * theParameters : are the parameters to be passed to the method being
     *                 invoked.<p>
     * The method returns a WorkPackIt if the method being invoked executes
     * successfully and returns the WorkPackIt that describes the work
     * performed as is required. The method returns null if associated work
     * method is not invoked successfully or the WorkPackIt containing the
     * work done is returned as null.
     */
    public WorkPackIt invoke (Object theObject, Object[] theParameters)
    {
      Object theReturnObject = null;

      // Check if the method to invoke has been specified.
      if (m_Method != null)
      {
        try
        {
          // Invoke the method and pass it the parameters.
          theReturnObject = m_Method.invoke (theObject, theParameters);
        } // if
        catch (InvocationTargetException ex)
        {
          m_theLogger.error ("invoke", ex);
          theReturnObject = null;
        } // catch
        catch (IllegalAccessException ex)
        {
          theReturnObject = null;
          m_theLogger.error ("IllegalAccessException", ex);
        } // catch
        //  Do a catch all.
        catch (Exception ex)
        {
          theReturnObject = null;
          m_theLogger.warn ("Exception", ex);
        } // catch
      } // if
      // Return the method status.
      return (WorkPackIt) theReturnObject;
    } // method invoke

  } // class MethodType

  /**
   * Class WorkerMethodType represents a general work method type
   * that is invoked in response to a work request. It provides specialised
   * handling of work method invocation.<p>
   * Note: Currently no specialisation is provided.
   */
  private class WorkerMethodType extends MethodType
  {

    /**
     * Method WorkerMethodType is the constructor for the class and
     * performs member variable initialisation.
     */
    public WorkerMethodType ()
    {
    } // method WorkerMethodType

  } // class WorkerMethodType

  /**
   * Class PeriodicMethodType represents a work method type that is invoked
   * in response to a periodic timer expiry event. It provides specialised
   * handling of method invocation that performs work in response to the
   * timer expiry.<p>
   * Note: Currently no specialisation is provided.
   */
  private class PeriodicMethodType extends MethodType
  {
    /**
     * Method PeriodicMethodType is the constructor for the class and
     * performs member variable initialisation.
     */
    public PeriodicMethodType ()
    {
    } // method PeriodicMethodType

  } // class PeriodicMethodType




  // ===========================================================================
  // ThreadIt Member Variables
  // ---------------------------------------------------------------------------

  /**
   * m_WorkPackID is a running number used to uniquely identify work
   * packages in the system. It should just reset itself when incremented
   * beyond its limit. This allows the system to handle work packages of
   * the same work instruction.
   */
  protected long m_WorkPackID = 0;

  /**
   * m_ExitThread is set to true when the thread of execution can exit.
   */
  protected volatile boolean m_ExitThread = false;

  /**
   * m_WorkQ represents the queue that receives work packages to execute
   * that is then processed by the work thread. This is the protocted
   * queue (by semaphore) that will deliver a work request to the thread
   * in terms of a WorkPackIt instance.
   */
  protected ProtectedQueue m_WorkQ = null;

  /**
   * m_DoneQ receives work done packages for return to the initiators of work.
   * This is the protocted queue (by semaphore) that will return
   * the output of work in terms of a WorkPackIt instance. Results don't have
   * to be returned in the queue but this is the default queue for this
   * purpose.
   */
  protected ProtectedQueue m_DoneQ = null;

  /**
   * m_WorkerMethod is the array of methods of the WorkerMethodType signature that are called to process work packages. These methods are declared in a derived class.
   * @uml.property  name="m_WorkerMethod"
   * @uml.associationEnd  multiplicity="(0 -1)"
   */
  protected WorkerMethodType[] m_WorkerMethod = null;

  /**
   * m_PeriodMethod is the method type that is called to peform processing
   * when a time period expires.
   */
  protected PeriodicMethodType m_PeriodicMethod = null;

  // ===========================================================================
  // ThreadIt Exectution Timing Member Variables
  // ---------------------------------------------------------------------------

  /**
   * m_TStart measures the start of a timing operation.
   */
  protected long m_TStart = 0;

  /**
   * m_TStop measures the end of a timing operation.
   */
  protected long m_TStop = 0;

  /**
   * m_IsTiming indicates if execution timing is in progress.
   */
  protected boolean m_IsTiming = false;

  /**
   * m_TimeAllowed is the time allowed for work execution for the
   * current operation.
   */
  protected long m_TimeAllowed = 0;

  /**
   * m_TimePeriod is the time period at which periodic work is expected
   * to occur.
   */
  protected long m_TimePeriod = 0;

  /**
   * m_TimeOut is used while delaying for an event.
   */
  protected long m_TimeOut = 0;

  /**
   *  m_Period is the timer for determining when the periodic method should
   * execute.
   */
  protected TimeIt m_Period = null;

  /**
   * m_thePeriodicMethodCallback is set by a client in the event that the
   * client wants to receive a callback every time the periodic method
   * completes execution.
   */
  private WorkDoneCallback m_thePeriodicMethodCallback = null;

  private Semaphore m_theExitSignal = new Semaphore (0);
  
  protected String m_theThreadName = null;

  /**
   * Method ThreadIt is the constructor for the class. The method sets the
   * initial values for the member variables, sets the thread to be executed
   * at default priority and starts thread execution.
   */
  public ThreadIt ()
  {
    // Perform the standard initialisation.
    threadItInit (PARENT_CATEGORY + MODULE_NAME);
    // Start the thread execution.
    start ();
  } // method ThreadIt

  /**
   * Method ThreadIt is the constructor for the class. The method sets the
   * initial values for the member variables, sets the priority of the thread
   * and then starts thread execution.<p>
   * Priority : Specifies the priority of the thread of execution associated
   *            with this instance of the ThreadIt class.
   */
  public ThreadIt (int thePriority)
  {
    // Perform the standard initialisation.
    threadItInit (PARENT_CATEGORY + MODULE_NAME);
    // Set the threads execution priority.
    setPriority (thePriority);
    // Start the thread execution.
    start ();
  } // method ThreadIt

  /**
   * Method ThreadIt is the constructor for the class. The method sets the
   * initial values for the member variables, sets the priority of the thread
   * and then starts thread execution.<p>
   * Priority : Specifies the priority of the thread of execution associated
   *            with this instance of the ThreadIt class.
   */
  public ThreadIt (String theThreadName)
  {
    String thePath = theThreadName + "." + MODULE_NAME;
    // Perform the standard initialisation.
    threadItInit (thePath);
    // Start the thread execution.
    start ();
  } // method ThreadIt

  /**
   * Method ThreadIt is the constructor for the class. The method sets the
   * initial values for the member variables, sets the priority of the thread
   * and then starts thread execution.<p>
   * Priority : Specifies the priority of the thread of execution associated
   *            with this instance of the ThreadIt class.
   */
  public ThreadIt (int thePriority, String theThreadName)
  {
    // Perform the standard initialisation.
    String thePath = theThreadName + "." + MODULE_NAME;
    threadItInit (thePath);
    // Set the threads execution priority.
    setPriority (thePriority);
    // Start the thread execution.
    start ();
  } // method ThreadIt

  /**
   * Method ThreadItInit performs the shared initialisation code for the
   * class.
   * @param theLogName
   */
  private void threadItInit (String theThreadName)
  {
    int Cntr;

	  m_theThreadName = theThreadName;
    // Set the thread name.
    setName (theThreadName);
    // Initialise the worker method list.
    m_WorkerMethod = new WorkerMethodType[THREADIT_MAX_WORK_METHODS + 1];
    // Initialise the worker methods.
    for (Cntr = 0; Cntr < THREADIT_MAX_WORK_METHODS; Cntr++)
    {
      m_WorkerMethod[Cntr] = null;
    } // if
    // Set the work packet identity counter.
    m_WorkPackID = 0;
    // The thread is executing.
    m_ExitThread = false;
    // Initialise the timing variables.
    m_TStart = 0;
    m_TStop = 0;
    m_IsTiming = false;
    // Set the default timing period.
    m_TimePeriod = THREADIT_DEFAULT_PERIOD;
    m_TimeOut = m_TimePeriod;
    // Set the periodic method.
    m_PeriodicMethod = null;
    // Create the work queues.
    m_WorkQ = new ProtectedQueue ();
    m_DoneQ = new ProtectedQueue ();
    // Create an instance that manages timing.
    m_Period = new TimeIt ();
  } // method threadItInit

  // ===========================================================================
  // CLIENT INTERFACE METHODS
  // ---------------------------------------------------------------------------

  /**
   * Method startWork provides a work package that defines the work to be
   * performed by the thread. The work pack is placed in queue to be processed
   * when a work slot is available. When the work slot is available the
   * method associated with the work package instruction is invoked to do the
   * work.<p>
   * WorkPack   : The work package to be performed. This describes the work
   *              by work instruction and contains references to all the
   *              information needed to perform the work.<p>
   * Method startWork returns a non-negative value this is the reference to
   * the work package. The caller can use this value to track the result of
   * this work request.
   */
  public synchronized long startWork (WorkPackIt WorkPack)
  {
    boolean Success = true;
    long Result = 0;
    long WorkPackID = -1;

    // Check if the value can be incremented.
    if (m_WorkPackID >= Long.MAX_VALUE)
    {
      // Reset the counter.
      m_WorkPackID = 0;
    }
    else
    {
      // Now increment the value.
      m_WorkPackID++;
    } // if
    // Return this to the caller.
    WorkPackID = m_WorkPackID;
    // Setup the work package identity.
    WorkPack.m_WorkPackID = WorkPackID;
    // Now send the work package on for execution.
    m_WorkQ.insertItem (WorkPack);
    // Return the method status.
    return WorkPackID;
  } // method startWork

  /**
   * Method getWork waits for work processing to be completed and returns
   * a WorkPackIt package that describes the work performed. This method
   * looks for the result in the default result queue.<p>
   * TimeOut : Indicates how long the caller is willing to wait
   *           for the work result.<p>
   * Method getWork returns a non-null WorkPackIt instance that then
   * contains the output of the work processing.
   */
  public WorkPackIt getWork (long TimeOut)
  {
    // Get and return any work items in the work done queue.
    return (WorkPackIt) m_DoneQ.waitItem (TimeOut);
  } // method getWork

  /**
   * Method getWorkDoneQ returns a reference to the work done queue. This is
   * provided to allow custom handling of the queue. The caller must remember
   * to release the queue when processing on the instance is completed.
   */
  public ProtectedQueue getWorkDoneQ ()
  {
    return m_DoneQ;
  } // method getWorkDoneQ

  /**
   * Method getWorkQ returns a reference to the work queue. This is the queue
   * where jobs are queued waiting to be performed by the ThreadIt instance.
   * The queue is accessible in order to support custom handling of the queue.
   * The caller must remember to release the queue when processing on the
   * instance is completed.
   */
  public ProtectedQueue getWorkQ ()
  {
    return m_WorkQ;
  } // method getWorkQ

  /**
   * Method notifyEvent
   */
  public void notifyEvent (int theEventHandler)
  {
    EventWorkPackIt theEvent = new EventWorkPackIt (theEventHandler);
    // Now send the work package on for execution.
    m_WorkQ.insertPriorityItem (theEvent);
  } // method notifyEvent

  public void notifyEvent (int theEventHandler, Object theData)
  {
    EventWorkPackIt theEvent = new EventWorkPackIt (theEventHandler);
    theEvent.m_Object = theData;
    // Now send the work package on for execution.
    m_WorkQ.insertPriorityItem (theEvent);
  } // method notifyEvent

  public void notifyEvent (int theEventHandler, WorkPackIt theWork)
  {
    EventWorkPackIt theEvent = new EventWorkPackIt (theEventHandler, theWork);
    // Now send the work package on for execution.
    m_WorkQ.insertPriorityItem (theEvent);
  } // method notifyEvent

  public synchronized void setPeriodicMethodCallback (WorkDoneCallback theCallback)
  {
    m_thePeriodicMethodCallback = theCallback;
  } // setPeriodicMethodCallback

  public synchronized WorkDoneCallback getPeriodicMethodCallback ()
  {
    WorkDoneCallback theCallback = null;

    theCallback = m_thePeriodicMethodCallback;
    // Release the mutex.
    return theCallback;
  } // setPeriodicMethodCallback

  /**
   * Method run represents the thread of execution for the instance.
   * Once started, the thread waits for work packages in the work queue. When
   * a package is found the associated method is invoked to perform
   * the work within the thread. On completion, the client is notified of
   * work completion using the WorkDoneQ and the process repeats.
   */
  public void run ()
  {
    int WorkInstruction = 0;
    int Result = 0;
    long EventId = 0;
    long Elapsed = 0;
    boolean isWorkItem = false;
    boolean theSuccess = false;
    boolean IsWorkToDo = false;
    boolean isExitThread = false;
    // Establish the method parameters.
    Object args[] = new Object[1];
    Method aMethod = null;
    WorkPackIt WorkPack = null;
    WorkPackIt theWorkDone = null;
    WorkPackIt TimedWork = null;

    // Start a timing operation.
    m_Period.StartTiming (m_TimePeriod);
    // Set the timeout to match.
    m_TimeOut = m_TimePeriod;
    // Perform the data processing.
    do
    {
      // Establish the method parameters.
      args[0] = null;
      aMethod = null;
      // Initialise the work packages.
      WorkPack = null;
      TimedWork = new WorkPackIt ();
      theWorkDone = null;

      // Wait for for a work instruction to arrive, an event to occur or for a time out.
      // Process the outcome of the wait.
      if (!m_ExitThread)
      {
        // Wait for for a work instruction to arrive or for a time out.
        // Get the instruction associated with the incoming work request.
        WorkPack = (WorkPackIt) m_WorkQ.waitItem (m_TimeOut);
        // If no work package is retrieved then we do nothing.
        if ((!m_ExitThread) && (WorkPack != null))
        {
          // Set the work instruction status.
          theSuccess = false;
          if (WorkPack.isEvent ())
          {
            WorkInstruction = ((EventWorkPackIt)WorkPack).getEventId ();
          }
          else
          {
            // Perform the work according to the work instruction given.
            WorkInstruction = WorkPack.getWorkInstruction ();
          } // if
          // Check that a valid work instruction has been given.
          if ((WorkInstruction >= 0) && (WorkInstruction < THREADIT_MAX_WORK_METHODS))
          {
            // Make sure that a method has been provided to perform the work
            // instruction.
            if (m_WorkerMethod[WorkInstruction] != null)
            {
              // Measure the execution time of this work.
              startTiming (WorkPack.m_TimeAllowed);
              // Establish the work package.
              args[0] = WorkPack;
              // Invoke the method with argument.
              theWorkDone = m_WorkerMethod[WorkInstruction].invoke (this, args);
              // Ensure that a work package result is returned.
              if (theWorkDone == null)
              {
                // Initialise the work done information.
                theWorkDone = (WorkPackIt) WorkPack.clone ();
                theWorkDone.m_Status = THREADIT_NO_RESULT;
              } // if
              // Get the time to completion.
              theWorkDone.m_TimeElapsed = stopTiming ();
            }
            else
            {
              // Initialise the work done information.
              theWorkDone = (WorkPackIt) WorkPack.clone ();
              // No method specified for this work instruction.
              theWorkDone.m_Status = THREADIT_NO_METHOD;
              m_theLogger.error ("No method specified for work instruction");
            } // if (m_WorkerMethod[WorkInstruction] != null)
          }
          else
          {
            // Initialise the work done information.
            theWorkDone = (WorkPackIt) WorkPack.clone ();
            // Invalid work instruction given.
            theWorkDone.m_Status = THREADIT_INVALID_INSTRUCTION;
            m_theLogger.error ("Invalid work instruction specified");
          } // if ((WorkInstruction >= 0) && (WorkInstruction < MAX_WORK_METHODS))
          // Now that the work is done. Send a response back the issuer.
          sendResponse (theWorkDone, WorkInstruction, false);
        }
      } // if (!m_ExitThread)
      // Check if periodic processing is required.
      if ((!m_ExitThread) && (m_PeriodicMethod != null)) 
        if (m_Period.IsExpired ())
        {
          // There is a time out waiting for an incoming message or
          // the threads period timer has expired. Execute the periodic method.
          // Setup the work request.
          TimedWork.initialise ();
          TimedWork.setWorkInstruction (0);
          // Measure the execution time of this work.
          startTiming (m_TimePeriod);
          // Establish the work argument.
          args[0] = TimedWork;
          // Execute the work according to the work instruction.
          theWorkDone = m_PeriodicMethod.invoke (this, args);
          // Ensure that a work package result is returned.
          if (theWorkDone == null)
          {
            theWorkDone = (WorkPackIt) TimedWork.clone ();
          } // if
          // Get the time to completion.
          theWorkDone.m_TimeElapsed = stopTiming ();
          // The period method requires a result to be returned.
          WorkInstruction = TimedWork.getWorkInstruction ();
          // Now that the work is done. Send a response back the issuer.
          sendResponse (theWorkDone, WorkInstruction, true);
          // Start timing again.
          m_Period.StartTiming (m_TimePeriod);
          m_TimeOut = m_TimePeriod;
        }
        else
        {
          // The timer has not expired. Count down the remaining time
          // on this timer.
          m_TimeOut = m_Period.timeRemaining ();
        } // if (m_Period.IsExpired ())
      // The following variable is global but we access it here
      // unprotected for performance reasons.
      isExitThread = m_ExitThread;
      // Continue until we need to exit the thread.
    } while (!isExitThread);
    // Signal anyone waiting for the release of this thread.
    m_theExitSignal.release ();
  } // method run

  /**
   * Method sendResponse checks if a response to work is required and then
   * interprets the work done settings to send off the response. This method
   * is provided in the event that work processing requires the generation
   * of multiple responses per work item. This allows the work handler method
   * to place reponses in queues and to generate events associated with them.<p>
   * WorkDone : The work done instance whose settings are used to
   *            determine the type of response required.<p>
   * WorkId   : The identifier allocated to the work performed.<p>
   * Method SendResponse returns true if the response to a work package
   * has been generated successfully.
   * @param isPeriodic
   */
  private boolean sendResponse (WorkPackIt WorkDone, long WorkId, boolean isPeriodic)
  {
    boolean Success = true;
    String theThreadName = null;

    if (WorkDone == null) { return false; };
    // Send a result to the user if requested.
    if (WorkDone.m_SendResult)
    {
      // Return a reference to this instance of CThreadIt
      WorkDone.m_ptheSource = this;
      // Insert it into the queue for the issuer to pick up.
      // Check if the default work done queue is to be used.
      if (WorkDone.m_UseDefaultQ)
      {
        m_DoneQ.insertItem (WorkDone);
      }
      else
      {
        // Use a the provided queue and check that the provided queue exists.
        if (WorkDone.m_WorkDoneQ != null)
        {
          WorkDone.m_WorkDoneQ.insertItem (WorkDone);
        }
        else
        {
          WorkDone.m_Status = THREADIT_INVALID_DONE_QUEUE;
          Success = false;
        } // if
      } // if
    } // if (WorkDone.SendResult)
    // Check if the issuer is notified with a callback method.
    if (WorkDone.m_NotifyWithCallback)
    {
      // The issuer wants a notification message. Check that a callback
      // is available.
      if (WorkDone.m_Callback != null)
      {
        // Invoke the method used to callback into the issuer.
        theThreadName = getName ();
        try
        {
          WorkDone.m_Callback.onWorkDone (theThreadName, WorkDone.m_WorkPackID);
        } // try
        catch (Exception anException)
        {
          m_theLogger.warn ("onWorkDone", anException);
          Success = false;
        } // catch
      } // if
    } //   if (WorkDone.m_NotifyWithCallback)
    // Return the method status.
    return Success;
  } // method sendResponse

  /**
   * Method setWorkerMethod associates a method of a derived class with
   * work instructions. This implies that when a work instruction is received
   * in the work queue, the method associated with the work instruction will be
   * invoked to perform the work. This method should be called from the derived
   * class before any work instruction is sent to the instance.<p>
   * theMethodName : The name of the method in the derived class that will be
   *                 required to perform work according to a work instruction. <p>
   * Instruction   : The work instruction that the class member is to
   *                 be associated with.
   *                 Values range from 1 to THREADIT_MAX_WORK_METHODS.<p>
   * Method setWorkerMethod returns true if the method is successfully
   * associated with the work instruction.
   */
  public boolean setWorkerMethod (String theMethodName, int Instruction) throws IOException
  {
    boolean Success = false;
    WorkerMethodType aMethodType = null;
    Method aMethod = null;

    // Store the list of arguments to the method we associate with the
    // instruction. It can take the WorkPackIt class as a method parameter.
    ArrayList theTypes = new ArrayList ();
    theTypes.add (WorkPackIt.class);
    // Check that the instruction value is valid.
    if (Instruction < THREADIT_MAX_WORK_METHODS)
    {
      // Get the method reference for the method name given.
      try
      {
        Class[] theArgTypes = (Class[]) theTypes.toArray (new Class[1]);
        aMethod = this.getClass ().getMethod (theMethodName, theArgTypes);
        Success = true;
        aMethodType = new WorkerMethodType ();
        aMethodType.setMethod (aMethod);
        m_WorkerMethod[Instruction] = aMethodType;
      }
      catch (Exception e)
      {
        Success = false;
        throw new IOException ("No such method found, or wrong argument types:" + theMethodName);
      } // catch
    } // if
    // Return the method status.
    return Success;
  } // setWorkerMethod

  /**
   * Method setPeriodicMethod associates the method of a derived class
   * with the periodic method. This implies that when a time period
   * elapses, the given periodic method is executed. The periodic methods
   * is able to return a WorkPackIt instance in the work done queue.<p>
   * theMethodName : The name of the method in the derived class that will be
   *                 required to perform periodic work processing.<p>
   * Method setPeriodicMethod returns true if the method is setup
   * successfully.
   */
  public synchronized boolean setPeriodicMethod (String theMethodName) throws IOException
  {
    boolean Success = false;
    PeriodicMethodType aMethodType = null;
    Method aMethod = null;
    // Store the list of arguments to the method we associate with the
    // instruction. It can take the WorkPackIt class as a method parameter.
    ArrayList theTypes = new ArrayList ();
    theTypes.add (WorkPackIt.class);
    try
    {
      Class[] theArgTypes = (Class[]) theTypes.toArray (new Class[1]);
      // Get the method reference for the method name given.
      aMethod = this.getClass ().getMethod (theMethodName, theArgTypes);
      aMethodType = new PeriodicMethodType ();
      aMethodType.setMethod (aMethod);
      m_PeriodicMethod = aMethodType;
      Success = true;
    }
    catch (Exception e)
    {
      Success = false;
      throw new IOException ("No such method found, or wrong argument types:" + theMethodName);
    } // catch
    // Return the method status.
    return Success;
  } // setPeriodicMethod

  /**
   * Method setPeriod sets the rate at which the periodic method
   * is invoked.<p>
   * Period : The timer period in milliseconds that sets the time
   *          period between Periodic Method invocations.
   *          A value of zero turns off period timing.<p>
   *  This method should only be called from Worker Methods or from the
   *  Periodic method itself.
   */
  public void setPeriod (long Period)
  {
    // Check if the time period has changed or not.
    if (m_TimePeriod != Period)
    {
      if (Period <= 0)
      {
        m_TimePeriod = THREADIT_INFINITE;
      }
      else
      {
        m_TimePeriod = Period;
      } // if
      // Start timing again.
      m_TimeOut = m_TimePeriod;
      m_Period.StartTiming (m_TimePeriod);
    } // if
  } // setPeriod

  /**
   * Method isAvailableTime is provided for use in the WorkerMethodType function
   * to determine if the time allowed for processing has elapsed or not. If
   * the time available has elpased then the worker method should stop
   * processing and return the status THREADIT_TIME_OUT in the WorkPackIt
   * result. Method isAvailableTime returns true if there is still work
   * execution time available.
   */
  protected synchronized boolean isAvailableTime ()
  {
    long Elapsed;
    boolean StillTime;

    // Get the time elapsed so far.
    Elapsed = timeElapsed ();
    // Check if there is still time available.
    if (m_TimeAllowed == 0)
      StillTime = true;
    else
      StillTime = (m_TimeAllowed > Elapsed);
    // Return the method result.
    return StillTime;
  } // isAvailableTime

  /**
   * Method startTiming is called to record the start of work execution timing.<p>
   * TimeAllowed : Specifies the time allocated for work to be
   *               executed. A value of zero indicates that there is
   *               no timing restriction.
   */
  protected synchronized void startTiming (long TimeAllowed)
  {
    // Record the time allowed for work execution.
    m_TimeAllowed = TimeAllowed;
    // Start the timer.
    m_TStart = getTickCount ();
    // Timing is in progress.
    m_IsTiming = true;
  } // startTiming

  /*
   * Method stopTiming is called to record the end of work execution timing.
   * The method returns the time lapsed so far for in work execution
   * in milliseconds.
   */
  protected synchronized long stopTiming ()
  {
    long Elapsed = 0;

    // Get the time Elapsed so far.
    Elapsed = timeElapsed ();
    // Stop timing.
    m_IsTiming = false;
    return Elapsed;
  } // stopTiming

  /**
   * Method timeElapsed calculates how much time has so far elapsed since
   * timing was started. The method returns the time elapsed since
   * timing was started. Note: timeElapsed is not secured for re-entrancy.
   */
  protected long timeElapsed ()
  {
    boolean IsTiming;
    long Elapsed = 0;

    // Start the timer.
    m_TStop = getTickCount ();
    // Calculate the time elapsed so far.
    // Get the time differences from the start to the end.
    if (m_TStop >= m_TStart)
      // The timer has not wrapped.
      Elapsed = m_TStop - m_TStart;
    else
      // The timer has wrapped around.
      Elapsed = (Long.MAX_VALUE - m_TStart) + m_TStop;
    // Indicate if timing is in progress or not.
    IsTiming = m_IsTiming;
    // Return result.
    return Elapsed;
  } // timeElapsed

  /**
   * Method startThread resumes the execution of the thread of control for the
   * instance.
   */
  public void startThread ()
  {
    start ();
  } // startThread

  /**
   * Method waitForThreadToStop can be called to wait for the thread to stop
   * execution.
   */
  public boolean waitForThreadToStop (long theTimeOut)
  {
    boolean isThreadExit = false;

    try
    {
      isThreadExit = m_theExitSignal.tryAcquire (theTimeOut, TimeUnit.MILLISECONDS);
    } // try
    catch (InterruptedException anInterruptedException)
    {
      m_theLogger.debug ("InterruptedException", anInterruptedException);
    } // catch
    return isThreadExit;
  } // waitForThreadToStop

  /**
   * Method stopThread stops the execution of the thread of control for the
   * instance.
   */
  public synchronized void stopThread ()
  {
    WorkPackIt theExitWorkPack = new WorkPackIt ();

    // Indicate that the thread must now terminate execution.
    m_ExitThread = true;
    // In future send an out of band message to the worker thread and ask it to exit.
    theExitWorkPack.m_Instruction = -1;
    theExitWorkPack.m_SendResult = false;
    startWork (theExitWorkPack);
  } // stopThread

  /**
   * Method exittThread is called internally to check if the thread of
   * execution is required to stop. The method indicates if the thread
   * control variable that indicates thread execution must stop has been
   * set to true or not.
   */
  public synchronized boolean exitThread ()
  {
    boolean ExitIt = false;

    // Indicate that the thread must now terminate execution.
    ExitIt = m_ExitThread;
    // Return the thread value.
    return ExitIt;
  } // exitThread

  /**
   * Method getTickCount is a helper that returns the number of milliseconds
   * using the System.currentTimeMillis call.
   */
  protected long getTickCount ()
  {
    return System.currentTimeMillis ();
  } // getTickCount

  /**
   * Method checkParams is called to assist when checking input parameters to the worker method. The aim is to simplify the task
   * of the programmer in performing repeated error checks for every method. This particular method returns the object
   * pointer in the ptheWorkPack.
   * @param[in] ptheWorkPack is check that it is not NULL and that it contains an object pointer.
   * @param[out] ptheWorkDone returns a new CWorkPackIt to return the result. If isCopy is true then this is a copy of the
   * ptheWorkPack parameter.
   * @param[out] ptheObj returns the object pointer to the object in ptheWorkPack.
   * @param[out] theStatus returns the status of the parameter check. It has a value of
   * THREADIT_STATUS_OK - parameters check out ok.
   * THREADIT_STATUS_PARAM_OBJECT_NULL - the expected object parameter is NULL.
   * THREADIT_STATUS_PARAM_WORK_PACK_NULL - the input work packet is NULL.
   * @param isCopy[in] indicates if the input ptheWorkPack should be copied to the output ptheWorkDone.
   * \return true true if the check on the parameters is successful.
   * If the check fails then the ptheWorkDone is always returned as a new CWorkPackIt. If ptheWorkPack is not NULL and
   * isCopy is specified then ptheWorkDone will be a copy of ptheWorkPack. The ptheObj is returned as NULL on failure.
   * If the check fails then the string representation of the error status will be written as an error to the log file.
   */
  public PayLoad checkParams (WorkPackIt ptheWorkPack, boolean isCopy)
  {
    boolean isSuccess = false;
    PayLoad theLoad = new PayLoad ();
    WorkPackIt ptheWorkDone = null;
    Object ptheObj = null;
    Integer theStatus = null;

    if (ptheWorkPack != null)
    {
      ptheObj = ptheWorkPack.m_Object;
      if (ptheObj != null)
      {
        theStatus = THREADIT_STATUS_OK;
        isSuccess = true;
      }
      else
      {
        theStatus = THREADIT_STATUS_PARAM_OBJECT_NULL;
      } // if
    }
    else
    {
      theStatus = THREADIT_STATUS_PARAM_WORK_PACK_NULL;
    } // if
    if ((ptheWorkPack != null) && (isCopy))
    {
      ptheWorkDone = (WorkPackIt)ptheWorkPack.clone ();
    }
    else
    {
      ptheWorkDone = new WorkPackIt ();
    } // if
    // Return the operating status at this point.
    ptheWorkDone.m_Status = theStatus;
    if (!isSuccess)
    {
      logParameterError (theStatus);
    } // if
    theLoad.m_theData = ptheObj;
    theLoad.m_theWorkDone = ptheWorkDone;
    theLoad.m_theStatus = theStatus;
    theLoad.m_isGoodPayLoad = isSuccess;
    return theLoad;
  } // checkParams

  /**
   * Method checkParams is called to assist when checking input parameters to the worker method. The aim is to simplify the task
   * of the programmer in performing repeated error checks for every method. This particular method does not expect and
   * object pointer in ptheWorkPack.
   * @param[in] ptheWorkPack is checked that it is not NULL and that it does not contain an object pointer.
   * @param[out] ptheWorkDone returns a new WorkPackIt to return the result. If isCopy is true then this is a copy of the
   * ptheWorkPack parameter.
   * @param[out] theStatus returns the status of the parameter check. It has a value of
   * THREADIT_STATUS_OK - parameters check out ok.
   * THREADIT_STATUS_PARAM_WORK_PACK_NULL - the input work packet is NULL.
   * @param isCopy[in] indicates if the input ptheWorkPack should be copied to the output ptheWorkDone.
   * \return true true if the check on the parameters is successful.
   * If the check fails then the ptheWorkDone is always returned as a new CWorkPackIt. If ptheWorkPack is not NULL and
   * isCopy is specified then ptheWorkDone will be a copy of ptheWorkPack.
   * If the check fails then the string representation of the error status will be written as an error to the log file.
   */
  public PayLoad checkParamsNoData (WorkPackIt ptheWorkPack, boolean isCopy)
  {
    WorkPackIt ptheWorkDone = null;
    Integer theStatus = null;
    PayLoad theLoad = new PayLoad ();
    boolean isSuccess = false;

    ptheWorkDone = null;

    if (ptheWorkPack != null)
    {
      if (ptheWorkPack.m_Object == null)
      {
        theStatus = THREADIT_STATUS_OK;
        isSuccess = true;
      }
      else
      {
        theStatus = THREADIT_PAYLOAD_NOT_EXPECTED;
      } // if
    }
    else
    {
      theStatus = THREADIT_STATUS_PARAM_WORK_PACK_NULL;
    } // if
    if ((ptheWorkPack != null) && (isCopy))
    {
      ptheWorkDone = (WorkPackIt)ptheWorkPack.clone ();
    }
    else
    {
      ptheWorkDone = new WorkPackIt ();
    } // if
    // Return the operating status at this point.
    ptheWorkDone.m_Status = theStatus;
    if (!isSuccess)
    {
      logParameterError (theStatus);
    } // if
    theLoad.m_theData = null;
    theLoad.m_theWorkDone = ptheWorkDone;
    theLoad.m_theStatus = theStatus;
    theLoad.m_isGoodPayLoad = isSuccess;
    return theLoad;
  } // checkParams

  /**
   * Method logParameterError is called to log the error string in the log as an error
   * that represents theStatus. These are the enum StatusIds values.
   * @param[in] theStatus is the status value to be logged to the log file.
   */
  public void logParameterError (int theStatus)
  {
    String theError = toParameterErrorStr (theStatus);
    // Log the parameter issue as an error.
    m_theLogger.error ("Status error: " + theError);
  } // logParameterError

  /**
   * Method toParameterErrorStr is called to return the error string
   * that represents theStatus value. These are the enum StatusIds values.
   * @param[in] theStatus is the status value to be returned as string.
   * \return the string representation of theStatus.
   */
  public String toParameterErrorStr (int theStatus)
  {
    String theStr;

    switch (theStatus)
    {
      case THREADIT_STATUS_START :
        theStr = "ThreadIt: First status message";
        break;
      case THREADIT_WORKDONE_NO_RESULT :
        theStr = "ThreadIt: no work done yet";
        break;
      case THREADIT_WORKDONE_INVALID_INSTRUCTION :
        theStr = "ThreadIt: invalid work instruction";
        break;
      case THREADIT_WORKDONE_NO_METHOD :
        theStr = "ThreadIt: no method for work instruction";
        break;
      case THREADIT_WORKDONE_TIME_OUT :
        theStr = "ThreadIt: timeout waiting for work to complete";
        break;
      case THREADIT_WORKDONE_INVALID_DONE_QUEUE :
        theStr = "ThreadIt: queue to return result not specified";
        break;
      case THREADIT_WORKDONE_INVALID_EVENT :
        theStr = "ThreadIt: an event occurred that has no valid handler";
        break;
      case THREADIT_WORKDONE_NO_EVENT_METHOD :
        theStr = "ThreadIt: there is no handler for the workinstruction";
        break;
      case THREADIT_THREADIT_STATUS_OK :
        theStr = "ThreadIt: success";
        break;
      case THREADIT_STATUS_PARAM_OBJECT_NULL :
        theStr = "ThreadIt: expected input work object is not supplied as expected";
        break;
      case THREADIT_STATUS_PARAM_WORK_PACK_NULL :
        theStr = "ThreadIt: input work pack is null";
        break;
      case THREADIT_PAYLOAD_NOT_EXPECTED :
        theStr = "ThreadIt: object supplied when it is not expected";
        break;
      case THREADIT_STATUS_LAST :
        theStr = "ThreadIt: status last";
        break;
      default :
        theStr = "ThreadIt: status invalid";
    } // switch
    return theStr;
  } // toParameterErrorStr

} // class ThreadIt
