package com.magento2utilities.packages.models;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.magento2utilities.util.Files;
import com.magento2utilities.util.Resources;
import java.util.*;
import java.util.stream.Collectors;

public class CompilerDefinitions {

    private static boolean definitionsInitialized = false;
    private static boolean extensionsInitialized = false;
    private static List<XmlTag> compilerDefinitions;
    private static ArrayList<String> extensionDefinitions;

    /**
     * Construct a list of all the defined compiler instances on the magento_compilers.xml file.
     *
     * @param project Project
     * @return List<XmlTag>
     */
    public static List<XmlTag> getCompilerDefinitions(Project project)
    {
        if (definitionsInitialized) {
            return compilerDefinitions;
        }
        XmlFile compilersMapping = Resources.getInstance("/magento2utilities/", "magento_compilers.xml", project).getXmlFile();
        compilerDefinitions = new LinkedList<>(Arrays.asList(Objects.requireNonNull(compilersMapping.getRootTag()).findSubTags("compiler")));
        definitionsInitialized = true;
        return compilerDefinitions;
    }

    /**
     * Construct a list of all the defined file extensions on the magento_compilers.xml file.
     * Only these extensions should be processed by the recompiler, every other extension should be ignored.
     *
     * @return ArrayList<String>
     */
    public static ArrayList<String> getDefinedExtensions(Project project) {
        if (extensionsInitialized) {
            return extensionDefinitions;
        }
        ArrayList<String> definedExtensions = new ArrayList<>();
        for (XmlTag compilerDefinition : getCompilerDefinitions(project)) {
            String fileExtensionAttribute = Objects.requireNonNull(compilerDefinition.getAttribute("fileExtension")).getValue();
            assert fileExtensionAttribute != null;
            definedExtensions.add(fileExtensionAttribute);
        }
        extensionDefinitions = definedExtensions;
        extensionsInitialized = true;
        return extensionDefinitions;
    }

    /**
     * Returns the collection of virtual files from an action event.
     *
     * @param actionEvent AnActionEvent
     * @return List<VirtualFile>
     */
    public static List<VirtualFile> getFiles(AnActionEvent actionEvent) {
        return Files.getFiles(actionEvent).stream().filter(
                file -> getDefinedExtensions(actionEvent.getProject()).contains(file.getExtension())
        ).collect(Collectors.toList());
    }
}
