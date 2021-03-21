package com.magento2utilities.packages.models.Commands;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.*;
import com.intellij.notification.*;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.magento2utilities.packages.models.Notifications;
import org.jetbrains.annotations.NotNull;
import java.nio.charset.StandardCharsets;

public class TerminalExecution {

    private final Project project;

    /**
     * TerminalExecution Constructor.
     *
     * @param project Project
     */
    public TerminalExecution(Project project) {
        this.project = project;
    }

    /**
     * Run a Magento CLI Command on the IDE.
     *
     * @param commandDefinition Definition
     * @param canBeCancelled boolean
     */
    public void execute(Definition commandDefinition, boolean canBeCancelled) {

        try {
            ProgressManager.getInstance().run(new Task.Modal(project, commandDefinition.getTitle(), canBeCancelled){

                @Override
                public void run(@NotNull ProgressIndicator indicator) {

                    indicator.setIndeterminate(false);
                    GeneralCommandLine generalCommandLine = new GeneralCommandLine(commandDefinition.getCommands());
                    generalCommandLine.setCharset(StandardCharsets.UTF_8);
                    generalCommandLine.setWorkDirectory(project.getBasePath());

                    ProcessHandler processHandler;
                    try {
                        processHandler = new OSProcessHandler(generalCommandLine);
                        processHandler.addProcessListener(new ProcessAdapter() {

                            String lastCommand = "";
                            final NotificationGroup notificationGroup = new NotificationGroup("", NotificationDisplayType.NONE, true);

                            @Override
                            public void startNotified(@NotNull ProcessEvent event) {
                                super.startNotified(event);
                                Notifications.MAGENTO2UTILITIES_NOTIFICATION_GROUP.createNotification(
                                        "Magento 2 Utilities | " + commandDefinition.getTitle(),
                                        "Initiated",
                                        NotificationType.INFORMATION,
                                        null
                                ).notify(project);
                            }

                            @Override
                            public void processTerminated(@NotNull final ProcessEvent event) {
                                if (event.getExitCode() != 0) {
                                    createNotification(commandDefinition.getTitle(), "Error when trying to complete command: " + lastCommand, NotificationType.ERROR);
                                } else {
                                    createNotification(commandDefinition.getTitle(), "Finished successfully.", NotificationType.INFORMATION);
                                }
                            }

                            @Override
                            public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                                super.onTextAvailable(event, outputType);
                                if(!event.getText().replace(" ", "").replace("\n", "").isEmpty()) {
                                    lastCommand = event.getText().trim();
                                    notificationGroup.createNotification(
                                            "Magento 2 Utilities | " + commandDefinition.getTitle() + ": " + lastCommand,
                                            NotificationType.INFORMATION
                                    ).notify(project);
                                }
                            }
                        });
                        processHandler.startNotify();
                    } catch (ExecutionException executionException) {
                        createNotification(commandDefinition.getTitle(), executionException.getMessage(), NotificationType.ERROR);
                    }
                }
            });
        } catch (Exception ioException) {
            createNotification(commandDefinition.getTitle(), ioException.getMessage(), NotificationType.ERROR);
        }
    }

    /**
     * Create a notification.
     *
     * @param title String
     * @param message String
     * @param type NotificationType
     */
    private void createNotification(String title, String message, NotificationType type) {
        Notifications.MAGENTO2UTILITIES_NOTIFICATION_GROUP.createNotification(
            "Magento 2 Utilities | " + title, message, type, null
        ).notify(project);
    }
}
