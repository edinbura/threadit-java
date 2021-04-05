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
 * Title : WorkPackIt
 * --------------------------------------------------------------------------
 * Description :
 * Class WorkPackIt is a description of work to be performed, the
 * constraints under which the work is to be performed and the result
 * of the work performed. The WorkPackIt defines the unit of work to be
 * done as part of the thread of execution. When the work has been done,
 * the results are placed back into a WorkPackIt and returned as a
 * response.
 * --------------------------------------------------------------------------
 * Copyright: Copyright (c) 2008
 * Company:   Ashkel Software
 * @author Ari Edinburg
 * @version 1.0
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

import java.lang.ref.WeakReference;

import au.com.ashkel.javalib.threads.ProtectedQueue;

/**
 * Class WorkPackIt describes work to be executed - a work package. The WorkPackIt defines the type work to be done in terms of the work instruction. The instances need to perform the work are provided. Once the work has been done an instance of the WorkPackIt is used to return the output back in response.
 */
public class WorkPackIt implements Cloneable
{
  /**
   * m_Instruction is the instruction of work to perform. The value starts
   * from one onwards. A value of zero implies no work to be performed.
   * The instruction is assigned to a method that performs work that
   * corresponds to the instruction.
   */
  public int m_Instruction;
  /**
   * m_WorkPackID is a unique value assigned to the work packet instance
   * once it is placed in the queue of work to be done. The originator of the
   * WorkPackIt can then use this number to track the progress of the work
   * or where the response to the original work request is.
   */
  public long  m_WorkPackID;
  /**
   * m_Callback is the reference to the instance that is notified when
   * the work specified by a WorkPackIt has been performed. The instance
   * referenced implements the WorkDoneCallback interface.
   */
  public WorkDoneCallback m_Callback;
  /**
   * m_NotifyWithCallback is set to true if notification of work completion
   * is required. If m_NotifyWithCallback is true, then the m_Callback
   * reference is informed of the work completion.
   */
  public boolean m_NotifyWithCallback;
  /**
   * m_SendResult is set to true if a WorkPackIt object is to be returned
   * in the work done queue after the work is completed. A thread waiting
   * on this queue will be signalled when the WorkPackIt object is placed
   * in the queue.
   */
  public boolean m_SendResult;
  /**
   * m_UseDefaultQ is set to true if the in-built work done queue is used
   * to return the Work Done results. This value is set to true by default.
   */
  public boolean m_UseDefaultQ;
  /**
   * m_WorkDoneQ receives work done packages for return to the initiator
   * of the work request if the value of this member is not null and
   * m_UseDefaultQ is false.
   */
  public ProtectedQueue m_WorkDoneQ;
  /**
   * m_Object is a general reference for passing information to the
   * method that will perform the work. The method will interpret
   * what has been provided.
   */
  public Object m_Object;
  /**
   * m_TimeAllowed indicates the time allowed to perform the work package.
   * If the work package cannot be done in this time period, then the
   * method must stop execution as soon as it recognises that the
   * time allowed period expired and return the status WORKDONE_TIME_OUT
   * as part of the WorkPackIt response. A time value of zero implies
   * no time restriction on the processing. The value is in milliseconds and
   * the value selected should be based on a resolution of approx 10
   * milliseconds.
   */
  public long m_TimeAllowed;
  /**
   * m_TimeElapsed is the time take for the work to complete. It is monitored
   * automatically. The value returned is in milliseconds.
   */
  public long m_TimeElapsed;
  /**
   * m_Status returns the operation status of the work performed.
   */
  public long m_Status;
  public WeakReference<ThreadIt> m_wptheSource;
  public Object m_ptheObject;
  public ThreadIt m_ptheSource;
  public int m_theReplyInstructionId;
  protected boolean m_isEvent = false;

  /**
   * Method WorkPackIt is the constructor for the class. The method sets the
   * initial values for the member variables.
   */
  public WorkPackIt ()
  {
    initialise ();
  } // method WorkPackIt
  
