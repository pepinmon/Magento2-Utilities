package com.magento2utilities.packages.models;

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
import com.intellij.psi.xml.XmlTag;
import com.magento2utilities.actions.RecompileStaticFileAction;
import com.magento2utilities.packages.MagentoDefinitions;
import com.magento2utilities.packages.compilers.Compiler;
import com.magento2utilities.util.Files;
import java.util.*;
import org.jetbrains.annotations.NotNull;

public class FileRecompiler {

    public AnActionEvent actionEvent;
    final private Project project;
    final private Map<String, Compiler> compilers;

    /**
     * StaticsRecompiler Constructor
     *
     * @param action AnActionEvent
     */
    public FileRecompiler(AnActionEvent action) {
        actionEvent = action;
        project = actionEvent.getProject();
        compilers = this.assembleCompilers();
    }

    /**
     * Recompiles a collection of files.
     * Based on those files creates a modal dialog with a standard progression bar.
     */
    public void recompile(List<VirtualFile> files)
    {
        ProgressManager.getInstance().run(new Task.Modal(project, "Recompiling Files...", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                int i = 0;
                int filesSize = files.size();
                indicator.setIndeterminate(false);
                for (VirtualFile file : files) {
                    indicator.checkCanceled();
                    indicator.setText("Recompiling " + file.getName());
                    indicator.setFraction(i != 0 ? (double)i / filesSize : 0);
                    recompileFile(file);
                    i++;
                }
            }
        });
        RecompileStaticFileAction.MAGENTO2UTILITIES_NOTIFICATION_GROUP.createNotification(
                "Magento 2 Utilities | Recompile",
                "Recompiled successfully",
                NotificationType.INFORMATION,
                null
        ).notify(project);
    }

    /**
     * Recompiles a static Magento file.
     *
     * @param file VirtualFile
     */
    private void recompileFile(VirtualFile file) {
        WriteCommandAction.runWriteCommandAction(project, () -> ApplicationManager.getApplication().runReadAction(() -> {
            ArrayList<PsiFile> psiFiles = this.getDefinedFiles(file);
            for (PsiFile psiFile : psiFiles) {
                try {
                    Compiler compiler = this.compilers.get(Files.getFileExtension(psiFile.getVirtualFile().getName()));
                    if (compiler.canRecompile(psiFile, file)) {
                        compiler.recompile(psiFile, file);
                    }
                } catch (Exception exception) {
                    System.out.println(exception.getMessage());
                }
            }
        }));
    }

    /**
     * Most file types follow the same static creation logic, meaning a direct path from the web folder.
     * However, .less files are not compiled individually, they are all merged an compiled into css files, the styles-x.css files.
     * These files account all the less files in the application, module.less, extend.less, etc...
     *
     * @param file VirtualFile
     * @return ArrayList<PsiFile>
     */
    private ArrayList<PsiFile> getDefinedFiles(VirtualFile file) {
        ArrayList<PsiFile> psiFiles = new ArrayList<>();
        if (Files.getFileExtension(file.getName()).equals(MagentoDefinitions.lessFileExtension)) {
            for (String cssFileName : MagentoDefinitions.cssStyleFileNames) {
                psiFiles.addAll(Arrays.asList(FilenameIndex.getFilesByName(project, cssFileName, GlobalSearchScope.allScope(project))));
            }
            for (String cssFileName : MagentoDefinitions.lessStyleFileNames) {
                psiFiles.addAll(Arrays.asList(FilenameIndex.getFilesByName(project, cssFileName, GlobalSearchScope.allScope(project))));
            }
        }
        psiFiles.addAll(Arrays.asList(FilenameIndex.getFilesByName(project, file.getName(), GlobalSearchScope.allScope(project))));
        return psiFiles;
    }

    /**
     * Constructs the full list of compilers based on the defined extensions on the magento_compilers.xml file.
     *
     * @return Map<String, Compiler>
     */
    private Map<String, Compiler> assembleCompilers() {
        Map<String, Compiler> compilers = new HashMap<>();
        for (XmlTag compilerDefinition : CompilerDefinitions.getCompilerDefinitions(project)) {
            String fileExtensionAttribute = Objects.requireNonNull(compilerDefinition.getAttribute("fileExtension")).getValue();
            if (fileExtensionAttribute == null) {
                continue;
            }
            String instanceAttribute = Objects.requireNonNull(compilerDefinition.getAttribute("instance")).getValue();
            try {
                compilers.put(fileExtensionAttribute, (Compiler) Class.forName(instanceAttribute).getDeclaredConstructor().newInstance());
            } catch (Exception ignored) {}
        }
        return compilers;
    }
}
