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
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import org.apache.log4j.Logger;
import test.threadit.ActiveModel;

/**
 * Title:        Threading Library Description:  This is a port of the C++ Threading Library to Java.
 * The library provides a framework for an asynchronous threading model that can be easily used to build
 * up a system using concurrency as a means of abstraction.
 * Copyright:    Copyright (c) 2001
 * Company:      Ashkel Software
 * @author  Ari Edinburg
 * @version  1.0
 */

public class ThreadTestFrame extends JFrame 
{
  /**
   * Logger for this class
   */
  private static final Logger m_theLogger = Logger.getLogger (ThreadTestFrame.class);
  
  JPanel contentPane;
  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu jMenuFile = new JMenu();
  JMenuItem jMenuFileExit = new JMenuItem();
  JMenu jMenuHelp = new JMenu();
  JMenuItem jMenuHelpAbout = new JMenuItem();
  JToolBar jToolBar = new JToolBar();
  ImageIcon image1;
  ImageIcon image2;
  ImageIcon image3;
  JLabel statusBar = new JLabel();
  TitledBorder titledBorder1;

 
  ActiveModel m_theWorker = null;
  
  JButton jButtonCalcInfo = new JButton();
  JButton jButtonGetInfo = new JButton();
  JButton jButtonGetInfoDirect = new JButton();
  JButton jButtonExitThread = new JButton();
  JButton jButtonRunIt = new JButton();
  
  JTextField jTextFieldCalcInfo = new JTextField();
  JTextField jTextFieldGetInfo = new JTextField();  
  JTextField jTextFieldBackground = new JTextField();
  JTextField jTextFieldDirect = new JTextField();  
  JTextField jTextThreadState = new JTextField();
  private volatile boolean m_isRunning = false;
  

