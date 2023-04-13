/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is Forte for Java, Community Edition. The Initial
 * Developer of the Original Code is Sun Microsystems, Inc. Portions
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.editor.ext;

import java.util.ArrayList;
import javax.swing.text.BadLocationException;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.EditorDebug;

/**
* Expression generated by parsing text by java completion
*
* @author Miloslav Metelka
* @version 1.00
*/

public class JCExpression {

    /** Invalid expression - this ID is used only internally */
    private static final int INVALID = -1;
    /** Constant - int/long/String/char etc. */
    public static final int CONSTANT = 0;
    /** Variable 'a' or 'a.b.c' */
    public static final int VARIABLE = 1;
    /** Operator '+' or '--' */
    public static final int OPERATOR = 2;
    /** Special value for unary operators */
    public static final int UNARY_OPERATOR = 3;
    /** Dot between method calls 'a().b()' or 'a().b.c.d(e, f)' */
    public static final int DOT = 4;
    /** Dot between method calls and dot at the end 'a().b().' or 'a().b.c.d(e, f).' */
    public static final int DOT_OPEN = 5;
    /** Opened array 'a[0' or 'a.b.c[d.e' */
    public static final int ARRAY_OPEN = 6;
    /** Array 'a[0]' or 'a.b.c[d.e]' */
    public static final int ARRAY = 7;
    /** Left opened parentheses */
    public static final int PARENTHESIS_OPEN = 8;
    /** Closed parenthesis holding the subexpression or conversion */
    public static final int PARENTHESIS = 9;
    /** Opened method 'a(' or 'a.b.c(d, e' */
    public static final int METHOD_OPEN = 10;
    /** Method closed by right parentheses 'a()' or 'a.b.c(d, e, f)' */
    public static final int METHOD = 11;
    /** Constructor closed by right parentheses 'new String()' or 'new String("hello")' */ // NOI18N
    public static final int CONSTRUCTOR = 12;
    /** Conversion '(int)a.b()' */
    public static final int CONVERSION = 13;
    /** Data type */
    public static final int TYPE = 14;
    /** 'new' keyword */
    public static final int NEW = 15;
    /** 'instanceof' operator */
    public static final int INSTANCEOF = 16;

    /** Constant to add to expression ID to convert
    * it to operator.
    */
    private static final int EXP_ID_START = 50;
    /** Array that holds the priority, associativity
    * and other info about operators. The first part
    * is reserved for operator IDs as defined in JavaSyntax.
    * The rest is used for expression IDs.
    */
    private static final int[] OP = new int[70];

    /** Is the operator right associative? */
    private static final int RIGHT_ASSOCIATIVE = 32;

