/*
 * This file is part of the OpenJML project. 
 * Author: David R. Cok
 */
package org.jmlspecs.openjml.walkers;

import java.util.Map;

import org.jmlspecs.annotation.Nullable;
import org.jmlspecs.openjml.JmlSpecs;
import org.jmlspecs.openjml.JmlTokenKind;
import org.jmlspecs.openjml.JmlTree;
import org.jmlspecs.openjml.JmlTreeVisitor;
import org.jmlspecs.openjml.Utils;
import org.jmlspecs.openjml.JmlTreeUtils;
import org.jmlspecs.openjml.JmlTree.*;
import org.jmlspecs.openjml.JmlTreeScanner;

import com.sun.source.tree.*;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Log;

/** This class determines whether two subtrees match.
 * 
 * @author David Cok
 *
 */

public class JmlTreeMatch extends JmlTreeScanner {
    
    protected Log log;
    protected Utils utils;
    protected Context context;
    
    /** Creates a new copier, whose new nodes are generated from the given factory*/
    public JmlTreeMatch(Context context) {
        super();
        this.log = Log.instance(context);
        this.utils = Utils.instance(context);
        this.context = context;
    }
    
    public static class NoMatchException extends RuntimeException {}
    
    JCTree top;
    
    public boolean matches(JCTree tree, JCTree arg) {
        if (tree == null) return false;
        top = arg;
        try {
            scan(tree);
        } catch (NoMatchException e) {
            if (utils.jmlverbose >= Utils.JMLVERBOSE) log.note(tree, "jml.message", "Match not found");
            return false;
        }
        if (utils.jmlverbose >= Utils.JMLVERBOSE) log.note(tree, "jml.message", "Match found");
        return true;
    }

    /** Visitor method: Scan a single node.
     */
    public void scan(JCTree tree) {
        if (tree!=null) {
            if (tree.getClass() != top.getClass()) {
                throw new NoMatchException();
            }
            tree.accept(this);
        }
    }

