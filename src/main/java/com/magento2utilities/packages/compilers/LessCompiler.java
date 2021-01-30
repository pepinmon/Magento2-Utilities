package com.magento2utilities.packages.compilers;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.magento2utilities.packages.MagentoDefinitions;
import com.magento2utilities.util.Files;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.regex.Pattern;

public class LessCompiler extends BaseStaticCompiler {

    /**
     * Compiler for less type files.
     *
     * @param file PsiFile
     * @param originalFile VirtualFile
     * @return bool
     */
    @Override
    public boolean canRecompile(PsiFile file, VirtualFile originalFile) {

        Project project = file.getProject();
        String filePath = Files.getRealPath(project, file.getVirtualFile().getPath());
        String originalFilePath = Files.getRealPath(project, originalFile.getPath());
        String moduleVendor, moduleName;

        if (ArrayUtils.contains(MagentoDefinitions.lessStyleFileNames, file.getVirtualFile().getName())) {
            boolean condition = Files.isFileFromVarViewPreProcessed(filePath);
            String fileArea = this.getFileArea(originalFilePath);
            if (fileArea != null) {
                condition = condition && filePath.contains(fileArea);
            }
            if (Files.isFileFromAppDesign(originalFilePath)) {
                moduleVendor = originalFilePath.split("/")[4];
                moduleName = originalFilePath.split("/")[5];
                condition = condition && filePath.contains(moduleVendor + "/" + moduleName);
            }
            return condition;

        } else {

            String regex;
            String fileArea = this.getFileArea(originalFilePath);
            String pathFromWeb = StringUtils.substringAfter(originalFilePath, "/web/");

            if (Files.isFileFromAppCode(originalFilePath)) {
                moduleVendor = originalFilePath.split("/")[3];
                moduleName = originalFilePath.split("/")[4];
                pathFromWeb = moduleVendor + "_" + moduleName + "/" + pathFromWeb;
                regex = "\\/var\\/view_preprocessed\\/pub\\/static\\/" + fileArea + "\\/.*\\/" + pathFromWeb;
                return Pattern.compile(regex, Pattern.MULTILINE).matcher(filePath).matches();
            }

            if (Files.isFileFromAppDesign(originalFilePath)) {
                moduleVendor = originalFilePath.split("/")[4];
                moduleName = originalFilePath.split("/")[5];
                regex = "\\/var\\/view_preprocessed\\/pub\\/static\\/" + fileArea + "\\/" + moduleVendor + "." + moduleName + "\\/.*\\/" + pathFromWeb;
                return Pattern.compile(regex, Pattern.MULTILINE).matcher(filePath).matches();
            }

        }
        return false;
    }
}
