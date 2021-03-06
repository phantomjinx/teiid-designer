/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.util;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.uml.UmlAssociation;
import org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier;
import org.teiid.designer.core.metamodel.aspect.uml.UmlComment;
import org.teiid.designer.core.metamodel.aspect.uml.UmlDiagramAspect;
import org.teiid.designer.core.metamodel.aspect.uml.UmlGeneralization;
import org.teiid.designer.core.metamodel.aspect.uml.UmlOperation;
import org.teiid.designer.core.metamodel.aspect.uml.UmlPackage;
import org.teiid.designer.core.metamodel.aspect.uml.UmlProperty;
import org.teiid.designer.core.metamodel.aspect.uml.UmlRelationship;
import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.metamodels.diagram.DiagramEntity;
import org.teiid.designer.metamodels.diagram.DiagramLink;



/** 
 * @since 8.0
 */
public class RelationalUmlEObjectHelper {
    public static final int UNKNOWN = -1;
    public static final int UML_PACKAGE = 0;
    public static final int UML_CLASSIFIER = 1;
    public static final int UML_ASSOCIATION = 2;
    public static final int UML_OPERATION = 3;
    public static final int UML_ATTRIBUTE = 4;
    public static final int UML_COMMENT = 5;
    public static final int UML_RELATIONSHIP = 6;
    public static final int UML_GENERALIZATION = 7;
    
    public static final int UML_DIAGRAM = 10;
    public static final int UML_DIAGRAM_ENTITY = 11;
    public static final int UML_DIAGRAM_LINK = 12;

    public static int getEObjectType(final EObject eObj) {
        int type = UNKNOWN;
        
        AspectManager aspManager = DiagramUiPlugin.getDiagramAspectManager();
        MetamodelAspect someAspect = aspManager.getUmlAspect(eObj);
        
        if( someAspect instanceof UmlClassifier )
            type = UML_CLASSIFIER;
        else if( someAspect instanceof UmlPackage )
            type = UML_PACKAGE;
        else if( someAspect instanceof UmlProperty )
            type = UML_ATTRIBUTE;
        else if( someAspect instanceof UmlAssociation )
            type = UML_ASSOCIATION;
        else if( someAspect instanceof UmlOperation )
            type = UML_OPERATION;
        else if( someAspect instanceof UmlComment )
            type = UML_COMMENT;
        else if( someAspect instanceof UmlRelationship )
            type = UML_RELATIONSHIP;
        else if( someAspect instanceof UmlGeneralization )
            type = UML_GENERALIZATION;
        else if( someAspect instanceof UmlDiagramAspect )
            type = UML_DIAGRAM;
        
        if( type == UNKNOWN ) {
            if( eObj instanceof DiagramLink )
                type = UML_DIAGRAM_LINK;
            else if( eObj instanceof DiagramEntity ) {
                type = UML_DIAGRAM_ENTITY;
            }
        }

        return type;
    }

}
