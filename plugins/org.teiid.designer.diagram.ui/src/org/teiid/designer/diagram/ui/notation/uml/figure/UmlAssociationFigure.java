/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.notation.uml.figure;

import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.diagram.ui.PluginConstants;
import org.teiid.designer.diagram.ui.figure.LabeledRectangleFigure;
import org.teiid.designer.diagram.ui.util.colors.ColorPalette;
import org.teiid.designer.diagram.ui.util.directedit.DirectEditFigure;

/**
 * UmlAttributeFigure
 *
 * @since 8.0
 */
public class UmlAssociationFigure extends LabeledRectangleFigure implements DirectEditFigure {
    private ImageFigure errorIcon;
    private ImageFigure warningIcon;
    
    /**
     * Construct an instance of UmlAttributeFigure.
     * 
     */
    
    public UmlAssociationFigure(String labelString, ColorPalette colorPalette) {
        super(labelString, true, colorPalette);
        showSelected(false);
    }
    
    
    public UmlAssociationFigure(String labelString, Image icon, ColorPalette colorPalette) {
        super(labelString, icon, true, colorPalette);
        showSelected(false);
    }
    
    
    public UmlAssociationFigure(String labelString, Font newFont, ColorPalette colorPalette) {
        super(labelString, newFont, true, colorPalette);
        showSelected(false);
    }
    
    /*
     *  (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.figure.DiagramFigure#layoutFigure()
     */
    @Override
    public void layoutFigure() {
        // Default does nothing
    }
    
    /*
     *  (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.figure.DiagramFigure#activate()
     */
    @Override
    public void activate() {
        // Default implementation does nothing;
    }
    
    /*
     *  (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.figure.DiagramFigure#deactivate()
     */
    @Override
    public void deactivate() {
        // Default implementation does nothing;
    }
    
    /*
     *  (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.figure.DiagramFigure#refreshFont()
     */
    @Override
    public void refreshFont() {
        super.refreshFont();
    }
    
    /*
     *  (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.util.directedit.DirectEditFigure#getLabelFigure()
     */
    @Override
	public Label getLabelFigure() {
        return this.getLabel();
    }
    
    /*
     *  (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.figure.DiagramFigure#updateForName(java.lang.String)
     */
    @Override
    public void updateForName(String newName ) {
        super.updateForName(newName);
//        super.resize();
    }
    
    /*
     *  (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.figure.DiagramFigure#updateForName(java.lang.String, org.eclipse.swt.graphics.Image)
     */
	@Override
    public void updateForName(String newName, Image icon) {
		super.updateForName(newName);
		super.setIcon(icon);
	}
    
    /*
     *  (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.figure.DiagramFigure#updateForError(boolean)
     */
    @Override
    public void updateForError(boolean hasErrors) {
        if( hasErrors ) {
            if( errorIcon == null ) {
                errorIcon = new ImageFigure(DiagramUiPlugin.getDefault().getImage(PluginConstants.Images.ERROR_ICON));
                if( errorIcon != null ) {
                    this.add(errorIcon);
                    errorIcon.setLocation(new Point(2, 2));
                    errorIcon.setSize(errorIcon.getPreferredSize());
                }
            }
        } else if( errorIcon != null ) {
            this.remove(errorIcon);
            errorIcon = null;
        }
    }
    
    /*
     *  (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.figure.DiagramFigure#updateForWarning(boolean)
     */
    @Override
    public void updateForWarning(boolean hasWarnings) {
        if( hasWarnings ) {
            if( warningIcon == null ) {
                warningIcon = new ImageFigure(DiagramUiPlugin.getDefault().getImage(PluginConstants.Images.WARNING_ICON));
                if( warningIcon != null ) {
                    this.add(warningIcon);
                    warningIcon.setLocation(new Point(4, 4));
                    warningIcon.setSize(warningIcon.getPreferredSize());
                }
            }
        } else if( warningIcon != null ) {
            this.remove(warningIcon);
            warningIcon = null;
        }
    }
    
    /*
     *  (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.figure.DiagramFigure#showSelected(boolean)
     */
    @Override
    public void showSelected(boolean selected) {
        if( selected )
            this.setBackgroundColor(getColor(ColorPalette.SELECTION_COLOR_ID));
        else
            this.setBackgroundColor(getColor(ColorPalette.SECONDARY_BKGD_COLOR_ID));
    }


}
