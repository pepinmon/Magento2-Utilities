package com.magento2utilities.actions;

import com.intellij.icons.AllIcons;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.magento2utilities.packages.models.CompilerDefinitions;
import com.magento2utilities.packages.models.FileRecompiler;
import com.magento2utilities.util.Files;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecompileStaticFileAction extends AnAction {

    public static final NotificationGroup MAGENTO2UTILITIES_NOTIFICATION_GROUP =
            new NotificationGroup("Magento2Utilities Notification Group", NotificationDisplayType.BALLOON, true);

    /**
     * Run file recompilation on every seleced file / combination of files.
     * @param e AnActionEvent
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e)
    {
        Project project = e.getProject();
        if (e.getPlace().equals(ActionPlaces.PROJECT_VIEW_POPUP)) {
            new FileRecompiler(e).recompile(CompilerDefinitions.getFiles(e));
            return;
        }

        if (e.getPlace().equals(ActionPlaces.EDITOR_TAB_POPUP)) {
            ArrayList<VirtualFile> files = new ArrayList<>();
            files.add(CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext()));
            new FileRecompiler(e).recompile(files);
            return;
        }

        try {

            Document currentlyOpenEditorDocument = Objects.requireNonNull(e.getData(LangDataKeys.EDITOR)).getDocument();
            VirtualFile currentlyOpenEditorVirtualFile = FileDocumentManager.getInstance().getFile(currentlyOpenEditorDocument);
            if (project == null) {
                throw new NullPointerException("Canonical Path for Virtual File or Project is null");
            }
            assert currentlyOpenEditorVirtualFile != null;

            if (!CompilerDefinitions.getDefinedExtensions(project).contains(currentlyOpenEditorVirtualFile.getExtension())) {
                return;
            }

            String projectRelativeFilePath = Files.getRealPath(project, currentlyOpenEditorVirtualFile.getPath());
            if (!(Files.isFileFromAppCode(projectRelativeFilePath) || Files.isFileFromAppDesign(projectRelativeFilePath))) {
                throw new NullPointerException("Virtual File not inside app/design or app/code");
            }

            ArrayList<VirtualFile> files = new ArrayList<>();
            files.add(CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext()));
            new FileRecompiler(e).recompile(files);

        } catch (NullPointerException exception) {
            MAGENTO2UTILITIES_NOTIFICATION_GROUP.createNotification(
                    "Magento 2 Utilities | Recompile",
                    "Cannot run compilation: File selection missing or the selected file is not inside app/design or app/code directories.",
                    NotificationType.WARNING,
                    null
            ).notify(project);
        }

    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        Project project = e.getProject();
        if (project == null) {
            return;
        }

        if (e.getPlace().equals(ActionPlaces.PROJECT_VIEW_POPUP)) {
            e.getPresentation().setIcon(AllIcons.Actions.Refresh);

            List<VirtualFile> files = CompilerDefinitions.getFiles(e);
            if (files.size() <= 0) {
                e.getPresentation().setVisible(false);
                return;
            }

            boolean isVisible = true;
            for (VirtualFile virtualFile : files) {
                if (virtualFile.getCanonicalPath() == null) {
                    continue;
                }
                String filePath = Files.getRealPath(project, virtualFile.getPath());
                if (!(Files.isFileFromAppCode(filePath) || Files.isFileFromAppDesign(filePath))) {
                    isVisible = false;
                    break;
                }

            }
            e.getPresentation().setText("Recompile Selected Files");
            e.getPresentation().setVisible(isVisible);
        }

        if (e.getPlace().equals(ActionPlaces.EDITOR_TAB_POPUP)) {

            final VirtualFile virtualFile = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
            assert virtualFile != null;

            if (!CompilerDefinitions.getDefinedExtensions(project).contains(virtualFile.getExtension())) {
                e.getPresentation().setVisible(false);
                return;
            }

            boolean isVisible = true;
            String filePath = Files.getRealPath(project, virtualFile.getPath());
            if (!(Files.isFileFromAppCode(filePath) || Files.isFileFromAppDesign(filePath))) {
                isVisible = false;
            }
            e.getPresentation().setVisible(isVisible);
        }
    }
}
