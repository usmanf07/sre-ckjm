/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ExtractedClasses;

/**
 *
 * @author hp
 */
import gr.spinellis.ckjm.ClassVisitor;
import org.apache.bcel.generic.CodeExceptionGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;

public class ExceptionHandlerUpdater {
    private final MethodGen mg;
    private final ClassVisitor cv;

    public ExceptionHandlerUpdater(MethodGen mg, ClassVisitor cv) {
        this.mg = mg;
        this.cv = cv;
    }

    public void updateExceptionHandlers() {
        CodeExceptionGen[] handlers = mg.getExceptionHandlers();
        for (CodeExceptionGen handler : handlers) {
            Type catchType = handler.getCatchType();
            if (catchType != null) {
                cv.getRegister().registerCoupling(catchType);
            }
        }
    }
}