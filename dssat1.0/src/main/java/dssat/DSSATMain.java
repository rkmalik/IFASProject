/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dssat;

import dssat.tablecell.CellButtonRenderer;
import dssat.tablecell.CellButtonEditor;
import dssat.tablecell.CellCalendarRenderer;
import com.opencsv.CSVReader;
import com.toedter.calendar.JDateChooserCellEditor;
import dssat.tablecell.CellTimeEditor;
import dssat.tablecell.CellTimeRenderer;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import tablecell.CellComboBoxEditor;
import tablecell.CellComboBoxRenderer;
import javax.swing.JOptionPane;
import java.nio.file.StandardCopyOption;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author rkmalik
 */
public class DSSATMain extends javax.swing.JFrame {

    public static final Logger LOGGER = Logger.getLogger(DSSATMain.class.getName());
    //public static final Level = Level.ALL;
    static WeatherFileSystem wthfile;
    static CultivaFileSystem cultivafile;
    static int weatherstationid;
    public static String curdirpath;
    public static String csvdirpath;
    public static String datadir;
    public static String dirseprator;
    public static String culfilepath;
    static HashMap<String, String> datamap = new HashMap<String, String>();
    CSVFileHandler mycsvfile = new CSVFileHandler();
    private String mDSSATInstallation = "";

    //static DBConnect global_db = null;    
    //static DBConnect soil_db = null;    
    //static DBConnect weather_historic_daily = null;
    static HashMap<String, String> cropname_culfilename = null;

    CalendarProgram calender = null;
    private Object FileChooserDemo;

    private DateFormat dateFormat;

    class Couple {

        String method;
        String code;

        Couple(String method, String code) {
            this.method = method;
            this.code = code;
        }

        @Override
        public String toString() {
            return method;
        }

        public String[] converToArray(Couple[] array) {
            String[] stringArray = new String[array.length];
            for (int i = 0; i < array.length; i++) {
                stringArray[i] = array[i].method;
            }
            return stringArray;
        }

    }

    class CoupleCompare implements Comparator<Couple> {

        @Override
        public int compare(Couple o1, Couple o2) {
            return o1.method.compareTo(o2.method);
        }
    }
    LinkedList<Couple> plantingMethodsList = new LinkedList<>();
    LinkedList<Couple> irrigationMethod = new LinkedList<>();

    
    LinkedList<Couple> fertmethod = new LinkedList<>();
    LinkedList<Couple> fertmaterial = new LinkedList<>();

    
    HashMap<String, String> fertMethodVsCode = new HashMap<>();
    HashMap<String, String> fertMaterialVsCode = new HashMap<String, String> ();    

