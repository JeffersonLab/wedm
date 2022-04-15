package org.jlab;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;
import java.io.FileFilter;
import java.util.Locale;

public class CombinePlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        CombineFileTask task = project.getTasks().create("combineFile", CombineFileTask.class);

        File projectDir = project.getProjectDir();

        task.getDest().fileValue(new File(projectDir, "build"));

        File cssDir = new File(projectDir, "src/main/webapp/resources/css");
        String csspath = cssDir.getAbsolutePath();

        File[] cssFiles = cssDir.listFiles(new ExtensionFilter("css"));

        if(cssFiles != null && cssFiles.length > 0) {
            csspath = cssFiles[0].getAbsolutePath();
        }

        task.getSource().from(csspath);
    }

    class ExtensionFilter implements FileFilter {

        private String extension;

        public ExtensionFilter(String extension) {
            this.extension = extension;
        }

        @Override
        public boolean accept(File pathname) {
            String name = pathname.getName();
            int i = name.lastIndexOf('.');
            if (i > 0 && i < name.length() - 1) {
                String desiredExtension = name.substring(i + 1).
                        toLowerCase(Locale.ENGLISH);
                if (desiredExtension.equals(extension)) {
                    return true;
                }
            }

            return false;
        }
    }
}