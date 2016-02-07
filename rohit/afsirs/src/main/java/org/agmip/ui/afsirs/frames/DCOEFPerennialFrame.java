/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.agmip.ui.afsirs.frames;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.agmip.ui.afsirs.util.AFSIRSUtils;

/**
 *
 * @author Piyush
 */
public class DCOEFPerennialFrame extends javax.swing.JFrame {

    /**
     * Creates new form DCOEFFrame
     */
    AFSIRSUtils utils = AFSIRSUtils.getInstance();
    double DWT = 20.0, DRZIRR, DRZTOT;

    double[] AKC = new double[12];
    double[] ALDP = new double[12];

    double[] RKTMP = new double[365];
    double[] AWTMP = new double[365];
    int ir, icrop;
    boolean ivers;
    JFrame prev, next = null;

    public DCOEFPerennialFrame(JFrame prev) {
        initComponents();
        ir = utils.getIrrigationSystem();
        ivers = utils.getIVERS();
        icrop = utils.getICROP();
        this.prev = prev;
        setTitle("Perennial Crop");
        irrigationSelectedLabel.setText("Irrigation System = " + utils.getIrrigationSystemName());
        dwtEnteredLabel.setText("Entered Depth of Water Table is  " + utils.getDWT() + " inches");
        setLocation(400, 50);
        init();
        getRootPane().setDefaultButton(nextButton);
    }

    public void init() {
        setDefaultValues();

        //If generic crop
        if (ir != AFSIRSUtils.IRCRFL) {
            hgtText.setEnabled(false);
        }

    }

