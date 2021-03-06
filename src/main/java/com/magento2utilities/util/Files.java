package com.magento2utilities.util;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Files {

    private static ArrayList<VirtualFile> directoryChildFiles;

    /**
     * Retrieves the list of files / selection of files from an Action Event.
     *
     * @param e AnActionEvent
     * @return JBIterable<VirtualFile>
     */
    public static List<VirtualFile> getFiles(@NotNull AnActionEvent e) {
        ArrayList<VirtualFile> compiledFiles = new ArrayList<>();
        for (Object selectedElement : Objects.requireNonNull(e.getData(PlatformDataKeys.SELECTED_ITEMS))) {
            if (selectedElement instanceof PsiDirectory) {
                compiledFiles.addAll(Arrays.stream(((PsiDirectory) selectedElement).getFiles()).map(PsiFile::getVirtualFile).collect(Collectors.toList()));
                compiledFiles.addAll(getAllChildren((PsiDirectory) selectedElement, true));
            }
            if (selectedElement instanceof PsiFile) {
                VirtualFile fileToAdd = ((PsiFile) selectedElement).getVirtualFile();
                if (fileToAdd.isInLocalFileSystem()) {
                    compiledFiles.add(fileToAdd);
                }
            }
        }
        return compiledFiles;
    }

    /**
     * Constructs a list of child files of a directory, recursively.
     *
     * @param psiDirectory PsiDirectory
     * @param createList boolean
     * @return ArrayList<VirtualFile>
     */
    public static ArrayList<VirtualFile> getAllChildren(PsiDirectory psiDirectory, boolean createList) {
        if (createList) {
            directoryChildFiles = new ArrayList<>();
        }
        for (PsiElement element : psiDirectory.getChildren()) {
            if (element instanceof PsiDirectory) {
                getAllChildren((PsiDirectory) element, false);
            } else if (element instanceof PsiFile) {
                VirtualFile fileToAdd = ((PsiFile) element).getVirtualFile();
                if (fileToAdd.isInLocalFileSystem()) {
                    directoryChildFiles.add(fileToAdd);
                }
            }
        }
        return directoryChildFiles;
    }

    /**
     * Returns the file extension of a file, including multi dotted extensions.
     *
     * @param filename String
     * @return String
     */
    public static String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    /**
     * Finds a file based on its path.
     *
     * @param file VirtualFile
     * @param path File Path
     *
     * @return VirtualFile
     */
    public static VirtualFile findFile(VirtualFile file, String path)
    {
        return VfsUtil.findRelativeFile(file, path.split(File.separator));
    }

    /**
     * Retreives the 'real' path from a file, meaning the relative path from the project root.
     *
     * @param project Project
     * @param path String
     * @return String
     */
    public static String getRealPath(Project project, String path) {
        if (project.getBasePath() == null) {
            return "";
        }
        return path.replace(project.getBasePath(), "");
    }

    /**
     * Checks if a given file path starts in the app code Magento directory.
     *
     * @param path String
     * @return boolean
     */
    public static boolean isFileFromAppCode(String path)
    {
        return path.startsWith("/app/code");
    }

    /**
     * Checks if a given file path starts in the app design Magento directory.
     *
     * @param path String
     * @return boolean
     */
    public static boolean isFileFromAppDesign(String path)
    {
        return path.startsWith("/app/design");
    }

    /**
     * Checks if a given file path starts in the pub static Magento directory.
     *
     * @param path String
     * @return boolean
     */
    public static boolean isFileFromPubStatic(String path)
    {
        return path.startsWith("/pub/static");
    }

    /**
     * Checks if a given file path starts in the pub static Magento directory.
     *
     * @param path String
     * @return boolean
     */
    public static boolean isFileFromVarViewPreProcessed(String path)
    {
        return path.startsWith("/var/view_preprocessed");
    }

    /**
     * Checks if a given file path starts in the generated code Magento directory.
     *
     * @param path String
     * @return boolean
     */
    public static boolean isFileFromGeneratedCode(String path)
    {
        return path.startsWith("/generated/code");
    }
}
