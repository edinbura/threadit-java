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
 * Title:        CheckedThreadException
 * Description:  CheckedThreadException forms part of the SmartThread
 *               extension of ThreadGroup that provides support for properly
 *               handling exceptions that occur on secondary threads.
 *               CheckedThreadException forms part of a framework supporting
 *               the production of robust code in terms of dealing
 *               with error situations in multi-threaded java programs.
 *
 * Class CheckedThreadException is used by a code inside the public void run()
 * method of a Runnable to enable it to throw a checked exception. Because
 * public void run() does not advertise that it throws any exceptions, you
 * cannot throw any checked exceptions from run(). All checked exceptions must
 * be handled in the run() method with a catch() clause. This class enables
 * you to wrapper your checked exception inside an unchecked exception and
 * propagate this exception out of run(). It will then be caught by
 * uncaughtException() and passed back to the object instantiating the thread.
 * Two getters are provided to make the additional information available.
 *
 * Copyright:    Copyright (c) 2008
 * Company:      Ashkel Software
 * @author based on code examples in Java Report, August 1998
 * @version 1.0
 */

package au.com.ashkel.javalib.threadexceptions;

/**
 * class CheckedThreadException
 * Class CheckedThreadException is part of framework for handling exceptions
 * that occur on secondary threads. The class allows you to converts a
 * checked exception to an unchecked exception and provides details of the
 * exception that has occurred to the exception handler.
 */
public class CheckedThreadException extends RuntimeException
{
private Thread    m_SourceThread;
// m_SourceThread is the thread in which exception occurred,
private Throwable m_ThreadException;
// m_ThreadException is the actual checked exception that was caught and
// is converted into an unchecked exception.

/**
 * Method CheckedThreadException
 * Method CheckedThreadException creates the exception information that
 * includes the thread name and the details of the exception that occurred.
 * @param sourceThread is the thread on which the exception occurred.
 * @param threadException is the actual checked exception details.
 */
public CheckedThreadException(Thread sourceThread, Throwable threadException)
{
  super("Exception caught in thread ["+sourceThread.getName()+"]");
  // Store the exeception details.
  m_SourceThread = sourceThread;
  m_ThreadException = threadException;
} // CheckedThreadException

/**
 * Method getSourceThread
 * Method getSourceThread returns the thread associated with the exception.
 */
public Thread getSourceThread()
{
  return m_SourceThread;
} // getSourceThread

/**
 * Method getThreadException
 * Method getThreadException returns the checked exception that occurred
 * on the thread.
 */
public Throwable getThreadException()
{
  return m_ThreadException;
} // getThreadException

} // class CheckedThreadException