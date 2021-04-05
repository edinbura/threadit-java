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

import static org.junit.Assert.*;

import java.util.concurrent.Semaphore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import au.com.ashkel.javalib.concurrency.WaitForObjects;

/**
 * Class WaitForObjectsTest is the junit test suite for the WaitForObjects class.
 * @author Ari Edinburg
 */
public class WaitForObjectsTest
{
  /** MAX_SIGNALS is the number of signallers that are tested. */
  static final public int MAX_SIGNALS = 10;
  
  /** m_theWaiter is the controlling object instance for the WaitForObjects methods. */
  WaitForObjects m_theWaiter = null;
  /** m_theSignals is used to signal the waiters waiting on the WaitForObjects methods. */
  Semaphore m_theSignals[] = null;
  /**
   * Method setUp is called before each test. The method initialises the environment for each test  case. 
   * In this case the method creates m_theWaiter and creates the signals to be used for waiter 
   * notification purposes. 
   * @throws java.lang.Exception
   */
  @Before
  public void setUp () throws Exception
  {
    m_theWaiter = new WaitForObjects ();
    m_theSignals = new Semaphore[MAX_SIGNALS];
    for (int i = 0; i < MAX_SIGNALS; i++)
    {
      m_theSignals[i] = new Semaphore (0);
    } // for
    // Setup the environment for the subsequent waitForObjects call.
    m_theWaiter.initWaitForObjects (m_theSignals);
  } // setup

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown () throws Exception
  {
    m_theWaiter.stopWaiting ();
  } // tearDown

  /**
   * Test method that checks that the waitForObjects method will timeout. 
   */
  @Test
  public void testWaitForObjectsTimeOut ()
  {
    int theResult = 0;
    theResult = m_theWaiter.waitForObjects (100);
    assertEquals ("result", WaitForObjects.WAIT_TIMEDOUT, theResult);
  } // testWaitForObjectsTimeOut
  
  /**
   * Test method that checks that the waitForObjects method will be signalled once. 
   */
  @Test
  public void testWaitForObjectsSignalled ()
  {
    int theResult = 0;
    SignallerTask theSignaller = new SignallerTask (m_theSignals[0], 50);    
    theResult = m_theWaiter.waitForObjects (200);
    assertEquals ("result", WaitForObjects.WAIT_SUCCEEDED, theResult);
  } // testWaitForObjectsSignalled
  
  /**
   * Test method for that checks that waitForObjects identifies the signal it was signalled on.
   */
  @Test
  public void testWaitForObjectsSignalId1 ()
  {
    int theResult = 0;
    SignallerTask theSignaller = new SignallerTask (m_theSignals[1], 50);    
    theResult = m_theWaiter.waitForObjects (200);
    assertEquals ("result", WaitForObjects.WAIT_SUCCEEDED + 1, theResult);
  } // testWaitForObjectsSignalId
  
  /**
   * Test method for that checks that waitForObjects identifies the signal it was signalled on.
   */
  @Test
  public void testWaitForObjectsSignalId2 ()
  {
    int theResult = 0;
    SignallerTask theSignaller = new SignallerTask (m_theSignals[9], 50);    
    theResult = m_theWaiter.waitForObjects (200);
    assertEquals ("result", WaitForObjects.WAIT_SUCCEEDED + 9, theResult);
  } // testWaitForObjectsSignalId1
  
  /**
   * Test method for waitForObjects will be signalled for some signalled objects.
   */
  @Test
  public void testWaitForObjectsOnMultipleSignals1 ()
  {
    int theResult = 0;
    Semaphore[] theList = new Semaphore[MAX_SIGNALS / 2];
    
    for (int i = 0; i < MAX_SIGNALS / 2; i++)
    {
      theList[i] = m_theSignals[i];
    } // for 
    
    SignallerTask theSignaller = new SignallerTask (theList, 50);
    for (int i = 0; i < MAX_SIGNALS / 2; i++)
    {
      theResult = m_theWaiter.waitForObjects (200);
      assertTrue ("result", theResult >= 0);
    } // for 
  } // testWaitForObjectsOnMultipleSignals
  
  /**
   * Test method for waitForObjects will be signalled for all signalled objects.
   */
  @Test
  public void testWaitForObjectsOnMultipleSignals2 ()
  {
    int theResult = 0;
    Semaphore[] theList = new Semaphore[MAX_SIGNALS];
    
    for (int i = 0; i < MAX_SIGNALS; i++)
    {
      theList[i] = m_theSignals[i];
    } // for 
    
    SignallerTask theSignaller = new SignallerTask (theList, 50);
    for (int i = 0; i < MAX_SIGNALS; i++)
    {
      theResult = m_theWaiter.waitForObjects (200);
      assertTrue ("result", theResult >= WaitForObjects.WAIT_SUCCEEDED);
    } // for 
  } // testWaitForObjectsOnMultipleSignals2
  
  /**
   * Test method for waitForObjects will be signalled for all signalled and timed out objects.
   */
  @Test
  public void testWaitForObjectsSignalledAndTimedOut ()
  {
    int theResult = 0;
    Semaphore[] theList = new Semaphore[MAX_SIGNALS - 1];
    int theSuccessCount = 0;
    int theFailCount = 0;
    
    for (int i = 0; i < MAX_SIGNALS - 1; i++)
    {
      theList[i] = m_theSignals[i];
    } // for 
    
    SignallerTask theSignaller = new SignallerTask (theList, 50);
    for (int i = 0; i < MAX_SIGNALS; i++)
    {
      theResult = m_theWaiter.waitForObjects (100);
      if (theResult >= WaitForObjects.WAIT_SUCCEEDED)
      {
        theSuccessCount++;
      } //
      if (theResult == WaitForObjects.WAIT_TIMEDOUT)
      {
        theFailCount++;
      } // if 
    } // for
    assertTrue ("result", theSuccessCount >= MAX_SIGNALS - 1);
    assertTrue ("result", theFailCount == 1);    
  } // testWaitForObjectsSignalledAndTimedOut 
    
