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
package test.load;

import org.apache.log4j.Logger;

import au.com.ashkel.javalib.threads.ProtectedQueue;
import au.com.ashkel.javalib.concurrency.CriticalSection;

public class ThreadItLoadTest 
{
      /** Logger for this class */
    private static final Logger m_theLogger = Logger.getLogger (ThreadItLoadTest.class);
    
    private ProtectedQueue q;
    private static int WRITE_COUNT = 400000;
    private volatile boolean m_isInserting = true;
    private volatile boolean m_isRetrieving = true;
    
    public ThreadItLoadTest ()
    {
        q = new ProtectedQueue();
        
        //thread to insert objects in queue
        Runnable r = new Runnable ()
        {
            public void run ()
            {
                insert();
            }
        };
        Thread inserter = new Thread (r, "Inserter");       

        
        try
        {
            Thread.sleep(100);
        }
        catch (Exception ex)
        {
            
        }
        
        //thread to retrieve objects from queue
        r = new Runnable ()
        {
            public void run ()
            {
                retrieve();
            }
        };
        Thread retriever = new Thread (r, "Retriever");     
        retriever.start();
        inserter.start();
        try
        {
            Thread.sleep(60000);
        }
        catch (Exception ex)
        {
            
        }
        //m_isInserting = false;
        //m_isRetrieving = false;
        try
        {
            Thread.sleep(1000);
        }
        catch (Exception ex)
        {
            
        }
    }
    
    private void insert ()
    {
        long theCount = 0;
        while (m_isInserting)
        {   
            theCount++;
            if (theCount >= WRITE_COUNT)
            {
              m_theLogger.debug ("inserting item into queue of size: " + q.size());
            } // if 
            q.insertItem (new Object());
            if (theCount >= WRITE_COUNT) 
            {
              m_theLogger.debug ("inserted item into queue of size: " + q.size());
              theCount = 0;
              try
              {
                Thread.sleep(300);
              }
              catch (Exception ex)
              {
                m_theLogger.debug ("insert exception " +  ex.getMessage ());                  
              }

            } // if 
            theCount++;
        }
        m_theLogger.debug ("exited from insert thread with " + q.size());
    }
    
    private void retrieve ()
    {
        long theCount = 0;
        Object anItem = null; 
        while (m_isRetrieving)
        {   
            theCount++;
            if (theCount >= WRITE_COUNT) 
            {
              m_theLogger.debug ("retrieving item from queue of size: " + q.size());
            } // if 
            anItem = q.waitItem (100001);
            if ((theCount >= WRITE_COUNT) || (q.size () < 5))
            {
              m_theLogger.debug("retrieved item from queue of size: " + q.size());
              theCount = 0;
            } // if 
            theCount++;
        }   
        m_theLogger.debug ("exited from retrieve thread with " + q.size());
    }
    
    public static void main (String args[])
    {
        new ThreadItLoadTest ();
    }   
}