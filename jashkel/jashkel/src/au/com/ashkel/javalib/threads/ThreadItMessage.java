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
 * Title : ThreadItMessage
 * --------------------------------------------------------------------------
 * Description :
 * Class ThreadItMessage is the base class used to define messages that
 * can be sent onto work performers using the WorkPackIt and ThreadIt work
 * model. The messages define the work to be peformed and need to be
 * extended for use use in the particular application.
 * Comment: The class needs additional work to provide a command pattern.
 * --------------------------------------------------------------------------
 * Copyright: Copyright (c) 2008
 * Company:   Ashkel Software
 * @author Ari Edinburg
 * @version 1.0
 * --------------------------------------------------------------------------
 * Associated Documentation :
 * Java Sun JDK 1.6.0_02
 * --------------------------------------------------------------------------
 * Development History
 * --------------------------------------------------------------------------
 * @version    : 0.00
 * Date        : 10th December 2001
 * Author      : A Edinburg, Ashkel Software
 * Description : Start of Development
 * --------------------------------------------------------------------------
 * version     : 1.00
 * Date        : 5th January 2002
 * Author      : A Edinburg, Ashkel Software
 * Description : First release.
 * --------------------------------------------------------------------------
 */

/** Package */
package au.com.ashkel.javalib.threads;

import java.lang.ref.WeakReference;

/**
 * Class ThreadItMessage is the base class of messages sent to work performers
 * indicating the task to be performed with all the information needed to
 * perform the task and the way in which the results are to be returned when
 * the task is completed.
 */
public class ThreadItMessage
{
  /**
   * _theInstruction is the work instruction that this message
   * represents.
   */
  protected int _theInstruction = 0;
  /**
   * _theCallback is the instance used to effect the callback to the
   * client when the work is completed.
   */
  WorkDoneCallback _theCallback = null;
  /**
   * _aNotification indicates if the client requires notification of
   * the task completion.
   */
  protected boolean _aNotification = false;
  /**
   * _theWorkPack is the WorkPackIt that specifies the task to be done.
   */
  protected WorkPackIt  _theWorkPack = null;
  /**
   * m_ptheSource specifies the source of the message if it is of the CThreadIt type.
   */
  ThreadIt m_ptheSource = null;
  /**
   * m_ptheSource specifies the source of the message if it is of the CThreadIt type.
   */
  WeakReference<ThreadIt> m_wptheSource = null;
  /**
   * m_theReplyInstruction specifies the instruction to use when replying to this 
   * m_ptheSource.
   */
  int m_theReplyInstruction = 0;


  /**
   * Method ThreadItMessage is the constructor for the class. The method
   * associates the work instruction with the message.<p>
   * theInstruction : is the identity of the work to be performed when this
   *                  message is received.
   */
  public ThreadItMessage (int theInstruction)
  {
    initialise ();
    // Initialise the parameters to the default values.
    _theInstruction = theInstruction;
    _theCallback   =  null;
    _aNotification = false;
    m_ptheSource = null;
    m_theReplyInstruction = 0;
  } // ThreadItMessage

  /**
   * Method ThreadItMessage is the constructor for the class. The method
   * associates the work instruction with the message and specifies the
   * way in which the client is informed that the work has been completed.<p>
   * theInstruction : is the identity of the work to be performed when this
   *                  message is received.<p>
   * theWorkDoneCallback : is the instance to use to inform the client that the
   *                       work has been completed.
   */
  public ThreadItMessage (int theInstruction, WorkDoneCallback theWorkDoneCallback)
  {
    initialise ();
    // Initialise the parameters to the default values.
    _theInstruction = theInstruction;
    _theCallback = theWorkDoneCallback;
    _aNotification = (_theCallback != null);
    m_ptheSource = null;
    m_theReplyInstruction = 0;
  } // ThreadItMessage

  ThreadItMessage (ThreadIt ptheSource)
  {
    initialise ();
    // Initialise the parameters to the default values.
    _theInstruction = 0;
    _theCallback   =  null;
    _aNotification = false;
    m_ptheSource = ptheSource;
    m_theReplyInstruction = 0;
  } // ThreadItMessage


  /**
   * Method initialise is called to perform general initialisation for the
   * instance and is used by the class constructors.
   */
  private void initialise ()
  {
    _theInstruction = 0;
    _theCallback =  null;
    _aNotification = false;
    m_ptheSource = null;
    m_theReplyInstruction = 0;
    _theWorkPack = new WorkPackIt ();
  } // initialise

  /**
   * Method sendTo is called to send the message onto the work performer
   * which is the destination of the message. The message destination
   * must implement the startWork method.<p>
   * theMessageDestination : is the instance that will perform the work
   *                         in response to this message.
   * NOTE: This message will be setup to return a reply.                          
   */
  public long sendTo (ThreadIt theMessageDestination)
  {
    boolean     aSuccess = false;
    long        aPackId = -1;

    // Send a message to start the work.
    _theWorkPack.m_Instruction        = _theInstruction;
    // The work package object is not changed in the event that its value
    // is set.
    _theWorkPack.m_Object       = null;
    _theWorkPack.m_NotifyWithCallback = _aNotification;
    _theWorkPack.m_SendResult = true;
    _theWorkPack.m_ptheSource = m_ptheSource;
    _theWorkPack.m_wptheSource = m_wptheSource;
    _theWorkPack.m_Callback   = _theCallback;
    _theWorkPack.m_theReplyInstructionId = m_theReplyInstruction;
    // Start doing the work.
    if (theMessageDestination != null)
    {
      aPackId = theMessageDestination.startWork (_theWorkPack);
      aSuccess = true;
    } // if
    // Return the method status.
    return aPackId;
  } // sendTo

