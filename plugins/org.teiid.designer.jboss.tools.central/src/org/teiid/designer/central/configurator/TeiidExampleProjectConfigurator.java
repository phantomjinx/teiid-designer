package org.teiid.designer.central.configurator;

import java.util.ArrayList;
import java.util.List;
import org.jboss.tools.project.examples.configurators.DefaultJBossCentralConfigurator;
/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/

/**
 * Extends the default configurator by adding teiid designer
 * related content to jboss central
 */
public class TeiidExampleProjectConfigurator extends DefaultJBossCentralConfigurator {

    private static final String NEW_MODEL_PROJECT_ID = "newModelProject"; //$NON-NLS-1$
    
    List<String> wizards;
    
    @Override
    public List<String> getWizardIds() {
        if (wizards == null) {
            List<String> parentWizards = super.getWizardIds();
            wizards = new ArrayList<String>(parentWizards);
            wizards.add(NEW_MODEL_PROJECT_ID);
        }
        
        return wizards;
    }

}
