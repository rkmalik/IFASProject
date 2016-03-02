/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.agmip.ui.afsirs.frames;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import org.agmip.ui.afsirs.util.AFSIRSUtils;
import org.agmip.ui.afsirs.util.Soil;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.agmip.ui.afsirs.util.Messages;
import org.agmip.ui.afsirs.util.SoilData;
 

/**
 *
 * @author rkmalik
 */
public class SWFrame extends javax.swing.JFrame {

    /**
     * Creates new form SWFrame
     */
    private String SNAME;
    private String[] TXT = new String[3];
    private double[] DU = new double[6];
    private double[] DL = new double[6];
    private double[] WCU = new double[6];
    private double[] WCL = new double[6];
    private double[] WC = new double[6];
    private int NL = 0;
    private double DWT = 20.0;
    int ir;
    
    // map, saved location, file, keyboard
    private int [] waterHoldSelectedItemIndex = {1,1,1,1};

    AFSIRSUtils utils;

    JFrame prev = null;
    
    JFrame next = null;

    public SWFrame(JFrame prev) {
        initComponents();
        this.prev = prev;
        setLocation(400, 50);
        utils = AFSIRSUtils.getInstance();
        ir = utils.getIrrigationSystem();
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        soilTable.setDefaultRenderer(Double.class, centerRenderer);
        soilTable.setDefaultRenderer(Integer.class, centerRenderer);
        infoButton.setForeground(Color.blue);
        infoButton.setBorder(LineBorder.createGrayLineBorder());

        JTableHeader tHeader = soilTable.getTableHeader();
        tHeader.setPreferredSize(new Dimension(300, 35));

        wcErrorLabel.setVisible(false);
        errorLabel.setVisible(false);

        init();
        
        if (AFSIRSUtils.defaultMode) {
            dwtText.setText(60.0 + "");
            soilListBox.setSelectedIndex(5);
            waterholdcapacityBox.setSelectedIndex(waterHoldSelectedItemIndex[0]);
        }
        
        getRootPane().setDefaultButton(nextButton);
    }