    /** Visitor method: scan a list of nodes.
     */
    public void scan(List<? extends JCTree> trees) {
        if (trees != null)
        for (List<? extends JCTree> l = trees; l.nonEmpty(); l = l.tail)
            scan(l.head);
    }


/* ***************************************************************************
 * Visitor methods
 ****************************************************************************/
//    public void visitTopLevel(JCCompilationUnit tree) {
//        scan(tree.packageAnnotations);
//        scan(tree.pid);
//        scan(tree.defs);
//    }
//
//    public void visitImport(JCImport tree) {
//        scan(tree.qualid);
//    }
//
//    public void visitClassDef(JCClassDecl tree) {
//        scan(tree.mods);
//        scan(tree.typarams);
//        scan(tree.extending);
//        scan(tree.implementing);
//        scan(tree.defs);
//    }
//
//    public void visitMethodDef(JCMethodDecl tree) {
//        scan(tree.mods);
//        scan(tree.restype);
//        scan(tree.typarams);
//        scan(tree.recvparam);
//        scan(tree.params);
//        scan(tree.thrown);
//        scan(tree.defaultValue);
//        scan(tree.body);
//    }
//
//    public void visitVarDef(JCVariableDecl tree) {
//        scan(tree.mods);
//        scan(tree.vartype);
//        scan(tree.nameexpr);
//        scan(tree.init);
//    }
//
//    public void visitSkip(JCSkip tree) {
//    }
//
//    public void visitBlock(JCBlock tree) {
//        scan(tree.stats);
//    }
//
//    public void visitDoLoop(JCDoWhileLoop tree) {
//        scan(tree.body);
//        scan(tree.cond);
//    }
//
//    public void visitWhileLoop(JCWhileLoop tree) {
//        scan(tree.cond);
//        scan(tree.body);
//    }
//
//    public void visitForLoop(JCForLoop tree) {
//        scan(tree.init);
//        scan(tree.cond);
//        scan(tree.step);
//        scan(tree.body);
//    }
//
//    public void visitForeachLoop(JCEnhancedForLoop tree) {
//        scan(tree.var);
//        scan(tree.expr);
//        scan(tree.body);
//    }
//
//    public void visitLabelled(JCLabeledStatement tree) {
//        scan(tree.body);
//    }
//
//    public void visitSwitch(JCSwitch tree) {
//        scan(tree.selector);
//        scan(tree.cases);
//    }
//
//    public void visitCase(JCCase tree) {
//        scan(tree.pat);
//        scan(tree.stats);
//    }
//
//    public void visitSynchronized(JCSynchronized tree) {
//        scan(tree.lock);
//        scan(tree.body);
//    }
//
//    public void visitTry(JCTry tree) {
//        scan(tree.resources);
//        scan(tree.body);
//        scan(tree.catchers);
//        scan(tree.finalizer);
//    }
//
//    public void visitCatch(JCCatch tree) {
//        scan(tree.param);
//        scan(tree.body);
//    }
//
//    public void visitConditional(JCConditional tree) {
//        scan(tree.cond);
//        scan(tree.truepart);
//        scan(tree.falsepart);
//    }
//
//    public void visitIf(JCIf tree) {
//        scan(tree.cond);
//        scan(tree.thenpart);
//        scan(tree.elsepart);
//    }
//
//    public void visitExec(JCExpressionStatement tree) {
//        scan(tree.expr);
//    }
//
//    public void visitBreak(JCBreak tree) {
//    }
//
//    public void visitContinue(JCContinue tree) {
//    }
//
//    public void visitReturn(JCReturn tree) {
//        scan(tree.expr);
//    }
//
//    public void visitThrow(JCThrow tree) {
//        scan(tree.expr);
//    }
//
//    public void visitAssert(JCAssert tree) {
//        scan(tree.cond);
//        scan(tree.detail);
//    }
//
//    public void visitApply(JCMethodInvocation tree) {
//        scan(tree.typeargs);
//        scan(tree.meth);
//        scan(tree.args);
//    }
//
//    public void visitNewClass(JCNewClass tree) {
//        scan(tree.encl);
//        scan(tree.typeargs);
//        scan(tree.clazz);
//        scan(tree.args);
//        scan(tree.def);
//    }
//
//    public void visitNewArray(JCNewArray tree) {
//        scan(tree.annotations);
//        scan(tree.elemtype);
//        scan(tree.dims);
//        for (List<JCAnnotation> annos : tree.dimAnnotations)
//            scan(annos);
//        scan(tree.elems);
//    }
//
//    public void visitLambda(JCLambda tree) {
//        scan(tree.body);
//        scan(tree.params);
//    }
//
    public void visitParens(JCParens tree) {
        JCParens t = (JCParens)top;
        top = t.expr;
        scan(tree.expr);
        top = t;
    }

//    public void visitAssign(JCAssign tree) {
//        scan(tree.lhs);
//        scan(tree.rhs);
//    }
//
//    public void visitAssignop(JCAssignOp tree) {
//        scan(tree.lhs);
//        scan(tree.rhs);
//    }
//
    public void visitUnary(JCUnary tree) {
        JCUnary t = (JCUnary)top; 
        if (t.getTag() != tree.getTag()) throw new NoMatchException();
        top = t.arg;
        scan(tree.arg);
        top = t;
    }

