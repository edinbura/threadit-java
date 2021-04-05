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
 * Title: ThreadItObserver
 * Description: class CThreadItObserver is the virtual base class for a class that wishes to receive 
 * notifications when information of interest updates or changes. This is an 
 * implementation of the Observer pattern and allows a class to be notified of 
 * change asynchronously. Classes declare an interest by becoming observers of a subject. 
 * When data related to the subject changes the class method is invoked to notify 
 * the observers of the change or update. See class CSubject as the partner of the 
 * CThreadItObserver class in the implementation of this observer pattern.
 *
 * Copyright: Copyright (c) 2008 Ashkel Software 
 * @author Ari Edinburg
 * @version 1.0
 * $Revision: 1.1 $<br>
 * $Date: 2006/12/19 02:38:06 $
 */

/** Package */
package au.com.ashkel.javalib.threads;

import java.lang.ref.WeakReference;


/**
 * Class CThreadItObserver is used as the base class and implements the two virtual
 * methods in order to be notified when data/information of interest is
 * being observed. The inheriting class registers its interest by 
 * by attaching to a subject. When the subject or information relating to
 * the subject changes the onUpdate or onChange methods are invoked asynchronously.
 */
class ThreadItObserver
{
	// Protected member variables
	
		/** m_theWorkInstruction is the work instruction at the target. */
  protected int m_theWorkInstruction;
		/** m_wptheThreadIt is the target for the notification. */ 
  protected WeakReference<ThreadIt> m_wptheThreadIt;
		/** m_wptheSource is the source - the initiator of the notification. */ 
  protected WeakReference<ThreadIt> m_wptheSource;

		/** 
  	 * Method CThreadItObserver is the constructor for the instance. This is an empty
	   * implementation.
		 */
	  public ThreadItObserver ()
	  {
	    m_theWorkInstruction = 0;
	  } // constructor ThreadItObserver
		/** 
  	 * Method CThreadItObserver is the constructor for the instance. 
		 * ptheSource specifies the source of the notification. If this is set then
		 * the work request to the target will have this information attached. 
		 */
		public ThreadItObserver (ThreadIt ptheSource)
		{
		  m_wptheSource = new WeakReference (ptheSource);		  
		}
		/** 
		 * Method CThreadItObserver is the constructor for the instance. 
		 * ptheThreadIt is the target CThreadIt 
		 * theWorkInstruction is the work instruction at the target CThreadIt
		 */
		public ThreadItObserver (ThreadIt ptheThreadIt, int theWorkInstruction)
		{
		  m_theWorkInstruction = theWorkInstruction;
		  m_wptheThreadIt = new WeakReference (ptheThreadIt);;
		}
		/** 
		 * Method CThreadItObserver is the constructor for the instance. 
		 * ptheSource specifies the source of the notification. If this is set then
		 * the work request to the target will have this information attached. 
		 * ptheThreadIt is the target CThreadIt 
		 * theWorkInstruction is the work instruction at the target CThreadIt
		 */
		public ThreadItObserver (ThreadIt ptheSource, ThreadIt ptheThreadIt, int theWorkInstruction)
		{
		  m_theWorkInstruction = theWorkInstruction;
      m_wptheThreadIt = new WeakReference (ptheThreadIt);;
      m_wptheSource = new WeakReference (ptheSource);     
		}

		
	// Methods
		/**
			// Do something about loosely held memory. This should be a shared pointer or weakref.
		 */
	  public boolean getTarget (ThreadIt ptheThreadIt, int theWorkInstruction)
	  {

	    boolean isGood = false;

	    theWorkInstruction = m_theWorkInstruction;
	    ptheThreadIt = m_wptheThreadIt.get ();
	    isGood = (ptheThreadIt != null);
	    return isGood;

	  }// getTarget
	  
  
	  public boolean isEqual (ThreadItObserver theOther)
	  {
	    boolean isEqual = false;


	    ThreadIt sptheThreadIt = m_wptheThreadIt.get ();
	    ThreadIt sptheThreadItOther = theOther.m_wptheThreadIt.get ();
  if ((sptheThreadIt != null) && (sptheThreadItOther != null))
  {
    if (sptheThreadIt == sptheThreadItOther)
    {
      isEqual = true;
    } // if 
  } // if 
  return isEqual;
	  }// isEqual

	  
		public ThreadIt getTarget ()
		{
		  return m_wptheThreadIt.get ();		  
		}
		
		public boolean doNotify ()
    {
		  boolean isSuccess = false;

		  ThreadIt sptheThreadIt = m_wptheThreadIt.get ();
		  if (sptheThreadIt != null)
		  {
		    ThreadItMessage aWorkMessage = new ThreadItMessage (m_theWorkInstruction);
		    aWorkMessage.setSourceInfo(m_wptheSource, 0);
		    aWorkMessage.sendWithNoReplyTo (sptheThreadIt);
		    isSuccess = true;
		  } // if 
		  return isSuccess;

    }

		public boolean notify (Cloneable ptheObject)
    {
		  boolean isSuccess = false;

		  ThreadIt sptheThreadIt = m_wptheThreadIt.get ();
		  if (sptheThreadIt != null)
		  {
		    ThreadItMessage aWorkMessage = new ThreadItMessage (m_theWorkInstruction);
		    aWorkMessage.getWork ().m_ptheObject = null; //ptheObject.clone ();
		    aWorkMessage.setSourceInfo(m_wptheSource, 0);
		    aWorkMessage.sendWithNoReplyTo (sptheThreadIt);
		    isSuccess = true;
		  } // if 
		  return isSuccess;
      
    }
		
		public boolean notify (int theWorkInstruction)
    {
		  boolean notify = false;

		  ThreadIt sptheThreadIt = m_wptheThreadIt.get ();
		  if (sptheThreadIt != null)
		  {
		    ThreadItMessage aWorkMessage = new ThreadItMessage (theWorkInstruction);
		    aWorkMessage.setSourceInfo(m_wptheSource, 0);
		    aWorkMessage.sendWithNoReplyTo (sptheThreadIt);
		    notify = true;
		  } // if 
		  return notify;      
    }
		
		public boolean notify (Cloneable ptheObject, int theWorkInstruction)
    {
		  boolean notify = false;

		  ThreadIt sptheThreadIt = m_wptheThreadIt.get ();
		  if (sptheThreadIt != null)
		  {
		    ThreadItMessage aWorkMessage = new ThreadItMessage (theWorkInstruction);
		    aWorkMessage.getWork ().m_ptheObject = null; // ptheObject.clone ();
		    aWorkMessage.setSourceInfo(m_wptheSource, 0);
		    aWorkMessage.sendWithNoReplyTo (sptheThreadIt);
		    notify = true;
		  } // if 
		  return notify;
      
    }
		
		public boolean isValid () //  returns if the reference held is good.
    {
		  boolean isGood = false;
		  ThreadIt thePtr = null;

		  thePtr = m_wptheThreadIt.get (); 
		  if (thePtr != null)
		  {
		    isGood = true;
		  } // if 
		  return isGood;
      
    }
		

} // class ThreadItObserver
