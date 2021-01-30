package com.magento2utilities.packages.compilers;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;

public interface Compiler {

    /**
     * Recompiles a file.
     *
     * @param file PsiFile
     * @param originalFile VirtualFile
     */
    void recompile(PsiFile file, VirtualFile originalFile);

    /**
     * Checks if a file can be recompiled based on a ruleset.
     *
     * @param file PsiFile
     * @param originalFile VirtualFile
     */
    boolean canRecompile(PsiFile file, VirtualFile originalFile);
}
