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
package test.threadit;

import au.com.ashkel.javalib.threads.*;
import javax.swing.*;
import org.apache.log4j.Logger;

/**
 * Title:        Threading Library Description:  This is a port of the C++ Threading Library to Java.
 * The library provides a framework for an asynchronous threading model that can be easily used to
 * build up a system using concurrency as a means of abstraction.
 * Copyright:    Copyright (c) 2001
 * Company:      Ashkel Software
 * @author  Ari Edinburg
 * @version  1.0
 */

public class Callback implements WorkDoneCallback
{
  private ActiveModel m_theWorker;
  private JTextField m_theTextField;
  private static long Counter = 0;
  private static long _theMaxSize = 0;
  /**
   * Logger for this class
   */
  private static final Logger m_theLogger = Logger.getLogger (Callback.class);

  public Callback()
  {
    m_theWorker = null;
  }
  public void setWorker (ActiveModel aWorker)
  {
    m_theWorker = aWorker;
  }

  public void setTextField (JTextField aTextField)
  {
    m_theTextField = aTextField;
  } // setTextField

  public void onWorkDone (String theName, long theWorkItemId)
  {
    Counter++;
    SwingUtilities.invokeLater(
      new Runnable ()
      {
        public void run ()
        {
          m_theLogger.info ("onWorkDone Method");
          updateInfo ();
        } // run
      } // Runnable
      );
  }
  
  private void updateInfo ()
  {
    ProtectedQueue theWorkDoneQ = null;
    String aString = null;
    WorkPackIt aWorkPackIt = null;

    if (m_theWorker != null)
    {
      theWorkDoneQ = m_theWorker.getWorkDoneQ();;////// AE m_theWorker.getWorkDoneQ ();
      if (theWorkDoneQ.size () > 1 )
      {
        log ("WorkDoneQ is larger than 1!");        
      } // if 
      aWorkPackIt = (WorkPackIt)theWorkDoneQ.waitItem( 100);
      if (aWorkPackIt.m_Object != null)
      {
        aString = (String)aWorkPackIt.m_Object;
      }
      else
      {
        aString = "isEmpty";
      } // if 
      if (m_theTextField != null)
      {
        m_theTextField.setText (aString);
      } // if
    }
    else
    {
      log ("Callback is not configured correctly for use!");
    } // if (m_theWorker != null)
  } // updateInfo
  
  /**
   * Method log is used to send the provided string to the logger.
   * @param theMessage the text to output to the logger.
   */
  private void log (String theMessage)
  {
    if (m_theLogger.isInfoEnabled ())
    {
      m_theLogger.info (theMessage);
    } // if
  } // log  
  public int getChangeCount ()
  {
    // TODO Auto-generated method stub
    return 0;
  }
  public int getUpdateCount ()
  {
    // TODO Auto-generated method stub
    return 0;
  }
  
} // class Callback



