package org.sanfengzh.symbol.substitution;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * @author sanfengzh
 * @description 输入限制
 * @created 2020-06-13 18:47
 */
public class IJTextFieldLimit extends PlainDocument {
    // 输入长度限制
    private int inputCountlimit;

    /**
     * Constructs a plain text document.  A default model using
     * <code>GapContent</code> is constructed and set.
     */
    public IJTextFieldLimit(int inputCountlimit) {
        super();
        this.inputCountlimit = inputCountlimit;
    }

    /**
     * Inserts some content into the document.
     * Inserting content causes a write lock to be held while the
     * actual changes are taking place, followed by notification
     * to the observers on the thread that grabbed the write lock.
     * <p>
     * This method is thread safe, although most Swing methods
     * are not. Please see
     * <A HREF="https://docs.oracle.com/javase/tutorial/uiswing/concurrency/index.html">Concurrency
     * in Swing</A> for more information.
     *
     * @param offs the starting offset &gt;= 0
     * @param str  the string to insert; does nothing with null/empty strings
     * @param a    the attributes for the inserted content
     * @throws BadLocationException the given insert position is not a valid
     *                              position within the document
     * @see Document#insertString
     */
    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (str == null) {
            return;
        }
        // 输入长度限制
        if (getLength() + str.length() <= this.inputCountlimit) {
            // 长度没超调用父类的方法
            if(!str.contains(" ")) {
                super.insertString(offs, str, a);
            }
        }
        // 输入内容限制，只能输入英文字母、数字和标点符号
//        if () {
//
//        }
    }
}
