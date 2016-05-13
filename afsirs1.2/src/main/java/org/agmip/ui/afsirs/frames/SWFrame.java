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

import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.Level;

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
import java.awt.event.ItemEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import static org.agmip.ui.afsirs.util.FrameTracker.soilDataNext;
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
    private String SOILSERIESNAME;
    private String[] TXT = new String[3];
    private double[] DU = new double[6];
    private double[] DL = new double[6];
    private double[] WCU = new double[6];
    private double[] WCL = new double[6];
    private double[] WC = new double[6];
    private int NL = 0;
    private double DWT = 20.0;
    private int ir;
    private File [] files = null;
    
    // map, saved location, file, keyboard
    private int [] waterHoldSelectedItemIndex = {1,1,1};
    private boolean isInitializing = false;
    private boolean prevButtonSelectedSavedLocation = false;
 
    //public static final Logger LOGGER = Logger.getLogger(SWFrame.class.class.getName());
    
    
    private Soil prevSoil = null;
    private Boolean isPreviousSelectionSavedLocation = false;

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
    
    /*private void initLogger() {
        Handler fileHandler = null;
        try {
            String logdir = curdirpath + dirseprator + "log";
            System.out.println ("Logging in the following folder : " + logdir);
            new File(logdir).mkdir();
            fileHandler = new FileHandler(logdir + dirseprator + "dssat.log");
            datadir = curdirpath + dirseprator + "data";
            new File(datadir).mkdir();
            LOGGER.addHandler(fileHandler);
            fileHandler.setLevel(Level.ALL);
            LOGGER.setLevel(Level.ALL);
            LOGGER.log(Level.ALL, "Initializing all the Components of the Application....");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

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
        toogleStateOfControls (null);
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
        jRadioKeyboard = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jRadioButtonSavedLocation = new javax.swing.JRadioButton();
        soilFileListCombo = new javax.swing.JComboBox<>();
        soilNameCombo = new javax.swing.JComboBox<>();
        jButtonRefresh = new javax.swing.JButton();

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
        jRadioFile.setSelected(true);
        jRadioFile.setText("Soil File");
        jRadioFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioFileActionPerformed(evt);
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

        soilFileListCombo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                soilFileListComboMouseClicked(evt);
            }
        });
        soilFileListCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                soilFileListComboActionPerformed(evt);
            }
        });

        soilNameCombo.setEnabled(false);
        soilNameCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                soilNameComboItemStateChanged(evt);
            }
        });
        soilNameCombo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                soilNameComboMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                soilNameComboMouseReleased(evt);
            }
        });
        soilNameCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                soilNameComboActionPerformed(evt);
            }
        });

        jButtonRefresh.setText("Refresh");
        jButtonRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 382, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addContainerGap())
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
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(NLComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(70, 70, 70)
                                        .addComponent(errorLabel)
                                        .addGap(37, 37, 37)
                                        .addComponent(wcErrorLabel))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(54, 54, 54)
                                        .addComponent(jRadioFile)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jRadioButtonSavedLocation)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jRadioKeyboard)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                                .addGap(365, 365, 365)
                                .addComponent(soilTextureLabel))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jButtonMap)
                                        .addGap(46, 46, 46)
                                        .addComponent(soilFileListCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(showSoilDataButton))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(soilListBox, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel1))
                                        .addGap(176, 176, 176)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(waterholdcapacityBox, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel5))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButtonRefresh)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(soilNameText, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(soilNameCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(soilTextureText, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36))))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioFile)
                    .addComponent(jRadioKeyboard)
                    .addComponent(jLabel2)
                    .addComponent(jRadioButtonSavedLocation))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonMap)
                    .addComponent(showSoilDataButton)
                    .addComponent(soilFileListCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonRefresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(soilListBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(waterholdcapacityBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(soilTextureLabel)
                    .addComponent(soilNameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(soilTextureText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(soilNameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(soilNameCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(49, 49, 49)
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
                .addContainerGap())
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
                vec[1] = Math.floor(DU[row] * 1000) / 1000;
                vec[2] = Math.floor(WC[row] * 1000) / 1000;
                
                model.addRow(vec);
                
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
                waterholdcapacityBox.setSelectedIndex(waterHoldSelectedItemIndex[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_soilListBoxActionPerformed

    
    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
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
        if (jRadioKeyboard.isSelected()) {
            
            if (soilNameText.getText().length() < 1) {
            //if (soilNameCombo.getItemAt(0).length() < 1) {
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

        //@rohit_note: Data needs to be taken based on the selection from UI Map/File/Keyboard. For eg. If this is file/Keyboard
        // Then Take the data from the controls/ else if it is Map then take the data from the json File. 
        SNAME = soilNameText.getText();
        TXT[0] = soilTextureText.getText();
        DefaultTableModel model = (DefaultTableModel) soilTable.getModel();
        SoilData soilData = utils.getSoilData();
        
        if (soilData == null) {
            soilData = new SoilData ();
            utils.setSoilData(soilData);
        }
        
        try {
            
            for (int i = 0; i < NL; i++) {
                DU[i] = (Double) model.getValueAt(i, 1);
                WC[i] = (Double) model.getValueAt(i, 2);
                
                if (jRadioButtonSavedLocation.isSelected()) {
                    ArrayList<Soil> soilList = soilData.getSoilsFromFile((String)soilFileListCombo.getSelectedItem());
                    soilData.setKey((String)soilFileListCombo.getSelectedItem());
                    if (soilList!=null) {
                        Soil soil = soilList.get(soilNameCombo.getSelectedIndex());
                        soil.getDU()[i]=DU[i];
                        soil.getWC()[i]=WC[i];  
                        utils.setDefaultSoil(soil);
                    }             
                } else if (WC[i] < 0.01 || WC[i] > 0.9) {
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
        
        if (jRadioKeyboard.isSelected() || jRadioFile.isSelected()) {
            // The Soil name will be Empty
            Soil soil = new Soil(ISOIL, SNAME, SNAME, NL);
            soil.setValues(WC, WCL, WCU, DU, TXT);
            soilData.addSoil(jRadioKeyboard.isSelected()? Messages.KEYBOARD_KEY : Messages.FILE_KEY, soil);
            utils.setSoilData(soilData);
            utils.setDefaultSoil(soil);
            
        }
        
        //Define Lower Soil Layer Dimensions
        utils.setDWT(DWT);
        
                
        setVisible(false);
        if (soilDataNext != null) {
            soilDataNext.setVisible(true);
        } else {
            if (utils.getPerennial()) {
                DCOEFPerennialFrame nextFrame = new DCOEFPerennialFrame(this);
                nextFrame.setVisible(true);
                soilDataNext = nextFrame;
            } else {
                DCOEFAnnualFrame nextFrame = new DCOEFAnnualFrame(this);
                nextFrame.setVisible(true);
                soilDataNext = nextFrame;
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
        if (isInitializing) return;
        
        int whc = waterholdcapacityBox.getSelectedIndex();
        boolean isFileLocationSelected = jRadioButtonSavedLocation.isSelected();
        boolean isFileSelected = jRadioFile.isSelected();
        boolean isKeyboardEnabled = jRadioKeyboard.isSelected();
        
        if (isFileSelected)
            waterHoldSelectedItemIndex[0] = whc;
        else if (isFileLocationSelected)
            waterHoldSelectedItemIndex[1] = whc;
        else 
            waterHoldSelectedItemIndex[2] = whc;
        
        
        DefaultTableModel model = (DefaultTableModel) soilTable.getModel();
        
        for (int i = 0; i < NL; i++) {
            if (whc == 0) {
                WC[i] = WCL[i];
            } else if (whc == 2) {
                WC[i] = WCU[i];
            } else {
                WC[i] = 0.5 * (WCL[i] + WCU[i]);
                WC[i] = Math.floor(WC[i] * 1000) / 1000;
            }
            model.setValueAt(WC[i], i, 2);
        }

    }//GEN-LAST:event_waterholdcapacityBoxActionPerformed

    private void showSoilDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showSoilDataButtonActionPerformed
        // TODO add your handling code here:

        boolean isFileEnabled = jRadioFile.isSelected();
        boolean isFileLocationEnabled = jRadioButtonSavedLocation.isSelected();
        String str = "";
            
        if (isFileLocationEnabled) {
            SoilData soilData;
            File latestFile;
            try {
                latestFile = findLatestFile (soilFileListCombo.getSelectedIndex());
                str = readFromJsonFileForSoilDetailsButton(latestFile);

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
        
        JTextArea textArea = new JTextArea(str);
        JScrollPane scrollPane = new JScrollPane(textArea);  
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true); 
        scrollPane.setPreferredSize( new Dimension( 500, 300 ) );
        JOptionPane.showMessageDialog(null, scrollPane, "Soil Info",  
                                       JOptionPane.NO_OPTION);
        
        
        
        //JOptionPane.showMessageDialog(null, str);            
        
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
       
        String siteName = utils.getSITE();
        //siteName = siteName.replaceFirst(".txt", "");
        Desktop.getDesktop().browse(new URL("http://abe.ufl.edu/bmpmodel/Shivam/v3_shivam/index.html?site="+siteName+"&unit="+utils.getUNIT()+"#").toURI());
    } catch (Exception e) {
        e.printStackTrace();
    }
        
    }//GEN-LAST:event_jButtonMapActionPerformed

    private void jRadioKeyboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioKeyboardActionPerformed
        // TODO add your handling code here:
        if (isPreviousSelectionSavedLocation) {
            DefaultTableModel model = (DefaultTableModel) soilTable.getModel();

            if (prevSoil!=null) {
                for (int i = 0; i < model.getRowCount(); i++) {
                    prevSoil.getDU()[i] = (Double) model.getValueAt(i, 1);
                    prevSoil.getWC()[i] = (Double) model.getValueAt(i, 2);
                }
            }
            prevSoil = null;
            isPreviousSelectionSavedLocation = false;
        }
        toogleStateOfControls (evt);
    }//GEN-LAST:event_jRadioKeyboardActionPerformed

    private void jRadioFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioFileActionPerformed
        // TODO add your handling code here:
        
        if (isPreviousSelectionSavedLocation) {
            DefaultTableModel model = (DefaultTableModel) soilTable.getModel();

            if (prevSoil!=null) {
                for (int i = 0; i < model.getRowCount(); i++) {
                    prevSoil.getDU()[i] = (Double) model.getValueAt(i, 1);
                    prevSoil.getWC()[i] = (Double) model.getValueAt(i, 2);
                }
            }
            prevSoil = null;
            isPreviousSelectionSavedLocation = false;
        }
        
        toogleStateOfControls (evt);
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
        toogleStateOfControls (evt); 
        isPreviousSelectionSavedLocation = true;
    }//GEN-LAST:event_jRadioButtonSavedLocationActionPerformed

    private void soilNameComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_soilNameComboActionPerformed
        
        // TODO add your handling code here:
        if (isInitializing) return;
        
        int index = soilNameCombo.getSelectedIndex();
        if (index==-1) return;
        //index = index ==-1? 0 : index;
        SoilData soilData = utils.getSoilData();
        String key = (String)soilFileListCombo.getSelectedItem();
        key = key.trim();
        ArrayList<Soil> soilList = soilData.getSoilsFromFile(key);
            
        if (soilList.size()>0) {
            
            if (index == -1) {
                
                // TODO add your handling code here:
                DefaultTableModel model = (DefaultTableModel) soilTable.getModel();
                int row = model.getRowCount();
                for (int i = 0; i < row; i++) {
                    model.removeRow(0);
                }
                index = 0;
                Soil soil = soilList.get(index);
                for (int i = 0; i < soil.getNL(); i++) {
                    Object[] vec = new Object[3];
                    vec[0] = i + 1;
                    vec[1] = soil.getDU()[i];
                    
                    vec[2] = soil.getWC()[i];
                    model.addRow(vec);
                    
                }
            } else {
                
                // This is to take the backup of the data so that I can 
                
                
                //Soil soil = soilList.get(selectedSoilIndex);
                DefaultTableModel model = (DefaultTableModel) soilTable.getModel();

                
                if (prevSoil!=null) {
                    for (int i = 0; i < model.getRowCount(); i++) {
                        prevSoil.getDU()[i] = (Double) model.getValueAt(i, 1);
                        prevSoil.getWC()[i] = (Double) model.getValueAt(i, 2);
                    }
                }
                
                
                int row = model.getRowCount();
                for (int i = 0; i < row; i++) {
                    model.removeRow(0);
                }
                
                Soil soil = soilList.get(index);
                
                DU = soil.getDU();
                WCL = soil.getWCL();
                WCU = soil.getWCU();
                WC = soil.getWC ();
                NL = soil.getNL();
                SNAME = soil.getName();
                utils.setDefaultSoil(soil);
                
                for (int i = 0; i < soil.getNL(); i++) {
                    Object[] vec = new Object[3];
                    vec[0] = i + 1;
                    vec[1] = soil.getDU()[i];
                    vec[2] = soil.getWC()[i];
                    model.addRow(vec);
                    
                }
                
                prevSoil = soil;                
            }
            
        }
        //selectedSoilIndex = index;
    }//GEN-LAST:event_soilNameComboActionPerformed

    private void updateSoilDataBaseOnSoilFileSelection (int index) {
        try {
            File [] files = getListOfDataFiles ();
            System.out.println ("File Index to Found : " + index);
            System.out.println ("Total Number of Files : " + files.length);
            readSoilDataJsonFileForUtils(files[index]);
            //utils.setSoilData(SoilData.getSoilDataInstance());
	} catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
	}
    }
    
    private void updateSoilInfoAndSoilLayerInfo(int index) {
        
       
        // Update the data in the table base on the index 0
        SoilData soilData = utils.getSoilData();
        String key = (String)soilFileListCombo.getItemAt(index);
        if (key==null) {return ;}
        
        soilNameCombo.removeAllItems();
        
        ArrayList<Soil> soilList = soilData.getSoilsFromFile(key);
        if (soilList.size()>0) {
            
            for (Soil soil : soilList) {
                soilNameCombo.addItem(soil.getName());
            }

            DefaultTableModel model = (DefaultTableModel) soilTable.getModel();
            int row = model.getRowCount();
            for (int i = 0; i < row; i++) {
                model.removeRow(0);
                
            }
            
            int soilIndex = soilNameCombo.getSelectedIndex();
            Soil soil = soilList.get(soilIndex==-1?0:soilIndex);
            DU = soil.getDU();
            WCL = soil.getWCL();
            WCU = soil.getWCU();
            WC = soil.getWC ();
            NL = soil.getNL();
            SNAME = soil.getName();
            utils.setDefaultSoil(soil);
            
            for (int i = 0; i < soil.getNL(); i++) {
                Object[] vec = new Object[3];
                vec[0] = i + 1;
                vec[1] = soil.getDU()[i];
                vec[2] = soil.getWC()[i];
                model.addRow(vec);
            }
        }
    }

    
    private void soilFileListComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_soilFileListComboActionPerformed
        // TODO add your handling code here:
        if (isInitializing) return;
        
  
        
        int index  = soilFileListCombo.getSelectedIndex();
        index = index ==-1? 0 : index;
        //prevSoil = 0;
        
        // Read this file and update the Reference accordingly in the utils.
        // Get the file of the selected index and update the utils

        updateSoilInfoAndSoilLayerInfo (index);
    }//GEN-LAST:event_soilFileListComboActionPerformed

    private void soilFileListComboMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_soilFileListComboMouseClicked
        
    }//GEN-LAST:event_soilFileListComboMouseClicked

    private void soilNameComboMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_soilNameComboMouseClicked


    }//GEN-LAST:event_soilNameComboMouseClicked

    private void soilNameComboMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_soilNameComboMouseReleased
        
    }//GEN-LAST:event_soilNameComboMouseReleased

    private void soilNameComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_soilNameComboItemStateChanged

    }//GEN-LAST:event_soilNameComboItemStateChanged

    private void jButtonRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRefreshActionPerformed
        // TODO add your handling code here:
        utils.setSoilData(null);
        files = null;
        toogleStateOfControls (evt);
               
        
    }//GEN-LAST:event_jButtonRefreshActionPerformed

    private void toogleStateOfControls (java.awt.event.ActionEvent evt) {
       
        isInitializing = true;
        boolean isFileLocationSelected = jRadioButtonSavedLocation.isSelected();
        boolean isFileSelected = jRadioFile.isSelected();
        boolean isKeyboardEnabled = jRadioKeyboard.isSelected();
        
        jButtonMap.setEnabled(isFileLocationSelected);
        soilFileListCombo.setEnabled(isFileLocationSelected);
        showSoilDataButton.setEnabled(isFileSelected||isFileLocationSelected);
        soilListBox.setEnabled(isFileSelected);
        waterholdcapacityBox.setEnabled(!isKeyboardEnabled);

        soilNameCombo.setEnabled(isFileLocationSelected);
        soilNameText.setEnabled(isKeyboardEnabled);
        soilTextureText.setEnabled(isKeyboardEnabled);
        NLComboBox.setEnabled(isKeyboardEnabled);
        soilTable.setEnabled(isKeyboardEnabled||isFileLocationSelected);
        soilNameCombo.setEditable(isKeyboardEnabled);
        jButtonRefresh.setEnabled(isFileLocationSelected);
        
        errorLabel.setVisible(false);
        wcErrorLabel.setVisible(false);

        DefaultTableModel model = (DefaultTableModel) soilTable.getModel();
        int row = model.getRowCount();
        for (int i = 0; i < row; i++) {
            model.removeRow(0);
        }

        // Update the list of soils in the soil name combo box.
        if (jRadioButtonSavedLocation.isSelected()) {
            
          
            if (utils.getSoilData()==null) {
                utils.setSoilData(new SoilData ());                
            }

            if (soilFileListCombo.getItemCount()>0) {
                soilFileListCombo.removeAllItems();
            }
        
            soilFileListCombo.removeAllItems();
            
            if (isFileLocationSelected) {
                files = null;
                files = getListOfDataFiles();
                int i = 0;
                for (File f : files) {
                    String key = f.getName(); 
                    key = key.trim ();
                    
                    soilFileListCombo.addItem(f.getName());
                    updateSoilDataBaseOnSoilFileSelection (i++);
                    
                }
            }
            
            //updateSoilDataBaseOnSoilFileSelection (0);
            updateSoilInfoAndSoilLayerInfo (0);
            prevButtonSelectedSavedLocation = true;
        }
        
        int index = isFileSelected ? 0 : (isFileLocationSelected ? 1 : 2);
        //System.out.println ("\n\n ToogleSelection ---- " + index);        
        waterholdcapacityBox.setSelectedIndex(waterHoldSelectedItemIndex[index]);
        isInitializing = false;
 
    }
    
    
    private File[] getListOfDataFiles () {
        
        if (files!=null) return files;

        String home = System.getProperty("user.home");
        File dir = new File(home+"/Downloads"); 
        
        String siteName = utils.getSITE();

        //System.out.println (siteName);
        FileFilter fileFilter = new WildcardFileFilter("*.json*");
        File[] files = dir.listFiles(fileFilter);
        
        if (files.length > 0) {
            Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
        }
        
        return files;
    }


    
    private File findLatestFile (int index) throws FileNotFoundException{
        if (files!=null)
            return files[index];
        
        // Get the latest file form the download folder.
        String home = System.getProperty("user.home");
        File dir = new File(home+"/Downloads"); 
        File theNewestFile = null;
        //File dir = new File(filePath);
        FileFilter fileFilter = new WildcardFileFilter("*." + "json");
        File[] thesefiles = dir.listFiles(fileFilter);
        
        if (thesefiles.length > 0) {
            Arrays.sort(thesefiles, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            files = thesefiles; 
            theNewestFile = thesefiles[index];
        } else {
            throw new FileNotFoundException(Messages.FILE_NOT_FOUND_MESSAGE);
        }
        return theNewestFile;
    }
    
    
    private void readSoilDataJsonFileForUtils (File latestFile) throws FileNotFoundException{
        SoilData soilData = utils.getSoilData();//SoilData.getSoilDataInstance();
        if (soilData.getSoilsFromFile(latestFile.getName().trim())!= null) 
            return;
  
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
        
        ArrayList<Soil> soilList = new ArrayList<Soil> ();
        
        for (JsonNode n : soils) {
            String soilName = n.path("soilName").textValue();
            String soilSeriesName = n.path("mukeyName").textValue();
            String soilTypeArea = n.path("compArea").textValue();
            JsonNode soilLayersNodes = n.path("soilLayer");
            
            int NL = 0;

            double[] wc = new double [6];
            double[] wcl= new double [6];
            double[] wcu= new double [6];
            double[] du= new double [6];
            String[] txt  = new String[3];
            
            for (JsonNode node : soilLayersNodes) {                
                System.out.println ("NL we are looking for: " + NL);
                wcu[NL] = node.get("sldul").asDouble()/100.00;
                du[NL] = node.get("sllb").asDouble()*0.39370;
                du[NL] = Math.floor(du[NL]*1000)/1000;
                wcl[NL] = node.get("slll").asDouble()/100.00;
                
                if (whcIndex == 0) {
                    wc[NL] = wcl[NL];
                } else if (whcIndex == 2) {
                    wc[NL] = wcu[NL];
                } else {
                    wc[NL] = 0.5 * (wcl[NL] + wcu[NL]);
                }
                
                wc[NL] = Math.floor(wc[NL]*1000)/1000;
                NL++;
            }
        
            Soil soil = new Soil (row, soilName, soilSeriesName, NL);
            soil.setValues(wc, wcl, wcu, du, txt);
            
            if (soilTypeArea!=null)
                soil.setSoilTypeArea(Double.valueOf(soilTypeArea));
            else 
                soil.setSoilTypeArea(0.0);
            //soilData.addSoil(latestFile.getName(), soil);
            soilList.add(soil);
            row++;
        }
        
        if (soilList.size()>0)
            utils.setDefaultSoil(soilList.get(0));
        
        soilData.addSoilList(latestFile.getName(), soilList);
    }    
    
    public String readFromJsonFileForSoilDetailsButton (File theNewestFile) throws IOException {

        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        str += "Data Date: " + sdf.format(theNewestFile.lastModified()) + "\n";
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(theNewestFile);
        JsonNode soils = root.path("soils");
        
        String fileName = theNewestFile.getName();
        str+= "Data File Name: " + fileName+"\n";
        for (JsonNode n : soils) {
            
            //Soil soil = new Soil ();
            
            String soilName = n.path("soilName").textValue();
            String soilTypeArea = n.path("compArea").textValue();
            String soilSeriesName = n.path("mukeyName").textValue();
            
            str += "Soil Seires Name = " + soilSeriesName + "\n";
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
            WC[i] =  Math.floor(WCL[i] * 100) / 100;
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
    private javax.swing.JButton jButtonRefresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JRadioButton jRadioButtonSavedLocation;
    private javax.swing.JRadioButton jRadioFile;
    private javax.swing.JRadioButton jRadioKeyboard;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton prevButton;
    private javax.swing.JButton showSoilDataButton;
    private javax.swing.JComboBox<String> soilFileListCombo;
    private javax.swing.JComboBox soilListBox;
    private javax.swing.JComboBox<String> soilNameCombo;
    private javax.swing.JLabel soilNameLabel;
    private javax.swing.JTextField soilNameText;
    private javax.swing.JTable soilTable;
    private javax.swing.JLabel soilTextureLabel;
    private javax.swing.JTextField soilTextureText;
    private javax.swing.JComboBox waterholdcapacityBox;
    private javax.swing.JLabel wcErrorLabel;
    // End of variables declaration//GEN-END:variables
}
