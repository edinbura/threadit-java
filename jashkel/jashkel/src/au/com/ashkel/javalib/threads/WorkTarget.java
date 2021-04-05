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


/**
 * Class WorkTarget
 */
class WorkTarget
{
  // Attributes
  protected ThreadIt m_ptheWorkTarget;
	int m_theWorkId = 0;

  // Methods

    /**
	   * Method CWorkTarget
	   */
  public WorkTarget ()
  {
    m_ptheWorkTarget = null;
    m_theWorkId = 0;
  }

    /**
	   * Method WorkTarget
  	 */
  WorkTarget (ThreadIt ptheWorkTarget, int theWorkId)
  {
    m_ptheWorkTarget = ptheWorkTarget;
    m_theWorkId = theWorkId;
  }


		/**
		 * Method setWorkTarget
		 */
		 boolean setWorkTarget (ThreadIt ptheWorkTarget, int theWorkId)
		 {
		  boolean isSuccess = false;

		  if ((ptheWorkTarget != null) && (theWorkId > 0) && (ThreadIt.THREADIT_MAX_WORK_METHODS > theWorkId))
		  {
		    m_ptheWorkTarget = ptheWorkTarget;
		    m_theWorkId = theWorkId;
		    isSuccess = true;
		  } // if 
		  return isSuccess;
		 } // setWorkTarget
		 
		 boolean getWorkTarget (ThreadIt ptheWorkTarget, int theWorkId)
		 {
		   boolean isSuccess = false;

		   ptheWorkTarget = null;
		   theWorkId = 0;
		   if ((m_ptheWorkTarget != null) && (m_theWorkId > 0) && (ThreadIt.THREADIT_MAX_WORK_METHODS > m_theWorkId))
		   {
		     ptheWorkTarget = m_ptheWorkTarget;
		     theWorkId = m_theWorkId;
		     isSuccess = true;
		   } // if 
		   return isSuccess;

		 }
} // class WorkTarget

