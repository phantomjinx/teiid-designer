/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.transaction;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Notification;

import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.util.ProcessedNotificationResult;

/**
 * Interface for the UnitOfWork Object for an Emf Container
 * @author lphillips
 * @since 3.1
 * 
 */
public interface UnitOfWork extends MtkTransaction{
    /**
     * Call back method for the EmfAdapter to notify the transaction of an event notification
     * @param notification
     */
    void processNotification(Notification notification) throws ModelerCoreException;
    
//    /**
//     * Process the given invocation through this UnitOfWork..
//     * If the invocation involves a write command, execute it through the edit
//     * domain, else, execute it directly against the delegate.
//     * @param invocation
//     * @return the Object result of processing the invocation
//     */
//    Object process(Invocation invocation) throws ModelerCoreException;

    /**
     * @return the id for this UnitOfWork
     */
    Object getId();

    /**
     * Setter for description attribute.  Used when creating the undoable.
     * @param description
     */
    public void setDescription(String description);
            
    /**
     * Pass through to the editing domain /  command stack to execute the
     * command
     * @return true if the command was executed, or false if it could not be executed
     * @see com.metamatrix.mtk.emf.container.container.transaction.api.UnitOfWork#executeCommand(Command)
     */
    boolean executeCommand(Command command) throws ModelerCoreException;  
    
    /**
     * Setter for the significant attribute that will passed on to the undoable upon commit
     * @param isSignificant
     * @throws ModelerCoreException if the UoW is not in a started state
     */
    void setSignificant(boolean isSignificant) throws ModelerCoreException; 
    
    /**
     * @return the isUndoable flag
     */
    boolean isUndoable();

    /**
     * Set the isUndoable flag
     * @param b
     */
    public void setUndoable(boolean b); 
    
    /**
     * Method to override the rollback functionality of this class and place it on the Source of the transaction. Even if a txn
     * was NOT undoable, we were still treating it under the hood as undoable and this was potentially causing memory issues for
     * txn's where a large amount of work was being done (i.e. New Model Wizards, XML Document model builder, etc.) If set to
     * TRUE, then the txn Command will NOT be created and txn's will NOT be cached. This is implemented to allow actions like
     * NewModelWizard to NOT care about the undoablity of any of the add/remove/change commands created when building the model.
     * 
     * @return
     * @since 5.0.2
     */
    public void setOverrideRollback( boolean b );

    /**
     * Method add a new ProcessedNotificationResult for post-commit processing. Method looks for existing result referencing the
     * same resource, then appends the dereferenced resources to it's list, else it adds a new result to the cache for use in
     * final processing.
     * 
     * @return
     * @since 5.0.2
     */
	void addProcessedNotificationResult(ProcessedNotificationResult result);
}
