package org.jlab;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.*;
import java.util.Iterator;

public abstract class CombineFileTask extends DefaultTask {
    @InputFiles
    public abstract ConfigurableFileTree getSource();

    @OutputFile
    public abstract RegularFileProperty getDest();

    @TaskAction
    public void run() throws IOException {
        File outFile = getDest().getAsFile().get();

        try(PrintWriter writer = new PrintWriter(outFile)) {
            for (Iterator<File> it = getSource().iterator(); it.hasNext(); ) {
                File f = it.next();
                //System.out.println("File to concat: " + f);
                try(BufferedReader br = new BufferedReader(new FileReader(f))) {

                    String line = br.readLine();
                    while (line != null) {
                        writer.println(line);
                        line = br.readLine();
                    }
                }
            }
        }

        //System.out.println("Output to: " + getDest().getAsFile().get());
    }
}