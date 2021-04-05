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
 * Class EventWorkPackIt represents a work request type that is invoked
 * in response to an event occurrence. 
 */
class EventWorkPackIt extends WorkPackIt
{
  private int m_theEventId = 0;

  public EventWorkPackIt (int theEventId)
  {
    m_isEvent = true;
    m_theEventId = theEventId;
  } // method EventMethodType
  
  public EventWorkPackIt (int theEventId, WorkPackIt theWorkPack)
  {
    super (theWorkPack);
    m_isEvent = true;
    m_theEventId = theEventId;

  } // method EventMethodType

  public int getEventId ()
  {
    return m_theEventId;
  } // getEventId
  
  public Object clone ()
  {
    EventWorkPackIt theClone = new EventWorkPackIt (m_theEventId, (WorkPackIt)super.clone ());
    // Return the results of the copy operation.
    return theClone;
  } // method initialise

} // class EventWorkPackIt
