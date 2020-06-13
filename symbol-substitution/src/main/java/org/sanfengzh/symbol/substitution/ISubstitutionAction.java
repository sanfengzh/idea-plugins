package org.sanfengzh.symbol.substitution;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import org.jetbrains.annotations.NotNull;

/**
 * @description action
 * @author sanfengzh
 * @created 2020-06-13 17:09
 */
public class ISubstitutionAction extends AnAction {
    static {
        EditorActionManager manager = EditorActionManager.getInstance();
        TypedAction typedAction = manager.getTypedAction();
        typedAction.setupHandler(new ISubstitutionHandler());
    }

    /**
     * Implement this method to provide your action handler.
     *
     * @param e Carries information on the invocation place
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

    }
}
