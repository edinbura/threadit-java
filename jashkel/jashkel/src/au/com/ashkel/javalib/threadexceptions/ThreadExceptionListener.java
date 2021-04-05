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
 * Title:        ThreadExceptionListener
 * Description:  ThreadExceptionListener forms part of the SmartThread
 *               extension of ThreadGroup that provides support for properly
 *               handling exceptions that occur on secondary threads.
 *               SmarthThread forms part of a framework supporting the
 *               production of robust code in terms of dealing
 *               with error situations in multi-threaded java programs.
 *
 * Interface ThreadExceptionListener defines the method that a class should
 * implement to handle exceptions that are thrown in its secondary threads.
 * This method will be called after an exception has occurred in the run()
 * method of a Runnable object, but before the thread has been terminated.
 * Implementing this method will allow you to execute code inside of the class
 * that created the secondary thread. You can then optionally call the
 * cleanupOnException() method of the Runnable.
 * See the ThreadExceptionCleanup interface for details.
 *
 * Copyright:    Copyright (c) 2008
 * Company:      Ashkel Software
 * @author based on code examples in Java Report, August 1998
 * @version 1.0
 */

package au.com.ashkel.javalib.threadexceptions;

/**
 * Interface: ThreadExceptionListener
 * Interface ThreadExceptionListener provides the interface for listeners for
 * uncaught/unhandled exceptions on secondary threads.
 */
public interface ThreadExceptionListener
{

/**
 * Method exceptionOccurred
 * Method exceptionOccurred is invoked on a listener instance when an
 * uncaught exception occurs on a secondary thread. The following information
 * is passed to the listener instance.
 * @param runnable is object that runs within a thread with the exception.
 * @param sourceThread is the thread on which the exception has occurred.
 * @param threadException is the exception and details thereof.
 *
 * Note: call the method cleanupOnException on the ThreadExceptionCleanup
 * interface if some cleanup processing is performed by the thread that has
 * raised the the exception and it implements the interface.
 */
 void exceptionOccurred (Runnable sourceRunnable, Thread sourceThread, Throwable threadException);

} // interface ThreadExceptionListener