    public void init() {
        //Read DWT
        irrTypeLabel.setText("Irrigation Type Selected is " + utils.getIrrigationSystemName());
        if (ir == AFSIRSUtils.IRSEEP) {
            dwtText.setText(DWT + "");
        } else {
            DWT = 10.0;
            if (ir == utils.IRNSCY || ir == utils.IRCRFL || ir == utils.IRRICE) {
                dwtText.setText(DWT + "");
            } else {
                dwtText.setText("");
            }
        }

        //soilListBox.addItem("Enter from Keyboard");
        toogleStateOfControls ();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/data/soil.dat")));
            String line;
            br.readLine(); //Ignore first line
            line = br.readLine();
            int start = 0;
            while (line.charAt(start) == ' ') {
                start++;
            }
            int end = start;
            while (line.charAt(end) != ' ') {
                end++;
            }
            br.readLine();//Ignore Line

            int N = Integer.parseInt(line.substring(start, end));
            System.out.println(N);

            for (int i = 0; i < N; i++) {
                line = br.readLine();
                String item = line.substring(4, 24).trim() + "    ";

                String[] parts = line.substring(24).split(" ");
                int k = 0;
                for (String x : parts) {
                    if (x.length() < 1) {
                        continue;
                    }
                    k++;
                    item += x + "    ";
                }
                soilListBox.addItem(item);
                br.readLine();//Ignore next line
            }
            br.close();

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

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        soilListBox = new javax.swing.JComboBox();
        soilNameLabel = new javax.swing.JLabel();
        soilNameText = new javax.swing.JTextField();
        soilTextureLabel = new javax.swing.JLabel();
        soilTextureText = new javax.swing.JTextField();
        NLLabel = new javax.swing.JLabel();
        NLComboBox = new javax.swing.JComboBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        soilTable = new javax.swing.JTable();
        prevButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        waterholdcapacityBox = new javax.swing.JComboBox();
        dwtLabel = new javax.swing.JLabel();
        dwtText = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        showSoilDataButton = new javax.swing.JButton();
        infoButton = new javax.swing.JButton();
        irrTypeLabel = new javax.swing.JLabel();
        wcErrorLabel = new javax.swing.JLabel();
        errorLabel = new javax.swing.JLabel();
        jButtonMap = new javax.swing.JButton();
        jRadioFile = new javax.swing.JRadioButton();
        jRadioMap = new javax.swing.JRadioButton();
        jRadioKeyboard = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jRadioButtonSavedLocation = new javax.swing.JRadioButton();
        jComboBox1 = new javax.swing.JComboBox<>();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Soil Data");

        jLabel1.setText("Soil Type");

        soilListBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                soilListBoxActionPerformed(evt);
            }
        });

        soilNameLabel.setText("Soil Series Name");

        soilNameText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                soilNameTextActionPerformed(evt);
            }
        });

        soilTextureLabel.setText("Soil Texture");

        soilTextureText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                soilTextureTextActionPerformed(evt);
            }
        });

        NLLabel.setText("Number of Soil Layers");

        NLComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6" }));
        NLComboBox.setSelectedIndex(-1);
        NLComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NLComboBoxActionPerformed(evt);
            }
        });

        soilTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Layer", "<html><body>Depth from Soil surface to <br>bottom of layer(Inches)</body><html>", "<html><body>Volumetric Water Content<br>(0.01 - 0.90)</body></html>"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(soilTable);
        if (soilTable.getColumnModel().getColumnCount() > 0) {
            soilTable.getColumnModel().getColumn(0).setResizable(false);
            soilTable.getColumnModel().getColumn(0).setPreferredWidth(2);
            soilTable.getColumnModel().getColumn(1).setResizable(false);
            soilTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        }

        prevButton.setText("Back");
        prevButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevButtonActionPerformed(evt);
            }
        });

        nextButton.setText("Next");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        jLabel5.setText("Water-Hold Capacity");

        waterholdcapacityBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Minimum", "Average", "Maximum" }));
        waterholdcapacityBox.setSelectedIndex(1);
        waterholdcapacityBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waterholdcapacityBoxActionPerformed(evt);
            }
        });

        dwtLabel.setText("Depth of Water Table");

        dwtText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dwtTextActionPerformed(evt);
            }
        });

        jLabel7.setText("Inches");

        showSoilDataButton.setText("Show Soil Data");
        showSoilDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showSoilDataButtonActionPerformed(evt);
            }
        });

        infoButton.setFont(new java.awt.Font("Yu Gothic Light", 2, 18)); // NOI18N
        infoButton.setText("i");
        infoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoButtonActionPerformed(evt);
            }
        });

        irrTypeLabel.setText("jLabel2");

        wcErrorLabel.setForeground(new java.awt.Color(255, 0, 51));
        wcErrorLabel.setText("Enter Water Content value in range 0.01 and 0.90");

        errorLabel.setForeground(new java.awt.Color(255, 0, 0));
        errorLabel.setText("Please enter valid values for all the rows");

        jButtonMap.setText("Browse Map");
        jButtonMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMapActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioFile);
        jRadioFile.setText("Soil File");
        jRadioFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioFileActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioMap);
        jRadioMap.setSelected(true);
        jRadioMap.setText("Map");
        jRadioMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioMapActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioKeyboard);
        jRadioKeyboard.setText("Keyboard");
        jRadioKeyboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioKeyboardActionPerformed(evt);
            }
        });

        jLabel2.setText("Input Data From:");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Soil Layer Characteristics");

        buttonGroup1.add(jRadioButtonSavedLocation);
        jRadioButtonSavedLocation.setText("Saved Location");
        jRadioButtonSavedLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonSavedLocationActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(81, 81, 81)
                                .addComponent(prevButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(nextButton)
                                .addGap(127, 127, 127))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(NLLabel)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 566, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(NLComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(70, 70, 70)
                                        .addComponent(errorLabel)
                                        .addGap(37, 37, 37)
                                        .addComponent(wcErrorLabel))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jRadioMap)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jRadioButtonSavedLocation)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jRadioFile)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jRadioKeyboard)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(soilNameText, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(32, 32, 32)
                                .addComponent(soilTextureText, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(dwtLabel)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(dwtText, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addGap(27, 27, 27)
                                .addComponent(infoButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(irrTypeLabel))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(soilNameLabel)
                                .addGap(153, 153, 153)
                                .addComponent(soilTextureLabel))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jButtonMap)
                                    .addGap(46, 46, 46)
                                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(showSoilDataButton))
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(soilListBox, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel1))
                                    .addGap(176, 176, 176)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(waterholdcapacityBox, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel5)))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 382, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(irrTypeLabel)
                .addGap(18, 18, 18)
                .addComponent(dwtLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dwtText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(infoButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioFile)
                    .addComponent(jRadioMap)
                    .addComponent(jRadioKeyboard)
                    .addComponent(jLabel2)
                    .addComponent(jRadioButtonSavedLocation))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonMap)
                    .addComponent(showSoilDataButton)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(soilListBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(waterholdcapacityBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(soilNameLabel)
                    .addComponent(soilTextureLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(soilTextureText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(soilNameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(NLLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(NLComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(wcErrorLabel)
                    .addComponent(errorLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(prevButton)
                    .addComponent(nextButton))
                .addGap(21, 21, 21))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void NLComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NLComboBoxActionPerformed
        // TODO add your handling code here:
        NL = NLComboBox.getSelectedIndex() + 1;
        DefaultTableModel model = (DefaultTableModel) soilTable.getModel();

        int row = model.getRowCount();
        for (int i = 0; i < row; i++) {
            model.removeRow(0);
        }
        row = 0;
        if (row < NL) {
            while (row < NL) {
                Object[] vec = new Object[3];
                if (jRadioKeyboard.isSelected()) {
                    vec[0] = row + 1;
                    model.addRow(vec);
                    row++;
                    continue;
                }
                vec[0] = row + 1;
                vec[1] = DU[row];
                vec[2] = WC[row];
                model.addRow(vec);
                //model.addRow(new Vector(DU[row], WC[row]));
                row++;
            }
        }
    }//GEN-LAST:event_NLComboBoxActionPerformed

    private void soilListBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_soilListBoxActionPerformed
        // TODO add your handling code here:
        wcErrorLabel.setVisible(false);
        errorLabel.setVisible(false);
        
        if(soilTable.isEditing()){
            soilTable.getCellEditor().stopCellEditing();
        }
        int index = soilListBox.getSelectedIndex();

        if (jRadioFile.isSelected()) {
            try {
                readFromFile(soilListBox.getSelectedIndex());
                soilNameText.setText(SNAME);
                soilTextureText.setText(TXT[0]);
                NLComboBox.setSelectedIndex(NL - 1);
                waterholdcapacityBox.setSelectedIndex(waterHoldSelectedItemIndex[2]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_soilListBoxActionPerformed

    
    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        //Validation of data
        
        // Check if the data is to be read from the file or from the table.
        // Avoid this patching and rewrite the code after imp
        // Segregate both the logics/ Map Handline and File+Keyboard handling.
        if (jRadioMap.isSelected()|| jRadioButtonSavedLocation.isSelected()) {
            SoilData soilData;
            File latestFile;
            try {
                if (jRadioMap.isSelected()) {
                    latestFile = findLatestFile(0);
                }  else {
                    latestFile = findLatestFile (jComboBox1.getSelectedIndex());
                }
                soilData = readSoilDataFromJSONMap (latestFile);
            } catch (FileNotFoundException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                    return;
            }
            utils.setSoilData(soilData);
            
        } else {
            
            if(soilTable.isEditing()){
                soilTable.getCellEditor().stopCellEditing();
            }

            wcErrorLabel.setVisible(false);
            errorLabel.setVisible(false);
            soilNameLabel.setForeground(Color.black);
            soilTextureLabel.setForeground(Color.black);
            dwtLabel.setForeground(Color.black);

            boolean isFailed = false;
            int ISOIL = soilListBox.getSelectedIndex();
            if (ISOIL == 0) {
                if (soilNameText.getText().length() < 1) {
                    isFailed = true;
                    soilNameLabel.setForeground(Color.red);
                }

                if (soilTextureText.getText().length() < 1) {
                    isFailed = true;
                    soilTextureLabel.setForeground(Color.red);
                }

                DefaultTableModel model = (DefaultTableModel) soilTable.getModel();
                int nl = NLComboBox.getSelectedIndex() + 1;
                if (nl != model.getRowCount()) {
                    isFailed = true;
                }
            }

            if (dwtText.isEnabled()) {
                try {
                    DWT = Double.parseDouble(dwtText.getText());
                } catch (NumberFormatException e) {
                    dwtLabel.setForeground(Color.red);
                    isFailed = true;
                }
                if (DWT < 0.01) {
                    dwtLabel.setForeground(Color.red);
                    isFailed = true;
                }
            }

            System.out.println("DWT ="  + DWT);
            //@rohit_note: Data needs to be taken based on the selection from UI Map/File/Keyboard. For eg. If this is file/Keyboard
            // Then Take the data from the controls/ else if it is Map then take the data from the json File. 
            SNAME = soilNameText.getText();
            TXT[0] = soilTextureText.getText();
            DefaultTableModel model = (DefaultTableModel) soilTable.getModel();
            try {
                for (int i = 0; i < NL; i++) {
                    DU[i] = (Double) model.getValueAt(i, 1);
                    WC[i] = (Double) model.getValueAt(i, 2);
                    if (WC[i] < 0.01 || WC[i] > 0.9) {
                        isFailed = true;
                        wcErrorLabel.setVisible(true);
                    }
                }
            } catch (Exception e) {
                isFailed = true;
                errorLabel.setVisible(true);
            }

            if (isFailed) {
                return;
            }

            //Define Lower Soil Layer Dimensions
            utils.setDWT(DWT);
            Soil soil = new Soil(ISOIL, SNAME, NL);
            soil.setValues(WC, WCL, WCU, DU, TXT);
            //soil.setSoilTypeArea(area);

            SoilData soilData = new SoilData ();
            soilData.addSoil(soil);
            utils.setSoilData(soilData);
            
        }
                
        setVisible(false);
        if (next != null) {
            next.setVisible(true);
        } else {
            if (utils.getPerennial()) {
                DCOEFPerennialFrame nextFrame = new DCOEFPerennialFrame(this);
                nextFrame.setVisible(true);
                next = nextFrame;
            } else {
                DCOEFAnnualFrame nextFrame = new DCOEFAnnualFrame(this);
                nextFrame.setVisible(true);
                next = nextFrame;
            }
        }

        //finish.setVisible(true);
    }//GEN-LAST:event_nextButtonActionPerformed

    private void prevButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevButtonActionPerformed
        // TODO add your handling code here:
        prev.setVisible(true);
        setVisible(false);
    }//GEN-LAST:event_prevButtonActionPerformed

    private void waterholdcapacityBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waterholdcapacityBoxActionPerformed
        // TODO add your handling code here:
        int whc = waterholdcapacityBox.getSelectedIndex();
        boolean isMapSelected = jRadioMap.isSelected();        
        boolean isFileLocationSelected = jRadioButtonSavedLocation.isSelected();
        boolean isFileSelected = jRadioFile.isSelected();
        boolean isKeyboardEnabled = jRadioKeyboard.isSelected();
        
        if (isMapSelected)
            waterHoldSelectedItemIndex[0] = whc;
        else if (isFileLocationSelected)
            waterHoldSelectedItemIndex[1] = whc;
        else if (isFileSelected)
            waterHoldSelectedItemIndex[2] = whc;
        else 
            waterHoldSelectedItemIndex[3] = whc;
        
        
        DefaultTableModel model = (DefaultTableModel) soilTable.getModel();
        for (int i = 0; i < NL; i++) {
            if (whc == 0) {
                WC[i] = WCL[i];
            } else if (whc == 2) {
                WC[i] = WCU[i];
            } else {
                WC[i] = 0.5 * (WCL[i] + WCU[i]);
            }
            model.setValueAt(WC[i], i, 2);
        }


    }//GEN-LAST:event_waterholdcapacityBoxActionPerformed

    private void showSoilDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showSoilDataButtonActionPerformed
        // TODO add your handling code here:
        
        // There are two paths of this 1. if the Map is enabled 
        boolean isMapEnabled = jRadioMap.isSelected();
        boolean isFileEnabled = jRadioFile.isSelected();
        boolean isFileLocationEnabled = jRadioButtonSavedLocation.isSelected();
        String str = "";
            
        if (isMapEnabled || isFileLocationEnabled) {
            
           
            SoilData soilData;
            File latestFile;
            try {
                if (jRadioMap.isSelected()) {
                    latestFile = findLatestFile(0);
                }  else {
                    latestFile = findLatestFile (jComboBox1.getSelectedIndex());
                }
                str = readFromMapJson(latestFile);
                //soilData = readSoilDataFromJSONMap (latestFile);
            } catch (FileNotFoundException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                    return;
            } catch (IOException e) {
                e.printStackTrace();
            } 
        }  else if (isFileEnabled) {
            str += "Soil Name = " + SNAME + "\n";
            str += "Texture = " + TXT[0] + "\n";
            str += "Number of Layers = " + NL + "\n";
            str += "    DU        WCL        WCU\n";
            for (int i = 0; i < NL; i++) {
                str += (i + 1) + "  " + DU[i] + "       " + WCL[i] + "       " + WCU[i] + "\n";
            }
        }
        JOptionPane.showMessageDialog(null, str);            
        
    }//GEN-LAST:event_showSoilDataButtonActionPerformed

    private void infoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoButtonActionPerformed
        // TODO add your handling code here:
        String str;
        str = "Default Depth of Water Table is as below (in inches) :\n"
                + "If Irrigation Type is,\n"
                + "Container Nursery Irrigation, 10.0\n"
                + "Crown Flood Irrigation, 10.0\n"
                + "Rice Flood Irrigation, 10.0\n"
                + " Seepage Irrigation, 20.0 \n"
                + "Otherwise, take input from User";

        JOptionPane.showMessageDialog(null, str);
    }//GEN-LAST:event_infoButtonActionPerformed

    private void dwtTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dwtTextActionPerformed
        // TODO add your handling code here:
        nextButtonActionPerformed(evt);
    }//GEN-LAST:event_dwtTextActionPerformed

    private void soilNameTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_soilNameTextActionPerformed
        // TODO add your handling code here:
        nextButtonActionPerformed(evt);
    }//GEN-LAST:event_soilNameTextActionPerformed

    private void soilTextureTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_soilTextureTextActionPerformed
        // TODO add your handling code here:
        nextButtonActionPerformed(evt);
    }//GEN-LAST:event_soilTextureTextActionPerformed

    private void jButtonMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMapActionPerformed
        // TODO add your handling code here:
    try {
        
        String siteName = utils.getOutFile();
        siteName = siteName.replaceFirst(".txt", "");
        System.out.println (siteName);
        Desktop.getDesktop().browse(new URL("http://abe.ufl.edu/bmpmodel/Shivam/v3_shivam/index.html?site="+siteName+"#").toURI());
    } catch (Exception e) {
        e.printStackTrace();
    }
        
    }//GEN-LAST:event_jButtonMapActionPerformed

    private void jRadioMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioMapActionPerformed
        // TODO add your handling code here:
        toogleStateOfControls ();        
    }//GEN-LAST:event_jRadioMapActionPerformed

    private void jRadioKeyboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioKeyboardActionPerformed
        // TODO add your handling code here:
        toogleStateOfControls ();
    }//GEN-LAST:event_jRadioKeyboardActionPerformed

    private void jRadioFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioFileActionPerformed
        // TODO add your handling code here:
        toogleStateOfControls ();
        if (jRadioFile.isSelected()) {
            
            try {
                readFromFile(soilListBox.getSelectedIndex());
                soilNameText.setText(SNAME);
                soilTextureText.setText(TXT[0]);
                NLComboBox.setSelectedIndex(NL - 1);
                waterholdcapacityBox.setSelectedIndex(waterHoldSelectedItemIndex[2]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jRadioFileActionPerformed

    private void jRadioButtonSavedLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonSavedLocationActionPerformed
        // TODO add your handling code here:
        toogleStateOfControls (); 
    }//GEN-LAST:event_jRadioButtonSavedLocationActionPerformed

    private void toogleStateOfControls () {

        boolean isMapSelected = jRadioMap.isSelected();        
        boolean isFileLocationSelected = jRadioButtonSavedLocation.isSelected();
        boolean isFileSelected = jRadioFile.isSelected();
        boolean isKeyboardEnabled = jRadioKeyboard.isSelected();

        jButtonMap.setEnabled(isMapSelected||(isKeyboardEnabled&&isFileSelected));
        jComboBox1.setEnabled(isFileLocationSelected);

        showSoilDataButton.setEnabled(isMapSelected||isFileSelected||isFileLocationSelected);

        soilListBox.setEnabled(isFileSelected);
        waterholdcapacityBox.setEnabled(isFileSelected||isMapSelected||isFileLocationSelected);
        int index =  0;

        // switch ()
                
        
        soilNameText.setEnabled(isKeyboardEnabled);
        soilTextureText.setEnabled(isKeyboardEnabled);
        NLComboBox.setEnabled(isKeyboardEnabled);
        soilTable.setEnabled(isKeyboardEnabled);
        
        while (jComboBox1.getItemCount()>0) {
            jComboBox1.removeAllItems();
        }
        
        
        if (isMapSelected) {
             index = 0;
        } else if (isFileLocationSelected) {
            index = 1;
        } else if (isFileSelected) {
            index = 2;
        } else {
            index = 3;
        }
        
         waterholdcapacityBox.setSelectedIndex(waterHoldSelectedItemIndex[index]);
        
        if (isFileLocationSelected) {
            File [] files = getListOfDataFiles();
            for (File f : files) {
                jComboBox1.addItem(f.getName());
            }
        }
            
        if (!isFileSelected) {
            soilNameText.setText("");
            soilTextureText.setText("");
            NLComboBox.setSelectedIndex(-1);
           
            DefaultTableModel model = (DefaultTableModel) soilTable.getModel();
            int row = model.getRowCount();
            for (int i = 0; i < row; i++) {
                model.removeRow(0);
            }
            row = 0;

        } 
    }
    
    
    public File[] getListOfDataFiles () {
        

        String home = System.getProperty("user.home");
        
        File dir = new File(home+"/Downloads"); 
        
        String siteName = utils.getOutFile().replaceFirst(".txt", "");
        String regEx = siteName+"*." + "json";
        System.out.println (siteName);
        FileFilter fileFilter = new WildcardFileFilter(siteName+"*.*");
        File[] files = dir.listFiles(fileFilter);
        
        if (files.length > 0) {
            Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
        }
        
        return files;
    }


    
    private File findLatestFile (int index) throws FileNotFoundException{
        
        // Get the latest file form the download folder.
        String home = System.getProperty("user.home");
        File dir = new File(home+"/Downloads"); 
        File theNewestFile = null;
        //File dir = new File(filePath);
        FileFilter fileFilter = new WildcardFileFilter("*." + "json");
        File[] files = dir.listFiles(fileFilter);
        
        if (files.length > 0) {
            Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            theNewestFile = files[index];
        } else {
            throw new FileNotFoundException(Messages.FILE_NOT_FOUND_MESSAGE);
        }
        return theNewestFile;
    }
    
    
    private SoilData readSoilDataFromJSONMap (File latestFile) throws FileNotFoundException{
        
        SoilData soilData = new SoilData ();    
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        str += "Data Date: " + sdf.format(latestFile.lastModified()) + "\n";
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        FileReader fr = null;
        
        try 
        {
            fr = new FileReader(latestFile);
            root = mapper.readTree(fr);
                        
        } catch (FileNotFoundException e) {
            //System.out.println (e.getMessage());
            throw new FileNotFoundException(Messages.FILE_NOT_FOUND_MESSAGE);
            
        } catch (IOException e) {
            e.printStackTrace();
            
        }finally {
            if (fr!=null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace ();
                }
            }
        }
        JsonNode soils = root.path("soils");
        
        int row = 0;
        
        int whcIndex = waterholdcapacityBox.getSelectedIndex();
        
        for (JsonNode n : soils) {
            String soilName = n.path("soilName").textValue();
            String soilTypeArea = n.path("soilArea").textValue();
            JsonNode soilLayersNodes = n.path("soilLayer");
            
            int NL = 0;

            double[] wc = new double [6];
            double[] wcl= new double [6];
            double[] wcu= new double [6];
            double[] du= new double [6];
            String[] txt  = new String[3];
            
            for (JsonNode node : soilLayersNodes) {
                wcu[NL] = node.get("sldul").asDouble()/100.00;
                //wcu[NL] =  Math.round(wcu[NL] * 1000) / 1000;
                du[NL] = node.get("sllb").asDouble()*0.39370;
                
                wcl[NL] = node.get("slll").asDouble()/100.00;
                //wcl[NL] =  Math.round(wcl[NL] * 1000) / 1000;
                
                if (whcIndex == 0) {
                    wc[NL] = wcl[NL];
                } else if (whcIndex == 2) {
                    wc[NL] = wcu[NL];
                } else {
                    wc[NL] = 0.5 * (wcl[NL] + wcu[NL]);
                }
                NL++;
            }
        
            Soil soil = new Soil (row, soilName, NL);
            soil.setValues(wc, wcl, wcu, du, txt);
            soil.setSoilTypeArea(Double.valueOf(soilTypeArea));
            soilData.getSoils().add(soil);
            row++;
        }
        
        return soilData;
    }    
    
    public String readFromMapJson (File theNewestFile) throws IOException {

        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        str += "Data Date: " + sdf.format(theNewestFile.lastModified()) + "\n";
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(theNewestFile);
        JsonNode soils = root.path("soils");
        DefaultTableModel model = (DefaultTableModel) soilTable.getModel();
        int row = model.getRowCount();
        for (int i = 0; i < row; i++) {
            model.removeRow(0);
        }
        row = 0;
        
        String fileName = theNewestFile.getName();
        str+= "Data File Name: " + fileName+"\n";
        for (JsonNode n : soils) {
            
            //Soil soil = new Soil ();
            
            String soilName = n.path("soilName").textValue();
            String soilTypeArea = n.path("soilArea").textValue();
            
            str += "Soil Name = " + soilName + "\n";
            str += "Soil Area= " + soilTypeArea + " (acres)\n";
            
            int numberOfLayers = 0;
            //String layerDetails = "";
            
            String wcu = "";
            String du = "";
            String wcl = "";
            String strTmp = "";
            
            JsonNode soilLayersNodes = n.path("soilLayer");

            String formatter = "%2s%-15s%-15s%-15s\n";
            for (JsonNode node : soilLayersNodes) {
                double slll_wcl= node.get("slll").asDouble()/100.00;
                wcl = String.format("%.3f", slll_wcl);
                double sllb_du= node.get("sllb").asDouble()*0.39370;
                du = String.format("%.3f", sllb_du);
                double sldul_wcu = node.get("sldul").asDouble()/100.00;

                wcu = String.format("%.3f", sldul_wcu);
                System.out.println ("Rohit: " + slll_wcl + " " + sldul_wcu);
                // strTmp +=  String.format ("%-5d%-15s%-15s%-15s\n", ++numberOfLayers, du, wcl, wcu);
                strTmp +=  String.format ("%-5d%-25.3f%-25.3f%-25.3f\n", ++numberOfLayers, sllb_du, slll_wcl,  sldul_wcu);
            }
            
            str += "Number of Layers = " + numberOfLayers + "\n";
            str+= String.format ("%-5s%-15s%-15s%-15s\n", "LN", "DU(inches)", "WCL", "WCU\n");
            //str += "LN   DU(inches)   WCL(inches)   WCU(inches)\n";
            str+= strTmp;
            str+= "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" ;

        }
        
        return str;
    }
    public void readFromFile(int code) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/data/soil.dat")));
        br.readLine();
        br.readLine();
        br.readLine();
        int i = 1;
        while (i < code) {
            br.readLine();
            br.readLine();
            i++;
        }
        br.readLine();
        String line1 = soilListBox.getSelectedItem().toString();
        String line2 = br.readLine();

        String[] parts = line1.split("    ");
        SNAME = parts[0];
        i = 1;
        while (i < parts.length) {
            TXT[i - 1] = parts[i];
            i++;
        }

        NL = Integer.parseInt(line2.substring(0, 1));
        i = 0;
        int j = 1;
        while (line2.charAt(j) == ' ') {
            j++;
        }
        line2 = line2.substring(j);
        parts = line2.split(" ");
        while (i < NL) {
            String[] fields = parts[i].split("[.]");
            DU[i] = Double.parseDouble(fields[0]);
            WCL[i] = Double.parseDouble(fields[1]) / 100.0;
            WCU[i] = Double.parseDouble(fields[2]) / 100.0;
            
            WC[i] =  Math.round(WCL[i] * 100) / 100;
            //WC[i] = WCL[i];
            i++;
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox NLComboBox;
    private javax.swing.JLabel NLLabel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel dwtLabel;
    private javax.swing.JTextField dwtText;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JButton infoButton;
    private javax.swing.JLabel irrTypeLabel;
    private javax.swing.JButton jButtonMap;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JRadioButton jRadioButtonSavedLocation;
    private javax.swing.JRadioButton jRadioFile;
    private javax.swing.JRadioButton jRadioKeyboard;
    private javax.swing.JRadioButton jRadioMap;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton prevButton;
    private javax.swing.JButton showSoilDataButton;
    private javax.swing.JComboBox soilListBox;
    private javax.swing.JLabel soilNameLabel;
    private javax.swing.JTextField soilNameText;
    private javax.swing.JTable soilTable;
    private javax.swing.JLabel soilTextureLabel;
    private javax.swing.JTextField soilTextureText;
    private javax.swing.JComboBox waterholdcapacityBox;
    private javax.swing.JLabel wcErrorLabel;
    // End of variables declaration//GEN-END:variables
}