    Action delete = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            JTable table = (JTable) e.getSource();
            int modelRow = Integer.valueOf(e.getActionCommand());
            ((DefaultTableModel) table.getModel()).removeRow(modelRow);
        }
    };

    private String mSiteCode;

    /**
     * Creates new form DSSATMain
     */
    public DSSATMain(String siteName) {
        this.setIconImage(Toolkit.getDefaultToolkit().getImage("/images/Dripper.jpg"));
        setVisible(false);
        initAbsoluteDataPath();
        initLogger();
        initComponents();
        IrrigationFertilizerPanel.setVisible(false);
        initTextFields();
        //initPlantingDate ();        
        initGlobalDBInfo();
        initSoilInfo();
        initWeatherHistoric();
        initCropInfo();
        initFertInfo();
        initFrameSize();
        initializeTree();
        setSiteName(siteName);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        double cwidth = getSize().getWidth();
        double cheight = getSize().getHeight();
        
        if (cheight > height) {
            jSplitPane1.setSize(620, 680);
            IrrigationFertilizerPanel.setSize(620, 680);
            FieldPanel.setSize(620, 680);
            this.setSize((int) 620, (int) 680);
        } else {
            jSplitPane1.setSize(770, 850);
            IrrigationFertilizerPanel.setSize(770, 850);
            FieldPanel.setSize(770, 850);
            this.setSize((int) 770, (int) 850);
            //jSplitPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            //jSplitPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        }
        //setLocation(400, 50);
        //setResizable(false);
        
        
        LOGGER.log(Level.ALL, "Application Initialized.");
    }

    private void initializeTree() {
        //Read Project File and get the array of Paths
        //CSVFileHandler csvfile = new CSVFileHandler ();
        HashMap<String, HashMap<String, HashMap<String, String>>> mymap = mycsvfile.ReadSitesFile();
        jProjectExplorer.removeAll();
        // Read the sitesinfo.sites file and read the data from the file. 
        DefaultTreeModel model = (DefaultTreeModel) jProjectExplorer.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();

        root.setUserObject("Sites");
        model.nodeChanged(root);

        //for (int sites = 0; sites < ; sites++) {
        for (Map.Entry<String, HashMap<String, HashMap<String, String>>> esites : mymap.entrySet()) {

            String sitename = esites.getKey();
            HashMap<String, HashMap<String, String>> myblocks = esites.getValue();
            DefaultMutableTreeNode site = new DefaultMutableTreeNode(sitename);
            root.add(site);

            // Get Block count for site
            for (Map.Entry<String, HashMap<String, String>> eblocks : myblocks.entrySet()) {
                String blockname = eblocks.getKey();
                HashMap<String, String> myzones = eblocks.getValue();
                DefaultMutableTreeNode block = new DefaultMutableTreeNode(blockname);

                site.add(block);
                for (Map.Entry<String, String> ezones : myzones.entrySet()) {
                    String zonename = ezones.getKey();
                    DefaultMutableTreeNode zone = new DefaultMutableTreeNode(zonename);
                    block.add(zone);
                }
            }
        }
    }

    private void initLogger() {
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
    }

    private void initAbsoluteDataPath() {
        Path currentRelativePath = Paths.get("");
        curdirpath = currentRelativePath.toAbsolutePath().toString();
        dirseprator = File.separator;
        csvdirpath = curdirpath + dirseprator + "properties" + dirseprator + "csv";
        culfilepath = curdirpath + dirseprator + "properties" + dirseprator + "Genotype";
        LOGGER.log(Level.ALL, "Initialized the csv directory path : " + csvdirpath);
        LOGGER.log(Level.ALL, "Initialized the cultivar file path : " + culfilepath);
        //System.out.println(csvdirpath);
    }

    private void initFertInfo() {

        CSVReader reader;
        String[] nextLine;

        LOGGER.log(Level.ALL, "Initializing fertilization information.");
        try {
            LOGGER.log(Level.ALL, "Initializing Fertilizer info from " + "/properties/csv/dssat_codelookup.csv");
            reader = new CSVReader(new InputStreamReader(getClass().getResourceAsStream("/properties/csv/dssat_codelookup.csv")));
            
            while ((nextLine = reader.readNext()) != null) {
                //cropName.add(nextLine[1]);
                String column2 = nextLine[2];
                String column1 = nextLine[1];
                String plantingMethodHeader = nextLine[0];

                String description = new String();
                // Check if the code is FE/AP Then Build the descripttion till end.
                if (column2.startsWith("FE")) {
                    for (int i = 3; i < nextLine.length; i++) {
                        description = description + nextLine[i];
                    }
                    Couple c = new Couple(description, column2);
                    fertmaterial.add(c);
                    
                    // This have all the values for code vs Code value
                    fertMaterialVsCode.put(description.trim (), column2);
                } else if (column1.startsWith("AP")) {

                    for (int i = 2; i < nextLine.length; i++) {
                        description = description + nextLine[i];
                    }
                    Couple c = new Couple(description.trim(), column2);
                    fertmethod.add(c);
                    // This have all the values for code vs Code value                    
                    fertMethodVsCode.put(description.trim(), column1);
                } else if (nextLine[0].contains("Methods - Irrigation and")) {
                    
                    System.out.println ("Hello Irrigation."+ nextLine[2] + nextLine[1]);
                    
                    if (nextLine[2].startsWith("Drip") || nextLine[2].startsWith("Water")) {
                        
                        if (nextLine[2].startsWith("Drip")) {
                            Couple c = new Couple("Dripper", nextLine[1]);
                            irrigationMethod.add(c);
                        }
                        
                        if (nextLine[2].startsWith("Water")) {
                            Couple c = new Couple("Water Table Depth", nextLine[1]);
                            irrigationMethod.add(c);
                        }
                    }
                    
                }

                if (plantingMethodHeader.equals("Planting Material/Method")) {
                    String plantingMethodName = nextLine[2];
                    if (plantingMethodName.isEmpty() == false) {
                        Couple pm = new Couple(nextLine[2], nextLine[1]);
                        plantingMethodsList.add(pm);
                    }
                }
            }
            
            //wthfile.initFertilizerMap(fertMethodVsCode);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Couple c = new Couple(nextLine[2], nextLine[1]);
             //       irrigationMethod.add(c);
        Collections.sort(plantingMethodsList, new CoupleCompare());
        Collections.sort(fertmethod, new CoupleCompare());
        Collections.sort(fertmaterial, new CoupleCompare());
        Collections.sort(irrigationMethod, new CoupleCompare());
        jComboBoxPlantingMethod.removeAllItems();
        for (Couple p : plantingMethodsList) {
            jComboBoxPlantingMethod.addItem(p.method);
        }

        Couple[] fertMatArray = fertmaterial.toArray(new Couple[0]);
        Couple[] fertMethArray = fertmethod.toArray(new Couple[0]);
        Couple[] irriMethArray = irrigationMethod.toArray(new Couple[0]);
        fertilizerTable.getColumn("Fertilization Material").setCellRenderer(new CellComboBoxRenderer(new Couple("", "").converToArray(fertMatArray)));
        fertilizerTable.getColumn("Fertilization Material").setCellEditor(new CellComboBoxEditor(new Couple("", "").converToArray(fertMatArray)));

        fertilizerTable.getColumn("Fertilization Method").setCellRenderer(new CellComboBoxRenderer(new Couple("", "").converToArray(fertMethArray)));
        fertilizerTable.getColumn("Fertilization Method").setCellEditor(new CellComboBoxEditor(new Couple("", "").converToArray(fertMethArray)));

        irrigationTable.getColumn("Date").setCellRenderer(new CellCalendarRenderer(new Date()));
        irrigationTable.getColumn("Date").setCellEditor(new JDateChooserCellEditor());
        irrigationTable.getColumn("Irrigation Method").setCellRenderer(new CellComboBoxRenderer(new Couple("", "").converToArray(irriMethArray)));
        irrigationTable.getColumn("Irrigation Method").setCellEditor(new CellComboBoxEditor(new Couple("", "").converToArray(irriMethArray)));
        irrigationTable.getColumn("Start Time(HH:MM)").setCellRenderer(new CellTimeRenderer(new Date()));
        irrigationTable.getColumn("Start Time(HH:MM)").setCellEditor(new CellTimeEditor());
        irrigationTable.getColumn("Delete").setCellRenderer(new CellButtonRenderer());
        irrigationTable.getColumn("Delete").setCellEditor(new CellButtonEditor(delete, irrigationTable));
        LOGGER.log(Level.ALL, "Initialized Fertilization Information");
    }

    private void initFrameSize() {
        // Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //this.setBounds(0,0,screenSize.width, screenSize.height);
        LOGGER.log(Level.ALL, "Initializing frame size.");
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        //this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.pack();
        LOGGER.log(Level.ALL, "Initialized frame size");
    }

    private void initTextFields() {
        //jTextFieldOrgName.setDocument(new LimitedPlainDocument(2));        
        //jTextFieldSiteIndex.setDocument(new LimitedPlainDocument(2));        
        Date date = new Date();
        jPlantingDateChooser.setDate(date);
        LOGGER.log(Level.ALL, "Initialized planting Date ....");
    }

    public void setSiteName(String siteName) {
        String[] str = siteName.split(";");
        jSiteNameText.setText(str[0]);
        jBlockNameText.setText(str[1]);
        jZoneNameText.setText(str[2]);
    }

    private void setIrrigationPanelData() {
        Date d = jPlantingDateChooser.getDate();
        irrigationDepthET.setText(jBedHeightET.getText());

        fertilizerTable.getColumn("Delete").setCellRenderer(new CellButtonRenderer());
        fertilizerTable.getColumn("Delete").setCellEditor(new CellButtonEditor(delete, fertilizerTable));

        fertilizerTable.getColumn("Fertilization Date").setCellRenderer(new CellCalendarRenderer(d));
        fertilizerTable.getColumn("Fertilization Date").setCellEditor(new JDateChooserCellEditor());

    }

    private void initCropInfo() {

        CSVReader reader;
        String[] nextLine;
        ArrayList<String> cropName = new ArrayList<String>();
        cropname_culfilename = new HashMap<String, String>();
        //String filepath = csvdirpath + dirseprator+"dssat_crop_lookup.csv";
        LOGGER.log(Level.ALL, "Initializing crop information.");
        try {
            reader = new CSVReader(new InputStreamReader(getClass().getResourceAsStream("/properties/csv/dssat_crop_lookup.csv")));
            while ((nextLine = reader.readNext()) != null) {
                cropName.add(nextLine[1]);
                cropname_culfilename.put(nextLine[1], nextLine[3]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Sort the arrayList and Initialize the comboboxes.
        Collections.sort(cropName);
        for (int i = 0; i < cropName.size(); i++) {
            jComboBoxPrevCropList.addItem(cropName.get(i));
            jComboBoxCropList.addItem(cropName.get(i));
        }

        LOGGER.log(Level.ALL, "Initialized crop information.");
        //Check for C://DSSAT46
        File f = new File("C:\\DSSAT46");
        if (f.exists()) {
            browse.setVisible(false);
            dssatPath.setVisible(false);
            dssatLabel.setVisible(false);
            mDSSATInstallation = "C:\\DSSAT46";
        }

        initCultivar();
    }

    public String getInstallation() {
        return mDSSATInstallation;
    }

    private void initCultivar() {
        // TODO add your handling code here:
        // TODO add your handling code here:

        LOGGER.log(Level.ALL, "Initializing cultivar information.");
        String cropName = (String) jComboBoxCropList.getSelectedItem();
        HashMap<String, String> cultivarHashMap = null;

        StringBuilder cultivadata = new StringBuilder();

        cultivadata.append("Crop Name,");
        cultivadata.append(cropName);

        cultivadata.append(",CultivarFile,");
        String culfileName = cropname_culfilename.get(cropName);
        cultivadata.append(culfileName);

        System.out.println("Crop Name" + cropName);

        if (cultivafile == null) {
            cultivafile = CultivaFileSystem.getInstance();
        }

        cultivafile.UpdateCache(cultivadata.toString());
        cultivarHashMap = cultivafile.ReadFromFile("VAR-NAME");

        ArrayList<String> cultivalist = new ArrayList<String>();
        Set keys = cultivarHashMap.keySet();
        Iterator itr = keys.iterator();

        String key;
        String value;
        while (itr.hasNext()) {
            key = (String) itr.next();
            value = cultivarHashMap.get(key);
            cultivalist.add(value);
        }
        Collections.sort(cultivalist);
        int itemCount = jComboBoxCultivar.getItemCount();

        for (int i = 0; i < itemCount; i++) {
            jComboBoxCultivar.removeItemAt(0);
        }

        for (int i = 0; i < cultivalist.size(); i++) {
            key = cultivalist.get(i);
            String cultivarname = key.trim();
            System.out.printf(cultivarname + "->");
            if (cultivarname.length() > 0) {
                jComboBoxCultivar.addItem(cultivarname);
            }
        }
        LOGGER.log(Level.ALL, "Initialized cultivar information.");
    }

    private void initSoilInfo() {

        //soil_db = new DBConnect (ServerDetails.SERVER_NUM_RW, ServerDetails.soil_dbname);
        LOGGER.log(Level.ALL, "Initializing soil information....");
        //CSVFileHandler csvfile = new CSVFileHandler ();

        // From the global db initialize the county comboBox
        ArrayList<String> soilList = null;
        ArrayList<String> countyList = null;
        countyList = mycsvfile.getCountyList_SoilDB();
        Collections.sort(countyList);

        /*for (int i = 0; i < countyList.size(); i++) {            
         String countyName = countyList.get(i);
         countyName = countyName.substring(0, 1).toUpperCase() + countyName.substring(1).toLowerCase(); 
         soilInfoCountyCombobox.addItem(countyName);     
         }*/
        // String countyName = (String) soilInfoCountyCombobox.getSelectedItem();
        // soilList = csvfile.getCountyBasedSoilList_SoilDB(countyList.get(0));
        String countName = (String) countyNameGlobal.getSelectedItem();
        soilList = mycsvfile.getCountyBasedSoilList_SoilDB(countName);

        for (int i = 0; i < soilList.size(); i++) {
            soilSeriesCombobox.addItem(soilList.get(i));
        }
        LOGGER.log(Level.ALL, "Initialized soil information....");

    }

    private String GenerateSoilData() {

        /*
         * Below are the steps to prepare the Soil File
         * 
         * 1. Get the Soil name from the soil Combobox
         * 2. Get the Soild ID 
         * 3. Need to open the FL.SOL file
         * 4. Read the soil file line by line and Find the SOIL Id section in the file. 
         * 5. Read the whole section till you reach the next Soil ID details. 
         * 6. Now you Open a file to write the data 
         * 7. Write the data to the New soild file. 
         * 8. Close teh file if you need to close the file. 	 * 
         * */
        //CSVFileHandler csvfile = new CSVFileHandler ();
        // Get soild name from the combo box
        String soilName = (String) soilSeriesCombobox.getSelectedItem();
        String countyName = (String) countyNameGlobal.getSelectedItem();
        // Pass the soild name to the method and get back the Soil Id
        String soilId = mycsvfile.getSoilid(countyName, soilName);
        SoilDataGenerator sdg = new SoilDataGenerator();
        
        String finalOutputPath = dssatPath.getText() + "\\"+jComboBoxCropList.getSelectedItem();
        
        return sdg.ReadSoilData(countyName, soilId, finalOutputPath);
        
    }

    private void initWeatherHistoric() {
        //weather_historic_daily = new DBConnect (ServerDetails.SERVER_NUM_RONLY, ServerDetails.weather_historic_daily_dbname);
    }

    private void initGlobalDBInfo() {
        LOGGER.log(Level.ALL, "Initializing Global Data information....");
        //CSVFileHandler csvfile = new CSVFileHandler ();
        // From the global db initialize the county comboBox
        ArrayList<String> countyList = null;
        countyList = mycsvfile.getCountyList_GlobalDB();
        if (countyList == null) {
            LOGGER.log(Level.ALL, "County List is not initialized. Exiting the application!");
            System.exit(0);
            return;
        }
        Collections.sort(countyList);
        for (int i = 0; i < countyList.size(); i++) {
            String countyName = countyList.get(i);
            countyName = countyName.substring(0, 1).toUpperCase() + countyName.substring(1).toLowerCase();

            countyNameGlobal.addItem(countyName);
        }

        LOGGER.log(Level.ALL, "Added the county names.");

        // By default get the first item in the list and get the Co-Ordinates of the at item and populate the 
        // weather stations based on the first item in the combobox 
        Location countypos = new Location();
        mycsvfile.getCountyLocation_GlobalDB(countyList.get(0), countypos);

        ArrayList<String> weatherstations = null;
        weatherstations = mycsvfile.getWeatherStations_GlobalDB(countypos);
        Collections.sort(weatherstations);

        int itemCount = weatherStComboBox.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            weatherStComboBox.removeItemAt(0);
        }

        System.out.println("Total Number of Fawn Weather Stations = " + weatherstations.size() + " for " + countyList.get(0));

        //weatherStComboBox.
        for (int i = 0; i < weatherstations.size(); i++) {

            String wtstationname = weatherstations.get(i);
            wtstationname = wtstationname.substring(0, 1).toUpperCase() + wtstationname.substring(1).toLowerCase();

            System.out.println(wtstationname);
            weatherStComboBox.addItem(wtstationname);
        }

        LOGGER.log(Level.ALL, "Initialized Fawn Weather stations.");
        LOGGER.log(Level.ALL, "Initialized Global Data information....");

    }

    //********Class to limit the number of characters a user can enter into a field.*********
    public class LimitedPlainDocument extends javax.swing.text.PlainDocument {

        private int maxLen = -1;

        /**
         * Creates a new instance of LimitedPlainDocument
         */
        public LimitedPlainDocument() {
        }

        public LimitedPlainDocument(int maxLen) {
            this.maxLen = maxLen;
        }

        public void insertString(int param, String str, javax.swing.text.AttributeSet attributeSet) throws javax.swing.text.BadLocationException {
            if (str != null && maxLen > 0 && this.getLength() + str.length() > maxLen) {
                java.awt.Toolkit.getDefaultToolkit().beep();
                return;
            }

            super.insertString(param, str, attributeSet);
        }

    }
//    public String getOrgName () {
//        return jTextFieldOrgName.getText();
//    }
//    public String getSiteIndex () {
//        return jTextFieldSiteIndex.getText();
//        
//    }

    public String getCountyName() {
        return (String) countyNameGlobal.getSelectedItem();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        irrigationGroup = new javax.swing.ButtonGroup();
        jSplitPane1 = new javax.swing.JSplitPane();
        jMainPanel = new javax.swing.JPanel();
        FieldPanel = new javax.swing.JPanel();
        GeneralInformation = new javax.swing.JPanel();
        jSiteNameLabel = new javax.swing.JLabel();
        jSiteNameText = new javax.swing.JTextField();
        jPlantingDateLabel = new javax.swing.JLabel();
        jPlantingDateChooser = new com.toedter.calendar.JDateChooser();
        jLocationLabel = new javax.swing.JLabel();
        jBlockNameLabel = new javax.swing.JLabel();
        jZoneNameLabel = new javax.swing.JLabel();
        jBlockNameText = new javax.swing.JTextField();
        jZoneNameText = new javax.swing.JTextField();
        jEditSaveButton = new javax.swing.JButton();
        dssatLabel = new javax.swing.JLabel();
        countyNameGlobal = new javax.swing.JComboBox();
        dssatPath = new javax.swing.JTextField();
        WeatherInfo = new javax.swing.JPanel();
        jWSLabel = new javax.swing.JLabel();
        weatherStComboBox = new javax.swing.JComboBox();
        jButtonOpenWeatherFile = new javax.swing.JButton();
        SoilInfo = new javax.swing.JPanel();
        jSoilLabel = new javax.swing.JLabel();
        soilSeriesCombobox = new javax.swing.JComboBox();
        jButtonDisplaySoilFile = new javax.swing.JButton();
        CropInfo = new javax.swing.JPanel();
        jPrevCropLabel = new javax.swing.JLabel();
        jCropNameLabel = new javax.swing.JLabel();
        jCultiVarLabel = new javax.swing.JLabel();
        jComboBoxPrevCropList = new javax.swing.JComboBox();
        jComboBoxCultivar = new javax.swing.JComboBox();
        jComboBoxCropList = new javax.swing.JComboBox();
        BedSystemInfo = new javax.swing.JPanel();
        jBedWidthLabel = new javax.swing.JLabel();
        jBedWidthET = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jPlasticLabel = new javax.swing.JLabel();
        jComboBoxPlastic = new javax.swing.JComboBox();
        jBedHeightLabel = new javax.swing.JLabel();
        jBedHeightET = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jComboBoxPlantingMethod = new javax.swing.JComboBox();
        jPlantingDepthEdit = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jPlantSpaceLabel = new javax.swing.JLabel();
        jPlantSapceEdit = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jRowSpacingLabel = new javax.swing.JLabel();
        jRowSpaceEdit = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jPlantSpaceLabel1 = new javax.swing.JLabel();
        jPlantingDepthLabel = new javax.swing.JLabel();
        jNextButton = new javax.swing.JButton();
        messageDisplayLabel = new javax.swing.JLabel();
        browse = new javax.swing.JButton();
        infoButtonField = new javax.swing.JButton();
        IrrigationFertilizerPanel = new javax.swing.JPanel();
        pIrrigationPanel = new javax.swing.JPanel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        dripRateET = new javax.swing.JTextField();
        jLabel51 = new javax.swing.JLabel();
        dripSpacingET = new javax.swing.JTextField();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        dripOffsetET = new javax.swing.JTextField();
        dripDepthET1 = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        irrigationDepthET = new javax.swing.JTextField();
        jLabel58 = new javax.swing.JLabel();
        irrigationTablePanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        irrigationTable = new javax.swing.JTable();
        irrigationLabel = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        fertilizerTable = new javax.swing.JTable();
        fertilizerLabel = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        warningMessaage = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        dripEfficiency = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jMsgLabelIrrigation = new javax.swing.JLabel();
        infoButtonField1 = new javax.swing.JButton();
        pButtonPanel = new javax.swing.JPanel();
        bFinishButton = new javax.swing.JButton();
        bBackButton = new javax.swing.JButton();
        bRunButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jProjectExplorer = new javax.swing.JTree();
        jMenuBarFields = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jOpenMenuItem = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItemSaveAs = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem5 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuFields = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuIrrigationFertilizer = new javax.swing.JMenu();
        jMenuItemGoToIrrigationFertilizer = new javax.swing.JMenuItem();
        jMenuTools = new javax.swing.JMenu();
        jMenuItemOptions = new javax.swing.JMenuItem();
        jMenuRun = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("DSSAT");

        jSplitPane1.setDividerLocation(150);
        jSplitPane1.setAutoscrolls(true);
        jSplitPane1.setOneTouchExpandable(true);
        jSplitPane1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jSplitPane1MouseDragged(evt);
            }
        });

        jMainPanel.setAutoscrolls(true);

        FieldPanel.setAutoscrolls(true);
        FieldPanel.setPreferredSize(new java.awt.Dimension(783, 750));

        jSiteNameLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jSiteNameLabel.setText("Farm Name*");

        jSiteNameText.setBackground(new java.awt.Color(204, 204, 255));
        jSiteNameText.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jSiteNameText.setMaximumSize(new java.awt.Dimension(115, 26));
        jSiteNameText.setMinimumSize(new java.awt.Dimension(115, 26));
        jSiteNameText.setPreferredSize(new java.awt.Dimension(115, 26));
        jSiteNameText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSiteNameTextActionPerformed(evt);
            }
        });
        jSiteNameText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jSiteNameTextKeyTyped(evt);
            }
        });

        jPlantingDateLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPlantingDateLabel.setText("Planting Date");

        jPlantingDateChooser.setDateFormatString("MMM, dd, yyyy");
        jPlantingDateChooser.setMinimumSize(new java.awt.Dimension(235, 26));
        jPlantingDateChooser.setPreferredSize(new java.awt.Dimension(235, 26));

        jLocationLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLocationLabel.setText("Your Location");

        jBlockNameLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jBlockNameLabel.setText("Block Name*");

        jZoneNameLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jZoneNameLabel.setText("Zone*");

        jBlockNameText.setEditable(false);
        jBlockNameText.setBackground(new java.awt.Color(204, 204, 255));
        jBlockNameText.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jZoneNameText.setEditable(false);
        jZoneNameText.setBackground(new java.awt.Color(204, 204, 255));
        jZoneNameText.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jZoneNameText.setMaximumSize(new java.awt.Dimension(115, 26));
        jZoneNameText.setMinimumSize(new java.awt.Dimension(115, 26));
        jZoneNameText.setPreferredSize(new java.awt.Dimension(115, 26));

        jEditSaveButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jEditSaveButton.setText("Edit Block/Site");
        jEditSaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jEditSaveButtonActionPerformed(evt);
            }
        });

        dssatLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        dssatLabel.setText("DSSAT Installation Folder");

        countyNameGlobal.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        countyNameGlobal.setMaximumSize(new java.awt.Dimension(235, 26));
        countyNameGlobal.setMinimumSize(new java.awt.Dimension(235, 26));
        countyNameGlobal.setPreferredSize(new java.awt.Dimension(235, 26));
        countyNameGlobal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                countyNameGlobalActionPerformed(evt);
            }
        });

        dssatPath.setEditable(false);
        dssatPath.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        dssatPath.setMaximumSize(new java.awt.Dimension(235, 26));
        dssatPath.setMinimumSize(new java.awt.Dimension(235, 26));
        dssatPath.setPreferredSize(new java.awt.Dimension(235, 26));

        javax.swing.GroupLayout GeneralInformationLayout = new javax.swing.GroupLayout(GeneralInformation);
        GeneralInformation.setLayout(GeneralInformationLayout);
        GeneralInformationLayout.setHorizontalGroup(
            GeneralInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(GeneralInformationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(GeneralInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSiteNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jZoneNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBlockNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(GeneralInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSiteNameText, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBlockNameText, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jZoneNameText, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jEditSaveButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 74, Short.MAX_VALUE)
                .addGroup(GeneralInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(GeneralInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPlantingDateLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLocationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(dssatLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
                .addGroup(GeneralInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(dssatPath, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPlantingDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(countyNameGlobal, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        GeneralInformationLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLocationLabel, jPlantingDateLabel});

        GeneralInformationLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jBlockNameLabel, jSiteNameLabel, jZoneNameLabel});

        GeneralInformationLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jBlockNameText, jSiteNameText, jZoneNameText});

        GeneralInformationLayout.setVerticalGroup(
            GeneralInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(GeneralInformationLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(GeneralInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPlantingDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(GeneralInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jSiteNameLabel)
                        .addComponent(jSiteNameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPlantingDateLabel)
                        .addComponent(jEditSaveButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(GeneralInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(GeneralInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jBlockNameLabel)
                        .addComponent(jBlockNameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(GeneralInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(countyNameGlobal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLocationLabel)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(GeneralInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(GeneralInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jZoneNameLabel)
                        .addComponent(jZoneNameText, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(dssatLabel)
                    .addComponent(dssatPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        GeneralInformationLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBlockNameLabel, jSiteNameLabel, jZoneNameLabel});

        GeneralInformationLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jBlockNameText, jSiteNameText, jZoneNameText});

        WeatherInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Weather Station", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 3, 16), new java.awt.Color(0, 51, 204))); // NOI18N
        WeatherInfo.setPreferredSize(new java.awt.Dimension(620, 193));

        jWSLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jWSLabel.setText("Nearest FAWN Weather Station");
        jWSLabel.setToolTipText("");

        weatherStComboBox.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        weatherStComboBox.setMaximumSize(new java.awt.Dimension(235, 26));
        weatherStComboBox.setMinimumSize(new java.awt.Dimension(235, 26));
        weatherStComboBox.setPreferredSize(new java.awt.Dimension(235, 26));
        weatherStComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                weatherStComboBoxActionPerformed(evt);
            }
        });

        jButtonOpenWeatherFile.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jButtonOpenWeatherFile.setText("View Weather File");
        jButtonOpenWeatherFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOpenWeatherFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout WeatherInfoLayout = new javax.swing.GroupLayout(WeatherInfo);
        WeatherInfo.setLayout(WeatherInfoLayout);
        WeatherInfoLayout.setHorizontalGroup(
            WeatherInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(WeatherInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(WeatherInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(WeatherInfoLayout.createSequentialGroup()
                        .addComponent(jWSLabel)
                        .addGap(44, 44, 44)
                        .addComponent(weatherStComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButtonOpenWeatherFile))
                .addContainerGap(84, Short.MAX_VALUE))
        );
        WeatherInfoLayout.setVerticalGroup(
            WeatherInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(WeatherInfoLayout.createSequentialGroup()
                .addGroup(WeatherInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(weatherStComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jWSLabel))
                .addGap(18, 18, 18)
                .addComponent(jButtonOpenWeatherFile)
                .addContainerGap())
        );

        SoilInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Soil Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 3, 16), new java.awt.Color(0, 51, 204))); // NOI18N
        SoilInfo.setPreferredSize(new java.awt.Dimension(620, 193));

        jSoilLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jSoilLabel.setText("Soil Series Name");

        soilSeriesCombobox.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        soilSeriesCombobox.setMaximumSize(new java.awt.Dimension(235, 26));
        soilSeriesCombobox.setMinimumSize(new java.awt.Dimension(235, 26));
        soilSeriesCombobox.setPreferredSize(new java.awt.Dimension(235, 26));

        jButtonDisplaySoilFile.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jButtonDisplaySoilFile.setText("View Soil File");
        jButtonDisplaySoilFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDisplaySoilFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout SoilInfoLayout = new javax.swing.GroupLayout(SoilInfo);
        SoilInfo.setLayout(SoilInfoLayout);
        SoilInfoLayout.setHorizontalGroup(
            SoilInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SoilInfoLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(SoilInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonDisplaySoilFile)
                    .addGroup(SoilInfoLayout.createSequentialGroup()
                        .addComponent(jSoilLabel)
                        .addGap(18, 18, 18)
                        .addComponent(soilSeriesCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        SoilInfoLayout.setVerticalGroup(
            SoilInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SoilInfoLayout.createSequentialGroup()
                .addGroup(SoilInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(soilSeriesCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSoilLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 99, Short.MAX_VALUE)
                .addComponent(jButtonDisplaySoilFile)
                .addGap(23, 23, 23))
        );

        CropInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Crop", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 3, 16), new java.awt.Color(0, 51, 204))); // NOI18N
        CropInfo.setPreferredSize(new java.awt.Dimension(620, 193));

        jPrevCropLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPrevCropLabel.setText("Previous Crop From List");

        jCropNameLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jCropNameLabel.setText("Crop Name");

        jCultiVarLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jCultiVarLabel.setText("Cultivar");

        jComboBoxPrevCropList.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jComboBoxPrevCropList.setMaximumSize(new java.awt.Dimension(235, 26));
        jComboBoxPrevCropList.setMinimumSize(new java.awt.Dimension(235, 26));
        jComboBoxPrevCropList.setPreferredSize(new java.awt.Dimension(235, 26));

        jComboBoxCultivar.setEditable(true);
        jComboBoxCultivar.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jComboBoxCultivar.setMaximumSize(new java.awt.Dimension(235, 26));
        jComboBoxCultivar.setMinimumSize(new java.awt.Dimension(235, 26));
        jComboBoxCultivar.setPreferredSize(new java.awt.Dimension(235, 26));

        jComboBoxCropList.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jComboBoxCropList.setMaximumSize(new java.awt.Dimension(235, 26));
        jComboBoxCropList.setMinimumSize(new java.awt.Dimension(235, 26));
        jComboBoxCropList.setPreferredSize(new java.awt.Dimension(235, 26));
        jComboBoxCropList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxCropListItemStateChanged(evt);
            }
        });
        jComboBoxCropList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxCropListActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout CropInfoLayout = new javax.swing.GroupLayout(CropInfo);
        CropInfo.setLayout(CropInfoLayout);
        CropInfoLayout.setHorizontalGroup(
            CropInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CropInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(CropInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPrevCropLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCropNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCultiVarLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
                .addGroup(CropInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBoxCropList, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBoxPrevCropList, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBoxCultivar, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(134, Short.MAX_VALUE))
        );

        CropInfoLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jCropNameLabel, jCultiVarLabel, jPrevCropLabel});

        CropInfoLayout.setVerticalGroup(
            CropInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CropInfoLayout.createSequentialGroup()
                .addGroup(CropInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPrevCropLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBoxPrevCropList, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(CropInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxCropList, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCropNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(CropInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCultiVarLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxCultivar, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        CropInfoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jComboBoxCropList, jComboBoxCultivar, jComboBoxPrevCropList, jCropNameLabel, jCultiVarLabel, jPrevCropLabel});

        BedSystemInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Bed System", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 3, 16), new java.awt.Color(0, 51, 204))); // NOI18N
        BedSystemInfo.setPreferredSize(new java.awt.Dimension(620, 193));

        jBedWidthLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jBedWidthLabel.setText("Bed Width*");
        jBedWidthLabel.setToolTipText("Valid Bed Width 8-36 inches.");
        jBedWidthLabel.setMaximumSize(new java.awt.Dimension(160, 26));
        jBedWidthLabel.setMinimumSize(new java.awt.Dimension(160, 26));
        jBedWidthLabel.setPreferredSize(new java.awt.Dimension(160, 26));

        jBedWidthET.setColumns(10);
        jBedWidthET.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jBedWidthET.setToolTipText("Valid Bed Width 8-36 inches.");
        jBedWidthET.setMaximumSize(new java.awt.Dimension(160, 26));
        jBedWidthET.setMinimumSize(new java.awt.Dimension(160, 26));
        jBedWidthET.setPreferredSize(new java.awt.Dimension(160, 26));
        jBedWidthET.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jBedWidthETKeyTyped(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        jLabel24.setText("Inches (8-36)");
        jLabel24.setToolTipText("");

        jPlasticLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPlasticLabel.setText("Plastic Mulch Color*");
        jPlasticLabel.setMaximumSize(new java.awt.Dimension(106, 26));
        jPlasticLabel.setMinimumSize(new java.awt.Dimension(106, 26));
        jPlasticLabel.setPreferredSize(new java.awt.Dimension(106, 26));

        jComboBoxPlastic.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jComboBoxPlastic.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None", "Black", "Silver", "White", " " }));
        jComboBoxPlastic.setMaximumSize(new java.awt.Dimension(160, 26));
        jComboBoxPlastic.setMinimumSize(new java.awt.Dimension(160, 26));
        jComboBoxPlastic.setPreferredSize(new java.awt.Dimension(160, 26));

        jBedHeightLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jBedHeightLabel.setText("Bed Height*");
        jBedHeightLabel.setToolTipText("Valid Bed Hight 4-12 inches.");
        jBedHeightLabel.setPreferredSize(new java.awt.Dimension(160, 26));

        jBedHeightET.setColumns(10);
        jBedHeightET.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jBedHeightET.setToolTipText("Valid Bed Hight 4-12 inches.");
        jBedHeightET.setMaximumSize(new java.awt.Dimension(160, 26));
        jBedHeightET.setMinimumSize(new java.awt.Dimension(160, 26));
        jBedHeightET.setPreferredSize(new java.awt.Dimension(160, 26));
        jBedHeightET.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jBedHeightETKeyTyped(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        jLabel29.setText("Inches (4-12)");

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("-----Planting-----");

        jComboBoxPlantingMethod.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jComboBoxPlantingMethod.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " " }));
        jComboBoxPlantingMethod.setMaximumSize(new java.awt.Dimension(160, 26));
        jComboBoxPlantingMethod.setMinimumSize(new java.awt.Dimension(160, 26));
        jComboBoxPlantingMethod.setPreferredSize(new java.awt.Dimension(160, 26));
        jComboBoxPlantingMethod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxPlantingMethodActionPerformed(evt);
            }
        });

        jPlantingDepthEdit.setColumns(5);
        jPlantingDepthEdit.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPlantingDepthEdit.setToolTipText("Valid Planting Depth 0-6 inches.");
        jPlantingDepthEdit.setMaximumSize(new java.awt.Dimension(160, 26));
        jPlantingDepthEdit.setMinimumSize(new java.awt.Dimension(160, 26));
        jPlantingDepthEdit.setPreferredSize(new java.awt.Dimension(160, 26));

        jLabel20.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        jLabel20.setText("Inches (0-6)");

        jPlantSpaceLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPlantSpaceLabel.setText("Planting Spacing in Row*");
        jPlantSpaceLabel.setToolTipText("Valid Planting Spacing is 4-30 inches");
        jPlantSpaceLabel.setMaximumSize(new java.awt.Dimension(160, 26));
        jPlantSpaceLabel.setMinimumSize(new java.awt.Dimension(160, 26));
        jPlantSpaceLabel.setPreferredSize(new java.awt.Dimension(160, 26));

        jPlantSapceEdit.setColumns(5);
        jPlantSapceEdit.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPlantSapceEdit.setToolTipText("Valid Planting Spacing 4-30 inches.");
        jPlantSapceEdit.setMaximumSize(new java.awt.Dimension(160, 26));
        jPlantSapceEdit.setMinimumSize(new java.awt.Dimension(160, 26));
        jPlantSapceEdit.setPreferredSize(new java.awt.Dimension(160, 26));

        jLabel19.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        jLabel19.setText("Inches (4-30)");

        jRowSpacingLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jRowSpacingLabel.setText("Row Spacing*");
        jRowSpacingLabel.setToolTipText("Valid Row Spacing 4-7 feet.");
        jRowSpacingLabel.setMaximumSize(new java.awt.Dimension(106, 26));
        jRowSpacingLabel.setMinimumSize(new java.awt.Dimension(106, 26));
        jRowSpacingLabel.setPreferredSize(new java.awt.Dimension(106, 26));

        jRowSpaceEdit.setColumns(5);
        jRowSpaceEdit.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jRowSpaceEdit.setToolTipText("Valid Row Spacing 4-7 feet.");
        jRowSpaceEdit.setMaximumSize(new java.awt.Dimension(160, 26));
        jRowSpaceEdit.setMinimumSize(new java.awt.Dimension(160, 26));
        jRowSpaceEdit.setPreferredSize(new java.awt.Dimension(160, 26));
        jRowSpaceEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRowSpaceEditActionPerformed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        jLabel21.setText("Feet (4-7)");

        jPlantSpaceLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPlantSpaceLabel1.setText("Planting Depth*");
        jPlantSpaceLabel1.setToolTipText("Valid Planting Depth 0-6 inches.");
        jPlantSpaceLabel1.setMaximumSize(new java.awt.Dimension(160, 26));
        jPlantSpaceLabel1.setMinimumSize(new java.awt.Dimension(160, 26));
        jPlantSpaceLabel1.setPreferredSize(new java.awt.Dimension(160, 26));

        jPlantingDepthLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPlantingDepthLabel.setText("Planting Method*");
        jPlantingDepthLabel.setMaximumSize(new java.awt.Dimension(160, 26));
        jPlantingDepthLabel.setMinimumSize(new java.awt.Dimension(160, 26));
        jPlantingDepthLabel.setPreferredSize(new java.awt.Dimension(160, 26));

        javax.swing.GroupLayout BedSystemInfoLayout = new javax.swing.GroupLayout(BedSystemInfo);
        BedSystemInfo.setLayout(BedSystemInfoLayout);
        BedSystemInfoLayout.setHorizontalGroup(
            BedSystemInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BedSystemInfoLayout.createSequentialGroup()
                .addGroup(BedSystemInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(BedSystemInfoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(BedSystemInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jBedHeightLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jBedWidthLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPlantSpaceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPlasticLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPlantSpaceLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPlantingDepthLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jRowSpacingLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(59, 59, 59)
                        .addGroup(BedSystemInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jPlantingDepthEdit, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPlantSapceEdit, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBoxPlastic, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jRowSpaceEdit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jBedHeightET, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jBedWidthET, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBoxPlantingMethod, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(28, 28, 28)
                        .addGroup(BedSystemInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
                            .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(BedSystemInfoLayout.createSequentialGroup()
                        .addGap(130, 130, 130)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        BedSystemInfoLayout.setVerticalGroup(
            BedSystemInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(BedSystemInfoLayout.createSequentialGroup()
                .addGroup(BedSystemInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(BedSystemInfoLayout.createSequentialGroup()
                        .addGroup(BedSystemInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(BedSystemInfoLayout.createSequentialGroup()
                                .addGroup(BedSystemInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jBedWidthLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jBedWidthET, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(BedSystemInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jBedHeightLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jBedHeightET, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BedSystemInfoLayout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(BedSystemInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(BedSystemInfoLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(BedSystemInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRowSpaceEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jRowSpacingLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BedSystemInfoLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(BedSystemInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPlasticLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxPlastic, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(BedSystemInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jPlantSpaceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPlantSapceEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
                .addGroup(BedSystemInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(BedSystemInfoLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(124, 124, 124))
                    .addGroup(BedSystemInfoLayout.createSequentialGroup()
                        .addGroup(BedSystemInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPlantingDepthEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(BedSystemInfoLayout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addComponent(jPlantSpaceLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(BedSystemInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxPlantingMethod, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPlantingDepthLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        BedSystemInfoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jComboBoxPlantingMethod, jPlantSapceEdit, jPlantSpaceLabel, jPlantingDepthEdit});

        jNextButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jNextButton.setText("Next");
        jNextButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jNextButtonMouseClicked(evt);
            }
        });
        jNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNextButtonActionPerformed(evt);
            }
        });

        messageDisplayLabel.setForeground(new java.awt.Color(255, 51, 51));

        browse.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        browse.setText("Browse");
        browse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseActionPerformed(evt);
            }
        });

        infoButtonField.setFont(new java.awt.Font("Yu Gothic Light", 2, 18)); // NOI18N
        infoButtonField.setText("info");
        infoButtonField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoButtonFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout FieldPanelLayout = new javax.swing.GroupLayout(FieldPanel);
        FieldPanel.setLayout(FieldPanelLayout);
        FieldPanelLayout.setHorizontalGroup(
            FieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FieldPanelLayout.createSequentialGroup()
                .addGroup(FieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(FieldPanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(WeatherInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 562, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FieldPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(CropInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 562, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(FieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BedSystemInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 859, Short.MAX_VALUE)
                    .addComponent(SoilInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 859, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(FieldPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(GeneralInformation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(FieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(FieldPanelLayout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addComponent(messageDisplayLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 451, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(FieldPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(FieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(browse)
                            .addComponent(infoButtonField))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FieldPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jNextButton)
                .addGap(799, 799, 799))
        );
        FieldPanelLayout.setVerticalGroup(
            FieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FieldPanelLayout.createSequentialGroup()
                .addGroup(FieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(FieldPanelLayout.createSequentialGroup()
                        .addComponent(GeneralInformation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FieldPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(infoButtonField, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browse)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(messageDisplayLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)))
                .addGroup(FieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(SoilInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(WeatherInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                .addGap(56, 56, 56)
                .addGroup(FieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BedSystemInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                    .addComponent(CropInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jNextButton))
        );

        pIrrigationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Irrigation", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 24), new java.awt.Color(0, 0, 255))); // NOI18N
        pIrrigationPanel.setName("Dripper Information"); // NOI18N

        jLabel49.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel49.setText("Dripper Rate");
        jLabel49.setToolTipText("Valid Irrigation rate 0.3-3 ml/sec.");
        jLabel49.setMaximumSize(new java.awt.Dimension(258, 26));
        jLabel49.setMinimumSize(new java.awt.Dimension(258, 26));
        jLabel49.setPreferredSize(new java.awt.Dimension(258, 26));

        jLabel50.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        jLabel50.setText("ml/sec (0.3-3)");

        dripRateET.setToolTipText("Valid Irrigation rate 0.3-3 ml/sec.");
        dripRateET.setMaximumSize(new java.awt.Dimension(160, 26));
        dripRateET.setMinimumSize(new java.awt.Dimension(160, 26));

        jLabel51.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel51.setText("Drip Emitter Spacing");
        jLabel51.setToolTipText("Valid Emitter Spacing");
        jLabel51.setMaximumSize(new java.awt.Dimension(258, 26));
        jLabel51.setMinimumSize(new java.awt.Dimension(258, 26));
        jLabel51.setPreferredSize(new java.awt.Dimension(258, 26));

        dripSpacingET.setToolTipText("Valid Emitter Spacing");
        dripSpacingET.setMaximumSize(new java.awt.Dimension(160, 26));
        dripSpacingET.setMinimumSize(new java.awt.Dimension(160, 26));

        jLabel52.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        jLabel52.setText("Inches");

        jLabel53.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel53.setText("Dripper Emitter Offset  From Bed Center Line");
        jLabel53.setToolTipText("Valid Dripper Emitter Offset 0-8 inches or less than half of Bed Width.");
        jLabel53.setMaximumSize(new java.awt.Dimension(258, 26));
        jLabel53.setMinimumSize(new java.awt.Dimension(258, 26));
        jLabel53.setPreferredSize(new java.awt.Dimension(258, 26));

        jLabel54.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel54.setText("Dripper Emitter depth from Surface");
        jLabel54.setToolTipText("Valid Dripper Emitter depth from surface is 0-8 inches.");
        jLabel54.setMaximumSize(new java.awt.Dimension(258, 26));
        jLabel54.setMinimumSize(new java.awt.Dimension(258, 26));
        jLabel54.setPreferredSize(new java.awt.Dimension(258, 26));

        dripOffsetET.setToolTipText("Valid Dripper Emitter Offset 0-8 inches or less than half of Bed Width.");
        dripOffsetET.setMaximumSize(new java.awt.Dimension(160, 26));
        dripOffsetET.setMinimumSize(new java.awt.Dimension(160, 26));

        dripDepthET1.setToolTipText("Valid Dripper Emitter depth from surface is 0-8 inches.");
        dripDepthET1.setMaximumSize(new java.awt.Dimension(160, 26));
        dripDepthET1.setMinimumSize(new java.awt.Dimension(160, 26));

        jLabel55.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        jLabel55.setText("Inches (0-8)");

        jLabel56.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        jLabel56.setText("Inches (0-8)");

        jLabel57.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        jLabel57.setText("Inches (6-8)");

        irrigationDepthET.setToolTipText("Valid Irrigation depth 6-18 inches.");
        irrigationDepthET.setMaximumSize(new java.awt.Dimension(160, 26));
        irrigationDepthET.setMinimumSize(new java.awt.Dimension(160, 26));
        irrigationDepthET.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                irrigationDepthETActionPerformed(evt);
            }
        });

        jLabel58.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel58.setText("Irrigation Depth");
        jLabel58.setToolTipText("Valid Irrigation depth 6-18 inches.");
        jLabel58.setMaximumSize(new java.awt.Dimension(258, 26));
        jLabel58.setMinimumSize(new java.awt.Dimension(258, 26));
        jLabel58.setPreferredSize(new java.awt.Dimension(258, 26));

        irrigationTable.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        irrigationTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Date", "Irrigation Method", "Water Table Depth", "Start Time(HH:MM)", "Duration(min)", "Interval(min)", "Event Times", "Delete"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        irrigationTable.setPreferredSize(new java.awt.Dimension(452, 113));
        irrigationTable.setRowHeight(30);
        irrigationTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                irrigationTableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(irrigationTable);
        if (irrigationTable.getColumnModel().getColumnCount() > 0) {
            irrigationTable.getColumnModel().getColumn(0).setPreferredWidth(100);
            irrigationTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        }

        javax.swing.GroupLayout irrigationTablePanelLayout = new javax.swing.GroupLayout(irrigationTablePanel);
        irrigationTablePanel.setLayout(irrigationTablePanelLayout);
        irrigationTablePanelLayout.setHorizontalGroup(
            irrigationTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
        );
        irrigationTablePanelLayout.setVerticalGroup(
            irrigationTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(irrigationTablePanelLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        irrigationLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        irrigationLabel.setForeground(new java.awt.Color(51, 0, 204));
        irrigationLabel.setText("Click \"Add Row\" Button to start inserting row in the table");

        jButton3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton3.setText("Add Row");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        irrigationGroup.add(jRadioButton1);
        jRadioButton1.setText("Auto Irrigation");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        irrigationGroup.add(jRadioButton2);
        jRadioButton2.setSelected(true);
        jRadioButton2.setText("Scheduled Irrigation");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        fertilizerTable.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        fertilizerTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Fertilization Date", "Fertilization Material", "Fertilization Method", "Fertilizer Depth(inches)", "Rate Per Application", "Delete"
            }
        ));
        fertilizerTable.setMinimumSize(new java.awt.Dimension(105, 0));
        fertilizerTable.setPreferredSize(new java.awt.Dimension(452, 113));
        fertilizerTable.setRowHeight(30);
        fertilizerTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fertilizerTableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(fertilizerTable);
        if (fertilizerTable.getColumnModel().getColumnCount() > 0) {
            fertilizerTable.getColumnModel().getColumn(5).setResizable(false);
            fertilizerTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        }

        fertilizerLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        fertilizerLabel.setForeground(new java.awt.Color(0, 0, 204));
        fertilizerLabel.setText("Click \"Add Row\" Button to start inserting row in the table");

        jButton1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton1.setText("Add Row");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        warningMessaage.setForeground(new java.awt.Color(255, 0, 0));

        jLabel59.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        jLabel59.setText("in % (3-99)");

        dripEfficiency.setToolTipText("Valid Dripper efficiency 3%-99%.");
        dripEfficiency.setMaximumSize(new java.awt.Dimension(160, 26));
        dripEfficiency.setMinimumSize(new java.awt.Dimension(160, 26));

        jLabel1.setText("Dripper Efficieny");
        jLabel1.setToolTipText("Valid Dripper efficiency 3%-99%.");
        jLabel1.setMaximumSize(new java.awt.Dimension(258, 26));
        jLabel1.setPreferredSize(new java.awt.Dimension(93, 26));

        jMsgLabelIrrigation.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        infoButtonField1.setFont(new java.awt.Font("Yu Gothic Light", 2, 18)); // NOI18N
        infoButtonField1.setText("info");
        infoButtonField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoButtonField1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pIrrigationPanelLayout = new javax.swing.GroupLayout(pIrrigationPanel);
        pIrrigationPanel.setLayout(pIrrigationPanelLayout);
        pIrrigationPanelLayout.setHorizontalGroup(
            pIrrigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pIrrigationPanelLayout.createSequentialGroup()
                .addGroup(pIrrigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addComponent(irrigationTablePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pIrrigationPanelLayout.createSequentialGroup()
                        .addGroup(pIrrigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pIrrigationPanelLayout.createSequentialGroup()
                                .addComponent(jButton3)
                                .addGap(27, 27, 27)
                                .addComponent(irrigationLabel))
                            .addGroup(pIrrigationPanelLayout.createSequentialGroup()
                                .addGroup(pIrrigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pIrrigationPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(dripEfficiency, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pIrrigationPanelLayout.createSequentialGroup()
                                        .addGroup(pIrrigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel53, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                                            .addComponent(jLabel54, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(142, 142, 142)
                                        .addGroup(pIrrigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(dripDepthET1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(irrigationDepthET, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(dripOffsetET, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(dripSpacingET, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(dripRateET, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(28, 28, 28)
                                .addGroup(pIrrigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pIrrigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel52, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                                        .addComponent(jLabel55, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel57, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel56, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel59, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(244, 244, 244))
                    .addGroup(pIrrigationPanelLayout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(18, 18, 18)
                        .addComponent(fertilizerLabel))
                    .addComponent(warningMessaage, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pIrrigationPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jRadioButton1)
                        .addGap(18, 18, 18)
                        .addComponent(jRadioButton2)
                        .addGap(18, 18, 18)
                        .addComponent(jMsgLabelIrrigation, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(74, 74, 74)
                        .addComponent(infoButtonField1)))
                .addContainerGap())
        );
        pIrrigationPanelLayout.setVerticalGroup(
            pIrrigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pIrrigationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pIrrigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pIrrigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jRadioButton1)
                        .addComponent(jRadioButton2))
                    .addGroup(pIrrigationPanelLayout.createSequentialGroup()
                        .addGroup(pIrrigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(infoButtonField1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jMsgLabelIrrigation, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)))
                .addGroup(pIrrigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dripRateET, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel50))
                .addGap(18, 18, 18)
                .addGroup(pIrrigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dripSpacingET, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel52))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pIrrigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dripOffsetET, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel53, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel55))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pIrrigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel54, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dripDepthET1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel56))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pIrrigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dripEfficiency, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pIrrigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(irrigationDepthET, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel57))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pIrrigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(irrigationLabel))
                .addGap(18, 18, 18)
                .addComponent(irrigationTablePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(warningMessaage, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pIrrigationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(fertilizerLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45))
            .addGroup(pIrrigationPanelLayout.createSequentialGroup()
                .addGap(228, 228, 228)
                .addComponent(jLabel59, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(536, Short.MAX_VALUE))
        );

        bFinishButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        bFinishButton.setText("Finish");
        bFinishButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bFinishButtonActionPerformed(evt);
            }
        });

        bBackButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        bBackButton.setText("Back");
        bBackButton.setMaximumSize(new java.awt.Dimension(67, 25));
        bBackButton.setMinimumSize(new java.awt.Dimension(67, 25));
        bBackButton.setPreferredSize(new java.awt.Dimension(67, 25));
        bBackButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bBackButtonMouseClicked(evt);
            }
        });

        bRunButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        bRunButton.setText("Run");
        bRunButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRunButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pButtonPanelLayout = new javax.swing.GroupLayout(pButtonPanel);
        pButtonPanel.setLayout(pButtonPanelLayout);
        pButtonPanelLayout.setHorizontalGroup(
            pButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pButtonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bBackButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(bFinishButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bRunButton)
                .addContainerGap(22, Short.MAX_VALUE))
        );
        pButtonPanelLayout.setVerticalGroup(
            pButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pButtonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bBackButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bFinishButton)
                    .addComponent(bRunButton))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout IrrigationFertilizerPanelLayout = new javax.swing.GroupLayout(IrrigationFertilizerPanel);
        IrrigationFertilizerPanel.setLayout(IrrigationFertilizerPanelLayout);
        IrrigationFertilizerPanelLayout.setHorizontalGroup(
            IrrigationFertilizerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(IrrigationFertilizerPanelLayout.createSequentialGroup()
                .addGroup(IrrigationFertilizerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(IrrigationFertilizerPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pIrrigationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(IrrigationFertilizerPanelLayout.createSequentialGroup()
                        .addGap(645, 645, 645)
                        .addComponent(pButtonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(68, 68, 68)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        IrrigationFertilizerPanelLayout.setVerticalGroup(
            IrrigationFertilizerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(IrrigationFertilizerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(IrrigationFertilizerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pIrrigationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(pButtonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pIrrigationPanel.getAccessibleContext().setAccessibleName("Irrigation and Fertilizer");

        javax.swing.GroupLayout jMainPanelLayout = new javax.swing.GroupLayout(jMainPanel);
        jMainPanel.setLayout(jMainPanelLayout);
        jMainPanelLayout.setHorizontalGroup(
            jMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jMainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(FieldPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1452, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jMainPanelLayout.createSequentialGroup()
                    .addGap(18, 18, 18)
                    .addComponent(IrrigationFertilizerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(18, 18, 18)))
        );
        jMainPanelLayout.setVerticalGroup(
            jMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jMainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(FieldPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 915, Short.MAX_VALUE)
                .addContainerGap(19, Short.MAX_VALUE))
            .addGroup(jMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jMainPanelLayout.createSequentialGroup()
                    .addGap(28, 28, 28)
                    .addComponent(IrrigationFertilizerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jSplitPane1.setRightComponent(jMainPanel);

        jProjectExplorer.setBackground(new java.awt.Color(240, 240, 240));
        jProjectExplorer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jProjectExplorer.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        jProjectExplorer.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jProjectExplorer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jProjectExplorerMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(jProjectExplorer);

        jSplitPane1.setLeftComponent(jScrollPane1);

        jMenuFile.setText("File");
        jMenuFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuFileActionPerformed(evt);
            }
        });

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("New Site");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItem2);

        jOpenMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jOpenMenuItem.setText("Open Site");
        jOpenMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOpenMenuItemActionPerformed(evt);
            }
        });
        jMenuFile.add(jOpenMenuItem);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setText("Save Site");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItem4);

        jMenuItemSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemSaveAs.setText("Save Site As");
        jMenuItemSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveAsActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSaveAs);
        jMenuFile.add(jSeparator1);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem5.setText("Close Site");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItem5);
        jMenuFile.add(jSeparator2);

        jMenuItemExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_MASK));
        jMenuItemExit.setText("Exit");
        jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemExit);

        jMenuBarFields.add(jMenuFile);

        jMenuFields.setText("Fields");
        jMenuFields.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuFieldsActionPerformed(evt);
            }
        });

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem6.setText("Go to Fields");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenuFields.add(jMenuItem6);

        jMenuBarFields.add(jMenuFields);

        jMenuIrrigationFertilizer.setText("Irrigation/Fertilizer");
        jMenuIrrigationFertilizer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuIrrigationFertilizerActionPerformed(evt);
            }
        });

        jMenuItemGoToIrrigationFertilizer.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemGoToIrrigationFertilizer.setText("Go to Irrigation/Fertilizer");
        jMenuItemGoToIrrigationFertilizer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGoToIrrigationFertilizerActionPerformed(evt);
            }
        });
        jMenuIrrigationFertilizer.add(jMenuItemGoToIrrigationFertilizer);

        jMenuBarFields.add(jMenuIrrigationFertilizer);

        jMenuTools.setText("Tools");

        jMenuItemOptions.setText("Options");
        jMenuTools.add(jMenuItemOptions);

        jMenuBarFields.add(jMenuTools);

        jMenuRun.setText("Run");

        jMenuItem1.setText("View Weather File");
        jMenuRun.add(jMenuItem1);

        jMenuBarFields.add(jMenuRun);

        setJMenuBar(jMenuBarFields);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1322, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 802, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void countyNameGlobalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_countyNameGlobalActionPerformed
        // TODO add your handling code here:

        LOGGER.log(Level.ALL, "Method Name:countyNameGlobalActionPerformed" + "Initializing information based on county Name.");

        String countyName = (String) countyNameGlobal.getSelectedItem();

        // By default get the first item in the list and get the Co-Ordinates of the at item and populate the
        // weather stations based on the first item in the combobox
        int itemCount = weatherStComboBox.getItemCount();

        for (int i = 0; i < itemCount; i++) {
            weatherStComboBox.removeItemAt(0);
        }

        //CSVFileHandler csvfilehandler = new CSVFileHandler ();
        Location countypos = new Location();
        mycsvfile.getCountyLocation_GlobalDB(countyName, countypos);

        ArrayList<String> weatherstations = null;
        weatherstations = mycsvfile.getWeatherStations_GlobalDB(countypos);
        Collections.sort(weatherstations);

        System.out.println("Total Number of Fawn Weather Stations = " + weatherstations.size() + " for " + countyName);

        //weatherStComboBox.
        for (int i = 0; i < weatherstations.size(); i++) {
            String wtstationname = weatherstations.get(i);
            wtstationname = wtstationname.substring(0, 1).toUpperCase() + wtstationname.substring(1).toLowerCase();

            System.out.println(wtstationname);
            weatherStComboBox.addItem(wtstationname);
        }

        ArrayList<String> soilList = null;
        //String countyName = (String) soilInfoCountyCombobox.getSelectedItem();
        //soilList = soil_db.getCountyBasedSoilList_SoilDB(countyName);
        //CSVFileHandler csvfilehandler = new CSVFileHandler();
        soilList = mycsvfile.getCountyBasedSoilList_SoilDB(countyName);

        int soilitemCount = soilSeriesCombobox.getItemCount();
        for (int i = 0; i < soilitemCount; i++) {
            soilSeriesCombobox.removeItemAt(0);
        }

        for (int i = 0; i < soilList.size(); i++) {
            //System.out.print(soilList.get(i));
            soilSeriesCombobox.addItem(soilList.get(i));
        }


    }//GEN-LAST:event_countyNameGlobalActionPerformed

    private void weatherStComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_weatherStComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_weatherStComboBoxActionPerformed

    private void jNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNextButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jNextButtonActionPerformed

    private void jNextButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jNextButtonMouseClicked
        
        createWeatherFile();
        String filePath = GenerateSoilData();

        
        messageDisplayLabel.setText("");
        try {
            double rowSpacing = Double.parseDouble(jRowSpaceEdit.getText());
            
            // Validation
            if (rowSpacing < 4 || rowSpacing > 7) {
                messageDisplayLabel.setText("Valid ROW SPACING 4-7 feet.");
                return;
            }

            // Convert feet to inches 
            rowSpacing = rowSpacing * 12;
            
            double bedWidth = Double.parseDouble(jBedWidthET.getText());
            if (rowSpacing < bedWidth) {
                messageDisplayLabel.setText("ROW SPACING should be more than BED WIDTH.");
                return;
            }
            
            if (bedWidth<8 || bedWidth > 36) {
                messageDisplayLabel.setText("Valid BED WIDTH is 8-36 inches.");
                return;
            }
            
            double bedHeight = Double.parseDouble(jBedHeightET.getText());
            if (bedHeight < 4 || bedHeight > 12) {
                messageDisplayLabel.setText("Valid BED HEIGHT is 4-12 inches.");
                return;
            }

            bedWidth*=2.54;
            bedHeight*=2.54;

            // Validation for plant Spacing 
            double plantingSpacing = Double.parseDouble(jPlantSapceEdit.getText());
            if (plantingSpacing < 4 || plantingSpacing > 30) {
                messageDisplayLabel.setText("Valid PLANTING SPACING is 4-30 inches.");
                return;
            }
              
            double plantingDepth = Double.parseDouble(jPlantingDepthEdit.getText());
            if (plantingDepth > 6) {
                messageDisplayLabel.setText("Valid PLANTING DEPTH is 0-6 inches.");
                return;
            }
            
            
            
            
        } catch (NumberFormatException e) {
            messageDisplayLabel.setText("Please enter numerical values in bed width and row spacing");
            return;
        }
        if (mDSSATInstallation.length() < 1) {
            messageDisplayLabel.setText("Please select DSSAT Installation folder");
            return;
        }

        if (UpdateFile() == true) {
            messageDisplayLabel.setText("");
            FieldPanel.setVisible(false);
            setIrrigationPanelData();
            IrrigationFertilizerPanel.setVisible(true);
        } else {
            messageDisplayLabel.setText("Please update necessary information.");
        }
    }//GEN-LAST:event_jNextButtonMouseClicked

    private void jComboBoxCropListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxCropListActionPerformed


    }//GEN-LAST:event_jComboBoxCropListActionPerformed

    private void jComboBoxCropListItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxCropListItemStateChanged
        // TODO add your handling code here:
        // TODO add your handling code here:

        if (evt.getStateChange() == ItemEvent.SELECTED) {
            initCultivar();
            /* String cropName = (String)jComboBoxCropList.getSelectedItem();
             HashMap <String, String> cultivarHashMap = null; 

             StringBuilder cultivadata = new StringBuilder ();

             cultivadata.append ("Crop Name,");        
             cultivadata.append(cropName);


             cultivadata.append (",CultivarFile,");  
             String culfileName = cropname_culfilename.get(cropName);        
             cultivadata.append(culfileName); 

             System.out.println ("Crop Name" + cropName);

             if (cultivafile == null)
             cultivafile = CultivaFileSystem.getInstance();

             cultivafile.UpdateCache(cultivadata.toString());         
             cultivarHashMap = cultivafile.ReadFromFile("VAR-NAME");

             ArrayList<String> cultivalist = new ArrayList<String> ();
             Set keys = cultivarHashMap.keySet();
             Iterator itr = keys.iterator();

             String key;
             String value;
             while(itr.hasNext())
             {
             key = (String)itr.next();
             String val = cultivarHashMap.get(key);
             cultivalist.add(val);
             }
             Collections.sort(cultivalist); 

             int itemCount = jComboBoxCultivar.getItemCount();
             for(int i=0;i<itemCount;i++){
             jComboBoxCultivar.removeItemAt(0);
             }

             for (int i = 0; i < cultivalist.size(); i++) {
             key = cultivalist.get(i);
             key = key.trim();
             jComboBoxCultivar.addItem(key);
             } */

        }
    }//GEN-LAST:event_jComboBoxCropListItemStateChanged

    private void jRowSpaceEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRowSpaceEditActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRowSpaceEditActionPerformed

    private void bFinishButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bFinishButtonActionPerformed
        jMsgLabelIrrigation.setText("");
        // Here firs validate all the values if they are valid or not.
        try {
            double dripperEfficiency = Double.parseDouble(dripEfficiency.getText());
            // Validation
            if (dripperEfficiency < 3 || dripperEfficiency > 99) {
                jMsgLabelIrrigation.setText("Valid DRIPPER EFFICIENCY is 3-99.");
                return;
            }
            double irrigationRate = Double.parseDouble(dripRateET.getText());
            
            // Validation
            if (irrigationRate < .3 || irrigationRate > 3) {
                jMsgLabelIrrigation.setText("Valid IRRIGATION RATE is .3-3 ml/sec.");
                return;
            }
            
            double dripperEmitterOffset = Double.parseDouble(dripOffsetET.getText());
            double bedWidth = Double.parseDouble(jBedWidthET.getText());
            bedWidth *= .5;
            
            // Validation
            if (dripperEmitterOffset > 8 || dripperEmitterOffset > bedWidth) {
                jMsgLabelIrrigation.setText("Valid DRIPPER EMITTER OFFSET is 0-8 inches or less than half of BED WIDTH.");
                    return;
            }
            
            
            double dripperEmitterDepth = Double.parseDouble(dripDepthET1.getText());
             if (dripperEmitterDepth < 0 || dripperEmitterDepth > 8) {
                jMsgLabelIrrigation.setText("Valid DRIPPER EMITTER DEPTH is 0-8 inches.");
                return;
            }
             
             double irrigationDepth = Double.parseDouble(irrigationDepthET.getText());
             if (irrigationDepth < 6 || irrigationDepth > 18) {
                jMsgLabelIrrigation.setText("Valid IRRIGATION DEPTH is 6-18 inches.");
                return;
            }
                       
        } catch (NumberFormatException e) {
            jMsgLabelIrrigation.setText("Please input only Numbers.");
            return;
        }
      


        // TODO add your handling code here:
        LOGGER.log(Level.ALL, "Creating a Weather file after Finish Button is pressed ...");
        String keyvalue = "dssat, dssat1";

        // First check if all the necessary data is available in the window or not. 
        if (ValidateFram2Data() == false) {
            return;
        }

        if (wthfile == null) {
            wthfile = WeatherFileSystem.getInstance();
            wthfile.initFertilizerMap(fertMethodVsCode);
        }
        wthfile.UpdateCache(keyvalue);
        setFileName();
        wthfile.WriteToFile(mSiteCode + ".WTH");

        LOGGER.log(Level.ALL, "Created a Weather file after Finish Button is pressed ...");
        try {
            wthfile.copyWeatherFile(mSiteCode + ".WTH", mDSSATInstallation + dirseprator + "Weather");
            wthfile.createJson();
            wthfile.createXFileOutput();
//            Process proc = Runtime.getRuntime().exec("java -cp E:\\Job\\QUAD\\quadui-1.3.1\\Quad.jar org.agmip.ui.quadui.QuadUIApp -cli -n -D E:\\Job\\QUAD\\FABL1502.json \"\" E:\\Job\\QUAD");
//            InputStream in = proc.getInputStream();
//            InputStream err = proc.getErrorStream();
//
//            byte b[] = new byte[in.available()];
//            in.read(b, 0, b.length);
//            System.out.println(new String(b));
//
//            byte c[] = new byte[err.available()];
//            err.read(c, 0, c.length);
//            System.out.println(new String(c));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }//GEN-LAST:event_bFinishButtonActionPerformed

    private boolean ValidateFram2Data() {

        boolean isSuccess = true;
        String str = new String("");

        str = irrigationDepthET.getText();
        if (str.trim().isEmpty()) {
            jLabel58.setForeground(Color.red);
            isSuccess = false;
        } else {
            jLabel58.setForeground(Color.BLACK);

        }

        str = dripDepthET1.getText();
        if (str.trim().isEmpty()) {
            jLabel54.setForeground(Color.red);
            isSuccess = false;
        } else {
            jLabel54.setForeground(Color.BLACK);

        }

        str = dripOffsetET.getText();
        if (str.trim().isEmpty()) {
            jLabel53.setForeground(Color.red);
            isSuccess = false;
        } else {
            jLabel53.setForeground(Color.BLACK);
        }

        str = dripRateET.getText();
        if (str.trim().isEmpty()) {
            jLabel49.setForeground(Color.red);
            isSuccess = false;
        } else {
            jLabel49.setForeground(Color.BLACK);
        }

        str = dripSpacingET.getText();
        if (str.trim().isEmpty()) {
            jLabel51.setForeground(Color.red);
            isSuccess = false;
        } else {
            jLabel51.setForeground(Color.BLACK);
        }

        //Update cahce with fertilizer data
        DefaultTableModel model = (DefaultTableModel) fertilizerTable.getModel();
        wthfile.writeAttribute("fert_row_count", "" + model.getRowCount());
        for (int i = 0; i < model.getRowCount(); i++) {
            int j = 0;
            Date date = (Date) model.getValueAt(i, j);
            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
            String[] d = format.format(date).split("[.]");
            String date_S = d[0] + d[1] + d[2];
            long jd = Integer.parseInt(d[0].substring(2)) * 1000 + dayOfYear(d);

            j++;
            String mat = model.getValueAt(i, j).toString();
            String matCode = null;
            
            // Check if the material value is null or not {
            if (mat!=null && fertMaterialVsCode.containsKey(mat)) {
                matCode = fertMaterialVsCode.get(mat);
            }
            
            
            
            /*for (Couple cu : fertmaterial) {
                if (cu.method.equals(mat)) {
                    matCode = cu.code;
                    break;
                }
            }*/

            j++;
            String meth = model.getValueAt(i, j).toString();
            String methCode = null;
            
            if (meth!=null && fertMethodVsCode.containsKey(meth)) {
                methCode = fertMethodVsCode.get(meth);
            }
            
            /*for (Couple cu : fertmethod) {
                if (cu.method.equals(meth)) {
                    methCode = cu.code;
                    break;
                }
            }*/

            j++;
            String rate = model.getValueAt(i, j).toString();

            wthfile.writeAttribute("ftdate" + i, date_S);
            wthfile.writeAttribute("feacd" + i, methCode);
            wthfile.writeAttribute("fecd" + i, matCode);
            wthfile.writeAttribute("feamn" + i, rate);

        }

        wthfile.writeAttribute("ireff", dripEfficiency.getText());
        
        // Update cache with irrigation data
        model = (DefaultTableModel) irrigationTable.getModel();
        wthfile.writeAttribute("irr_row_count", "" + model.getRowCount());
        for (int i = 0; i < model.getRowCount(); i++) {
            int j = 0;
            Date date = (Date) model.getValueAt(i, j);
            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
            String[] d = format.format(date).split("[.]");
            String date_S = d[0] + d[1] + d[2];
            long jd = Integer.parseInt(d[0].substring(2)) * 1000 + dayOfYear(d);

            j++;
            // get the Irrigation Method
            String mat = model.getValueAt(i, j).toString();
            String irrCode = null;
            for (Couple cu : irrigationMethod) {
                if (cu.method.equals(mat)) {
                    irrCode = cu.code;
                    break;
                }
            }

            j++;
            
            // Find the Water Table Detpsh
            String water_table_depth = model.getValueAt(i, j).toString();
            j++;
            
            
            String start_time = model.getValueAt(i, j).toString();
            j++;

            
            String duration = model.getValueAt(i, j).toString();
            j++;

            
            String interval = model.getValueAt(i, j).toString();
            j++;

            
            String event_time = model.getValueAt(i, j).toString();
            j++;

            wthfile.writeAttribute("irdate" + i, date_S);
            wthfile.writeAttribute("irop" + i, irrCode);
            wthfile.writeAttribute("irval" + i, dripRateET.getText());

            wthfile.writeAttribute("irstr" + i, start_time);
            wthfile.writeAttribute("irdur" + i, duration);
            wthfile.writeAttribute("irint" + i, interval);
            wthfile.writeAttribute("irnum" + i, event_time);
            wthfile.writeAttribute("irspc", dripSpacingET.getText());
            wthfile.writeAttribute("irdep", dripDepthET1.getText());
            wthfile.writeAttribute("irofs", dripOffsetET.getText());

        }
        return isSuccess;
    }

    private int dayOfYear(String[] d) {
        Calendar gc = Calendar.getInstance();
        gc.set(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2]));
        return gc.get(6);
    }

    private void ResetForeGround() {
        jLabel49.setForeground(Color.BLACK);
        jLabel51.setForeground(Color.BLACK);
        jLabel58.setForeground(Color.BLACK);
        jLabel54.setForeground(Color.BLACK);
        jLabel53.setForeground(Color.BLACK);
    }


    private void bBackButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bBackButtonMouseClicked
        // TODO add your handling code here:
        ResetForeGround();
        IrrigationFertilizerPanel.setVisible(false);
        FieldPanel.setVisible(true);

    }//GEN-LAST:event_bBackButtonMouseClicked

    private void irrigationTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_irrigationTableMouseClicked
        // TODO add your handling code here:

        if (irrigationTable.getSelectedRow() >= 0) {
            warningMessaage.setText("");
            DefaultTableModel model = (DefaultTableModel) irrigationTable.getModel();
            Date date = new Date(model.getValueAt(irrigationTable.getSelectedRow(), 0).toString());
            jPlantingDateChooser.setDate(date);
        }
    }//GEN-LAST:event_irrigationTableMouseClicked

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:


    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jSiteNameTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSiteNameTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jSiteNameTextActionPerformed

    private void jOpenMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOpenMenuItemActionPerformed
        // TODO add your handling code here:
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("."));
        if (evt.getSource() == jOpenMenuItem) {

            int returnVal = fc.showOpenDialog(jMenuFile);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                //This is where a real application would open the file.
                //log.append("Opening: " + file.getName() + "." + newline);
            }
        } else if (evt.getSource() == jMenuItemSaveAs) {
            //log.append("Open command cancelled by user." + newline);
            int returnVal = fc.showSaveDialog(jMenuFile);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                //This is where a real application would save the file.
                //log.append("Saving: " + file.getName() + "." + newline);
            } else {
                //log.append("Save command cancelled by user." + newline);
            }
        }
    }//GEN-LAST:event_jOpenMenuItemActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItemSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsActionPerformed
        // TODO add your handling code here:
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("."));
        if (evt.getSource() == jMenuItemSaveAs) {
            //log.append("Open command cancelled by user." + newline);
            int returnVal = fc.showSaveDialog(jMenuFile);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                //This is where a real application would save the file.
                //log.append("Saving: " + file.getName() + "." + newline);
            } else {
                //log.append("Save command cancelled by user." + newline);
            }
        }
    }//GEN-LAST:event_jMenuItemSaveAsActionPerformed

    private void jMenuFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuFileActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_jMenuFileActionPerformed

    private void jMenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExitActionPerformed
        // TODO add your handling code here:

        //FileOperations.WriteToSiteFile (this);
        System.exit(0);
    }//GEN-LAST:event_jMenuItemExitActionPerformed

    private void jMenuIrrigationFertilizerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuIrrigationFertilizerActionPerformed
        // TODO add your handling code here:


    }//GEN-LAST:event_jMenuIrrigationFertilizerActionPerformed

    private void jMenuFieldsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuFieldsActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_jMenuFieldsActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // TODO add your handling code here:
        IrrigationFertilizerPanel.setVisible(false);
        FieldPanel.setVisible(true);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItemGoToIrrigationFertilizerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGoToIrrigationFertilizerActionPerformed
        // TODO add your handling code here:
        UpdateFile();
        FieldPanel.setVisible(false);
        IrrigationFertilizerPanel.setVisible(true);
    }//GEN-LAST:event_jMenuItemGoToIrrigationFertilizerActionPerformed

    private void jProjectExplorerMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jProjectExplorerMousePressed
        // TODO add your handling code here:
        if (SwingUtilities.isRightMouseButton(evt)) {
            displayPopupOptions(evt);
        } else if (SwingUtilities.isLeftMouseButton(evt)) {
            saveSiteFile(evt);
            openSiteFile(evt);

        }
    }//GEN-LAST:event_jProjectExplorerMousePressed

    private void openSiteFile(java.awt.event.MouseEvent evt) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jProjectExplorer.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }

        String filename = node.getUserObject().toString();
        // Open the zone1 file 
        String file = DSSATMain.curdirpath + dirseprator + "sites\\site1\\Alachua\\Block1\\" + filename + ".data";
        String name;
        String value;
        // Open the zone1 file 
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
//            for(String line = br.readLine(); line != null; line = br.readLine()) {
//                // process the line.
//                StringTokenizer tokenizer = StringTokenizer(line,":",false);
//                tokenizer.
//                System.out.println(line);
//            }
        } catch (IOException e) {

        }
    }

    // This method will be useful while saving the data to the file whil closing the application.
    private String getTreeText(TreeModel model, Object object, String indent) {
        String myRow = indent + object + "\n";
        for (int i = 0; i < model.getChildCount(object); i++) {
            myRow += getTreeText(model, model.getChild(object, i), indent + "  ");
        }
        return myRow;
    }

    private void saveSiteFile(java.awt.event.MouseEvent evt) {
        // Open the zone1 file 

    }

    private void displayPopupOptions(java.awt.event.MouseEvent evt) {
        TreePath path = jProjectExplorer.getPathForLocation(evt.getX(), evt.getY());
        Rectangle pathBounds = jProjectExplorer.getUI().getPathBounds(jProjectExplorer, path);

        int childcount = jProjectExplorer.getModel().getChildCount(jProjectExplorer.getModel().getRoot());
        LOGGER.log(Level.ALL, path.toString());
        LOGGER.log(Level.ALL, "Total Numbe of Child nodes = ", childcount);
        if (pathBounds != null && pathBounds.contains(evt.getX(), evt.getY())) {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) jProjectExplorer.getModel().getRoot();
            // Checked if the selection is made on the root node then
            // Then we give the options to add an new Block or Save the block
            //root.getParent()

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

            // We are right clicking on the root node
            if (path.getLastPathComponent() == root) {
                //Integer (childcount)
                JPopupMenu menu = new JPopupMenu();
                menu.add(new JMenuItem("New Site"));
                menu.show(jProjectExplorer, pathBounds.x, pathBounds.y + pathBounds.height);
            } // Check if this is a block node
            // We need to check if the current one is the child of the root node then this is the 
            // Block node. 
            // We are right clicking on any of the site node
            else if (node.getParent() == root) {
                JPopupMenu menu = new JPopupMenu();
                menu.add(new JMenuItem("New Block"));
                menu.add(new JMenuItem("Save Site"));
                menu.show(jProjectExplorer, pathBounds.x, pathBounds.y + pathBounds.height);

            } // We are right clicking on any of the block node
            else if (node.getParent().getParent() == root) {
                JPopupMenu menu = new JPopupMenu();
                menu.add(new JMenuItem("New Zone"));
                menu.add(new JMenuItem("Save Block"));
                menu.show(jProjectExplorer, pathBounds.x, pathBounds.y + pathBounds.height);

            } // We are right clicking on any of the zone node
            else if (node.getParent().getParent().getParent() == root) {
                JPopupMenu menu = new JPopupMenu();
                menu.add(new JMenuItem("Open"));
                menu.add(new JMenuItem("Copy"));
                menu.add(new JMenuItem("Paste"));
                menu.add(new JMenuItem("Save"));
                menu.show(jProjectExplorer, pathBounds.x, pathBounds.y + pathBounds.height);

            }

        }
    }
    private void jSplitPane1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSplitPane1MouseDragged
        // TODO add your handling code here:

