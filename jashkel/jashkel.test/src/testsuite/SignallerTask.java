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

import java.util.concurrent.Semaphore;

/**
 * class SignallerTask is used to signal waiters during the WaitForObjectsTest case execution.
 * The class allows a test to signal the test case waiter asynchronously while the test 
 * case executes. The task can accept a single signal to signal asynchronously after some
 * delay or alternatively the task can accept an array of signals to signal asynchronously after some
 * specified delay.
 * @author Ari Edinburg 
 */
public class SignallerTask extends Thread
{
  protected long m_theDelay = 0;  
  protected Semaphore[] m_theSemaphoreList = null;

  public SignallerTask (Semaphore[] theSemaphoreList, long theDelay)
  {
    m_theSemaphoreList = theSemaphoreList;
    m_theDelay = theDelay;    
    start();
  } // constructor SemaphoreSignaller
  
  public SignallerTask (Semaphore theSemaphore, long theDelay)
  {
    m_theSemaphoreList = new Semaphore[1];
    m_theSemaphoreList[0] = theSemaphore;
    m_theDelay = theDelay;
    start();
  } // constructor SemaphoreSignaller
  
  public void run()
  {
    if (m_theSemaphoreList != null)
    {
      try
      {
        Thread.sleep (m_theDelay);
      } // try
      catch (InterruptedException e)
      {
      } // catch
      for (int i = 0; i < m_theSemaphoreList.length; i++)
      {
        m_theSemaphoreList[i].release ();
      } // for
    } // if 
  } // run

}; // class SignallerTask