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

/** Package */
package au.com.ashkel.javalib.threads;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * class ThreadItWorkTargets 
 */
class ThreadItWorkTargets
{
	public class WorkTargetType
	{
		long m_theSelectWorkInstruction;
		long m_theDestWorkInstruction;
		ThreadIt m_ptheDestination;
	} // class WorkTargetType;

	Map<Long, WorkTargetType> m_theWorkTargets;

  public ThreadItWorkTargets ()
	{
	} // constructor CThreadItWorkTargets

	public ThreadItWorkTargets (long theSelectorInstruction, long theDestinationInstruction, ThreadIt ptheDestination)
	{
		WorkTargetType pWorkTarget = new WorkTargetType ();
		pWorkTarget.m_theSelectWorkInstruction = theSelectorInstruction;
		pWorkTarget.m_theDestWorkInstruction = theDestinationInstruction;
		pWorkTarget.m_ptheDestination = ptheDestination;
		m_theWorkTargets.put (new Long (theSelectorInstruction), pWorkTarget);
	} // constructor CThreadItWorkTargets

	public void setWorkTarget (long theSelectorInstruction, long theDestinationInstruction, ThreadIt ptheDestination)
	{
		WorkTargetType pWorkTarget = null;
		
		pWorkTarget =	 m_theWorkTargets.get (new Long (theSelectorInstruction));
		if (pWorkTarget != null)
		{
			pWorkTarget = null;
		} // if 
		pWorkTarget = new WorkTargetType ();
		pWorkTarget.m_theSelectWorkInstruction = theSelectorInstruction;
		pWorkTarget.m_theDestWorkInstruction = theDestinationInstruction;
		pWorkTarget.m_ptheDestination = ptheDestination;
		m_theWorkTargets.put(new Long (theSelectorInstruction), pWorkTarget);
	} // setWorkTarget

	public void setWorkTarget (WorkTargetType theWorkTarget)
	{
		m_theWorkTargets.put(new Long (theWorkTarget.m_theSelectWorkInstruction), theWorkTarget);
	} // setWorkTarget

	public void getWorkTarget (long theSelectorInstruction, long theDestinationInstruction, ThreadIt ptheDestination)
	{
		WorkTargetType pWorkTarget = null;
		
		pWorkTarget =	 m_theWorkTargets.get(new Long (theSelectorInstruction));
		if (pWorkTarget != null)
		{
			theDestinationInstruction = pWorkTarget.m_theDestWorkInstruction;
			ptheDestination = pWorkTarget.m_ptheDestination;
		} // if 
	} // getWorkTarget

	public WorkTargetType getWorkTarget (long theSelectorInstruction)
	{
		WorkTargetType pWorkTarget = null;
		WorkTargetType pWorkTargetResult = null;
		
		pWorkTarget =	 m_theWorkTargets.get(new Long (theSelectorInstruction)); 
		if (pWorkTarget != null)
		{
			pWorkTargetResult = pWorkTarget;
		} // if 
		return pWorkTargetResult;
	} // getWorkTarget

		/**
		 * Method operator+ supports the concatenation of two data buffers.
		 * The data sequence of the second buffer is added to the data
		 * sequence of the first buffer.
		 * theBuffer is the data buffer whose bytes are to be added.
		 * The method returns the combination of the byte sequence in the
		 * two buffers.
		 */
	public ThreadItWorkTargets plus (ThreadItWorkTargets theWorkTargets)
	{
		WorkTargetType aWorkTarget = null;
		ThreadItWorkTargets ptheTargets = new ThreadItWorkTargets ();

		// Copy the bytes in the current buffer to the new buffer.
		if (!m_theWorkTargets.isEmpty ())
		{
		  Iterator<Long> theIter = m_theWorkTargets.keySet().iterator();
			while (theIter.hasNext ())
			{
			  Long aKey = theIter.next ();
				aWorkTarget = m_theWorkTargets.get (aKey);
				if (aWorkTarget != null)
				{
					ptheTargets.setWorkTarget (aWorkTarget);
				} // if 
			} // for
		} // if 
			// Add the bytes from the supplied buffer into the new buffer.
		if (!theWorkTargets.m_theWorkTargets.isEmpty ())
		{
      Iterator<Long> theIter = theWorkTargets.m_theWorkTargets.keySet().iterator();
      while (theIter.hasNext ())
      {
        Long aKey = theIter.next ();
        aWorkTarget = m_theWorkTargets.get (aKey);        
				if (aWorkTarget != null)
				{
					ptheTargets.setWorkTarget (aWorkTarget);
				} // if 
			} // for
		} // if 
			// Return the concatenated work targets.
		return ptheTargets;
	} // operator+

		/**
		 * Method operator= supports the assignment of the specified data
		 * buffer to the target data buffer. All data bytes associated with the
		 * target data buffer will be lost.
		 * theBuffer is the data buffer whose bytes are to be assigned to the
		 * target buffer.
		 */
	public ThreadItWorkTargets setEqualTo (ThreadItWorkTargets theWorkTargets)
		{
			WorkTargetType aWorkTarget = null;

			// Clear all the data from the target buffer.
			m_theWorkTargets.clear ();
			// Copy all the data from the supplied buffer into the target buffer.
			if (!theWorkTargets.m_theWorkTargets.isEmpty ())
			{
	      Iterator<Long> theIter = theWorkTargets.m_theWorkTargets.keySet().iterator();
	      while (theIter.hasNext ())
	      {
	        Long aKey = theIter.next ();
	        aWorkTarget = theWorkTargets.m_theWorkTargets.get (aKey);
					if (aWorkTarget != null)
					{
						setWorkTarget (aWorkTarget);
					} // if 
				} // for
			} // if 
			return this;
		} // operator=

	/**
	 * Method size is called to return the number of bytes currently
	 * contained in the data buffer.
	 */

	public int size ()
	{
		return m_theWorkTargets.size ();
	} // size

	/**
	 * Method reset is called to clear the data buffer of all elements. This allows
	 * the data buffer to be re-used without having to be re-created.
	 */
	public void reset ()
	{
		WorkTargetType pWorkTarget = null;

		if (!m_theWorkTargets.isEmpty ())
		{
      Iterator<Long> theIter = m_theWorkTargets.keySet().iterator();
      while (theIter.hasNext ())
      {
        Long aKey = theIter.next ();
        pWorkTarget = m_theWorkTargets.get (aKey);
				if (pWorkTarget != null)
				{
					pWorkTarget = null;
				} // if 
			} // for
			m_theWorkTargets.clear ();
		} // if 
	} // reset;

} // class ThreadItWorkTargets


