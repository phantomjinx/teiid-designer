/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.query.sql.visitor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.core.util.StringUtil;
import org.teiid.designer.annotation.Removed;
import org.teiid.designer.annotation.Since;
import org.teiid.designer.query.sql.ISQLStringVisitor;
import org.teiid.designer.query.sql.symbol.IAggregateSymbol.Type;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.language.SQLConstants;
import org.teiid.language.SQLConstants.NonReserved;
import org.teiid.language.SQLConstants.Tokens;
import org.teiid.query.parser.LanguageVisitor;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.sql.lang.AlterProcedure;
import org.teiid.query.sql.lang.AlterTrigger;
import org.teiid.query.sql.lang.AlterView;
import org.teiid.query.sql.lang.ArrayTable;
import org.teiid.query.sql.lang.BetweenCriteria;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.lang.CompoundCriteria;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.CriteriaSelector;
import org.teiid.query.sql.lang.Delete;
import org.teiid.query.sql.lang.Drop;
import org.teiid.query.sql.lang.DynamicCommand;
import org.teiid.query.sql.lang.ElementSymbol;
import org.teiid.query.sql.lang.ExistsCriteria;
import org.teiid.query.sql.lang.ExpressionCriteria;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.FromClause;
import org.teiid.query.sql.lang.GroupBy;
import org.teiid.query.sql.lang.HasCriteria;
import org.teiid.query.sql.lang.Insert;
import org.teiid.query.sql.lang.Into;
import org.teiid.query.sql.lang.IsNullCriteria;
import org.teiid.query.sql.lang.JoinPredicate;
import org.teiid.query.sql.lang.JoinType;
import org.teiid.query.sql.lang.Labeled;
import org.teiid.query.sql.lang.LanguageObject;
import org.teiid.query.sql.lang.Limit;
import org.teiid.query.sql.lang.MatchCriteria;
import org.teiid.query.sql.lang.NamespaceItem;
import org.teiid.query.sql.lang.NotCriteria;
import org.teiid.query.sql.lang.ObjectColumn;
import org.teiid.query.sql.lang.ObjectTable;
import org.teiid.query.sql.lang.Option;
import org.teiid.query.sql.lang.OrderBy;
import org.teiid.query.sql.lang.OrderByItem;
import org.teiid.query.sql.lang.PredicateCriteria;
import org.teiid.query.sql.lang.ProjectedColumn;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.QueryCommand;
import org.teiid.query.sql.lang.SPParameter;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.lang.SetClause;
import org.teiid.query.sql.lang.SetClauseList;
import org.teiid.query.sql.lang.SetCriteria;
import org.teiid.query.sql.lang.SetQuery;
import org.teiid.query.sql.lang.SourceHint;
import org.teiid.query.sql.lang.SourceHint.SpecificHint;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.lang.SubqueryCompareCriteria;
import org.teiid.query.sql.lang.SubqueryFromClause;
import org.teiid.query.sql.lang.SubqueryHint;
import org.teiid.query.sql.lang.SubquerySetCriteria;
import org.teiid.query.sql.lang.TextColumn;
import org.teiid.query.sql.lang.TextTable;
import org.teiid.query.sql.lang.TranslateCriteria;
import org.teiid.query.sql.lang.UnaryFromClause;
import org.teiid.query.sql.lang.Update;
import org.teiid.query.sql.lang.WithQueryCommand;
import org.teiid.query.sql.lang.XMLTable;
import org.teiid.query.sql.lang.proc.AssignmentStatement;
import org.teiid.query.sql.lang.proc.Block;
import org.teiid.query.sql.lang.proc.BranchingStatement;
import org.teiid.query.sql.lang.proc.CommandStatement;
import org.teiid.query.sql.lang.proc.CreateProcedureCommand;
import org.teiid.query.sql.lang.proc.CreateUpdateProcedureCommand;
import org.teiid.query.sql.lang.proc.DeclareStatement;
import org.teiid.query.sql.lang.proc.ExceptionExpression;
import org.teiid.query.sql.lang.proc.IfStatement;
import org.teiid.query.sql.lang.proc.LoopStatement;
import org.teiid.query.sql.lang.proc.RaiseErrorStatement;
import org.teiid.query.sql.lang.proc.RaiseStatement;
import org.teiid.query.sql.lang.proc.ReturnStatement;
import org.teiid.query.sql.lang.proc.Statement;
import org.teiid.query.sql.lang.proc.TriggerAction;
import org.teiid.query.sql.lang.proc.WhileStatement;
import org.teiid.query.sql.lang.symbol.AggregateSymbol;
import org.teiid.query.sql.lang.symbol.AliasSymbol;
import org.teiid.query.sql.lang.symbol.Array;
import org.teiid.query.sql.lang.symbol.CaseExpression;
import org.teiid.query.sql.lang.symbol.Constant;
import org.teiid.query.sql.lang.symbol.DerivedColumn;
import org.teiid.query.sql.lang.symbol.Expression;
import org.teiid.query.sql.lang.symbol.ExpressionSymbol;
import org.teiid.query.sql.lang.symbol.Function;
import org.teiid.query.sql.lang.symbol.GroupSymbol;
import org.teiid.query.sql.lang.symbol.JSONObject;
import org.teiid.query.sql.lang.symbol.MultipleElementSymbol;
import org.teiid.query.sql.lang.symbol.QueryString;
import org.teiid.query.sql.lang.symbol.Reference;
import org.teiid.query.sql.lang.symbol.ScalarSubquery;
import org.teiid.query.sql.lang.symbol.SearchedCaseExpression;
import org.teiid.query.sql.lang.symbol.Symbol;
import org.teiid.query.sql.lang.symbol.TextLine;
import org.teiid.query.sql.lang.symbol.WindowFunction;
import org.teiid.query.sql.lang.symbol.WindowSpecification;
import org.teiid.query.sql.lang.symbol.XMLAttributes;
import org.teiid.query.sql.lang.symbol.XMLColumn;
import org.teiid.query.sql.lang.symbol.XMLElement;
import org.teiid.query.sql.lang.symbol.XMLForest;
import org.teiid.query.sql.lang.symbol.XMLNamespaces;
import org.teiid.query.sql.lang.symbol.XMLParse;
import org.teiid.query.sql.lang.symbol.XMLQuery;
import org.teiid.query.sql.lang.symbol.XMLSerialize;
import org.teiid.translator.SourceSystemFunctions;

/**
 * <p>
 * The SQLStringVisitor will visit a set of ast nodes and return the corresponding SQL string representation.
 * </p>
 */
