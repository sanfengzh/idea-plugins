package org.sanfengzh.symbol.substitution;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.ConfigurableEP;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author sanfengzh
 * @description 组件
 * @created 2020-06-13 17:14
 */
public class ISubstitutionComponent implements SearchableConfigurable {
    // 存储配置数据的key
    private static final String STORE_DATA_KEY = "symbol-substitution-store-data-key";
    // 配置的替换映射数据
//    public static List<Map<String, String>> settingData = new ArrayList<>();
    public static Map<String, String> settingDataMap = new TreeMap<>();
    private JTextField[] leftJTextFields;
    private JTextField[] rightJTextFields;
    private static final String LEFT_TEXT_DATA_KEY = "left";
    private static final String RIGHT_TEXT_DATA_KEY = "right";
    private static final int INPUT_COUNT_LIMIT = 2;

    static {
        System.out.println("初始化");
        String storedDataStr = PropertiesComponent.getInstance().getValue(STORE_DATA_KEY);
        if(storedDataStr != null && storedDataStr.length() > 0) {
            storedDataStr = storedDataStr.trim();
        }
        if(storedDataStr != null && storedDataStr.length() > 0) {
            Gson gson = new GsonBuilder().create();
            Map<String, String> map = gson.fromJson(storedDataStr, TreeMap.class);
            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                settingDataMap.put(next.getKey(), next.getValue());
            }
        } else {
            settingDataMap.clear();
            settingDataMap.put("、、", "//");
            settingDataMap.put("、*", "/*");
            settingDataMap.put("*、", "*/");
        }
        Gson gson = new GsonBuilder().create();
        System.out.println("初始化结束settingData:" + gson.toJson(settingDataMap));
    }


    /**
     * Unique configurable id.
     * Note this id should be THE SAME as the one specified in XML.
     *
     * @see ConfigurableEP#id
     */
    @Override
    public @NotNull String getId() {
        return "symbol-substitution";
    }

    /**
     * Returns the visible name of the configurable component.
     * Note, that this method must return the display name
     * that is equal to the display name declared in XML
     * to avoid unexpected errors.
     *
     * @return the visible name of the configurable component
     */
    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
        return "符号替换插件设置";
    }

    /**
     * Creates new Swing form that enables user to configure the settings.
     * Usually this method is called on the EDT, so it should not take a long time.
     * <p>
     * Also this place is designed to allocate resources (subscriptions/listeners etc.)
     *
     * @return new Swing form to show, or {@code null} if it cannot be created
     * @see #disposeUIResources
     */
    @Override
    public @Nullable JComponent createComponent() {
        System.out.println("打开panel");
        // 设置面板
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setPreferredSize(new Dimension(300, 800));
        int maxSettingCount = 15;
        if (maxSettingCount - settingDataMap.size() <= 5) {
            maxSettingCount = settingDataMap.size() + 10;
        }

        List<Map<String, String>> list = new ArrayList<>();
        if (settingDataMap.size() > 0) {
            Iterator<Map.Entry<String, String>> iterator = settingDataMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                Map<String, String> map = new HashMap<>();
                map.put(LEFT_TEXT_DATA_KEY, next.getKey());
                map.put(RIGHT_TEXT_DATA_KEY, next.getValue());
                list.add(map);
            }
        }

        this.leftJTextFields = new JTextField[maxSettingCount];
        this.rightJTextFields = new JTextField[maxSettingCount];
        for (int i = 0; i < maxSettingCount; i++) {
            JPanel innerPanel = new JPanel();
            innerPanel.setPreferredSize(new Dimension(225, 50));
            Map<String, String> itemData = null;
            if (i < list.size()) {
                itemData = list.get(i);
            }
            JLabel jLabel = new JLabel();
            // 两个输入框中间加入一个箭头符号
            jLabel.setText(" -> ");
            // 箭头符号左边的输入框
            JTextField leftJTextField = new JTextField();
            leftJTextField.setPreferredSize(new Dimension(70, 40));
            this.leftJTextFields[i] = leftJTextField;
            // 输入框内居中输入展示
            leftJTextField.setHorizontalAlignment(JLabel.CENTER);
            // 限制输入字符长度
            leftJTextField.setDocument(new IJTextFieldLimit(INPUT_COUNT_LIMIT));
            // 箭头符号右边的输入框
            JTextField rightJTextField = new JTextField();
            rightJTextField.setPreferredSize(new Dimension(70, 40));
            this.rightJTextFields[i] = rightJTextField;
            // 输入框内居中输入展示
            rightJTextField.setHorizontalAlignment(JLabel.CENTER);
            // 限制输入字符长度
            rightJTextField.setDocument(new IJTextFieldLimit(INPUT_COUNT_LIMIT));
            if (itemData != null) {
                // 如果itemData不是空，也就是有配置的值，放进去，展示出来
                leftJTextField.setText(itemData.get(LEFT_TEXT_DATA_KEY));
                rightJTextField.setText(itemData.get(RIGHT_TEXT_DATA_KEY));
                System.out.println("aeeeeeeee->left:"+itemData.get(LEFT_TEXT_DATA_KEY) + ", right:"+ itemData.get(RIGHT_TEXT_DATA_KEY));
            }

            innerPanel.add(leftJTextField);
            innerPanel.add(jLabel);
            innerPanel.add(rightJTextField);
            panel.add(innerPanel);
        }

        return panel;
    }

    /**
     * Indicates whether the Swing form was modified or not.
     * This method is called very often, so it should not take a long time.
     * 这个方法用来判断面板是否有修改，目的控制 Apply 和 OK 按钮是否可用
     * 这个方法调用会和频繁，所以不能太耗时
     *
     * @return {@code true} if the settings were modified, {@code false} otherwise
     */
    @Override
    public boolean isModified() {
        Gson gson = new GsonBuilder().create();
        String storedDataStr = PropertiesComponent.getInstance().getValue(STORE_DATA_KEY, "null str").trim();
        String currentDataStr = gson.toJson(settingDataMap);
        return !storedDataStr.endsWith(currentDataStr);
    }

    /**
     * Stores the settings from the Swing form to the configurable component.
     * This method is called on EDT upon user's request.
     * 用户点击 Apply 或 OK 按钮后，把设置从swing表单存储到可配置的组件
     *
     * @throws ConfigurationException if values cannot be applied
     */
    @Override
    public void apply() throws ConfigurationException {
        // 首先把数据放到this.settingData中
        settingDataMap.clear();
        for(int i=0;i<leftJTextFields.length;i++) {
            String leftText = leftJTextFields[i].getText();
            String rightText = rightJTextFields[i].getText();
            if(leftText != null && leftText.length() > 0 && rightText != null && rightText.length() > 0) {
                // 左侧右侧输入框都不能为空格，因为空格在配置上是看不出来的
                leftText = leftText.trim();
                if(leftText != null && leftText.length() > 0 && rightText != null && rightText.length() > 0) {
                    settingDataMap.put(leftText, rightJTextFields[i].getText());
                }
            }
        }

        Gson gson = new GsonBuilder().create();
        // 把数据存储起来
        PropertiesComponent.getInstance().setValue(STORE_DATA_KEY, gson.toJson(settingDataMap));
        System.out.println("新的配置：" + gson.toJson(settingDataMap));
        // 刷新一下ISubstitutionHandler中substitutionMap缓存的数据
        ISubstitutionHandler.init();
    }
}
