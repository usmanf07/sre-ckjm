///*
// * $Id: \\dds\\src\\Research\\ckjm.RCS\\src\\gr\\spinellis\\ckjm\\ClassVisitor.java,v 1.21 2012/04/04 13:08:23 dds Exp $
// *
// * (C) Copyright 2005 Diomidis Spinellis
// *
// * Permission to use, copy, and distribute this software and its
// * documentation for any purpose and without fee is hereby granted,
// * provided that the above copyright notice appear in all copies and that
// * both that copyright notice and this permission notice appear in
// * supporting documentation.
// *
// * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED
// * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
// * MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
// */
//package gr.spinellis.ckjm;
//
//import org.apache.bcel.classfile.*;
//import org.apache.bcel.generic.*;
//import org.apache.bcel.Repository;
//import org.apache.bcel.Constants;
//import org.apache.bcel.util.*;
//import java.io.*;
//import java.util.*;
//import java.lang.reflect.Modifier;
//import ExtractedClasses.*;
//
///**
// * Visit a class updating its Chidamber-Kemerer metrics.
// *
// * @see ClassMetrics
// * @version $Revision: 1.21 $
// * @author <a href="http://www.spinellis.gr">Diomidis Spinellis</a>
// */
//public class ClassVisitor extends org.apache.bcel.classfile.EmptyVisitor {
//
//    /**
//     * The class being visited.
//     */
//    private JavaClass visitedClass;
//    /**
//     * The class's constant pool.
//     */
//    public VisitJavaClass myjavaclass;
//    private ConstantPoolGen cp;
//    /**
//     * The class's fully qualified name.
//     */
//    private String myClassName;
//    /**
//     * The container where metrics for all classes are stored.
//     */
//    private ClassMetricsContainer cmap;
//    /**
//     * The emtrics for the class being visited.
//     */
//    private ClassMetrics cm;
//
//    public Register register;
//    /* Classes encountered.
//     * Its cardinality is used for calculating the CBO.
//     */
//    private HashSet<String> efferentCoupledClasses = new HashSet<String>();
//    /**
//     * Methods encountered. Its cardinality is used for calculating the RFC.
//     */
//    private HashSet<String> responseSet = new HashSet<String>();
//    /**
//     * Use of fields in methods. Its contents are used for calculating the LCOM.
//     * We use a Tree rather than a Hash to calculate the intersection in O(n)
//     * instead of O(n*n).
//     */
//    ArrayList<TreeSet<String>> mi = new ArrayList<TreeSet<String>>();
//
//    private ClassVisitor(JavaClass jc, ConstantPoolGen constantPool, ClassMetricsContainer classMap, String className, ClassMetrics metrics) {
//        visitedClass = jc;
//        cp = constantPool;
//        cmap = classMap;
//        myClassName = className;
//        cm = metrics;
//        register = new Register();
//        myjavaclass = new VisitJavaClass();
//        
//    }
//
//    // Other methods of ClassVisitor...
//    public static ClassVisitor createInstance(JavaClass jc, ClassMetricsContainer classMap) {
//        ConstantPoolGen cp = new ConstantPoolGen(jc.getConstantPool());
//        String myClassName = jc.getClassName();
//        ClassMetrics cm = classMap.getMetrics(myClassName);
//        return new ClassVisitor(jc, cp, classMap, myClassName, cm);
//    }
//
//    /**
//     * Return the class's metrics container.
//     */
//    public ClassMetrics getMetrics() {
//        return cm;
//    }
//
//    public void start() {
//        visitJavaClass(visitedClass);
//    }
//
//    /**
//     * Calculate the class's metrics based on its elements.
//     */
//    public void visitJavaClass(JavaClass jc) {
//        
//        myjavaclass.visit( jc);
//                
//        
//        processClassAttributes(jc);
//        processClassHierarchy(jc);
//        processInterfaces(jc);
//        processFields(jc);
//        processMethods(jc);
//    }
//
//    private void processClassAttributes(JavaClass jc) {
//        String superName = jc.getSuperclassName();
//
//        cm.setVisited();
//        if (jc.isPublic()) {
//            cm.setPublic();
//        }
//        ClassMetrics superClassMetrics = cmap.getMetrics(superName);
//        superClassMetrics.incNoc();
//    }
//
//    private void processClassHierarchy(JavaClass jc) {
//        try {
//            cm.setDit(jc.getSuperClasses().length);
//        } catch (ClassNotFoundException ex) {
//            System.err.println("Error obtaining all superclasses of " + jc);
//        }
//    }
//
//    private void processInterfaces(JavaClass jc) {
//        String[] interfaceNames = jc.getInterfaceNames();
//        for (String interfaceName : interfaceNames) {
//            registercall(interfaceName);
//        }
//    }
//
//    private void processFields(JavaClass jc) {
//        Field[] fields = jc.getFields();
//        for (Field field : fields) {
//            field.accept(this);
//        }
//    }
//
//    private void processMethods(JavaClass jc) {
//        Method[] methods = jc.getMethods();
//        for (Method method : methods) {
//            method.accept(this);
//        }
//    }
//
//
//    /* Add a given method to our response set */
//    void registerMethodInvocation(String className, String methodName, Type[] args) {
//        registercall(className);
//        /* Measuring decision: calls to JDK methods are included in the RFC calculation */
//        incRFC(className, methodName, args);
//    }
//
//    public void visitField(Field field) {
//        registercall(field.getType());
//    }
//
//    private void incRFC(String className, String methodName, Type[] arguments) {
//        String argumentList = Arrays.asList(arguments).toString();
//        // remove [ ] chars from begin and end
//        String args = argumentList.substring(1, argumentList.length() - 1);
//        String signature = className + "." + methodName + "(" + args + ")";
//        responseSet.add(signature);
//    }
//
//    /**
//     * Called when a method invocation is encountered.
//     */
//    public void visitMethod(Method method) {
//        MethodGen mg = new MethodGen(method, visitedClass.getClassName(), cp);
//
//        processMethodSignature(mg);
//        processMethodExceptions(mg);
//        processMethodRFC(method, mg);
//        processMethodMetrics(method);
//        createMethodVisitorAndStart(mg);
//    }
//
//    private void processMethodSignature(MethodGen mg) {
//        Type resultType = mg.getReturnType();
//        Type[] argTypes = mg.getArgumentTypes();
//
//        registercall(resultType);
//        for (Type argType : argTypes) {
//            registercall(argType);
//        }
//    }
//
//    private void processMethodExceptions(MethodGen mg) {
//        String[] exceptions = mg.getExceptions();
//        for (String exception : exceptions) {
//            registercall(exception);
//        }
//    }
//
//    private void processMethodRFC(Method method, MethodGen mg) {
//        Type[] argTypes = mg.getArgumentTypes();
//        incRFC(myClassName, method.getName(), argTypes);
//    }
//
//    private void processMethodMetrics(Method method) {
//        cm.incWmc();
//        if (Modifier.isPublic(method.getModifiers())) {
//            cm.incNpm();
//        }
//        mi.add(new TreeSet<String>());
//    }
//
//    private void createMethodVisitorAndStart(MethodGen mg) {
//        MethodVisitor factory = new MethodVisitor(mg, this);
//        factory.start();
//    }
//
//    public void registercall(Type T) {
//        register.registerCoupling(T, myClassName, mi, efferentCoupledClasses, cmap);
//    }
//
//    public void registercall(String classN) {
//        register.registerCoupling(classN, myClassName, efferentCoupledClasses, cmap);
//    }
//
//    public void registerFieldAccessCaller(String classNa, String fieldNa) {
//        register.registerFieldAccess(classNa, fieldNa, myClassName, mi, efferentCoupledClasses, cmap);
//    }
//
//    public void end() {
//        cm.setCbo(efferentCoupledClasses.size());
//        cm.setRfc(responseSet.size());
//
//        int lcom = 0;
//        for (int i = 0; i < mi.size(); i++) {
//            for (int j = i + 1; j < mi.size(); j++) {
//                /* A shallow unknown-type copy is enough */
//                TreeSet<?> intersection = (TreeSet<?>) mi.get(i).clone();
//                intersection.retainAll(mi.get(j));
//                if (intersection.size() == 0) {
//                    lcom++;
//                } else {
//                    lcom--;
//                }
//            }
//        }
//        cm.setLcom(lcom > 0 ? lcom : 0);
//    }
//}
