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
 * Title : TimeIt
 * --------------------------------------------------------------------------
 * Description :
 * Class TimeIt is used to measure time intervals. Timing for a given
 * time period is started and calls into the instance indicate how much
 * time has elapsed or when the timer expires. There are no events
 * associated with timer expiry and this class works on a polled basis.
 * --------------------------------------------------------------------------
 * Copyright: Copyright (c) Ashkel Software 2008
 * Company:   Ashkel Software
 * @author Ari Edinburg
 * @version 1.0
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

/**
 * Class TimeIt is used to measure time intervals. The class includes critical section management and thus supports simultaneous access via multiple threads.
 */
public class TimeIt
{
  /**
   * m_TStart measures the start of a timing operation and is the system
   * number of milliseconds.
   */
  protected long     m_TStart;
  /**
   * m_TStop measures the end of a timing operation and is the system
   * number of milliseconds.
   */
  protected long     m_TStop;
  /**
   * m_IsTiming indicates if execution timing is in progress.
   */
  protected boolean  m_IsTiming;
  /**
   * m_TimeAllowed is the time allowed before the timer expires
   * in milliseconds.
   */
  protected long     m_TimeAllowed;

  /**
   * Method TimeIt is the constructor for the class and creates an
   * initialised TimeIt instance ready to commence timing operations.
   */
  public TimeIt()
  {
    // Initialise the timing variables.
    m_TStart = 0;
    m_TStop  = 0;
    m_IsTiming = false;
    m_TimeAllowed = 0;
  } // method TimeIt
  
  /**
   * Method IsExpired is provided for use in the WorkerMethodType function
   * to determine if the time allowed for processing has elapsed or not. If
   * the time available has elpased then the worker method should stop
   * processing and return the status WORKDONE_TIME_OUT in the cWorkDoneIt
   * result.
   * Elapsed returns the time elapsed so far for work execution in milliseconds.
   * If return value of zero and the method returns TRUE indicates that timing is
   * not currently in progress.
   * Method Expired returns true if the timer has expired.
   */
  public synchronized boolean isExpired (LongHolder Elapsed) 
  {
    boolean Expired = false;

    // Get the time elapsed so far.
    timeElapsed (Elapsed);
    // Check if there is still time available.
    if (m_TimeAllowed == 0)
      Expired = false;
    else
      Expired = (Elapsed.m_theValue > m_TimeAllowed);
    // Return the method result.
    return Expired;
  } // IsExpired

  /**
   * Method StartTiming is called to start timing of the time period
   * specified. From the point of this call, the instance may be used
   * to check when the timing period has elapsed.<p>
   * TimeAllowed : Specifies the time period before the timer expires.
   *               Timing will start only if a non-zero value is given.<p>
   * Method StartTiming returns true if timing is started successfully.
   */
  public synchronized boolean StartTiming (long TimeAllowed)
  {
    boolean hasStarted = false;

    // Record the time allowed for work execution.
    m_TimeAllowed = 0;
    if (TimeAllowed > 0)
    {
      m_TimeAllowed = TimeAllowed;
      hasStarted = true;
      // Start the timer.
      m_TStart = System.currentTimeMillis ();
      m_TStop = m_TStart + m_TimeAllowed;      
      // Timing is in progress.
      m_IsTiming = true;
    } // if
    // Return the method status.
    return hasStarted;
  } // method StartTiming

  /**
   * Method StopTiming is called to record the end of the timing period
   * whether or not the timer has expired. Method StopTiming returns the
   * time elapsed so far from the timer start in milliseconds.
   */
  public synchronized long StopTiming ()
  {
    long Elapsed = 0;

    // Get the time Elapsed so far.
    Elapsed = TimeElapsed ();
    // Stop timing.
    m_IsTiming = false;
    // Return the time elapsed.
    return Elapsed;
  } // method StopTiming
  
  /**
   * Method TimeElapsed calculates how much time has so far elapsed since timing
   * was started.
   * Elapsed returns the time elapsed since timing was started.
   * Method TimeElapsed returns true if timing is currently in progress.
   */
  public boolean timeElapsed (LongHolder Elapsed) 
  {
    boolean IsTiming;
    long theStopTime;

    // Start the timer.
    theStopTime = System.currentTimeMillis ();
    // Calculate the time elapsed so far.
    // Get the time differences from the start to the end.
    if (theStopTime >= m_TStart)
      // The timer has not wrapped.
      Elapsed.m_theValue = theStopTime - m_TStart;
    else
      // The timer has wrapped around.
      Elapsed.m_theValue = (Long.MAX_VALUE - m_TStart) + theStopTime;
    // Indicate if timing is in progress or not.
    IsTiming = m_IsTiming;
    // Return result.
    return IsTiming;
  } // TimeElapsed  

  /**
   * Method TimeRemaining calculates how much time remains before the timer
   * times out. Method TimeRemaining returns the number of milliseconds
   * left until the timer expires. A return value of zero implies that
   * the timer has expired and that there is no time left.
   */
  public synchronized long timeRemaining ()
  {
    long theStopTime = 0;
    long theTimeElapsed = 0;
    long theTimeRemaining = 0;

    theStopTime = System.currentTimeMillis ();
    // Calculate the time elapsed so far.
    // Get the time differences from the start to the end.
    if (theStopTime >= m_TStart)
    {
      // The timer has not wrapped.
      theTimeElapsed = theStopTime - m_TStart;
    }
    else
    {
      // The timer has wrapped around.
      theTimeElapsed = (Long.MAX_VALUE - m_TStart) + theStopTime;
    }
    // Get the time remaining.    
    theTimeRemaining = m_TimeAllowed - theTimeElapsed;
    if (theTimeRemaining < 0)
    {
      theTimeRemaining = 0;
    } // if
    // Return the result.
    return theTimeRemaining;
  } // timeRemaining

  /**
   * Method Reset is invoked to reset the timer so that it
   * can be used again in a call to StartTiming.
   */
  public synchronized void Reset ()
  {
    // Reset the variables.
    m_TStart = 0;
    m_TStop  = 0;
    m_IsTiming = false;
    m_TimeAllowed = 0;
  } // Method Reset

  /**
   * Method IsExpired is provided to determine, once a timing operation
   * has been started, if the time allowed for timing has elapsed or not.
   * If the time available has elpased then the method returns true. If
   * there is still remaining time then the method returns false.
   */
  public boolean IsExpired ()
  {
    // Check if timing is still in progress or not.
    return (timeRemaining () <= 0);
  } // method IsExpired

  /**
   * Method stillTiming is called to determine if timing is still
   * currently in progress.
   */
  public boolean stillTiming ()
  {
    // Check if timing is still in progress or not.
    return (timeRemaining () > 0);
  } // method stillTiming

  /**
   * Method TimeElapsed calculates how much time has so far elapsed
   * since timing was started. This method may be called as many times
   * as required. The method will always return the number of
   * milliseconds elapsed since timing started whether or not the
   * timer timing is still in progress.
   */
  private synchronized long TimeElapsed ()
  {
    long Elapsed = 0;
    long theTimeNow;

    // Get the time now.
    theTimeNow = System.currentTimeMillis ();
    // Calculate the time elapsed so far.
    // Get the time differences from the start to the end.
    if (theTimeNow >= m_TStart)
    {
      // The timer has not wrapped.
      Elapsed = theTimeNow - m_TStart;
    }
    else
    {
      // The timer has wrapped around.
      Elapsed = (Long.MAX_VALUE - m_TStart) + theTimeNow;
    }
    // Return the time that has elapsed since timing started.
    return Elapsed;
  } // TimeElapsed

} // Class TimeIt
