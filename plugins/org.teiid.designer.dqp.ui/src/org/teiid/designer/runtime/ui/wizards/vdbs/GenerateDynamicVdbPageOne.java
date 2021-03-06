/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.wizards.vdbs;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.Messages;
import org.teiid.designer.ui.common.graphics.GlobalUiColorManager;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.widget.Label;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;
import org.teiid.designer.ui.viewsupport.ModelProjectSelectionStatusValidator;
import org.teiid.designer.ui.viewsupport.SingleProjectOrFolderFilter;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbPlugin;

/**
 * Page 1 of the Generate Dynamic Vdb Wizard
 */
public class GenerateDynamicVdbPageOne extends AbstractWizardPage implements DqpUiConstants, StringConstants {

    private Text dynamicVdbName;

    private Label dynamicVdbLocationText;

    private Text dynamicVdbFileName;

    private GenerateDynamicVdbManager vdbManager;
    
    private Button excludeSourceDdlButton;

    private Button suppressDefaultAttributesOption;

    /**
     * ShowDDlPage constructor
     * @param vdbManager the vdb manager
     * @since 8.1
     */
    public GenerateDynamicVdbPageOne(GenerateDynamicVdbManager vdbManager) {
        super(GenerateDynamicVdbPageOne.class.getSimpleName(), EMPTY_STRING);
        this.vdbManager = vdbManager;
        setTitle(Messages.GenerateDynamicVdbPageOne_title);
    }

    @Override
    public void createControl(Composite parent) {
        final Composite mainPanel = new Composite(parent, SWT.NONE);
        mainPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        mainPanel.setLayout(new GridLayout(2, false));

        setControl(mainPanel);

        // VDB ARCHIVE GROUP

        {
            // Selected VDB: MyProject/myFolder/ABC.vdb
            Composite summaryGroup = WidgetFactory.createGroup(mainPanel,
                                                               Messages.GenerateDynamicVdbPageOne_summaryGroupName,
                                                               SWT.NO_SCROLL,
                                                               2);
            summaryGroup.setLayout(new GridLayout(2, false));
            GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(summaryGroup);

            Label nameLabel = new Label(summaryGroup, SWT.NONE);
            nameLabel.setText(Messages.GenerateDynamicVdbPageOne_vdb);

            Label vdbAndLocation = new Label(summaryGroup, SWT.NONE);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(vdbAndLocation);
            vdbAndLocation.setText(vdbManager.getArchiveVdb().getSourceFile().getFullPath().toString());
            vdbAndLocation.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);

            // VDB Name: products_info
            Label vdbNameLabel = new Label(summaryGroup, SWT.NONE);
            vdbNameLabel.setText(Messages.GenerateDynamicVdbPageOne_vdbName);

            Label vdbName = new Label(summaryGroup, SWT.NONE);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(vdbName);
            vdbName.setText(vdbManager.getArchiveVdb().getName());
            vdbName.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);

            // Version #: 25
            WidgetFactory.createLabel(summaryGroup, Messages.GenerateDynamicVdbPageOne_version);

