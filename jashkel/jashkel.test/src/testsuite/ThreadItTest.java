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


import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.threadit.ActiveModel;

import au.com.ashkel.javalib.concurrency.WaitForObjects;
import au.com.ashkel.javalib.threads.TestResult;
import au.com.ashkel.javalib.threads.TestResultQ;
import au.com.ashkel.javalib.threads.ThreadItMessage;
import au.com.ashkel.javalib.threads.WorkPackIt;

/**
 * Class ThreadItTest provides some basic tests that demonstrate
 * some of the uses of ThreadIt. 
 * @author Ari Edinburg
 * 17/08/2011
 */
public class ThreadItTest
{
  protected ActiveObject m_theWorker = null;
  protected TestResultQ m_theResultQ = new TestResultQ ();
  protected long m_theWaitTime = 5000;
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp () throws Exception
  {
    // Initialise the active object instance.
    m_theWorker = new ActiveObject ("ActiveObject", m_theResultQ);
  } // setUp

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown () throws Exception
  {
    if (m_theWorker != null)
    {
      m_theWorker.stopIt ();
    } // if 
  } // tearDown
  
  /**
   * Test method that checks that ThreadIt initialisation has taken place.  
   */
  @Test
  public void testWaitForStartUp ()
  {
    TestResult theResult = new TestResult ();
    boolean isResult = false;

    theResult = m_theResultQ.getResult (0, m_theWaitTime);
    assertEquals ("result", true, theResult.isSuccess ());
  } // testWaitForStartUp
  
  /**
   * Test method that checks that the THREADIT_TEST_METHOD is invoked
   * using the normal worker invocation process.    
   */
  @Test
  public void testWaitForWorkToBeDone ()
  {
    long    theWorkId = 0;
    boolean isResult = false;    
    TestResult theResult = new TestResult ();
    ThreadItMessage aMsg = null;
    
    // Send a message.
    aMsg = new ThreadItMessage (ActiveObject.THREADIT_TEST_METHOD);
    theWorkId = aMsg.sendTo (m_theWorker);

    theResult = m_theResultQ.getResult (0, m_theWaitTime);
    assertEquals ("result", true, theResult.isSuccess ());
    
    theResult = m_theResultQ.getResult (0, m_theWaitTime);
    assertEquals ("result", true, theResult.isSuccess ());
  } // testWaitForWorkToBeDone
  
  /**
   * Test method that checks that the THREADIT_EVENT_METHOD is invoked
   * using the event notification process. This places the event
   * at the head of the queue so this should be processed next.   
   */
  @Test
  public void testWaitForEvent ()
  {
    long    theWorkId = 0;
    boolean isResult = false;    
    TestResult theResult = new TestResult ();
    ThreadItMessage aMsg = null;
    
    m_theWorker.notifyEvent (ActiveObject.THREADIT_EVENT_METHOD);    

    theResult = m_theResultQ.getResult (0, m_theWaitTime);
    assertEquals ("result", true, theResult.isSuccess ());
    
    theResult = m_theResultQ.getResult (0, m_theWaitTime);
    assertEquals ("result", true, theResult.isSuccess ());
  } // testWaitForEvent
  
  /**
   * Test method that checks that the THREADIT_TEST_METHOD is invoked
   * using the event notification process. This places the event
   * at the head of the queue so this should be processed next.
   * This is most probably a very unlikely scenario.   
   */
  @Test
  public void testWaitForEventUsingNormalWorker ()
  {
    long    theWorkId = 0;
    boolean isResult = false;    
    TestResult theResult = new TestResult ();
    ThreadItMessage aMsg = null;
    
    // Notify of the event
    m_theWorker.notifyEvent (ActiveObject.THREADIT_TEST_METHOD);
    
    theResult = m_theResultQ.getResult (0, m_theWaitTime);
    assertEquals ("result", true, theResult.isSuccess ());
    
    theResult = m_theResultQ.getResult (0, m_theWaitTime);
    assertEquals ("result", true, theResult.isSuccess ());
  } // testWaitForEventUsingNormalWorker
  
