package com.magento2utilities.packages.compilers;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.magento2utilities.packages.MagentoDefinitions;
import com.magento2utilities.util.Files;

public class JsCompiler extends BaseStaticCompiler {

    /**
     * Compiler for JS type files.
     * Regular js files follow base statics rules, meaning direct paths from the web folder.
     * However, the requirejs-config file is an exception, because the file is compiled to the frontend folder
     * on the all themes / locales folder, and not a module base structure. The requirejs-config file is compiled
     * by Magento with the data across all modules, being one big file.
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

        if (file.getName().equals(MagentoDefinitions.requireJsConfigFileName)) {
            String filePath = Files.getRealPath(project, file.getVirtualFile().getPath());
            boolean condition = Files.isFileFromPubStatic(filePath);
            String fileArea = this.getFileArea(filePath);
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
