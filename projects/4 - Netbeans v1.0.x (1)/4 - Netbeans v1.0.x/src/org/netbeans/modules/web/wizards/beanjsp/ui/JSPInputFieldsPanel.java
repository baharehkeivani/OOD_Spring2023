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

package  org.netbeans.modules.web.wizards.beanjsp.ui;

import org.netbeans.modules.web.wizards.beanjsp.model.*;
import org.netbeans.modules.web.wizards.wizardfw.*;
import org.netbeans.modules.web.wizards.beanjsp.ide.netbeans.*;

import org.netbeans.modules.web.util.*;
import org.netbeans.modules.web.wizards.beanjsp.util.*;

import org.openide.util.*;


import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;



public class JSPInputFieldsPanel extends StandardWizardPanel {


    // ---------------------------------------------------------------------------------------
    // WizardPanel initialization

    /** Creates new JspColumnsPanel */

    public JSPInputFieldsPanel() {
        this(JSPPage.INPUT_PAGE);
    }

    public JSPInputFieldsPanel(int pageType) {
        super();
        this.pageType = pageType;
        initComponents ();
        addSelectionListeners();
        addDoubleClickListeners();
    }
    /*public HelpCtx getHelp () {
      return new HelpCtx (JSPInputFieldsPanel.class);
}*/


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents () {//GEN-BEGIN:initComponents

        java.util.ResourceBundle resBundle = NbBundle.getBundle(JSPPageWizard.i18nBundle);
        //// create components

        this.setTopMessage(resBundle.getString("JBW_InputFieldsMsg"));								 // NOI18N

        beanPropsLabel = new JLabel(resBundle.getString("JBW_AvailableInputFieldsListTitle"));		 // NOI18N
        beanFieldsList = new JList();
        beanPropsSPane = new JScrollPane(beanFieldsList);

        beanPropsSPane.setPreferredSize (new java.awt.Dimension(150, 150));
        beanPropsSPane.setMinimumSize (new java.awt.Dimension(150, 150));
        beanPropsSPane.setMaximumSize (new java.awt.Dimension(150, 150));


        useBeanPropsLabel = new JLabel(resBundle.getString("JBW_InputFieldsTableTitle"));				 // NOI18N
        jspInputFieldsTable = new JSPInputFieldTable();

        buttonsPanel = new javax.swing.JPanel();

        addButton = new javax.swing.JButton ();
        addButton.setText (resBundle.getString("JBW_AddButton"));									 // NOI18N
        addButton.setMinimumSize (new java.awt.Dimension(73, 15));
        addButton.setMaximumSize (new java.awt.Dimension(100, 27));


        removeButton = new javax.swing.JButton ();
        removeButton.setText (resBundle.getString("JBW_RemoveButton"));							 // NOI18N
        removeButton.setMinimumSize (new java.awt.Dimension(73, 15));
        removeButton.setMaximumSize (new java.awt.Dimension(100, 27));

        addAllButton = new javax.swing.JButton ();
        addAllButton.setText (resBundle.getString("JBW_AddAllButton"));							 // NOI18N
        addAllButton.setMinimumSize (new java.awt.Dimension(73, 15));
        addAllButton.setMaximumSize (new java.awt.Dimension(100, 27));

        removeAllButton = new javax.swing.JButton ();
        removeAllButton.setText (resBundle.getString("JBW_RemoveAllButton"));						 // NOI18N
        removeAllButton.setMinimumSize (new java.awt.Dimension(73, 15));
        removeAllButton.setMaximumSize (new java.awt.Dimension(100, 27));


        moveUpButton = new javax.swing.JButton ();
        moveUpButton.setText (resBundle.getString("JBW_MoveUpButton"));							 // NOI18N
        moveUpButton.setMinimumSize (new java.awt.Dimension(73, 15));
        moveUpButton.setMaximumSize (new java.awt.Dimension(100, 27));


        moveDownButton = new javax.swing.JButton ();
        moveDownButton.setText (resBundle.getString("JBW_MoveDownButton"));						 // NOI18N
        moveDownButton.setMinimumSize (new java.awt.Dimension(73, 15));
        moveDownButton.setMaximumSize (new java.awt.Dimension(100, 27));

        //// layout and add components

        arrangeComponents();

        //// create and add listeners

        beanFieldsModel = JSPPageWizard.simpleJSPPage.getAvailableSetterFieldsModel();
        jspInputFieldsTableModel = JSPPageWizard.simpleJSPPage.getJSPInputFieldsModel();

        this.beanFieldsList.setModel(beanFieldsModel);
        this.jspInputFieldsTable.setTableModel(jspInputFieldsTableModel);

        addAllButton.addActionListener( new ActionListener() {
                                            public void actionPerformed(ActionEvent evt) {
                                                addAllToUseBeanFieldsTable();
                                            }
                                        });

        removeAllButton.addActionListener( new ActionListener() {
                                               public void actionPerformed(ActionEvent evt) {
                                                   removeAllFromUseBeanFieldsTable();
                                               }
                                           });


        addButton.addActionListener( new ActionListener() {
                                         public void actionPerformed(ActionEvent evt) {
                                             addToUseBeanFieldsTable();
                                         }
                                     });

        removeButton.addActionListener( new ActionListener() {
                                            public void actionPerformed(ActionEvent evt) {
                                                removeFromUseBeanFieldsTable();
                                            }
                                        });

        moveUpButton.addActionListener( new ActionListener() {
                                            public void actionPerformed(ActionEvent evt) {
                                                moveUpUseBeanField();
                                            }
                                        });

        moveDownButton.addActionListener( new ActionListener() {
                                              public void actionPerformed(ActionEvent evt) {
                                                  moveDownUseBeanField();
                                              }
                                          });


    }