    static {
        OP[JavaSyntax.EQ] = 2 | RIGHT_ASSOCIATIVE;
        OP[JavaSyntax.LT] = 10;
        OP[JavaSyntax.GT] = 10;
        OP[JavaSyntax.LLT] = 11;
        OP[JavaSyntax.GGT] = 11;
        OP[JavaSyntax.GGGT] = 11;
        OP[JavaSyntax.PLUS] = 12;
        OP[JavaSyntax.MINUS] = 12;
        OP[JavaSyntax.MUL] = 13;
        OP[JavaSyntax.DIV] = 13;
        OP[JavaSyntax.AND] = 8;
        OP[JavaSyntax.OR] = 6;
        OP[JavaSyntax.XOR] = 7;
        OP[JavaSyntax.MOD] = 13;
        OP[JavaSyntax.NOT] = 14;
        OP[JavaSyntax.NEG] = 14;

        OP[JavaSyntax.EQ_EQ] = 9;
        OP[JavaSyntax.LE] = 10;
        OP[JavaSyntax.GE] = 10;
        OP[JavaSyntax.LLE] = 2 | RIGHT_ASSOCIATIVE;
        OP[JavaSyntax.GGE] = 2 | RIGHT_ASSOCIATIVE;
        OP[JavaSyntax.GGGE] = 2 | RIGHT_ASSOCIATIVE;
        OP[JavaSyntax.PLUS_EQ] = 2 | RIGHT_ASSOCIATIVE;
        OP[JavaSyntax.MINUS_EQ] = 2 | RIGHT_ASSOCIATIVE;
        OP[JavaSyntax.MUL_EQ] = 2 | RIGHT_ASSOCIATIVE;
        OP[JavaSyntax.DIV_EQ] = 2 | RIGHT_ASSOCIATIVE;
        OP[JavaSyntax.AND_EQ] = 2 | RIGHT_ASSOCIATIVE;
        OP[JavaSyntax.OR_EQ] = 2 | RIGHT_ASSOCIATIVE;
        OP[JavaSyntax.XOR_EQ] = 2 | RIGHT_ASSOCIATIVE;
        OP[JavaSyntax.MOD_EQ] = 2 | RIGHT_ASSOCIATIVE;
        OP[JavaSyntax.NOT_EQ] = 9;

        OP[JavaSyntax.DOT] = 15;
        OP[JavaSyntax.COLON] = 3 | RIGHT_ASSOCIATIVE;
        OP[JavaSyntax.QUESTION] = 3 | RIGHT_ASSOCIATIVE;
        OP[JavaSyntax.LEFT_SQUARE_BRACKET] = 15;
        OP[JavaSyntax.RIGHT_SQUARE_BRACKET] = 0; // stop
        OP[JavaSyntax.PLUS_PLUS] = 15;
        OP[JavaSyntax.MINUS_MINUS] = 15;
        OP[JavaSyntax.AND_AND] = 5;
        OP[JavaSyntax.OR_OR] = 4;

        OP[JavaSyntax.COMMA] = 0; // stop
        OP[JavaSyntax.SEMICOLON] = 0; // not-recognized
        OP[JavaSyntax.LEFT_PARENTHESES] = 16;
        OP[JavaSyntax.RIGHT_PARENTHESES] = 0; // not-recognized
        OP[JavaSyntax.LEFT_BRACE] = 0; // not-recognized
        OP[JavaSyntax.RIGHT_BRACE] = 0; // not-recognized

        OP[EXP_ID_START + INVALID] = 0;
        OP[EXP_ID_START + CONSTANT] = 1;
        OP[EXP_ID_START + VARIABLE] = 1;
        OP[EXP_ID_START + UNARY_OPERATOR] = 15;
        OP[EXP_ID_START + DOT] = 1;
        OP[EXP_ID_START + DOT_OPEN] = 0; // stop
        OP[EXP_ID_START + ARRAY_OPEN] = 0; // stop
        OP[EXP_ID_START + ARRAY] = 1;
        OP[EXP_ID_START + PARENTHESIS_OPEN] = 0; // stop
        OP[EXP_ID_START + PARENTHESIS] = 1;
        OP[EXP_ID_START + METHOD_OPEN] = 0; // stop
        OP[EXP_ID_START + METHOD] = 1;
        OP[EXP_ID_START + CONSTRUCTOR] = 1;
        OP[EXP_ID_START + CONVERSION] = 1;
        OP[EXP_ID_START + TYPE] = 0; // stop
        OP[EXP_ID_START + NEW] = 0; // stop
        OP[EXP_ID_START + INSTANCEOF] = 10;

    }

    private static final int[] EMPTY_INT_ARRAY = new int[0];

    private static final char[] EMPTY_CHAR_ARRAY = new char[0];

    private JCExpression parent;

    /** ID of the expression */
    private int id;

    /** Result type */
    private JCType type;

    /** Current token index saying how many token blocks was added * 4 */
    private int tokenInd;

    /** token info blocks containing token ID, helper ID,
    * offset of the token in the buffer and the length of the token
    */
    private int[] tokenBlocks = EMPTY_INT_ARRAY;

    /** List of parameters 
     * @associates JCExpression*/
    private ArrayList prmList;

    /** Buffer with the scanned characters */
    private char[] buffer;

    /** Starting position of the buffer */
    private int bufferStartPos;

    JCExpression(char[] buffer, int bufferStartPos, int id) {
        this.buffer = buffer;
        this.bufferStartPos = bufferStartPos;
        this.id = id;
    }

