/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.jboss.ide.eclipse.as.storage.ui.viewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.jboss.ide.eclipse.as.storage.IStorageUnit;
import org.jboss.ide.eclipse.as.storage.IStorageUnitRegistry;

/**
 *
 */
public class StorageUnitContentProvider implements ITreeContentProvider {

    @Override
    public Object[] getChildren(Object parentElement) {
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        return false;
    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof IStorageUnitRegistry) {
            IStorageUnitRegistry registry = (IStorageUnitRegistry) inputElement;
            return registry.getRegisteredUnits().toArray();
        } else if (inputElement instanceof Collection) {
            Collection<?> elements = (Collection<?>) inputElement;
            List<IStorageUnit> units = new ArrayList<IStorageUnit>();
            for (Object element : elements) {
                if (element instanceof IStorageUnit)
                    units.add((IStorageUnit) element);
            }

            return units.toArray();
        }

        return new IStorageUnit[0];
    }

    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

}
