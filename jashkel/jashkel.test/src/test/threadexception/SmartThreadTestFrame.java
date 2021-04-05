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
package test.threadexception;

import au.com.ashkel.javalib.threadexceptions.CheckedThreadException;
import au.com.ashkel.javalib.threadexceptions.SmartThread;
import au.com.ashkel.javalib.threadexceptions.ThreadExceptionCleanup;
import au.com.ashkel.javalib.threadexceptions.ThreadExceptionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;

/**
 * Title:        Threads and Exceptions
 * Description:  This project explores a mechanism for capturing exceptions that
 * are generated within a thread and managing them.
 * Copyright:    Copyright (c) 2001
 * Company:      Ashkel Software
 * @author Ari Edinburg
 * @version 1.0
 */

public class SmartThreadTestFrame extends JFrame implements ActionListener, ThreadExceptionListener 
{
  JPanel contentPane;
  BorderLayout borderLayout1 = new BorderLayout();

  private Button startButton1;
  private Button startButton2;
  public List list1;
  public List list2;
  public static List status;


  /**Construct the frame*/
  public SmartThreadTestFrame() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  /**Component initialization*/
  private void jbInit() throws Exception  {
    //setIconImage(Toolkit.getDefaultToolkit().createImage(SmartThreadTestFrame.class.getResource("[Your Icon]")));
    contentPane = (JPanel) this.getContentPane();
    contentPane.setLayout(borderLayout1);
    this.setSize(new Dimension(400, 300));
    this.setTitle("Smart Threads Exception Testing");

	Panel buttonPanel = new Panel();
	startButton1 = new Button("Start Smart Java Thread, throw Checked Exception");
	startButton2 = new Button("Start Smart Java Thread, throw Unchecked Exception");
//	startButton1.addActionListener(this);
//	startButton2.addActionListener(this);
        buttonPanel.setLayout(new GridLayout(2,1));
	startButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        startButton1_actionPerformed(e);
      }
    });
    startButton2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        startButton2_actionPerformed(e);
      }
    });
    buttonPanel.add(startButton1);
	buttonPanel.add(startButton2);
	contentPane.add (buttonPanel, "North");

	Panel panel = new Panel();
	panel.setLayout(new GridLayout(1,2));
	list1 = new List();
	list2 = new List();
	panel.add(list1);
	panel.add(list2);
	contentPane.add(panel, "Center");

	status = new List();
	contentPane.add(status, "South");
    

  }
  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      System.exit(0);
    }
  }

  void startButton1_actionPerformed(ActionEvent event)
  {
    String fileName = "";
    String threadName = "";
    SmartThreadTestFrame primaryFrame = null;
    

	//When the user clicks one of the startButtons, create a FileOpener object and tell it to open a
	//file and place the file contents in a listbox. This operation will be dispatched to a secondary
	//thread while the primary thread opens, reads, and writes another file to the other listbox. The
	//idea is to have both listboxes being populated with their respective files at the same time.
	  primaryFrame = this;
	  fileName = new String("dummy.fil");
      threadName = new String("Smart Java Thread #1");
    startFileOpener (threadName);
  }

  void startButton2_actionPerformed(ActionEvent event)
  {
    String fileName = "";
    String threadName = "";
    SmartThreadTestFrame primaryFrame = null;
    
		primaryFrame = null;
		fileName = new String("layoutmanager2.java");
		threadName = new String("Smart Java Thread #2");

      startFileOpener (threadName);
  }

void startFileOpener (String theThreadName)
{
  String fileName = "";
  String threadName = "";
  SmartThreadTestFrame primaryFrame = null;


	FileOpener runnable = new FileOpener (primaryFrame, fileName);
	SmartThread smartThread = new SmartThread (runnable, threadName);
	status.add("Thread: "+ Thread.currentThread().getName() + ". Creating Secondary Thread");

	//We register this object as an exception listener. This will ensure that exceptionOccurred()
	//will be called if ANY exceptions occur on our secondary thread.
	smartThread.addThreadExceptionListener(this);

	//An exception will occur in our runnable sometime after the call to start(). Because we have
	//passed a file that does not exist a FileNotFoundException will be thrown. However, it must
	//be caught inside of run() because run() can't throw any checked exceptions. To propagate it out of
	//run() we wrapper it in an unchecked Exception so our exceptionOccurred() method will be called.
	smartThread.start();

	//This code will run fine.
	try {
		FileReader filReader = new FileReader("layoutmanager.java");
		BufferedReader bufReader = new BufferedReader(filReader);
		String str = bufReader.readLine();
		while (str != null)
		{
			list1.add(str);
			str = bufReader.readLine();
		}
	}
	catch (FileNotFoundException FileNotFoundExc) {}
	catch (IOException IOExc) {}
  } // startFileOpener