    public void readCropData() {
        //Read crop data from crop.dat 
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/data/crop.dat")));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(utils.getCropName())) {
                    break;
                }
            }

            String data = line.substring(14);
            StringBuffer popupText = new StringBuffer("");
            popupText.append("Crop Name : " + line.substring(0, 14) + "\n");
            cropSelectedLabel.setText("Crop selected is " + line.substring(0, 14));
            String[] arr = data.split(" ");
            int i = 0;
            for (String str : arr) {
                AKC[i] = Double.parseDouble(str);
                i++;
            }
            line = br.readLine();
            data = line.substring(8);
            if (data.charAt(0) == ' ') {
                data = data.substring(1);
            }
            arr = data.split(" ");
            i = 0;
            for (String str : arr) {
                if (str.length() < 1) {
                    continue;
                }
                if (i == 0) {
                    DRZIRR = Double.parseDouble(str);
                } else if (i == 1) {
                    DRZTOT = Double.parseDouble(str);
                } else {
                    ALDP[i - 2] = Double.parseDouble(str);
                }
                i++;
            }

            if (ir == AFSIRSUtils.IRSEEP) {
                for (int j = 0; j < 12; j++) {
                    ALDP[j] = 0.0;
                    if (AKC[j] < 1.0) {
                        AKC[j] = 1.0;
                    }
                }
                DRZIRR = DWT;
                DRZTOT = 1.5 * DWT;
                drzirrText.setEnabled(false);
                drztotText.setEnabled(false);
                aldpTable.setEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        crownLabel = new javax.swing.JLabel();
        hgtText = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        backButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        generic = new javax.swing.JRadioButton();
        manual = new javax.swing.JRadioButton();
        drzirrLabel = new javax.swing.JLabel();
        drzirrText = new javax.swing.JTextField();
        drztotLabel = new javax.swing.JLabel();
        drztotText = new javax.swing.JTextField();
        akcLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        aldpLabel = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        aldpTable = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        akcTable = new javax.swing.JTable();
        dwtEnteredLabel = new javax.swing.JLabel();
        irrigationSelectedLabel = new javax.swing.JLabel();
        cropSelectedLabel = new javax.swing.JLabel();
        infoButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        crownLabel.setText("Height of Crown flood system bed (1.0 - 5.0)");

        hgtText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hgtTextActionPerformed(evt);
            }
        });

        jLabel12.setText("Feet");

        backButton.setText("Back");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        nextButton.setText("Finish");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        jLabel13.setText("Crop data Input Mode");

        buttonGroup1.add(generic);
        generic.setText("Default Crop Data");
        generic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genericActionPerformed(evt);
            }
        });

        buttonGroup1.add(manual);
        manual.setSelected(true);
        manual.setText("Manually Input Crop Data");
        manual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manualActionPerformed(evt);
            }
        });

        drzirrLabel.setText("Irrigated root zone depth");

        drzirrText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drzirrTextActionPerformed(evt);
            }
        });

        drztotLabel.setText("Total crop root zone depth");

        drztotText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drztotTextActionPerformed(evt);
            }
        });

        akcLabel.setText("Monthly Crop Water use coefficients (0.0 - 2.0)");

        jLabel4.setText("Inches");

        jLabel6.setText("Inches");

        aldpLabel.setText("Allowable soil water depletions (0.0 - 1.0)");

        aldpTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Jan.", "Feb.", "Mar.", "April", "May", "June", "July", "Aug.", "Sep.", "Oct.", "Nov.", "Dec."
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        aldpTable.setRowHeight(24);
        jScrollPane1.setViewportView(aldpTable);

        jScrollPane3.setViewportView(jScrollPane1);

        akcTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Jan.", "Feb.", "Mar.", "April", "May", "June", "July", "Aug.", "Sep.", "Oct.", "Nov.", "Dec."
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        akcTable.setRowHeight(24);
        jScrollPane5.setViewportView(akcTable);

        jScrollPane4.setViewportView(jScrollPane5);

        dwtEnteredLabel.setText("jLabel3");

        irrigationSelectedLabel.setText("jLabel2");

        cropSelectedLabel.setText("jLabel1");

        infoButton.setFont(new java.awt.Font("Yu Gothic Light", 2, 18)); // NOI18N
        infoButton.setText("i");
        infoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cropSelectedLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(infoButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
                                .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(drzirrLabel, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(drztotLabel, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(aldpLabel, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addComponent(akcLabel, javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                    .addGap(10, 10, 10)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(generic)
                                            .addGap(18, 18, 18)
                                            .addComponent(manual))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(drztotText, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jLabel6))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(drzirrText, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jLabel4)))))
                            .addComponent(dwtEnteredLabel)
                            .addComponent(irrigationSelectedLabel))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cropSelectedLabel)
                    .addComponent(infoButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(irrigationSelectedLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(dwtEnteredLabel)
                .addGap(20, 20, 20)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(generic)
                    .addComponent(manual))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(drzirrLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(drzirrText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(drztotLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(drztotText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(akcLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(aldpLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(backButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(nextButton))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(crownLabel)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(hgtText, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel12)))
                                .addGap(16, 16, 16)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(crownLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hgtText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(backButton)
                    .addComponent(nextButton))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void manualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manualActionPerformed
        // TODO add your handling code here:
        if (manual.isSelected()) {
            if (ir != AFSIRSUtils.IRSEEP) {
                drzirrText.setEnabled(true);
                drztotText.setEnabled(true);
                aldpTable.setEnabled(true);
            }
            akcTable.setEnabled(true);
        }
    }//GEN-LAST:event_manualActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
        prev.setVisible(true);

    }//GEN-LAST:event_backButtonActionPerformed

    private void genericActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genericActionPerformed
        // TODO add your handling code here:
        if (generic.isSelected()) {
            drzirrText.setEnabled(false);
            drztotText.setEnabled(false);
            akcTable.setEnabled(false);
            aldpTable.setEnabled(false);
        }
        setDefaultValues();
    }//GEN-LAST:event_genericActionPerformed

    private void setDefaultValues() {
        readCropData();
        drzirrText.setText(DRZIRR + "");
        drztotText.setText(DRZTOT + "");
        Vector akcData = new Vector();
        for (int i = 0; i < 12; i++) {
            akcData.add(AKC[i]);
        }

        Vector aldpData = new Vector();
        for (int i = 0; i < 12; i++) {
            aldpData.add(ALDP[i]);
        }
        ((DefaultTableModel) akcTable.getModel()).removeRow(0);
        ((DefaultTableModel) aldpTable.getModel()).removeRow(0);
        ((DefaultTableModel) akcTable.getModel()).addRow(akcData);
        ((DefaultTableModel) aldpTable.getModel()).addRow(aldpData);
    }

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        // TODO add your handling code here:
        boolean isFailed = false;

        if (jPanel1.isVisible() && manual.isSelected()) {
            try {
                DRZIRR = Double.parseDouble(drzirrText.getText());
            } catch (NumberFormatException e) {
                isFailed = true;
                drzirrLabel.setForeground(Color.red);
            }
            try {
                DRZTOT = Double.parseDouble(drztotText.getText());
            } catch (NumberFormatException e) {
                isFailed = true;
                drztotLabel.setForeground(Color.red);
            }

            if (DRZIRR > DRZTOT) {
                drzirrLabel.setForeground(Color.red);
                drztotLabel.setForeground(Color.red);
                isFailed = true;
            }
            DefaultTableModel akcModel = (DefaultTableModel) akcTable.getModel();
            DefaultTableModel aldpModel = (DefaultTableModel) aldpTable.getModel();
            try {
                for (int i = 0; i < 12; i++) {
                    AKC[i] = (Double) akcModel.getValueAt(0, i);
                    if (AKC[i] < 0.0 || AKC[i] > 2.0) {
                        isFailed = true;
                        akcLabel.setForeground(Color.red);
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                isFailed = true;
                akcLabel.setForeground(Color.red);
            }
            try {
                for (int i = 0; i < 12; i++) {
                    ALDP[i] = (Double) aldpModel.getValueAt(0, i);
                    if (ALDP[i] < 0.0 || ALDP[i] > 1.0) {
                        isFailed = true;
                        aldpLabel.setForeground(Color.red);
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                isFailed = true;
                aldpLabel.setForeground(Color.red);
            }

        } else {
            if (ir == AFSIRSUtils.IRSEEP) {
                if (ivers) {
                    DWT = 20.0;
                }
                DRZIRR = DWT;
                DRZTOT = 1.5 * DWT;
                for (int i = 0; i < 12; i++) {
                    ALDP[i] = 0.0;
                    if (AKC[i] < 1.0) {
                        akcLabel.setForeground(Color.red);
                    }
                }
            }
        }
        double HGT = 0.0;
        if (ir == AFSIRSUtils.IRCRFL) {
            try {
                HGT = Double.parseDouble(hgtText.getText());
                if (HGT > 5.0 || HGT < 1.0) {
                    isFailed = true;
                    crownLabel.setText("Enter value in range 1.0 to 5.0 ");
                    crownLabel.setForeground(Color.red);
                } else {
                    double HGTIN = 12.0 * HGT;
                    DWT = HGTIN;
                    DRZIRR = HGTIN - 6.0;
                    DRZTOT = HGTIN + 12.0 / HGT;
                    for (int i = 0; i < 12; i++) {
                        if (ALDP[i] < 0.5) {
                            ALDP[i] = 0.5;
                        }
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                crownLabel.setForeground(Color.red);
                isFailed = true;
            }

        }

        if (!isFailed) {
            drzirrLabel.setForeground(Color.black);
            drztotLabel.setForeground(Color.black);
            akcLabel.setForeground(Color.black);
            aldpLabel.setForeground(Color.black);
            hgtText.setForeground(Color.black);

            utils.setDCOEFPerennial(DRZIRR, DRZTOT, AKC, ALDP);
            if (ir == AFSIRSUtils.IRCRFL) {
                utils.setHGT(HGT);
            }
            utils.finishInput();

            this.setVisible(false);
            JFrame finish = new FinalFrame();
        }


    }//GEN-LAST:event_nextButtonActionPerformed

    private void hgtTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hgtTextActionPerformed
        // TODO add your handling code here:
        nextButtonActionPerformed(evt);
    }//GEN-LAST:event_hgtTextActionPerformed

    private void infoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoButtonActionPerformed
        // TODO add your handling code here:
        String str;
        str = "<html><body><p>Select Default for reading values from CROP.dat <br>and manual to enter values from keyboard.</p><p>"
                + "<br>If irrigation type selected is Seepage, then <br>"
                + "Irrigated root zone depth = Depth of Water Table<br>Total crop root zone depth = 1.5 * Depth of Water Table<br>Monthly Crop Water Use Co-efficients >= 1.0<br>and,"
                + "<br>Allowable soil water depletions = 0.0</p>"
                + "<p>Height of Crown Flood System Bed is required only<br>for Crown Flood System.</p></body></html>";

        JOptionPane.showMessageDialog(null, str);
    }//GEN-LAST:event_infoButtonActionPerformed

    private void drzirrTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drzirrTextActionPerformed
        // TODO add your handling code here:
        nextButtonActionPerformed(evt);
    }//GEN-LAST:event_drzirrTextActionPerformed

    private void drztotTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drztotTextActionPerformed
        // TODO add your handling code here:
        nextButtonActionPerformed(evt);
    }//GEN-LAST:event_drztotTextActionPerformed

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel akcLabel;
    private javax.swing.JTable akcTable;
    private javax.swing.JLabel aldpLabel;
    private javax.swing.JTable aldpTable;
    private javax.swing.JButton backButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel cropSelectedLabel;
    private javax.swing.JLabel crownLabel;
    private javax.swing.JLabel drzirrLabel;
    private javax.swing.JTextField drzirrText;
    private javax.swing.JLabel drztotLabel;
    private javax.swing.JTextField drztotText;
    private javax.swing.JLabel dwtEnteredLabel;
    private javax.swing.JRadioButton generic;
    private javax.swing.JTextField hgtText;
    private javax.swing.JButton infoButton;
    private javax.swing.JLabel irrigationSelectedLabel;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JRadioButton manual;
    private javax.swing.JButton nextButton;
    // End of variables declaration//GEN-END:variables
}