  /**Construct the frame*/
  public ThreadTestFrame() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  /**Component initialization*/
  private void jbInit() throws Exception 
  {
    image1 = new ImageIcon (ThreadTestFrame.class.getResource("openFile.gif"));
    image2 = new ImageIcon (ThreadTestFrame.class.getResource("closeFile.gif"));
    image3 = new ImageIcon (ThreadTestFrame.class.getResource("help.gif"));
    //setIconImage(Toolkit.getDefaultToolkit().createImage(ThreadTestFrame.class.getResource("[Your Icon]")));
    contentPane = (JPanel) this.getContentPane();
    titledBorder1 = new TitledBorder("");
    contentPane.setLayout(null);
    this.setSize(new Dimension(400, 400));
    this.setTitle("Java Thread Tests");
    statusBar.setText(" ");
    statusBar.setBounds(new Rectangle(0, 283, 400, 17));
    jMenuFile.setText("File");
    jMenuFileExit.setText("Exit");
    jMenuFileExit.addActionListener(new ActionListener()  {
      public void actionPerformed(ActionEvent e) {
        jMenuFileExit_actionPerformed(e);
      }
    });
    jMenuHelp.setText("Help");
    jMenuHelpAbout.setText("About");
    jMenuHelpAbout.addActionListener(new ActionListener()  {
      public void actionPerformed(ActionEvent e) {
        jMenuHelpAbout_actionPerformed(e);
      }
    });
    jToolBar.setBounds(new Rectangle(0, 0, 400, 31));
    
    // Set the button that will be used to call calcInfo
    jButtonCalcInfo.setToolTipText("calcInfo");    
    jButtonCalcInfo.setText ("calcInfo");
    jButtonCalcInfo.setBounds (new Rectangle(13, 60, 133, 38));
    jButtonCalcInfo.addActionListener (new java.awt.event.ActionListener ()
    {
      public void actionPerformed (ActionEvent e)
      {
        jButtonCalcInfo_actionPerformed (e);
      } // actionPerformed
    });
    // Set the button that will be used to call getInfo    
    jButtonGetInfo.setToolTipText("getInfo");
    jButtonGetInfo.setText ("getInfo");
    jButtonGetInfo.setBounds (new Rectangle(13, 115, 133, 38));
    jButtonGetInfo.addActionListener (new java.awt.event.ActionListener ()
    {
      public void actionPerformed (ActionEvent e)
      {
        jButtonGetInfo_actionPerformed (e);
      } // actionPerformed
    });
    // Set the button that will be used to call getInfo direct.
    jButtonGetInfoDirect.setToolTipText("getInfo direct");    
    jButtonGetInfoDirect.setText ("getInfo direct");
    jButtonGetInfoDirect.setBounds (new Rectangle(13, 170, 133, 38));
    jButtonGetInfoDirect.addActionListener (new java.awt.event.ActionListener ()
    {
      public void actionPerformed (ActionEvent e)
      {
        jButtonGetInfoDirect_actionPerformed (e);
      } // actionPerformed
    });
    // Set the button that will be used exit the thread.
    jButtonExitThread.setToolTipText("exitThread");    
    jButtonExitThread.setText ("exitThread");
    jButtonExitThread.setBounds (new Rectangle(13, 10, 133, 20));
    jButtonExitThread.addActionListener (new java.awt.event.ActionListener ()
    {
      public void actionPerformed (ActionEvent e)
      {
        jButtonExitThread_actionPerformed (e);
      } // actionPerformed
    });
    
    jButtonRunIt.setToolTipText("run fast");    
    jButtonRunIt.setText ("run fast");
    jButtonRunIt.setBounds (new Rectangle(13, 215, 133, 38));
    jButtonRunIt.addActionListener (new java.awt.event.ActionListener ()
    {
      public void actionPerformed (ActionEvent e)
      {
        jButtonRunFast_actionPerformed (e);
      } // actionPerformed
    });
    
    
    jTextThreadState.setToolTipText ("");
    jTextThreadState.setText ("running");
    jTextThreadState.setBounds (new Rectangle (160, 10, 133, 20));

    // Set the fields that will hold the result of the request.
    jTextFieldBackground.setToolTipText ("");
    jTextFieldBackground.setText ("Background");
    jTextFieldBackground.setBounds (new Rectangle (160, 44, 133, 20));
    
    jTextFieldCalcInfo.setToolTipText ("");
    jTextFieldCalcInfo.setText ("CalcInfo");
    jTextFieldCalcInfo.setBounds (new Rectangle (160, 69, 133, 20));
    
    jTextFieldGetInfo.setToolTipText ("");
    jTextFieldGetInfo.setText ("GetInfo");
    jTextFieldGetInfo.setBounds (new Rectangle (160, 124, 133, 20));
    
    jTextFieldDirect.setToolTipText ("");
    jTextFieldDirect.setText ("Direct");
    jTextFieldDirect.setBounds (new Rectangle (160, 179, 133, 20));
    
    jMenuFile.add(jMenuFileExit);
    jMenuHelp.add(jMenuHelpAbout);
    jMenuBar1.add(jMenuFile);
    jMenuBar1.add(jMenuHelp);
    this.setJMenuBar(jMenuBar1);
    //contentPane.add(jToolBar, null);

    contentPane.add(statusBar, null);
    
    contentPane.add(jButtonCalcInfo, null);    
    contentPane.add(jButtonGetInfo, null);
    contentPane.add(jButtonGetInfoDirect, null);    
    contentPane.add(jButtonExitThread, null);
    contentPane.add(jButtonRunIt, null);
    
    contentPane.add (jTextThreadState, null);
    contentPane.add (jTextFieldCalcInfo, null);
    contentPane.add (jTextFieldGetInfo, null);
    contentPane.add (jTextFieldBackground, null);    
    contentPane.add (jTextFieldDirect, null);    
    
    
    // Initialise the active object instnace.
    m_theWorker = new ActiveModel ("ActiveWorkerThread");
    setupPeriodicCallback ();
    
  } // jbInit
  /**File | Exit action performed*/
  public void jMenuFileExit_actionPerformed(ActionEvent e) {
    System.exit(0);
  }
  /**Help | About action performed*/
  public void jMenuHelpAbout_actionPerformed(ActionEvent e) {
    ThreadTestFrame_AboutBox dlg = new ThreadTestFrame_AboutBox(this);
    Dimension dlgSize = dlg.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
    dlg.setModal(true);
    dlg.setVisible(true);
  }
  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      jMenuFileExit_actionPerformed(null);
    }
  }

  void setupPeriodicCallback ()
  {
    Notification aNotification = new Notification ();
    aNotification.setWorker (m_theWorker);
    aNotification.setTextField (jTextFieldBackground);
    m_theWorker.setPeriodicMethodCallback (aNotification);
  } // setupPeriodicCallback  
  
  /**
   * Method jButtonCalcInfo_actionPerformed is called when the 
   * user clicks the CalcInfo button. 
   * @param e 
   */
  void jButtonCalcInfo_actionPerformed (ActionEvent e)
  {
    ThreadItMessage aMsg = null;
    long theWorkId = 0;

    Callback aCallback = new Callback ();
    aCallback.setWorker (m_theWorker);
    aCallback.setTextField (jTextFieldCalcInfo);
    aMsg = new ThreadItMessage (ActiveModel.ATIVE_MODEL_CALC_INFO, aCallback);
    theWorkId = aMsg.sendTo (m_theWorker);    
  } // jButtonCalcInfo_actionPerformed
  
  /**
   * Method jButtonGetInfo_actionPerformed is called when the 
   * user clicks the GetInfo button. 
   * @param e 
   */
  void jButtonGetInfo_actionPerformed (ActionEvent e)
  {
    ThreadItMessage aMsg = null;
    long theWorkId = 0;

    Callback aCallback = new Callback ();
    aCallback.setWorker (m_theWorker);
    aCallback.setTextField (jTextFieldGetInfo);
    aMsg = new ThreadItMessage (ActiveModel.ATIVE_MODEL_GET_INFO, aCallback);
    theWorkId = aMsg.sendTo (m_theWorker);    
  } // jButtonGetInfo_actionPerformed

  void jButtonGetInfoDirect_actionPerformed (ActionEvent e)
  {
    String aString = null;

    if (m_theWorker != null)
    {
      aString = m_theWorker.getInfo ();
      if (aString == null)
      {
        aString = "isEmpty";
      } // 
      jTextFieldDirect.setText (aString);
    }
    else
    {
      log ("The worker active object is not configured correctly for use!");      
    } // if 
  } // jButtonGetInfoDirect_actionPerformed
  
  /**
   * Method jButtonExitThread_actionPerformed is called when the 
   * user clicks the CalcInfo button. 
   * @param e 
   */
  void jButtonExitThread_actionPerformed (ActionEvent e)
  {
    boolean isThreadStopped = false;	  
    m_theWorker.stopThread ();
    isThreadStopped = m_theWorker.waitForThreadToStop (2000);
    if (!isThreadStopped)
    {
   	  jTextThreadState.setText ("not stopped");
    }
    else
    {
   	  jTextThreadState.setText ("stopped");    	
    } // if 
  } // jButtonExitThread_actionPerformed
  
  void jButtonRunFast_actionPerformed (ActionEvent e)
  {
	if (m_isRunning)
	{
	  m_isRunning = false;
	}
	else
	{
	  m_isRunning = true;		
	  //thread to insert objects in queue
	  Runnable aRunnable = new Runnable ()
	  {
	    public void run ()
	    {
		  runfast ();
	    } // run
	  };
	  Thread theInserter = new Thread (aRunnable, "Inserter");
	  theInserter.start ();
	} // if     
  } // jButtonRunFast_actionPerformed
  
  private void runfast ()
  {
    ThreadItMessage aMsg = null;
    long theWorkId = 0;
    long theCounter = 0;

    while (m_isRunning)
    {
      Callback aCallback = new Callback ();
      aCallback.setWorker (m_theWorker);
      aCallback.setTextField (jTextFieldCalcInfo);
      aMsg = new ThreadItMessage (ActiveModel.ATIVE_MODEL_CALC_INFO, aCallback);
      theWorkId = aMsg.sendTo (m_theWorker);
      theCounter++;
      if (theCounter > 40000)
      {
    	theCounter = 0;
    	try 
    	{
		  Thread.sleep (30000);
		} // try
    	catch (InterruptedException e) 
    	{
		  // TODO Auto-generated catch block
		  e.printStackTrace();
		} // catch 
      } // if
    } // for
  } // runfast
  
  
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
  

  
} // ThreadTestFrame