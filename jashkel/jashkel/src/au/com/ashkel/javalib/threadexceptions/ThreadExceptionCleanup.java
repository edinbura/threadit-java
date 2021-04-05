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
 * Title:        ThreadExceptionCleanup
 * Description:  ThreadExceptionCleanup forms part of the SmartThread
 *               extension of ThreadGroup that provides support for properly
 *               handling exceptions that occur on secondary threads.
 *               ThreadExceptionCleanup forms part of a framework supporting
 *               the production of robust code in terms of dealing
 *               with error situations in multi-threaded java programs.
 *
 * Interface ThreadExceptionCleanup defines the method that a class should
 * implement to do cleanup for an object running on a thread after it has
 * thrown an exception and before it is shut down by the system. This method
 * will be called after an exception has occurred on a secondary thread, but
 * before the thread has been terminated. Implementing this method will allow
 * you to execute code inside of your runnable object after an exception was
 * thrown from its run() method. This method is called  from
 * exceptionOccurred() method of the ThreadExceptionListener class. See the
 * ThreadExceptionListener interface for details.
 *
 * Copyright:    Copyright (c) 2008
 * Company:      Ashkel Software
 * @author based on code examples in Java Report, August 1998
 * @version 1.0
 */

package au.com.ashkel.javalib.threadexceptions;

/**
 * Interface: ThreadExceptionCleanup
 * Interface ThreadExceptionCleanup provides the interface for listeners to
 * call to have cleanup work on an exception within a thread performed.
 * This interface may be implemented by the class that has the thread that
 * raised the exception. This gives the thread the opportunity to do its
 * own cleanup.
 */
public interface ThreadExceptionCleanup
{
/**
 * Method cleanupOnException
 * Method exceptionOccurred is invoked on a listener instance when an
 * uncaught exception occurs on a secondary thread. The following information
 * is passed to the listener instance.
 * @param threadException is the exception information.
 */
public void cleanupOnException (Throwable threadException);

} // interface ThreadExceptionCleanup