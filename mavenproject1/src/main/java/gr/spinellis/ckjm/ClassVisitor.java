

package gr.spinellis.ckjm;

import Processor.JavaClassProcessor;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import java.util.*;
import ExtractedClasses.*;
import Processor.JavaMethodProcessor;

public class ClassVisitor extends org.apache.bcel.classfile.EmptyVisitor {

    private JavaClass visitedClass;
    public VisitJavaClass myjavaclass;
    private ConstantPoolGen cp;
    private String myClassName;

    private ClassMetricsContainer cmap;
    private JavaMethodProcessor methodProcessor;
    private ClassMetrics cm;
    private JavaClassProcessor classProcessor;

    private Register register;

    public Register getRegister() {
        return register;
    }
    private HashSet<String> efferentCoupledClasses = new HashSet<String>();
//    private HashSet<String> responseSet = new HashSet<String>();
    ArrayList<TreeSet<String>> mi = new ArrayList<TreeSet<String>>();

    
    
    public ClassMetrics getClassMetrics() {
        return cm;
    }

    public ClassMetricsContainer getClassMetricsContainer() {
        return cmap;
    }

    public String getClassName() {
        return myClassName;
    }

    public ArrayList<TreeSet<String>> getMi() {
        return mi;
    }
    
    public HashSet<String> getEfferentCoupledClasses()
    {
        return efferentCoupledClasses;
    }
    
    private ClassVisitor(JavaClass jc, ConstantPoolGen constantPool, ClassMetricsContainer classMap, String className, ClassMetrics metrics) {
        visitedClass = jc;
        cp = constantPool;
        cmap = classMap;
        myClassName = className;
        cm = metrics;
        register = new Register(this);
        myjavaclass = new VisitJavaClass();
        classProcessor = new JavaClassProcessor(this);
        methodProcessor = new JavaMethodProcessor(this);

    }

    public static ClassVisitor createInstance(JavaClass jc, ClassMetricsContainer classMap) {
        ConstantPoolGen cp = new ConstantPoolGen(jc.getConstantPool());
        String myClassName = jc.getClassName();
        ClassMetrics cm = classMap.getMetrics(myClassName);
        return new ClassVisitor(jc, cp, classMap, myClassName, cm);
    }



    public void start() {
        visitJavaClass(visitedClass);
    }

    public void visitJavaClass(JavaClass jc) {

        myjavaclass.visit(jc);
        classProcessor.ClassProcessor(jc);
    }


    public void visitField(Field field) {
        register.registerCoupling(field.getType());
    }


    public void visitMethod(Method method) {
        MethodGen mg = new MethodGen(method, visitedClass.getClassName(), cp);
        methodProcessor.processors(method, mg);
    }


    public void end() {
        calculateCBO();
        calculateRFC();
        calculateLCOM();
    }

    private void calculateCBO() {
        cm.setCbo(efferentCoupledClasses.size());
    }

    private void calculateRFC() {
        cm.setRfc(register.responseSize());
    }

    private void calculateLCOM() {
        int lcom = calculateLCOMValue();
        cm.setLcom(lcom > 0 ? lcom : 0);
    }

    private int calculateLCOMValue() {
        
        int lcom = 0;
        for (int i = 0; i < mi.size(); i++) {
            for (int j = i + 1; j < mi.size(); j++) {
                TreeSet<?> intersection = (TreeSet<?>) mi.get(i).clone();
                intersection.retainAll(mi.get(j));
                if (intersection.size() == 0) {
                    lcom++;
                } else {
                    lcom--;
                }
            }
        }
        return lcom;
    }

}

