/* Generated By:JJTree: Do not edit this line. WhileStatement.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.teiid.query.sql.lang.proc;

import org.teiid.designer.query.sql.proc.IWhileStatement;
import org.teiid.query.parser.LanguageVisitor;
import org.teiid.query.parser.TeiidParser;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.Labeled;

/**
 *
 */
public class WhileStatement extends Statement implements Labeled, IWhileStatement<LanguageVisitor> {

    private Block whileBlock;

    // criteria on the if block
    private Criteria condition;

    private String label;

    /**
     * @param p
     * @param id
     */
    public WhileStatement(TeiidParser p, int id) {
        super(p, id);
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Get the condition that determines which block needs to be executed.
     * @return The <code>Criteria</code> to determine block execution
     */
    public Criteria getCondition() {
        return condition;
    }
    
    /**
     * Set the condition that determines which block needs to be executed.
     * @param criteria The <code>Criteria</code> to determine block execution
     */
    public void setCondition(Criteria criteria) {
        this.condition = criteria;
    }
    
    /**
     * @return block
     */
    public Block getBlock() {
        return whileBlock;
    }

    /**
     * @param block
     */
    public void setBlock(Block block) {
        whileBlock = block;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.condition == null) ? 0 : this.condition.hashCode());
        result = prime * result + ((this.label == null) ? 0 : this.label.hashCode());
        result = prime * result + ((this.whileBlock == null) ? 0 : this.whileBlock.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        WhileStatement other = (WhileStatement)obj;
        if (this.condition == null) {
            if (other.condition != null) return false;
        } else if (!this.condition.equals(other.condition)) return false;
        if (this.label == null) {
            if (other.label != null) return false;
        } else if (!this.label.equals(other.label)) return false;
        if (this.whileBlock == null) {
            if (other.whileBlock != null) return false;
        } else if (!this.whileBlock.equals(other.whileBlock)) return false;
        return true;
    }

    /** Accept the visitor. **/
    @Override
    public void acceptVisitor(LanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public WhileStatement clone() {
        WhileStatement clone = new WhileStatement(this.parser, this.id);

        if(getCondition() != null)
            clone.setCondition(getCondition().clone());
        if(getLabel() != null)
            clone.setLabel(getLabel());
        if(getBlock() != null)
            clone.setBlock(getBlock().clone());

        return clone;
    }

}
/* JavaCC - OriginalChecksum=0d39f04cb283c52ac70af21f40941176 (do not edit this line) */