  /**
   * Method WorkPackIt is the constructor for the class. The method sets the
   * initial values for the member variables.
   */
  public WorkPackIt (WorkPackIt theOriginal)
  {
    m_Instruction  = theOriginal.m_Instruction;
    m_WorkPackID   = theOriginal.m_WorkPackID;
    m_Callback     = theOriginal.m_Callback;
    m_SendResult   = theOriginal.m_SendResult;
    m_UseDefaultQ  = theOriginal.m_UseDefaultQ;
    m_WorkDoneQ    = theOriginal.m_WorkDoneQ;
    m_Object       = theOriginal.m_Object;
    m_TimeAllowed  = theOriginal.m_TimeAllowed;
    m_TimeElapsed  = theOriginal.m_TimeElapsed;
    m_Status       = theOriginal.m_Status;
    m_NotifyWithCallback = theOriginal.m_NotifyWithCallback;
    
    m_wptheSource = theOriginal.m_wptheSource;
    m_ptheObject = theOriginal.m_ptheObject;
    m_ptheSource = theOriginal.m_ptheSource;
    m_theReplyInstructionId = theOriginal.m_theReplyInstructionId;
    m_isEvent = theOriginal.m_isEvent;
  } // method WorkPackIt

  /**
   * Method Initialise clears the work packet information and sets the
   * values up to the defaults.
   */
  public void initialise ()
  {
    m_Instruction  = 0;
    m_WorkPackID   = 0;
    m_Callback     = null;
    m_SendResult   = false;
    m_UseDefaultQ  = true;
    m_WorkDoneQ    = null;
    m_Object       = null;
    m_TimeAllowed  = 0;
    m_TimeElapsed  = 0;
    m_Status       = 0;
    m_NotifyWithCallback = false;
    
    m_wptheSource = null;
    m_ptheObject = null;
    m_ptheSource = null;
    m_theReplyInstructionId = 0;
    m_isEvent = false;
  } // method initialise

  /**
   * Method clone creates a copy of the instance. This results in a
   * new instance with a field by field copy of the original. The
   * method returns the new instance.
   */
  public Object clone ()
  {
    WorkPackIt theClone = new WorkPackIt ();
    theClone.m_Instruction        = m_Instruction;
    theClone.m_WorkPackID         = m_WorkPackID;
    theClone.m_Callback           = m_Callback;
    theClone.m_SendResult         = m_SendResult;
    theClone.m_UseDefaultQ        = m_UseDefaultQ;
    theClone.m_WorkDoneQ          = m_WorkDoneQ;
    theClone.m_Object             = m_Object;
    theClone.m_TimeAllowed        = m_TimeAllowed;
    theClone.m_TimeElapsed        = m_TimeElapsed;
    theClone.m_Status             = m_Status;
    theClone.m_NotifyWithCallback = m_NotifyWithCallback;
    
    theClone.m_wptheSource = m_wptheSource ;
    theClone.m_ptheObject = m_ptheObject;
    theClone.m_ptheSource = m_ptheSource;
    theClone.m_theReplyInstructionId  = m_theReplyInstructionId;
    theClone.m_isEvent = m_isEvent;
    // Return the results of the copy operation.
    return theClone;
  } // method initialise

  /**
   * Method getWorkInstruction returns the work instruction associated with the
   * WorkPackIt instance. A work instruction value of zero is not a valid
   * work instruction. In this case the WorkPackIt request is ignored.
   */
  public int getWorkInstruction ()
  {
    return (m_Instruction);
  } // method getWorkInstruction

  /**
   * Method setWorkInstruction sets the work instruction associated with the
   * WorkPackIt instance. A work instruction value of zero is not a valid
   * work instruction.
   */
  public void setWorkInstruction (int theInstruction)
  {
    m_Instruction = 0;
    if (theInstruction > 0)
    {
      m_Instruction = theInstruction;
    } // if
  } // method setWorkInstruction
  
  public boolean isEvent ()
  {
    return m_isEvent;
  } // isEvent
  
  public boolean hasObj ()
  {
    return (m_Object != null);
  } // hasObj

} // class WorkPackIt