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
 * Title: TestResult
 * Description: TestResult is the test operation result status.
 *
 * Copyright:    Copyright (c) 2010
 * Company:      Ashkel Software
 * @version 1.0
 */
public class TestResult
{
	// Attributes

	private boolean m_isSuccess = false;  // Is this a successful test result. m_isReady;	
	private String m_theReason = null; // the reason for the success or failure. 
	private String m_theOperationName = null;	// The operation for which this result applies. 
	private int m_theRequestId = 0;
	private int m_theResultId = 0; // the result of what. 
	private boolean m_isResult = false;


	// Methods
	public TestResult () {};

	/**
	 * retrieves the name of the requested operation to which this status object applies
	 */
	public String getOperationName () 
	{
		return m_theOperationName;
	} // getOperationName

	/**
	 * sets the name of the operation that is being requested
	 */
	public void setOperationName (String theOpName)
	{
		m_theOperationName = theOpName;
	} // setOperationName

	public void setSuccess (boolean isSuccess)
	{
		m_isSuccess = isSuccess;
	} 
	public boolean isSuccess ()
	{
		return m_isSuccess;
	} // 

	public void setResultId (int theResultId)
	{
		m_theResultId = theResultId;
	} // setResultId

	public int getResultId ()
	{
		return m_theResultId;
	} // 

	public void setRequestId (int theRequestId)
	{
		m_theRequestId = theRequestId;
	} // setRequestId

	public int getRequestId ()
	{
		return m_theRequestId;
	} // getRequestId

	public String getReason ()
	{
		return m_theReason;
	} //

	public void setReason (String theReason)
	{
		m_isResult = true;
		m_theReason = theReason;
	} // setReason

	public void setResult ()
	{
		m_isResult = true;
	} // setReason

	public boolean isResult ()
	{
		return m_isResult;
	} // 

	public String toString ()
	{
		String theMsg;

		theMsg = "Test Result = \n";
		theMsg += "Result Id : " + m_theResultId + " \n";	
		theMsg += "Status    : " + m_isSuccess + "\n";	
		theMsg += "Operation : " + m_theOperationName + "\n";
		theMsg += "Status    : " + m_theReason;
		return theMsg;
	} // toString

} // class CResultStatus

