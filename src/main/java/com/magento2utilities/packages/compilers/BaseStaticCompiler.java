package com.magento2utilities.packages.compilers;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.magento2utilities.util.Files;
import org.apache.commons.lang.StringUtils;
import java.util.regex.Pattern;

public class BaseStaticCompiler implements Compiler {

    /**
     * Base static files recompiler. Deletes the static version counterpart of any declared extension on magento_compilers.xml.
     *
     * @param file PsiFile
     * @param originalFile VirtualFile
     */
    @Override
    public void recompile(PsiFile file, VirtualFile originalFile) {
        file.delete();
    }

    /**
     * This specific recompiler can only perform actions on files in wich the files are present on the pub/static folder.
     * Also, only files selected from app/code and app/design should be processed. Everything else does not belong
     * to user managed files and should not be touched.
     *
     * @param file PsiFile
     * @param originalFile VirtualFile
     * @return boolean
     */
    @Override
    public boolean canRecompile(PsiFile file, VirtualFile originalFile) {

        Project project = file.getProject();
        String filePath = Files.getRealPath(project, file.getVirtualFile().getPath());
        if (!Files.isFileFromPubStatic(filePath)) {
            return false;
        }

        String moduleVendor, moduleName, regex;
        String originalFilePath = Files.getRealPath(project, originalFile.getPath());
        String pathFromWeb = StringUtils.substringAfter(originalFilePath, "/web/");
        String fileArea = this.getFileArea(originalFilePath);

        if (Files.isFileFromAppCode(originalFilePath)) {
            moduleVendor = originalFilePath.split("/")[3];
            moduleName = originalFilePath.split("/")[4];

            /*
             * If the selected file is in the frontend or adminhtml folder then the regex pattern is single when matching
             * that area.
             * However, if the file is placed inside the 'base' folder then the static counterpart was created in both
             * areas simultaneally.
             */
            if (originalFilePath.contains("frontend") || originalFilePath.contains("adminhtml")) {
                regex = "\\/pub\\/static\\/" + fileArea + "\\/.*\\/" + moduleVendor + "." + moduleName + "\\/" + pathFromWeb;
                return Pattern.compile(regex, Pattern.MULTILINE).matcher(filePath).matches();
            } else if (originalFilePath.contains("base")) {
                String frontendRegex = "\\/pub\\/static\\/frontend\\/.*\\/" + moduleVendor + "." + moduleName + "\\/" + pathFromWeb;
                String adminHtmlRegex = "\\/pub\\/static\\/adminhtml\\/.*\\/" + moduleVendor + "." + moduleName + "\\/" + pathFromWeb;
                return (
                        Pattern.compile(frontendRegex, Pattern.MULTILINE).matcher(filePath).matches() ||
                        Pattern.compile(adminHtmlRegex, Pattern.MULTILINE).matcher(filePath).matches()
                );
            }
            return false;
        }

        if (Files.isFileFromAppDesign(originalFilePath)) {
            String moduleReference = originalFilePath.split("/")[6];

            String themeVendor = originalFilePath.split("/")[4];
            String themeName = originalFilePath.split("/")[5];

            if (moduleReference.equals("web")) {
                regex = "\\/pub\\/static\\/" + fileArea + "\\/" + themeVendor + "\\/" + themeName + "\\/.*\\/" + pathFromWeb;
            } else {
                moduleVendor = moduleReference.split("_")[0];
                moduleName = moduleReference.split("_")[1];
                regex = "\\/pub\\/static\\/" + fileArea + "\\/" + themeVendor + "\\/" + themeName + "\\/.*\\/" + moduleVendor + "." + moduleName + "\\/" + pathFromWeb;
            }
            return Pattern.compile(regex, Pattern.MULTILINE).matcher(filePath).matches();
        }

        return false;
    }

    /**
     * Obtains the current area (frontend / adminhtml / base) from the current selected file.
     *
     * @param originalFilePath String
     * @return String
     */
    protected String getFileArea(String originalFilePath) {
        String fileArea = null;
        if (Files.isFileFromAppCode(originalFilePath) && (originalFilePath.contains("frontend") || originalFilePath.contains("adminhtml"))) {
            fileArea = originalFilePath.split("/")[6];
        } else if (Files.isFileFromAppDesign(originalFilePath)) {
            fileArea = originalFilePath.split("/")[3];
        }
        return fileArea;
    }
}
