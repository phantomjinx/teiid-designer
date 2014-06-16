/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.jboss.ide.eclipse.as.storage.ui.viewer;

import org.eclipse.jface.viewers.LabelProvider;
import org.jboss.ide.eclipse.as.storage.IStorageUnit;
import org.jboss.ide.eclipse.as.storage.util.StringConstants;

/**
 *
 */
public class StorageUnitLabelProvider extends LabelProvider implements StringConstants {

    @Override
    public String getText(Object element) {
        if (element instanceof IStorageUnit) {
            IStorageUnit unit = (IStorageUnit) element;
            IStorageUnit.Category category = unit.getCategory();
            return category.getLabel();
        }

        return EMPTY_STRING;
    }
}
