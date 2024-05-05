/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ExtractedClasses;

import gr.spinellis.ckjm.CkjmOutputHandler;
import gr.spinellis.ckjm.PrintPlainResults;
import gr.spinellis.ckjm.ant.PrintXmlResults;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 *
 * @author usman
 */
public interface OutputHandlerFactory {
    CkjmOutputHandler createOutputHandler(String format, OutputStream outputStream);
}

class SimpleOutputHandlerFactory implements OutputHandlerFactory {
    public CkjmOutputHandler createOutputHandler(String format, OutputStream outputStream) {
        if ("xml".equals(format)) {
            return new PrintXmlResults(new PrintStream(outputStream));
        } else {
            return new PrintPlainResults(new PrintStream(outputStream));
        }
    }
}

