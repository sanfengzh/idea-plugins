package org.sanfengzh.symbol.substitution;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @description handler
 * @author sanfengzh
 * @created 2020-06-13 17:13
 */
public class ISubstitutionHandler implements TypedActionHandler {
    // map 用来存放指定的被替换字符串和替换成的字符串的映射
    private static Map<String, String> substitutionMap = new HashMap<>();

    static {
        // 初始化数据到substitutionMap中
        init();
    }

    public static void init() {
        if (ISubstitutionComponent.settingDataMap.size() > 0) {
            substitutionMap.clear();
            Iterator<Map.Entry<String, String>> iterator = ISubstitutionComponent.settingDataMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                substitutionMap.put(next.getKey(), next.getValue());
                Gson gson = new  GsonBuilder().create();
                System.out.println("settingDataMap:" + gson.toJson(substitutionMap));
            }
        }
    }


    /**
     * Processes a key typed in the editor. The handler is responsible for delegating to
     * the previously registered handler if it did not handle the typed key.
     *
     * @param editor      the editor in which the key was typed.
     * @param charTyped   the typed character.
     * @param dataContext the current data context.
     */
    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
        System.out.println("=================================================");
        Gson gson = new GsonBuilder().create();
        System.out.println("配置集合:" + gson.toJson(substitutionMap));
        System.out.println("输入字符:" + charTyped);
        Document document = editor.getDocument();
        Project project = editor.getProject();
        CaretModel caretModel = editor.getCaretModel();
        Caret primaryCaret = caretModel.getPrimaryCaret();
        int offset = primaryCaret.getOffset();
        // 标记处理成功或未处理
        boolean substitutionFlag = false;
        // 首先处理单个字符的替换
        if (substitutionMap.get(String.valueOf(charTyped)) != null) {
            System.out.println("处理单个字符,替换成:" + substitutionMap.get(String.valueOf(charTyped)));
            System.out.println("offset:" + offset);
            // 在substitutionMap中有映射关系，说明需要被处理，无论最终能否处理成功都记为处理成功
            substitutionFlag = true;
            Runnable runnable = () -> {
                // 替换字符
                document.replaceString(offset, offset, substitutionMap.get(String.valueOf(charTyped)));
                // 光标的位置
                primaryCaret.moveToOffset(offset + 1);
            };
            // 写回去
            WriteCommandAction.runWriteCommandAction(project, runnable);
        }
        // 如果单个字符处理不成功，再尝试替换两个字符
        if (!substitutionFlag) {
            // 拿到整个文档的字符串
            String text = document.getText();
            // 找到当前输入字符的前一个字符 + 当前输入的字符 组成两个字符的串
            String str = text.substring(offset - 1, offset) + charTyped;
            System.out.println("处理两个字符,str:" + str);
            if (substitutionMap.get(str) != null) {
                // 在substitutionMap中有映射关系，说明需要被处理，无论最终能否处理成功都记为处理成功
                substitutionFlag = true;
                Runnable runnable = () -> {
                    // 替换字符
                    document.replaceString(offset - 1, offset, substitutionMap.get(str));
                    // 光标的位置
                    primaryCaret.moveToOffset(offset + 1);
                };
                // 写回去
                WriteCommandAction.runWriteCommandAction(project, runnable);
            }
        }
        // 如果两个字符也没替换成功，就不替换了
        if (!substitutionFlag) {
            System.out.println("不处理");
            Runnable runnable = () -> {
                // 如果不需要替换为指定的字符，就把当前输入的字符放进去
                // 这里可能有点费解，既然不需要替换为什么还要走个insertString
                // idea sdk的源码没有进去研究，这里如果不调用inserString，当前输入的字符是不会显示在输入域中的
                document.insertString(offset, String.valueOf(charTyped));
                // 光标的位置
                primaryCaret.moveToOffset(offset + 1);
            };
            // 写回去
            WriteCommandAction.runWriteCommandAction(project, runnable);
        }
    }
}
