/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.resource.active;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;


/**
 * This implementation of {@link EmfResourceLoader}
 * does nothing.
 *
 * @since 8.0
 */
public class DefaultResourceLoader implements EmfResourceLoader {

    /**
     * Construct an instance of DefaultResourceLoader.
     * 
     */
    public DefaultResourceLoader() {
        super();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.mtk.emf.resource.active.EmfResourceLoader#loadFeature(org.eclipse.emf.common.util.EList, java.lang.Class, org.eclipse.emf.ecore.EObject, int)
     */
    @Override
	public void loadFeature(EList valueHolder, Class dataClass, EObject owner, int featureId) {
        // do nothing
    }

    /* (non-Javadoc)
     * @see com.metamatrix.mtk.emf.resource.active.EmfResourceLoader#loadFeature(org.eclipse.emf.common.util.EList, java.lang.Class, org.eclipse.emf.ecore.EObject, int, int)
     */
    @Override
	public void loadFeature(EList valueHolder, Class dataClass, EObject owner, int featureId, int reverseFeatureId) {
        // do nothing
    }

}
