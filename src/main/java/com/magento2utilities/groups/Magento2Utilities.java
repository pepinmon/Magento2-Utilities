package com.magento2utilities.groups;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.impl.LaterInvocator;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.magento2utilities.util.MagentoInstallation;
import org.jetbrains.annotations.NotNull;
import javax.swing.Icon;
import java.util.Objects;

public class Magento2Utilities extends DefaultActionGroup {

    /*
     * Utilities Group Context Mneu Icon
     */
    public static final Icon MAGENTO_UTILITIES = IconLoader.getIcon("/icons/magento.png");

    /*
     *  UtilitiesGroup Constructor
     */
    public Magento2Utilities() {
        super();
        this.getTemplatePresentation().setIcon(MAGENTO_UTILITIES);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        /*
         * By default, the utilities group visibility depends on if the project is Magento based.
         */
        if (e.getProject() == null) {
            e.getPresentation().setVisible(false);
            return;
        }
        if (!MagentoInstallation.isInstalled(e.getProject().getBasePath())) {
            e.getPresentation().setVisible(false);
            return;
        }

        /*
         * The utilities group visibility also depends on if there are any children binded to it.
         * This is declared in plugin.xml.
         */
        boolean isVisible = true;

        /*
         * The group can only be visible on folders and files context menus.
         */
        if (e.getPlace().equals(ActionPlaces.PROJECT_VIEW_POPUP)) {
            isVisible = (isDirectoryOrFile(e) || isMultipleSelection(e));
        }

        e.getPresentation().setVisible(isVisible);
    }

    /**
     * Checks if the current action is binded to a folder or file.
     *
     * @param e Event
     * @return boolean
     */
    private boolean isDirectoryOrFile(@NotNull AnActionEvent e)
    {
        final PsiElement psiElement = e.getData(PlatformDataKeys.PSI_ELEMENT);
        return psiElement instanceof PsiDirectory || psiElement instanceof PsiFile;
    }

    /**
     * Checks if the current action is binded to a multiple file / folder selection.
     *
     * @param e Event
     * @return boolean
     */
    private boolean isMultipleSelection(@NotNull AnActionEvent e)
    {
        try {
            return Objects.requireNonNull(e.getData(PlatformDataKeys.SELECTED_ITEMS)).length > 1;
        } catch (NullPointerException exception) {
            return false;
        }
    }
}
