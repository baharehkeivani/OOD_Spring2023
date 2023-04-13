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

package org.netbeans.modules.form.compat2.border;

import java.awt.*;
import java.beans.*;

import javax.swing.border.*;

import org.openide.nodes.*;

/**
*
* @author   Petr Hamernik
* @version  1.02, Aug 07, 1998
*/
public class BevelBorderInfo extends BevelAbstractBorderInfo {
    /** generated Serialized Version UID */
    static final long serialVersionUID = -874648206615934776L;

    private final static BevelBorder DEFAULT_BORDER = new BevelBorder(BevelBorder.RAISED);

    /** Default icons for Border in design time. */
    static protected Image defaultIcon;
    static protected Image defaultIcon32;

    static {
        defaultIcon = Toolkit.getDefaultToolkit ().getImage (
                          BevelBorderInfo.class.getResource ("/org/netbeans/modules/form/resources/palette/bevelBorder.gif")); // NOI18N
        defaultIcon32 = Toolkit.getDefaultToolkit ().getImage (
                            BevelBorderInfo.class.getResource ("/org/netbeans/modules/form/resources/palette/bevelBorder32.gif")); // NOI18N
    }

    public BevelBorderInfo() {
    }

    /** An icon of the border. This icon will be used on the ComponentPalette
    * for this layout's item.
    * @param  type the desired type of the icon (BeanInfo.ICON_???)
    * @return layout's icon.
    */
    public Image getIcon(int type) {
        if ((type == BeanInfo.ICON_COLOR_16x16) || (type == BeanInfo.ICON_MONO_16x16))
            return defaultIcon;
        else
            return defaultIcon32;
    }

    protected Border createDefaultBorder() {
        return DEFAULT_BORDER;
    }

    protected void updateBorder() {
        border = new BevelBorder(bevelType, highlightOuter, highlightInner, shadowOuter, shadowInner);
    }

    public String getDisplayName() {
        return BorderInfo.bundle.getString("NAME_BevelBorder");
    }
}

/*
 * Log
 *  5    Gandalf   1.4         1/12/00  Ian Formanek    NOI18N
 *  4    Gandalf   1.3         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  3    Gandalf   1.2         6/8/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  2    Gandalf   1.1         4/6/99   Ian Formanek    fixed obtaining 
 *       resources (Object.class.getResource -> getClass ().getResource)
 *  1    Gandalf   1.0         4/2/99   Ian Formanek    
 * $
 */