public class SQLStringVisitor extends LanguageVisitor
    implements SQLConstants.Reserved, SQLConstants.Tokens, ISQLStringVisitor<LanguageObject> {

    /**
     * Undefined
     */
    public static final String UNDEFINED = "<undefined>"; //$NON-NLS-1$

    private static final TeiidServerVersion TEIID_VERSION_8 = new TeiidServerVersion("8.0.0"); //$NON-NLS-1$

    private static final String BEGIN_HINT = "/*+"; //$NON-NLS-1$

    private static final String END_HINT = "*/"; //$NON-NLS-1$

    private static final char ID_ESCAPE_CHAR = '\"';

    protected StringBuilder parts = new StringBuilder();

    private boolean shortNameOnly = false;

    /**
     * @param teiidVersion
     */
    public SQLStringVisitor(ITeiidServerVersion teiidVersion) {
        super(teiidVersion.getMaximumVersion());
    }

    /**
     * Helper to quickly get the parser string for an object using the visitor.
     *
     * @param teiidVersion
     * @param obj Language object
     *
     * @return String SQL String for obj
     */
    public static final String getSQLString(ITeiidServerVersion teiidVersion, LanguageObject obj) {
        SQLStringVisitor visitor = new SQLStringVisitor(teiidVersion);
        return visitor.returnSQLString(obj);
    }

    /**
     * @param languageObject
     * @return sql representation of {@link LanguageObject}
     */
    @Override
    public String returnSQLString(LanguageObject languageObject) {
        if (languageObject == null) {
            return UNDEFINED;
        }

        isApplicable(languageObject);
        languageObject.acceptVisitor(this);
        return getSQLString();
    }

    /**
     * Retrieve completed string from the visitor.
     *
     * @return Complete SQL string for the visited nodes
     */
    public String getSQLString() {
        return this.parts.toString();
    }

    protected boolean isTeiid8OrGreater() {
        return teiidVersion.equals(TEIID_VERSION_8) || teiidVersion.isGreaterThan(TEIID_VERSION_8);
    }

    protected void visitNode(LanguageObject obj) {
        if (obj == null) {
            append(UNDEFINED);
            return;
        }
        isApplicable(obj);
        obj.acceptVisitor(this);
    }

    protected void append(Object value) {
        this.parts.append(value);
    }

    protected void beginClause(int level) {
        append(SPACE);
    }

    private Constant newConstant(Object value) {
        Constant constant = createNode(ASTNodes.CONSTANT);
        constant.setValue(value);
        return constant;
    }

    // ############ Visitor methods for language objects ####################

    @Override
    public void visit(BetweenCriteria obj) {
        visitNode(obj.getExpression());
        append(SPACE);

        if (obj.isNegated()) {
            append(NOT);
            append(SPACE);
        }
        append(BETWEEN);
        append(SPACE);
        visitNode(obj.getLowerExpression());

        append(SPACE);
        append(AND);
        append(SPACE);
        visitNode(obj.getUpperExpression());
    }

    @Override
    public void visit(CaseExpression obj) {
        append(CASE);
        append(SPACE);
        visitNode(obj.getExpression());
        append(SPACE);

        for (int i = 0; i < obj.getWhenCount(); i++) {
            append(WHEN);
            append(SPACE);
            visitNode(obj.getWhenExpression(i));
            append(SPACE);
            append(THEN);
            append(SPACE);
            visitNode(obj.getThenExpression(i));
            append(SPACE);
        }

        if (obj.getElseExpression() != null) {
            append(ELSE);
            append(SPACE);
            visitNode(obj.getElseExpression());
            append(SPACE);
        }
        append(END);
    }

    @Override
    public void visit(CompareCriteria obj) {
        Expression leftExpression = obj.getLeftExpression();
        visitNode(leftExpression);
        append(SPACE);
        append(obj.getOperatorAsString());
        append(SPACE);
        Expression rightExpression = obj.getRightExpression();
        visitNode(rightExpression);
    }

    @Override
    public void visit(CompoundCriteria obj) {
        // Get operator string
        int operator = obj.getOperator();
        String operatorStr = ""; //$NON-NLS-1$
        if (operator == CompoundCriteria.AND) {
            operatorStr = AND;
        } else if (operator == CompoundCriteria.OR) {
            operatorStr = OR;
        }

        // Get criteria
        List<Criteria> subCriteria = obj.getCriteria();

        // Build parts
        if (subCriteria.size() == 1) {
            // Special case - should really never happen, but we are tolerant
            Criteria firstChild = subCriteria.get(0);
            visitNode(firstChild);
        } else {
            // Add first criteria
            Iterator<Criteria> iter = subCriteria.iterator();

            while (iter.hasNext()) {
                // Add criteria
                Criteria crit = iter.next();
                append(Tokens.LPAREN);
                visitNode(crit);
                append(Tokens.RPAREN);

                if (iter.hasNext()) {
                    // Add connector
                    append(SPACE);
                    append(operatorStr);
                    append(SPACE);
                }
            }
        }
    }

    @Override
    public void visit(Delete obj) {
        // add delete clause
        append(DELETE);
        addSourceHint(obj.getSourceHint());
        append(SPACE);
        // add from clause
        append(FROM);
        append(SPACE);
        visitNode(obj.getGroup());

        // add where clause
        if (obj.getCriteria() != null) {
            beginClause(0);
            visitCriteria(WHERE, obj.getCriteria());
        }

        // Option clause
        if (obj.getOption() != null) {
            beginClause(0);
            visitNode(obj.getOption());
        }
    }

    @Override
    public void visit(From obj) {
        append(FROM);
        beginClause(1);
        registerNodes(obj.getClauses(), 0);
    }

    @Override
    public void visit(GroupBy obj) {
        append(GROUP);
        append(SPACE);
        append(BY);
        append(SPACE);
        registerNodes(obj.getSymbols(), 0);
    }

    @Override
    public void visit(Insert obj) {
        if (isTeiid8OrGreater() && obj.isMerge()) {
            append(MERGE);
        } else {
            append(INSERT);
        }
        addSourceHint(obj.getSourceHint());
        append(SPACE);
        append(INTO);
        append(SPACE);
        visitNode(obj.getGroup());

        if (!obj.getVariables().isEmpty()) {
            beginClause(2);

            // Columns clause
            List<ElementSymbol> vars = obj.getVariables();
            if (vars != null) {
                append("("); //$NON-NLS-1$
                this.shortNameOnly = true;
                registerNodes(vars, 0);
                this.shortNameOnly = false;
                append(")"); //$NON-NLS-1$
            }
        }
        beginClause(1);
        if (obj.getQueryExpression() != null) {
            visitNode(obj.getQueryExpression());
            //         } else if (obj.getTupleSource() != null) {
            //             append(VALUES);
            //             append(" (...)"); //$NON-NLS-1$
        } else if (obj.getValues() != null) {
            append(VALUES);
            beginClause(2);
            append("("); //$NON-NLS-1$
            registerNodes(obj.getValues(), 0);
            append(")"); //$NON-NLS-1$
        }

        // Option clause
        if (obj.getOption() != null) {
            beginClause(1);
            visitNode(obj.getOption());
        }
    }

    @Override
    public void visit(Drop obj) {
        append(DROP);
        append(SPACE);
        append(TABLE);
        append(SPACE);
        visitNode(obj.getTable());
    }

    @Override
    public void visit(IsNullCriteria obj) {
        Expression expr = obj.getExpression();
        if (isTeiid8OrGreater())
            appendNested(expr);
        else
            visitNode(expr);

        append(SPACE);
        append(IS);
        append(SPACE);
        if (obj.isNegated()) {
            append(NOT);
            append(SPACE);
        }
        append(NULL);
    }

    @Override
    public void visit(JoinPredicate obj) {
        addHintComment(obj);

        if (obj.hasHint()) {
            append("(");//$NON-NLS-1$
        }

        // left clause
        FromClause leftClause = obj.getLeftClause();
        if (leftClause instanceof JoinPredicate && !((JoinPredicate)leftClause).hasHint()) {
            append("("); //$NON-NLS-1$
            visitNode(leftClause);
            append(")"); //$NON-NLS-1$
        } else {
            visitNode(leftClause);
        }

        // join type
        append(SPACE);
        visitNode(obj.getJoinType());
        append(SPACE);

        // right clause
        FromClause rightClause = obj.getRightClause();
        if (rightClause instanceof JoinPredicate && !((JoinPredicate)rightClause).hasHint()) {
            append("("); //$NON-NLS-1$
            visitNode(rightClause);
            append(")"); //$NON-NLS-1$
        } else {
            visitNode(rightClause);
        }

        // join criteria
        List joinCriteria = obj.getJoinCriteria();
        if (joinCriteria != null && joinCriteria.size() > 0) {
            append(SPACE);
            append(ON);
            append(SPACE);
            Iterator critIter = joinCriteria.iterator();
            while (critIter.hasNext()) {
                Criteria crit = (Criteria)critIter.next();
                if (crit instanceof PredicateCriteria || crit instanceof NotCriteria) {
                    visitNode(crit);
                } else {
                    append("("); //$NON-NLS-1$
                    visitNode(crit);
                    append(")"); //$NON-NLS-1$
                }

                if (critIter.hasNext()) {
                    append(SPACE);
                    append(AND);
                    append(SPACE);
                }
            }
        }

        if (obj.hasHint()) {
            append(")"); //$NON-NLS-1$
        }
    }

    private void addHintComment(FromClause obj) {
        if (obj.hasHint()) {
            append(BEGIN_HINT);
            append(SPACE);
            if (obj.isOptional()) {
                append(Option.OPTIONAL);
                append(SPACE);
            }
            if (obj.isMakeDep()) {
                append(Option.MAKEDEP);
                append(SPACE);
            }
            if (obj.isMakeNotDep()) {
                append(Option.MAKENOTDEP);
                append(SPACE);
            }
            if (obj.isMakeInd()) {
                append(FromClause.MAKEIND);
                append(SPACE);
            }
            if (obj.isNoUnnest()) {
                append(SubqueryHint.NOUNNEST);
                append(SPACE);
            }

            if (isTeiid8OrGreater() && obj.isPreserve()) {
                append(FromClause.PRESERVE);
                append(SPACE);
            }
            append(END_HINT);
            append(SPACE);
        }
    }

    @Override
    public void visit(JoinType obj) {
        String[] output = null;
        switch (obj.getKind()) {
            case JOIN_ANTI_SEMI:
                output = new String[] {"ANTI SEMI", SPACE, JOIN}; //$NON-NLS-1$
                break;
            case JOIN_CROSS:
                output = new String[] {CROSS, SPACE, JOIN};
                break;
            case JOIN_FULL_OUTER:
                output = new String[] {FULL, SPACE, OUTER, SPACE, JOIN};
                break;
            case JOIN_INNER:
                output = new String[] {INNER, SPACE, JOIN};
                break;
            case JOIN_LEFT_OUTER:
                output = new String[] {LEFT, SPACE, OUTER, SPACE, JOIN};
                break;
            case JOIN_RIGHT_OUTER:
                output = new String[] {RIGHT, SPACE, OUTER, SPACE, JOIN};
                break;
            case JOIN_SEMI:
                output = new String[] {"SEMI", SPACE, JOIN}; //$NON-NLS-1$
                break;
            case JOIN_UNION:
                output = new String[] {UNION, SPACE, JOIN};
                break;
            default:
                throw new AssertionError();
        }

        for (String part : output) {
            append(part);
        }
    }

    @Override
    public void visit(MatchCriteria obj) {
        visitNode(obj.getLeftExpression());

        append(SPACE);
        if (obj.isNegated()) {
            append(NOT);
            append(SPACE);
        }
        switch (obj.getMode()) {
            case SIMILAR:
                append(SIMILAR);
                append(SPACE);
                append(TO);
                break;
            case LIKE:
                append(LIKE);
                break;
            case REGEX:
                append(LIKE_REGEX);
                break;
        }
        append(SPACE);

        visitNode(obj.getRightExpression());

        if (obj.getEscapeChar() != MatchCriteria.NULL_ESCAPE_CHAR) {
            append(SPACE);
            append(ESCAPE);
            if (isTeiid8OrGreater()) {
                append(SPACE);
                outputLiteral(String.class, false, obj.getEscapeChar());
            } else {
                append(" '"); //$NON-NLS-1$
                append(String.valueOf(obj.getEscapeChar()));
                append("'"); //$NON-NLS-1$
            }
        }
    }

    @Override
    public void visit(NotCriteria obj) {
        append(NOT);
        append(" ("); //$NON-NLS-1$
        visitNode(obj.getCriteria());
        append(")"); //$NON-NLS-1$
    }

    @Override
    public void visit(Option obj) {
        append(OPTION);

        Collection<String> groups = obj.getDependentGroups();
        if (groups != null && groups.size() > 0) {
            append(" "); //$NON-NLS-1$
            append(MAKEDEP);
            append(" "); //$NON-NLS-1$

            Iterator<String> iter = groups.iterator();

            while (iter.hasNext()) {
                outputDisplayName(iter.next());

                if (iter.hasNext()) {
                    append(", ");//$NON-NLS-1$
                }
            }
        }

        groups = obj.getNotDependentGroups();
        if (groups != null && groups.size() > 0) {
            append(" "); //$NON-NLS-1$
            append(MAKENOTDEP);
            append(" "); //$NON-NLS-1$

            Iterator<String> iter = groups.iterator();

            while (iter.hasNext()) {
                outputDisplayName(iter.next());

                if (iter.hasNext()) {
                    append(", ");//$NON-NLS-1$
                }
            }
        }

        groups = obj.getNoCacheGroups();
        if (groups != null && groups.size() > 0) {
            append(" "); //$NON-NLS-1$
            append(NOCACHE);
            append(" "); //$NON-NLS-1$

            Iterator<String> iter = groups.iterator();

            while (iter.hasNext()) {
                outputDisplayName(iter.next());

                if (iter.hasNext()) {
                    append(", ");//$NON-NLS-1$
                }
            }
        } else if (obj.isNoCache()) {
            append(" "); //$NON-NLS-1$
            append(NOCACHE);
        }

    }

    @Override
    public void visit(OrderBy obj) {
        append(ORDER);
        append(SPACE);
        append(BY);
        append(SPACE);
        registerNodes(obj.getOrderByItems(), 0);
    }

    @Override
    public void visit(OrderByItem obj) {
        Expression ses = obj.getSymbol();
        if (ses instanceof AliasSymbol) {
            AliasSymbol as = (AliasSymbol)ses;
            outputDisplayName(as.getOutputName());
        } else {
            visitNode(ses);
        }
        if (!obj.isAscending()) {
            append(SPACE);
            append(DESC);
        } // Don't print default "ASC"
        if (obj.getNullOrdering() != null) {
            append(SPACE);
            append(NonReserved.NULLS);
            append(SPACE);
            append(obj.getNullOrdering().name());
        }
    }

    @Override
    public void visit(DynamicCommand obj) {
        append(EXECUTE);
        append(SPACE);
        append(IMMEDIATE);
        append(SPACE);
        visitNode(obj.getSql());

        if (obj.isAsClauseSet()) {
            beginClause(1);
            append(AS);
            append(SPACE);
            for (int i = 0; i < obj.getAsColumns().size(); i++) {
                ElementSymbol symbol = (ElementSymbol)obj.getAsColumns().get(i);
                outputShortName(symbol);
                append(SPACE);
                append(DataTypeManagerService.getInstance().getDataTypeName(symbol.getType()));
                if (i < obj.getAsColumns().size() - 1) {
                    append(", "); //$NON-NLS-1$
                }
            }
        }

        if (obj.getIntoGroup() != null) {
            beginClause(1);
            append(INTO);
            append(SPACE);
            visitNode(obj.getIntoGroup());
        }

        if (obj.getUsing() != null && !obj.getUsing().isEmpty()) {
            beginClause(1);
            append(USING);
            append(SPACE);
            visitNode(obj.getUsing());
        }

        if (obj.getUpdatingModelCount() > 0) {
            beginClause(1);
            append(UPDATE);
            append(SPACE);
            if (obj.getUpdatingModelCount() > 1) {
                append("*"); //$NON-NLS-1$
            } else {
                append("1"); //$NON-NLS-1$
            }
        }
    }

    @Override
    public void visit(SetClauseList obj) {
        for (Iterator<SetClause> iterator = obj.getClauses().iterator(); iterator.hasNext();) {
            SetClause clause = iterator.next();
            visitNode(clause);
            if (iterator.hasNext()) {
                append(", "); //$NON-NLS-1$
            }
        }
    }

    @Override
    public void visit(SetClause obj) {
        ElementSymbol symbol = obj.getSymbol();
        outputShortName(symbol);
        append(" = "); //$NON-NLS-1$
        visitNode(obj.getValue());
    }

    @Override
    public void visit(WithQueryCommand obj) {
        visitNode(obj.getGroupSymbol());
        append(SPACE);
        if (obj.getColumns() != null && !obj.getColumns().isEmpty()) {
            append(Tokens.LPAREN);
            shortNameOnly = true;
            registerNodes(obj.getColumns(), 0);
            shortNameOnly = false;
            append(Tokens.RPAREN);
            append(SPACE);
        }
        append(AS);
        append(SPACE);
        append(Tokens.LPAREN);
        visitNode(obj.getQueryExpression());
        append(Tokens.RPAREN);
    }

    @Override
    public void visit(Query obj) {
        addWithClause(obj);
        append(SELECT);

        SourceHint sh = obj.getSourceHint();
        addSourceHint(sh);
        if (obj.getSelect() != null) {
            visitNode(obj.getSelect());
        }

        if (obj.getInto() != null) {
            beginClause(1);
            visitNode(obj.getInto());
        }

        if (obj.getFrom() != null) {
            beginClause(1);
            visitNode(obj.getFrom());
        }

        // Where clause
        if (obj.getCriteria() != null) {
            beginClause(1);
            visitCriteria(WHERE, obj.getCriteria());
        }

        // Group by clause
        if (obj.getGroupBy() != null) {
            beginClause(1);
            visitNode(obj.getGroupBy());
        }

        // Having clause
        if (obj.getHaving() != null) {
            beginClause(1);
            visitCriteria(HAVING, obj.getHaving());
        }

        // Order by clause
        if (obj.getOrderBy() != null) {
            beginClause(1);
            visitNode(obj.getOrderBy());
        }

        if (obj.getLimit() != null) {
            beginClause(1);
            visitNode(obj.getLimit());
        }

        // Option clause
        if (obj.getOption() != null) {
            beginClause(1);
            visitNode(obj.getOption());
        }
    }

    private void addSourceHint(SourceHint sh) {
        if (sh != null) {
            append(SPACE);
            append(BEGIN_HINT);
            append("sh"); //$NON-NLS-1$

            if (isTeiid8OrGreater() && sh.isUseAliases()) {
                append(SPACE);
                append("KEEP ALIASES"); //$NON-NLS-1$
            }

            if (sh.getGeneralHint() != null) {
                appendSourceHintValue(sh.getGeneralHint());
            }
            if (sh.getSpecificHints() != null) {
                for (Map.Entry<String, SpecificHint> entry : sh.getSpecificHints().entrySet()) {
                    append(entry.getKey());
                    if (isTeiid8OrGreater() && entry.getValue().isUseAliases()) {
                        append(SPACE);
                        append("KEEP ALIASES"); //$NON-NLS-1$
                    }

                    appendSourceHintValue(entry.getValue().getHint());
                }
            }
            append(END_HINT);
        }
    }

    private void addWithClause(QueryCommand obj) {
        if (obj.getWith() != null) {
            append(WITH);
            append(SPACE);
            registerNodes(obj.getWith(), 0);
            beginClause(0);
        }
    }

    protected void visitCriteria(String keyWord, Criteria crit) {
        append(keyWord);
        append(SPACE);
        visitNode(crit);
    }

    @Override
    public void visit(SearchedCaseExpression obj) {
        append(CASE);
        for (int i = 0; i < obj.getWhenCount(); i++) {
            append(SPACE);
            append(WHEN);
            append(SPACE);
            visitNode(obj.getWhenCriteria(i));
            append(SPACE);
            append(THEN);
            append(SPACE);
            visitNode(obj.getThenExpression(i));
        }
        append(SPACE);
        if (obj.getElseExpression() != null) {
            append(ELSE);
            append(SPACE);
            visitNode(obj.getElseExpression());
            append(SPACE);
        }
        append(END);
    }

    @Override
    public void visit(Select obj) {
        if (obj.isDistinct()) {
            append(SPACE);
            append(DISTINCT);
        }
        beginClause(2);

        Iterator<Expression> iter = obj.getSymbols().iterator();
        while (iter.hasNext()) {
            Expression symbol = iter.next();
            visitNode(symbol);
            if (iter.hasNext()) {
                append(", "); //$NON-NLS-1$
            }
        }
    }

    private void appendSourceHintValue(String sh) {
        append(Tokens.COLON);
        append('\'');
        append(escapeStringValue(sh, "'")); //$NON-NLS-1$
        append('\'');
        append(SPACE);
    }

    @Override
    public void visit(SetCriteria obj) {
        // variable

        if (isTeiid8OrGreater())
            appendNested(obj.getExpression());
        else
            visitNode(obj.getExpression());

        // operator and beginning of list
        append(SPACE);
        if (obj.isNegated()) {
            append(NOT);
            append(SPACE);
        }
        append(IN);
        append(" ("); //$NON-NLS-1$

        // value list
        Collection vals = obj.getValues();
        int size = vals.size();
        if (size == 1) {
            Iterator iter = vals.iterator();
            Expression expr = (Expression)iter.next();
            visitNode(expr);
        } else if (size > 1) {
            Iterator iter = vals.iterator();
            Expression expr = (Expression)iter.next();
            visitNode(expr);
            while (iter.hasNext()) {
                expr = (Expression)iter.next();
                append(", "); //$NON-NLS-1$
                visitNode(expr);
            }
        }
        append(")"); //$NON-NLS-1$
    }

    /**
     * Condition operators have lower precedence than LIKE/SIMILAR/IS
     * @param ex
     */
    @Since( "8.0.0" )
    private void appendNested(Expression ex) {
        boolean useParens = ex instanceof Criteria;
        if (useParens) {
            append(Tokens.LPAREN);
        }
        visitNode(ex);
        if (useParens) {
            append(Tokens.RPAREN);
        }
    }

    @Override
    public void visit(SetQuery obj) {
        addWithClause(obj);
        QueryCommand query = obj.getLeftQuery();
        appendSetQuery(obj, query, false);

        beginClause(0);
        append(obj.getOperation());

        if (obj.isAll()) {
            append(SPACE);
            append(ALL);
        }
        beginClause(0);
        query = obj.getRightQuery();
        appendSetQuery(obj, query, true);

        if (obj.getOrderBy() != null) {
            beginClause(0);
            visitNode(obj.getOrderBy());
        }

        if (obj.getLimit() != null) {
            beginClause(0);
            visitNode(obj.getLimit());
        }

        if (obj.getOption() != null) {
            beginClause(0);
            visitNode(obj.getOption());
        }
    }

    protected void appendSetQuery(SetQuery parent, QueryCommand obj, boolean right) {
        if (obj.getLimit() != null
            || obj.getOrderBy() != null
            || (right && ((obj instanceof SetQuery && ((parent.isAll() && !((SetQuery)obj).isAll()) || parent.getOperation() != ((SetQuery)obj).getOperation()))))) {
            append(Tokens.LPAREN);
            visitNode(obj);
            append(Tokens.RPAREN);
        } else {
            visitNode(obj);
        }
    }

    @Override
    public void visit(StoredProcedure obj) {
        if (obj.isCalledWithReturn()) {
            for (SPParameter param : obj.getParameters()) {
                if (param.getParameterType() == SPParameter.RETURN_VALUE) {
                    if (param.getExpression() == null) {
                        append("?"); //$NON-NLS-1$
                    } else {
                        visitNode(param.getExpression());
                    }
                }
            }
            append(SPACE);
            append(Tokens.EQ);
            append(SPACE);
        }
        // exec clause
        append(EXEC);
        append(SPACE);
        append(obj.getProcedureName());
        append("("); //$NON-NLS-1$
        boolean first = true;
        for (SPParameter param : obj.getParameters()) {
            if (param.isUsingDefault() || param.getParameterType() == SPParameter.RETURN_VALUE
                || param.getParameterType() == SPParameter.RESULT_SET || param.getExpression() == null) {
                continue;
            }
            if (first) {
                first = false;
            } else {
                append(", "); //$NON-NLS-1$
            }
            if (obj.isDisplayNamedParameters()) {
                append(escapeSinglePart(Symbol.getShortName(param.getParameterSymbol().getOutputName())));
                append(" => "); //$NON-NLS-1$
            }

            boolean addParens = !obj.isDisplayNamedParameters() && param.getExpression() instanceof CompareCriteria;
            if (addParens) {
                append(Tokens.LPAREN);
            }
            visitNode(param.getExpression());
            if (addParens) {
                append(Tokens.RPAREN);
            }
        }
        append(")"); //$NON-NLS-1$

        // Option clause
        if (obj.getOption() != null) {
            beginClause(1);
            visitNode(obj.getOption());
        }
    }

    @Override
    public void visit(SubqueryFromClause obj) {
        addHintComment(obj);
        if (obj.isTable()) {
            append(TABLE);
        }
        append("(");//$NON-NLS-1$
        visitNode(obj.getCommand());
        append(")");//$NON-NLS-1$
        append(" AS ");//$NON-NLS-1$

        GroupSymbol groupSymbol = obj.getGroupSymbol();
        if (isTeiid8OrGreater())
            append(escapeSinglePart(groupSymbol.getOutputName()));
        else
            append(groupSymbol.getOutputName());
    }

    @Override
    public void visit(SubquerySetCriteria obj) {
        // variable
        visitNode(obj.getExpression());

        // operator and beginning of list
        append(SPACE);
        if (obj.isNegated()) {
            append(NOT);
            append(SPACE);
        }
        append(IN);
        addSubqueryHint(obj.getSubqueryHint());
        append(" ("); //$NON-NLS-1$
        visitNode(obj.getCommand());
        append(")"); //$NON-NLS-1$
    }

    @Override
    public void visit(UnaryFromClause obj) {
        addHintComment(obj);
        visitNode(obj.getGroup());
    }

    @Override
    public void visit(Update obj) {
        // Update clause
        append(UPDATE);
        addSourceHint(obj.getSourceHint());
        append(SPACE);
        visitNode(obj.getGroup());
        beginClause(1);
        // Set clause
        append(SET);
        beginClause(2);
        visitNode(obj.getChangeList());

        // Where clause
        if (obj.getCriteria() != null) {
            beginClause(1);
            visitCriteria(WHERE, obj.getCriteria());
        }

        // Option clause
        if (obj.getOption() != null) {
            beginClause(1);
            visitNode(obj.getOption());
        }
    }

    @Override
    public void visit(Into obj) {
        append(INTO);
        append(SPACE);
        visitNode(obj.getGroup());
    }

    // ############ Visitor methods for symbol objects ####################

    @Override
    public void visit(AggregateSymbol obj) {
        if (isTeiid8OrGreater())
            append(obj.getName());
        else
            append(obj.getAggregateFunction().name());
        append("("); //$NON-NLS-1$

        if (obj.isDistinct()) {
            append(DISTINCT);
            append(" "); //$NON-NLS-1$
        } else if (isTeiid8OrGreater() && obj.getAggregateFunction() == Type.USER_DEFINED) {
            append(ALL);
            append(" "); //$NON-NLS-1$
        }

        if ((!isTeiid8OrGreater() && obj.getExpression() == null) ||
             (isTeiid8OrGreater() && (obj.getArgs() == null ||  obj.getArgs().length == 0))) {
            if (obj.getAggregateFunction() == Type.COUNT) {
                append(Tokens.ALL_COLS);
            }
        } else if (isTeiid8OrGreater()) {
            registerNodes(obj.getArgs(), 0);
        } else {
            visitNode(obj.getExpression());
        }

        if (obj.getOrderBy() != null) {
            append(SPACE);
            visitNode(obj.getOrderBy());
        }
        append(")"); //$NON-NLS-1$

        if (obj.getCondition() != null) {
            append(SPACE);
            append(FILTER);
            append(Tokens.LPAREN);
            append(WHERE);
            append(SPACE);
            append(obj.getCondition());
            append(Tokens.RPAREN);
        }
    }

    @Override
    public void visit(AliasSymbol obj) {
        visitNode(obj.getSymbol());
        append(SPACE);
        append(AS);
        append(SPACE);
        append(escapeSinglePart(obj.getOutputName()));
    }

    @Override
    public void visit(MultipleElementSymbol obj) {
        if (obj.getGroup() == null) {
            append(Tokens.ALL_COLS);
        } else {
            visitNode(obj.getGroup());
            append(Tokens.DOT);
            append(Tokens.ALL_COLS);
        }
    }

    private void visit7(Constant obj) {
        Class<?> type = obj.getType();
        String[] constantParts = null;
        if (obj.isMultiValued()) {
            constantParts = new String[] {"?"}; //$NON-NLS-1$
        } else if (obj.getValue() == null) {
            if (type.equals(DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass())) {
                constantParts = new String[] {UNKNOWN};
            } else {
                constantParts = new String[] {"null"}; //$NON-NLS-1$
            }
        } else {
            if (Number.class.isAssignableFrom(type)) {
                constantParts = new String[] {obj.getValue().toString()};
            } else if (type.equals(DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass())) {
                constantParts = new String[] {obj.getValue().equals(Boolean.TRUE) ? TRUE : FALSE};
            } else if (type.equals(DataTypeManagerService.DefaultDataTypes.TIMESTAMP.getTypeClass())) {
                constantParts = new String[] {"{ts'", obj.getValue().toString(), "'}"}; //$NON-NLS-1$ //$NON-NLS-2$
            } else if (type.equals(DataTypeManagerService.DefaultDataTypes.TIME.getTypeClass())) {
                constantParts = new String[] {"{t'", obj.getValue().toString(), "'}"}; //$NON-NLS-1$ //$NON-NLS-2$
            } else if (type.equals(DataTypeManagerService.DefaultDataTypes.DATE.getTypeClass())) {
                constantParts = new String[] {"{d'", obj.getValue().toString(), "'}"}; //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (constantParts == null) {
                String strValue = obj.getValue().toString();
                strValue = escapeStringValue(strValue, "'"); //$NON-NLS-1$
                constantParts = new String[] {"'", strValue, "'"}; //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        for (String string : constantParts) {
            append(string);
        }
    }

    private void outputLiteral(Class<?> type, boolean multiValued, Object value) throws AssertionError {
        String[] constantParts = null;
        if (multiValued) {
            constantParts = new String[] {"?"}; //$NON-NLS-1$
        } else if (value == null) {
            if (type.equals(DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass())) {
                constantParts = new String[] {UNKNOWN};
            } else {
                constantParts = new String[] {"null"}; //$NON-NLS-1$
            }
        } else {
            if (Number.class.isAssignableFrom(type)) {
                constantParts = new String[] {value.toString()};
            } else if (type.equals(DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass())) {
                constantParts = new String[] {value.equals(Boolean.TRUE) ? TRUE : FALSE};
            } else if (type.equals(DataTypeManagerService.DefaultDataTypes.TIMESTAMP.getTypeClass())) {
                constantParts = new String[] {"{ts'", value.toString(), "'}"}; //$NON-NLS-1$ //$NON-NLS-2$
            } else if (type.equals(DataTypeManagerService.DefaultDataTypes.TIME.getTypeClass())) {
                constantParts = new String[] {"{t'", value.toString(), "'}"}; //$NON-NLS-1$ //$NON-NLS-2$
            } else if (type.equals(DataTypeManagerService.DefaultDataTypes.DATE.getTypeClass())) {
                constantParts = new String[] {"{d'", value.toString(), "'}"}; //$NON-NLS-1$ //$NON-NLS-2$
            } else if (type.equals(DataTypeManagerService.DefaultDataTypes.VARBINARY.getTypeClass())) {
                constantParts = new String[] {"X'", value.toString(), "'"}; //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (constantParts == null) {
                if (isTeiid8OrGreater() && DataTypeManagerService.DefaultDataTypes.isLOB(type)) {
                    constantParts = new String[] {"?"}; //$NON-NLS-1$
                } else {
                    String strValue = value.toString();
                    strValue = escapeStringValue(strValue, "'"); //$NON-NLS-1$
                    constantParts = new String[] {"'", strValue, "'"}; //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }

        for (String string : constantParts) {
            append(string);
        }
    }

    private void visit8(Constant obj) {
        Class<?> type = obj.getType();
        boolean multiValued = obj.isMultiValued();
        Object value = obj.getValue();
        outputLiteral(type, multiValued, value);
    }

    @Override
    public void visit(Constant obj) {
        if (isTeiid8OrGreater())
            visit8(obj);
        else
            visit7(obj);
    }

    /**
     * Take a string literal and escape it as necessary. By default, this converts ' to ''.
     * 
     * @param str String literal value (unquoted), never null
     * @return Escaped string literal value
     */
    static String escapeStringValue(String str, String tick) {
        return StringUtil.replaceAll(str, tick, tick + tick);
    }

    @Override
    public void visit(ElementSymbol obj) {
        if (obj.getDisplayMode().equals(ElementSymbol.DisplayMode.SHORT_OUTPUT_NAME) || shortNameOnly) {
            outputShortName(obj);
            return;
        }
        String name = obj.getOutputName();
        if (obj.getDisplayMode().equals(ElementSymbol.DisplayMode.FULLY_QUALIFIED)) {
            name = obj.getName();
        }
        outputDisplayName(name);
    }

    private void outputShortName(ElementSymbol obj) {
        outputDisplayName(Symbol.getShortName(obj.getOutputName()));
    }

    private void outputDisplayName(String name) {
        String[] pathParts = name.split("\\."); //$NON-NLS-1$
        for (int i = 0; i < pathParts.length; i++) {
            if (i > 0) {
                append(Symbol.SEPARATOR);
            }
            append(escapeSinglePart(pathParts[i]));
        }
    }

    @Override
    public void visit(ExpressionSymbol obj) {
        visitNode(obj.getExpression());
    }

    @Override
    public void visit(Function obj) {
        String name = obj.getName();
        Expression[] args = obj.getArgs();
        if (obj.isImplicit()) {
            // Hide this function, which is implicit
            visitNode(args[0]);

        } else if (name.equalsIgnoreCase(CONVERT) || name.equalsIgnoreCase(CAST)) {
            append(name);
            append("("); //$NON-NLS-1$

            if (args != null && args.length > 0) {
                visitNode(args[0]);

                if (name.equalsIgnoreCase(CONVERT)) {
                    append(", "); //$NON-NLS-1$
                } else {
                    append(" "); //$NON-NLS-1$
                    append(AS);
                    append(" "); //$NON-NLS-1$
                }

                if (args.length < 2 || args[1] == null || !(args[1] instanceof Constant)) {
                    append(UNDEFINED);
                } else {
                    append(((Constant)args[1]).getValue());
                }
            }
            append(")"); //$NON-NLS-1$

        } else if (name.equals("+") || name.equals("-") || name.equals("*") || name.equals("/") || name.equals("||")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            append("("); //$NON-NLS-1$

            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    visitNode(args[i]);
                    if (i < (args.length - 1)) {
                        append(SPACE);
                        append(name);
                        append(SPACE);
                    }
                }
            }
            append(")"); //$NON-NLS-1$

        } else if (name.equalsIgnoreCase(NonReserved.TIMESTAMPADD) || name.equalsIgnoreCase(NonReserved.TIMESTAMPDIFF)) {
            append(name);
            append("("); //$NON-NLS-1$

            if (args != null && args.length > 0) {
                append(((Constant)args[0]).getValue());
                registerNodes(args, 1);
            }
            append(")"); //$NON-NLS-1$

        } else if (name.equalsIgnoreCase(SourceSystemFunctions.XMLPI)) {
            append(name);
            append("(NAME "); //$NON-NLS-1$
            outputDisplayName((String)((Constant)args[0]).getValue());
            registerNodes(args, 1);
            append(")"); //$NON-NLS-1$
        } else if (name.equalsIgnoreCase(SourceSystemFunctions.TRIM)) {
            append(name);
            append(SQLConstants.Tokens.LPAREN);
            String value = (String)((Constant)args[0]).getValue();
            if (!value.equalsIgnoreCase(BOTH)) {
                append(((Constant)args[0]).getValue());
                append(" "); //$NON-NLS-1$
            }
            append(args[1]);
            append(" "); //$NON-NLS-1$
            append(FROM);
            append(" "); //$NON-NLS-1$
            append(args[2]);
            append(")"); //$NON-NLS-1$
        } else {
            append(name);
            append("("); //$NON-NLS-1$
            registerNodes(args, 0);
            append(")"); //$NON-NLS-1$
        }
    }

    private void registerNodes(LanguageObject[] objects, int begin) {
        registerNodes(Arrays.asList(objects), begin);
    }

    private void registerNodes(List<? extends LanguageObject> objects, int begin) {
        for (int i = begin; i < objects.size(); i++) {
            if (i > 0) {
                append(", "); //$NON-NLS-1$
            }
            visitNode(objects.get(i));
        }
    }

    @Override
    public void visit(GroupSymbol obj) {
        String alias = null;
        String fullGroup = obj.getOutputName();
        if (obj.getOutputDefinition() != null) {
            alias = obj.getOutputName();
            fullGroup = obj.getOutputDefinition();
        }

        outputDisplayName(fullGroup);

        if (alias != null) {
            append(SPACE);
            append(AS);
            append(SPACE);
            append(escapeSinglePart(alias));
        }
    }

    @Override
    public void visit(Reference obj) {
        if (!obj.isPositional() && obj.getExpression() != null) {
            visitNode(obj.getExpression());
        } else {
            append("?"); //$NON-NLS-1$
        }
    }

    // ############ Visitor methods for storedprocedure language objects ####################

    private void visit7(Block obj) {
        addLabel(obj);
        List<Statement> statements = obj.getStatements();
        // Add first clause
        append(BEGIN);
        if (obj.isAtomic()) {
            append(SPACE);
            append(ATOMIC);
        }
        append("\n"); //$NON-NLS-1$
        Iterator<Statement> stmtIter = statements.iterator();
        while (stmtIter.hasNext()) {
            // Add each statement
            addTabs(1);
            visitNode(stmtIter.next());
            append("\n"); //$NON-NLS-1$
        }
        addTabs(0);
        append(END);
    }

    private void addStatements(List<Statement> statements) {
        Iterator<Statement> stmtIter = statements.iterator();
        while (stmtIter.hasNext()) {
            // Add each statement
            addTabs(1);
            visitNode(stmtIter.next());
            append("\n"); //$NON-NLS-1$
        }
        addTabs(0);
    }

    private void visit8(Block obj) {
        addLabel(obj);
        List<Statement> statements = obj.getStatements();
        // Add first clause
        append(BEGIN);
        if (obj.isAtomic()) {
            append(SPACE);
            append(ATOMIC);
        }
        append("\n"); //$NON-NLS-1$
        addStatements(statements);
        if (obj.getExceptionGroup() != null) {
            append(NonReserved.EXCEPTION);
            append(SPACE);
            outputDisplayName(obj.getExceptionGroup());
            append("\n"); //$NON-NLS-1$
            if (obj.getExceptionStatements() != null) {
                addStatements(obj.getExceptionStatements());
            }
        }
        append(END);
    }

    @Override
    public void visit(Block block) {
        if (isTeiid8OrGreater())
            visit8(block);
        else
            visit7(block);
    }

    private void addLabel(Labeled obj) {
        if (obj.getLabel() != null) {
            outputDisplayName(obj.getLabel());
            append(SPACE);
            append(Tokens.COLON);
            append(SPACE);
        }
    }

    /**
    * @param level  
    */
    protected void addTabs(int level) {
    }

    @Override
    public void visit(CommandStatement obj) {
        visitNode(obj.getCommand());
        if (isTeiid8OrGreater() && !obj.isReturnable()) {
            append(SPACE);
            append(WITHOUT);
            append(SPACE);
            append(RETURN);
        }
        append(";"); //$NON-NLS-1$
    }

    @Override
    @Removed( "8.0.0" )
    public void visit(CreateUpdateProcedureCommand obj) {
        append(CREATE);
        append(SPACE);
        if (!obj.isUpdateProcedure()) {
            append(VIRTUAL);
            append(SPACE);
        }
        append(PROCEDURE);
        append("\n"); //$NON-NLS-1$
        addTabs(0);
        visitNode(obj.getBlock());
    }

    @Override
    public void visit(CreateProcedureCommand obj) {
        append(CREATE);
        append(SPACE);
        append(VIRTUAL);
        append(SPACE);
        append(PROCEDURE);
        append("\n"); //$NON-NLS-1$
        addTabs(0);
        visitNode(obj.getBlock());
    }

    @Override
    public void visit(DeclareStatement obj) {
        append(DECLARE);
        append(SPACE);
        append(obj.getVariableType());
        append(SPACE);
        createAssignment(obj);
    }

    /**
     * @param obj
     * @param parts
     */
    private void createAssignment(AssignmentStatement obj) {
        visitNode(obj.getVariable());
        if (obj.getExpression() != null) {
            append(" = "); //$NON-NLS-1$
            visitNode(obj.getExpression());
        }
        append(";"); //$NON-NLS-1$
    }

    @Override
    public void visit(IfStatement obj) {
        append(IF);
        append("("); //$NON-NLS-1$
        visitNode(obj.getCondition());
        append(")\n"); //$NON-NLS-1$
        addTabs(0);
        visitNode(obj.getIfBlock());
        if (obj.hasElseBlock()) {
            append("\n"); //$NON-NLS-1$
            addTabs(0);
            append(ELSE);
            append("\n"); //$NON-NLS-1$
            addTabs(0);
            visitNode(obj.getElseBlock());
        }
    }

    @Override
    public void visit(AssignmentStatement obj) {
        createAssignment(obj);
    }

    @Override
    public void visit(RaiseStatement obj) {
        append(NonReserved.RAISE);
        append(SPACE);
        if (obj.isWarning()) {
            append(SQLWARNING);
            append(SPACE);
        }
        visitNode(obj.getExpression());
        append(";"); //$NON-NLS-1$
    }

    @Override
    public void visit(HasCriteria obj) {
        append(HAS);
        append(SPACE);
        visitNode(obj.getSelector());
    }

    @Override
    public void visit(TranslateCriteria obj) {
        append(TRANSLATE);
        append(SPACE);
        visitNode(obj.getSelector());

        if (obj.hasTranslations()) {
            append(SPACE);
            append(WITH);
            append(SPACE);
            append("("); //$NON-NLS-1$
            Iterator critIter = obj.getTranslations().iterator();

            while (critIter.hasNext()) {
                visitNode((Criteria)critIter.next());
                if (critIter.hasNext()) {
                    append(", "); //$NON-NLS-1$
                }
                if (!critIter.hasNext()) {
                    append(")"); //$NON-NLS-1$
                }
            }
        }
    }

    @Override
    public void visit(CriteriaSelector obj) {
        switch (obj.getSelectorType()) {
            case EQ:
                append("= "); //$NON-NLS-1$
                break;
            case GE:
                append(">= "); //$NON-NLS-1$
                break;
            case GT:
                append("> "); //$NON-NLS-1$
                break;
            case LE:
                append("<= "); //$NON-NLS-1$
                break;
            case LT:
                append("< "); //$NON-NLS-1$
                break;
            case NE:
                append("<> "); //$NON-NLS-1$
                break;
            case IN:
                append(IN);
                append(SPACE);
                break;
            case IS_NULL:
                append(IS);
                append(SPACE);
                append(NULL);
                append(SPACE);
                break;
            case LIKE:
                append(LIKE);
                append(SPACE);
                break;
            case BETWEEN:
                append(BETWEEN);
                append(SPACE);
                break;
            case NO_TYPE:
            default:
                // Append nothing
                break;
        }

        append(CRITERIA);
        if (obj.hasElements()) {
            append(SPACE);
            append(ON);
            append(SPACE);
            append("("); //$NON-NLS-1$

            Iterator elmtIter = obj.getElements().iterator();
            while (elmtIter.hasNext()) {
                visitNode((ElementSymbol)elmtIter.next());
                if (elmtIter.hasNext()) {
                    append(", "); //$NON-NLS-1$
                }
            }
            append(")"); //$NON-NLS-1$
        }
    }

    @Override
    public void visit(RaiseErrorStatement obj) {
        append(ERROR);
        append(SPACE);
        visitNode(obj.getExpression());
        append(";"); //$NON-NLS-1$
    }

    @Override
    public void visit(ExceptionExpression exceptionExpression) {
        append(SQLEXCEPTION);
        append(SPACE);
        visitNode(exceptionExpression.getMessage());
        if (exceptionExpression.getSqlState() != null) {
            append(SPACE);
            append(SQLSTATE);
            append(SPACE);
            append(exceptionExpression.getSqlState());
            if (exceptionExpression.getErrorCode() != null) {
                append(Tokens.COMMA);
                append(SPACE);
                append(exceptionExpression.getErrorCode());
            }
        }
        if (exceptionExpression.getParent() != null) {
            append(SPACE);
            append(NonReserved.CHAIN);
            append(SPACE);
            append(exceptionExpression.getParent());
        }
    }

    @Override
    public void visit(ReturnStatement obj) {
        append(RETURN);
        if (obj.getExpression() != null) {
            append(SPACE);
            visitNode(obj.getExpression());
        }
        append(Tokens.SEMICOLON);
    }

    @Override
    public void visit(BranchingStatement obj) {
        switch (obj.getMode()) {
            case CONTINUE:
                append(CONTINUE);
                break;
            case BREAK:
                append(BREAK);
                break;
            case LEAVE:
                append(LEAVE);
                break;
        }
        if (obj.getLabel() != null) {
            append(SPACE);
            outputDisplayName(obj.getLabel());
        }
        append(";"); //$NON-NLS-1$
    }

    @Override
    public void visit(LoopStatement obj) {
        addLabel(obj);
        append(LOOP);
        append(" "); //$NON-NLS-1$
        append(ON);
        append(" ("); //$NON-NLS-1$
        visitNode(obj.getCommand());
        append(") "); //$NON-NLS-1$
        append(AS);
        append(" "); //$NON-NLS-1$
        if (isTeiid8OrGreater())
            outputDisplayName(obj.getCursorName());
        else
            append(obj.getCursorName());

        append("\n"); //$NON-NLS-1$
        addTabs(0);
        visitNode(obj.getBlock());
    }

    @Override
    public void visit(WhileStatement obj) {
        addLabel(obj);
        append(WHILE);
        append("("); //$NON-NLS-1$
        visitNode(obj.getCondition());
        append(")\n"); //$NON-NLS-1$
        addTabs(0);
        visitNode(obj.getBlock());
    }

    @Override
    public void visit(ExistsCriteria obj) {
        if (obj.isNegated()) {
            append(NOT);
            append(SPACE);
        }
        append(EXISTS);
        addSubqueryHint(obj.getSubqueryHint());
        append(" ("); //$NON-NLS-1$
        visitNode(obj.getCommand());
        append(")"); //$NON-NLS-1$
    }

    private void addSubqueryHint(SubqueryHint hint) {
        if (hint.isNoUnnest()) {
            append(SPACE);
            append(BEGIN_HINT);
            append(SPACE);
            append(SubqueryHint.NOUNNEST);
            append(SPACE);
            append(END_HINT);
        } else if (hint.isDepJoin()) {
            append(SPACE);
            append(BEGIN_HINT);
            append(SPACE);
            append(SubqueryHint.DJ);
            append(SPACE);
            append(END_HINT);
        } else if (hint.isMergeJoin()) {
            append(SPACE);
            append(BEGIN_HINT);
            append(SPACE);
            append(SubqueryHint.MJ);
            append(SPACE);
            append(END_HINT);
        }
    }

    @Override
    public void visit(SubqueryCompareCriteria obj) {
        Expression leftExpression = obj.getLeftExpression();
        visitNode(leftExpression);

        String operator = obj.getOperatorAsString();
        String quantifier = obj.getPredicateQuantifierAsString();

        // operator and beginning of list
        append(SPACE);
        append(operator);
        append(SPACE);
        append(quantifier);
        append("("); //$NON-NLS-1$
        visitNode(obj.getCommand());
        append(")"); //$NON-NLS-1$
    }

    @Override
    public void visit(ScalarSubquery obj) {
        // operator and beginning of list
        append("("); //$NON-NLS-1$
        visitNode(obj.getCommand());
        append(")"); //$NON-NLS-1$
    }

    @Override
    public void visit(XMLAttributes obj) {
        append(XMLATTRIBUTES);
        append("("); //$NON-NLS-1$
        registerNodes(obj.getArgs(), 0);
        append(")"); //$NON-NLS-1$
    }

    @Override
    public void visit(XMLElement obj) {
        append(XMLELEMENT);
        append("(NAME "); //$NON-NLS-1$
        outputDisplayName(obj.getName());
        if (obj.getNamespaces() != null) {
            append(", "); //$NON-NLS-1$
            visitNode(obj.getNamespaces());
        }
        if (obj.getAttributes() != null) {
            append(", "); //$NON-NLS-1$
            visitNode(obj.getAttributes());
        }
        if (!obj.getContent().isEmpty()) {
            append(", "); //$NON-NLS-1$
        }
        registerNodes(obj.getContent(), 0);
        append(")"); //$NON-NLS-1$
    }

    @Override
    public void visit(XMLForest obj) {
        append(XMLFOREST);
        append("("); //$NON-NLS-1$
        if (obj.getNamespaces() != null) {
            visitNode(obj.getNamespaces());
            append(", "); //$NON-NLS-1$
        }
        registerNodes(obj.getArgs(), 0);
        append(")"); //$NON-NLS-1$
    }

    @Override
    public void visit(JSONObject obj) {
        append(NonReserved.JSONOBJECT);
        append("("); //$NON-NLS-1$
        registerNodes(obj.getArgs(), 0);
        append(")"); //$NON-NLS-1$
    }

    @Override
    public void visit(TextLine obj) {
        append(FOR);
        append(SPACE);
        registerNodes(obj.getExpressions(), 0);

        if (obj.getDelimiter() != null) {
            append(SPACE);
            append(NonReserved.DELIMITER);
            append(SPACE);
            visitNode(newConstant(obj.getDelimiter()));
        }
        if (obj.getQuote() != null) {
            append(SPACE);
            append(NonReserved.QUOTE);
            append(SPACE);
            visitNode(newConstant(obj.getQuote()));
        }
        if (obj.isIncludeHeader()) {
            append(SPACE);
            append(NonReserved.HEADER);
        }
        if (obj.getEncoding() != null) {
            append(SPACE);
            append(NonReserved.ENCODING);
            append(SPACE);
            outputDisplayName(obj.getEncoding());
        }
    }

    @Override
    public void visit(XMLNamespaces obj) {
        append(XMLNAMESPACES);
        append("("); //$NON-NLS-1$
        for (Iterator<NamespaceItem> items = obj.getNamespaceItems().iterator(); items.hasNext();) {
            NamespaceItem item = items.next();
            if (item.getPrefix() == null) {
                if (item.getUri() == null) {
                    append("NO DEFAULT"); //$NON-NLS-1$
                } else {
                    append("DEFAULT "); //$NON-NLS-1$
                    visitNode(newConstant(item.getUri()));
                }
            } else {
                visitNode(newConstant(item.getUri()));
                append(" AS "); //$NON-NLS-1$
                outputDisplayName(item.getPrefix());
            }
            if (items.hasNext()) {
                append(", "); //$NON-NLS-1$
            }
        }
        append(")"); //$NON-NLS-1$
    }

    @Override
    public void visit(Limit obj) {
        if (!obj.isStrict()) {
            append(BEGIN_HINT);
            append(SPACE);
            append(Limit.NON_STRICT);
            append(SPACE);
            append(END_HINT);
            append(SPACE);
        }
        if (obj.getRowLimit() == null) {
            append(OFFSET);
            append(SPACE);
            visitNode(obj.getOffset());
            append(SPACE);
            append(ROWS);
            return;
        }
        append(LIMIT);
        if (obj.getOffset() != null) {
            append(SPACE);
            visitNode(obj.getOffset());
            append(","); //$NON-NLS-1$
        }
        append(SPACE);
        visitNode(obj.getRowLimit());
    }

    @Override
    public void visit(TextTable obj) {
        addHintComment(obj);
        append("TEXTTABLE("); //$NON-NLS-1$
        visitNode(obj.getFile());
        if (isTeiid8OrGreater() && obj.getSelector() != null) {
            append(SPACE);
            append(NonReserved.SELECTOR);
            append(SPACE);
            append(escapeSinglePart(obj.getSelector()));
        }
        append(SPACE);
        append(NonReserved.COLUMNS);

        for (Iterator<TextColumn> cols = obj.getColumns().iterator(); cols.hasNext();) {
            TextColumn col = cols.next();
            append(SPACE);
            outputDisplayName(col.getName());
            append(SPACE);
            append(col.getType());
            if (col.getWidth() != null) {
                append(SPACE);
                append(NonReserved.WIDTH);
                append(SPACE);
                append(col.getWidth());
            }
            if (col.isNoTrim()) {
                append(SPACE);
                append(NO);
                append(SPACE);
                append(NonReserved.TRIM);
            }
            if (isTeiid8OrGreater() && col.getSelector() != null) {
                append(SPACE);
                append(NonReserved.SELECTOR);
                append(SPACE);
                append(escapeSinglePart(col.getSelector()));
                append(SPACE);
                append(col.getPosition());
            }
            if (cols.hasNext()) {
                append(","); //$NON-NLS-1$
            }
        }
        if (!obj.isUsingRowDelimiter()) {
            append(SPACE);
            append(NO);
            append(SPACE);
            append(ROW);
            append(SPACE);
            append(NonReserved.DELIMITER);
        }
        if (obj.getDelimiter() != null) {
            append(SPACE);
            append(NonReserved.DELIMITER);
            append(SPACE);
            visitNode(newConstant(obj.getDelimiter()));
        }
        if (obj.getQuote() != null) {
            append(SPACE);
            if (obj.isEscape()) {
                append(ESCAPE);
            } else {
                append(NonReserved.QUOTE);
            }
            append(SPACE);
            visitNode(newConstant(obj.getQuote()));
        }
        if (obj.getHeader() != null) {
            append(SPACE);
            append(NonReserved.HEADER);
            if (1 != obj.getHeader()) {
                append(SPACE);
                append(obj.getHeader());
            }
        }
        if (obj.getSkip() != null) {
            append(SPACE);
            append(NonReserved.SKIP);
            append(SPACE);
            append(obj.getSkip());
        }
        append(")");//$NON-NLS-1$
        append(SPACE);
        append(AS);
        append(SPACE);
        outputDisplayName(obj.getName());
    }

    @Override
    public void visit(XMLTable obj) {
        addHintComment(obj);
        append("XMLTABLE("); //$NON-NLS-1$
        if (obj.getNamespaces() != null) {
            visitNode(obj.getNamespaces());
            append(","); //$NON-NLS-1$
            append(SPACE);
        }
        visitNode(newConstant(obj.getXquery()));
        if (!obj.getPassing().isEmpty()) {
            append(SPACE);
            append(NonReserved.PASSING);
            append(SPACE);
            registerNodes(obj.getPassing(), 0);
        }

        if ((isTeiid8OrGreater() && !obj.getColumns().isEmpty() && !obj.isUsingDefaultColumn())
            || (!isTeiid8OrGreater() && !obj.getColumns().isEmpty())) {
            append(SPACE);
            append(NonReserved.COLUMNS);
            for (Iterator<XMLColumn> cols = obj.getColumns().iterator(); cols.hasNext();) {
                XMLColumn col = cols.next();
                append(SPACE);
                outputDisplayName(col.getName());
                append(SPACE);
                if (col.isOrdinal()) {
                    append(FOR);
                    append(SPACE);
                    append(NonReserved.ORDINALITY);
                } else {
                    append(col.getType());
                    if (col.getDefaultExpression() != null) {
                        append(SPACE);
                        append(DEFAULT);
                        append(SPACE);
                        visitNode(col.getDefaultExpression());
                    }
                    if (col.getPath() != null) {
                        append(SPACE);
                        append(NonReserved.PATH);
                        append(SPACE);
                        visitNode(newConstant(col.getPath()));
                    }
                }
                if (cols.hasNext()) {
                    append(","); //$NON-NLS-1$
                }
            }
        }
        append(")");//$NON-NLS-1$
        append(SPACE);
        append(AS);
        append(SPACE);
        outputDisplayName(obj.getName());
    }

    @Override
    public void visit(ObjectTable obj) {
        addHintComment(obj);
        append("OBJECTTABLE("); //$NON-NLS-1$
        if (obj.getScriptingLanguage() != null) {
            append(LANGUAGE);
            append(SPACE);
            visitNode(newConstant(obj.getScriptingLanguage()));
            append(SPACE);
        }
        visitNode(newConstant(obj.getRowScript()));
        if (!obj.getPassing().isEmpty()) {
            append(SPACE);
            append(NonReserved.PASSING);
            append(SPACE);
            registerNodes(obj.getPassing(), 0);
        }
        append(SPACE);
        append(NonReserved.COLUMNS);
        for (Iterator<ObjectColumn> cols = obj.getColumns().iterator(); cols.hasNext();) {
            ObjectColumn col = cols.next();
            append(SPACE);
            outputDisplayName(col.getName());
            append(SPACE);
            append(col.getType());
            append(SPACE);
            visitNode(newConstant(col.getPath()));
            if (col.getDefaultExpression() != null) {
                append(SPACE);
                append(DEFAULT);
                append(SPACE);
                visitNode(col.getDefaultExpression());
            }
            if (cols.hasNext()) {
                append(","); //$NON-NLS-1$
            }
        }
        append(")");//$NON-NLS-1$
        append(SPACE);
        append(AS);
        append(SPACE);
        outputDisplayName(obj.getName());
    }

    @Override
    public void visit(XMLQuery obj) {
        append("XMLQUERY("); //$NON-NLS-1$
        if (obj.getNamespaces() != null) {
            visitNode(obj.getNamespaces());
            append(","); //$NON-NLS-1$
            append(SPACE);
        }
        visitNode(newConstant(obj.getXquery()));
        if (!obj.getPassing().isEmpty()) {
            append(SPACE);
            append(NonReserved.PASSING);
            append(SPACE);
            registerNodes(obj.getPassing(), 0);
        }
        if (obj.getEmptyOnEmpty() != null) {
            append(SPACE);
            if (obj.getEmptyOnEmpty()) {
                append(NonReserved.EMPTY);
            } else {
                append(NULL);
            }
            append(SPACE);
            append(ON);
            append(SPACE);
            append(NonReserved.EMPTY);
        }
        append(")");//$NON-NLS-1$
    }

    @Override
    public void visit(DerivedColumn obj) {
        visitNode(obj.getExpression());
        if (obj.getAlias() != null) {
            append(SPACE);
            append(AS);
            append(SPACE);
            outputDisplayName(obj.getAlias());
        }
    }

    @Override
    public void visit(XMLSerialize obj) {
        append(XMLSERIALIZE);
        append(Tokens.LPAREN);
        if (obj.getDocument() != null) {
            if (obj.getDocument()) {
                append(NonReserved.DOCUMENT);
            } else {
                append(NonReserved.CONTENT);
            }
            append(SPACE);
        }
        visitNode(obj.getExpression());
        if (obj.getTypeString() != null) {
            append(SPACE);
            append(AS);
            append(SPACE);
            append(obj.getTypeString());
        }
        if (isTeiid8OrGreater()) {
            if (obj.getEncoding() != null) {
                append(SPACE);
                append(NonReserved.ENCODING);
                append(SPACE);
                append(escapeSinglePart(obj.getEncoding()));
            }
            if (obj.getVersion() != null) {
                append(SPACE);
                append(NonReserved.VERSION);
                append(SPACE);
                append(newConstant(obj.getVersion()));
            }
            if (obj.getDeclaration() != null) {
                append(SPACE);
                if (obj.getDeclaration()) {
                    append(NonReserved.INCLUDING);
                } else {
                    append(NonReserved.EXCLUDING);
                }
                append(SPACE);
                append(NonReserved.XMLDECLARATION);
            }
        }
        append(Tokens.RPAREN);
    }

    @Override
    public void visit(QueryString obj) {
        append(NonReserved.QUERYSTRING);
        append("("); //$NON-NLS-1$
        visitNode(obj.getPath());
        if (!obj.getArgs().isEmpty()) {
            append(","); //$NON-NLS-1$
            append(SPACE);
            registerNodes(obj.getArgs(), 0);
        }
        append(")"); //$NON-NLS-1$
    }

    @Override
    public void visit(XMLParse obj) {
        append(XMLPARSE);
        append(Tokens.LPAREN);
        if (obj.isDocument()) {
            append(NonReserved.DOCUMENT);
        } else {
            append(NonReserved.CONTENT);
        }
        append(SPACE);
        visitNode(obj.getExpression());
        if (obj.isWellFormed()) {
            append(SPACE);
            append(NonReserved.WELLFORMED);
        }
        append(Tokens.RPAREN);
    }

    @Override
    public void visit(ExpressionCriteria obj) {
        visitNode(obj.getExpression());
    }

    @Override
    public void visit(TriggerAction obj) {
        append(FOR);
        append(SPACE);
        append(EACH);
        append(SPACE);
        append(ROW);
        append("\n"); //$NON-NLS-1$
        addTabs(0);
        visitNode(obj.getBlock());
    }

    @Override
    public void visit(ArrayTable obj) {
        addHintComment(obj);
        append("ARRAYTABLE("); //$NON-NLS-1$
        visitNode(obj.getArrayValue());
        append(SPACE);
        append(NonReserved.COLUMNS);

        for (Iterator<ProjectedColumn> cols = obj.getColumns().iterator(); cols.hasNext();) {
            ProjectedColumn col = cols.next();
            append(SPACE);
            outputDisplayName(col.getName());
            append(SPACE);
            append(col.getType());
            if (cols.hasNext()) {
                append(","); //$NON-NLS-1$
            }
        }

        append(")");//$NON-NLS-1$
        append(SPACE);
        append(AS);
        append(SPACE);
        outputDisplayName(obj.getName());
    }

    private void visit7(AlterProcedure<CreateUpdateProcedureCommand> obj) {
        append(ALTER);
        append(SPACE);
        append(PROCEDURE);
        append(SPACE);
        append(obj.getTarget());
        beginClause(1);
        append(AS);
        append(obj.getDefinition().getBlock());
    }

    private void visit8(AlterProcedure<CreateProcedureCommand> obj) {
        append(ALTER);
        append(SPACE);
        append(PROCEDURE);
        append(SPACE);
        append(obj.getTarget());
        beginClause(1);
        append(AS);
        append(obj.getDefinition().getBlock());
    }

    @Override
    public void visit(AlterProcedure obj) {
        if (isTeiid8OrGreater())
            visit8(obj);
        else
            visit7(obj);
    }

    @Override
    public void visit(AlterTrigger obj) {
        if (obj.isCreate()) {
            append(CREATE);
        } else {
            append(ALTER);
        }
        append(SPACE);
        append(TRIGGER);
        append(SPACE);
        append(ON);
        append(SPACE);
        append(obj.getTarget());
        beginClause(0);
        append(NonReserved.INSTEAD);
        append(SPACE);
        append(OF);
        append(SPACE);
        append(obj.getEvent());
        if (obj.getDefinition() != null) {
            beginClause(0);
            append(AS);
            append("\n"); //$NON-NLS-1$
            addTabs(0);
            append(obj.getDefinition());
        } else {
            append(SPACE);
            append(obj.getEnabled() ? NonReserved.ENABLED : NonReserved.DISABLED);
        }
    }

    @Override
    public void visit(AlterView obj) {
        append(ALTER);
        append(SPACE);
        append(NonReserved.VIEW);
        append(SPACE);
        append(obj.getTarget());
        beginClause(0);
        append(AS);
        append("\n"); //$NON-NLS-1$
        addTabs(0);
        append(obj.getDefinition());
    }

    @Override
    public void visit(WindowFunction windowFunction) {
        append(windowFunction.getFunction());
        append(SPACE);
        append(OVER);
        append(SPACE);
        append(windowFunction.getWindowSpecification());
    }

    @Override
    public void visit(WindowSpecification windowSpecification) {
        append(Tokens.LPAREN);
        boolean needsSpace = false;
        if (windowSpecification.getPartition() != null) {
            append(PARTITION);
            append(SPACE);
            append(BY);
            append(SPACE);
            registerNodes(windowSpecification.getPartition(), 0);
            needsSpace = true;
        }
        if (windowSpecification.getOrderBy() != null) {
            if (needsSpace) {
                append(SPACE);
            }
            append(windowSpecification.getOrderBy());
        }
        append(Tokens.RPAREN);
    }

    @Override
    public void visit(Array array) {
        if (!array.isImplicit()) {
            append(Tokens.LPAREN);
        }
        registerNodes(array.getExpressions(), 0);
        if (!array.isImplicit()) {
            append(Tokens.RPAREN);
        }
    }

    private String escapeSinglePart(String part) {
        if (isReservedWord(part)) {
            return ID_ESCAPE_CHAR + part + ID_ESCAPE_CHAR;
        }
        boolean escape = true;
        char start = part.charAt(0);
        if (start == '#' || start == '@' || StringUtil.isLetter(start)) {
            escape = false;
            for (int i = 1; !escape && i < part.length(); i++) {
                char c = part.charAt(i);
                escape = !StringUtil.isLetterOrDigit(c) && c != '_';
            }
        }
        if (escape) {
            return ID_ESCAPE_CHAR + escapeStringValue(part, "\"") + ID_ESCAPE_CHAR; //$NON-NLS-1$
        }
        return part;
    }

    /**
     * Check whether a string is considered a reserved word or not. Subclasses may override to change definition of reserved word.
     *
     * @param string String to check
     * @return True if reserved word
     */
    private boolean isReservedWord(String string) {
        if (string == null) {
            return false;
        }
        return SQLConstants.isReservedWord(teiidVersion, string);
    }

}