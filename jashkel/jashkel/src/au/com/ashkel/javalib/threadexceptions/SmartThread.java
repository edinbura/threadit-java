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
 * Title:        SmartThread
 * Description:  SmartThread is an extension of ThreadGroup that provides
 *               support for properly handling exceptions that occur
 *               on secondary threads. SmarthThread forms part of a framework
 *               supporting the production of robust code in terms of dealing
 *               with error situations in multi-threaded java programs.
 *
 * Class SmartThread wraps the thread class to allow exceptions that are
 * thrown from its run() method to be caught and passed to a listener object.
 * The wrapper actually is a subclass of (is-a) ThreadGroup, because we have
 * to override its uncaughtException() method. To achieve thread functionality,
 * the wrapper aggregates (has-a)Thread. This wrapper version is designed to
 * handle multiple asynchronous secondary threads. The main thread must
 * implement the ThreadExceptionListener interface. Any exception intercepted
 * in any thread is routed back to the calling code through the
 * exceptionOccurred() method (defined in the ThreadExceptionListener
 * interface).

 * Copyright:    Copyright (c) 2008
 * Company:      Ashkel Software
 * @author based on code examples in Java Report, August 1998
 * @version 1.0
 */
package au.com.ashkel.javalib.threadexceptions;

import java.lang.ThreadGroup;
import java.lang.Throwable;
import java.util.Vector;

/**
 * class SmartThread
 * Class SmartThread is part of framework for handling exceptions that occur
 * on secondary threads.
 */
public class SmartThread extends ThreadGroup
{
private Runnable m_Runnable;
// m_Runnable is the method that executes in it's own thread.
private Thread   m_Thread;
// m_Thread is the thread that is responsible for the m_Runnable.
private Vector   m_ExceptionListenerList;
// m_ExceptionListenerList is the list of listeners that will be notified
// when an exception occurs on the associated thread.

/**
 * Method SmartThread
 * Method SmartThread associates an object implementing interface Runnable
 * with a thread so that the object's run method can be called in the
 * seperately executing thread. The thread is given a description so that
 * it can be recognised if there is an exception.
 * @param runnable is object that is to run within a thread.
 * @param name     is the text name that easily identifies the thread instance.
 */
public SmartThread (Runnable runnable, String name)
{
  super("SmartThread thread group");
  // Create an initial list with two entries and incrementing in size
  // by two every time.
  m_ExceptionListenerList = new Vector(2,2);
  m_Runnable = runnable;
  // Create this thread as a member in our thread group.
  m_Thread = new Thread (this, runnable, name);
} // SmartThread

/**
 * Method addThreadExceptionListener
 * Method addThreadExceptionListener adds a listener to the list of listeners
 * on exceptions that occur within the thread associated with the SmartThread
 * instance.
 * @param exceptionListener is the object that needs to be notified when the
 * thread associated with the SmartThread instance has an unhandled exception
 * condition.
 */
public void addThreadExceptionListener (ThreadExceptionListener exceptionListener)
{
  m_ExceptionListenerList.addElement(exceptionListener);
} // addThreadExceptionListener

/**
 * Method getThread
 * Method getThread returns the thread associated with the SmartThread
 * instance.
 */
public Thread getThread()
{
  return m_Thread;
} // getThread

/**
 * Method start
 * Method start is called to start execution of the thread associated with
 * the SmartThread instance.
 */
public void start()
{
  m_Thread.start();
} // start

/**
 * Method uncaughtException
 * Method uncaughtException overrides the ThreadGroup method and notifies
 * each of the exception listeners that an exception within the thread
 * associated with the SmartThread instance has occurred.
 * @param sourceThread is the thread that has the exception condition that
 * has not been successfully handled.
 * @param threadException is the exception description.
 *
 * The runnable instance, the source thread and the exception information
 * associated with the thread that has the uncaught exception condition are
 * sent on to each of the exception listeners.
 */
public void uncaughtException (Thread sourceThread, Throwable threadException)
{
  int numElem = m_ExceptionListenerList.size();
  for (int i = 0; i < numElem; i++)
  {
    ThreadExceptionListener listener = ((ThreadExceptionListener)m_ExceptionListenerList.elementAt(i));
    listener.exceptionOccurred (m_Runnable, sourceThread, threadException);
  } // for
} // uncaughtException

} // class SmartThread