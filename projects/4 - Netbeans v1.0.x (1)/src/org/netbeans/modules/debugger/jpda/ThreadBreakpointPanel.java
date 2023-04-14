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

package org.netbeans.modules.debugger.jpda;

import org.openide.util.NbBundle;


/**
 *
 * @author  jjancura
 * @version 
 */
public class ThreadBreakpointPanel extends javax.swing.JPanel {

    private ThreadBreakpoint threadBreakpoint;


    /** Creates new form ThreadBreakpointPanel */
    public ThreadBreakpointPanel (ThreadBreakpoint tb) {
        threadBreakpoint = tb;
        initComponents ();
        jComboBox1.addItem (NbBundle.getBundle (ThreadBreakpointPanel.class).
                            getString ("CTL_Thread_start"));
        jComboBox1.addItem (NbBundle.getBundle (ThreadBreakpointPanel.class).
                            getString ("CTL_Thread_death"));
        jComboBox1.addItem (NbBundle.getBundle (ThreadBreakpointPanel.class).
                            getString ("CTL_Thread_both"));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents () {//GEN-BEGIN:initComponents
        setLayout (new java.awt.GridBagLayout ());
        java.awt.GridBagConstraints gridBagConstraints1;

        jLabel1 = new javax.swing.JLabel ();
        jLabel1.setText (NbBundle.getBundle (ThreadBreakpointPanel.class).getString ("CTL_Thread_Set_breakpoint_on"));


        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.insets = new java.awt.Insets (2, 2, 2, 2);
        add (jLabel1, gridBagConstraints1);

        jComboBox1 = new javax.swing.JComboBox ();
        jComboBox1.addActionListener (new java.awt.event.ActionListener () {
                                          public void actionPerformed (java.awt.event.ActionEvent evt) {
                                              jComboBox1ActionPerformed (evt);
                                          }
                                      }
                                     );


        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.insets = new java.awt.Insets (2, 2, 2, 2);
        add (jComboBox1, gridBagConstraints1);

        jPanel1 = new javax.swing.JPanel ();
        jPanel1.setLayout (new java.awt.FlowLayout ());


        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 10;
        gridBagConstraints1.gridy = 10;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        add (jPanel1, gridBagConstraints1);

    }//GEN-END:initComponents

    private void jComboBox1ActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // Add your handling code here:
        threadBreakpoint.setType (jComboBox1.getSelectedIndex () + 1);
    }//GEN-LAST:event_jComboBox1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

}
/*
 * Log
 *  2    Gandalf   1.1         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  1    Gandalf   1.0         9/28/99  Jan Jancura     
 * $
 */