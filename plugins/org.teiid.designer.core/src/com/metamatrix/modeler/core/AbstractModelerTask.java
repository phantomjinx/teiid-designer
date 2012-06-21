/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core;

import org.eclipse.core.runtime.IStatus;

import com.metamatrix.modeler.core.transaction.UnitOfWork;

/**
 * A task to be executed by
 * {@link ModelerCore#startTxn(String, Object, AbstractModelerTask)} and its
 * related methods.
 */
public abstract class AbstractModelerTask {

	private IStatus status;
	private boolean succeeded;
	private boolean started;
	private Object returnObject;

	/**
	 * Execute this task
	 * 
	 * @param unitOfWork The unit of work assigned to this task
	 * 
	 * @throws ModelerCoreException if execution fails
	 * 
	 */
	public abstract void execute(UnitOfWork unitOfWork) throws ModelerCoreException;

	/**
	 * @return the status of the executed task
	 */
	public IStatus getStatus() {
		return this.status;
	}
	
	/**
	 * Set the status of the executed task
	 * 
	 * @param status
	 */
	void setStatus(IStatus status) {
		this.status = status;
	}
	
	/**
	 * @return this task has been started
	 */
	public boolean isStarted() {
		return this.started;
	}
	
	/**
	 * Set this task to be started
	 */
	void setStarted() {
		this.started = true;
	}

	/**
	 * @return this task completed
	 */
	public boolean isComplete() {
		return this.succeeded;
	}
	
	/**
	 * Set the task as complete
	 */
	void setCompleted() {
		succeeded = true;
	}
	
	/**
	 * @return the task's return value if one was set
	 */
	public Object getReturnValue() {
		return returnObject;
	}
	
	/**
	 * Allows the task implementation to set a value to be returned
	 * after the task has completed.
	 * 
	 * @param object
	 */
	protected void setReturnValue(Object object) {
		this.returnObject = object;
	}
}
