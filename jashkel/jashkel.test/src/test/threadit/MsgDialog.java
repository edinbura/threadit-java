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

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * Title:        Threading Library
 * Description:  This is a port of the C++ Threading Library to Java. The library provides a framework for an
 * asynchronous threading model that can be easily used to build up a system using concurrency as a means
 * of abstraction.
 * Copyright:    Copyright (c) 2001
 * Company:      Ashkel Software
 * @author Ari Edinburg
 * @version 1.0
 */

public class MsgDialog extends JDialog {
  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();

  public MsgDialog(Frame frame, String title, boolean modal) {
    super(frame, title, modal);
    try {
      jbInit();
      pack();
    } 
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public MsgDialog() {
    this(null, "", false);
  }
  void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    jButton1.setText("jButton1");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    jButton2.setText("jButton2");
    getContentPane().add(panel1);
    panel1.add(jButton1, BorderLayout.SOUTH);
    panel1.add(jButton2, BorderLayout.NORTH);
  }

  void jButton1_actionPerformed(ActionEvent e)
  {
    this.dispose();
  }
}