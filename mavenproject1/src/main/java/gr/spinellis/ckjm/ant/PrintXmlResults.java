package gr.spinellis.ckjm.ant;

import gr.spinellis.ckjm.CkjmOutputHandler;
import gr.spinellis.ckjm.ClassMetrics;

import java.io.PrintStream;

/**
 * XML output formatter
 *
 * @author Julien Rentrop
 */
public class PrintXmlResults implements CkjmOutputHandler {
    private final PrintStream printStream;

    public PrintXmlResults(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public void startOutput() {
        printStream.println("<?xml version=\"1.0\"?>");
        printStream.println("<ckjm>");
    }

    @Override
    public void handleClass(String name, ClassMetrics metrics) {
        printStream.printf("<class>%n");
        printStream.printf("    <name>%s</name>%n", name);
        printStream.printf("    <wmc>%d</wmc>%n", metrics.getWmc());
        printStream.printf("    <dit>%d</dit>%n", metrics.getDit());
        printStream.printf("    <noc>%d</noc>%n", metrics.getNoc());
        printStream.printf("    <cbo>%d</cbo>%n", metrics.getCbo());
        printStream.printf("    <rfc>%d</rfc>%n", metrics.getRfc());
        printStream.printf("    <lcom>%d</lcom>%n", metrics.getLcom());
        printStream.printf("    <ca>%d</ca>%n", metrics.getCa());
        printStream.printf("    <npm>%d</npm>%n", metrics.getNpm());
        printStream.printf("</class>%n");
    }

    @Override
    public void endOutput() {
        printStream.println("</ckjm>");
    }
}
