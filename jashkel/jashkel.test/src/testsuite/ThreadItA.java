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

import au.com.ashkel.javalib.threads.PayLoad;
import au.com.ashkel.javalib.threads.ProtectedQueue;
import au.com.ashkel.javalib.threads.TestResultQ;
import au.com.ashkel.javalib.threads.ThreadIt;
import au.com.ashkel.javalib.threads.WorkDoneCallback;
import au.com.ashkel.javalib.threads.WorkPackIt;
import test.threadit.Callback;

/**
 * Class ThreadItA is an implementation of Component A.
 * @author Ari Edinburg
 * 18/08/2011
 */
public class ThreadItA extends ThreadIt
{
  // Attributes  
  private static final Logger m_theLogger = Logger.getLogger(ThreadItA.class);

  protected TestResultQ m_theResultQ = null; // queue to return results to the test program. 

  public ThreadItA (String theThreadName, TestResultQ theResultQ)
  {
    super (theThreadName);
    m_theResultQ = theResultQ;
    // Initialise the work package.
    testItInit ();
  } // ThreadItA

  ThreadItA (int Priority, TestResultQ theResultQ)
  {
    m_theResultQ = theResultQ;    
    // Initialise the work package.
    testItInit ();
  } // ThreadItA

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
      setWorkerMethod ("functionA", ComponentA.FUNCTION_A);
      setWorkerMethod ("functionB", ComponentA.FUNCTION_B);
    }
    catch (IOException e)
    {
      m_theLogger.error ("exception will setting worker methods");
    }
  }  // testItInit

  /**
   * Method functionA is the implementation of the ComponentA's method 
   * of the same name.   
   */
  public WorkPackIt functionA (WorkPackIt pWorkPack)
  {
    PayLoad theRequest = null; 

    // Perform standard processing for no incoming data payload.
    theRequest = checkParamsNoData (pWorkPack, true);
    // Tell junit that something has happend in this thread.    
    m_theResultQ.publish(0, "functionA", true, "functionA good");
    // Return the method status.
    return  theRequest.getWorkDone ();
  } // functionA

  /**
   * Method functionA is the implementation of the ComponentA's method 
   * of the same name.   
   */
  public WorkPackIt functionB (WorkPackIt pWorkPack)
  {
    PayLoad theRequest = null; 
    
    // Perform standard processing for no incoming data payload.    
    theRequest = checkParamsNoData (pWorkPack, true);
    // Tell junit that something has happend in this thread. 
    m_theResultQ.publish(0, "functionB", true, "functionB good");
    // Return the method status.
    return  theRequest.getWorkDone ();
  } // functionB

}; // class ThreadItA