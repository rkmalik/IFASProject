/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.agmip.ui.afsirs.frames;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.agmip.ui.afsirs.util.AFSIRSUtils;

/**
 *
 * @author Rohit Kumar Malik
 */
public class DCOEFAnnualFrame extends javax.swing.JFrame {

    /**
     * Creates new form DCOEFAnnualFrame
     */
    double[] ALD = new double[4];
    double[] F = new double[4];
    double AKC3, AKC4, DZN, DZX, DWT = 20.0;
    private JFrame prev, next = null;
    AFSIRSUtils utils = null;
    int ir, icrop;
    boolean ivers;

    public DCOEFAnnualFrame(JFrame prev) {
        initComponents();
        setLocation(400, 50);
        utils = AFSIRSUtils.getInstance();
        ir = utils.getIrrigationSystem();
        ivers = utils.getIVERS();
        icrop = utils.getICROP();
        this.prev = prev;
        irrigationSelectedLabel.setText("Irrigation System = " + utils.getIrrigationSystemName());
        dwtEnteredLabel.setText("Entered Depth of Water Table is  " + utils.getDWT() + " inches");
        setTitle("Annual Crop");
        getRootPane().setDefaultButton(nextButton);
        init();
    }

    public void init() {
        readCropData();
        setDefaultValues();
    }

