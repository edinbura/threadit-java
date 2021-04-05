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
 * Title : ProtectedQueueTyped
 * --------------------------------------------------------------------------
 * Description :
 * Class ProtectedQueue provides support for a queue for objects of type T
 * that is secured from multiple simultaneous access.
 * --------------------------------------------------------------------------
 * Copyright: Copyright (c) 2008
 * Company:   Ashkel Software
 * @author    Ari Edinburg
 * @version   1.0
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

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * Class ProtectedQueue provides insert and remove methods for items in a queue. The queue is protected for access by mulitiple threads.
 */
public class ProtectedQueueTyped<T>
{
  /** Logger for this class */
  private static final Logger m_theLogger = Logger.getLogger (ProtectedQueueTyped.class);

  /** m_Q is the queue of elements. */
  private LinkedBlockingDeque<T>  m_Q = null;

  /**
   * Method ProtectedQueue is the constructor  that initializes the
   * critical section and creates the Semaphore.
   */
  public ProtectedQueueTyped ()
  {
    m_Q = new LinkedBlockingDeque<T> ();
  } // ProtectedQueue

  /**
   * Method insertItem acquires a critical section before adding an item to
   * the tail of the queue. After it leaves the critical section, it
   * releases a semaphore to indicate that an item has been added to the queue.
   * T : The item to be added to the queue.
   */
  public void insertItem (T T)
  {
    m_Q.add (T);
  } // insertItem
  
  /**
   * Method insertPriorityItem acquires a critical section before adding an item to
   * the head of the queue. After it leaves the critical section, it
   * releases a semaphore to indicate that an item has been added to the queue.
   * T : The item to be added to the head of the queue.
   */
  public void insertPriorityItem (T T)
  {
    m_Q.addFirst (T);
  } // insertItem
  

  /**
   * Method waitItem waits for a single object of type T and then enters a critical section
   * where it gets and then removes the first item in the queue if the
   * queue is not empty.<p>
   * The method returns the object of type T retrieved if one is available otherwise
   * a null value is returned if the method times out.<p>
   * WaitTime : The time (in milliseconds) to wait for an object of type T to arrive in the queue.
   */
  public T waitItem (long WaitTime)
  {
    T anItem = null;

    // wait for an item to arrive
    try
     {
	  anItem = m_Q.poll (WaitTime, TimeUnit.MILLISECONDS);
    }
    catch (InterruptedException anInterruptedException)
    {
      m_theLogger.debug ("InterruptedException", anInterruptedException);
    } // catch
    // Return the retrieved item.
    return anItem;
  } // waitItem

  /**
   * Method getItem method enters a critical section where it gets and then
   * removes the first item in the  queue if the queue is not empty. The
   * method does not wait for an item to be placed in an empty queue.
   */
  public T getItem ()
  {
    T anItem = null;

    try
    {
	  anItem = m_Q.take ();
    }
    catch (InterruptedException anInterruptedException)
    {
      m_theLogger.debug ("InterruptedException", anInterruptedException);
    } // catch
    return anItem;
  } // getItem

  /**
   * Method clear enters a critical section before it removes all objects
   * from the queue.
   */
  public void clear()
  {
    m_Q.clear ();
  } // clear

  /**
   * Method size enters a critical section before it returns
   * the number of items in the queue.
   */
  public long size()
  {
    int qSize = m_Q.size();
    return qSize;
  } // size

  /**
   * Method isEmpty enters a critical section before it tests to see if the
   * queue is empty and returns the result.
   */
  public boolean isEmpty()
  {
    boolean bEmpty = m_Q.isEmpty();
    return bEmpty;
  } // isEmpty

  public T get(int i) {
	  
	  Iterator<T> item=m_Q.iterator();
	  int index=0;
	  
	  if(i < m_Q.size()) {
		  
		  
		  while(item.hasNext())
		  {
		    T element = item.next();
			  if(index ==i) return element;
			  index++;
		  }
	  }	
	  
	  return null;
  }
  
  public boolean remove(T o) {
	
	  return m_Q.remove(o);
  }
  
} // class ProtectedQueue