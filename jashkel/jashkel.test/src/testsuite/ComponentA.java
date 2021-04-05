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
 * class ComponentA represents the interface to implementations of the 
 * Component A.
 * @author Ari Edinburg
 * 18/08/2011
 */
public class ComponentA extends ISafeThreadItInterface
{
  public static final int FUNCTION_A = 0; // Component A has a function A
  public static final int FUNCTION_B = 1; // Component A has a function B

  /**
   * Method ComponentA sets the implementation selected to implement the interface
   * as expected by this ComponentA.
   * @param theWorker - the ThreadIt that is the implementation of ComponentA.
   */
  public ComponentA (ThreadIt theWorker)
  {
    super (new WeakReference<ThreadIt>(theWorker), new WeakReference<ThreadIt> (null));
  } // ComponentA
  
  /**
   * method callFunctionA does the work expected according to specification of FunctionA.
   */
  void callFunctionA ()
  {
    long theWorkId = 0;
    ThreadIt theWorker = null;    
    ThreadItMessage aMsg = null;
        
    // Send a message.
    aMsg = new ThreadItMessage (FUNCTION_A);
    theWorker = getSafeWorker ();
    if (theWorker != null)
    {
      theWorkId = aMsg.sendTo (theWorker);
    } // if 
  } // callFunctionA

  /**
   * method callFunctionA does the work expected according to specification of FunctionB.  
   */
  void callFunctionB ()
  {
    long theWorkId = 0;
    ThreadIt theWorker = null;    
    ThreadItMessage aMsg = null;
        
    // Send a message.
    aMsg = new ThreadItMessage (FUNCTION_B);
    theWorker = getSafeWorker ();
    if (theWorker != null)
    {
      theWorkId = aMsg.sendTo (theWorker);
    } // if 
  } // callFunctionB
  
} // class ComponentA
