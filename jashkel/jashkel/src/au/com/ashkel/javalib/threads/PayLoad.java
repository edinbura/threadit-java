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

public class PayLoad
{
  protected Object m_theData = null;
  public WorkPackIt m_theWorkDone = null;
  public Integer m_theStatus = null;
  public boolean m_isGoodPayLoad = false;

  public boolean isOk ()
  {
    return m_isGoodPayLoad;
  } // isOk

  public boolean hasData ()
  {
    return (m_theData != null);
  } // hasData
  
  public void setData (Object theData)
  {
    m_theData = theData;
  } // setData
  public Object getData ()
  {
    return m_theData;
  } // getData
  
  public WorkPackIt getWorkDone ()
  {
    return m_theWorkDone; 
  }
  
  public Integer getStatus ()
  {
    return m_theStatus;
  } // getStatus;
  
} // PayLoad
