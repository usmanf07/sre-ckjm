/*
 * $Id: \\dds\\src\\Research\\ckjm.RCS\\src\\gr\\spinellis\\ckjm\\MethodVisitor.java,v 1.8 2005/10/09 15:36:08 dds Exp $
 *
 * (C) Copyright 2005 Diomidis Spinellis
 *
 * Permission to use, copy, and distribute this software and its
 * documentation for any purpose and without fee is hereby granted,
 * provided that the above copyright notice appear in all copies and that
 * both that copyright notice and this permission notice appear in
 * supporting documentation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 * MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */
package gr.spinellis.ckjm;

import ExtractedClasses.ExceptionHandlerUpdater;
import org.apache.bcel.generic.*;
import org.apache.bcel.Constants;
import org.apache.bcel.util.*;
import java.util.*;

/**
 * Visit a method calculating the class's Chidamber-Kemerer metrics. A helper
 * class for ClassVisitor.
 *
 * @see ClassVisitor
 * @version $Revision: 1.8 $
 * @author <a href="http://www.spinellis.gr">Diomidis Spinellis</a>
 */
public class MethodVisitor extends EmptyVisitor {

    /**
     * Method generation template.
     */
    private MethodGen mg;
    /* The class's constant pool. */
    private ConstantPoolGen cp;
    /**
     * The visitor of the class the method visitor is in.
     */
    private ClassVisitor cv;
    /**
     * The metrics of the class the method visitor is in.
     */
    private ClassMetrics cm;

    private final ExceptionHandlerUpdater exceptionHandlerUpdater;

    /**
     * Constructor.
     */
    private MethodVisitor(MethodGen m, ClassVisitor c) {
        mg = m;
        cv = c;
        cp = mg.getConstantPool();
        cm = cv.getClassMetrics();
        exceptionHandlerUpdater = new ExceptionHandlerUpdater(m, c);
    }

    // Static factory method for instantiation
    public static MethodVisitor create(MethodGen m, ClassVisitor c) {
        return new MethodVisitor(m, c);
    }

    /**
     * Start the method's visit.
     */
    public void start() {
        if (!mg.isAbstract() && !mg.isNative()) {
            for (InstructionHandle ih = mg.getInstructionList().getStart();
                    ih != null; ih = ih.getNext()) {
                Instruction i = ih.getInstruction();

                if (!visitInstruction(i)) {
                    i.accept(this);
                }
            }
            exceptionHandlerUpdater.updateExceptionHandlers();
        }
    }

    /**
     * Visit a single instruction.
     */
    public boolean visitInstruction(Instruction i) {
        short opcode = i.getOpcode();

        return ((InstructionConstants.INSTRUCTIONS[opcode] != null)
                && !(i instanceof ConstantPushInstruction)
                && !(i instanceof ReturnInstruction));
    }

    /**
     * Local variable use.
     */
    public void visitLocalVariableInstruction(LocalVariableInstruction i) {
        if (i.getOpcode() != Constants.IINC) {
            cv.getRegister().registerCoupling(i.getType(cp));
        }
    }

    /**
     * Array use.
     */
    public void visitArrayInstruction(ArrayInstruction i) {
        cv.getRegister().registerCoupling(i.getType(cp));
    }

    /**
     * Field access.
     */
    public void visitFieldInstruction(FieldInstruction i) {
        cv.getRegister().registerFieldAccess(i.getClassName(cp), i.getFieldName(cp));
        cv.getRegister().registerCoupling(i.getFieldType(cp));
    }

    public void visitInvokeInstruction(InvokeInstruction i) {
        Type[] argTypes = i.getArgumentTypes(cp);
        registerArgumentTypes(argTypes);
        Type returnType = i.getReturnType(cp);
        cv.getRegister().registerCoupling(returnType);
        /* Measuring decision: measure overloaded methods separately */
        cv.getRegister().registerMethodInvocation(i.getClassName(cp), i.getMethodName(cp), argTypes);
    }
    private void registerArgumentTypes(Type[] argTypes) {
    for (Type argType : argTypes) {
        cv.getRegister().registerCoupling(argType);
    }
}
    public void visitINSTANCEOF(INSTANCEOF i) {
        cv.getRegister().registerCoupling(i.getType(cp));
    }

    public void visitCHECKCAST(CHECKCAST i) {
        cv.getRegister().registerCoupling(i.getType(cp));
    }

    /**
     * Visit return instruction.
     */
    public void visitReturnInstruction(ReturnInstruction i) {
        cv.getRegister().registerCoupling(i.getType(cp));
    }

}