    /** Create empty variable. */
    static JCExpression createEmptyVariable(int pos) {
        JCExpression empty = new JCExpression(EMPTY_CHAR_ARRAY, pos, VARIABLE);
        empty.addToken(JavaSyntax.IDENTIFIER, -1, 0, 0);
        return empty;
    }

    static int getOperatorID(int tokenID, int helperID) {
        switch (tokenID) {
        case JavaSyntax.OPERATOR:
            return helperID;
        case JavaSyntax.KEYWORD:
            switch (helperID) {
            case JavaKeywords.NEW:
                return EXP_ID_START + NEW;
            case JavaKeywords.INSTANCEOF:
                return EXP_ID_START + INSTANCEOF;
            default:
                return -1;
            }
        }
        return -1;
    }

    static int getOperatorID(JCExpression exp) {
        int expID = (exp != null) ? exp.getID() : INVALID;
        switch (expID) {
        case OPERATOR:
            return exp.getTokenHelperID(0);
        }
        return EXP_ID_START + expID;
    }

    static int getOperatorPrecedence(int opID) {
        return OP[opID] & 31;
    }

    static boolean isOperatorRightAssociative(int opID) {
        return (OP[opID] & RIGHT_ASSOCIATIVE) != 0;
    }

    /** Is the expression a valid type. It can be either datatype
    * or array.
    */
    static boolean isValidType(JCExpression exp) {
        switch (exp.getID()) {
        case ARRAY:
            if (exp.getParameterCount() == 1) {
                return isValidType(exp.getParameter(0));
            }
            return false;

        case DOT:
            int prmCnt = exp.getParameterCount();
            for (int i = 0; i < prmCnt; i++) {
                if (exp.getParameter(i).getID() != VARIABLE) {
                    return false;
                }
            }
            return true;

        case TYPE:
        case VARIABLE:
            return true;
        }

        return false;
    }


    public int getID() {
        return id;
    }

    void setID(int id) {
        this.id = id;
    }

    public JCExpression getParent() {
        return parent;
    }

    void setParent(JCExpression parent) {
        this.parent = parent;
    }

    public JCType getType() {
        return type;
    }

    void setType(JCType type) {
        this.type = type;
    }

    public int getTokenCount() {
        return tokenInd / 4;
    }

    public int[] getTokenBlocks() {
        return tokenBlocks;
    }

    public String getTokenText(int tokenInd) {
        tokenInd *= 4;
        return new String(buffer, tokenBlocks[tokenInd + 2],
                          tokenBlocks[tokenInd + 3]);
    }

    public int getTokenPosition(int tokenInd) {
        tokenInd *= 4;
        return bufferStartPos + tokenBlocks[tokenInd + 2];
    }

    public int getTokenLength(int tokenInd) {
        tokenInd *= 4;
        return tokenBlocks[tokenInd + 3];
    }

    public int getTokenID(int tokenInd) {
        tokenInd *= 4;
        return tokenBlocks[tokenInd];
    }

    public int getTokenHelperID(int tokenInd) {
        tokenInd *= 4;
        return tokenBlocks[tokenInd + 1];
    }

    char[] getBuffer() {
        return buffer;
    }

    int getBufferStartPosition() {
        return bufferStartPos;
    }

    void addToken(int tokenID, int helperID, int offset, int len) {
        if (tokenInd == tokenBlocks.length) {
            int[] tmp = new int[Math.max(4, tokenBlocks.length * 2)];
            if (tokenBlocks.length > 0) {
                System.arraycopy(tokenBlocks, 0, tmp, 0, tokenBlocks.length);
            }
            tokenBlocks = tmp;
        }
        tokenBlocks[tokenInd++] = tokenID;
        tokenBlocks[tokenInd++] = helperID;
        tokenBlocks[tokenInd++] = offset;
        tokenBlocks[tokenInd++] = len;
    }

    public int getParameterCount() {
        return (prmList != null) ? prmList.size() : 0;
    }

    public JCExpression getParameter(int index) {
        return (JCExpression)prmList.get(index);
    }