    public void visitBinary(JCBinary tree) {
        JCBinary t = (JCBinary)top; 
        if (t.getTag() != tree.getTag()) throw new NoMatchException();
        top = t.lhs;
        scan(tree.lhs);
        top = t.rhs;
        scan(tree.rhs);
        top = t;
    }

//    public void visitTypeCast(JCTypeCast tree) {
//        scan(tree.clazz);
//        scan(tree.expr);
//    }
//
//    public void visitTypeTest(JCInstanceOf tree) {
//        scan(tree.expr);
//        scan(tree.clazz);
//    }
//
//    public void visitIndexed(JCArrayAccess tree) {
//        scan(tree.indexed);
//        scan(tree.index);
//    }
//
//    public void visitSelect(JCFieldAccess tree) {
//        scan(tree.selected);
//    }
//
//    public void visitReference(JCMemberReference tree) {
//        scan(tree.expr);
//        scan(tree.typeargs);
//    }
//
    @Override
    public void visitIdent(JCIdent tree) {
        JCIdent t = (JCIdent) top;
        if (t.sym != tree.sym) throw new NoMatchException();
    }

//    public void visitLiteral(JCLiteral tree) {
//    }
//
//    public void visitTypeIdent(JCPrimitiveTypeTree tree) {
//    }
//
//    public void visitTypeArray(JCArrayTypeTree tree) {
//        scan(tree.elemtype);
//    }
//
//    public void visitTypeApply(JCTypeApply tree) {
//        scan(tree.clazz);
//        scan(tree.arguments);
//    }
//
//    public void visitTypeUnion(JCTypeUnion tree) {
//        scan(tree.alternatives);
//    }
//
//    public void visitTypeIntersection(JCTypeIntersection tree) {
//        scan(tree.bounds);
//    }
//
//    public void visitTypeParameter(JCTypeParameter tree) {
//        scan(tree.annotations);
//        scan(tree.bounds);
//    }
//
//    @Override
//    public void visitWildcard(JCWildcard tree) {
//        scan(tree.kind);
//        if (tree.inner != null)
//            scan(tree.inner);
//    }
//
//    @Override
//    public void visitTypeBoundKind(TypeBoundKind that) {
//    }
//
//    public void visitModifiers(JCModifiers tree) {
//        scan(tree.annotations);
//    }
//
//    public void visitAnnotation(JCAnnotation tree) {
//        scan(tree.annotationType);
//        scan(tree.args);
//    }
//
//    public void visitAnnotatedType(JCAnnotatedType tree) {
//        scan(tree.annotations);
//        scan(tree.underlyingType);
//    }
//
//    public void visitErroneous(JCErroneous tree) {
//    }
//
//    public void visitLetExpr(LetExpr tree) {
//        scan(tree.defs);
//        scan(tree.expr);
//    }

    public void visitTree(JCTree tree) {
        // TODO - AND ALSO SOMETHING NOT IMPLEMENTED?
        throw new NoMatchException();
    }
    

    
    public void visitJmlBinary(JmlBinary that) {
        JmlBinary t = (JmlBinary)top;
        if (that.getOp() != t.getOp()) throw new NoMatchException();
        top = t.lhs;
        scan(that.lhs);
        top = t.rhs;
        scan(that.rhs);
        top = t;
    }
    
