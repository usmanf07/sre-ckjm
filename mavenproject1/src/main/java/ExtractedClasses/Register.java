/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ExtractedClasses;

import gr.spinellis.ckjm.ClassMetrics;
import gr.spinellis.ckjm.ClassMetricsContainer;
import gr.spinellis.ckjm.MetricsFilter;
import java.util.ArrayList;
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
    
    
    public void registerFieldAccess(String className, String fieldName,String myClassName, ArrayList<TreeSet<String>> mi,HashSet<String> efferentCoupledClasses,ClassMetricsContainer cmap) {
	registerCoupling(className, myClassName,efferentCoupledClasses,cmap);
	if (className.equals(myClassName))
	    mi.get(mi.size() - 1).add(fieldName);
    }
    static String className(Type t) {
	String ts = t.toString();

	if (t.getType() <= Constants.T_VOID) {
	    return "java.PRIMITIVE";
	} else if(t instanceof ArrayType) {
	    ArrayType at = (ArrayType)t;
	    return className(at.getBasicType());
	} else {
	    return t.toString();
	}
    }
    
    
     public void registerCoupling(Type t,String myClassName, ArrayList<TreeSet<String>> mi,HashSet<String> efferentCoupledClasses,ClassMetricsContainer cmap) {
	registerCoupling(className(t),myClassName,efferentCoupledClasses,cmap);
    }
      public void registerCoupling(String className, String myClassName,HashSet<String> efferentCoupledClasses, ClassMetricsContainer cmap) {
	/* Measuring decision: don't couple to Java SDK */
	if ((MetricsFilter.isJdkIncluded() ||
	     !ClassMetrics.isJdkClass(className)) &&
	    !myClassName.equals(className)) {
	    efferentCoupledClasses.add(className);
	    cmap.getMetrics(className).addAfferentCoupling(myClassName);
	}
    }
}
