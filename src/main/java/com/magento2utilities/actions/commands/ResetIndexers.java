package com.magento2utilities.actions.commands;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.magento2utilities.packages.models.Commands.Definition;
import com.magento2utilities.packages.models.Commands.TerminalExecution;
import org.jetbrains.annotations.NotNull;

public class ResetIndexers extends AnAction implements DumbAware {

    /**
     * Perform the cache:flush Magento CLI command.
     *
     * @param e AnActionEvent
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e)
    {
        new TerminalExecution(e.getProject()).execute(new Definition("reindex_reset", e.getProject()), true);
    }
}
