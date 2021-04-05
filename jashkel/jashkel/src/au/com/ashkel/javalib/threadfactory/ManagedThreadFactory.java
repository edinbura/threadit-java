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

/*--------------------------------------------------------------------------*/
/* Package declaration.                                                     */
/*--------------------------------------------------------------------------*/
package au.com.ashkel.javalib.threadfactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

/**
 * This class implements <code>ThreadFactory</code> and provides a name prefix
 * for all thread created by this thread factory, it also create a default
 * uncaught exception hanlder to log error message if any uncaught exception
 * happening within the thread
 * 
 * Copyright:    Copyright (c) 2010
 * Company:      Ashkel Software
 * @version 1.0
 */

public class ManagedThreadFactory implements ThreadFactory {
	private static final Logger logger = Logger.getLogger(ManagedThreadFactory.class);

	private String namePrefix;

	static {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread t, Throwable e) {
				logger.error("Uncaught exception found in " + t.getName() + ": " + e.getLocalizedMessage() ,e);
			}
		});
	}

	public ManagedThreadFactory(String namePrefix) {
		super();
		this.namePrefix = namePrefix;
	}
	
	public ManagedThreadFactory() {
		super();
		this.namePrefix = "managed thread";
	}
	

	private AtomicInteger nextThreadId = new AtomicInteger(1);

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(r, namePrefix + "-" + nextThreadId.getAndIncrement());
	}
}