public void actionPerformed (ActionEvent event)
{
    String fileName = "";
    String threadName = "";
    SmartThreadTestFrame primaryFrame = null;

    //When the user clicks one of the startButtons, create a FileOpener object and tell it to open a 
    //file and place the file contents in a listbox. This operation will be dispatched to a secondary 
    //thread while the primary thread opens, reads, and writes another file to the other listbox. The 
    //idea is to have both listboxes being populated with their respective files at the same time.
    //
    //However, notice what happens when we pass in a dummy filename, i.e., one that does not exist.
    //There is no indication to this code that its secondary thread could not complete its task 
    //successfully. Passing in "null" for the first param in the FileOpener ctor, yields similar
    //results.
    if (event.getSource() == startButton1)
    {
        primaryFrame = this;
        fileName = new String("dummy.fil");
        threadName = new String("Regular Java Thread #1");
    }
    else if (event.getSource() == startButton2)
    {
        primaryFrame = null;
        fileName = new String("layoutmanager2.java");
        threadName = new String("Regular Java Thread #2");
    }

    FileOpener runnable = new FileOpener(primaryFrame, fileName);
    Thread thread = new Thread(runnable, threadName);

    //An exception will occur in our runnable sometime after the call to start(). Because we have
    //passed a file that does not exist a FileNotFoundException will be thrown. However, it must
    //be caught inside of run() because run() can't throw any checked exceptions. We currently have no
    //way to know that our secondary thread failed with this exception.
    thread.start();

    //This code will run fine
    try {
        FileReader filReader = new FileReader("layoutmanager.java");
        BufferedReader bufReader = new BufferedReader(filReader);
        String str = bufReader.readLine();
        while (str != null)
        {
            list1.add(str);
            str = bufReader.readLine();
        }
    }
    catch (FileNotFoundException fileNotFoundExc) {}
    catch (IOException IOExc) {}
    }

//This method is called whenever an exception occurs on a secondary thread that 
//this object has been registered a listener for. 
    public void exceptionOccurred(Runnable sourceRunnable, Thread sourceThread, Throwable threadException) 
    { //1
    //If we get a CheckedThreadException, we need to figure out exactly what type 
//of exception it is.
    if (threadException instanceof CheckedThreadException) //2
    { 
        if (((CheckedThreadException)threadException).getThreadException() instanceof    FileNotFoundException) 
        { //3
            //Don't add ourselves as a listener if this file is not found because that // would cause an infinite loop without some special code. You can of // course add another object as a listener. Create another thread and 
//give it another try with another file.
            FileOpener runnable2 = new FileOpener(this, "layoutmanager2.java"); //4
            SmartThread smartThread2 = new SmartThread(runnable2, "Smart Java Thread #3");
            smartThread2.start();
        }
    }
    else 
    {
        //Call back into our runnable object to execute some cleanup code before this 
//secondary thread is terminated by the system.
        ((FileOpener)sourceRunnable).cleanupOnException(threadException); //5
    }
    }

/**
 * Class: FileOpener This class opens a given file and displays the contents of the file in a list box created by another object on another thread. This class implements Runnable and is to be executed on a secondary thread. Notice that if any exception occurs, whether a checked or unchecked exception, there is no way to notify the calling code of the problem. This code will fail and the calling code will not know why it didn't complete successfully.
 */
class FileOpener implements Runnable, ThreadExceptionCleanup
{
    private SmartThreadTestFrame frame;
    private String fileName;
    BufferedReader bufReader = null;
    //ctor: Store away the frame because we are going to write this file into a listbox in this frame.
    public FileOpener(SmartThreadTestFrame primaryFrame, String file)
    {
        frame = primaryFrame;
        fileName = file;
    }   

    public void run() {
        try {
            Object anObj = null;
            anObj.notify();
            FileReader filReader = new FileReader(fileName);
            bufReader = new BufferedReader(filReader); //2
            String str = bufReader.readLine();
            while (str != null) {
                frame.list2.add(str); //will throw NullPointerException if frame is null
                str = bufReader.readLine();
            } 
            }
            catch (FileNotFoundException FileNotFoundExc) { //3
                //Because we can't throw checked exceptions out of run(), wrapper it 
//    in an unchecked exception.
                throw new CheckedThreadException(Thread.currentThread(), 
    FileNotFoundExc); //4
            } 
            catch (IOException IOExc) {}
        }
        public void cleanupOnException(Throwable threadException) //5
        { 
            frame.status.add("Thread: "+ Thread.currentThread().getName() + ". In cleanupOnException "+  "calling close() on the bufferedReader.");
            try {
                bufReader.close(); //6
            }
            catch (IOException IOExc){}
        }
    } 

} // class




