/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.metadata.v810;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.metadata.v88.Test88MetadataValidator;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Test810MetadataValidator extends Test88MetadataValidator {

    protected Test810MetadataValidator(ITeiidServerVersion teiidVersion) {
        super(teiidVersion);
    }

    public Test810MetadataValidator() {
        this(Version.TEIID_8_10.get());
    }
}