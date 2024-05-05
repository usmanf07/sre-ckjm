/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ExtractedClasses;

import gr.spinellis.ckjm.ClassMetrics;
import gr.spinellis.ckjm.ClassMetricsContainer;
import gr.spinellis.ckjm.ClassVisitor;
import gr.spinellis.ckjm.MetricsFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeSet;
import org.apache.bcel.Constants;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.Type;

/**
 *
 * @author hp
 */
public class Register {

    private ClassVisitor cv;
    private HashSet<String> responseSet = new HashSet<String>();

    public Register(ClassVisitor temp) {
        cv = temp;
    }
    public void registerMethodInvocation(String className, String methodName, Type[] args) {
       registerCoupling(className);
        /* Measuring decision: calls to JDK methods are included in the RFC calculation */
        incRFC(className, methodName, args);
    }
    public void registerFieldAccess(String className, String fieldName) {
        registerCoupling(className);
        if (className.equals(cv.getClassName())) {
            cv.getMi().get(cv.getMi().size() - 1).add(fieldName);
        }
    }

    static String className(Type t) {
        String ts = t.toString();

        if (t.getType() <= Constants.T_VOID) {
            return "java.PRIMITIVE";
        } else if (t instanceof ArrayType) {
            ArrayType at = (ArrayType) t;
            return className(at.getBasicType());
        } else {
            return t.toString();
        }
    }

    public void registerCoupling(Type t) {
        registerCoupling(className(t));
    }

    public void registerCoupling(String className) {
        /* Measuring decision: don't couple to Java SDK */
        if ((MetricsFilter.isJdkIncluded()
                || !ClassMetrics.isJdkClass(className))
                && !cv.getClassName().equals(className)) {
            cv.getEfferentCoupledClasses().add(className);
            cv.getClassMetricsContainer().getMetrics(className).addAfferentCoupling(cv.getClassName());
        }
    }

    public void incRFC(String className, String methodName, Type[] arguments) {
        String argumentList = Arrays.asList(arguments).toString();
        // remove [ ] chars from begin and end
        String args = argumentList.substring(1, argumentList.length() - 1);
        String signature = className + "." + methodName + "(" + args + ")";
        responseSet.add(signature);
    }

    public int responseSize() {
        return responseSet.size();
    }

}