    public void visitJmlBlock(JmlBlock that) {
        visitBlock(that);
    }
    
//    public void visitJmlChoose(JmlChoose that) {
//        scan(that.orBlocks);
//        scan(that.elseBlock);
//    }
//
//    public void visitJmlClassDecl(JmlClassDecl that) {
//        if (scanMode == AST_SPEC_MODE) {
//            if (!that.isTypeChecked()) throw new RuntimeException("AST_SPEC_MODE requires that the Class be type-checked; class " + that.name + " is not.");
//        }
//        boolean isJML = (that.mods.flags & Utils.JMLBIT) != 0; // SHould use Utils.isJML(), but it needs a context value
//        if (!isJML || scanMode == AST_JML_MODE) visitClassDef(that);
//        if (scanMode == AST_SPEC_MODE) {
//            JmlSpecs.TypeSpecs ms = that.typeSpecs;
//            if (ms != null) {
//                scan(ms.modifiers);
//                scan(ms.clauses);
//                scan(ms.decls);
//            } else {
//                // FIXME - why does this happen: System.out.println("No specs found for " + that.name);
//            }
//        }
//        if (scanMode == AST_JML_MODE) {
//            JmlSpecs.TypeSpecs ms = that.typeSpecs;
//            // already done - scan(ms.modifiers);
//            if (ms != null) scan(ms.clauses);
//            if (ms != null) scan(ms.decls);
//        }
//    }
//
//    public void visitJmlCompilationUnit(JmlCompilationUnit that) {
//        scan(that.packageAnnotations);
//        scan(that.pid); // package id
//        scan(that.defs);
////        if (scanMode == AST_JML_MODE) scan(that.parsedTopLevelModelTypes);
////        if (scanMode == AST_SPEC_MODE) scan(that.specsTopLevelModelTypes);
//    }
//
//    public void visitJmlMethodSig(JmlMethodSig that) {
//        scan(that.expression);
//        scan(that.argtypes);
//    }
//
//    public void visitJmlDoWhileLoop(JmlDoWhileLoop that) {
//        scan(that.loopSpecs);
//        visitDoLoop(that);
//    }
//
//    public void visitJmlEnhancedForLoop(JmlEnhancedForLoop that) {
//        scan(that.loopSpecs);
//        visitForeachLoop(that);
//    }
//
//    public void visitJmlForLoop(JmlForLoop that) {
//        scan(that.loopSpecs);
//        visitForLoop(that);
//    }
//
//    public void visitJmlGroupName(JmlGroupName tree) {
//        scan(tree.selection);
//    }
//
//    public void visitJmlImport(JmlImport that) {
//        visitImport(that);
//    }
//    
//    public void visitJmlLabeledStatement(JmlLabeledStatement that) {
////        scan(that.extraStatements.toList());
//        scan(that.body);
//    }
//    
//    public void visitJmlLblExpression(JmlLblExpression that) {
//        scan(that.expression);
//    }
//
//    public void visitJmlMethodClauseCallable(JmlMethodClauseCallable tree) {
//        scan(tree.keyword);
//        scan(tree.methodSignatures);
//    }
//
//    public void visitJmlMethodClauseConditional(JmlMethodClauseConditional tree) {
//        scan(tree.expression);
//        scan(tree.predicate);
//    }
//
//    public void visitJmlMethodClauseDecl(JmlMethodClauseDecl tree) {
//        for (JCTree.JCVariableDecl s: tree.decls) {
//            scan(s);
//        }
//    }
//
//    public void visitJmlMethodClauseExpr(JmlMethodClauseExpr tree) {
//        scan(tree.expression);
//    }
//
//    public void visitJmlMethodClauseGroup(JmlMethodClauseGroup tree) {
//        for (JCTree t: tree.cases) {
//            scan(t);
//        }
//    }
//
//    public void visitJmlMethodClauseSignals(JmlMethodClauseSignals tree) {
//        scan(tree.expression);
//    }
//
//    public void visitJmlMethodClauseSigOnly(JmlMethodClauseSignalsOnly tree) {
//        scan(tree.list);
//    }
//
//    public void visitJmlMethodClauseStoreRef(JmlMethodClauseStoreRef tree) {
//        scan(tree.list);
//    }
//
//    public void visitJmlMethodDecl(JmlMethodDecl that) {
//        if (scanMode == AST_SPEC_MODE) {
//            JmlSpecs.MethodSpecs ms = that.methodSpecsCombined;
//            scan(ms.mods);
//            scan(ms.cases);
//        }
//        if (scanMode == AST_JML_MODE) {
//            scan(that.cases);
//        }
//        visitMethodDef(that);
//    }
//
//    public void visitJmlMethodInvocation(JmlMethodInvocation that) {
//        scan(that.args);
//    }
//    
//    public void visitJmlMethodSpecs(JmlMethodSpecs tree) {
//        scan(tree.cases);
//        scan(tree.impliesThatCases);
//        scan(tree.forExampleCases);
//    }
//
//    public void visitJmlModelProgramStatement(JmlModelProgramStatement that) {
//        scan(that.item);
//    }
//
//    public void visitJmlPrimitiveTypeTree(JmlPrimitiveTypeTree tree) {
//        // no children to scan
//    }
//
//    public void visitJmlQuantifiedExpr(JmlQuantifiedExpr that) {
//        scan(that.decls);
//        scan(that.range);
//        scan(that.value);
//        scan(that.racexpr);
//    }
//
//    public void visitJmlSetComprehension(JmlSetComprehension that) {
//        scan(that.newtype);
//        scan(that.variable);
//        scan(that.predicate);
//    }
//
//    public void visitJmlSingleton(JmlSingleton that) {
//        // no children to scan
//    }
//
//    public void visitJmlSpecificationCase(JmlSpecificationCase tree) {
//        scan(tree.modifiers);
//        scan(tree.clauses);
//        scan(tree.block);
//    }
//
//    public void visitJmlStatement(JmlStatement tree) {
//        scan(tree.statement);
//    }
//    
//    /** inlined_loop statement */
//    public void visitJmlInlinedLoop(JmlInlinedLoop that) {
//    }
//
//    public void visitJmlStatementShow(JmlStatementShow tree) {
//        for (JCExpression e: tree.expressions) scan(e);
//    }
//    
//    public void visitJmlStatementDecls(JmlStatementDecls tree) {
//        for (JCTree.JCStatement s : tree.list) {
//            scan(s);
//        }
//    }
//
//    public void visitJmlStatementExpr(JmlStatementExpr tree) {
//        scan(tree.expression);
//        scan(tree.optionalExpression);
//    }
//
//    public void visitJmlStatementHavoc(JmlStatementHavoc tree) {
//        scan(tree.storerefs);
//    }
//
//    public void visitJmlStatementLoopExpr(JmlStatementLoopExpr tree) {
//        scan(tree.expression);
//    }
//    
//    public void visitJmlStatementLoopModifies(JmlStatementLoopModifies tree) {
//        scan(tree.storerefs);
//    }
//    
//    public void visitJmlStatementSpec(JmlStatementSpec tree) {
//        scan(tree.statementSpecs);
//        scan(tree.statements);
//    }
//    
//    public void visitJmlStoreRefArrayRange(JmlStoreRefArrayRange that) {
//        scan(that.expression);
//        scan(that.lo);
//        scan(that.hi);
//    }
//
//    public void visitJmlStoreRefKeyword(JmlStoreRefKeyword that) {
//        // nothing to scan
//    }
//
//    public void visitJmlStoreRefListExpression(JmlStoreRefListExpression that) {
//        for (JCTree t: that.list) {
//            scan(t);
//        }
//    }
//
//    public void visitJmlTypeClauseConditional(JmlTypeClauseConditional tree) {
//        scan(tree.modifiers);
//        scan(tree.identifier);
//        scan(tree.expression);
//    }
//
//    public void visitJmlTypeClauseConstraint(JmlTypeClauseConstraint tree) {
//        scan(tree.modifiers);
//        scan(tree.expression);
//        scan(tree.sigs);
//    }
//
//    public void visitJmlTypeClauseDecl(JmlTypeClauseDecl tree) {
//        scan(tree.modifiers);
//        scan(tree.decl);
//    }
//
//    public void visitJmlTypeClauseExpr(JmlTypeClauseExpr tree) {
//        scan(tree.modifiers);
//        scan(tree.expression);
//    }
//
//    public void visitJmlTypeClauseIn(JmlTypeClauseIn tree) {
//        scan(tree.modifiers);
//        for (JmlGroupName g: tree.list) {
//            scan(g);
//        }
//    }
//
//    public void visitJmlTypeClauseInitializer(JmlTypeClauseInitializer tree) {
//        scan(tree.modifiers);
//        scan(tree.specs);
//    }
//
//    public void visitJmlTypeClauseMaps(JmlTypeClauseMaps tree) {
//        scan(tree.modifiers);
//        scan(tree.expression);
//        for (JmlGroupName g: tree.list) {
//            scan(g);
//        }
//    }
//
//    public void visitJmlTypeClauseMonitorsFor(JmlTypeClauseMonitorsFor tree) {
//        scan(tree.modifiers);
//        scan(tree.identifier);
//        for (JCTree.JCExpression e: tree.list) {
//            scan(e);
//        }
//    }
//
//    public void visitJmlTypeClauseRepresents(JmlTypeClauseRepresents tree) {
//        scan(tree.modifiers);
//        scan(tree.ident);
//        scan(tree.expression);
//    }
//
//    public void visitJmlVariableDecl(JmlVariableDecl that) {
//        visitVarDef(that);
//        if (scanMode == AST_SPEC_MODE) {
//            if (that.fieldSpecsCombined != null) {
//                scan(that.fieldSpecsCombined.mods);
//                scan(that.fieldSpecsCombined.list);
//            }
//        }
//        if (scanMode == AST_JML_MODE) {
//            if (that.fieldSpecs != null) {
//                scan(that.fieldSpecs.mods);
//                scan(that.fieldSpecs.list);
//            }
//        }
//    }
//
//    public void visitJmlWhileLoop(JmlWhileLoop that) {
//        scan(that.loopSpecs);
//        visitWhileLoop(that);
//    }

}
