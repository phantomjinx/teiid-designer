/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.connectivity;

import java.util.ResourceBundle;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.PluginUtilImpl;

/**
 * The activator class controls the plug-in life cycle
 *
 * @since 8.0
 */
public class Activator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.teiid.datatools.connectivity"; //$NON-NLS-1$

    public static final String PACKAGE_ID = Activator.class.getPackage().getName();
    
    public static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
    
    /**
     * Provides access to the plugin's log and to it's resources.
     * @since 4.0
     */
    public static final PluginUtil UTIL = new PluginUtilImpl(PLUGIN_ID, I18N_NAME, ResourceBundle.getBundle(I18N_NAME));
    
    // The shared instance
    private static Activator plugin;
    
    // The shared context
    private BundleContext bundleContext;

    /**
     * The constructor
     */
    public Activator() {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        plugin = this;
        bundleContext = context;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop( BundleContext context ) throws Exception {
        plugin = null;
        bundleContext = null;
        super.stop(context);
    }
    
    public BundleContext getBundleContext() {
        return bundleContext;
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

    /**
     * Log the given exception 
     * 
     * @param exception
     */
    public static void log(Exception exception) {
        IStatus status = new Status(
                                    IStatus.ERROR, 
                                    PLUGIN_ID,
                                    PLUGIN_ID,
                                    exception);
        
        plugin.getLog().log(status);
    }

}
