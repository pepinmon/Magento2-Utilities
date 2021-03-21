package com.magento2utilities.actions;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.magento2utilities.packages.models.Notifications;
import com.magento2utilities.packages.models.PHP.FilesUtil;
import com.magento2utilities.packages.models.PHP.InterceptorManager;
import com.magento2utilities.util.Files;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ManageClassInterceptor extends AnAction {

    /**
     * Deletes the interceptor counterpart of a php class/list of classes.
     *
     * @param e AnActionEvent
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e)
    {
        Project project = e.getProject();
        if (e.getPlace().equals(ActionPlaces.PROJECT_VIEW_POPUP)) {
            new InterceptorManager(e).delete(FilesUtil.getFiles(e));
            return;
        }

        if (e.getPlace().equals(ActionPlaces.EDITOR_TAB_POPUP)) {
            ArrayList<VirtualFile> files = new ArrayList<>();
            files.add(CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext()));
            new InterceptorManager(e).delete(files);
            return;
        }

        try {

            Document currentlyOpenEditorDocument = Objects.requireNonNull(e.getData(LangDataKeys.EDITOR)).getDocument();
            VirtualFile currentlyOpenEditorVirtualFile = FileDocumentManager.getInstance().getFile(currentlyOpenEditorDocument);
            if (project == null) {
                throw new NullPointerException("Canonical Path for Virtual File or Project is null");
            }
            assert currentlyOpenEditorVirtualFile != null;
            if (!Objects.equals(currentlyOpenEditorVirtualFile.getExtension(), "php")) {
                return;
            }
            String projectRelativeFilePath = Files.getRealPath(project, currentlyOpenEditorVirtualFile.getPath());
            if (!(Files.isFileFromAppCode(projectRelativeFilePath))) {
                throw new NullPointerException("Selected file not inside app/code");
            }
            ArrayList<VirtualFile> files = new ArrayList<>();
            files.add(CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext()));
            new InterceptorManager(e).delete(files);

        } catch (NullPointerException exception) {
            Notifications.MAGENTO2UTILITIES_NOTIFICATION_GROUP.createNotification(
                    "Magento 2 Utilities | Interceptors",
                    "Cannot process class interceptor: File selection missing or the selected file is not inside app/code directory.",
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
        if (e.getPlace().equals(ActionPlaces.MAIN_MENU)) {
            e.getPresentation().setVisible(false);
            return;
        }
        if (e.getPlace().equals(ActionPlaces.POPUP + "@" + ActionPlaces.MAIN_TOOLBAR)) {
            e.getPresentation().setVisible(false);
            return;
        }
        if (e.getPlace().equals(ActionPlaces.PROJECT_VIEW_POPUP)) {
            final List<VirtualFile> virtualFiles = Arrays.stream(CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(e.getDataContext())).filter(
                file -> Objects.equals(file.getExtension(), "php")
            ).collect(Collectors.toList());
            e.getPresentation().setText("Delete Selected Class(es) Interceptor(s)");
            e.getPresentation().setVisible(virtualFiles.size() > 0);
        }
        if (e.getPlace().equals(ActionPlaces.EDITOR_TAB_POPUP)) {
            final VirtualFile virtualFile = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
            assert virtualFile != null;
            if (!Objects.equals(virtualFile.getExtension(), "php")) {
                e.getPresentation().setVisible(false);
                return;
            }
            boolean isVisible = true;
            String filePath = Files.getRealPath(project, virtualFile.getPath());
            if (!(Files.isFileFromAppCode(filePath))) {
                isVisible = false;
            }
            e.getPresentation().setVisible(isVisible);
        }
    }
}