    public void readCropData() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/data/crop.dat")));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(utils.getCropName())) {
                    break;
                }
            }
            cropSelectedLabel.setText("Crop selected is " + utils.getCropName());
            String data = line.substring(12);
            StringBuffer popupText = new StringBuffer("");
            String[] arr = data.split(" ");
            int i = 0;
            for (String str : arr) {
                if (str.length() < 1) {
                    continue;
                }
                if (i == 0) {
                    DZN = Double.parseDouble(str);
                } else if (i == 1) {
                    DZX = Double.parseDouble(str);
                } else if (i == 2) {
                    AKC3 = Double.parseDouble(str);
                } else if (i == 3) {
                    AKC4 = Double.parseDouble(str);
                } else if (i < 8) {
                    F[i - 4] = Double.parseDouble(str);
                } else {
                    ALD[i - 8] = Double.parseDouble(str);
                }
                i++;
            }

            if (ir == AFSIRSUtils.IRSEEP) {
                DZN = DWT;
                DZX = DWT;
                for (int j = 0; j < 4; j++) {
                    ALD[j] = 0.0;
                }
                AKC3 = Math.max(1.0, AKC3);
                AKC4 = Math.max(1.0, AKC4);
                dznText.setEnabled(false);
                dzxText.setEnabled(false);
                ALD1Text.setEnabled(false);
                ALD2Text.setEnabled(false);
                ALD3Text.setEnabled(false);
                ALD4Text.setEnabled(false);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
                        
            }
            
        }
    }

    public void enableInput(boolean enable) {
        if (ir != AFSIRSUtils.IRSEEP) {
            dznText.setEnabled(enable);
            dzxText.setEnabled(enable);
            ALD1Text.setEnabled(enable);
            ALD2Text.setEnabled(enable);
            ALD3Text.setEnabled(enable);
            ALD4Text.setEnabled(enable);
        }

        akc3Text.setEnabled(enable);
        akc4Text.setEnabled(enable);
        F1Text.setEnabled(enable);
        F2Text.setEnabled(enable);
        F3Text.setEnabled(enable);
        F4Text.setEnabled(enable);

    }

    public void setDefaultValues() {
        readCropData();
        dznText.setText(DZN + "");
        dzxText.setText(DZX + "");
        akc3Text.setText(AKC3 + "");
        akc4Text.setText(AKC4 + "");
        F1Text.setText(F[0] + "");
        F2Text.setText(F[1] + "");
        F3Text.setText(F[2] + "");
        F4Text.setText(F[3] + "");
        ALD1Text.setText(ALD[0] + "");
        ALD2Text.setText(ALD[1] + "");
        ALD3Text.setText(ALD[2] + "");
        ALD4Text.setText(ALD[3] + "");
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
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        generic = new javax.swing.JRadioButton();
        manual = new javax.swing.JRadioButton();
        dznLabel = new javax.swing.JLabel();
        dznText = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        F2Text = new javax.swing.JTextField();
        ALD4Text = new javax.swing.JTextField();
        aldLabel = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        akc3Text = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        FLabel = new javax.swing.JLabel();
        ALD1Text = new javax.swing.JTextField();
        F1Text = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        akc4Text = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        ALD2Text = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        F3Text = new javax.swing.JTextField();
        F4Text = new javax.swing.JTextField();
        akcLabel = new javax.swing.JLabel();
        ALD3Text = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        dzxLabel = new javax.swing.JLabel();
        dzxText = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        backButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        cropSelectedLabel = new javax.swing.JLabel();
        irrigationSelectedLabel = new javax.swing.JLabel();
        infoButton = new javax.swing.JButton();
        dwtEnteredLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel3.setText("For Generic Crops");

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

        dznLabel.setText("Initial irrigated root zone depth");

        dznText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dznTextActionPerformed(evt);
            }
        });

        jLabel19.setText("Inches");

        jLabel15.setText("Stage 1");

        aldLabel.setText("Allowable Soil Water depletions ");

        jLabel17.setText("Stage 3");

        jLabel8.setText("Stage 4");

        FLabel.setText("Fraction of Growing Season for each stage");

        jLabel11.setText("Stage 2");

        akc4Text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                akc4TextActionPerformed(evt);
            }
        });

        jLabel7.setText("Stage 3");

        jLabel16.setText("Stage 2");

        jLabel18.setText("Stage 4");

        akcLabel.setText("Crop water use coefficients for growth stages 3 and 4");

        jLabel13.setText("Stage 4");

        jLabel12.setText("Stage 3");

        jLabel10.setText("Stage 1");

        dzxLabel.setText("Maximum irrigated root zone depth");

        jLabel20.setText("Inches");

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(akcLabel)
                    .addComponent(FLabel)
                    .addComponent(aldLabel)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(ALD1Text, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(ALD2Text))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(akc3Text, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(akc4Text, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(ALD3Text, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(ALD4Text))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(F1Text, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel11))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(F3Text, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel13)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(F4Text, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                    .addComponent(F2Text))))))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(generic)
                        .addGap(45, 45, 45)
                        .addComponent(manual))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(dznText, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel19))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(dzxText, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(dzxLabel)
                    .addComponent(dznLabel)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(backButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(nextButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(generic)
                    .addComponent(manual))
                .addGap(9, 9, 9)
                .addComponent(dznLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dznText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dzxLabel)
                .addGap(1, 1, 1)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(dzxText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(akcLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(akc3Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(akc4Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(FLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(F1Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(F2Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(F3Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(F4Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(aldLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(ALD1Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(ALD2Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(ALD3Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(ALD4Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(backButton)
                    .addComponent(nextButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cropSelectedLabel.setText("jLabel1");

        infoButton.setFont(new java.awt.Font("Yu Gothic Light", 2, 18)); // NOI18N
        infoButton.setText("i");
        infoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoButtonActionPerformed(evt);
            }
        });

        dwtEnteredLabel.setText("jLabel1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 31, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(dwtEnteredLabel)
                            .addComponent(cropSelectedLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
                            .addComponent(irrigationSelectedLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(infoButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(irrigationSelectedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(infoButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cropSelectedLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(dwtEnteredLabel)
                .addGap(20, 20, 20)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void dznTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dznTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dznTextActionPerformed

    private void akc4TextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_akc4TextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_akc4TextActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
        prev.setVisible(true);
    }//GEN-LAST:event_backButtonActionPerformed

    private void manualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manualActionPerformed
        // TODO add your handling code here:
        enableInput(manual.isSelected());
    }//GEN-LAST:event_manualActionPerformed

    private void genericActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genericActionPerformed
        // TODO add your handling code here:
        setDefaultValues();
        enableInput(!generic.isSelected());
    }//GEN-LAST:event_genericActionPerformed

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        // TODO add your handling code here:
        boolean isFailed = false;

        if (generic.isEnabled() && manual.isSelected()) {
            try {
                DZN = Double.parseDouble(dznText.getText());
            } catch (NumberFormatException e) {
                isFailed = true;
                dznLabel.setForeground(Color.red);
            }
            try {
                DZX = Double.parseDouble(dzxText.getText());
            } catch (NumberFormatException e) {
                isFailed = true;
                dzxLabel.setForeground(Color.red);
            }

            if (DZX < DZN) {
                isFailed = true;
                dzxLabel.setForeground(Color.red);
            }

            try {
                AKC3 = Double.parseDouble(akc3Text.getText());
                AKC4 = Double.parseDouble(akc4Text.getText());
                if (ir == AFSIRSUtils.IRSEEP) {
                    if (AKC3 < 1.0 || AKC4 < 1.0) {
                        isFailed = true;
                        akcLabel.setForeground(Color.red);
                    }
                }
            } catch (NumberFormatException e) {
                isFailed = true;
                akcLabel.setForeground(Color.red);
            }

            try {
                double sum = 0.0;
                F[0] = Double.parseDouble(F1Text.getText());
                F[1] = Double.parseDouble(F2Text.getText());
                F[2] = Double.parseDouble(F3Text.getText());
                F[3] = Double.parseDouble(F4Text.getText());
                sum = F[0] + F[1] + F[2] + F[3];
                if (sum != 1.0) {
                    isFailed = true;
                    FLabel.setForeground(Color.red);
                    FLabel.setText(FLabel.getText() + ": Values must sum to 1.0");
                } else {
                    FLabel.setText("Fraction of Growing Season for each stage");
                }
            } catch (NumberFormatException e) {
                isFailed = true;
                FLabel.setForeground(Color.red);
            }

            try {
                ALD[0] = Double.parseDouble(ALD1Text.getText());
                ALD[1] = Double.parseDouble(ALD2Text.getText());
                ALD[2] = Double.parseDouble(ALD3Text.getText());
                ALD[3] = Double.parseDouble(ALD4Text.getText());
            } catch (NumberFormatException e) {
                isFailed = true;
                aldLabel.setForeground(Color.red);
            }
        }

        if (!isFailed) {

            dznLabel.setForeground(Color.black);
            dzxLabel.setForeground(Color.black);
            akcLabel.setForeground(Color.black);
            FLabel.setForeground(Color.black);
            aldLabel.setForeground(Color.black);

            utils.setDCOEFAnnual(DZN, DZX, F, ALD);
            utils.setAKC(AKC3, AKC4);
            new Thread(new Runnable() {

                @Override
                public void run() {
                   utils.finishInput();
                }
            }).run();
            

            this.setVisible(false);
            JFrame finish = new GraphOutput();
        }
    }//GEN-LAST:event_nextButtonActionPerformed

    private void infoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoButtonActionPerformed
        // TODO add your handling code here:
        String str;
        str = "<html><body><p>Select Default for reading values from CROP.dat <br>and manual to enter values from keyboard.</p><p>"
        + "<br>If irrigation type selected is Seepage, then <br>"
        + "Initial root zonee depth = Depth of Water Table<br>Maximum irrigated root zone depth = Depth of Water Table<br>Crop Water Use Co-efficients for both stages >= 1.0<br>and,"
        + "<br>Allowable soil water depletions = 0.0</p>"
        + "<p>The Sum of Fraction of Growing Season for each stage <br> should be 1.0</p></body></html>";

        JOptionPane.showMessageDialog(null, str);
    }//GEN-LAST:event_infoButtonActionPerformed

    /**
     * @param args the command line arguments
     *
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField ALD1Text;
    private javax.swing.JTextField ALD2Text;
    private javax.swing.JTextField ALD3Text;
    private javax.swing.JTextField ALD4Text;
    private javax.swing.JTextField F1Text;
    private javax.swing.JTextField F2Text;
    private javax.swing.JTextField F3Text;
    private javax.swing.JTextField F4Text;
    private javax.swing.JLabel FLabel;
    private javax.swing.JTextField akc3Text;
    private javax.swing.JTextField akc4Text;
    private javax.swing.JLabel akcLabel;
    private javax.swing.JLabel aldLabel;
    private javax.swing.JButton backButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel cropSelectedLabel;
    private javax.swing.JLabel dwtEnteredLabel;
    private javax.swing.JLabel dznLabel;
    private javax.swing.JTextField dznText;
    private javax.swing.JLabel dzxLabel;
    private javax.swing.JTextField dzxText;
    private javax.swing.JRadioButton generic;
    private javax.swing.JButton infoButton;
    private javax.swing.JLabel irrigationSelectedLabel;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton manual;
    private javax.swing.JButton nextButton;
    // End of variables declaration//GEN-END:variables

}
