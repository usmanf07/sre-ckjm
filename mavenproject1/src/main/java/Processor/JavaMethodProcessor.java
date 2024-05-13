/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Processor;

import gr.spinellis.ckjm.MethodVisitor;
import java.lang.reflect.Modifier;
import java.util.TreeSet;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;
import gr.spinellis.ckjm.ClassVisitor;

/**
 *
 * @author hp
 */
public class JavaMethodProcessor {

    
    private ClassVisitor cv;
    public JavaMethodProcessor(ClassVisitor temp) {
        cv = temp;
    }
    public void processors(Method method,MethodGen mg)
    {
        processMethodSignature(mg);
        processMethodExceptions(mg);
        processMethodRFC(method, mg);
        processMethodMetrics(method);
        createMethodVisitorAndStart(mg);
    }
    
    
     private void processMethodSignature(MethodGen mg) {
        Type resultType = mg.getReturnType();
        Type[] argTypes = mg.getArgumentTypes();

        cv.getRegister().registerCoupling(resultType);
        for (Type argType : argTypes) {
            cv.getRegister().registerCoupling(argType);
        }
    }

    private void processMethodExceptions(MethodGen mg) {
        String[] exceptions = mg.getExceptions();
        for (String exception : exceptions) {
            cv.getRegister().registerCoupling(exception);
        }
    }

    private void processMethodRFC(Method method, MethodGen mg) {
        Type[] argTypes = mg.getArgumentTypes();
        cv.getRegister().incRFC(cv.getClassName(), method.getName(), argTypes);
    }

    private void processMethodMetrics(Method method) {
        cv.getClassMetrics().incWmc();
        if (Modifier.isPublic(method.getModifiers())) {
            cv.getClassMetrics().incNpm();
        }
        cv.getMi().add(new TreeSet<String>());
    }

    private void createMethodVisitorAndStart(MethodGen mg) {
        MethodVisitor factory =  MethodVisitor.create(mg, cv);
        factory.start();
    }

}