  //  NOTE: This message will be setup not to return a reply.
  public long sendWithNoReplyTo (ThreadIt theMessageDestination)
  {
    boolean aSuccess = false;
    long aPackId = -1;

    // Send a message to start the work.
    _theWorkPack.m_Instruction  = _theInstruction;
    _theWorkPack.m_SendResult   = false;
    // The work package object is not changed in the event that its value is set.
    //_theWorkPack.m_Object       = null;
    _theWorkPack.m_NotifyWithCallback = false;
    _theWorkPack.m_ptheSource = m_ptheSource;
    _theWorkPack.m_wptheSource = m_wptheSource;
    _theWorkPack.m_theReplyInstructionId = m_theReplyInstruction;
    //_theWorkPack.m_Callback = null;
    // Start doing the work.
    if (theMessageDestination != null)
    {
      aPackId = theMessageDestination.startWork (_theWorkPack);
    } // if
    // Return the method status.
    return aPackId;
  } // sendTo
  /**
   * Method sendTo is called to send the message onto the work performer
   * which is the destination of the message. The message destination
   * must implement the startWork method. This method allows the
   * work instruction to be specified when sending the message.<p>
   * theMessageDestination : is the instance that will perform the work
   *                         in response to this message.<p>
   * theInstruction : is the work instruction to be assocated with
   *                  this message.
   */
  public long sendTo (ThreadIt theMessageDestination, int theInstruction)
  {
    boolean    aSuccess = false;
    long       aPackId = -1;

    // Send a message to start the work.
    _theInstruction = theInstruction;
    _theWorkPack.m_Instruction  = theInstruction;
    _theWorkPack.m_SendResult   = true;
    // The work package object is not changed in the event that its value
    // is set.
    //_theWorkPack.m_Object       = null;
    _theWorkPack.m_NotifyWithCallback = _aNotification;
    _theWorkPack.m_ptheSource = m_ptheSource;
    _theWorkPack.m_wptheSource = m_wptheSource;
    _theWorkPack.m_theReplyInstructionId = m_theReplyInstruction;
    _theWorkPack.m_Callback = _theCallback;
    // Start doing the work.
    if (theMessageDestination != null)
    {
      aPackId = theMessageDestination.startWork (_theWorkPack);
      aSuccess = true;
    } // if
    // Return the method status.
    return aPackId;
  } // sendTo

  /**
   * Method sendTo is called to send the message onto the work performer
   * which is the destination of the message. The message destination
   * must implement the startWork method. This method allows the
   * work instruction to be specified when sending the message.<p>
   * theMessageDestination : is the instance that will perform the work
   *                         in response to this message.<p>
   * theInstruction : is the work instruction to be assocated with
   *                  this message.
   */
  public long sendWithNoReplyTo (ThreadIt theMessageDestination, int theInstruction)
  {
    boolean    aSuccess = false;
    long       aPackId = -1;

    // Send a message to start the work.
    _theInstruction = theInstruction;
    _theWorkPack.m_Instruction  = theInstruction;
    _theWorkPack.m_SendResult   = false;
    // The work package object is not changed in the event that its value is set.
    //_theWorkPack.m_Object       = null;
    _theWorkPack.m_NotifyWithCallback = false;
    _theWorkPack.m_ptheSource = m_ptheSource;
    _theWorkPack.m_wptheSource = m_wptheSource;
    _theWorkPack.m_theReplyInstructionId = m_theReplyInstruction;
    _theWorkPack.m_Callback = null;
    // Start doing the work.
    if (theMessageDestination != null)
    {
      aPackId = theMessageDestination.startWork (_theWorkPack);
      aSuccess = true;
    } // if
    // Return the method status.
    return aPackId;
  } // sendTo

  /**
   * Method getWork returns the WorkPackIt associated with this message.
   */
  public WorkPackIt getWork ()
  {
    return _theWorkPack;
  } // getWork

  /**
   * Method setTheCallback is used to set the instance used to inform that the
   * work has been completed in response to the message. For this to work,
   * the method must be called prior to the sendTo request being invoked.<p>
   * theWorkDoneCallback : is the instance that implements the WorkDoneCallback
   *                       interface that will be invoked to inform of the
   *                       work execution completion.
   */
  public void setTheCallback (WorkDoneCallback theWorkDoneCallback)
  {
    _theCallback = theWorkDoneCallback;
    _aNotification = (_theCallback != null);
  } // setTheCallback

  /**
   * Method setTheInstruction is called to set or to modify the work
   * instruction associated with this message. The invocation will only
   * affect the next call to sendTo.<p>
   * theInstruction: is the work instruction to be associated with this message.
   */
  public void setTheInstruction (int theInstruction)
  {
    _theInstruction = theInstruction;
  } // setTheInstruction


  /**
   * Method setSourceInfo is called to set the source information 
   * in terms of the originating CThreadIt instance and also the work instruction
   * that can be used to reply to this message. 
   * theInstruction: is the work instruction to be associated with this message.
   */
  public void setSourceInfo (ThreadIt ptheSource, int theReplyInstruction)
  {
    m_ptheSource = ptheSource;
    m_theReplyInstruction = theReplyInstruction;
  } // setTheInstruction

  /**
   * Method setSourceInfo is called to set the source information 
   * in terms of the originating CThreadIt instance and also the work instruction
   * that can be used to reply to this message. 
   * theInstruction: is the work instruction to be associated with this message.
   */
  public void setSourceInfo (WeakReference<ThreadIt> ptheSource, int theReplyInstruction)
  {
    m_wptheSource = ptheSource;
    m_theReplyInstruction = theReplyInstruction;
  } // setTheInstruction

} // class ThreadItMessage