  /**
   * Test testEventNoPayLoad that checks that the checkParamsNoData method
   * in ThreadIt is working with regards to a payload - data to operate on
   * during work method execution. In this case the method is invoked with
   * no payload.  
   */
  @Test  
  public void testEventNoPayLoad ()
  {
    long    theWorkId = 0;
    boolean isResult = false;    
    TestResult theResult = new TestResult ();
    ThreadItMessage aMsg = null;
    
    // Notify of the event
    WorkPackIt aWorkPack = new WorkPackIt ();
    aWorkPack.m_Object = null;
    m_theWorker.notifyEvent (ActiveObject.THREADIT_EVENT_NO_PAYLOAD_METHOD, aWorkPack);
    
    theResult = m_theResultQ.getResult (0, m_theWaitTime);
    assertEquals ("result", true, theResult.isSuccess ());
    
    theResult = m_theResultQ.getResult (0, m_theWaitTime);
    assertEquals ("result", true, theResult.isSuccess ());
  } // testEventNoPayLoad
  
  /**
   * Test testEventPayLoad that checks that the checkParams method
   * in ThreadIt is working with regards to a payload - data to operate on
   * during work method execution. In this case the method is invoked with
   * a payload.  
   */
  @Test
  public void testEventPayLoad ()
  {
    long    theWorkId = 0;
    boolean isResult = false;    
    TestResult theResult = new TestResult ();
    ThreadItMessage aMsg = null;
    
    // Notify of the event
    WorkPackIt aWorkPack = new WorkPackIt ();
    aWorkPack.m_Object = new Object ();
    m_theWorker.notifyEvent (ActiveObject.THREADIT_EVENT_PAYLOAD_METHOD, aWorkPack);
    
    theResult = m_theResultQ.getResult (0, m_theWaitTime);
    assertEquals ("result", true, theResult.isSuccess ());
    
    theResult = m_theResultQ.getResult (0, m_theWaitTime);
    assertEquals ("result", true, theResult.isSuccess ());
  } // testEventPayLoad
  
  /**
   * Test testEventNoPayLoadButThereIs that checks that the checkParamsNoData method
   * in ThreadIt is working with regards to a payload - data to operate on
   * during work method execution. In this case the method is invoked with
   * a payload but the method is not expecting this..  
   */
  @Test
  public void testEventNoPayLoadButThereIs ()
  {
    long    theWorkId = 0;
    boolean isResult = false;    
    TestResult theResult = new TestResult ();
    ThreadItMessage aMsg = null;
    
    // Notify of the event
    WorkPackIt aWorkPack = new WorkPackIt ();
    aWorkPack.m_Object = new Object ();
    m_theWorker.notifyEvent (ActiveObject.THREADIT_EVENT_NO_PAYLOAD_METHOD, aWorkPack);
    
    theResult = m_theResultQ.getResult (0, m_theWaitTime);
    assertEquals ("result", true, theResult.isSuccess ());
    
    theResult = m_theResultQ.getResult (0, m_theWaitTime);
    assertEquals ("result", false, theResult.isSuccess ());
  } // testEventNoPayLoadButThereIs
  
  /**
   * Test testEventPayLoadButThereIsNot that checks that the checkParams method
   * in ThreadIt is working with regards to a payload - data to operate on
   * during work method execution. In this case the method is invoked without
   * a payload but the method is expecting a payload.  
   */
  @Test
  public void testEventPayLoadButThereIsNot ()
  {
    long    theWorkId = 0;
    boolean isResult = false;    
    TestResult theResult = new TestResult ();
    ThreadItMessage aMsg = null;
    
    // Notify of the event
    WorkPackIt aWorkPack = new WorkPackIt ();
    aWorkPack.m_Object = null;
    m_theWorker.notifyEvent (ActiveObject.THREADIT_EVENT_PAYLOAD_METHOD);
    
    theResult = m_theResultQ.getResult (0, m_theWaitTime);
    assertEquals ("result", true, theResult.isSuccess ());
    
    theResult = m_theResultQ.getResult (0, m_theWaitTime);
    assertEquals ("result", false, theResult.isSuccess ());
  } // testEventPayLoadButThereIsNot
  
  /**
   * Test method testPeriodMethod checks that the period method
   * is working using callbacks. 
   */
  @Test
  public void testPeriodicMethod ()
  {
    long    theWorkId = 0;
    boolean isResult = false;    
    TestResult theResult = new TestResult ();
    
    theResult = m_theResultQ.getResult (0, m_theWaitTime);
    assertEquals ("result", true, theResult.isSuccess ());
    
    try
    {
      m_theWorker.setPeriodicMethod ("periodicMethod");
    }
    catch (IOException anException)
    {
      org.junit.Assert.fail ("unable to set the periodic method");      
    }
    m_theWorker.setPeriod (200);
    
    theResult = m_theResultQ.getResult (0, m_theWaitTime);
    assertEquals ("result", true, theResult.isSuccess ());
  } // testPeriodicMethod

} // ThreadItTest
