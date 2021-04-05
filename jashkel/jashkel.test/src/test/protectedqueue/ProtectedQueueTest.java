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
package test.protectedqueue;

import au.com.ashkel.javalib.threads.ProtectedQueue;

public class ProtectedQueueTest 
{
    private ProtectedQueue q;
    
    public void test ()
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
        try
        {
            Thread.sleep(100);
        }
        catch (Exception ex)
        {
            
        }
        
        inserter.start();       
    }
    
    private void insert ()
    {
        long theCount = 0;
        while (true)
        {   
            theCount++;
            if (theCount >= 200000)
            {
              System.out.println("inserting item into queue of size: " + q.size());
            } // if 
            q.insertItem(new Object());
            if (theCount >= 200000)
            {
              System.out.println("inserted item into queue of size: " + q.size());
              theCount = 0;
                try
                {
                    Thread.sleep(60);
                }
                catch (Exception ex)
                {
                    
                }
            } // if 
            theCount++;
        }
    }
    
    private void retrieve ()
    {
        long theCount = 0;
        while (true)
        {   
            theCount++;
            if (theCount >= 100000) 
            {
              System.out.println("retrieving item from queue of size: " + q.size());
            } // if 
            q.waitItem(10);
            if ((theCount >= 100000) || (q.size () < 2))
            {
              System.out.println("retrieved item from queue of size: " + q.size());
              theCount = 0;
            } // if 
            theCount++;
        }   
    }
    
    public static void main (String args[])
    {
    	ProtectedQueueTest theTest = new ProtectedQueueTest ();
    	theTest.test ();
    }   
}