//         BasicSplitPaneDivider l_divider = jSplitPane1.getDivider();
//        @Override
//        public void mouseDragged(MouseEvent evt) {
//          Dimension l_pane_size = getSize();
//          if (getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
//            int l_new_loc = getDividerLocation() + e.getX();
//            if (l_new_loc >= 0 && l_new_loc <= l_pane_size.width) {
//              setDividerLocation(l_new_loc);
//            }
//          } else {
//            int l_new_loc = getDividerLocation() + e.getY();
//            if (l_new_loc >= 0 && l_new_loc <= l_pane_size.height) {
//              setDividerLocation(l_new_loc);
//            }
//          }
//        }
    }//GEN-LAST:event_jSplitPane1MouseDragged

    private void jButtonOpenWeatherFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOpenWeatherFileActionPerformed
        String filepath = createWeatherFile();
        OpenTxtFileEditor(filepath);
    }//GEN-LAST:event_jButtonOpenWeatherFileActionPerformed

    private String createWeatherFile() {
        // TODO add your handling code here:
        String watherstation = (String) weatherStComboBox.getSelectedItem();
        if (wthfile == null) {
            wthfile = WeatherFileSystem.getInstance();
            wthfile.initFertilizerMap(fertMethodVsCode);
        }
        wthfile.writeAttribute("DssatFolder", mDSSATInstallation);
        wthfile.writeAttribute("Crop", (String)jComboBoxCropList.getSelectedItem());
        wthfile.writeAttribute("StationLocationId", mycsvfile.getWeatherStationId_GlobalDB(watherstation));
        setFileName();
        String filepath = wthfile.WriteToFile(mSiteCode + ".WTH");
        System.out.println ("Full weather file path" + filepath);
        //String filepath = mSiteCode + ".WTH";
        return filepath;
    }

    private void jButtonDisplaySoilFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDisplaySoilFileActionPerformed
        // TODO add your handling code here:
        String filePath = GenerateSoilData();
        OpenTxtFileEditor(filePath);
    }//GEN-LAST:event_jButtonDisplaySoilFileActionPerformed

    private void jSiteNameTextKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jSiteNameTextKeyTyped
    }//GEN-LAST:event_jSiteNameTextKeyTyped

    private void fertilizerTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fertilizerTableMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_fertilizerTableMouseClicked

    private void irrigationDepthETActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_irrigationDepthETActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_irrigationDepthETActionPerformed

    private void jEditSaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEditSaveButtonActionPerformed
        // TODO add your handling code here:
        if (evt.getActionCommand().contains("Edit")) {
            jSiteNameText.setEditable(true);
            jSiteNameText.setBackground(new Color(255, 255, 255));
            jBlockNameText.setEditable(true);
            jBlockNameText.setBackground(new Color(255, 255, 255));
            jZoneNameText.setEditable(true);
            jZoneNameText.setBackground(new Color(255, 255, 255));
            jEditSaveButton.setActionCommand("Save");
            jEditSaveButton.setText("Lock Block/Site");
        } else {
            jSiteNameText.setEditable(false);
            jSiteNameText.setBackground(new Color(204, 204, 255));
            jBlockNameText.setEditable(false);
            jBlockNameText.setBackground(new Color(204, 204, 255));
            jZoneNameText.setEditable(false);
            jZoneNameText.setBackground(new Color(204, 204, 255));
            jEditSaveButton.setActionCommand("Edit");
            jEditSaveButton.setText("EditBlock/Site");
            setFileName();
        }
    }//GEN-LAST:event_jEditSaveButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) fertilizerTable.getModel();
        int r = model.getRowCount();
        if (r > 0) {
            int c = model.getColumnCount();
            r--;
            for (int i = 0; i < c - 1; i++) {
                if (model.getValueAt(r, i) == null) {
                    fertilizerLabel.setText("Please enter details for last row first !!");
                    fertilizerLabel.setForeground(Color.red);
                    return;
                }
            }
        }
        fertilizerLabel.setText("Click \"Add Row\" Button to start inserting row in the table");
        fertilizerLabel.setForeground(Color.blue);
        Vector rowData = new Vector();
        rowData.add(new Date());
        model.addRow(rowData);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) irrigationTable.getModel();
        int r = model.getRowCount();
        if (r > 0) {
            int c = model.getColumnCount();
            r--;
            for (int i = 0; i < c - 1; i++) {
                if (model.getValueAt(r, i) == null) {
                    irrigationLabel.setText("Please enter details for last row first !!");
                    irrigationLabel.setForeground(Color.red);
                    return;
                }
            }
        }
        irrigationLabel.setText("Click \"Add Row\" Button to start inserting row in the table");
        irrigationLabel.setForeground(Color.blue);
        Vector rowData = new Vector();
        rowData.add(new Date());
        model.addRow(rowData);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        // TODO add your handling code here:
        irrigationTablePanel.setEnabled(false);
        irrigationTable.setEnabled(false);
        irrigationLabel.setEnabled(false);
        jButton3.setEnabled(false);
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
        // TODO add your handling code here:
        // TODO add your handling code here:
        
        irrigationTablePanel.setEnabled(true);
        irrigationTable.setEnabled(true);
        irrigationLabel.setEnabled(true);
        jButton3.setEnabled(true);

    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void browseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseActionPerformed
        // TODO add your handling code here:
        JFileChooser jc = new JFileChooser();
        jc.setCurrentDirectory(new File("C:\\"));
        jc.setDialogTitle("Select DSSAT installation");
        jc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jc.setAcceptAllFileFilterUsed(false);
        if (jc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            mDSSATInstallation = "" + jc.getSelectedFile();
            dssatPath.setText(mDSSATInstallation);
        } else {
            mDSSATInstallation = "";
        }
    }//GEN-LAST:event_browseActionPerformed

    private void jComboBoxPlantingMethodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxPlantingMethodActionPerformed
        // TODO add your handling code here:
        int id = jComboBoxPlantingMethod.getSelectedIndex();
        if(id == 7){ //transplants 
            jPlantingDepthLabel.setText("Planting Method");
        }else{
            jPlantingDepthLabel.setText("Planting Method*");
        }
    }//GEN-LAST:event_jComboBoxPlantingMethodActionPerformed

    private void infoButtonFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoButtonFieldActionPerformed
        // TODO add your handling code here:
        String str;
        str = "Below are the valid parameters\n"
        + "Bed Width 8-36 inches,\n"
        + "Bed Hight 4-12 inches,\n"
        + "Row Spacing 4-7 feet, \n"
        + "Planting Spacing 4-30 inches\n"
        + "Planting Depth 0-6 inches.";
        
        JOptionPane.showMessageDialog(null, str);        
    }//GEN-LAST:event_infoButtonFieldActionPerformed

    private void jBedWidthETKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBedWidthETKeyTyped
        // TODO add your handling code here:
        messageDisplayLabel.setText("");
        int bedWidth = 0;
        try {  
            bedWidth = Integer.parseInt(jBedWidthET.getText());
            if (bedWidth<8 || bedWidth > 36) {
                messageDisplayLabel.setText("Valid BED WIDTH is 8-36 inches." + bedWidth);
                return;
            }
        } catch(NumberFormatException nfe) {  
            messageDisplayLabel.setText("Only Numbers are allowed in BED WIDTH field" + bedWidth);
            jBedWidthET.setText("");
        }  
        
    }//GEN-LAST:event_jBedWidthETKeyTyped

    private void jBedHeightETKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jBedHeightETKeyTyped
        // TODO add your handling code here:
        messageDisplayLabel.setText("");
        int bedHeight = 0;
        try {  
            bedHeight = Integer.parseInt(jBedHeightET.getText());
            if (bedHeight<8 || bedHeight > 36) {
                messageDisplayLabel.setText("Valid BED HEIGHT is 4-12 inches.");
                return;
            }
        } catch(NumberFormatException nfe) {  
            messageDisplayLabel.setText("Only Numbers are allowed in BED Height field" + bedHeight);
            jBedHeightET.setText("");
        }  
    }//GEN-LAST:event_jBedHeightETKeyTyped

    private void infoButtonField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoButtonField1ActionPerformed
        // TODO add your handling code here:
        String str;
        str = "Below are the valid parameters\n"
        + "Dripper efficiency 3%-99%,\n"
        + "Irrigation rate 0.3-3 ml/sec,\n"
        + "Dripper Emitter Offset 0-8 inches or less than half of Bed Width, \n"
        + "Dripper Emitter depth from surface 0-8 inches,\n"
        + "Irrigation depth 6-18 inches,\n"
        + "Water Table depth is 0 or more.\n";
        
        JOptionPane.showMessageDialog(null, str); 
    }//GEN-LAST:event_infoButtonField1ActionPerformed

    private void bRunButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRunButtonActionPerformed
        // TODO add your handling code here:
        File batchFile = new File ("RunDSSAT2D.bat");
        PrintWriter fileWriter = null;
        
        
        try {
            if (!batchFile.exists()) {
                batchFile.createNewFile();
            }

            String dssatFolderPath = dssatPath.getText();
 
            fileWriter = new PrintWriter(batchFile);
            // Build crop absolute path 
            String cropFolderPath = dssatFolderPath + "\\" + jComboBoxCropList.getSelectedItem();
            String batchFilePath = cropFolderPath   + "\\" + "DSS40.v45";
            String exeFilePath = dssatFolderPath + "\\" + "DSSAT_2D.exe";
            String runCommand = exeFilePath + " b " + batchFilePath;
            
            // C:\DSSAT45\dscsm045.exe b C:\DSSAT45\potato\DSS40.v45            
            // Validate if the exe file is available in the exeFilePath or not. If it is not 
            // available then we need to copy this from the current folder into the DSSAT folder.
            
            if (!(new File(exeFilePath).exists())) {
                Files.copy(Paths.get("DSSAT_2D.exe"), Paths.get(exeFilePath), StandardCopyOption.COPY_ATTRIBUTES);
            }
            
            String drive = dssatFolderPath.substring(0, 2);
            fileWriter.println(drive);
            fileWriter.println("cd " + dssatFolderPath);
            fileWriter.println(runCommand);
            fileWriter.println("@echo off");            
            fileWriter.println("pause");            
            fileWriter.println("exit");
        } catch (IOException e) {
            
        } finally {
            fileWriter.close();
        }
        
        // Batch File execution
        List cmdAndArgs = Arrays.asList("cmd", "/c", "RunDSSAT2D.bat");
        File dir = new File(".");

        ProcessBuilder pb = new ProcessBuilder(cmdAndArgs);
        pb.directory(dir);
        
        try {
            Process p = pb.start();
        } catch (IOException e) {
            
        }       
    }//GEN-LAST:event_bRunButtonActionPerformed

    private void OpenTxtFileEditor(String filepath) {

        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                String cmds[] = new String[]{"notepad", filepath};
                Runtime.getRuntime().exec(cmds);
            } else {
                File file = new File(filepath);
                Desktop.getDesktop().browse(file.toURI());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void setFileName() {
        File f = new File(datadir + dirseprator + "/SiteIDList.txt");

        String block = jBlockNameText.getText();
        String site = jSiteNameText.getText();
        int expNo = 1;
        Calendar c = Calendar.getInstance();
        String year = "" + c.get(Calendar.YEAR);
        year = year.substring(2);
        mSiteCode = site.toUpperCase().substring(0, 2) + "" + block.toUpperCase().substring(0, 2);

        try {
            if (f.exists()) {
                //Check for existing Site ID

                BufferedReader br = new BufferedReader(new FileReader(f));
                String line;
                boolean isExist = false;
                while ((line = br.readLine()) != null) {
                    String[] fields = line.split(",");
                    if (site.toUpperCase().equals(fields[0]) && block.toUpperCase().equals(fields[1])) {
                        isExist = true;
                        String code = fields[2];
                        expNo = Integer.parseInt(fields[3]) + 1;
                        mSiteCode = code + year + String.format("%02d", expNo);
                    }
                }
                br.close();

                if (!isExist) {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
                    line = site.toUpperCase() + "," + block.toUpperCase() + "," + mSiteCode + "," + expNo + "\n";
                    mSiteCode = mSiteCode + year + String.format("%02d", expNo);
                    bw.write(line);
                    bw.close();
                }
            } else {
                BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                String line = site.toUpperCase() + "," + block.toUpperCase() + "," + mSiteCode + "," + expNo;
                mSiteCode = mSiteCode + year + String.format("%02d", expNo);
                bw.write(line);
                bw.close();
            }

        } catch (Exception e) {
            mSiteCode = mSiteCode + year + "01";
            e.printStackTrace();
        }
    }

    private boolean UpdateFile() {

        LOGGER.log(Level.ALL, "Updating the cache with the latest information...");

        StringBuilder weatherdata = new StringBuilder("");
        String str = new String("");
        boolean isSuccess = true;

        if (wthfile == null) {
            wthfile = WeatherFileSystem.getInstance();
            wthfile.initFertilizerMap(fertMethodVsCode);
        }

        weatherdata.append("Station Code,");
        setFileName();
        str = mSiteCode;
        weatherdata.append(str + ",");

        weatherdata.append("Farm,");
        str = jSiteNameText.getText();

        if (!str.isEmpty()) {
            weatherdata.append(str + ",");
        } else {
            isSuccess = false;
        }

        weatherdata.append("Block,");
        str = jBlockNameText.getText();
        if (str.trim().isEmpty()) {
            jBlockNameLabel.setForeground(Color.red);
            isSuccess = false;
        } else {
            jBlockNameLabel.setForeground(Color.BLACK);
            weatherdata.append(str + ",");
        }

        weatherdata.append("Zone,");
        str = jZoneNameText.getText();
        if (str.trim().isEmpty()) {
            jZoneNameLabel.setForeground(Color.red);
            isSuccess = false;
        } else {
            jZoneNameLabel.setForeground(Color.BLACK);
            weatherdata.append(str + ",");
        }

        weatherdata.append("Planting Date,");
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        str = format.format(jPlantingDateChooser.getDate());
        if (str.trim().isEmpty()) {
            jPlantingDateLabel.setForeground(Color.red);
            isSuccess = false;
        } else {
            jPlantingDateLabel.setForeground(Color.BLACK);
            weatherdata.append(str + ",");
        }

        weatherdata.append("Location,");
        str = countyNameGlobal.getSelectedItem().toString();
        weatherdata.append(str + ",");

        weatherdata.append("Weather Station,");
        str = weatherStComboBox.getSelectedItem().toString();
        weatherdata.append(str + ",");

        weatherdata.append("Soil,");
        str = soilSeriesCombobox.getSelectedItem().toString();
        weatherdata.append(str + ",");

        weatherdata.append("Previous Crop,");
        str = jComboBoxPrevCropList.getSelectedItem().toString();
        weatherdata.append(str + ",");

        weatherdata.append("Crop,");
        str = jComboBoxCropList.getSelectedItem().toString();
        weatherdata.append(str + ",");

        weatherdata.append("Cultivar,");
        str = jComboBoxCultivar.getSelectedItem().toString();
        weatherdata.append(str + ",");
        
        weatherdata.append("DssatFolder,");
        str = dssatPath.getText();
        weatherdata.append(str + ",");

        weatherdata.append("Bed Width,");
        str = jBedWidthET.getText();
        double cmval = Double.parseDouble(str);
        cmval*=2.54;
        /*int val = (int) Double.parseDouble(str);
        val = (int) (val * 0.0254 * 10);*/
        if (str.trim().isEmpty()) {
            jBedWidthLabel.setForeground(Color.red);
            isSuccess = false;
        } else {
            jBedWidthLabel.setForeground(Color.BLACK);
            //weatherdata.append(str + ",");
            weatherdata.append(String.valueOf(cmval) + ",");
        }

        weatherdata.append("Bed Height,");
        str = jBedHeightET.getText();
        cmval = Double.parseDouble(str);
        cmval*=2.54;
        //val = (int) (val * 0.0254 * 10);
        if (str.trim().isEmpty()) {
            jBedWidthLabel.setForeground(Color.red);
            isSuccess = false;
        } else {
            jBedWidthLabel.setForeground(Color.BLACK);
            weatherdata.append(String.valueOf(cmval) + ",");
        }

        weatherdata.append("PlasticMulch Color,");
        str = jComboBoxPlastic.getSelectedItem().toString();
        
        if (str.equals("Black")) {
            str="0.03";
        } else if (str.equals("Silver")) {
            str="0.39";
        } else if (str.equals("White")) {
            str="0.48";
        } else {
            str="";
        }
        
        
        weatherdata.append(str + ",");

        weatherdata.append("Planting Method,");
        String plantingMethod = jComboBoxPlantingMethod.getSelectedItem().toString();
        weatherdata.append(plantingMethod + ",");

        weatherdata.append("pldp,");
        str = jPlantingDepthEdit.getText();
        double depth = 0.0;
        try {
            depth = Double.parseDouble(str);
            depth = depth * 0.0254 * 10;
            jRowSpacingLabel.setForeground(Color.BLACK);
            weatherdata.append((int) depth + " ,");
        } catch (NumberFormatException e) {
            jRowSpacingLabel.setForeground(Color.red);
            isSuccess = false;
        }

        weatherdata.append("Planting Spacing in Row,");
        str = jPlantSapceEdit.getText();
        double inRowSpace = 0.0;
        try {
            inRowSpace = Double.parseDouble(str);
            //inRowSpace.
            inRowSpace = inRowSpace * 0.0254 * 10;
            jRowSpacingLabel.setForeground(Color.BLACK);
            weatherdata.append((int) inRowSpace + " ,");
        } catch (NumberFormatException e) {
            jRowSpacingLabel.setForeground(Color.red);
            if(jComboBoxPlantingMethod.getSelectedIndex() != 7)
                isSuccess = false;
        }

        weatherdata.append("plrs,");
        str = jRowSpaceEdit.getText();
        double rowSpace = 0.0;
        try {
            rowSpace = Double.parseDouble(str);
            rowSpace = rowSpace * 30.48;
            jRowSpacingLabel.setForeground(Color.BLACK);
            weatherdata.append(rowSpace + " ,");
        } catch (NumberFormatException e) {
            jRowSpacingLabel.setForeground(Color.red);
            isSuccess = false;
        }

        LOGGER.log(Level.ALL, "Updated the cache with the latest information...");

        String[] crop = mycsvfile.getCropID(jComboBoxCropList.getSelectedItem().toString()).split(",");
        String culFile = crop[1];
        String cropCode = crop[0];
        weatherdata.append("crid,");
        weatherdata.append(cropCode + ",");

        String culCode = mycsvfile.getCulId(culFile, jComboBoxCultivar.getSelectedItem().toString());
        weatherdata.append("dssat_cul_id,");
        weatherdata.append(culCode + ",");

        String plantingCode = "None";
        for (Couple p : plantingMethodsList) {
            if (p.method.equals(plantingMethod)) {
                plantingCode = p.code;
                break;
            }
        }

        weatherdata.append("plma,");
        weatherdata.append(plantingCode + ",");

        weatherdata.append("plpop,");
        weatherdata.append((int) (10000 / (rowSpace * inRowSpace)) + ",");

        String watherstation = (String) weatherStComboBox.getSelectedItem();
        wthfile.writeAttribute("StationLocationId", mycsvfile.getWeatherStationId_GlobalDB(watherstation));

        if (isSuccess == true) {
            wthfile.UpdateCache(weatherdata.toString());
        }

        return isSuccess;
    }

    /*private void UpdateFile () {
        
     StringBuilder   weatherdata = new StringBuilder ("");
     if (wthfile == null)
     wthfile = WeatherFileSystem.getInstance();
        
     weatherdata.append ("Organization,");
     weatherdata.append(jTextFieldOrgName.getText() + ",");
        
     weatherdata.append ("SiteIndex,");
     weatherdata.append (jTextFieldSiteIndex.getText() + ",");
        
     Date date = jDateChooser1.getDate();        
     Calendar calendar = new GregorianCalendar();
     calendar.setTime(date);
        
     Integer day =  new Integer(calendar.get(Calendar.DATE));
     String daystr = day.toString();
        
     Integer year = new Integer (calendar.get(Calendar.YEAR));
     String yearstr = year.toString();
        
     Integer month = new Integer (calendar.get(Calendar.MONTH));
     String monthstr = month.toString();
                     
        
     //System.out.println ("Hello This is rohit - " + day + "//" + month + "//"+  year);
        
     weatherdata.append ("PlantingMonth,");
     //weatherdata.append ((String) jPlantingMonthComboBox.getSelectedItem() + ",");
     weatherdata.append (monthstr + ",");
        
     weatherdata.append ("PlantingDay,");
     //weatherdata.append ((String) jPlantingDayComboBox.getSelectedItem() + ",");
     weatherdata.append (daystr + ",");
        
     weatherdata.append ("PlantingYear,");
     //weatherdata.append ((String) jPlantingYearComboBox.getSelectedItem() + ",");
     weatherdata.append (yearstr + ",");
        
     weatherdata.append ("FawnWeatherStation,");
     String watherstation = (String) weatherStComboBox.getSelectedItem();
     weatherdata.append (watherstation + ",");  
        
     weatherdata.append ("StationLocationId,");        
     weatherdata.append(global_db.getWeatherStationId_GlobalDB (watherstation));
        
     System.out.println("Weather Data -" + weatherdata);
     wthfile.UpdateCache(weatherdata.toString());  
        
        
        
     }*/
    /*static DBConnect getDBConnection (String dbName){
     DBConnect db = null;
     if (dbName.equals("global")) {
            
     if (global_db == null) {
                
     global_db = new DBConnect (ServerDetails.DB_URL_SERVER1, ServerDetails.USER_SERVER1, ServerDetails.PASS_SERVER1, global_dbname);
     }
            
     db = global_db; 
            
     } else if (dbName.equals("weather_historic_daily")){
     if (weather_historic_daily == null) {
                
     weather_historic_daily = new DBConnect (ServerDetails.DB_URL_SERVER1, ServerDetails.USER_SERVER1, ServerDetails.PASS_SERVER1, weather_historic_daily_dbname);
     }
     db = weather_historic_daily;    
     } 
        
     return db; 
     }*/
    /**
     * @param args the command line arguments
     */
    private CommonMenuBar jMenuBar1;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel BedSystemInfo;
    private javax.swing.JPanel CropInfo;
    private javax.swing.JPanel FieldPanel;
    private javax.swing.JPanel GeneralInformation;
    private javax.swing.JPanel IrrigationFertilizerPanel;
    private javax.swing.JPanel SoilInfo;
    private javax.swing.JPanel WeatherInfo;
    private javax.swing.JButton bBackButton;
    private javax.swing.JButton bFinishButton;
    private javax.swing.JButton bRunButton;
    private javax.swing.JButton browse;
    private javax.swing.JComboBox countyNameGlobal;
    private javax.swing.JTextField dripDepthET1;
    private javax.swing.JTextField dripEfficiency;
    private javax.swing.JTextField dripOffsetET;
    private javax.swing.JTextField dripRateET;
    private javax.swing.JTextField dripSpacingET;
    private javax.swing.JLabel dssatLabel;
    private javax.swing.JTextField dssatPath;
    private javax.swing.JLabel fertilizerLabel;
    public static javax.swing.JTable fertilizerTable;
    private javax.swing.JButton infoButtonField;
    private javax.swing.JButton infoButtonField1;
    private javax.swing.JTextField irrigationDepthET;
    private javax.swing.ButtonGroup irrigationGroup;
    private javax.swing.JLabel irrigationLabel;
    private javax.swing.JTable irrigationTable;
    private javax.swing.JPanel irrigationTablePanel;
    private javax.swing.JTextField jBedHeightET;
    private javax.swing.JLabel jBedHeightLabel;
    private javax.swing.JTextField jBedWidthET;
    private javax.swing.JLabel jBedWidthLabel;
    private javax.swing.JLabel jBlockNameLabel;
    private javax.swing.JTextField jBlockNameText;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButtonDisplaySoilFile;
    private javax.swing.JButton jButtonOpenWeatherFile;
    private javax.swing.JComboBox jComboBoxCropList;
    private javax.swing.JComboBox jComboBoxCultivar;
    private javax.swing.JComboBox jComboBoxPlantingMethod;
    private javax.swing.JComboBox jComboBoxPlastic;
    private javax.swing.JComboBox jComboBoxPrevCropList;
    private javax.swing.JLabel jCropNameLabel;
    private javax.swing.JLabel jCultiVarLabel;
    private javax.swing.JButton jEditSaveButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLocationLabel;
    private javax.swing.JPanel jMainPanel;
    private javax.swing.JMenuBar jMenuBarFields;
    private javax.swing.JMenu jMenuFields;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuIrrigationFertilizer;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemGoToIrrigationFertilizer;
    private javax.swing.JMenuItem jMenuItemOptions;
    private javax.swing.JMenuItem jMenuItemSaveAs;
    private javax.swing.JMenu jMenuRun;
    private javax.swing.JMenu jMenuTools;
    private javax.swing.JLabel jMsgLabelIrrigation;
    private javax.swing.JButton jNextButton;
    private javax.swing.JMenuItem jOpenMenuItem;
    private javax.swing.JTextField jPlantSapceEdit;
    private javax.swing.JLabel jPlantSpaceLabel;
    private javax.swing.JLabel jPlantSpaceLabel1;
    private com.toedter.calendar.JDateChooser jPlantingDateChooser;
    private javax.swing.JLabel jPlantingDateLabel;
    private javax.swing.JTextField jPlantingDepthEdit;
    private javax.swing.JLabel jPlantingDepthLabel;
    private javax.swing.JLabel jPlasticLabel;
    private javax.swing.JLabel jPrevCropLabel;
    private javax.swing.JTree jProjectExplorer;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JTextField jRowSpaceEdit;
    private javax.swing.JLabel jRowSpacingLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JLabel jSiteNameLabel;
    private javax.swing.JTextField jSiteNameText;
    private javax.swing.JLabel jSoilLabel;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel jWSLabel;
    private javax.swing.JLabel jZoneNameLabel;
    private javax.swing.JTextField jZoneNameText;
    private javax.swing.JLabel messageDisplayLabel;
    private javax.swing.JPanel pButtonPanel;
    private javax.swing.JPanel pIrrigationPanel;
    private javax.swing.JComboBox soilSeriesCombobox;
    private javax.swing.JLabel warningMessaage;
    private javax.swing.JComboBox weatherStComboBox;
    // End of variables declaration//GEN-END:variables
}