    //// layout components in
    private void arrangeComponents() {
        createButtonPanelComponents();
        arrangeCompsWithGridBag();
    }

    private void addGridBagComponent(Container parent, Component comp,
                                     int gridx, int gridy, int gridwidth, int gridheight,
                                     double weightx, double weighty,
                                     int anchor, int fill,
                                     Insets insets, int ipadx, int ipady
                                    ) {
        GridBagConstraints cons = new GridBagConstraints();
        cons.gridx = gridx;
        cons.gridy = gridy;
        cons.gridwidth = gridwidth;
        cons.gridheight = gridheight;
        cons.weightx = weightx;
        cons.weighty = weighty;
        cons.anchor = anchor;
        cons.fill = fill;
        cons.insets = insets;
        cons.ipadx = ipadx;
        cons.ipady = ipady;
        parent.add(comp,cons);
    }

    private void createButtonPanelComponents() {

        this.buttonsPanel.setLayout(new GridBagLayout());

        Component allGap = Box.createVerticalStrut(2);
        Component moveGap = Box.createVerticalStrut(6);

        Component remainderGlue = Box.createGlue();

        addGridBagComponent(this.buttonsPanel,addButton,
                            0,0,1,1,
                            100,00,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(2,2,2,2),5,5	);
        addGridBagComponent(this.buttonsPanel,removeButton,
                            0,1,1,1,
                            100,0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(2,2,2,2),5,5	);

        addGridBagComponent(this.buttonsPanel,allGap,
                            0,2,1,1,
                            100,0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(2,2,2,2),5,5	);

        addGridBagComponent(this.buttonsPanel,addAllButton,
                            0,3,1,1,
                            100,00,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(2,2,2,2),5,5	);
        addGridBagComponent(this.buttonsPanel,removeAllButton,
                            0,4,1,1,
                            100,0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(2,2,2,2),5,5	);

        addGridBagComponent(this.buttonsPanel,moveGap,
                            0,5,1,1,
                            100,0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(2,2,2,2),5,5	);

        addGridBagComponent(this.buttonsPanel,moveUpButton,
                            0,6,1,1,
                            100,0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(2,2,2,2),5,5	);

        addGridBagComponent(this.buttonsPanel,moveDownButton,
                            0,7,1,1,
                            100,0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(2,2,2,2),5,5	);

        addGridBagComponent(this.buttonsPanel,remainderGlue,
                            0,8,1,1,
                            100,100,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(2,2,2,2),5,5	);


    }

