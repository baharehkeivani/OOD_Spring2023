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

package org.netbeans.modules.projects;

import java.util.Vector;
import java.io.File;
import org.openide.util.*;

/**
 * Panel that allow choose imported system jar zip files
 * @author  Petr Zajac
 * @version 
 */
public class ImportClassPathPane extends javax.swing.JPanel {

    /** Vector of String , contains input classpath */
    Vector inputStrings = new Vector ();
    /** Vector of String , contains output classpath 
     * @associates Object*/
    Vector outputStrings = new Vector ();

    /** Reload JLists
     */
    public void updateData () {
        inputList.setListData (inputStrings);
        outputList.setListData (outputStrings);
    }

    /** Creates new form ImportClassPathPane
     * @param classpath aray of jar and zip files 
     */

    public ImportClassPathPane (File [] classpath) {
        int i;
        for ( i = 0 ; i < classpath.length  ; i++ ) {
            inputStrings.addElement (classpath[i].getAbsolutePath ());
        }

        initComponents ();
        updateData();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents () {
        setLayout (new java.awt.GridBagLayout ());
        java.awt.GridBagConstraints gridBagConstraints1;

        jPanel1 = new javax.swing.JPanel ();
        jPanel1.setLayout (new java.awt.FlowLayout ());

        addBut = new javax.swing.JButton ();
        addBut.setText (NbBundle.getBundle (Import.class).getString ("CTL_AddButtonText"));
        addBut.addActionListener (new java.awt.event.ActionListener () {
                                      public void actionPerformed (java.awt.event.ActionEvent evt) {
                                          addButActionPerformed (evt);
                                      }
                                  }
                                 );

        jPanel1.add (addBut);

        addAllBut = new javax.swing.JButton ();
        addAllBut.setText (NbBundle.getBundle (Import.class).getString ("CTL_AddAllButtonText"));
        addAllBut.addActionListener (new java.awt.event.ActionListener () {
                                         public void actionPerformed (java.awt.event.ActionEvent evt) {
                                             addAllButActionPerformed (evt);
                                         }
                                     }
                                    );

        jPanel1.add (addAllBut);

        removeBut = new javax.swing.JButton ();
        removeBut.setText (NbBundle.getBundle (Import.class).getString ("CTL_RemoveButtonText"));
        removeBut.addActionListener (new java.awt.event.ActionListener () {
                                         public void actionPerformed (java.awt.event.ActionEvent evt) {
                                             removeButActionPerformed (evt);
                                         }
                                     }
                                    );

        jPanel1.add (removeBut);

        removeAll = new javax.swing.JButton ();
        removeAll.setText (NbBundle.getBundle (Import.class).getString ("CTL_RemoveAllButtonText"));
        removeAll.addActionListener (new java.awt.event.ActionListener () {
                                         public void actionPerformed (java.awt.event.ActionEvent evt) {
                                             removeAllActionPerformed (evt);
                                         }
                                     }
                                    );

        jPanel1.add (removeAll);


        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        add (jPanel1, gridBagConstraints1);

        jPanel3 = new javax.swing.JPanel ();
        jPanel3.setLayout (new java.awt.GridLayout (1, 1));
        jPanel3.setBorder (new javax.swing.border.CompoundBorder(
                               new javax.swing.border.TitledBorder(NbBundle.getBundle (Import.class).getString ("CTL_TitledBorderInputFileSystemClasses")),
                               new javax.swing.border.EmptyBorder(new java.awt.Insets(8, 8, 8, 8))));

        jScrollPane3 = new javax.swing.JScrollPane ();

        inputList = new javax.swing.JList ();
        inputList.addMouseListener (new java.awt.event.MouseAdapter () {
                                        public void mouseClicked (java.awt.event.MouseEvent evt) {
                                            inputListMouseClicked (evt);
                                        }
                                    }
                                   );

        jScrollPane3.setViewportView (inputList);

        jPanel3.add (jScrollPane3);


        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.insets = new java.awt.Insets (8, 8, 8, 8);
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 0.2;
        add (jPanel3, gridBagConstraints1);

        jPanel4 = new javax.swing.JPanel ();
        jPanel4.setLayout (new java.awt.GridLayout (1, 1));
        jPanel4.setBorder (new javax.swing.border.CompoundBorder(
                               new javax.swing.border.TitledBorder(NbBundle.getBundle (Import.class).getString ("CTL_TitledBorderMountFiles")),
                               new javax.swing.border.EmptyBorder(new java.awt.Insets(8, 8, 8, 8))));

        jScrollPane4 = new javax.swing.JScrollPane ();

        outputList = new javax.swing.JList ();
        outputList.addMouseListener (new java.awt.event.MouseAdapter () {
                                         public void mouseClicked (java.awt.event.MouseEvent evt) {
                                             outputListMouseClicked (evt);
                                         }
                                     }
                                    );

        jScrollPane4.setViewportView (outputList);

        jPanel4.add (jScrollPane4);


        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.insets = new java.awt.Insets (8, 8, 8, 8);
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 0.2;
        add (jPanel4, gridBagConstraints1);

    }

    private void outputListMouseClicked (java.awt.event.MouseEvent evt) {//GEN-FIRST:event_outputListMouseClicked
        // Add your handling code here:
        if (evt.getClickCount () % 2 == 0) {
            removeFile ();
        }
    }//GEN-LAST:event_outputListMouseClicked

    private void inputListMouseClicked (java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inputListMouseClicked
        // Add your handling code here:
        if (evt.getClickCount () % 2 == 0) {
            addFile ();
        }
    }//GEN-LAST:event_inputListMouseClicked

    /** Action after clicking on removeAll Button
     * @param event 
     */

    private void removeAllActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAllActionPerformed
        // Add your handling code here:
        int index = 0;
        for (index = 0 ; index < outputStrings.size () ; index ++) {
            inputStrings.addElement (outputStrings.elementAt (index));
        }
        outputStrings.removeAllElements ();
        updateData ();
    }//GEN-LAST:event_removeAllActionPerformed

