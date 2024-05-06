/*
 * $Id: \\dds\\src\\Research\\ckjm.RCS\\src\\gr\\spinellis\\ckjm\\ant\\CkjmTask.java,v 1.3 2007/07/25 15:19:09 dds Exp $
 *
 * (C) Copyright 2005 Diomidis Spinellis, Julien Rentrop
 *
 * Permission to use, copy, and distribute this software and its
 * documentation for any purpose and without fee is hereby granted,
 * provided that the above copyright notice appear in all copies and that
 * both that copyright notice and this permission notice appear in
 * supporting documentation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 * MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */

package gr.spinellis.ckjm.ant;

import ExtractedClasses.OutputHandlerFactory;
import gr.spinellis.ckjm.CkjmOutputHandler;
import gr.spinellis.ckjm.MetricsFilter;
import gr.spinellis.ckjm.PrintPlainResults;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.Path;



/**
 * Ant task definition for the CKJM metrics tool.
 *
 * @version $Revision: 1.3 $
 * @author Julien Rentrop
 */
public class CkjmTask extends MatchingTask {
    private File outputFile;

    private File classDir;

    private Path extdirs;

    private String format;
    
    private OutputHandlerFactory factory;
    
    public CkjmTask(OutputHandlerFactory factory) {
        this.factory = factory;
        this.format = "plain"; // Default format
    }

    /**
     * Sets the format of the output file.
     *
     * @param format
     *            the format of the output file. Allowable values are 'plain' or
     *            'xml'.
     */
    public void setFormat(String format) {
        this.format = format;

    }

    /**
     * Sets the outputfile
     *
     * @param outputfile
     *            Location of outputfile
     */
    public void setOutputfile(File outputfile) {
        this.outputFile = outputfile;
    }

    /**
     * Sets the dir which contains the class files that will be analyzed
     *
     * @param classDir
     *            Location of class files
     */
    public void setClassdir(File classDir) {
        this.classDir = classDir;
    }

    /**
     * Sets the extension directories that will be used by ckjm.
     * @param extdirs a path containing .jar files
     */
    public void setExtdirs(Path e) {
        if (extdirs == null) {
            extdirs = new Path(getProject());
        }
        extdirs.append(e);
    }

    /**
     * Gets the extension directories that will be used by ckjm.
     * @return the extension directories as a path
     */
    public Path getExtdirs() {
        return extdirs;
    }

    /**
     * Adds a path to extdirs.
     * @return a path to be modified
     */
    public Path createExtdirs() {
        if (extdirs == null) {
            extdirs = new Path(getProject());
        }
        return extdirs.createPath();
    }
    
    private void handleOutput(String[] files) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(outputFile)) {
            CkjmOutputHandler outputHandler = factory.createOutputHandler(format, outputStream);

            outputHandler.startOutput();
            MetricsFilter.runMetrics(files, outputHandler);
            outputHandler.endOutput();
        } catch (IOException ioe) {
            throw new BuildException("Error file handling: " + ioe.getMessage());
        }
    }
    
    private void validateClassDirectory() throws BuildException {
        if (classDir == null) {
            throw new BuildException("classdir attribute must be set!");
        }
        if (!classDir.exists()) {
            throw new BuildException("classdir does not exist!");
        }
        if (!classDir.isDirectory()) {
            throw new BuildException("classdir is not a directory!");
        }
    }

    private void configureExtensionDirectories() {
        if (extdirs != null && extdirs.size() > 0) {
            String existingDirs = System.getProperty("java.ext.dirs", "");
            if (existingDirs.isEmpty()) {
                System.setProperty("java.ext.dirs", extdirs.toString());
            } else {
                System.setProperty("java.ext.dirs", existingDirs + File.pathSeparator + extdirs);
            }
        }
    }

    /**
     * Executes the CKJM Ant Task. This method redirects the output of the CKJM
     * tool to a file. When XML format is used it will buffer the output and
     * translate it to the XML format.
     *
     * @throws BuildException
     *             if an error occurs.
     */
    
    public void execute() throws BuildException {
        // Validate class directory
        validateClassDirectory();

        // Configure extension directories
        configureExtensionDirectories();

        DirectoryScanner ds = super.getDirectoryScanner(classDir);

        String files[] = ds.getIncludedFiles();
        if (files.length == 0) {
            log("No class files in specified directory " + classDir);
        } else {
            for (int i = 0; i < files.length; i++) {
                files[i] = classDir.getPath() + File.separatorChar + files[i];
            }

           try {
                handleOutput(files);
            } catch (IOException e) {
                throw new BuildException("Error during output handling: " + e.getMessage());
            }
        }
    }
}
