package com.mxgraph.examples.swing.util;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 15:51 2020/5/15
 * @Modify By:
 */
public class editorUtil {
    private static BasicGraphEditor editor;

    public static BasicGraphEditor getEditor() {
        return editor;
    }

    public static void setEditor(BasicGraphEditor editor) {
        editorUtil.editor = editor;
    }
}
