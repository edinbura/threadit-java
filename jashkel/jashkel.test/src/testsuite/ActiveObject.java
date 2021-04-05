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
package testsuite;

import java.io.IOException;

import org.apache.log4j.Logger;

import test.threadit.Callback;
import au.com.ashkel.javalib.threads.PayLoad;
import au.com.ashkel.javalib.threads.ProtectedQueue;
import au.com.ashkel.javalib.threads.TestResultQ;
import au.com.ashkel.javalib.threads.ThreadIt;
import au.com.ashkel.javalib.threads.WorkDoneCallback;
import au.com.ashkel.javalib.threads.WorkPackIt;

/**
 * Class ActiveObject is a sample thread it used as the basis for the
 * ThreadItTest unit test cases.
 * @author Ari Edinburg
 * 17/08/2011
 */
public class ActiveObject extends ThreadIt
{
  public static final int THREADIT_TEST_METHOD = 0;
  public static final int THREADIT_EVENT_METHOD = 1;
  public static final int THREADIT_EVENT_NO_PAYLOAD_METHOD = 2;
  public static final int THREADIT_EVENT_PAYLOAD_METHOD = 3; 
  // Attributes  
  protected Callback m_theCallback;
  protected ProtectedQueue m_theEventQueue;
  private static final Logger m_theLogger = Logger.getLogger(ActiveObject.class);

  protected TestResultQ m_theResultQ = null; // Queue used to return test results to the unit test cases. 

  public ActiveObject (String theThreadName, TestResultQ theResultQ)
  {
    super (theThreadName);
    m_theResultQ = theResultQ;
    // Initialise the work package.
    testItInit ();
  } // ActiveObject

  ActiveObject (int Priority, TestResultQ theResultQ)
  {
    m_theResultQ = theResultQ;    
    // Initialise the work package.
    testItInit ();
  } // ActiveObject

  public void stopIt ()
  {
    // Terminate the work processing thread.
    stopThread ();
    // Wait for the thread to stop.
    waitForThreadToStop (1000);
  } // StopIt

  /**
   * Associated methods to work ids.
   */
  public void testItInit ()
  {
    // Setup all the work packages and associated messages..
    try
    {
      setWorkerMethod ("setup", THREADIT_TEST_METHOD);
      setWorkerMethod ("event", THREADIT_EVENT_METHOD);
      setWorkerMethod ("eventNoPayload", THREADIT_EVENT_NO_PAYLOAD_METHOD);
      setWorkerMethod ("eventPayload", THREADIT_EVENT_PAYLOAD_METHOD);      
    }
    catch (IOException anException)
    {
      m_theLogger.error ("Error setting up the worker methods", anException);
    }
    //addObserver (m_theCallback);
    setPeriod (100);
    // Send startup result.
    if (m_theResultQ != null)
    {
      m_theResultQ.publish(0, "testItInit", true, "all good");
    } // if 
  }  // testItInit

  /**
   * Method setup is an example worker method implementation. 
   */
  public WorkPackIt setup (WorkPackIt pWorkPack)
  {
    PayLoad theRequest = null; 
    
    theRequest = checkParamsNoData (pWorkPack, true);    
    m_theResultQ.publish(0, "setup", true, "all good");
    // Return the method status.
    return  theRequest.getWorkDone ();
  } // Setup

  /**
   * Method event is an example worker method implementation that
   * can respond to events. 
   */
  public WorkPackIt event (WorkPackIt pWorkPack)
  {
    PayLoad theRequest = null; 
    
    theRequest = checkParamsNoData (pWorkPack, true);    
    m_theResultQ.publish(0, "event", true, "all good");
    // Return the method status.
    return  theRequest.getWorkDone ();
  } // Setup

  /**
   * Method eventNoPayload expects work to be done without a payload. 
   */
  public WorkPackIt eventNoPayload (WorkPackIt pWorkPack)
  {
    PayLoad theRequest = null; 
    
    theRequest = checkParamsNoData (pWorkPack, true);    
    if (theRequest.isOk ())
    {
      if (!theRequest.hasData ())
      {
        m_theResultQ.publish(0, "event-no-payload", true, "there is no payload as expected");
      }
      else
      {
        m_theResultQ.publish(0, "event-no-payload", false, toParameterErrorStr (theRequest.getStatus ()));  
      } // if 
    }
    else
    {
      m_theResultQ.publish(0, "event-no-payload", false, toParameterErrorStr (theRequest.getStatus ()));
    } // if
    // Return the method status.
    return theRequest.getWorkDone ();
  } // Setup
  
  /**
   * Method eventNoPayload expects work to be done with a payload. 
   */
  public WorkPackIt eventPayload (WorkPackIt pWorkPack)
  {
    PayLoad theRequest = null;
    
    theRequest = checkParams (pWorkPack, true);    
    if (theRequest.isOk ())
    {
      if (theRequest.hasData ())
      {
        m_theResultQ.publish(0, "event-payload", true, "there is a payload which is expected");
      }
      else
      {
        m_theResultQ.publish(0, "event-payload", false, toParameterErrorStr (theRequest.getStatus ()));  
      } // if 
    }
    else
    {
      m_theResultQ.publish(0, "event-with-payload", false, toParameterErrorStr (theRequest.getStatus ()));      
    } // if 
    // Return the method status.
    return theRequest.getWorkDone ();
  } // Setup

  /**
   * Method periodicMethod is an example periodicMethod method implementation.
   * In this case the periodic method will issue a callback on each invocation. 
   */
  public WorkPackIt periodicMethod (WorkPackIt pWorkPack)
  {
    PayLoad theRequest = null;
    WorkPackIt theWork = null;
    
    theRequest = checkParamsNoData (pWorkPack, true);    
    if (theRequest.isOk ())
    {
      theWork = theRequest.getWorkDone ();
    }
    theWork.m_SendResult = false;
    theWork.m_NotifyWithCallback = true;
    theWork.m_UseDefaultQ = false;
    theWork.m_Callback = new Callback ();
    return theWork;
  }  // periodicMethod

  /**
   * Class Callback is a sample callback that can be used by ThreadIt on each
   * work request to return a result (as one of many ways to do so).
   */
  class Callback implements WorkDoneCallback 
  {
    // Attributes
    protected int m_theCallCount = 0; // keeps track of how many times the callback has been called.

    // Services
    public Callback ()
    {
      m_theCallCount = 0;
    } // constructor CCallback

    /**
     * The actual callback. 
     */
    public void onWorkDone (String theThreadName, long theWorkItemId) 
    {
      m_theCallCount++;
      m_theResultQ.publish(0, "call back in periodic", true, "call back in periodic");      
    } // onUpdate

    int getCallCount ()
    {
      return m_theCallCount;
    } // getChangeCount

  }; // class Callback    

}; // class ActiveObject