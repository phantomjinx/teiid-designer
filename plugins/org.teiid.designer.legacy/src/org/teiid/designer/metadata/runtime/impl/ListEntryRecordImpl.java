/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.metadata.runtime.impl;

import org.teiid.designer.metadata.runtime.ListEntryRecord;

/**
 * ListEntryRecordImpl
 *
 * @since 8.0
 */
public class ListEntryRecordImpl implements ListEntryRecord {
    
    private String uuid;
    private int position;
    
    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================
    
    public ListEntryRecordImpl(final String uuid, final int position) {
        this.uuid     = uuid;
        this.position = position;
    }

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /** 
     * @see org.teiid.designer.metadata.runtime.ListEntryRecord#getPosition()
     */
    @Override
	public int getPosition() {
        return this.position;
    }

    /** 
     * @see org.teiid.designer.metadata.runtime.ListEntryRecord#getUUID()
     */
    @Override
	public String getUUID() {
        return this.uuid;
    }

}
