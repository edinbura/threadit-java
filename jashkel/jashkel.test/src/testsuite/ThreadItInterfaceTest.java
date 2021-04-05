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

import java.lang.ref.WeakReference;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.threadit.ThreadTestFrame;

import au.com.ashkel.javalib.threads.TestResult;
import au.com.ashkel.javalib.threads.TestResultQ;
import au.com.ashkel.javalib.threads.ThreadItMessage;

/**
 * Class ThreadItInterfaceTest is the junit test suite for the interface functionality
 * of the ThreadIt lilbrary. The main classes that are part of this test are;
 * ThreadItA, ThreadItB, ComponentA and ComponentB.
 * @author Ari Edinburg
 * 18/08/2011
 */
public class ThreadItInterfaceTest
{
  private static final Logger logger = Logger.getLogger (ThreadItInterfaceTest.class);
  protected ThreadItA m_theImplA = null;  // The implementation of component A - implements calls to functions A and B
  protected ThreadItB m_theImplB = null;  // The implementation of component B - implements calls to functions C and D
  protected TestResultQ m_theResultQFromA = new TestResultQ (); // the queue to return results from the implementation of Component A
  protected TestResultQ m_theResultQFromB = new TestResultQ (); // the queue to return results from the implementation of Component B 
  protected long m_theWaitTime = 5000; // general wait time to get results from the queues.

  @Before
  public void setUp () throws Exception
  {
	  logger.trace("setUp()");
    // Create the implementations of the components. 
    m_theImplA = new ThreadItA ("ThreadItA", m_theResultQFromA);
    m_theImplB = new ThreadItB ("ThreadItB", m_theResultQFromB);    
  } // setUp

  @After
  public void tearDown () throws Exception
  {
    if (m_theImplA != null)
    {
      m_theImplA.stopIt ();
    } // if
    if (m_theImplB != null)
    {
      m_theImplB.stopIt ();
    } // if 
  } // tearDown
  
  /**
   * Method callOnComponentA shows how to use a lightweight interface to call a function
   * on ComponentA. We expect a reply from the implementation showing us that the
   * correct method was invoked.
   */
  @Test
  public void callOnComponentA ()
  {
    long    theWorkId = 0;
    boolean isResult = false;    
    TestResult theResult = new TestResult ();
    ThreadItMessage aMsg = null;
    
    // Create the interface of the component and choose the implementation.
    ComponentA theCompA = new ComponentA (m_theImplA);
    // Call the function on the implementation.
    theCompA.callFunctionA ();

    // Check the result.
    theResult = m_theResultQFromA.getResult (0, m_theWaitTime);
    // Is it a good result.
    assertEquals ("result", true, theResult.isSuccess ());
    // Did function A on Component A actually execute.
    assertEquals ("result", "functionA", theResult.getOperationName());    
  } // callOnComponentA
  
  /**
   * Method callOnComponentB shows how to use a lightweight interface to call a function
   * on ComponentB. We expect a reply from the implementation showing us that the
   * correct method was invoked.
   */
  @Test
  public void callOnComponentB ()
  {
    long    theWorkId = 0;
    boolean isResult = false;    
    TestResult theResult = new TestResult ();
    ThreadItMessage aMsg = null;
    
    ComponentB theCompB = new ComponentB (m_theImplB);
    theCompB.callFunctionC ();

    theResult = m_theResultQFromB.getResult (0, m_theWaitTime);
    assertEquals ("result", true, theResult.isSuccess ());
    assertEquals ("result", "functionC", theResult.getOperationName());    
  } // callOnComponentB
  
  /**
   * Method callOnComponentBWithReplyToFunctionB shows how to use the lightweight interfaces
   * to use the full component specification to invoke functions on the components from within
   * ComponentB without introducing implementation dependencies.  
   */
  @Test
  public void callOnComponentBWithReplyToFunctionB ()
  {
    long    theWorkId = 0;
    boolean isResult = false;    
    TestResult theResult = new TestResult ();
    ThreadItMessage aMsg = null;
    
    // Call function D on component B. Function D will reply to the specified implementation
    // using the specified function - in this case Function B on Component A. 
    ComponentB theCompB = new ComponentB (m_theImplB);
    theCompB.callFunctionD (m_theImplA, ComponentA.FUNCTION_B);

    // See that function D was invoked. 
    theResult = m_theResultQFromB.getResult (0, m_theWaitTime);
    assertEquals ("result", true, theResult.isSuccess ());
    assertEquals ("result", "functionD", theResult.getOperationName());
    
    // Now check that function D has replied to implementation A and called Function B.
    // To see how this works, look at the interface for callFunctionD and how the implementation
    // of function D works.
    theResult = m_theResultQFromA.getResult (0, m_theWaitTime);
    assertEquals ("result", true, theResult.isSuccess ());
    assertEquals ("result", "functionB", theResult.getOperationName());    
    
  } // callOnComponentBWithReplyToFunctionB

} // ThreadItInterfaceTest
