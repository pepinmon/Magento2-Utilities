package com.magento2utilities.packages.compilers;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.magento2utilities.packages.MagentoDefinitions;
import com.magento2utilities.util.Files;
import org.apache.commons.lang.ArrayUtils;

public class CssCompiler extends BaseStaticCompiler {

    /**
     * Compiler for css type files.
     * Less files are exception handled in
     * com.magento2utilities.packages.models.StaticsRecompilation.FileRecompiler#getDefinedFiles(com.intellij.openapi.vfs.VirtualFile).
     * When less files are selected, the couterpart compiled css files are targeted for recompilation.
     *
     * @param file PsiFile
     * @param originalFile VirtualFile
     * @return bool
     */
    @Override
    public boolean canRecompile(PsiFile file, VirtualFile originalFile) {

        Project project = file.getProject();
        String originalFilePath = Files.getRealPath(project, originalFile.getPath());
        String moduleVendor, moduleName;

        if (ArrayUtils.contains(MagentoDefinitions.cssStyleFileNames, file.getVirtualFile().getName())) {

            String filePath = Files.getRealPath(project, file.getVirtualFile().getPath());
            boolean condition = Files.isFileFromPubStatic(filePath) || Files.isFileFromVarViewPreProcessed(filePath);
            String fileArea = this.getFileArea(Files.getRealPath(project, originalFile.getPath()));
            if (fileArea != null) {
                condition = condition && filePath.contains(fileArea);
            }
            if (Files.isFileFromAppDesign(originalFilePath)) {
                moduleVendor = originalFilePath.split("/")[4];
                moduleName = originalFilePath.split("/")[5];
                condition = condition && filePath.contains(moduleVendor + "/" + moduleName);
            }

            return condition;
        }
        return super.canRecompile(file, originalFile);
    }
}
