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
 * Title : WorkDoneCallBack
 * --------------------------------------------------------------------------
 * Description :
 * Class WorkDoneCallBack is the interface that specifies how a client
 * that has work done on it's behalf can be informed that the work (performed
 * in a separated thread) has completed. The client implements this interface
 * and is then informed when the work completes.
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

/**
 * Interface WorkDoneCallback specifies the signature for calling back
 * into a client when the work performed asynchronously on it's behalf
 * is completed.
 */
public interface WorkDoneCallback
{

  /**
   * Method onWorkDone is called when work in response to a work request
   * is completed for the client.<p>
   * theWorkItemId : is the reference number supplied that the client can use
   *                 to identify the package of work that has been completed
   *                 on it's behalf.
   */
  public void onWorkDone (String theThreadName, long theWorkItemId);

} // interface WorkDoneCallback


