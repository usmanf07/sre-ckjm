
package gr.spinellis.ckjm;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.apache.bcel.Repository;
import org.apache.bcel.Constants;
import org.apache.bcel.util.*;
import java.io.*;
import java.util.*;

// Refactored: Extracted ClassMetricsContainer into its own class
public class MetricsFilter {
    private static boolean includeJdk = false;
    private static boolean onlyPublic = false;

    public static boolean isJdkIncluded() { return includeJdk; }
    public static boolean includeAll() { return !onlyPublic; }

    // Refactored: Extracted method for running metrics
    public static void runMetrics(String[] files, CkjmOutputHandler outputHandler) {
        ClassMetricsContainer cm = new ClassMetricsContainer();
        for (String file : files)
            processClass(cm, file);
        cm.printMetrics(outputHandler);
    }

    // Refactored: Extracted method for processing a single class
    private static void processClass(ClassMetricsContainer cm, String clspec) {
        JavaClass jc = loadClass(clspec);
        if (jc != null) {
            ClassVisitor visitor = ClassVisitor.createInstance(jc, cm);
            visitor.start();
            visitor.end();
        }
    }

    // Refactored: Extracted method for loading a class
    private static JavaClass loadClass(String clspec) {
        int spc = clspec.indexOf(' ');
        if (spc != -1) {
            String jar = clspec.substring(0, spc);
            clspec = clspec.substring(spc + 1);
            return loadClassFromJar(jar, clspec);
        } else {
            return loadClassFromFile(clspec);
        }
    }

    // Refactored: Extracted method for loading a class from a JAR file
    private static JavaClass loadClassFromJar(String jar, String clspec) {
        try {
            JavaClass jc = null;
            jc = new ClassParser(jar, clspec).parse();
            return jc;
        } catch (IOException e) {
            throw new RuntimeException("Error loading " + clspec + " from " + jar + ": " + e);
        }
    }

    // Refactored: Extracted method for loading a class from a file
    private static JavaClass loadClassFromFile(String clspec) {
        try {
            JavaClass jc = null;
            jc = new ClassParser(clspec).parse();
            return jc;
        } catch (IOException e) {
            throw new RuntimeException("Error loading " + clspec + ": " + e);
        }
    }

    // Refactored: Extracted method for parsing command-line options
    private static void parseCommandLineOptions(String[] argv) {
        for (String arg : argv) {
            if (arg.equals("-s")) {
                includeJdk = true;
            } else if (arg.equals("-p")) {
                onlyPublic = true;
            }
        }
    }

    // Refactored: Extracted method for processing classes from standard input
    private static void processClassesFromStandardInput(ClassMetricsContainer cm) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            String s;
            while ((s = in.readLine()) != null)
                processClass(cm, s);
        } catch (IOException e) {
            System.err.println("Error reading line: " + e);
            System.exit(1);
        }
    }

    // Refactored: Extracted method for processing classes from command-line arguments
    private static void processClassesFromArguments(ClassMetricsContainer cm, String[] argv) {
        for (String arg : argv) {
            if (!arg.equals("-s") && !arg.equals("-p")) {
                processClass(cm, arg);
            }
        }
    }

    public static void main(String[] argv) {
        parseCommandLineOptions(argv);
        ClassMetricsContainer cm = new ClassMetricsContainer();
        if (argv.length == 0) {
            processClassesFromStandardInput(cm);
        } else {
            processClassesFromArguments(cm, argv);
        }
        CkjmOutputHandler handler = new PrintPlainResults(System.out);
        cm.printMetrics(handler);
    }
}
