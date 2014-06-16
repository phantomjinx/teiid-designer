/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.jboss.ide.eclipse.as.storage.ui.wizard.page;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.jboss.ide.eclipse.as.storage.ui.Messages;
import org.jboss.ide.eclipse.as.storage.ui.StorageUIPlugin;
import org.jboss.ide.eclipse.as.storage.ui.StorageUIPlugin.Images;

/**
 *
 */
public class StorageExportDestinationPage extends WizardPage {

    /**
     * Create new instance
     */
    public StorageExportDestinationPage() {
        super(StorageExportDestinationPage.class.getSimpleName(),
                   Messages.storageExportDestinationPageTitle,
                   StorageUIPlugin.getDefault().getImageDescriptor(Images.EXPORT_WIZARD));
    }

    @Override
    public void createControl(Composite parent) {
        Composite mainPanel = new Composite(parent, SWT.NONE);
        new Text(mainPanel, SWT.NONE).setText("Export Destination");

        setControl(mainPanel);
    }

}