    private void arrangeCompsWithGridBag() {

        this.contentPane.setLayout(new GridBagLayout());


        addGridBagComponent(this.contentPane,beanPropsLabel,
                            0,0,1,1,
                            0,0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(2,2,2,2),5,5	);

        addGridBagComponent(this.contentPane,useBeanPropsLabel,
                            2,0,1,1,
                            100,0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(2,2,2,2),5,5	);

        addGridBagComponent(this.contentPane,beanPropsSPane,
                            0,1,1,1,
                            0,100,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(2,2,2,2),5,5	);

        addGridBagComponent(this.contentPane,buttonsPanel,
                            1,1,1,1,
                            0,100,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(2,2,2,2),5,5	);

        addGridBagComponent(this.contentPane,jspInputFieldsTable,
                            2,1,1,1,
                            100,100,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(2,2,2,2),5,5	);
    }




    // Variables declaration - do not modify//GEN-BEGIN:variables

    private javax.swing.JLabel titleLabel;

    private javax.swing.JScrollPane beanPropsSPane;
    private javax.swing.JList beanFieldsList;
    private javax.swing.JLabel beanPropsLabel;

    private javax.swing.JPanel buttonsPanel;

    private javax.swing.JButton addButton;
    private javax.swing.JButton removeButton;

    private javax.swing.JButton addAllButton;
    private javax.swing.JButton removeAllButton;

    private javax.swing.JButton moveUpButton;
    private javax.swing.JButton moveDownButton;


    private JSPInputFieldTable jspInputFieldsTable;
    private javax.swing.JLabel useBeanPropsLabel;

    private JSPItemListModel beanFieldsModel;
    private JSPInputFieldTableModel jspInputFieldsTableModel;

    private int pageType = JSPPage.INPUT_PAGE;

    // methods


    public void addAllToUseBeanFieldsTable() {
        JSPVector addFields = beanFieldsModel.getItems();
        jspInputFieldsTableModel.addItems(addFields);
        beanFieldsModel.removeAll();
        setButtonsEnabled();
    }

    public void removeAllFromUseBeanFieldsTable() {
        JSPVector removeFields = jspInputFieldsTableModel.getJSPBeanFields();
        beanFieldsModel.addItems(removeFields);
        jspInputFieldsTableModel.removeAll();
        setButtonsEnabled();
    }


    public void addToUseBeanFieldsTable() {
        int[] idx = beanFieldsList.getSelectedIndices();
        int  count = 0;
        int oldRows = jspInputFieldsTable.getTable().getRowCount();
        if( (idx.length > 0) && (idx[0] >= 0)){
            for (int i=0; i<idx.length; i++) {
                Object obj = beanFieldsModel.remove(idx[i]-count);
                jspInputFieldsTableModel.add((JSPBeanField)obj);
                count++;
            }
            // set selection input fields
            int rows = jspInputFieldsTable.getTable().getRowCount();
            jspInputFieldsTable.getTable().getSelectionModel().setSelectionInterval(
                oldRows, rows-1);
            setButtonsEnabled();
        }
    }

    public void removeFromUseBeanFieldsTable() {
        int[] idx = jspInputFieldsTable.getTable().getSelectedRows();
        int  count = 0;
        int oldRows = beanFieldsModel.getSize();
        if((idx.length > 0) && (idx[0] >= 0)) {
            for (int i=0; i<idx.length; i++) {
                Object obj = jspInputFieldsTableModel.remove(idx[i]-count);
                beanFieldsModel.add(obj);
                count++;
            }
            // set selection bean fields
            int rows = beanFieldsModel.getSize();
            beanFieldsList.getSelectionModel().setSelectionInterval(
                oldRows, rows-1);
            setButtonsEnabled();
        }
    }

