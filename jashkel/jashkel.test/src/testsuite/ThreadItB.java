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
 * Class ThreadItB is an implementation of Component B.
 * @author Ari Edinburg
 * 18/08/2011
 */
public class ThreadItB extends ThreadIt
{
  // Attributes  
  private static final Logger m_theLogger = Logger.getLogger(ThreadItB.class);

  protected TestResultQ m_theResultQ = null; // queue to return results to the test program. 

  public ThreadItB (String theThreadName, TestResultQ theResultQ)
  {
    super (theThreadName);
    m_theResultQ = theResultQ;
    // Initialise the work package.
    testItInit ();
  } // ThreadItB

  ThreadItB (int Priority, TestResultQ theResultQ)
  {
    m_theResultQ = theResultQ;    
    // Initialise the work package.
    testItInit ();
  } // ThreadItB

  public void stopIt ()
  {
    // Terminate the work processing thread.
    stopThread ();
    // Wait for the thread to stop.
    waitForThreadToStop (1000);
  } // stopIt

  /**
   * method testItInit associates work identity values with methods on the instance. 
   */
  public void testItInit ()
  {
    // Setup all the work packages and associated messages..
    try
    {
      setWorkerMethod ("functionC", ComponentB.FUNCTION_C);
      setWorkerMethod ("functionD", ComponentB.FUNCTION_D);
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }  // testItInit

  /**
   * Method functionC is the implementation of the ComponentB's method 
   * of the same name.   
   */
  public WorkPackIt functionC (WorkPackIt pWorkPack)
  {
    PayLoad theRequest = null; 
    
    theRequest = checkParamsNoData (pWorkPack, true);    
    m_theResultQ.publish(0, "functionC", true, "functionC good");
    // Return the method status.
    return  theRequest.getWorkDone ();
  } // functionC
  
  /**
   * Method functionD is the implementation of the ComponentD's method 
   * of the same name.   
   * This function, however, has to return the result to the specified 
   * invoker and the specified method or worker or function on the
   * invoker. The implementation uses ThreadIt's in-built features
   * for this. Note in this case there are alternatives to using
   * the ComponentA interface. However, this particular approach allows fully specified
   * access to component A from anywhere without incurring dependencies in the code.
   */
  public WorkPackIt functionD (WorkPackIt pWorkPack)
  {
    PayLoad theRequest = null; 
    
    theRequest = checkParamsNoData (pWorkPack, true);    
    m_theResultQ.publish(0, "functionD", true, "functionD good");
    
    // Now send a reply to function B in Component A.
    ThreadIt theImplA = theRequest.getWorkDone().m_ptheSource;
    long theInstruction = theRequest.getWorkDone().getWorkInstruction();
    if (theImplA != null)
    {
      ComponentA theCompA = new ComponentA (theImplA);
      if (theInstruction == ComponentA.FUNCTION_B)
      {
        theCompA.callFunctionB ();
      } // if 
    } // if 
    // Return the method status.
    return  theRequest.getWorkDone ();
  } // functionD

}; // class ThreadItB