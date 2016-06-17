/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.agmip.ui.afsirs.frames;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import org.agmip.ui.afsirs.util.AFSIRSUtils;
import org.agmip.ui.afsirs.util.FrameTracker;
import org.agmip.ui.afsirs.util.Irrigation;
import org.agmip.ui.afsirs.util.Messages;

/**
 *
 * @author rkmalik
 */
public class SiteInfoFrame extends javax.swing.JFrame {

    /**
     * Creates new form SiteInfoFrame
     */
    private String CTYPE, PDATE, STARTIRRDATE, ENDIRRDATE;
    private int ICODE, IPRT;
    private double FIX, PIR;
    private ArrayList<String> climFileList = new ArrayList<String>();
    private ArrayList<Irrigation> irrList = new ArrayList<Irrigation>();

    final static String[][] MDAY = {
        {"Day","1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"},
        {"Day","1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"},
        {"Day","1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28"}
    };

    private AFSIRSUtils utils;
    //private JFrame next = null;
    private boolean fileChecked = false;
    private int IDCODE;
    private InputStreamReader climIR;
    

    public SiteInfoFrame() {
        initComponents();
        cropListInit();
        climateLocationListInit();
        irrigationListInit();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        double cwidth = getSize().getWidth();
        double cheight = getSize().getHeight();
        if (cheight > height) {
            jPanel1.setSize(620, 680);
            this.setSize((int) cwidth, (int) height);
        } else {
            jPanel1.setSize(770, 850);
            jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        }
        setLocation(400, 50);
        setResizable(false);
        initializeUtilities();

        if (AFSIRSUtils.defaultMode) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM,dd,yyyy");
                //siteUnitName.setText("Unit1");
                versionLable.setText("AFSIRS INTERACTIVE VERSION "+Messages.MAX_VERSION+"."+Messages.MIN_VERSION);
                irrTypeBox.setSelectedIndex(1);
                annual.setSelected(true);
                //outputStyleCombo.setSelectedIndex(3);
                climateLocationBox.setSelectedIndex(5);
            } catch (Exception e) {

            }
        }

        getRootPane().setDefaultButton(nextButton);
    }

    public void initializeUtilities() {
        utils = AFSIRSUtils.getInstance();
    }

    public void cropListInit() {

        // TODO add your handling code here:
        cropList.removeAllItems();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/data/crop.dat")));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("ANNUAL")) {
                    break;
                }
            }
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                String crop = line.substring(0, 13).trim();
                if (crop.length() < 1) {
                    break;
                }
                cropList.addItem(crop);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void climateLocationListInit() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/data/CLIMLIST.txt")));
            String line;
            while ((line = br.readLine()) != null) {
                String city = line.split(" ")[0];
                String file = line.split(" ")[1];
                climFileList.add(file);
                climateLocationBox.addItem(city);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        climateLocationBox.addItem("None of the above");
    }

    public void irrigationListInit() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/data/ir.dat")));
            String line = br.readLine();
            String[] parts = line.split(" ");
            int i = 0;
            while (i < parts.length) {
                if (parts[i].length() > 0) {
                    break;
                }
                i++;
            }
            int n = Integer.parseInt(parts[i].trim());

            line = br.readLine();
            i = 0;
            while (i < n) {
                line = br.readLine();
                parts = line.split("  ");
                Irrigation irr = new Irrigation();
                irr.setCode(Integer.parseInt(parts[1].trim()));
                irr.setEff(Double.parseDouble(parts[2]));
                irr.setArea(Double.parseDouble(parts[3]));
                irr.setEx(Double.parseDouble(parts[4]));
                irr.setSys(parts[5]);
                irrList.add(irr);

                irrTypeBox.addItem(irr.getSys());
                i++;
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

        cropTypeGroup = new javax.swing.ButtonGroup();
        irrigationReqGroup = new javax.swing.ButtonGroup();
        irrDepthRadioGroup = new javax.swing.ButtonGroup();
        jLabel5 = new javax.swing.JLabel();
        versionLable = new javax.swing.JLabel();
        helpButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        nextButton = new javax.swing.JButton();
        siteUnitName = new javax.swing.JTextField();
        siteNameTextBox = new javax.swing.JTextField();
        unitNameLabel = new javax.swing.JLabel();
        outFileLabel = new javax.swing.JLabel();
        climFileNameLabel = new javax.swing.JLabel();
        startIrrDateLabel = new javax.swing.JLabel();
        endIrrDateLabel = new javax.swing.JLabel();
        climateLocationBox = new javax.swing.JComboBox();
        climateLocationLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        fieldCapacityRadioButton = new javax.swing.JRadioButton();
        fixedDepthRadioButton = new javax.swing.JRadioButton();
        deficitIrrRadioButton = new javax.swing.JRadioButton();
        idcodeText = new javax.swing.JTextField();
        idcodeLabel = new javax.swing.JLabel();
        noneRadioButton = new javax.swing.JRadioButton();
        jPanel3 = new javax.swing.JPanel();
        iversCheckBox = new javax.swing.JCheckBox();
        irrAppEffLabel = new javax.swing.JLabel();
        irrAppEffText = new javax.swing.JTextField();
        soilSurfaceIrrLabel = new javax.swing.JLabel();
        soilSurfaceIrrText = new javax.swing.JTextField();
        exirLabel = new javax.swing.JLabel();
        exirText = new javax.swing.JTextField();
        netRadio = new javax.swing.JRadioButton();
        grossRadio = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        irrTypeBox = new javax.swing.JComboBox();
        irrTypeLabel = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        cropList = new javax.swing.JComboBox();
        cropLabel = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        perennial = new javax.swing.JRadioButton();
        annual = new javax.swing.JRadioButton();
        ownersName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        startMonth = new javax.swing.JComboBox<>();
        startDay = new javax.swing.JComboBox<>();
        endDay = new javax.swing.JComboBox<>();
        endMonth = new javax.swing.JComboBox<>();
        plantedAreaText = new javax.swing.JTextField();
        plantedAreaLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("AFSIRS");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setText("AGRICULTURAL FIELD SCALE IRRIGATION REQUIREMENTS SIMULATION");
        jLabel5.setToolTipText("THIS MODEL CALCULATES IRRIGATION REQUIREMENTS FOR FLORIDA CROPS, SOILS, AND CLIMATE CONDITIONS.");

        versionLable.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        versionLable.setText("ASFIRS INTERACTIVE VERSION 6.2");
        versionLable.setToolTipText("THIS MODEL CALCULATES IRRIGATION REQUIREMENTS FOR FLORIDA CROPS, SOILS, AND CLIMATE CONDITIONS.");

        helpButton.setText("Help");
        helpButton.setActionCommand("helpButton");
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpButtonActionPerformed(evt);
            }
        });

        nextButton.setText("Next");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        siteNameTextBox.setFocusTraversalPolicyProvider(true);
        siteNameTextBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                siteNameTextBoxActionPerformed(evt);
            }
        });

        unitNameLabel.setText("Unit Name");

        outFileLabel.setText("Site Name");

        climFileNameLabel.setForeground(new java.awt.Color(0, 0, 255));

        startIrrDateLabel.setText("Beginning Date of Crop Irrigation Season");

        endIrrDateLabel.setText("Ending Date of Crop Irrigation Season");

        climateLocationBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                climateLocationBoxActionPerformed(evt);
            }
        });

        climateLocationLabel.setText("Select Climate Location");

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setText("Define Irrigation Water depths per application");

        irrDepthRadioGroup.add(fieldCapacityRadioButton);
        fieldCapacityRadioButton.setSelected(true);
        fieldCapacityRadioButton.setText("Irrigate to Field Capacity");
        fieldCapacityRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldCapacityRadioButtonActionPerformed(evt);
            }
        });

        irrDepthRadioGroup.add(fixedDepthRadioButton);
        fixedDepthRadioButton.setText("Apply a fixed depth per application ( > 0.1)");
        fixedDepthRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fixedDepthRadioButtonActionPerformed(evt);
            }
        });

        irrDepthRadioGroup.add(deficitIrrRadioButton);
        deficitIrrRadioButton.setText("Deficit Irrigation (50 - 100)");
        deficitIrrRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deficitIrrRadioButtonActionPerformed(evt);
            }
        });

        idcodeText.setEnabled(false);
        idcodeText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idcodeTextActionPerformed(evt);
            }
        });

        irrDepthRadioGroup.add(noneRadioButton);
        noneRadioButton.setText("None");
        noneRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noneRadioButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fixedDepthRadioButton)
                            .addComponent(fieldCapacityRadioButton)
                            .addComponent(deficitIrrRadioButton)
                            .addComponent(noneRadioButton)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(idcodeText, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(idcodeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(49, 49, 49))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fieldCapacityRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fixedDepthRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deficitIrrRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noneRadioButton)
                .addGap(14, 14, 14)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(idcodeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(idcodeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Irrigation System"));

        iversCheckBox.setText("Check to use default values from IR.dat");
        iversCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iversCheckBoxActionPerformed(evt);
            }
        });

        irrAppEffLabel.setText("Irrigation Application Efficiency (0.01 - 1.0)");

        irrAppEffText.setText("1.0");
        irrAppEffText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                irrAppEffTextActionPerformed(evt);
            }
        });

        soilSurfaceIrrLabel.setText("Fraction of the Soil Surface Irrigated (0.01 - 1.0)");

        soilSurfaceIrrText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                soilSurfaceIrrTextActionPerformed(evt);
            }
        });
        soilSurfaceIrrText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                soilSurfaceIrrTextKeyReleased(evt);
            }
        });

        exirLabel.setText("<html><body>Fraction of ET Extracted from the irrigated <br>zone (0.01 - 1.0)</body><html>");

        exirText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exirTextActionPerformed(evt);
            }
        });

        irrigationReqGroup.add(netRadio);
        netRadio.setSelected(true);
        netRadio.setText("Net");
        netRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                netRadioActionPerformed(evt);
            }
        });

        irrigationReqGroup.add(grossRadio);
        grossRadio.setText("Gross");
        grossRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grossRadioActionPerformed(evt);
            }
        });

        jLabel1.setText("Irrigation Requirements Calculation Options");

        irrTypeBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                irrTypeBoxActionPerformed(evt);
            }
        });

        irrTypeLabel.setText("Select Irrigation System");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(exirLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(irrAppEffLabel)
                            .addComponent(soilSurfaceIrrLabel)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(exirText, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(netRadio)
                                .addGap(113, 113, 113)
                                .addComponent(grossRadio))
                            .addComponent(irrTypeBox, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(16, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(iversCheckBox)
                            .addComponent(jLabel1)
                            .addComponent(irrTypeLabel)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(irrAppEffText, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(soilSurfaceIrrText, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(irrTypeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(irrTypeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(netRadio)
                    .addComponent(grossRadio))
                .addGap(11, 11, 11)
                .addComponent(iversCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(irrAppEffLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(irrAppEffText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(soilSurfaceIrrLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(soilSurfaceIrrText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exirLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exirText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder("Crop")));

        cropList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cropListActionPerformed(evt);
            }
        });

        cropLabel.setText("Crop");

        jLabel8.setText("Crop Type");

        cropTypeGroup.add(perennial);
        perennial.setText("Perennial");
        perennial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                perennialActionPerformed(evt);
            }
        });

        cropTypeGroup.add(annual);
        annual.setText("Annual");
        annual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                annualActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(cropList, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cropLabel)
                            .addComponent(jLabel8)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(perennial)
                        .addGap(18, 18, 18)
                        .addComponent(annual)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(perennial)
                    .addComponent(annual))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cropLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cropList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        ownersName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ownersNameActionPerformed(evt);
            }
        });

        jLabel2.setText("Owner's Name");

        startMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Month", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" }));
        startMonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startMonthActionPerformed(evt);
            }
        });

        startDay.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Day", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));
        startDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startDayActionPerformed(evt);
            }
        });

        endDay.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Day", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));
        endDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endDayActionPerformed(evt);
            }
        });

        endMonth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Month", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" }));
        endMonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endMonthActionPerformed(evt);
            }
        });

        plantedAreaText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plantedAreaTextActionPerformed(evt);
            }
        });

        plantedAreaLabel.setText("Planted Area (Acres) :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(ownersName, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(climateLocationLabel)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(10, 10, 10)
                                                .addComponent(climateLocationBox, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(65, 65, 65))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                .addComponent(climFileNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel2)
                                    .addGap(138, 138, 138))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(siteNameTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(siteUnitName, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(endIrrDateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(outFileLabel)
                                    .addComponent(unitNameLabel)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(plantedAreaText, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(plantedAreaLabel)
                                    .addComponent(startIrrDateLabel)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(startMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(40, 40, 40)
                                        .addComponent(startDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(endMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(38, 38, 38)
                                        .addComponent(endDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(nextButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(156, 156, 156))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(outFileLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(siteNameTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(unitNameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(siteUnitName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addGap(3, 3, 3)
                        .addComponent(ownersName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(climateLocationLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(climateLocationBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(185, 185, 185)
                                .addComponent(climFileNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(23, 23, 23))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(plantedAreaLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(plantedAreaText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(startIrrDateLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(startDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(startMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(13, 13, 13)
                        .addComponent(endIrrDateLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(endDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(endMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(nextButton, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(74, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(206, 206, 206)
                .addComponent(versionLable)
                .addGap(72, 72, 72)
                .addComponent(helpButton)
                .addGap(0, 189, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 561, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(versionLable)
                    .addComponent(helpButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpButtonActionPerformed
        // TODO add your handling code here:
        try {
            File file = new File("AFSIRSTechnicalManual.pdf");
            if (!file.exists()) {
                InputStream fis = getClass().getResourceAsStream("/data/AFSIRSTechnicalManual.pdf");
                FileOutputStream fos = new FileOutputStream(file);
                while (fis.available() > 0) {
                    fos.write(fis.read());
                }
                fis.close();
                fos.close();
            }
            Desktop.getDesktop().open(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_helpButtonActionPerformed

    private void deficitIrrRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deficitIrrRadioButtonActionPerformed
        // TODO add your handling code here:
        IDCODE = 2;
        idcodeLabel.setText("<html><body>%, of field capacity for deficit <br> irrigation (50-100)</body></html>");
        idcodeLabel.setForeground(Color.black);
        idcodeText.setText("");
        idcodeText.setEnabled(true);
    }//GEN-LAST:event_deficitIrrRadioButtonActionPerformed

    private void climateLocationBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_climateLocationBoxActionPerformed
        // TODO add your handling code here:
        if (climateLocationBox.getSelectedIndex() >= climFileList.size()) {
            JPanel inputFilePanel = new JPanel();
            JLabel fileLabel = new JLabel("File Path : ");
            JButton browseButton = new JButton("Browse");
            final JTextField filePath = new JTextField(30);
            browseButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setCurrentDirectory(new java.io.File("."));
                    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        filePath.setText(chooser.getSelectedFile().getPath());
                    }
                }
            });

            inputFilePanel.add(fileLabel);
            inputFilePanel.add(filePath);
            inputFilePanel.add(browseButton);

            String climateFile = null;

            int result = JOptionPane.showConfirmDialog(null, inputFilePanel, "Select climate file", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                climateFile = filePath.getText();
                System.out.println(climateFile);
                try {
                    climIR = new InputStreamReader(new FileInputStream(climateFile));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                climFileNameLabel.setText("FILE");
                climFileNameLabel.setToolTipText(filePath.getText());
            } else {
                climateLocationBox.setSelectedIndex(-1);
            }
        } else {
            climFileNameLabel.setText("");
            climFileNameLabel.setToolTipText(null);
        }
    }//GEN-LAST:event_climateLocationBoxActionPerformed

    private void fixedDepthRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fixedDepthRadioButtonActionPerformed
        // TODO add your handling code here:
        IDCODE = 1;
        idcodeLabel.setText("<html><body>inches, Depth of water to apply <br>per irrigation (Greater Than 0.1)</body></html>");
        idcodeLabel.setForeground(Color.black);
        idcodeText.setText("");
        idcodeText.setEnabled(true);
    }//GEN-LAST:event_fixedDepthRadioButtonActionPerformed

    private void fieldCapacityRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldCapacityRadioButtonActionPerformed
        // TODO add your handling code here:
        IDCODE = 0;
        idcodeLabel.setText("");
        idcodeText.setText("");
        idcodeText.setEnabled(false);
    }//GEN-LAST:event_fieldCapacityRadioButtonActionPerformed

    private void irrTypeBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_irrTypeBoxActionPerformed
        // TODO add your handling code here:
        int ir = irrTypeBox.getSelectedIndex();

        int icrop = cropList.getSelectedIndex();

        FrameTracker.soilData = null;
        setIrrgationOption(ir);

        if (ir == AFSIRSUtils.IRCRFL && icrop != AFSIRSUtils.ICIT) {
            JOptionPane.showMessageDialog(null, "CROWN FLOOD APPLIES ONLY TOCITRUS IRRIGATION");
            irrTypeBox.setSelectedIndex(0);
            return;
        } else if (ir == AFSIRSUtils.IRNSYC && icrop != AFSIRSUtils.INSCY) {
            JOptionPane.showMessageDialog(null, "THIS IRRIGATION SYSTEM IS FOR CONTAINER NURSERIES ONLY");
            irrTypeBox.setSelectedIndex(0);
            return;
        } else if (ir == AFSIRSUtils.IRRICE && icrop != AFSIRSUtils.IRICE) {
            JOptionPane.showMessageDialog(null, "THIS IRRIGATION SYSTEM IS FOR RICE PRODUCTION ONLY");
            irrTypeBox.setSelectedIndex(0);
            return;
        }

        boolean setButtons = (ir == AFSIRSUtils.IRSEEP || ir == AFSIRSUtils.IRCRFL || ir == AFSIRSUtils.IRRICE);
        if (setButtons) {
            noneRadioButton.setSelected(true);
        } else {
            fieldCapacityRadioButton.setSelected(true);
        }

        noneRadioButton.setEnabled(setButtons);
        fieldCapacityRadioButton.setEnabled(!setButtons);
        fixedDepthRadioButton.setEnabled(!setButtons);
        deficitIrrRadioButton.setEnabled(!setButtons);
    }//GEN-LAST:event_irrTypeBoxActionPerformed

    private void netRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_netRadioActionPerformed
        // TODO add your handling code here:
        setIrrgationOption(irrTypeBox.getSelectedIndex());
    }//GEN-LAST:event_netRadioActionPerformed

    private void grossRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grossRadioActionPerformed
        // TODO add your handling code here:
        setIrrgationOption(irrTypeBox.getSelectedIndex());
    }//GEN-LAST:event_grossRadioActionPerformed

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        // TODO add your handling code here:
        //Check the details entered

        String outFile = siteNameTextBox.getText()+"-"+siteUnitName.getText();
        String outFileSummary = siteNameTextBox.getText()+"-"+siteUnitName.getText()+"-Summary.pdf";
        String outFileSummaryExcel = siteNameTextBox.getText()+"-"+siteUnitName.getText()+"-Summary.xlsx";

        Date date = new Date();
        String siteName = siteNameTextBox.getText();
        String unitName = siteUnitName.getText();
        String ownerSName = ownersName.getText();
        String cropName, climateFile = null;

        outFileLabel.setForeground(Color.black);
        startIrrDateLabel.setForeground(Color.black);
        endIrrDateLabel.setForeground(Color.black);

        unitNameLabel.setForeground(Color.black);
        //outStyleLabel.setForeground(Color.black);
        //suppressLabel.setForeground(Color.black);
        plantedAreaLabel.setForeground(Color.black);
        cropLabel.setForeground(Color.black);
        idcodeLabel.setForeground(Color.black);
        irrAppEffLabel.setForeground(Color.black);
        soilSurfaceIrrLabel.setForeground(Color.black);
        exirLabel.setForeground(Color.black);
        startIrrDateLabel.setForeground(Color.black);
        endIrrDateLabel.setForeground(Color.black);
        
 
        
        int sd = (startMonth.getSelectedIndex()) * 10 + (startDay.getSelectedIndex());
        int ed = (endMonth.getSelectedIndex()) * 10 + (endDay.getSelectedIndex());
        double plantedArea = 0.0;
        
        if (siteName == null || siteName.length() < 1) {
            outFileLabel.setForeground(Color.red);
            return;
        } else if (unitName == null || unitName.length() < 1) {
            unitNameLabel.setForeground(Color.red);
            return;
        }
        /*else if (outputStyleCombo.getSelectedIndex() < 0) {
            outStyleLabel.setForeground(Color.red);
            return;
        } else if (suppressComboBox.getSelectedIndex() < 0) {
            suppressLabel.setForeground(Color.red);
            return;
        }*/ else if (cropList.getSelectedIndex() < 0) {
            cropLabel.setForeground(Color.red);;
            return;
        } else if (annual.isSelected()) {            
            if (startMonth.getSelectedIndex()<1 || startDay.getSelectedIndex() <1) {
                startIrrDateLabel.setForeground(Color.red);          
                return;
            }

            if (endMonth.getSelectedIndex()<1 || endDay.getSelectedIndex() <1) {
                endIrrDateLabel.setForeground(Color.red);            
                return;
            }            
        } 
        
        
        /*else if (irrigationEndDateChooser.getDate().before(irrigationStartDateChooser.getDate())) {
            //endIrrDateLabel.setForeground(Color.red);
            return;
        }*/

        int ir = irrTypeBox.getSelectedIndex();
        double arzi = 0.0;
        double exir = 0.0;
        double eff = 0.0;

        try {
            eff = Double.parseDouble(irrAppEffText.getText());
        } catch (Exception e) {
            irrAppEffLabel.setForeground(Color.red);
            return;
        }
        if (eff < 0.01 || eff > 1.00) {
            irrAppEffLabel.setForeground(Color.red);
            return;
        }

        try {
            arzi = Double.parseDouble(soilSurfaceIrrText.getText());
        } catch (Exception e) {
            soilSurfaceIrrLabel.setForeground(Color.red);
            return;
        }
        if (arzi < 0.01 || arzi > 1.00) {
            soilSurfaceIrrLabel.setForeground(Color.red);
            return;
        }
        try {
            exir = Double.parseDouble(exirText.getText());
        } catch (Exception e) {
            exirLabel.setForeground(Color.red);
            return;
        }
        if (exir < 0.01 || exir > 1.00) {
            exirLabel.setForeground(Color.red);
            return;
        }

        try {
            plantedArea = Double.parseDouble(plantedAreaText.getText());
        } catch (Exception e) {
            plantedAreaLabel.setForeground(Color.red);
            return;
        }
        
        if (plantedArea <= 0) {
            plantedAreaLabel.setForeground(Color.red);
            return;
        }

        outFile += ".txt";
        
        double fix = 15;
        double pir = 75;
        if (IDCODE == 1) {
            try {
                fix = Double.parseDouble(idcodeText.getText());
            } catch (Exception e) {
                idcodeLabel.setForeground(Color.red);
                return;
            }
            if (fix < 0.10) {
                idcodeLabel.setForeground(Color.red);
                return;
            }
        } else if (IDCODE == 2) {
            try {
                pir = Double.parseDouble(idcodeText.getText());
            } catch (Exception e) {
                idcodeLabel.setForeground(Color.red);
                return;
            }
            if (pir < 50 || pir > 100) {
                idcodeLabel.setForeground(Color.red);
                return;
            }
        }

        startIrrDateLabel.setForeground(Color.black);
        endIrrDateLabel.setForeground(Color.black);

        PDATE = date.toString();
        //ICODE = outputStyleCombo.getSelectedIndex() - 1;
        //IPRT = suppressComboBox.getSelectedIndex();

        ICODE = 2;
        IPRT = 0;
        

        
        cropName = cropList.getSelectedItem().toString();

        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM-DD");
        //STARTIRRDATE = //irrigationStartDateChooser.getDate().toString();
        //ENDIRRDATE = //irrigationEndDateChooser.getDate().toString();
        //All entries valid : Write to File
        try {
            File ofile = new File(outFile);
            if (!fileChecked && ofile.exists()) {
                int reply = JOptionPane.showConfirmDialog(this, "\"" + outFile + "\" Already exists. Do you want to overwrite?");
                if (reply != 0) {
                    return;
                }
                fileChecked = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int ivers = iversCheckBox.isSelected() ? 0 : 1;

        //Set variables in utils
        //Output FileName, Today's Date, Site Name, isPerennial, ICODE
        //IPRT, CTYPE, ICROP, IRRIGATION START and END date
        //IDCODE, FIX, PIR, IVERS, CLIMFIL, IR, ARZI, EXIR
        utils.setOutFile(outFile);
        utils.setSummaryFile(outFileSummary);
        utils.setSummaryFileExcel(outFileSummaryExcel);        
        utils.setUNIT(unitName);
        utils.setOWNER(ownerSName);
        utils.setTodayDate(date);
        utils.setSiteName(siteName);
        utils.setPerennial(perennial.isSelected());
        utils.setCodes(ICODE, IPRT);
        utils.setCropData(cropList.getSelectedIndex(), cropName);
        utils.setPLANTEDACRES(plantedArea);

        // As per latest discussion If the End date less than start date then it should be considered as teh 
        // Date in the next year.
        if (perennial.isSelected()) {
            utils.setIrrigationSeason(1, 1, 12, 31);
        } else {
            if (sd >= ed) {
                utils.setIrrigationSeason(startMonth.getSelectedIndex(),
                        startDay.getSelectedIndex(),
                        endMonth.getSelectedIndex(),
                        endDay.getSelectedIndex());
                Date d = new Date(Calendar.getInstance().get(Calendar.YEAR), startMonth.getSelectedIndex(), startDay.getSelectedIndex());
                ENDIRRDATE = d.toString();
                d = new Date(Calendar.getInstance().get(Calendar.YEAR), endMonth.getSelectedIndex(), endDay.getSelectedIndex());
                STARTIRRDATE = d.toString();
            } else {
                utils.setIrrigationSeason(startMonth.getSelectedIndex(),
                        startDay.getSelectedIndex(),
                        endMonth.getSelectedIndex(),
                        endDay.getSelectedIndex());
                Date d = new Date(Calendar.getInstance().get(Calendar.YEAR), startMonth.getSelectedIndex(), startDay.getSelectedIndex());
                STARTIRRDATE = d.toString();
                d = new Date(Calendar.getInstance().get(Calendar.YEAR), endMonth.getSelectedIndex(), endDay.getSelectedIndex());
                ENDIRRDATE = d.toString();
            }
        }

        utils.setisNet(netRadio.isSelected());
        if (IDCODE == 1) {
            utils.setIDCODE(IDCODE, fix);
        } else if (IDCODE == 2) {
            utils.setIDCODE(IDCODE, pir);
        } else {
            utils.setIDCODE(IDCODE, 0);
        }
        utils.setIVERS(ivers == 0);
        if (climateLocationBox.getSelectedIndex() < climFileList.size()) {
            String climateStation = (String)climateLocationBox.getSelectedItem();
            utils.setCLIMATESTATION(climateStation);
            climateFile = climFileList.get(climateLocationBox.getSelectedIndex());
            climIR = new InputStreamReader(getClass().getResourceAsStream("/data/" + climateFile));
        }

        utils.setClimateFile(climIR);

        utils.setIrrigationSystem(ir, arzi, exir, eff, irrTypeBox.getSelectedItem().toString());

        if (FrameTracker.soilData == null) {
            FrameTracker.soilData = new SWFrame(this);
        }
        FrameTracker.siteInfoFrame = this;
        FrameTracker.siteInfoFrame.setVisible(false);
        FrameTracker.soilData.setVisible(true);

    }//GEN-LAST:event_nextButtonActionPerformed

    private void idcodeTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idcodeTextActionPerformed
        // TODO add your handling code here:
        nextButtonActionPerformed(evt);
    }//GEN-LAST:event_idcodeTextActionPerformed

    private void iversCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iversCheckBoxActionPerformed
        // TODO add your handling code here:
        setIrrgationOption(irrTypeBox.getSelectedIndex());
        FrameTracker.soilData = null;
    }//GEN-LAST:event_iversCheckBoxActionPerformed

    private void soilSurfaceIrrTextKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_soilSurfaceIrrTextKeyReleased
        // TODO add your handling code here:
        int ir = irrTypeBox.getSelectedIndex();
        if (ir == 1 || ir == 2) {
            try {
                soilSurfaceIrrLabel.setForeground(Color.black);
                Double arzi = Double.parseDouble(soilSurfaceIrrText.getText());
                if (arzi < 0.01 || arzi > 1.0) {
                    throw new Exception("Incorrect value range");
                }
                double exir = Math.round((0.7 * arzi + 0.10 * (1.0 - arzi)) * 100) / 100.0;
                exirText.setText(exir + "");
                exirText.setEnabled(false);
            } catch (Exception e) {
                soilSurfaceIrrLabel.setForeground(Color.red);
            }
        }
    }//GEN-LAST:event_soilSurfaceIrrTextKeyReleased

    private void noneRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noneRadioButtonActionPerformed
        // TODO add your handling code here:
        IDCODE = -1;
        idcodeLabel.setText("");
        idcodeText.setText("");
        idcodeText.setEnabled(false);
    }//GEN-LAST:event_noneRadioButtonActionPerformed

    private void siteNameTextBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_siteNameTextBoxActionPerformed
        // TODO add your handling code here:
        nextButtonActionPerformed(evt);
    }//GEN-LAST:event_siteNameTextBoxActionPerformed

    private void irrAppEffTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_irrAppEffTextActionPerformed
        // TODO add your handling code here:
        nextButtonActionPerformed(evt);
    }//GEN-LAST:event_irrAppEffTextActionPerformed

    private void soilSurfaceIrrTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_soilSurfaceIrrTextActionPerformed
        // TODO add your handling code here:
        nextButtonActionPerformed(evt);
    }//GEN-LAST:event_soilSurfaceIrrTextActionPerformed

    private void exirTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exirTextActionPerformed
        // TODO add your handling code here:
        nextButtonActionPerformed(evt);
    }//GEN-LAST:event_exirTextActionPerformed

    private void ownersNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ownersNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ownersNameActionPerformed

    private void endMonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endMonthActionPerformed
        // TODO add your handling code here:
        // This can be the Min month as that of the startMonth
        endDay.removeAllItems();
        int index = endMonth.getSelectedIndex()-1;

        switch (index) {

            case 0:
            case 2:
            case 4:
            case 6:
            case 7:
            case 9:
            case 11:
                for (String s : MDAY[0]) {
                    endDay.addItem(s);
                }
                break;

            case 1:
                for (String s : MDAY[2]) {
                    endDay.addItem(s);
                }
                break;

            case 3:
            case 5:
            case 8:

            case -1:                
            case 10:
                for (String s : MDAY[1]) {
                    endDay.addItem(s);
                }
                break;

        }

    }//GEN-LAST:event_endMonthActionPerformed

    private void endDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endDayActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_endDayActionPerformed

    private void startDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startDayActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_startDayActionPerformed

    private void startMonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startMonthActionPerformed
        // TODO add your handling code here:
        startDay.removeAllItems();
        int index = startMonth.getSelectedIndex()-1;

        switch (index) {

            case 0:
            case 2:
            case 4:
            case 6:
            case 7:
            case 9:
            case 11:
                for (String s : MDAY[0]) {
                    startDay.addItem(s);
                }
                break;

            case 1:
                for (String s : MDAY[2]) {
                    startDay.addItem(s);
                }
                break;

            case -1:
            case 3:
            case 5:
            case 8:
            case 10:
                for (String s : MDAY[1]) {
                    startDay.addItem(s);
                }
                break;
        }
    }//GEN-LAST:event_startMonthActionPerformed

    private void annualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_annualActionPerformed
        // TODO add your handling code here:
        
        // Enable the date buttons
        startMonth.setEnabled(true);
        endMonth.setEnabled(true);
        startDay.setEnabled(true);
        endDay.setEnabled(true);
        
        cropList.removeAllItems();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/data/crop.dat")));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("ANNUAL")) {
                    break;
                }
            }
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                String crop = line.substring(0, 13).trim();
                if (crop.length() < 1) {
                    break;
                }
                cropList.addItem(crop);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        FrameTracker.soilData = null;
    }//GEN-LAST:event_annualActionPerformed

    private void perennialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_perennialActionPerformed
        // TODO add your handling code here:
        //Read Perennial crop list from CROP.DAT
        
        // Disable the date buttons
        startMonth.setEnabled(false);
        endMonth.setEnabled(false);
        startDay.setEnabled(false);
        endDay.setEnabled(false);
        
        
        cropList.removeAllItems();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/data/crop.dat")));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("CROP")) {
                    break;
                }
            }
            while ((line = br.readLine()) != null) {
                line = br.readLine();
                if (line.length() < 14) {
                    break;
                }
                String crop = line.substring(0, 14).trim();
                if (crop.length() < 1) {
                    break;
                }
                cropList.addItem(crop);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        FrameTracker.soilData = null;
    }//GEN-LAST:event_perennialActionPerformed

    private void cropListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cropListActionPerformed
        // TODO add your handling code here:
        FrameTracker.soilData = null;
    }//GEN-LAST:event_cropListActionPerformed

    private void plantedAreaTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plantedAreaTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_plantedAreaTextActionPerformed

    private void setIrrgationOption(int ir) {

        soilSurfaceIrrText.setEnabled(true);
        irrAppEffText.setEnabled(true);
        exirText.setEnabled(true);
        iversCheckBox.setEnabled(true);

        Irrigation irr = irrList.get(irrTypeBox.getSelectedIndex());
        irrAppEffText.setText(irr.getEff() + "");
        soilSurfaceIrrText.setText(irr.getArea() + "");
        exirText.setText(irr.getEx() + "");
        //If user defined irrigation system selected

        if (ir == 0) {
            irrAppEffText.setEnabled(!netRadio.isSelected());
            soilSurfaceIrrText.setEnabled(true);
            exirText.setEnabled(true);
        } else //Use default values
        if (iversCheckBox.isSelected()) {
            if (netRadio.isSelected()) {
                irrAppEffText.setText(1.0 + "");
            }
            irrAppEffText.setEnabled(false);
            soilSurfaceIrrText.setEnabled(false);
            exirText.setEnabled(false);
        } else {
            if (netRadio.isSelected()) {
                irrAppEffText.setText(1.0 + "");
                irrAppEffText.setEnabled(false);
            }
            if (irrTypeBox.getSelectedIndex() >= 3) {
                soilSurfaceIrrText.setEnabled(false);
            }
        }

        if (ir == 2 || ir == 1) {
            exirText.setEnabled(false);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SiteInfoFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SiteInfoFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SiteInfoFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SiteInfoFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SiteInfoFrame().setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton annual;
    private javax.swing.JLabel climFileNameLabel;
    private javax.swing.JComboBox climateLocationBox;
    private javax.swing.JLabel climateLocationLabel;
    private javax.swing.JLabel cropLabel;
    private javax.swing.JComboBox cropList;
    private javax.swing.ButtonGroup cropTypeGroup;
    private javax.swing.JRadioButton deficitIrrRadioButton;
    private javax.swing.JComboBox<String> endDay;
    private javax.swing.JLabel endIrrDateLabel;
    private javax.swing.JComboBox<String> endMonth;
    private javax.swing.JLabel exirLabel;
    private javax.swing.JTextField exirText;
    private javax.swing.JRadioButton fieldCapacityRadioButton;
    private javax.swing.JRadioButton fixedDepthRadioButton;
    private javax.swing.JRadioButton grossRadio;
    private javax.swing.JButton helpButton;
    private javax.swing.JLabel idcodeLabel;
    private javax.swing.JTextField idcodeText;
    private javax.swing.JLabel irrAppEffLabel;
    private javax.swing.JTextField irrAppEffText;
    private javax.swing.ButtonGroup irrDepthRadioGroup;
    private javax.swing.JComboBox irrTypeBox;
    private javax.swing.JLabel irrTypeLabel;
    private javax.swing.ButtonGroup irrigationReqGroup;
    private javax.swing.JCheckBox iversCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton netRadio;
    private javax.swing.JButton nextButton;
    private javax.swing.JRadioButton noneRadioButton;
    private javax.swing.JLabel outFileLabel;
    private javax.swing.JTextField ownersName;
    private javax.swing.JRadioButton perennial;
    private javax.swing.JLabel plantedAreaLabel;
    private javax.swing.JTextField plantedAreaText;
    private javax.swing.JTextField siteNameTextBox;
    private javax.swing.JTextField siteUnitName;
    private javax.swing.JLabel soilSurfaceIrrLabel;
    private javax.swing.JTextField soilSurfaceIrrText;
    private javax.swing.JComboBox<String> startDay;
    private javax.swing.JLabel startIrrDateLabel;
    private javax.swing.JComboBox<String> startMonth;
    private javax.swing.JLabel unitNameLabel;
    private javax.swing.JLabel versionLable;
    // End of variables declaration//GEN-END:variables
}
