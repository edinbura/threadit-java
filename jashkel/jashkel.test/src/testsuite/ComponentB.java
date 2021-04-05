/*-------------------------------------------------------------------------*/
/* Copyright (C) 2011 by Ashkel Software                                   */
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
package testsuite;

import java.lang.ref.WeakReference;

import au.com.ashkel.interfaces.ISafeThreadItInterface;
import au.com.ashkel.javalib.threads.ThreadIt;
import au.com.ashkel.javalib.threads.ThreadItMessage;

/**
 * class ComponentB represents the interface to implementations of the 
 * Component B.
 * @author Ari Edinburg
 * 18/08/2011
 */
public class ComponentB extends ISafeThreadItInterface
{
  public static final int FUNCTION_C = 0; // Component A has a function C
  public static final int FUNCTION_D = 1; // Component A has a function D
  
  /**
   * Method ComponentA sets the implementation selected to implement the interface
   * as expected by this ComponentB.
   * @param theWorker - the ThreadIt that is the implementation of ComponentA.
   */
  public ComponentB (ThreadIt theWorker)
  {
    super (new WeakReference<ThreadIt>(theWorker), new WeakReference<ThreadIt> (null));
  } // ComponentA

  /**
   * method callFunctionC does the work expected according to specification of FunctionC.
   */
  void callFunctionC ()
  {
    long theWorkId = 0;
    ThreadIt theWorker = null;    
    ThreadItMessage aMsg = null;
        
    // Send a message.
    aMsg = new ThreadItMessage (FUNCTION_C);
    theWorker = getSafeWorker ();
    if (theWorker != null)
    {
      theWorkId = aMsg.sendTo (theWorker);
    } // if 
  } // callFunctionC

  /**
   * method callFunctionD does the work expected according to specification of FunctionD.
   * However, according to this specification there are some additional facilities with this
   * function noted as follows:
   * @param theSender indicates which ThreadIt is sending this request and expects a reply.
   * @param theReplyFunction indicates which function on the implementation that this
   * FunctionD should reply to in doing the work according to FunctionD's specification.
   * In summary, FunctionD will do it's job and then send a reply to the ThreadIt sender
   * calling the particular work method as specified in the theReplyFunction. So to have
   * worked successfully a reply must be sent on the expected funtion at the Sender.
   */
  void callFunctionD (ThreadIt theSender, int theReplyFunction)
  {
    long theWorkId = 0;
    ThreadIt theWorker = null;    
    ThreadItMessage aMsg = null;
        
    // Send a message.
    aMsg = new ThreadItMessage (FUNCTION_D);
    theWorker = getSafeWorker ();
    aMsg.setSourceInfo (theSender, theReplyFunction);
    if (theWorker != null)
    {
      theWorkId = aMsg.sendTo (theWorker);
    } // if 
  } // callFunctionA
  
} // class ComponentA