    void addParameter(JCExpression prm) {
        if (prmList == null) {
            prmList = new ArrayList();
        }
        prm.setParent(this);
        prmList.add(prm);
    }

    void swapOperatorParms() {
        if ((id == OPERATOR || id == INSTANCEOF) && getParameterCount() == 2) {
            JCExpression exp1 = (JCExpression)prmList.remove(0);
            prmList.add(exp1);
            exp1.swapOperatorParms();
            ((JCExpression)prmList.get(0)).swapOperatorParms();
        }
    }

    private String getIndent(int indent) {
        StringBuffer sb = new StringBuffer();
        while (indent-- > 0) {
            sb.append(' ');
        }
        return sb.toString();
    }

    static String getIDName(int id) {
        switch (id) {
        case CONSTANT:
            return "CONSTANT"; // NOI18N
        case VARIABLE:
            return "VARIABLE"; // NOI18N
        case OPERATOR:
            return "OPERATOR"; // NOI18N
        case UNARY_OPERATOR:
            return "UNARY_OPERATOR"; // NOI18N
        case DOT:
            return "DOT"; // NOI18N
        case DOT_OPEN:
            return "DOT_OPEN"; // NOI18N
        case ARRAY:
            return "ARRAY"; // NOI18N
        case ARRAY_OPEN:
            return "ARRAY_OPEN"; // NOI18N
        case PARENTHESIS_OPEN:
            return "PARENTHESIS_OPEN"; // NOI18N
        case PARENTHESIS:
            return "PARENTHESIS"; // NOI18N
        case METHOD_OPEN:
            return "METHOD_OPEN"; // NOI18N
        case METHOD:
            return "METHOD"; // NOI18N
        case CONSTRUCTOR:
            return "CONSTRUCTOR"; // NOI18N
        case CONVERSION:
            return "CONVERSION"; // NOI18N
        case TYPE:
            return "TYPE"; // NOI18N
        case NEW:
            return "NEW"; // NOI18N
        case INSTANCEOF:
            return "INSTANCEOF"; // NOI18N
        default:
            return "Unknown id " + id; // NOI18N
        }
    }

    public String toString(int indent) {
        String indentStr = getIndent(indent);
        StringBuffer sb = new StringBuffer();
        sb.append("id=" + getIDName(id)); // NOI18N

        if (type != null) {
            sb.append(", result type="); // NOI18N
            sb.append(type);
        }

        // Debug tokens
        int tokenCnt = getTokenCount();
        int[] blocks = getTokenBlocks();
        sb.append(", token count="); // NOI18N
        sb.append(tokenCnt);
        if (tokenCnt > 0) {
            for (int i = 0; i < tokenInd;) {
                int tokenID = blocks[i++];
                int helperID = blocks[i++];
                int offset = blocks[i++];
                int len = blocks[i++];
                sb.append(", token" + (i / 4 - 1) + "='" + EditorDebug.debugChars(buffer, offset, len) + "'"); // NOI18N
            }
        }

        // Debug parameters
        int parmCnt = getParameterCount();
        sb.append(", parm count="); // NOI18N
        sb.append(parmCnt);
        if (parmCnt > 0) {
            for (int i = 0; i < parmCnt; i++) {
                sb.append('\n');
                sb.append(indentStr);
                sb.append("parm" + i + "=[" + getParameter(i).toString(indent + 2) + "]"); // NOI18N
            }
        }
        return sb.toString();
    }

    public String toString() {
        return toString(0);
    }

}

/*
 * Log
 *  8    Gandalf   1.7         1/13/00  Miloslav Metelka Localization
 *  7    Gandalf   1.6         12/28/99 Miloslav Metelka 
 *  6    Gandalf   1.5         11/14/99 Miloslav Metelka 
 *  5    Gandalf   1.4         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  4    Gandalf   1.3         10/10/99 Miloslav Metelka 
 *  3    Gandalf   1.2         10/4/99  Miloslav Metelka 
 *  2    Gandalf   1.1         9/30/99  Miloslav Metelka 
 *  1    Gandalf   1.0         9/15/99  Miloslav Metelka 
 * $
 */