            Label vdbVersion = new Label(summaryGroup, SWT.NONE);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(vdbVersion);
            vdbVersion.setText(vdbManager.getArchiveVdb().getVersion());
            vdbVersion.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);
            
            ((GridData)summaryGroup.getLayoutData()).widthHint = 400;
        }

        {
            Composite summaryGroup = WidgetFactory.createGroup(mainPanel,
                                                               Messages.GenerateDynamicVdbPageOne_dynamicVdbDefinition,
                                                               SWT.NO_SCROLL,
                                                               2);
            summaryGroup.setLayout(new GridLayout(3, false));
            GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(summaryGroup);

            // VDB Name: products_info
            WidgetFactory.createLabel(summaryGroup,
                                      GridData.VERTICAL_ALIGN_CENTER,
                                      Messages.GenerateDynamicVdbPageOne_dynamicVdbName);
            dynamicVdbName = WidgetFactory.createTextField(summaryGroup, SWT.NONE, GridData.FILL_HORIZONTAL);
            GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(dynamicVdbName);
            dynamicVdbName.setText(vdbManager.getOutputVdbName());
            dynamicVdbName.setToolTipText(Messages.GenerateDynamicVdbPageOne_dynamicVdbNameTooltip);
            dynamicVdbName.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(final ModifyEvent event) {
                    vdbManager.setOutputVdbName(dynamicVdbName.getText());
                    validatePage();
                }
            });

            Label vdbVersionLabel = WidgetFactory.createLabel(summaryGroup, Messages.GenerateDynamicVdbPageOne_version);
            GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(vdbVersionLabel);

            final Text vdbVersionText = WidgetFactory.createTextField(summaryGroup);
            GridDataFactory.fillDefaults().span(2, 1).align(SWT.LEFT, SWT.CENTER).applyTo(vdbVersionText);
            ((GridData)vdbVersionText.getLayoutData()).widthHint = 40;
            vdbVersionText.setText(vdbManager.getVersion());
            
            vdbVersionText.addModifyListener(new ModifyListener() {

                @Override
                public void modifyText(ModifyEvent e) {
                    vdbManager.setVersion(vdbVersionText.getText());
                    validatePage();
                }
            });
        }

        // Dynamic VDB Output GROUP
        {
            Composite summaryGroup = WidgetFactory.createGroup(mainPanel,
                                                               Messages.GenerateDynamicVdbPageOne_dynamicVdbDestination,
                                                               SWT.NO_SCROLL,
                                                               2);
            summaryGroup.setLayout(new GridLayout(3, false));
            GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(summaryGroup);

            // Workspace Location: MyProject/dynamic_vdbs (EDITABLE TEXT FIELD && ... Picker)
            Label locationLabel = new Label(summaryGroup, SWT.NONE);
            locationLabel.setText(Messages.GenerateDynamicVdbPageOne_location);

            dynamicVdbLocationText = new Label(summaryGroup, SWT.NONE);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(dynamicVdbLocationText);
            if (vdbManager.getOutputLocation() != null) {
                dynamicVdbLocationText.setText(vdbManager.getOutputLocation().getFullPath().toString());
            }

            Button browseButton = new Button(summaryGroup, SWT.PUSH);
            GridData buttonGridData = new GridData();
            browseButton.setLayoutData(buttonGridData);
            browseButton.setText(Messages.GenerateDynamicVdbPageOne_browse);
            browseButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    handleBrowse();
                }
            });

            // File Name: ABC-xml.vdb  (EDITABLE TEXT FIELD && ... Picker)
            WidgetFactory.createLabel(summaryGroup,
                                      GridData.VERTICAL_ALIGN_CENTER,
                                      Messages.GenerateDynamicVdbPageOne_dynamicVdbFileName);
            dynamicVdbFileName = WidgetFactory.createTextField(summaryGroup, SWT.NONE, GridData.FILL_HORIZONTAL);
            GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(dynamicVdbFileName);
            dynamicVdbFileName.setText(vdbManager.getOutputVdbFileName());
            dynamicVdbFileName.setToolTipText(Messages.GenerateDynamicVdbPageOne_dynamicVdbFileNameToolTip);
            dynamicVdbFileName.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(final ModifyEvent event) {
                    vdbManager.setOutputVdbFileName(dynamicVdbFileName.getText());
                    validatePage();
                }
            });
        }
        
        // Dynamic VDB Output GROUP
        {
            Composite optionsGroup = WidgetFactory.createGroup(mainPanel, 
                    Messages.GenerateDynamicVdbPageOne_options, SWT.NO_SCROLL, 1);
            optionsGroup.setLayout(new GridLayout(1, false));
            GridDataFactory.fillDefaults().grab(true,  false).span(2, 1).applyTo(optionsGroup);
            
            excludeSourceDdlButton = new Button(optionsGroup, SWT.CHECK);
            excludeSourceDdlButton.setText(Messages.GenerateDynamicVdbPageOne_excludeSourceDdlMetadata);
            excludeSourceDdlButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent arg0) {
					vdbManager.setExcludeSourceMetadata(excludeSourceDdlButton.getSelection());
					validatePage();
				}
			});

            suppressDefaultAttributesOption = WidgetFactory.createButton(optionsGroup,
                                                                Messages.GenerateDynamicVdbPageOne_suppressDefaultAttributesOption,
                                                                GridData.FILL_HORIZONTAL, 1, SWT.CHECK);

            GridDataFactory.fillDefaults().grab(true, false).applyTo(suppressDefaultAttributesOption);

            final IEclipsePreferences preferences = VdbPlugin.singleton().getPreferences();
            suppressDefaultAttributesOption.setSelection(preferences.getBoolean(Vdb.SUPPRESS_XML_DEFAULT_ATTRIBUTES, true));
            suppressDefaultAttributesOption.setToolTipText(Messages.GenerateDynamicVdbPageOne_suppressDefaultAttributesOptionTooltip);
            suppressDefaultAttributesOption.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    preferences.putBoolean(Vdb.SUPPRESS_XML_DEFAULT_ATTRIBUTES, suppressDefaultAttributesOption.getSelection());

                    //
                    // Reset the dynamic vdb
                    //
                    vdbManager.setDynamicVdb(null);
                }
            });
            
            final Button overwriteExistingOption = WidgetFactory.createButton(optionsGroup,
                    Messages.GenerateDynamicVdbPageOne_overwriteFilesOptionLabel,
                    GridData.FILL_HORIZONTAL, 2, SWT.CHECK);
            overwriteExistingOption.setSelection(vdbManager.overwriteExistingFiles());
            
			GridDataFactory.fillDefaults().span(1, 1).grab(true, false).applyTo(overwriteExistingOption);
			
			overwriteExistingOption.setToolTipText(Messages.GenerateDynamicVdbPageOne_overwriteVDBOptionTooltip);
			overwriteExistingOption.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
				vdbManager.setOverwriteExistingFiles(overwriteExistingOption.getSelection());
				validatePage();
				}
			});
        }
        
        setPageComplete(false);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            validatePage();
        }

        super.setVisible(visible);
    }

    void handleBrowse() {
        IProject project = vdbManager.getArchiveVdb().getSourceFile().getProject();
        final IContainer folder = WidgetUtil.showFolderSelectionDialog(project,
                                                                       new SingleProjectOrFolderFilter(project),
                                                                       new ModelProjectSelectionStatusValidator());

        if (folder != null && dynamicVdbLocationText != null) {
            vdbManager.setOutputLocation(folder);
            dynamicVdbLocationText.setText(folder.getFullPath().makeRelative().toString());
        }

        validatePage();
    }

    /* 
     * Validate the page
     */
    private void validatePage() {
        this.vdbManager.validate();
        IStatus status = vdbManager.getStatus();
        if (status.getSeverity() == IStatus.ERROR) {
            this.setErrorMessage(status.getMessage());
            this.setPageComplete(false);
            return;
        } else if (status.getSeverity() == IStatus.WARNING) {
            this.setErrorMessage(null);
            WizardUtil.setPageComplete(this, status.getMessage(), IStatus.WARNING);
        } else {
            setErrorMessage(null);
            WizardUtil.setPageComplete(this, EMPTY_STRING, NONE);
        }
    }

    @Override
    public boolean canFlipToNextPage() {
//        if (vdbManager.isGenerateRequired())
//            return false;

        return super.canFlipToNextPage();
    }
}
