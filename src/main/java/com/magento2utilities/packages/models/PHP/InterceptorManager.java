package com.magento2utilities.packages.models.PHP;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.magento2utilities.packages.models.Notifications;
import com.magento2utilities.util.Files;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InterceptorManager {

    final private Project project;
    final private String InterceptorDefinion = "Interceptor.php";

    /**
     * StaticsRecompiler Constructor
     *
     * @param action AnActionEvent
     */
    public InterceptorManager(AnActionEvent action) {
        project = action.getProject();
    }

    /**
     * Deletes the interceptor counterpart of a php class/list of classes.
     *
     * @param files List<VirtualFile>
     */
    public void delete(List<VirtualFile> files)
    {
        ProgressManager.getInstance().run(new Task.Modal(project, "Processing Interceptors...", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                int i = 0;
                int filesSize = files.size();
                indicator.setIndeterminate(false);
                for (VirtualFile file : files) {
                    indicator.checkCanceled();
                    indicator.setText("Processing " + file.getName());
                    indicator.setFraction(i != 0 ? (double)i / filesSize : 0);
                    delete(file);
                    i++;
                }
            }
        });
        Notifications.MAGENTO2UTILITIES_NOTIFICATION_GROUP.createNotification(
                "Magento 2 Utilities | Interceptors",
                "Interceptor deleted successfully.",
                NotificationType.INFORMATION,
                null
        ).notify(project);
    }

    /**
     * Deletes the interceptor counterpart of a php class.
     *
     * @param originalFile VirtualFile
     */
    private void delete(VirtualFile originalFile) {
        String filePath = Files.getRealPath(project, originalFile.getPath());
        String finalPath = filePath.replace("/app/code", "").replace(".php", "") + "/" + this.InterceptorDefinion;
        WriteCommandAction.runWriteCommandAction(project, () -> ApplicationManager.getApplication().runReadAction(() -> {
            List<PsiFile> files = new ArrayList<>(
                Arrays.asList(FilenameIndex.getFilesByName(project, this.InterceptorDefinion, GlobalSearchScope.projectScope(project)))
            ).stream().filter(file -> file.getVirtualFile().getPath().contains(finalPath)).collect(Collectors.toList());
            for (PsiFile file : files) {
                file.delete();
            }
        }));
    }
}
