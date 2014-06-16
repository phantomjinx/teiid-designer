/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.jboss.ide.eclipse.as.storage.ui.wizard;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.jboss.ide.eclipse.as.storage.IStorageSource;
import org.jboss.ide.eclipse.as.storage.ui.Messages;
import org.jboss.ide.eclipse.as.storage.ui.StorageUIPlugin;
import org.jboss.ide.eclipse.as.storage.ui.StorageUIPlugin.Images;
import org.jboss.ide.eclipse.as.storage.ui.wizard.page.StorageExportCategoryPage;
import org.jboss.ide.eclipse.as.storage.ui.wizard.page.StorageExportDestinationPage;

/**
 * Wizard for exporting workspace configuration settings from
 * implementing plugins (@linkIStorageUnit)
 * to a destination, implemented using the {@link IStorageSource}
 */
public class StorageExportWizard extends Wizard implements IExportWizard {

    private StorageExportCategoryPage categoryPage;
    private StorageExportDestinationPage destinationPage;

    /**
     * Create new instance
     */
    public StorageExportWizard() {
        IDialogSettings workbenchSettings = StorageUIPlugin.getDefault().getDialogSettings();
        IDialogSettings section = workbenchSettings.getSection(getClass().getSimpleName());
        if (section == null) {
            section = workbenchSettings.addNewSection(getClass().getSimpleName());
        }
        setDialogSettings(section);
    }

    @Override
    public void addPages() {
        categoryPage = new StorageExportCategoryPage();
        destinationPage = new StorageExportDestinationPage();
        addPage(categoryPage);
        addPage(destinationPage);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setWindowTitle(Messages.storageExportWizardName);
        
        setDefaultPageImageDescriptor(StorageUIPlugin.getDefault().getImageDescriptor(Images.EXPORT_WIZARD));
        setNeedsProgressMonitor(true);
    }

    @Override
    public boolean performFinish() {
        /*
         * TODO
         */
        return true;
    }

}
