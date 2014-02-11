/* Generated By:JJTree: Do not edit this line. Delete.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.teiid.query.sql.lang;

import java.util.List;
import org.teiid.designer.query.sql.lang.IDelete;
import org.teiid.query.parser.LanguageVisitor;
import org.teiid.query.parser.TeiidParser;
import org.teiid.query.sql.lang.symbol.Expression;
import org.teiid.query.sql.lang.symbol.GroupSymbol;

/**
 *
 */
public class Delete extends Command
    implements IDelete<Criteria, GroupSymbol, Expression, LanguageVisitor>{

    /** Identifies the group to delete data from. */
    private GroupSymbol group;

    /** The criteria specifying constraints on what data will be deleted. */
    private Criteria criteria;

    /**
     * @param p
     * @param id
     */
    public Delete(TeiidParser p, int id) {
        super(p, id);
    }

    /**
     * Return type of command.
     * @return {@link Command#TYPE_DELETE}
     */
    @Override
    public int getType() {
        return TYPE_DELETE;
    }

    /**
     * Returns the group being deleted from
     * @return Group symbol
     */
    @Override
    public GroupSymbol getGroup() {
        return group;
    }

    /**
     * Set the group for this Delete command
     * @param group Group to be associated with this command
     */
    @Override
    public void setGroup(GroupSymbol group) {
        this.group = group;
    }

    /**
     * Returns the criteria object for this command.
     * @return criteria
     */
    @Override
    public Criteria getCriteria() {
        return this.criteria;
    }

    /**
     * Set the criteria for this Delete command
     * @param criteria Criteria to be associated with this command
     */
    @Override
    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    /**
     * Get the ordered list of all elements returned by this query.  These elements
     * may be ElementSymbols or ExpressionSymbols but in all cases each represents a 
     * single column.
     * @return Ordered list of SingleElementSymbol
     */
    @Override
    public List<Expression> getProjectedSymbols(){
        return getUpdateCommandSymbol();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.criteria == null) ? 0 : this.criteria.hashCode());
        result = prime * result + ((this.group == null) ? 0 : this.group.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        Delete other = (Delete)obj;
        if (this.criteria == null) {
            if (other.criteria != null) return false;
        } else if (!this.criteria.equals(other.criteria)) return false;
        if (this.group == null) {
            if (other.group != null) return false;
        } else if (!this.group.equals(other.group)) return false;
        return true;
    }

    /** Accept the visitor. **/
    @Override
    public void acceptVisitor(LanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Delete clone() {
        Delete clone = new Delete(this.parser, this.id);

        if(getCriteria() != null)
            clone.setCriteria(getCriteria().clone());
        if(getGroup() != null)
            clone.setGroup(getGroup().clone());
        if(getSourceHint() != null)
            clone.setSourceHint(getSourceHint());
        if(getOption() != null)
            clone.setOption(getOption().clone());

        return clone;
    }

}
/* JavaCC - OriginalChecksum=2f55ba189d89e0ff22bf3715234a54a4 (do not edit this line) */