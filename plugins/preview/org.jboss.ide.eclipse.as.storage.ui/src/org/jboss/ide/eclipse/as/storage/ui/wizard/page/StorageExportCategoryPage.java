/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.jboss.ide.eclipse.as.storage.ui.wizard.page;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.statushandlers.StatusManager;
import org.jboss.ide.eclipse.as.storage.IStorageManager;
import org.jboss.ide.eclipse.as.storage.IStorageUnitRegistry;
import org.jboss.ide.eclipse.as.storage.manager.StorageManager;
import org.jboss.ide.eclipse.as.storage.ui.Messages;
import org.jboss.ide.eclipse.as.storage.ui.StorageUIPlugin;
import org.jboss.ide.eclipse.as.storage.ui.StorageUIPlugin.Images;
import org.jboss.ide.eclipse.as.storage.ui.viewer.StorageUnitContentProvider;
import org.jboss.ide.eclipse.as.storage.ui.viewer.StorageUnitLabelProvider;

/**
 *
 */
public class StorageExportCategoryPage extends WizardPage {

    private TreeViewer unitViewer;
    private Button exportAllChkBox;

    /**
     * Create new instance
     */
    public StorageExportCategoryPage() {
        super(StorageExportCategoryPage.class.getSimpleName(),
                   Messages.storageExportCategoryPageTitle,
                   StorageUIPlugin.getDefault().getImageDescriptor(Images.EXPORT_WIZARD));
    }

    @Override
    public void createControl(Composite parent) {
        Composite mainPanel = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(mainPanel);
        
        Text blah = new Text(mainPanel, SWT.NONE);
        blah.setText("Hello");
        GridDataFactory.swtDefaults().applyTo(blah);

        Group categoryGroup = new Group(mainPanel, SWT.NONE);
        GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(categoryGroup);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(categoryGroup);

        this.exportAllChkBox =  new Button(categoryGroup, SWT.CHECK);
        this.exportAllChkBox.setText(Messages.storageExportCategoryPageExportAllChkBox);

        FilteredTree unitFilteredTree = new FilteredTree(categoryGroup, SWT.NONE, new PatternFilter(), true) {
            @Override
            protected TreeViewer doCreateTreeViewer(Composite parent, int style) {
                return new CheckboxTreeViewer(parent, style);
            }
        };

        this.unitViewer = unitFilteredTree.getViewer();
        GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 200).applyTo(unitViewer.getControl());
        unitViewer.setContentProvider(new StorageUnitContentProvider());
        unitViewer.setLabelProvider(new StorageUnitLabelProvider());

        try { 
            IStorageManager manager = StorageManager.getInstance();
            IStorageUnitRegistry registry = manager.getStorageUnitRegistry();
            unitViewer.setInput(registry);
        } catch (Exception ex) {
            IStatus status = new Status(IStatus.ERROR, StorageUIPlugin.PLUGIN_ID, ex.getLocalizedMessage(), ex);
            StatusManager.getManager().handle(status, StatusManager.SHOW);
            StorageUIPlugin.log(ex);
        }

        setControl(mainPanel);
    }

}
