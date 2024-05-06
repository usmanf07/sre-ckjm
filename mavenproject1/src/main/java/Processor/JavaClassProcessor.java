/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Processor;
import gr.spinellis.ckjm.*;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
/**
 *
 * @author hp
 */
// JavaClassProcessor.java
public class JavaClassProcessor {
   
    
    ClassVisitor cv;
    
    public JavaClassProcessor(ClassVisitor temp)
    {
        cv = temp;
    }
    
    
    public void ClassProcessor(JavaClass jc)
    {
        processClassAttributes(jc);
        processClassHierarchy(jc);
        processInterfaces(jc);
        processFields(jc);
        processMethods(jc);
    }
    // Add other necessary methods here
    private void processClassAttributes(JavaClass jc) {
        String superName = jc.getSuperclassName();

        cv.getClassMetrics().setVisited();
        if (jc.isPublic()) {
            cv.getClassMetrics().setPublic();
        }
        ClassMetrics superClassMetrics = cv.getClassMetricsContainer().getMetrics(superName);
        superClassMetrics.incNoc();
    }

    private void processClassHierarchy(JavaClass jc) {
        try {
             cv.getClassMetrics().setDit(jc.getSuperClasses().length);
        } catch (ClassNotFoundException ex) {
            System.err.println("Error obtaining all superclasses of " + jc);
        }
    }

    private void processInterfaces(JavaClass jc) {
        String[] interfaceNames = jc.getInterfaceNames();
        for (String interfaceName : interfaceNames) {
            cv.getRegister().registerCoupling(interfaceName);
        }
    }

    private void processFields(JavaClass jc) {
        Field[] fields = jc.getFields();
        for (Field field : fields) {
            field.accept(cv);
        }
    }

    private void processMethods(JavaClass jc) {
        Method[] methods = jc.getMethods();
        for (Method method : methods) {
            method.accept(cv);
        }
    }
}

