/* Generated By:JJTree: Do not edit this line. Alter.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.teiid.query.sql.lang.v7;

import org.teiid.query.parser.LanguageVisitor;
import org.teiid.query.parser.v7.Teiid7Parser;
import org.teiid.query.sql.lang.AlterProcedure;
import org.teiid.query.sql.lang.proc.CreateUpdateProcedureCommand;

/**
 * From Teiid Version 7, the AlterProcedure uses a CreateUpdateProcedureCommand generic
 * This is changed in version 8.
 */
public class Alter7Procedure extends AlterProcedure<CreateUpdateProcedureCommand> {

    /**
     * @param p
     * @param id
     */
    public Alter7Procedure(Teiid7Parser p, int id) {
        super(p, id);
    }

    @Override
    public int getType() {
        return TYPE_ALTER_PROC;
    }

    /** Accept the visitor. **/
    @Override
    public void acceptVisitor(LanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Alter7Procedure clone() {
        Alter7Procedure clone = new Alter7Procedure((Teiid7Parser) this.parser, this.id);

        if(getDefinition() != null)
            clone.setDefinition(getDefinition().clone());
        if(getTarget() != null)
            clone.setTarget(getTarget().clone());
        if(getSourceHint() != null)
            clone.setSourceHint(getSourceHint());
        if(getOption() != null)
            clone.setOption(getOption().clone());

        return clone;
    }

}
/* JavaCC - OriginalChecksum=4c2a7e700d4af2b1569d4947a1d82223 (do not edit this line) */