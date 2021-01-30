package com.magento2utilities.util;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.magento2utilities.packages.MagentoDefinitions;

public class MagentoInstallation {

    /**
     * Determines if the current project installation is Magento 2 based.
     *
     * @param path String
     * @return boolean
     */
    public static boolean isInstalled(final String path)
    {
        if (StringUtil.isEmptyOrSpaces(path)) {
            return false;
        }
        final VirtualFile file = LocalFileSystem.getInstance().findFileByPath(path);
        if (file != null && file.isDirectory()) {
            return Files.findFile(file, MagentoDefinitions.frameworkRootComposer) != null || Files.findFile(file, MagentoDefinitions.frameworkRootGit) != null;
        }
        return false;
    }
}