  /**
   * Test method for waitForAllObjects to be signalled.
   */
  @Test
  public void testWaitForAllObjectsSignalled ()
  {
    int theResult = 0;
    Semaphore[] theList = new Semaphore[MAX_SIGNALS];
    
    for (int i = 0; i < MAX_SIGNALS; i++)
    {
      theList[i] = m_theSignals[i];
    } // for 
    
    SignallerTask theSignaller = new SignallerTask (theList, 50);
    theResult = m_theWaiter.waitForAllObjects (800);
    assertTrue ("result", theResult == WaitForObjects.WAIT_SUCCEEDED);
  } // testWaitForAllObjectsSignalled
  
  /**
   * Test method for waitForAllObjects to timed out. 
   */
  @Test
  public void testWaitForAllObjectsTimedOut ()
  {
    int theResult = 0;
    Semaphore[] theList = new Semaphore[MAX_SIGNALS - 1];
    
    for (int i = 0; i < MAX_SIGNALS -1 ; i++)
    {
      theList[i] = m_theSignals[i];
    } // for 
    
    SignallerTask theSignaller = new SignallerTask (theList, 50);
    theResult = m_theWaiter.waitForAllObjects (200);
    assertTrue ("result", theResult == WaitForObjects.WAIT_TIMEDOUT);
  } // testWaitForAllObjectsTimedOut

  /**
   * Test method for waitForObjects that provides it own single signal list to be signalled. 
   */
  @Test
  public void testWaitForObjectsWithList1 ()
  {
    int theResult = 0;
    Semaphore[] theList = new Semaphore[1];
    
    theList[0] = new Semaphore (0);
    SignallerTask theSignaller = new SignallerTask (theList, 50);
    theResult = m_theWaiter.waitForObjects (theList, 200);
    assertTrue ("result", theResult == WaitForObjects.WAIT_SUCCEEDED);
  } // testWaitForObjectsWithList1
    
  /**
   * Test method for waitForAllObjects that provides it own multiple signal list to be signalled.
   */
  @Test
  public void testWaitForAllObjectsWithList ()
  {
    int theResult = 0;
    Semaphore[] theShortList = new Semaphore[MAX_SIGNALS];
    Semaphore[] theList = new Semaphore[MAX_SIGNALS];    
    
    for (int i = 0; i < MAX_SIGNALS; i++)
    {
      theShortList[i] = m_theSignals[i];
      theList[i] = m_theSignals[i];
    } // for 
    
    SignallerTask theSignaller = new SignallerTask (theShortList, 50);
    theResult = m_theWaiter.waitForAllObjects (theList, 800);
    assertTrue ("result", theResult == WaitForObjects.WAIT_SUCCEEDED);
  } // testWaitForAllObjectsWithList
  
  /**
   * Test method for waitForObjects that provides it own multiple signal list to be signalled.
   */
  @Test
  public void testWaitForAllObjectsWithListAndTimeout ()
  {
    int theResult = 0;
    Semaphore[] theShortList = new Semaphore[MAX_SIGNALS - 1];
    Semaphore[] theList = new Semaphore[MAX_SIGNALS];    
    
    for (int i = 0; i < MAX_SIGNALS - 1 ; i++)
    {
      theShortList[i] = m_theSignals[i];
      theList[i] = m_theSignals[i];
    } // for 
    theList[MAX_SIGNALS - 1] = m_theSignals[MAX_SIGNALS - 1];
    
    SignallerTask theSignaller = new SignallerTask (theShortList, 50);
    theResult = m_theWaiter.waitForAllObjects (theList, 500);
    assertTrue ("result", theResult == WaitForObjects.WAIT_TIMEDOUT);
  } // testWaitForAllObjectsWithListAndTimeout
  
  /**
   * Test method for waitForObjects working with clean-up routines and not initialised errors. 
   */
  @Test
  public void testWaitForObjectsNotInitialised ()
  {
    int theResult = 0;
    Semaphore[] theList = new Semaphore[1];
    
    theList[0] = m_theSignals[0];
    m_theWaiter.stopWaiting ();
    
    SignallerTask theSignaller = new SignallerTask (theList, 50);
    theResult = m_theWaiter.waitForObjects (200);
    assertTrue ("result", theResult == WaitForObjects.WAIT_NOT_INITIALISED);
  } // testWaitForObjectsNotInitialised
  
  /**
   * Test method for waitForAllObjects working with clean-up routines and not initialised errors.
   */
  @Test
  public void testWaitForAllObjectsNotInitialised ()
  {
    int theResult = 0;
    Semaphore[] theList = new Semaphore[2];
    
    theList[0] = m_theSignals[0];
    theList[1] = m_theSignals[1];    
    m_theWaiter.stopWaiting ();
    
    SignallerTask theSignaller = new SignallerTask (theList, 50);
    theResult = m_theWaiter.waitForAllObjects (200);
    assertTrue ("result", theResult == WaitForObjects.WAIT_NOT_INITIALISED);
  } // testWaitForAllObjectsNotInitialised
  
} // class WaitForObjectsTest