    public void moveUpUseBeanField() {
        int[] idx = jspInputFieldsTable.getTable().getSelectedRows();
        if((idx.length > 0) && (idx[0] > 0 )) {
            for (int i=0; i<idx.length; i++){
                jspInputFieldsTableModel.moveUp(idx[i]);
            }
            jspInputFieldsTable.getTable().getSelectionModel().setSelectionInterval(
                idx[0]-1, idx[idx.length-1]-1);
            checkButtons();
        }
    }

    public void moveDownUseBeanField() {
        int[] idx = jspInputFieldsTable.getTable().getSelectedRows();
        int rowCount = jspInputFieldsTable.getTable().getRowCount();
        if((idx.length >0) && (idx[0] >= 0) && (idx[idx.length-1] < rowCount-1)) {
            for (int i=idx.length-1; i>=0; i--){
                jspInputFieldsTableModel.moveDown(idx[i]);
            }
            jspInputFieldsTable.getTable().getSelectionModel().setSelectionInterval(
                idx[0]+1,idx[idx.length-1]+1);
            checkButtons();
        }
    }

    public void setButtonsEnabled(){
        if (beanFieldsModel.getSize() > 0){
            addAllButton.setEnabled(true);
        }
        else {
            addAllButton.setEnabled(false);
        }
        int rows = jspInputFieldsTable.getTable().getRowCount();
        if (rows > 0){
            removeAllButton.setEnabled(true);
        } else {
            removeAllButton.setEnabled(false);
        }
        checkAddButton();
        checkButtons();
    }

    private void checkAddButton(){
        if ((beanFieldsModel.getSize() > 0) && (beanFieldsList.getSelectedIndex()>=0))
            addButton.setEnabled(true);
        else
            addButton.setEnabled(false);
    }

    private void checkButtons(){
        int rows = jspInputFieldsTable.getTable().getRowCount();
        int[] srow = jspInputFieldsTable.getTable().getSelectedRows();
        if ((rows > 0) && (srow.length >0)){
            removeButton.setEnabled(true);
        } else {
            removeButton.setEnabled(false);
        }
        if ((rows > 1) && (srow.length >0) && (srow[0] > 0)){
            moveUpButton.setEnabled(true);
        } else {
            moveUpButton.setEnabled(false);
        }
        if ((rows > 1) && (srow.length >0) && (srow[srow.length -1] < rows -1)){
            moveDownButton.setEnabled(true);
        } else {
            moveDownButton.setEnabled(false);
        }
    }

    private void addSelectionListeners(){
        beanFieldsList.addListSelectionListener(new ListSelectionListener(){
                                                    public void valueChanged(ListSelectionEvent e){
                                                        checkAddButton();
                                                    }
                                                });
        jspInputFieldsTable.getTable().getSelectionModel().addListSelectionListener(
            new ListSelectionListener(){
                public void valueChanged(ListSelectionEvent e){
                    checkButtons();
                }
            });
    }

    private void addDoubleClickListeners(){
        beanFieldsList.addMouseListener (new java.awt.event.MouseAdapter () {
                                             public void mouseClicked (java.awt.event.MouseEvent evt) {
                                                 beanFieldsListMouseClicked(evt);
                                             }
                                         });
        jspInputFieldsTable.getTable().addMouseListener (new java.awt.event.MouseAdapter () {
                    public void mouseClicked (java.awt.event.MouseEvent evt) {
                        useBeanFieldsTableMouseClicked(evt);
                    }
                });
    }

    private void beanFieldsListMouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            addToUseBeanFieldsTable();
        }
    }

    private void useBeanFieldsTableMouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            removeFromUseBeanFieldsTable();
        }
    }

    public static void main(String[] args) {
        if(Debug.TEST) {
            JFrame testFrame = new JFrame("This is Test Frame");			 // NOI18N
            testFrame.getContentPane().add(new JSPInputFieldsPanel(),SwingConstants.CENTER);
            testFrame.setSize(500,300);
            testFrame.pack();
            testFrame.show();
        }
    }
}
