package com.magento2utilities.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.ResourceUtil;
import java.net.URL;

public class Resources {

    public String path;
    public String name;
    public Project project;

    /**
     * XmlResources Constructor.
     *
     * @param basePath String
     * @param fileName String
     */
    public Resources(String basePath, String fileName, Project currentProject) {
        path = basePath;
        name = fileName;
        project = currentProject;
    }

    /**
     * Singleton Constructor.
     *
     * @param basePath String
     * @param fileName String
     * @return XmlResources
     */
    public static Resources getInstance(String basePath, String fileName, Project project) {
        return new Resources(basePath, fileName, project);
    }

    /**
     * Loads an resource file based on the defined base path and file name.
     *
     * @return VirtualFile
     */
    public VirtualFile getVirtualFile() {
        URL url = ResourceUtil.getResource(getClass().getClassLoader(), path, name);
        return VfsUtil.findFileByURL(url);
    }

    /**
     * Loads an resource file based on the defined base path and file name.
     * Returns a PSI resource file.
     *
     * @return PsiFile
     */
    public PsiFile getPsiFile() {
        return PsiManager.getInstance(project).findFile(getVirtualFile());
    }

    /**
     * Loads an resource file based on the defined base path and file name.
     * Returns a XML resource file.
     *
     * @return PsiFile
     */
    public XmlFile getXmlFile() {
        return (XmlFile) getPsiFile();
    }
}
