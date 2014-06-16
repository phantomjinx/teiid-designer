package org.jboss.ide.eclipse.as.storage.ui;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class StorageUIPlugin extends AbstractUIPlugin {

    /**
     * Enumerator of images available from this plugin
     * File name is relative to the icons directory.
     */
    public enum Images {
        /**
         * Export wizard image
         */
        EXPORT_WIZARD("property_export.png"), //$NON-NLS-1$

        /**
         * Import wizard image
         */
        IMPORT_WIZARD("property_import.png"); //$NON-NLS-1$

        private final String fileName;

        Images(String fileName) {
            this.fileName = fileName;
        }

        /**
         * @return the fileName
         */
        public String getFileName() {
            return this.fileName;
        }
    }
    /**
     * Image descriptor for export wizard icon
     */
    public final Map<Images, ImageDescriptor> iconCache = new HashMap<StorageUIPlugin.Images, ImageDescriptor>();

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = StorageUIPlugin.class.getPackage().getName();

	// The shared instance
	private static StorageUIPlugin plugin;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
    public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
    public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static StorageUIPlugin getDefault() {
		return plugin;
	}

	/**
     * @param status
     */
    public static void log(IStatus status) {
        getDefault().getLog().log(status);
    }

    /**
     * @param severity
     * @param message
     * @param e
     */
    public static void log(int severity, String message, Throwable e) {
        log(new Status(severity, PLUGIN_ID, 0, message, e));
    }
    

    /**
     * @param e
     */
    public static void log(Throwable e) {
        log(e.getMessage(), e);
    }

    /**
     * @param message
     * @param e
     */
    public static void log(String message, Throwable e) {
        log(IStatus.ERROR, message, e);
    }

    /**
     * @param imgEnum
     * @return image descriptor for given relative image path in icons directory
     */
    public ImageDescriptor getImageDescriptor( final Images imgEnum ) {
        ImageDescriptor imageDescriptor = iconCache.get(imgEnum);
        if (imageDescriptor == null) {
            imageDescriptor = imageDescriptorFromPlugin(PLUGIN_ID, "icons/" + imgEnum.getFileName()); //$NON-NLS-1$
            iconCache.put(imgEnum, imageDescriptor);
        }
        return imageDescriptor;
    }
}