    /** Action after clicking on Remove Button
     * @param event 
     */

    private void removeButActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButActionPerformed
        // Add your handling code here:
        removeFile ();
    }//GEN-LAST:event_removeButActionPerformed

    private void removeFile () {
        int index = outputList.getSelectedIndex ();
        if (index != -1 ) {
            // selected
            inputStrings.addElement (outputStrings.elementAt (index));
            outputStrings.removeElementAt (index);
            updateData ();
        }
    }

    /** Action after clicking on AddAll Button
     * @param event 
     */
    private void addAllButActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAllButActionPerformed
        // Add your handling code here:
        int index = 0;
        for (index = 0 ; index < inputStrings.size () ; index ++) {
            outputStrings.addElement (inputStrings.elementAt (index));
        }
        inputStrings.removeAllElements ();
        updateData ();
    }//GEN-LAST:event_addAllButActionPerformed

    /** Action after clicking on Add Button
     * @param event 
     */

    private void addButActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButActionPerformed
        // Add your handling code here:
        addFile ();
    }//GEN-LAST:event_addButActionPerformed

    private void addFile () {
        int index = inputList.getSelectedIndex ();
        if (index != -1 ) {
            // selected
            outputStrings.addElement (inputStrings.elementAt (index));
            inputStrings.removeElementAt (index);
            updateData ();
        }
    }

    /** get jar and zip files that will be import
     * @return jars and zips files wich will be imported
     */
    File [] getOutputFiles() {

        File[] files= new File[outputStrings.size ()];
        for (int i=0 ; i < files.length ; i ++ ) {
            files[i] = new File ((String ) outputStrings.elementAt(i));
        }
        return files;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton addBut;
    private javax.swing.JButton addAllBut;
    private javax.swing.JButton removeBut;
    private javax.swing.JButton removeAll;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList inputList;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JList outputList;
    // End of variables declaration//GEN-END:variables

}
/*
 * Log
 *  2    Gandalf   1.1         1/14/00  Petr Zajac      Bundles added
 *  1    Gandalf   1.0         1/3/00   Martin Ryzl     
 * $
 */

