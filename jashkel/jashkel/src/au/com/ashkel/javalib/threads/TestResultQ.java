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

/*--------------------------------------------------------------------------*/
/* Package declaration.                                                     */
/*--------------------------------------------------------------------------*/
package au.com.ashkel.javalib.threads;

/**
 * Title: TestResultQ
 * Description: TestResultQ is the test operation result status queue.
 *
 *
 * Copyright:    Copyright (c) 2010
 * Company:      Ashkel Software
 * @version 1.0
 */
public class TestResultQ
{
	// Attributes
	//protected	ProtectedQueue<TestResultPtr> m_theResQ;
	protected ProtectedQueue m_theResQ = new ProtectedQueue ();  
	// Methods
	public TestResultQ () {};

	public void getRestultQ (ProtectedQueue theResQ)
	{
		theResQ = m_theResQ;
	} // 

	public void publish (TestResult ptheResult)
	{
		m_theResQ.insertItem (ptheResult);
	} // publish

	public void publish (int theResultId, String theOpName, boolean isOk, String theMsg)
	{
		TestResult ptheResult = new TestResult ();
		ptheResult.setResultId (theResultId);
		ptheResult.setOperationName (theOpName);
		ptheResult.setSuccess (isOk);
		ptheResult.setReason (theMsg);
		m_theResQ.insertItem (ptheResult);
	} // publish

	void publish (int theResultId, int theRequestId, String theOpName, boolean isOk, String theMsg)
	{
		TestResult ptheResult = new TestResult ();
		ptheResult.setResultId (theResultId);
		ptheResult.setRequestId (theRequestId);
		ptheResult.setOperationName (theOpName);
		ptheResult.setSuccess (isOk);
		ptheResult.setReason (theMsg);
		m_theResQ.insertItem (ptheResult);
	} // publish


	// if the id is zero return anything that comes in. Waits for that result type
	// until time out.
	public TestResult getResult (int theId, long theWaitTime)
	{
		boolean isWaiting = true;
		long theTimeLeft = 0;
		LongHolder aPeriod = new LongHolder ();
		TimeIt theTimer = new TimeIt ();
		TestResult theResult = null;

		for(int i=0;i<m_theResQ.size();i++) {
			
			Object data= m_theResQ.get(i);
			
			
			if(data instanceof TestResult ) {
				
				TestResult item=(TestResult) data;
			
				if(item.getResultId() == theId) {
			
					m_theResQ.remove(item);
					return item;
				}
			}
		}
		
		theTimer.StartTiming (theWaitTime);
		do
		{
			if (theTimer.timeElapsed (aPeriod)) // still timing
			{ 
				theTimeLeft = theWaitTime - aPeriod.m_theValue; // how long left
				if (theTimeLeft > 20) // we should wait.
				{
					theResult = (TestResult)m_theResQ.waitItem (theTimeLeft);
					if (theResult != null)
					{
						theResult.setResult ();
						if (theId == 0)
						{
							isWaiting = false;
						}
						else
						{
							if (theResult.getResultId () == theId)
							{
								isWaiting = false;
							}
							else
							{
								// This is not the expected result.
								theResult = null;
							} // if 
						} // if 
					}
					else
					{
						// We have expired. 
						isWaiting = false;
						theResult = new TestResult ();
						theResult.setReason("timed out waiting for result");
					} // 
				}
				else
				{
					// We have expired. 
					isWaiting = false;
					theResult = new TestResult ();
					theResult.setReason("timed out waiting for result");
				} // if 
			}
			else
			{
				// We have expired. 
				isWaiting = false;
				theResult = new TestResult ();
				theResult.setReason("timed out waiting for result");
			} // if 
		} while (isWaiting);
		assert (theResult != null) : "the test result should not be null";
		return theResult;
	} // getResult

	// if the id is zero return anything that comes in.
	public TestResult getSuccessResult (int theId, long theLoops, long theLoopWaitTime)
	{
		long theCount = 0;
		boolean isResult = false;
		TestResult theResult = null;      
		do
		{
			theResult = getResult (theId, theLoopWaitTime);
			if ((theResult != null) && (theResult.isSuccess ()))
			{
				isResult = true;
				theResult.setResult ();
			} // if 
		} while ((theCount < theLoops) && (!isResult));
		if (theResult == null)
		{
			theResult = new TestResult ();			  
		} // if 
		assert (theResult != null) : "the test result should not be null";			
		return theResult;
	} // getSuccessResult

} // class CResultStatus

