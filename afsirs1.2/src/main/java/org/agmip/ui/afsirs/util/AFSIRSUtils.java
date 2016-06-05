/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.agmip.ui.afsirs.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import static org.agmip.ui.afsirs.util.Messages.DOC_HEADER;
import static org.agmip.ui.afsirs.util.Messages.USER_DETAILS;

/**
 *
 * @author rkmalik
 */

/*
 DEFINITIONS....

 IR = IRRIGATION SYSTEM IDENTIFICATION CODE
 IRCRFL = CROWN FLOOD IRRIGATION SYSTEM CODE
 IRNSCY = CONTAINER NURSERY IRRIGATION SYSTEM CODE
 IRRICE = RICE FLOOD IRRIGATION SYSTEM CODE
 IRSEEP = SEEPAGE (SUBIRRIGATION) IRRIGATION SYSTEM CODE
 ISIM = COUNTER FOR MULTIPLE SIMULATIONS
 JDAY = CALENDAR DAY OF CROP IRRIGATION SEASON
 J1SAVE = CALENDAR DAY OF FIRST DAY OF IRRIGATION SEASON FOR
 PREVIOUS SIMULATION
 JNSAVE = CALENDAR DAY OF LAST DAY OF IRRIGATION SEASON FOR
 PREVIOUS SIMULATION
 SITE = DESCRIPTION OF LOCATION OF THE PRODUCTION SYSTEM SIMULATED

 */
public class AFSIRSUtils {

    
    public static final Font BLACK_NORMAL = new Font(FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
    public static final Font BLACK_BOLD = new Font(FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
    //public static final Font BLACK_NORMAL = new Font(FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.RED);
    public static final Font BLUE_NORMAL = new Font(FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLUE);
    public static final Font GREEN_ITALIC = new Font(FontFamily.HELVETICA, 7, Font.ITALIC, BaseColor.GREEN);
    
    int IR, ISIM, J1REP, JNREP, J1SAVE, JNSAVE, ICODE, IPRT;

    // Initialization of constants
    public static final int IRSEEP = 6;
    public static final int IRCRFL = 7;
    public static final int IRNSCY = 4;
    public static final int IRRICE = 8;
    public static final int ICIT = 3;
    public static final int INSCY = 7;
    public static final int IRICE = 31;
    public static final int ICGP = 5; //Generic Crop
    public static final int ICGA = 17;
    public static final int IRNSYC = 4;

    public static final boolean defaultMode = true;

    int NYR, J1, JN, ICROP, NDAYS, IDCODE = 0;
    double ARZI, ARZN, FIX, FRIR, PIR, IEFF;
    double EXIR, EPS = 0.000001, SWCI1, SWCN1;
    double DRZIRR, DRZTOT;
    double DWT;
    double PLANTEDACRES = 0.0;
    double[][] RAIN = new double[64][365];
    double[][] ETP = new double[64][365];    
    double[][] IRR = new double[64][365]; 
    
    
    
    double[][] RAIN_1 = new double[64][365];
    double[][] ETP_1 = new double[64][365];    
    double[] DRZ_1 = new double[365];
    double[] DRZI_1 = new double[365];
    double[] AWD_1 = new double[365];
    double[] RKC_1 = new double[365];
    int[] NF_1 = new int[4];
    
    double[] SDR = new double[365];
    double[] SET = new double[365];
    double[] SETP = new double[365];
    double[] SRAIN = new double[365];
    double[] DRZ = new double[365];
    double[] DRZI = new double[365];
    double[] RKC = new double[365];
    double[] NIR = new double[365];
    double[] SWIRR = new double[365];
    double[] SWMAX = new double[365];
    double[] SWCIX = new double[365];
    double[] SWCNX = new double[365];

    double[] AWD = new double[365];
    double[] AKC = new double[12];
    double[] ALDP = new double[12];
    int[] NF = new int[4];
    double AKC3, AKC4;
    double DZN, DZX;
    double[] F;
    double[] ALD;
    double HGT;

    double[] PDATM = new double[12];
    double[] PDATBW = new double[26];
    double[] PDATW = new double[52];
    
    // ArrayList to Hold all the data
    ArrayList<PDAT> allSoilInfo = new ArrayList<> ();

    int[] JDAY = new int[365];
    int MONTH, IIDAY, IYEAR;
    int MO1, MON, DAY1, DAYN; //For Irrigation Season 
    //Irrigation parameters
    double[] EFF = new double[10];

    //Soil Data
    String SNAME;
    String[] TXT;
    double[] DU, WCL, WCU, WC;
    int NL;

    String OUTFIL, SUMMARYFILE, CTYPE, CLIMFIL, ALOC, IRNAME;
    String SITE, UNIT, OWNER;
    
    SummaryReport summaryReport = new SummaryReport();

    boolean isPerennial, IVERS, isNet = false;
    int[] NKC = {15, 45, 74, 105, 135, 166, 196, 227, 258, 288, 319, 349};
    final static int[] MDAY = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private final String EOL = "\r\n";

    private static AFSIRSUtils instance;
    
    BufferedWriter bwOutputFile;
    //BufferedWriter bwOutputSummaryFile;
    Document bwOutputSummaryFile;
    ArrayList<PdfPTable> summaryTables;
    private double[] soilFractions;
    private double[] soilArea;
    private Soil soil;
    private SoilData soilData;

    private AFSIRSUtils() {

    }
    
    private void getDataCopy () {
        
        RAIN_1=Arrays.copyOf(RAIN, RAIN.length);
        ETP_1=Arrays.copyOf(ETP, ETP.length);
        
        DRZ_1=Arrays.copyOf(DRZ, DRZ.length);
        DRZI_1=Arrays.copyOf(DRZI, DRZI.length);
        AWD_1=Arrays.copyOf(AWD, AWD.length);
        RKC_1=Arrays.copyOf(RKC, RKC.length);
        NF_1=Arrays.copyOf(NF, NF.length);
    }
    
    private void resetAllocate () {
        SDR = new double[365];
        SET = new double[365];
        SETP = new double[365];
        SRAIN = new double[365];
        IRR = new double[64][365];


        

        SWMAX = new double[365];
        SWCIX = new double[365];
        SWCNX = new double[365];
        
        
        SWCI1 = SWCN1 = 0.0;  
        SWIRR = new double[365];

        //RKC = new double[365];
        //AWD = new double[365];
        //NF = new int[4];
        //DRZ = new double[365];
        //DRZI = new double[365];
        
        RAIN=Arrays.copyOf(RAIN_1, RAIN_1.length);
        ETP=Arrays.copyOf(ETP_1, ETP_1.length);
        DRZ=Arrays.copyOf(DRZ_1, DRZ_1.length);
        DRZI=Arrays.copyOf(DRZI_1, DRZI_1.length);
        AWD=Arrays.copyOf(AWD_1, AWD_1.length);
        RKC=Arrays.copyOf(RKC_1, RKC_1.length);
        NF=Arrays.copyOf(NF_1, NF_1.length);
    }

    public static AFSIRSUtils getInstance() {
        if (instance == null) {
            instance = new AFSIRSUtils();
        }
        return instance;
    }
    
    public static void resetData(){
        instance = new AFSIRSUtils();
    }

    public void INIT() {
        IR = 0;
        ISIM = 0;
        J1SAVE = 0;
        JNSAVE = 0;
        EPS = 0.000001;
    }

    public void setOutFile(String name) {
        OUTFIL = name;
    }

    public String getOutFile() {
        return OUTFIL;
    }
    
    public void setSummaryFile(String fName) {
        SUMMARYFILE = fName;
    }
    
    public String getSummaryFile () {
        return SUMMARYFILE;
    }

    public void setTodayDate(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd");
        String[] date = sdf.format(d).split(" ");
        IYEAR = Integer.parseInt(date[0]);
        MONTH = Integer.parseInt(date[1]);
        IIDAY = Integer.parseInt(date[2]);
    }

    public void setSiteName(String site) {
        SITE = site;
    }

    public String getSITE() {
        return SITE;
    }

    public double getPLANTEDACRES() {
        return PLANTEDACRES;
    }

    public void setPLANTEDACRES(double PLANTEDACRES) {
        this.PLANTEDACRES = PLANTEDACRES;
    }    

    public String getUNIT() {
        return UNIT;
    }

    public void setUNIT(String UNIT) {
        this.UNIT = UNIT;
    }

    public String getOWNER() {
        return OWNER;
    }

    public void setOWNER(String OWNER) {
        this.OWNER = OWNER;
    }
    public void setCodes(int code, int print) {
        ICODE = code;
        IPRT = print;
    }

    public int getICODE() {
        return ICODE;
    }

    public void setCropData(int code, String name) {
        CTYPE = name;
        ICROP = code;
    }

    public String getCropName() {
        return CTYPE;
    }

    public int getICROP() {
        return ICROP;
    }

    public void setIVERS(boolean ivers) {
        IVERS = ivers;
    }

    public boolean getIVERS() {
        return IVERS;
    }

    public void setIrrigationSystem(int ir, double arzi, double exir, double eff, String name) {
        IR = ir;
        ARZI = arzi;
        EXIR = exir;
        IEFF = eff;
        IRNAME = name;
    }

    public void setEXIR(double exir) {
        EXIR = exir;
    }

    public double getARZI() {
        return ARZI;
    }

    public int getIrrigationSystem() {
        return IR;
    }

    public String getIrrigationSystemName() {
        return IRNAME;
    }

    public int getJ1SAVE() {
        return J1SAVE;
    }

    public int getJNSAVE() {
        return JNSAVE;
    }

    public int getJ1() {
        return J1;
    }

    public int getJN() {
        return JN;
    }

    public double getEPS() {
        return EPS;
    }

    public void setIDCODE(int code, double value) {
        if (code == 1) {
            FIX = value;
        } else if (code == 2) {
            PIR = value;
        }

        IDCODE = code;
    }

    public void setDCOEFPerennial(double drzirr, double drztot, double[] akc, double[] aldp) {
        DRZIRR = drzirr;
        DRZTOT = drztot;
        AKC = akc;
        ALDP = aldp;
    }

    public void setDRZ(double[] drz) {
        DRZ = drz;
    }

    public double[] getDRZ() {
        return DRZ;
    }

    public void setAWD(double[] awd) {
        AWD = awd;
    }

    public double[] getAWD() {
        return AWD;
    }

    public void setRKC(double[] rkc) {
        RKC = rkc;
    }

    public double[] getRKC() {
        return RKC;
    }

    public void setDWT(double val) {
        DWT = val;
    }

    public double getDWT() {
        return DWT;
    }

    public void setPerennial(boolean perennial) {
        isPerennial = perennial;
    }

    public boolean getPerennial() {
        return isPerennial;
    }

    public void setNF(int[] NF) {
        this.NF = NF;
    }

    public int[] getNF() {
        return NF;
    }

    public void setDCOEFAnnual(double dzn, double dzx, double[] f, double[] ald) {
        DZN = dzn;
        DZX = dzx;
        F = f;
        ALD = ald;
    }

    public void setHGT(double HGT) {
        this.HGT = HGT;
    }

    public void setAKC(double akc3, double akc4) {
        AKC3 = akc3;
        AKC4 = akc4;
    }

    public double getAKC(int index) {
        if (index == 3) {
            return AKC3;
        } else {
            return AKC4;
        }
    }


    public void setSoilData(SoilData soilData) {
        //this.soil = soilData.getSoils().get(0);
        this.soilData = soilData;
        
        
        //SNAME = soil.getName();
        //TXT = soil.getTXT();
        //DU = soil.getDU();
        
        double WCLn[] = {.9,.9,.9,.9,.9,.9};
        WCL = WCLn;
        //WCL = soil.getWCL();
        //WCU = soil.getWCU();
        double WCUn [] = {.9,.9,.9,.9,.9,.9};
        WCU = WCUn;
        //WC = soil.getWC();
        //NL = soil.getNL();
        
        
        /*SNAME = firstSoil.getName();
        TXT = firstSoil.getTXT();
        DU = firstSoil.getDU();
        WCL = firstSoil.getWCL();
        WCU = firstSoil.getWCU();
        WC = firstSoil.getWC();
        NL = firstSoil.getNL();*/
    }
    
    public void setDefaultSoil (Soil soil) {
        SNAME = soil.getName();
        TXT = soil.getTXT();
        DU = soil.getDU();
        WCL = soil.getWCL();
        WC = soil.getWC();
        WCU = soil.getWCU();
        NL = soil.getNL();
    }

    public SoilData getSoilData() {
        return soilData;
    }

    public void setSWIRR(double[] swirr) {
        SWIRR = swirr;
    }

    public void setSWCIX(double[] swcix) {
        SWCIX = swcix;
    }

    public void setSWCNX(double[] swcnx) {
        SWCNX = swcnx;
    }

    public void setSWMAX(double[] swmax) {
        SWMAX = swmax;
    }

    public double[] getSWCIX() {
        return SWCIX;
    }

    public double[] getSWCNX() {
        return SWCNX;
    }

    public double[] getSWMAX() {
        return SWMAX;
    }

    public void setSWCN1(double swcn1) {
        SWCN1 = swcn1;
    }

    public void setSWCI1(double swci1) {
        SWCI1 = swci1;
    }

    //type = 0, Monthly : 1, Bi-Weekly : 2, Weekly
    public ArrayList<SoilSpecificPeriodData> getGraphData(int type) {
        
        ArrayList<SoilSpecificPeriodData> data = new ArrayList<> ();
        int index = 1;
        
        switch (type) {
            case 0:
                
                for (PDAT i : allSoilInfo) {
                    SoilSpecificPeriodData d = new SoilSpecificPeriodData ();
                    d.setSoilDataPoints(i.PDATM);
                    d.setSoilName(i.soilName+"-"+(index++));
                    data.add(d);
                }
                return data;
            case 1:
                for (PDAT i : allSoilInfo) {
                    
                    SoilSpecificPeriodData d = new SoilSpecificPeriodData ();

                    d.setSoilDataPoints(i.PDATBW);
                    d.setSoilName(i.soilName+"-"+(index++));
                     data.add(d);
                }
                return data;
            case 2:
                for (PDAT i : allSoilInfo) {
                    SoilSpecificPeriodData d = new SoilSpecificPeriodData ();
                    d.setSoilDataPoints(i.PDATW);
                    d.setSoilName(i.soilName+"-"+(index++));
                    data.add(d);
                }
                return data;
        }
        return null;
    }

    public void setisNet(boolean net) {
        isNet = net;
    }

    //public void setIrrigationSeason(Date startDate, Date endDate) {
    public void setIrrigationSeason(int sMonth, int sDay, int eMonth, int eDay) {
        //J1, JN calculation and saved in 
        //J1Save and JNSave
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd");
        //String[] date = sdf.format(startDate).split(" ");
        //MO1 = Integer.parseInt(date[1]);
        //DAY1 = Integer.parseInt(date[2]);
        MO1 = sMonth;
        DAY1 = sDay;
        MON = eMonth;
        DAYN = eDay;

        //date = sdf.format(endDate).split(" ");
        //MON = Integer.parseInt(date[1]);
        //DAYN = Integer.parseInt(date[2]);

        J1 = DAY1;
        for (int i = 0; i < MO1 - 1; i++) {
            J1 += MDAY[i];
        }
        JN = DAYN;
        for (int i = 0; i < MON - 1; i++) {
            JN += MDAY[i];
        }

        NDAYS = JN - J1 + 1;

        if (NDAYS < 0) {
            NDAYS += 365;
        }

        //Save growing data for next simulation
        J1SAVE = J1;
        JNSAVE = JN;

        if (JN < J1) {
            J1 = 1;
            JN = NDAYS;
        }
    }

    public int getNYR() {
        return NYR;
    }

    public double[][] getRain() {
        return RAIN;
    }

    public double[][] getET() {
        return ETP;
    }

    public void setClimateFile(InputStreamReader ir) {
        try {
            BufferedReader br = new BufferedReader(ir);
            String line = br.readLine();
            ALOC = line.split(" ")[0];
            line = br.readLine();
            int i = 0;
            while (line.charAt(i) == ' ') {
                i++;
            }
            line = line.substring(i);
            i = 0;
            while (line.charAt(i) != ' ') {
                i++;
            }
            NYR = Integer.parseInt(line.substring(0, i).trim());

            for (int j = 0; j < NYR; j++) {
                int IYEAR = Integer.parseInt(br.readLine());

                int k = 0;
                while (k < 365) {
                    String[] parts = br.readLine().split(" ");
                    for (String x : parts) {
                        if (x.length() > 0) {
                            ETP[j][k] = Double.parseDouble(x);
                            k++;
                        }
                    }
                }
            }
            br.readLine();
            for (int j = 0; j < NYR; j++) {
                String line1 = br.readLine();
                int IYEAR = Integer.parseInt(line1);

                int k = 0;
                while (k < 365) {
                    String curLine = br.readLine();
                    String[] parts = curLine.split(" ");
                    
                    for (String x : parts) {
                        if (x.length() > 0) {
                            RAIN[j][k] = Double.parseDouble(x);
                            k++;
                        }
                    }
                }
            }

            
            for (int k = 0; k < 365; k++) {
                JDAY[k] = k + 1;
            }

            if (J1SAVE >= JNSAVE) {
                NYR = NYR - 1; // Reduced no of year
                for (int iy = 0; iy < NYR; iy++) {
                    int j = -1;
                    int iy1 = iy + 1;
                    for (int jd = J1SAVE - 1; jd < 365; jd++) {
                        j = j + 1;
                        JDAY[j] = jd + 1;
                        ETP[iy][j] = ETP[iy][jd];
                        RAIN[iy][j] = RAIN[iy][jd];
                    }
                    for (int jd = 0; jd < JNSAVE; jd++) {
                        j = j + 1;
                        JDAY[j] = jd + 1;
                        
                        ETP[iy][j] = ETP[iy1][jd];
                        RAIN[iy][j] = RAIN[iy1][jd];
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeOutput(String str) {
        try {
            bwOutputFile.append(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void MAIN() {
        // Iterate Simulations ..
        FRIR = 1.00;
        FIX = 0.00;
        for (int i = 0; i < 365; i++) {
            JDAY[i] = i;
        }
        ISIM = ISIM + 1;
        J1REP = J1SAVE;
        JNREP = JNSAVE;
    }

    //BAL.for
    public void calculateBalance() {
        
        //Initialize parameters
        double SWCISV = 0.0, SWCNSV = 0.0, WCISAVE, WCNSAVE;
        int NYX = NYR;
        double REWAT = 1.0;
        if (J1SAVE > JNSAVE) {
            NYX += 1;
        }
        double EXNR = 1.0 - EXIR;

        double[] KC = new double[365];

        //Iterate years
        for (int iy = 0; iy < NYR; iy++) {
            //Zero parameter for current year
            double[] ET = new double[365];
            double[] SWCI = new double[365];
            double[] SWCN = new double[365];
            double[] ER = new double[365];
            double[] NIR = new double[365];
            double[] DR = new double[365];

            //For rice flood irrigation systems
            if (IR != IRRICE) {
                for (int j = J1 - 1; j < JN; j++) {
                    int j1 = j - 1;
                    REWAT = REWAT + 1.0;
                    double NRR;
                    double NR;
                    //Calculate number of days required for redistribution to field capacity
                    if (IR != IRSEEP && IR != IRCRFL) {
                        NRR = RAIN[iy][j] + 1;
                        NR = 2;
                        if (DRZI[j] < 12) {
                            NR = 1;
                        }
                        if (DRZI[j] > 24) {
                            NR = 3;
                        }
                        if (NRR > NR) {
                            NR = NRR;
                        }
                        if (NR > 5) {
                            NR = 5;
                        }
                    } else {
                        NR = 2;
                        if (RAIN[iy][j] > 0.50) {
                            NR = 2.0 * RAIN[iy][j] + 1;
                        }
                        if (NR > 7) {
                            NR = 7;
                        }
                    }

                    //Calculate crop ET from ETP and crop coefficient
                    KC[j] = RKC[j];
                    double RKCS = 0.00;
                    /*
                     Do not increase crop coefficient for rain if RKC = 0.0 (No crop being grown)
                     Modifies KC based on days since last rain or irrigation during growth stages 1 and 2
                     */
                    if (!isPerennial && KC[j] >= EPS && j < NF[1]) {
                        RKCS = 1.0 / Math.sqrt(REWAT);
                    }
                    if (KC[j] >= EPS) {
                        //Adjust KC for low versus high ETP days
                        KC[j] = KC[j] - 0.03 * (ETP[iy][j] - 0.20);
                        if (RKCS > KC[j]) {
                            KC[j] = RKCS;
                        }
                        //Do not reduce crop coefficient for rain if KC is already > 1.0
                        if (KC[j] <= 1.0) {
                            double RKCR = 1.00;
                            //Calculate ET at potential rate for the day that Rain occurs.
                            if (ETP[iy][j] > EPS) {
                                RKCR = RAIN[iy][j] / ETP[iy][j];
                            }
                            if (RKCR > 1.00) {
                                RKCR = 1.00;
                            }
                            if (RKCR > KC[j]) {
                                KC[j] = RKCR;
                            }
                        }
                        if (KC[j] > 1.25) {
                            KC[j] = 1.25;
                        }
                    }
                    ET[j] = KC[j] * ETP[iy][j];

                    //Update Soil water content in crop root zone for expanding root zone, rain and ET
                    if (j != J1 - 1 || (iy != 1 && NDAYS > 365)) {
                        double SWCEPS = SWCIX[j1] + EPS;
                        if (SWCIX[j] > SWCEPS) {
                            double DRZEPS = DRZ[j] - EPS;
                            if (DRZI[j] >= DRZEPS) {
                                SWCI[j] = SWCI[j1] + SWCIX[j] - SWCIX[j1];
                                SWCN[j] = SWCN[j1] + SWCNX[j] - SWCNX[j1];
                            } else {
                                if (SWCNX[j1] >= EPS) {
                                    double ADX = SWCN[j1] / SWCNX[j1] * (SWCIX[j] - SWCIX[j1]);
                                    SWCI[j] = SWCI[j1] + ADX;
                                    SWCN[j] = SWCN[j1] + SWMAX[j] - SWMAX[j1] - ADX;
                                }
                            }
                        } else {
                            SWCI[j] = SWCI[j1];
                            SWCN[j] = SWCN[j1];
                        }
                        WCISAVE = SWCI[j];
                        WCNSAVE = SWCN[j];
                        SWCI[j] = SWCI[j] + ARZI * RAIN[iy][j];
                        SWCN[j] = SWCN[j] + (1.0 - ARZI) * RAIN[iy][j];
                        if (SWCI[j] - SWCIX[j] >= EPS || SWCN[j] - SWCNX[j] >= EPS) {
                            SWCI[j] = SWCI[j] - EXIR * ET[j];
                            SWCN[j] = SWCN[j] - EXNR * ET[j];
                        } else if (SWCN[j] < EPS) {
                            SWCI[j] = SWCI[j] - ET[j];
                        } else {
                            double XX = 0.5 * SWCNX[j] + EPS;
                            double ETNMAX = EXNR * ET[j];
                            if (SWCN[j] > XX && XX > ETNMAX) {
                                SWCI[j] = SWCI[j] - EXIR * ET[j];
                                SWCN[j] = SWCN[j] - EXNR * ET[j];
                            } else {
                                double XET = SWCN[j] / XX;
                                if (XET > 0.5) {
                                    XET = 0.5;
                                }
                                double ETN = ETNMAX * XET;
                                SWCI[j] = SWCI[j] - (ET[j] - ETN);
                                SWCN[j] = SWCN[j] - ETN;
                                if (SWCN[j] <= 0.0) {
                                    SWCI[j] = SWCI[j] + SWCN[j];
                                    SWCN[j] = 0.0;
                                }
                            }
                        }
                    } else if (iy == 0 || NDAYS < 365) {
                        SWCI[j] = SWCI1 - EXIR * ET[j] + ARZI * RAIN[iy][j];
                        SWCN[j] = SWCN1 - EXNR * ET[j] + (1.0 - ARZI) * RAIN[iy][j];
                    } else if (NDAYS == 365) {
                        SWCI[j] = SWCISV - EXIR * ET[j] + ARZI * RAIN[iy][j];
                        SWCN[j] = SWCNSV - EXNR * ET[j] + (1.0 - ARZI) * RAIN[iy][j];
                        if (SWCN[j] <= 0.0) {
                            SWCI[j] = SWCI[j] + SWCN[j];
                            SWCN[j] = 0.0;
                        }
                    }

                    // Check for drainage from root zone
                    if (RAIN[iy][j] >= EPS && (SWCI[j] >= SWCIX[j] || SWCN[j] >= SWCNX[j]) && (SWCI[j] >= SWCIX[j] || SWCN[j] >= EPS)) {
                        double PERCI;
                        double RINCI = 0.0;
                        double PERCN;
                        double RINCN = 0.0;

                        //Calculate percolate
                        for (int JJ = 0; JJ < (int) NR; JJ++) {
                            int JDINC = JJ + j + 1;
                            if (JDINC >= JN) {
                                break;
                            }
                            RINCI = RINCI + RKC[JDINC] * EXIR * ETP[iy][JDINC];
                            RINCN = RINCN + RKC[JDINC] * EXNR * ETP[iy][JDINC];
                        }

                        //Calculate drainage from irrigated zone
                        PERCI = SWCI[j] - SWCIX[j];
                        if (PERCI < RINCI) {
                            RINCI = PERCI;
                        }
                        double DRI = PERCI - RINCI;
                        SWCI[j] = SWCIX[j] + RINCI;
                        if (SWCNX[j] > EPS) {
                            //Calculate drainage from non-irrigated zone
                            if (DRZI[j] < (DRZ[j] - EPS)) {
                                //When DRZ > DRZI
                                double SWCX = SWCN[j] + DRI;
                                PERCN = SWCN[j] - SWCNX[j] + DRI;
                                if (PERCN < RINCN) {
                                    RINCN = PERCN;
                                }
                                double SWCZ = SWCNX[j] + RINCN;
                                SWCN[j] = Math.min(SWCX, SWCZ);
                                DR[j] = SWCX - SWCN[j];
                            } else {
                                //When DRZ = DRZI
                                PERCN = SWCN[j] - SWCNX[j];
                                if (PERCN < RINCN) {
                                    RINCN = PERCN;
                                }
                                double DRN = PERCN - RINCN;
                                SWCN[j] = SWCNX[j] + RINCN;
                                DR[j] = DRI + DRN;
                            }
                        } else {
                            DR[j] = DRI;
                        }
                        //Calculate effective rainfall
                        ER[j] = RAIN[iy][j] - DR[j];
                        if (ER[j] < 0.0) {
                            //Correct for redistribution increment
                            double EXINC = -ER[j];
                            SWCI[j] = SWCI[j] + EXIR * EXINC;
                            SWCN[j] = SWCN[j] + EXNR * EXINC;
                            ER[j] = 0.0;
                        }
                    } else {
                        //Calculate effective rainfall when drainage = 0.0
                        DR[j] = 0.0;
                        ER[j] = RAIN[iy][j];
                    }

                    //Check for need to irrigate
                    if (SWCI[j] > SWIRR[j]) {
                        NIR[j] = 0.0;
                    } else if (IR == IRSEEP) {
                        //For seepage irrigaton systems
                        NIR[j] = 0.9 * SWCIX[j] - SWCI[j];
                        if (NIR[j] < 0.0) {
                            NIR[j] = 0.0;
                        }
                    } else if (IR == IRCRFL) {
                        //For crown flood irrigation systems
                        //Assume 3 days required to irrigate, and 
                        // 3 days for drainage after irrigation
                        double ETINC = 0.0;
                        for (int jj = 0; j < 5; j++) {
                            int JDP = j + jj;
                            if (JDP > JN) {
                                break;
                            }
                            ETINC = ETINC + ETP[iy][JDP];
                        }
                        NIR[j] = SWCIX[j] - SWCI[j] + ETINC;
                    } else {
                        //Irrigate to restore firstSoil water content to field capacity
                        NIR[j] = SWCIX[j] - SWCI[j];
                        //Irrigate to only a fraction of field capacity
                        if (IDCODE == 2) {
                            NIR[j] = FRIR * SWCIX[j] - SWCI[j];
                        } //Irrigate a fixed irrigation amount
                        else if (IDCODE == 1) {
                            NIR[j] = FIX;
                        }
                    }

                    //Update Soil Water content for irrigation
                    SWCI[j] = SWCI[j] + NIR[j];
                    if (IR == IRNSYC) {
                        //For container nursery sprinkler irrigation system
                        SWCN[j] = SWCN[j] + (1.0 - ARZI) / ARZI * NIR[j];
                        if (SWCN[j] > SWCNX[j]) {
                            SWCN[j] = SWCNX[j];
                        }
                    }
                    //Calculate gross irrigation requirements
                    IRR[iy][j] = NIR[j] / IEFF;
                    if (RAIN[iy][j] > 0.1 || IRR[iy][j] > EPS) {
                        REWAT = 1.0;
                    }
                }

                //Save last day's firstSoil water contents to begin first day ofnext year for perennial crops
                SWCISV = SWCI[JN-1];
                SWCNSV = SWCN[JN-1];
            } else {
                //For rice flood irrigation
                for (int j = J1 - 1; j < JN; j++) {
                    int j1 = j - 1;
                    ET[j] = RKC[j] * ETP[iy][j];
                    if (j == J1 - 1) {
                        SWCI[j] = SWCI1;
                    } else if (j > J1 - 1) {
                        SWCI[j] = SWCI[j1];
                    }
                    SWCI[j] = SWCI[j] + RAIN[iy][j] - ET[j];
                    double XX = SWCI[j];
                    DR[j] = 0.0;
                    if (SWCI[j] > SWMAX[j]) {
                        SWCI[j] = SWMAX[j];
                        DR[j] = XX - SWMAX[j];
                    }
                    ER[j] = RAIN[iy][j] - DR[j];
                    NIR[j] = 0.0;
                    if (SWCI[j] < SWIRR[j]) {
                        NIR[j] = SWCIX[j] - SWCI[j];
                    }
                    SWCI[j] = SWCI[j] + NIR[j];
                    IRR[iy][j] = NIR[j] / IEFF;
                }
            }

            
            
            //Sum parameters for each day of the irrigation season
            for (int j = J1 - 1; j < JN; j++) {
                SDR[j] += DR[j];
                SET[j] += ET[j];
                SETP[j] += ETP[iy][j];
                SRAIN[j] += RAIN[iy][j];
            }
            if (ICODE == 2) {
                writeOutput(EOL + "C   RAIN = TOTAL RAINFALL (INCHES)");
                writeOutput(EOL + "C   ET = EVAPORATION TRANSPIRATION (INCHES)");

                
                writeOutput(EOL + "       OUTPUT DAILY COMPONENTS OF SOIL WATER BUDGET FOR DEBUGGING" + EOL + EOL);
                writeOutput(String.format("                            YEAR =  %2d" + EOL, iy + 1));
                writeOutput(" CDY JDY  SWCN SWCI  ETP   KC  ET  RAIN  ER   DR     SWMX  SWIX SWIR  NIR  IRR" + EOL);
                for (int jd = J1 - 1; jd < JN; jd++) {
                    String str = String.format(" %3d%4d%6.2f%5.2f %4.3f%5.2f %4.3f%5.2f%5.2f%5.2f%6.2f%5.2f%5.2f%5.2f%5.2f" + EOL,
                            jd + 1, JDAY[jd], SWCN[jd], SWCI[jd], ETP[iy][jd], KC[jd], ET[jd], RAIN[iy][jd], ER[jd], DR[jd], SWMAX[jd], SWCIX[jd], SWIRR[jd], NIR[jd], IRR[iy][jd]);
                    writeOutput(str);
                }
            }

        }

    }

    /*
     This subroutine calculates the LEAST SQUARE CURVE fit for a 
     set of N linearly distributed data points to fit the equation
     Y = SLOPE * X + INTCPT    
     */
    public LSQResult LSQ(double[] X, double[] Y, int N) {
        //Calculate mean XBAR and YBAR  
        double SUMX = 0.0;
        double SUMY = 0.0;
        for (int i = 0; i < N; i++) {
            SUMX += X[i];
            SUMY += Y[i];
        }
        double XBAR = SUMX / N;
        double YBAR = SUMY / N;

        //Calculate sums of deviations squared and cross products
        double SX2 = 0.0;
        double SY2 = 0.0;
        double SXY = 0.0;

        for (int i = 0; i < N; i++) {
            SX2 += Math.pow(X[i] - XBAR, 2);
            SY2 += Math.pow(Y[i] - YBAR, 2);
            SXY += (X[i] - XBAR) * (Y[i] - YBAR);
        }

        //Calculate intercept and slope
        double slope = SXY / SX2;
        double intercept = YBAR - slope * XBAR;

        //Calculate sample correlation coefficient, R
        //and Coefficient of determination, RSQ
        double R = SXY / Math.sqrt(SX2 * SY2);
        double RSQ = Math.pow(R, 2);

        return new LSQResult(slope, intercept, RSQ, R);
    }

    /*
     This subroutine calculates the 50%, 80%, 90% and 95%
     probability values of IRR based on an extreme value
     TYPE I rpobability distribution
     */
    public PROBResult PROBX(double[] AI, int NYR, double[] PROB, double XMEAN) {
        double[] W = new double[64];
        double[] ALIRR = new double[64];

        PROBResult result = new PROBResult();

        EPS = 0.000001;
        int RNYR = NYR;

        //Return -1 if database is not adequate to calculate indicated extreme value
        result.setAll(-1.0, -1.0);

        //Return 0 for all values if mean is 0.0
        if (XMEAN <= 0.0001) {
            result.setAll(0.0, 1.0);
            return result;
        }

        //Eliminate 0 datae values from analysis
        int IPOS = 0;
        for (int i = 0; i < NYR; i++) {
            if (AI[i] < EPS) {
                break;
            }
            IPOS = i;
        }

        //Calculate fraction of zero values
        result.X00 = (double) (NYR - IPOS - 1) / RNYR;
        double XPOS = 1.0 - result.X00;

        //Calculating pllotting positions for PROBs and IRRs
        result.RSQ = -9.99;
        if (IPOS < 2) {
             result.setAll(-99.0, -99.0);
            return result;
        }

        for (int i = 0; i <= IPOS; i++) {
            W[i] = Math.log10(-Math.log10(PROB[i]));
            ALIRR[i] = Math.log10(AI[i]);
        }

        //Call subroutine LSQ to FIT Straight line to transformed data by the method
        //of Least Squares
        LSQResult l = LSQ(W, ALIRR, IPOS + 1);
        result.RSQ = l.RSQ;
        if (l.RSQ < 0.500) {
            result.setAll(-99.0, result.RSQ);
            return result;
        }
        if ((IPOS + 1) >= NYR) {
            // Calculate extreme values of IRR from regression equation when no values are 0.0
            result.X50 = Math.pow(10.0, (-0.52139 * l.slope + l.intercept));
            result.X80 = Math.pow(10.0, (-0.15554 * l.slope + l.intercept));
            result.X90 = Math.pow(10.0, l.intercept);
            result.X95 = Math.pow(10.0, (0.11429 * l.slope + l.intercept));
            return result;
        }

        //Calculate extreme values of IRR when some values = 0.0
        double C50 = (XPOS - 0.5) / XPOS;
        if (C50 <= EPS) {
            result.X50 = 0.0;
        } else {
            C50 = Math.log10(-Math.log10(1.0 - C50));
            result.X50 = Math.pow(10.0, (C50 * l.slope + l.intercept));
        }

        double C80 = (XPOS - 0.2) / XPOS;
        if (C80 <= EPS) {
            result.X80 = 0.0;
        } else {
            C80 = Math.log10(-Math.log10(1.0 - C80));
            result.X80 = Math.pow(10.0, (C80 * l.slope + l.intercept));
        }

        double C90 = (XPOS - 0.1) / XPOS;
        if (C90 <= EPS) {
            result.X90 = 0.0;
        } else {
            C90 = Math.log10(-Math.log10(1.0 - C90));
            result.X90 = Math.pow(10.0, (C90 * l.slope + l.intercept));
        }

        double C95 = (XPOS - 0.05) / XPOS;
        if (C95 <= EPS) {
            result.X95 = 0.0;
        } else {
            C95 = Math.log10(-Math.log10(1.0 - C95));
            result.X95 = Math.pow(10.0, (C95 * l.slope + l.intercept));
        }

        return result;
    }

    public void SW() {
        double SWX = 0.0;
        double SWN = 0.0;

        double[] DL = new double[6];

        int IW0 = 0;
        int IW1 = 0;
        int IW2 = 0;
        int IW3 = 0;

        DL[0] = 0.0;
        for (int k = 1;
                k < NL;
                k++) {
            DL[k] = DU[k - 1];
        }

        //Calculate maximum firstSoil water-holding capacities
        //For perennial crops
        if (isPerennial) {
            //Check for high water table limiting irrigated root zone

            if (DRZI[J1 - 1] >= DWT) {
                DRZI[J1 - 1] = DWT;
            }
            int IS = 0;
            boolean flag = false;
            for (int I = 0; I < NL; I++) {
                IS = I;
                if (DRZI[J1 - 1] < DU[I]) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                for (int JD = J1 - 1; JD < JN; JD++) {
                    DRZI[JD] = DU[NL - 1];
                }
            }

            //Calculate available firstSoil water in irrigated root zone
            int IS1 = IS - 1;
            double SWI = 0.0;
            if (IS != 0) {
                for (int I = 0; I <= IS1; I++) {
                    SWI += (DU[I] - DL[I]) * WC[I];
                }
            }
            SWI += (DRZI[J1 - 1] - DL[IS]) * WC[IS];
            // For Total Root Zone
            // Check for high water table limiting root eexpansion
            if (DRZ[J1 - 1] >= DWT) {
                DRZ[J1 - 1] = DWT;
                if (DWT < 2.0 * DRZI[J1 - 1]) {
                    DRZ[J1 - 1] = 2.0 * DRZI[J1 - 1];
                }
            }
            flag = false;
            for (int I = 0; I < NL; I++) {
                IS = I;
                if (DRZ[J1 - 1] < DU[I]) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                DRZ[J1 - 1] = DU[NL - 1];
                if (DRZ[J1 - 1] < 2.0 * DRZI[J1 - 1]) {
                    DRZ[J1 - 1] = 2.0 * DRZI[J1 - 1];
                }
                for (int JD = J1 - 1; JD < JN; JD++) {
                    DRZ[JD] = DRZ[J1 - 1];
                }
            }

            IS1 = IS - 1;;
            double WCX;
            if (IS != 0) {
                for (int I = 0; I <= IS1; I++) {
                    WCX = WC[I];
                    if (IR == IRCRFL) {
                        WCX = 0.4;
                    }
                    SWX += (DU[I] - DL[I]) * WCX;
                }
            }
            WCX = WC[IS];
            if (IR == IRCRFL) {
                WCX = 0.4;
            }
            SWX += (DRZ[J1 - 1] - DL[IS]) * WCX;
            if (DRZ[J1 - 1] > DWT && WCX < 0.4) {
                SWX += (DRZ[J1 - 1] - DWT) * (0.4 - WCX);
            }

            //Correct for irrigation of only a fraction of the root zone
            SWI = ARZI * SWI;
            SWN = SWX - SWI;

            //Correct for nonirrigated zone in container nurseries
            if (ICROP == INSCY) {
                SWN = 0.20;
            }
            //Correct for bedded surface in crown flood citrus irrigation
            if (IR == IRCRFL) {
                SWI *= 0.667;
            }
            SWX = SWI + SWN;
            if (SWN < EPS) {
                setEXIR(1.00);
            }

            //Assign daily firstSoil water capacity data
            for (int JD = J1 - 1; JD < JN; JD++) {
                SWMAX[JD] = SWX;
                SWCIX[JD] = SWI;
                SWCNX[JD] = SWN;
            }

        } else {
            //For annual crops

            if (IR == IRRICE) {
                double DRINC = -1.0;
                while (DRINC < 0.00 || DRINC > 3.00) {
                    String input = "Enter Rice Flood Stroage Depth(Inches) : ";
                    if (DRINC != -1.0) {
                        input = "Enter Rice Flood Stroage Depth(Inches) [Value between 0.0 and 3.0]: ";
                    }
                    String result = JOptionPane.showInputDialog(null, input);
                    try {
                        DRINC = Double.parseDouble(result);
                    } catch (Exception e) {
                        DRINC = -2.0;
                    }
                }

                for (int JD = J1 - 1; JD < JN; JD++) {
                    SWCIX[JD] = 3.0;
                    SWMAX[JD] = SWCIX[JD] + DRINC;
                    SWCNX[JD] = 0.00;
                }
            } else {
                for (int JD = J1 - 1; JD < JN; JD++) {
                    if (DRZI[JD] >= DWT) {
                        DRZI[JD] = DWT;
                    }
                }

                //Check for firstSoil depth limiting irrigated root zone
                int IS = 0;

                for (int JD = J1 - 1; JD < JN; JD++) {
                    boolean flag = false;
                    for (int I = 0; I < NL; I++) {
                        IS = I;
                        if (DRZI[JD] < DU[I]) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        DRZI[JD] = DU[NL];
                    }

                    //Calculate irrigated zone available firstSoil water for each day from root zone expansion
                    double SWI = 0.0;
                    if (IS != 0) {
                        int IS1 = IS - 1;
                        for (int I = 0; I <= IS1; I++) {
                            SWI += (DU[I] - DL[I]) * WC[I];
                        }
                    }
                    SWI += (DRZI[JD] - DL[IS]) * WC[IS];

                    //Correct for  irrigation of only a fraction of the root zone
                    SWCIX[JD] = ARZI * SWI;
                }

                //Correct crop water use coefficients for growth stages 1 and 2
                //Based on Soil properties and stage 1 crop growth
                //For Growth Stage 1
                double AKKC = 0.8 * Math.sqrt(WC[0] + 0.05);
                int J5 = J1 + 4;
                for (int JD = J1 - 1; JD < NF[0]; JD++) {
                    RKC[JD] = AKKC;
                    if (JD >= J5) {
                        RKC[JD] = AKKC + 0.12 * (JD - J5 + 1) / (NF[0] - J5);
                    }
                }

                //For Growth Stage 2
                int NFP = NF[0];
                AKKC = AKKC + 0.12;
                for (int JD = NFP; JD < NF[1]; JD++) {
                    int RJD = JD;
                    RKC[JD] = AKKC + (RJD - NF[0] + 1) / (double) (NF[1] - NF[0]) * (AKC3 - AKKC);
                    if (RKC[JD] > AKC3) {
                        RKC[JD] = AKC3;
                    }
                }

                //For total root zone
                //Check for high water table limiting total root zone
                for (int JD = J1 - 1; JD < JN; JD++) {
                    if (DRZ[JD] >= DWT) {
                        DRZ[JD] = DWT;
                        if (DRZ[JD] < 2.0 * DRZI[JD]) {
                            DRZ[JD] = 2.0 * DRZI[JD];
                        }
                    }
                }

                for (int JD = J1 - 1; JD < JN; JD++) {
                    //Check for firstSoil depth limiting total root zone
                    IS = 0;
                    boolean flag = false;
                    for (int I = 0; I < NL; I++) {
                        IS = I;
                        if (DRZ[JD] < DU[I]) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        DRZ[JD] = DU[NL];
                        if (DRZ[JD] < 2.0 * DRZI[JD]) {
                            DRZ[JD] = 2.0 * DRZI[JD];
                        }
                    }
                    SWMAX[JD] = 0.0;
                    if (IS != 0) {
                        for (int I = 0; I < IS; I++) {
                            SWMAX[JD] += (DU[I] - DL[I]) * WC[I];
                        }
                    }
                    SWMAX[JD] += (DRZ[JD] - DL[IS]) * WC[IS];
                    if (DRZ[JD] > DWT && WC[IS] < 0.4) {
                        SWMAX[JD] += (DRZ[JD] - DWT) * (0.4 * WC[IS]);
                    }
                    SWCNX[JD] = SWMAX[JD] - SWCIX[JD];
                }
            }
        }

        //Calculate starting firstSoil water content as 0.9*Field Capacity
        SWCI1 = 0.9 * SWCIX[J1 - 1];
        SWCN1 = 0.9 * SWCNX[J1 - 1];

        if (SWCN1 < EPS) {
            setEXIR(1.00);
        }

        //Calculate firstSoil water content at which irrigation will be scheduled
        for (int JD = J1 - 1;
                JD < JN;
                JD++) {
            SWIRR[JD] = (1.0 - AWD[JD]) * SWCIX[JD];
        }
        
                //Print all soil data
        String txt = "";
        double[] WCL = soil.getWCL();
        double[] WCU = soil.getWCU();
        for (String t : soil.getTXT()) {
            if (t != null) {
                txt += t + " ";
            }
        }
        writeOutput(EOL + EOL + "     SOIL :  SERIES = " + soil.getName() + "         TEXTURE = " + txt + "           AREA(Fraction ) = " + soil.getSoilTypeArea() + EOL + EOL);
        writeOutput(EOL + "               SOIL LAYER DEPTHS (INCHES) AND WATER CONTENTS" + EOL);
        String str = "";
        str += "                   lDepth(I)        WCON(Min)    WCON(Max)\r\n";
        for (int i = 0; i < soil.getNL(); i++) {
            str += String.format("                %2d%8.1f%18.2f%12.2f" + EOL, i + 1, DU[i], Math.round(WCL[i] * 100.0) / 100.0, Math.round(WCU[i] * 100.0) / 100.0);
        }
        writeOutput(str);

        writeOutput(EOL + "                DEPTH TO WATER TABLE ENTERED =  " + DWT / 12.0 + " FEET" + EOL + EOL);

        if (ICODE >= 1) {
            writeOutput(EOL + "     OUTPUT PARAMETERS - ROOT DEPTHS, KCs, AND SOIL WATER CONTENTS" + EOL);
            writeOutput("       CDAY JDAY   DRZ    DRZI   RKC   SWMAX  SWCIX  SWCNX  SWIRR" + EOL + "       ");
            for (int i = J1 - 1; i < JN; i++) {
                String row = String.format("%4d %4d %6.1f %6.1f %6.3f %6.2f %6.2f %6.2f %6.2f", (i + 1), JDAY[i], DRZ[i], DRZI[i], RKC[i], SWMAX[i], SWCIX[i], SWCNX[i], SWIRR[i]);
                writeOutput(row + EOL + "       ");
            }
        }

    }

    public void DECOEF() {
        if (isPerennial) {
            //Calculate daily root depths and allowable firstSoil water depletions
            for (int i = 0; i < 365; i++) {
                DRZI[i] = DRZIRR;
                DRZ[i] = DRZTOT;
            }

            //Calculate daily root depths & allowable firstSoil water depletions
            int jd = -1;
            for (int imo = 0; imo < 12; imo++) {
                for (int j = 0; j < MDAY[imo]; j++) {
                    jd++;
                    AWD[jd] = ALDP[imo];
                }
            }

            //Calculate daily crop water use coefficients
            //For first half of January
            double rj = -1;
            double rjdif = 14;
            double rkdif = 0.5 * (AKC[0] - AKC[11]);
            double rk2 = 0.5 * (AKC[0] + AKC[11]);
            for (jd = 0; jd < 14; jd++) {
                rj++;
                RKC[jd] = rk2 + rj / rjdif * rkdif;
            }

            //January 15 through December 15
            for (int jmo = 0; jmo < 11; jmo++) {
                int jmo1 = jmo + 1;
                int nkm1 = NKC[jmo1] - 1;
                rjdif = NKC[jmo1] - NKC[jmo];
                rkdif = AKC[jmo1] - AKC[jmo];
                rj = -1;
                for (jd = NKC[jmo] - 1; jd < nkm1; jd++) {
                    rj++;
                    RKC[jd] = AKC[jmo] + rj / rjdif * rkdif;
                }
            }

            //Decemeber 16 throgh december 31
            rj = -1;
            rjdif = 16;
            rkdif = 0.5 * (AKC[0] - AKC[11]);
            for (jd = 348; jd < 365; jd++) {
                rj++;
                RKC[jd] = AKC[11] + rj / rjdif * rkdif;
            }

            double[] AWTMP = new double[365];
            double[] RKTMP = new double[365];
            if (J1SAVE >= JNSAVE) {
                //Rearrange data for crop year that extends beyond 1 calendar year
                int j = -1;
                for (jd = JNSAVE - 1; jd < 365; jd++) {
                    j++;
                    AWTMP[j] = AWD[jd];
                    RKTMP[j] = RKC[jd];
                }
                for (jd = 0; jd < JNSAVE; jd++) {
                    j++;;
                    AWTMP[j] = AWD[jd];
                    RKTMP[j] = RKC[jd];
                }
                for (jd = J1 - 1; jd < JN; jd++) {
                    AWD[jd] = AWTMP[jd];
                    RKC[jd] = RKTMP[jd];
                }

            }
        } else {
            //Calculate number of days in each growth stage
            double ff = EPS;
            for (int i = 0; i < 4; i++) {
                ff = ff + F[i];
                NF[i] = (int) (J1 + ff * (JN - J1));
            }

            //Calculate daily root zone depths and depths irrigated
            double AKKC = 0.4;
            if (IR == IRSEEP) {
                AKKC = 1.0;
            }
            if (IR == IRRICE) {
                AKKC = 1.0;
            }

            for (int jd = J1 - 1; jd < JN; jd++) {
                int rjd = jd + 1;
                DRZI[jd] = DZN + (rjd - NF[0]) * (DZX - DZN) / (double) (NF[1] - NF[0]);
                if (jd < NF[0]) {
                    DRZI[jd] = DZN;
                }
                if (jd >= NF[1]) {
                    DRZI[jd] = DZX;
                }
                if (IR == IRRICE) {
                    DRZ[jd] = DRZI[jd];
                } else {
                    DRZ[jd] = 2.0 * DRZI[jd];
                }

                //Calculate daily crop coefficients
                RKC[jd] = AKKC;
                if (jd >= NF[0]) {
                    RKC[jd] = AKKC + (rjd - NF[0]) / (double) (NF[1] - NF[0]) * (AKC3 - AKKC);
                }
                if (jd >= NF[1]) {
                    RKC[jd] = AKC3;
                }
                if (jd >= NF[2]) {
                    RKC[jd] = AKC3 - (rjd - NF[2]) / (double) (JN - NF[2]) * (AKC3 - AKC4);
                }

                //Assign daily values of allowable water depletion
                AWD[jd] = ALD[1];
                if (jd >= NF[3]) {
                    AWD[jd] = ALD[4];
                } else if (jd >= NF[2]) {
                    AWD[jd] = ALD[3];
                } else if (jd >= NF[1]) {
                    AWD[jd] = ALD[2];
                }

            }
        }
    }

    private void displayArray (double [] a) {
        
        for (double v : a) {
            System.out.print (v + " ");
        }
        System.out.println("");
        System.out.println(a);
    }
    /*
     This subroutine orders the irrigation requirements data base from
     largest to smallest and it calculates the plotting positions for
     the IRR data
     */
    public STATResult STATX(double[] X, int N) {

        STATResult result = new STATResult(N);

        int NP = N + 1;
        int NM = N - 1;
        double DATP = N + 1.0;

        //Order data from largest to smallest
        for (int i = 0; i < NM; i++) {
            int ip = i + 1;
            for (int j = ip; j < N; j++) {
                if (X[j] >= X[i]) {
                    double temp = X[i];
                    X[i] = X[j];
                    X[j] = temp;
                }
            }
            result.PROB[i] = (double) (i + 1) / DATP;
        }
        result.PROB[N - 1] = (double) N / DATP;

        //Calculate Mean
        double XSUM = 0.0;
        for (int i = 0; i < N; i++) {
            XSUM += X[i];
        }
        result.XMEAN = XSUM / N;

        //Compute variance, standard deviation, and XCV
        double VSUM = 0.0;
        for (int i = 0; i < N; i++) {
            VSUM += Math.pow(X[i] - result.XMEAN, 2);
        }
        result.XVAR = VSUM / (N - 1);
        result.XSDEV = 0.0;
        if (result.XVAR > 0.0) {
            result.XSDEV = Math.sqrt(result.XVAR);
        }
        result.XCV = 0.0;
        if (result.XMEAN > 0.0001) {
            result.XCV = result.XSDEV / result.XMEAN;
        }

        //Define Maximum and MMinimum values
        result.XMAX = X[0];
        result.XMIN = X[N - 1];

        //Determine the median
        int NT = N / 2;
        int NNT = NT * 2;
        result.XMED = X[NT];
        if (NNT == N) {
            result.XMED = 0.5 * (X[NT - 1] + X[NT]);
        }

        return result;
    }

    public void SUMX() {
        double[][] AETP = new double[64][52];
        double[][] ARAIN = new double[64][52];
        double[][] AIRR = new double[64][52];
        double[] TET = new double[52];
        double[] TETP = new double[52];
        double[] TDR = new double[52];
        double[] TRAIN = new double[52];
        double[] AI = new double[NYR];
        double[] PROB = new double[NYR];

        int IS = 0;
        for (int IY = 0; IY < NYR; IY++) {
            AETP[IY][0] = 0.0;
            ARAIN[IY][0] = 0.0;
            AIRR[IY][0] = 0.0;
            for (int JD = J1 - 1; JD < JN; JD++) {
                AETP[IY][0] += ETP[IY][JD];
                ARAIN[IY][0] += RAIN[IY][JD];
                AIRR[IY][0] += IRR[IY][JD];
            }
        }

        TET[0] = 0.0;
        TETP[0] = 0.0;
        TDR[0] = 0.0;
        TRAIN[0] = 0.0;
        for (int JD = J1 - 1; JD < JN; JD++) {
            TET[0] += SET[JD];
            TETP[0] += SETP[JD];
            TDR[0] += SDR[JD];
            TRAIN[0] += SRAIN[JD];
        }
        TET[0] /= NYR;
        TETP[0] /= NYR;
        TDR[0] /= NYR;
        TRAIN[0] /= NYR;

        if (isNet) {
            writeOutput(EOL + "          SEASONAL OR ANNUAL NET IRRIGATION REQUIREMENT (INCHES)" + EOL);
        } else {
            writeOutput(EOL + "          SEASONAL OR ANNUAL GROSS IRRIGATION REQUIREMENT (INCHES)" + EOL);
        }
        writeOutput("          --------------------------------------------------------" + EOL);
        
        //writeOutput(EOL + EOL + "C   CDAY = CONSECUTIVE DAYS OF CROP IRRIGATION SEASON" + EOL);
        if (ICODE >= 1) {
            writeOutput("               SUMMARY OF WATER BUDGET COMPONENTS" + EOL);
            writeOutput("                  YEAR    ETP(INCHES)    RAIN(INCHES)  IRR.RQD.(INCHES)" + EOL);
            for (int i = 0; i < NYR; i++) {
                String row = String.format("                 %4d%-16.2f%8.2f%8.2f" + EOL, i + 1, AETP[i][0], ARAIN[i][0], AIRR[i][0]);
                writeOutput(row);
                AI[i] = AIRR[i][0];
             
                
            }
        } else {
            for (int i = 0; i < NYR; i++) {
                AI[i] = AIRR[i][0];
            }
        }

        writeOutput(EOL + "   MEAN  MED.  CV  XMAX  XMIN ZERO  RSQ  50%  80%  90% 95% RAIN  ETP  ET   DR" + EOL);
        
        //displayArray(AI);
        STATResult statResult = STATX(AI, NYR);
        //displayArray(AI);
        PROBResult probResult = PROBX(AI, NYR, statResult.PROB, statResult.XMEAN);
        //displayArray(AI);
        System.out.println ("");
        
        double XIRR = TET[0] / IEFF;
        probResult.X50 = Math.min(probResult.X50, XIRR);
        probResult.X80 = Math.min(probResult.X80, XIRR);
        probResult.X90 = Math.min(probResult.X90, XIRR);
        probResult.X95 = Math.min(probResult.X95, XIRR);

        String str = String.format(" %6.1f%6.1f%5.2f%6.1f%5.1f%5.2f%5.2f%5.1f%5.1f%5.1f%5.1f%5.1f%5.1f%5.1f%5.1f",
                statResult.XMEAN, statResult.XMED, statResult.XCV, statResult.XMAX, statResult.XMIN, probResult.X00, probResult.RSQ,
                probResult.X50, probResult.X80, probResult.X90, probResult.X95, TRAIN[0], TETP[0], TET[0], TDR[0]);

        writeOutput(str + EOL);

        if (J1SAVE > JNSAVE) {
            for (int i = 0; i < NYR; i++) {
                int NY = NYR - i - 1;
                int NY1 = NY + 1;
                int JJ = 365 - J1SAVE;

                for (int JD = 0; JD < JNSAVE; JD++) {
                    int J = JJ + JD;
                    ETP[NY1][JD] = ETP[NY][J];
                    RAIN[NY1][JD] = RAIN[NY][J];
                    IRR[NY1][JD] = IRR[NY][J];
                }

                int J = 0;
                for (JJ = J1SAVE - 1; JJ < 365; JJ++) {
                    J++;
                    ETP[NY][365 - J] = ETP[NY][365 - JJ];
                    RAIN[NY][365 - J] = RAIN[NY][365 - JJ];
                    IRR[NY][365 - J] = IRR[NY][365 - JJ];
                }
            }

            for (int JD = 0; JD < JNSAVE; JD++) {
                ETP[0][JD] = ETP[NYR + 1][JD];
                RAIN[0][JD] = RAIN[NYR + 1][JD];
                IRR[0][JD] = IRR[NYR + 1][JD];
            }

            if (J1SAVE - JNSAVE >= 2) {
                for (int IY = 0; IY < NYR; IY++) {
                    for (int JD = JNSAVE; JD < J1SAVE; JD++) {
                        ETP[IY][JD] = 0.0;
                        RAIN[IY][JD] = 0.0;
                        IRR[IY][JD] = 0.0;
                    }
                }
            }
        }
        if (isNet) {
            writeOutput(EOL + "                MONTHLY NET IRRIGATION REQUIREMENTS (INCHES)" + EOL);
        } else {
            writeOutput(EOL + "                MONTHLY GROSS IRRIGATION REQUIREMENTS (INCHES)" + EOL);
        }

        writeOutput("                -------------------------------------------" + EOL);

        PDAT pdat = new PDAT ();
        
        int NX = -1;
        for (int imo = 0; imo < 12; imo++) {
            int NN = NX + 1;
            NX = NN + MDAY[imo] - 1;
            for (int iy = 0; iy < NYR; iy++) {
                AETP[iy][imo] = 0.0;
                ARAIN[iy][imo] = 0.0;
                AIRR[iy][imo] = 0.0;
                for (int JD = NN; JD <= NX; JD++) {
                    AETP[iy][imo] += ETP[iy][JD];
                    ARAIN[iy][imo] += RAIN[iy][JD];
                    AIRR[iy][imo] += IRR[iy][JD];
                }
            }
            TET[imo] = 0.0;
            TETP[imo] = 0.0;
            TDR[imo] = 0.0;
            TRAIN[imo] = 0.0;

            for (int JD = NN; JD <= NX; JD++) {
                TET[imo] += SET[JD];
                TETP[imo] += SETP[JD];
                TDR[imo] += SDR[JD];
                TRAIN[imo] += SRAIN[JD];
            }
            TET[imo] /= NYR;
            TETP[imo] /= NYR;
            TDR[imo] /= NYR;
            TRAIN[imo] /= NYR;

            double AISUM = 0.0;
            for (int iy = 0; iy < NYR; iy++) {
                AISUM += AIRR[iy][imo];
                AI[iy] = AIRR[iy][imo];
            }
            if (ICODE >= 1 || (ICODE < 1 && imo == 0)) {
                writeOutput(EOL + " MO  MEAN  MED.  CV XMAX XMIN ZERO  RSQ  50%  80%  90%  95% RAIN  ETP   ET   DR" + EOL);
            }
            
            //System.out.println ("---------------Month : " + (imo+1)+"------------------");
            
            statResult = STATX(AI, NYR);
            probResult = PROBX(AI, NYR, statResult.PROB, statResult.XMEAN);
            XIRR = TET[imo] / IEFF;
            probResult.X50 = Math.min(probResult.X50, XIRR);
            probResult.X80 = Math.min(probResult.X80, XIRR);
            probResult.X90 = Math.min(probResult.X90, XIRR);
            probResult.X95 = Math.min(probResult.X95, XIRR);
            
            str = String.format(" %2d%5.1f%5.1f%5.2f%5.1f%5.1f%5.2f%5.2f%5.1f%5.1f%5.1f%5.1f%5.1f%5.1f%5.1f%5.1f", (imo + 1),
                    statResult.XMEAN, statResult.XMED, statResult.XCV, statResult.XMAX, statResult.XMIN, probResult.X00, probResult.RSQ,
                    probResult.X50, probResult.X80, probResult.X90, probResult.X95, TRAIN[imo], TETP[imo], TET[imo], TDR[imo]);
            
            System.out.println (str);
            writeOutput(str + EOL);

            summaryReport.reset ();
            summaryReport.setTotalRainFall(imo+1, TRAIN[imo]);
            summaryReport.setTotalEvaporation(imo+1, TETP[imo]);
            summaryReport.addTotalIrrigationRequiredByMonth(imo+1, 0.0);
            summaryReport.setAverageIrrigationRequired(imo+1, probResult.X50);
            summaryReport.setTwoin10IrrigationRequired(imo+1, probResult.X80);
            summaryReport.setOnein10IrrigationRequired(imo+1, probResult.X90);
            
            pdat.PDATM[imo] = statResult.XMEAN;
            PDATM[imo] = statResult.XMEAN;
            if (ICODE >= 1) {
                writeOutput(EOL + "                            MONTH = " + (imo + 1) + EOL);
                writeOutput("               SUMMARY OF WATER BUDGET COMPONENTS" + EOL);
                writeOutput("                  YEAR    ETP    RAIN  IRR.RQD." + EOL);
                for (int i = 0; i < NYR; i++) {
                    String row = String.format("                 %4d%8.2f%8.2f%8.2f" + EOL, i + 1, AETP[i][imo], ARAIN[i][imo], AIRR[i][imo]);
                    writeOutput(row);
                    if (imo+1 >= MO1 && imo+1 <=MON) {
                        summaryReport.setPeakMonthlyEvaporation(imo+1, AETP[i][imo]);
                    }
                    summaryReport.addTotalIrrigationRequiredByMonth(imo+1, AIRR[i][imo]);  
                    
                }
            }

        }
        if (ICODE >= 0) {
            writeOutput(EOL + EOL + "                2-WEEK NET IRRIGATION REQUIREMENT (INCHES)" + EOL);
            writeOutput("                ------------------------------------------");
            NX = -1;
            for (int i2w = 0; i2w < 26; i2w++) {
                int NN = NX + 1;
                NX = NN + 13;
                for (int iy = 0; iy < NYR; iy++) {
                    AETP[iy][i2w] = 0.0;
                    ARAIN[iy][i2w] = 0.0;
                    AIRR[iy][i2w] = 0.0;
                    for (int JD = NN; JD <= NX; JD++) {
                        AETP[iy][i2w] += ETP[iy][JD];
                        ARAIN[iy][i2w] += RAIN[iy][JD];
                        AIRR[iy][i2w] += IRR[iy][JD];
                    }
                }
                TET[i2w] = 0.0;
                TETP[i2w] = 0.0;
                TDR[i2w] = 0.0;
                TRAIN[i2w] = 0.0;

                for (int JD = NN; JD <= NX; JD++) {
                    TET[i2w] += SET[JD];
                    TETP[i2w] += SETP[JD];
                    TDR[i2w] += SDR[JD];
                    TRAIN[i2w] += SRAIN[JD];
                }
                TET[i2w] /= NYR;
                TETP[i2w] /= NYR;
                TDR[i2w] /= NYR;
                TRAIN[i2w] /= NYR;

                double AISUM = 0.0;
                for (int iy = 0; iy < NYR; iy++) {
                    AISUM += AIRR[iy][i2w];
                    AI[iy] = AIRR[iy][i2w];
                }
                if (ICODE >= 1 || (ICODE < 1 && i2w == 0)) {
                    writeOutput(EOL + " I2W  MEAN  MED.  CV XMAX XMIN ZERO  RSQ  50%  80%  90%  95% RAIN  ETP   ET   DR" + EOL);
                }
                statResult = STATX(AI, NYR);
                probResult = PROBX(AI, NYR, statResult.PROB, statResult.XMEAN);
                XIRR = TET[i2w] / IEFF;
                probResult.X50 = Math.min(probResult.X50, XIRR);
                probResult.X80 = Math.min(probResult.X80, XIRR);
                probResult.X90 = Math.min(probResult.X90, XIRR);
                probResult.X95 = Math.min(probResult.X95, XIRR);
                str = String.format(" %2d%5.1f%5.1f%5.2f%6.1f%5.1f%5.2f%5.2f%5.1f%5.1f%5.1f%5.1f%5.1f%5.1f%5.1f%5.1f", (i2w + 1),
                        statResult.XMEAN, statResult.XMED, statResult.XCV, statResult.XMAX, statResult.XMIN, probResult.X00, probResult.RSQ,
                        probResult.X50, probResult.X80, probResult.X90, probResult.X95, TRAIN[i2w], TETP[i2w], TET[i2w], TDR[i2w]);
                writeOutput(str + EOL);
                PDATBW[i2w] = statResult.XMEAN;
                pdat.PDATBW[i2w] = statResult.XMEAN;
                if (ICODE >= 1) {
                    writeOutput(EOL + "                       2-WEEK PERIOD = " + (i2w + 1) + EOL);
                    writeOutput("               SUMMARY OF WATER BUDGET COMPONENTS" + EOL);
                    writeOutput("                  YEAR    ETP    RAIN  IRR.RQD." + EOL);
                    for (int i = 0; i < NYR; i++) {
                        String row = String.format("                 %4d%8.2f%8.2f%8.2f" + EOL, i + 1, AETP[i][i2w], ARAIN[i][i2w], AIRR[i][i2w]);
                        writeOutput(row);
                    }
                }
            }
            if (isNet) {
                writeOutput(EOL + EOL + "                WEEKLY NET IRRIGATION REQUIREMENT (INCHES)" + EOL);
            } else {
                writeOutput(EOL + EOL + "                WEEKLY GROSS IRRIGATION REQUIREMENT (INCHES)" + EOL);
            }
            writeOutput("                ------------------------------------------");
            NX = -1;
            for (int i7 = 0; i7 < 52; i7++) {
                int NN = NX + 1;
                NX = NN + 6;
                for (int iy = 0; iy < NYR; iy++) {
                    AETP[iy][i7] = 0.0;
                    ARAIN[iy][i7] = 0.0;
                    AIRR[iy][i7] = 0.0;
                    for (int JD = NN; JD <= NX; JD++) {
                        AETP[iy][i7] += ETP[iy][JD];
                        ARAIN[iy][i7] += RAIN[iy][JD];
                        AIRR[iy][i7] += IRR[iy][JD];
                    }
                }
                TET[i7] = 0.0;
                TETP[i7] = 0.0;
                TDR[i7] = 0.0;
                TRAIN[i7] = 0.0;

                for (int JD = NN; JD <= NX; JD++) {
                    TET[i7] += SET[JD];
                    TETP[i7] += SETP[JD];
                    TDR[i7] += SDR[JD];
                    TRAIN[i7] += SRAIN[JD];
                }
                TET[i7] /= NYR;
                TETP[i7] /= NYR;
                TDR[i7] /= NYR;
                TRAIN[i7] /= NYR;

                double AISUM = 0.0;
                for (int iy = 0; iy < NYR; iy++) {
                    AISUM += AIRR[iy][i7];
                    AI[iy] = AIRR[iy][i7];
                }
                if (ICODE >= 1 || (ICODE < 1 && i7 == 0)) {
                    writeOutput(EOL + " IWK  MEAN  MED.  CV XMAX XMIN ZERO  RSQ  50%  80%  90%  95% RAIN  ETP   ET   DR" + EOL);
                }
                statResult = STATX(AI, NYR);
                probResult = PROBX(AI, NYR, statResult.PROB, statResult.XMEAN);
                XIRR = TET[i7] / IEFF;
                probResult.X50 = Math.min(probResult.X50, XIRR);
                probResult.X80 = Math.min(probResult.X80, XIRR);
                probResult.X90 = Math.min(probResult.X90, XIRR);
                probResult.X95 = Math.min(probResult.X95, XIRR);
                str = String.format(" %2d%5.1f%5.1f%5.2f%6.1f%5.1f%5.2f%5.2f%5.1f%5.1f%5.1f%5.1f%5.1f%5.1f%5.1f%5.1f", (i7 + 1),
                        statResult.XMEAN, statResult.XMED, statResult.XCV, statResult.XMAX, statResult.XMIN, probResult.X00, probResult.RSQ,
                        probResult.X50, probResult.X80, probResult.X90, probResult.X95, TRAIN[i7], TETP[i7], TET[i7], TDR[i7]);
                writeOutput(str + EOL);
                PDATW[i7] = statResult.XMEAN;
                pdat.PDATW[i7] = statResult.XMEAN;
                if (ICODE >= 1) {
                    writeOutput(EOL + "                       WEEKLY PERIOD = " + (i7 + 1) + EOL);
                    writeOutput("               SUMMARY OF WATER BUDGET COMPONENTS" + EOL);
                    writeOutput("                  YEAR    ETP    RAIN  IRR.RQD." + EOL);
                    for (int i = 0; i < NYR; i++) {
                        String row = String.format("                 %4d%8.2f%8.2f%8.2f" + EOL, i + 1, AETP[i][i7], ARAIN[i][i7], AIRR[i][i7]);
                        writeOutput(row);
                    }
                }
            }
        }
        pdat.soilName=SNAME;
        allSoilInfo.add(pdat);
    }
    
    

    public void initOutputFile(String fName) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(fName));
            String str1 = EOL;
            for (int i = 0; i < 70; i++) {
                str1 += '*';
            }
            String newStr = "                          AGRICULTURAL" + EOL
                    + "                          FIELD" + EOL
                    + "                          SCALE" + EOL
                    + "                          IRRIGATION" + EOL
                    + "                          REQUIREMENTS" + EOL
                    + "                          SIMULATION" + EOL
                    + EOL
                    + "                             MODEL" + EOL
                    + EOL
                    + EOL
                    + "              AFSIRS MODEL: INTERACTIVE VERSION "+Messages.MAX_VERSION+"."+Messages.MIN_VERSION + EOL
                    + EOL
                    + EOL
                    + "           THIS MODEL SIMULATES IRRIGATION REQUIREMENTS" + EOL
                    + "         FOR FLORIDA CROPS, SOILS, AND CLIMATE CONDITIONS." + EOL
                    + EOL
                    + "      PROBABILITIES OF OCCURRENCE OF IRRIGATION REQUIREMENTS" + EOL
                    + "        ARE CALCULATED USING HISTORICAL WEATHER DATA BASES" + EOL
                    + "                   FOR NINE FLORIDA LOCATIONS." + EOL;

            bw.append(str1 + EOL + EOL);
            bw.append(newStr);
            bw.append(str1 + EOL + EOL);
            newStr = "          INSTRUCTIONS FOR THE USE OF THIS MODEL ARE GIVEN" + EOL
                    + "                 IN THE AFSIRS MODEL USER'S GUIDE." + EOL
                    + "" + EOL
                    + "       DETAILS OF THE OPERATION OF THIS MODEL, ITS APPLICATIONS" + EOL
                    + "    AND LIMITATIONS ARE GIVEN IN THE AFSIRS MODEL TECHNICAL MANUAL." + EOL;

            bw.append(newStr);
            bw.append(str1 + EOL + EOL);

            newStr = "              AFSIRS MODEL: INTERACTIVE VERSION "+Messages.MAX_VERSION+"."+Messages.MIN_VERSION + EOL
                    + EOL
                    + EOL
                    + "           THIS MODEL SIMULATES IRRIGATION REQUIREMENTS" + EOL
                    + "         FOR FLORIDA CROPS, SOILS, AND CLIMATE CONDITIONS." + EOL;
            bw.append(newStr);
            bw.append(str1 + EOL + EOL);
            bw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void finishInput() {
        try {
            bwOutputFile = new BufferedWriter(new FileWriter(OUTFIL, true));
            //bwOutputSummaryFile = new BufferedWriter(new FileWriter(SUMMARYFILE, true)); 
            bwOutputSummaryFile = new Document(); 
            PdfWriter.getInstance(bwOutputSummaryFile, new FileOutputStream(SUMMARYFILE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        DECOEF();

        initOutputFile(OUTFIL);
        bwOutputSummaryFile.open();
        formatSummaryOutputFile ();
        
        writeOutput("SITE = " + SITE + "     UNIT = "+ UNIT + "     OWNER = "+ OWNER+"     DATE = " + MONTH + "-" + IIDAY + "-" + IYEAR + "" + EOL);
        writeOutput(EOL + "                      CROP TYPE = " + CTYPE + EOL);
        writeOutput(EOL + " IRRIGATION SEASON = " + MO1 + "-" + DAY1 + " TO " + MON + "-" + DAYN + "                LENGTH = " + NDAYS + " DAYS" + EOL);
        writeOutput(EOL + " CLIMATE DATA BASE: LOCATION = " + ALOC + " LENGTH = " + NYR + " YEARS" + EOL);
        switch (IDCODE) {
            case 0:
                writeOutput("         NORMAL IRRIGATION: SOIL WILL BE IRRIGATED TO FIELD CAPACITY AT EACH IRRIGATION" + EOL);
                break;
            case 1:
                writeOutput("         FIXED DEPTH IRRIGATION: A FIXED (CONSTANT) DEPTH OF WATER WILL BE APPLIED AT EACH IRRIGATION" + EOL);
                writeOutput("         DEPTH OF WATER TO APPLY PER IRRIGATION = " + FIX + EOL);
                break;
            case 2:
                writeOutput("         DEFICIT IRRIGATION: THE SOIL WILL BE IRRIGATED TO A FRACTION OF FIELD CAPACITY AT EACH IRRIGATION" + EOL);
                writeOutput("         PERCENT OF FIELD CAPACITY ENTERED FOR DEFICIT IRRIGATION" + PIR + EOL);
                break;
            default:
                writeOutput(EOL);
        }
        writeOutput(EOL + " IRRIGATION SYSTEM : TYPE = " + IRNAME + EOL);
        writeOutput("                     DESIGN APPLICATION EFFICIENCY = " + (int) (100 * IEFF) + " %" + EOL);
        writeOutput("                     FRACTION OF SOIL SURFACE IRRIGATED = " + (int) (100 * ARZI) + " %" + EOL);
        writeOutput("                     FRACTION EXTRACTED FROM IRRIGATED ZONE = " + EXIR + EOL);
        if (IR == IRCRFL) {
            writeOutput("           HEIGHT OF CROWN FLOOD IRRIGATION SYSTEM BEDS = " + HGT + " FT" + EOL);
        }

        if (isPerennial) {
            writeOutput(EOL + "                      CROP TYPE = " + CTYPE + "(PERENNIAL)" + EOL);
            writeOutput(EOL + "               ROOT ZONE DEPTH IRRIGATED (INCHES) = " + DRZIRR);
            writeOutput(EOL + "               TOTAL CROP ROOT ZONE DEPTH (INCHES)= " + DRZTOT + EOL);
            writeOutput(EOL + "                                MONTH" + EOL + "         ");
            for (int i = 0; i < 12; i++) {
                writeOutput(String.format("%5d", (i + 1)));
            }
            writeOutput(EOL + "     KC= ");
            for (int i = 0; i < 12; i++) {
                writeOutput(String.format(" %.2f", AKC[i]));
            }
            writeOutput(EOL + "   ALDP= ");
            for (int i = 0; i < 12; i++) {
                writeOutput(String.format(" %.2f", ALDP[i]));
            }

        } else {
            writeOutput(EOL + "                      CROP TYPE = " + CTYPE + "(ANNUAL)");
            writeOutput(EOL + "    DZN   DZX  AKC3  AKC4    F1    F2    F3    F4  ALD1  ALD2  ALD3  ALD4" + EOL);
            writeOutput(String.format("    %3.1f  %3.1f  %3.2f  %3.2f  %3.2f  %3.2f  %3.2f  %3.2f  %3.2f  %3.2f  %3.2f  %3.2f\r\n", DZN, DZX,
                    AKC3, AKC4, F[0], F[1], F[2], F[3], ALD[0], ALD[1], ALD[2], ALD[3]));
            if (ICODE == 2) {
                double ff = EPS;
                for (int i = 0; i < 4; i++) {
                    String row = EOL + "          I,FF,NF(I),J1,JN = ";
                    ff += F[i];
                    writeOutput(String.format("%s %5d%7.3f%5d%5d%5d", row, (i + 1), ff, NF[i], J1, JN));
                }
                writeOutput(EOL + "C   CDAY = CONSECUTIVE DAYS OF CROP IRRIGATION SEASON");
                writeOutput(EOL + "C   DRZI(JD) = CROP ROOT ZONE DEPTH IRRIGATED (INCHES)");
                writeOutput(EOL + "C   DRZ(JD) = TOTAL EFFECTIVE CROP ROOT ZONE DEPTH (INCHES)");
                
                writeOutput(EOL + EOL + "   CDAY JDAY NF1  NF2  NF3  DRZI DRZ  RKC ALD1 ALD2 ALD3 ALD4   AWD" + EOL);
                for (int i = J1 - 1; i < JN; i++) {
                    writeOutput(String.format(" %5d%5d%5d%5d%5d %3.1f %3.1f %.2f %.2f %.2f %.2f %.2f  %.2f\r\n", i + 1, JDAY[i], NF[0], NF[1], NF[2], DRZI[i], DRZ[i], RKC[i], ALD[0], ALD[1], ALD[2], ALD[3], AWD[i]));
                }
            }
        }

        if (ICODE >= 1) {
            writeOutput(EOL + "C   CDAY = CONSECUTIVE DAYS OF CROP IRRIGATION SEASON");
            writeOutput(EOL + "C   DRZI(JD) = CROP ROOT ZONE DEPTH IRRIGATED (INCHES)");
            writeOutput(EOL + "C   DRZ(JD) = TOTAL EFFECTIVE CROP ROOT ZONE DEPTH (INCHES)");
            writeOutput(EOL + " DAILY ROOT ZONE DEPTHS, CROP COEFFICIENTS, & ALLOWABLE WATER DEPLETIONS" + EOL);
            writeOutput("               CDAY JDAY  DRZI(JD) DRZ(JD) RKC(JD) AWD(JD)" + EOL);
            for (int i = J1 - 1; i < JN; i++) {
                String row = String.format("               %3d %4d %9.2f %7.2f %7.3f %7.2f", (i + 1), JDAY[i], DRZI[i], DRZ[i], RKC[i], AWD[i]);
                writeOutput(row + EOL);
            }
        }
        
        //Don'tIn Delete the below commented code. This is very important for reference
        
        /*SW();
        String txt = "";
        for (String tIn : TXT) {
            if (tIn != null) {
                txt += tIn + " ";
            }
        }
        writeOutput(EOL + EOL + "     SOIL :  SERIES = " + SNAME + "         TEXTURE = " + txt + EOL + EOL);
        writeOutput(EOL + "               SOIL LAYER DEPTHS (INCHES) AND WATER CONTENTS" + EOL);
        String str = "";
        str += "                   Depth(I)        WCON(Min)    WCON(Max)\r\n";
        for (int i = 0; i < NL; i++) {
            str += String.format("                %2d%8.1f%18.2f%12.2f" + EOL, i + 1, DU[i], Math.round(WCL[i] * 100.0) / 100.0, Math.round(WCU[i] * 100.0) / 100.0);
        }
        writeOutput(str);

        writeOutput(EOL + "                DEPTH TO WATER TABLE ENTERED =  " + DWT / 12.0 + " FEET" + EOL + EOL);

        if (ICODE >= 1) {
            writeOutput(EOL + "     OUTPUT PARAMETERS - ROOT DEPTHS, KCs, AND SOIL WATER CONTENTS" + EOL);
            writeOutput("       CDAY JDAY   DRZ    DRZI   RKC   SWMAX  SWCIX  SWCNX  SWIRR" + EOL + "       ");
            for (int i = J1 - 1; i < JN; i++) {
                String row = String.format("%4d %4d %6.1f %6.1f %6.3f %6.2f %6.2f %6.2f %6.2f", (i + 1), JDAY[i], DRZ[i], DRZI[i], RKC[i], SWMAX[i], SWCIX[i], SWCNX[i], SWIRR[i]);
                writeOutput(row + EOL + "       ");
            }
        }
        calculateBalance();
        SUMX();*/
        getDataCopy ();
        summaryTables = new ArrayList<PdfPTable> ();
        try {
   
            ArrayList<Soil> soils = soilData.getSoils();

            
            soilFractions = new double[soils.size()];
            soilArea = new double[soils.size()];
            
            int i = 0;
            for (Soil soil : soils) {
                soilArea [i] = soil.getSoilTypeArea();
                i++;
            }
            
            i = 0;
            for (Soil soil : soils) {
                //setSoilData(soil);
                this.soil = soil;
                if(i>0)
                    soilFractions[i] = soilFractions[i-1];
                soilFractions[i]+=soil.getSoilTypeArea();                
                
                SNAME = soil.getName();
                TXT = soil.getTXT();
                DU = soil.getDU();
                WCL = soil.getWCL();
                WCU = soil.getWCU();
                WC = soil.getWC();
                NL = soil.getNL();
            
                resetAllocate();
                SW();
                calculateBalance();
                SUMX();
                finalSummaryOutput();
                
                i++;
            }
            setIrrigationWeightedAverage ();
            bwOutputSummaryFile.add(new Paragraph("\r\n"));
            if (summaryTables.size()>=2) {
                bwOutputSummaryFile.add(summaryTables.get(0));
                bwOutputSummaryFile.add(new Paragraph("\r\n"));
                
                // This is for the weighted Average of the irrigation
                bwOutputSummaryFile.add(summaryTables.get(1));
                bwOutputSummaryFile.add(new Paragraph("\r\n"));
                
                // This is for the weighted Average of the irrigation
                bwOutputSummaryFile.add(summaryTables.get(2));
                bwOutputSummaryFile.add(new Paragraph("\r\n"));
            }
            for(int inx = 3; inx <summaryTables.size(); inx++) {
                PdfPTable t =summaryTables.get(inx);
                bwOutputSummaryFile.add(t);
            }

            bwOutputFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        bwOutputSummaryFile.close();
    }

    private void addParagraphToTable(PdfPTable table, String str) {

        Paragraph p = new Paragraph(str, BLUE_NORMAL);
        p.setAlignment(Element.ALIGN_CENTER);

        PdfPCell c = new PdfPCell();
        c.addElement(p);
        c.setBorder(0);
        table.addCell(c);
    }

    private void addParagraphToTableSoilName(PdfPTable t, String key, String value) {
        PdfPCell c;
        Paragraph p = new Paragraph();

        Chunk keyChunk = new Chunk(key, BLACK_NORMAL);
        Chunk valChunk = new Chunk(value, BLACK_BOLD);

        p.add(keyChunk);
        p.add(valChunk);

        c = new PdfPCell(p);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setBorder(0);
        t.addCell(c);
    }

    private void addUserDetails(PdfPTable t, String key, String value) {
        PdfPCell c;
        Paragraph p = new Paragraph();

        Chunk keyChunk = new Chunk(key, BLACK_NORMAL);
        Chunk valChunk = new Chunk(value, BLACK_BOLD);

        p.add(keyChunk);
        p.add(valChunk);

        c = new PdfPCell(p);
        c.setHorizontalAlignment(Element.ALIGN_LEFT);
        c.setBorder(0);
        t.addCell(c);
    }

    public void formatSummaryOutputFile() {
        try {
            PdfPTable t = new PdfPTable(1);
            for (String s : DOC_HEADER) {
                addParagraphToTable(t, s);
            }
            bwOutputSummaryFile.add(t);

            t = new PdfPTable(3);
            addUserDetails(t, USER_DETAILS[0], getOWNER());
            addUserDetails(t, USER_DETAILS[1], getSITE());
            addUserDetails(t, USER_DETAILS[2], getUNIT());

            addUserDetails(t, USER_DETAILS[3], getCropName());
            addUserDetails(t, USER_DETAILS[4], getIrrigationSystemName());
            addUserDetails(t, USER_DETAILS[5], "");

            addUserDetails(t, USER_DETAILS[6], "");
            addUserDetails(t, USER_DETAILS[7], String.valueOf(PLANTEDACRES));
            addUserDetails(t, USER_DETAILS[8], "");

            bwOutputSummaryFile.add(t);

        } catch (DocumentException ex) {

        }
    }

    private void prepareWeightedAverageTable () throws DocumentException {
        PdfPTable tableWeightedInches = new PdfPTable(14);

        tableWeightedInches.setTotalWidth(new float[]{190, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 120});
        designTableTitleCell(tableWeightedInches, "Irrigation Weighted Average (Inches)");
        createTableHeader(tableWeightedInches);
        summaryTables.add(tableWeightedInches);
        
        PdfPTable tableWeightedGallon = new PdfPTable(14);
        tableWeightedGallon.setTotalWidth(new float[]{190, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 120});
        designTableTitleCell(tableWeightedGallon, "Irrigation Weighted Average (Gallons)");
        createTableHeader(tableWeightedGallon);
        summaryTables.add(tableWeightedGallon);
    }
    
    private void  setIrrigationWeightedAverage () throws DocumentException {
        PdfPTable tIn = summaryTables.get(1);
        PdfPTable tGa = summaryTables.get(2);

        designRowTitleCell(tIn, "Avg Irr Req");
        designRowTitleCell(tGa, "Avg Irr Req");

        double totalVal = 0.0;
        double totalValGa = 0.0;
        String str = "";
        
        // Find the weighted average of the irrigation
        for ( int i = 1; i <=12; i++) {
            double irr = summaryReport.getWeightedAvgIrrRequired(i);
            double irrGa = irr * PLANTEDACRES * 27154;
            irrGa = irrGa / 1000000;

            str =  String.format("%6.2f", irr);
            designDataCell(tIn, String.valueOf(str));
            if (irr>=0){
                totalVal += irr;
            }

            str =  String.format("%6.2f", irrGa);
            designDataCell(tGa, String.valueOf(str));
            if (irrGa>=0){
                totalValGa += irrGa;
            }
        }

        str = String.format("%6.2f", totalVal);
        designDataCell(tIn, str);

        str = String.format("%6.2f", totalValGa);
        designDataCell(tGa, str);
        
        designRowTitleCell(tIn, "2-In-10 Irr Req");
        designRowTitleCell(tGa, "2-In-10 Irr Req");

        totalVal=0.0;
        totalValGa=0.0;

        // Find the weighted average of the irrigation
        for ( int i = 1; i <=12; i++) {            
            double irr = summaryReport.getWeighted2In10IrrRequired(i);

            
            
            str =  String.format("%6.2f", irr);;
            designDataCell(tIn, String.valueOf(str));
            
            if (irr>=0){
                totalVal += irr;
            }
            
            double irrGa = irr * PLANTEDACRES * 27154;
            irrGa = irrGa / 1000000;
            str =  String.format("%6.2f", irrGa);
            designDataCell(tGa, String.valueOf(str));
            if (irrGa>=0){
                totalValGa += irrGa;
            }
        }
        str = String.format("%6.2f", totalVal);
        designDataCell(tIn, str);
        
        str = String.format("%6.2f", totalValGa);
        designDataCell(tGa, str);

        designRowTitleCell(tIn, "1-In-10 Irr Req");
        designRowTitleCell(tGa, "1-In-10 Irr Req");
        
        totalVal=0.0;
        totalValGa=0.0;

        // Find the weighted average of the irrigation
        for ( int i = 1; i <=12; i++) {            
            double irr = summaryReport.getWeighted1In10IrrRequired(i);
            str =  String.format("%6.2f", irr);
            designDataCell(tIn, String.valueOf(str));
            if (irr>=0){
                totalVal += irr;
            }
            
            double irrGa = irr * PLANTEDACRES * 27154;
            irrGa = irrGa / 1000000;
            str =  String.format("%6.2f", irrGa);
            designDataCell(tGa, String.valueOf(str));
            if (irrGa>=0){
                totalValGa += irrGa;
            }
        }

        str = String.format("%6.2f", totalVal);
        designDataCell(tIn, str);
        str = String.format("%6.2f", totalValGa);
        designDataCell(tGa, str);
    }
    
    private void finalSummaryOutput() {
        try {
            String str = "";
            String str1 = "";

            double area = PLANTEDACRES;

            if (summaryTables.size()<2) {
                generalInformation(summaryReport, bwOutputSummaryFile);
                prepareWeightedAverageTable ();
            }
            
            PdfPTable t = new PdfPTable(1);
            addParagraphToTableSoilName(t, " ", " ");
            addParagraphToTableSoilName(t, " ", " ");
            addParagraphToTableSoilName(t, "Soil Series Name : ", SNAME);
            summaryTables.add(t);
            //bwOutputSummaryFile.add(tIn);
            
            infoInInches(bwOutputSummaryFile, summaryReport);
            probablityInfoInGallons(bwOutputSummaryFile, summaryReport, area);

        } catch (DocumentException ex) {
            Logger.getLogger(AFSIRSUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void designTableTitleCell(PdfPTable table, String str) {
        PdfPCell cell;
        // we add a c with colspan 3
        cell = new PdfPCell(new Phrase(str, BLUE_NORMAL));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.ORANGE);
        cell.setColspan(14);
        table.addCell(cell);
    }

    private void designRowTitleCell(PdfPTable table, String str) {
        PdfPCell cell;
        // we add a c with colspan 3
        cell = new PdfPCell(new Phrase(str, BLUE_NORMAL));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.ORANGE);
        //cell.setColspan(14);
        table.addCell(cell);
    }

    private void designTableHeaderRowCell(PdfPTable table, String str) {
        PdfPCell cell;
        // we add a c with colspan 3
        cell = new PdfPCell(new Phrase(str, BLUE_NORMAL));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.ORANGE);
        //cell.setColspan(14);
        table.addCell(cell);
    }

    private void designDataCell(PdfPTable table, String str) {
        PdfPCell cell;
        // we add a c with colspan 3
        cell = new PdfPCell(new Phrase(str, BLACK_NORMAL));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.CYAN);
        //cell.setColspan(14);
        table.addCell(cell);
    }

    private void generalInformation(SummaryReport summaryReport1, Document bwOutputSummaryFile1) throws DocumentException {
        // a table with three columns
        PdfPTable table = new PdfPTable(14);

        table.setTotalWidth(new float[]{190, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 120});
        designTableTitleCell(table, "Details in Inches");
        createTableHeader(table);
        /**
         * *************Mean Rainfall Details*****************
         */

        designRowTitleCell(table, "Mean Rainfall");
        double totalVal = 0.0;
        String str = "";
        for (int i = 1; i <= 12; i++) {
            double val = summaryReport1.getTotalRainFallByMonth(i);
            if (val>=0)
                totalVal += val;
            str = String.format("%6.2f", val);
            designDataCell(table, str);
        }
        str = String.format("%6.2f", totalVal);
        designDataCell(table, str);
        /**
         * *************Mean Evaporation*****************
         */
        designRowTitleCell(table, "Mean ET");
        totalVal = 0.0;
        str = "";
        for (int i = 1; i <= 12; i++) {
            double val = summaryReport1.getTotalEvaporationByMonth(i);
            if (val>=0)
                totalVal += val;
            str = String.format("%6.2f", val);
            designDataCell(table, str);
        }
        str = String.format("%6.2f", totalVal);
        designDataCell(table, str);
        /**
         * *********Peak Evaporation Details************
         */
        designRowTitleCell(table, "Peak ET");
        totalVal = 0.0;
        str = "";

        for (int i = 1; i <= 12; i++) {
            double val = summaryReport1.getPeakEvaporationByMonth(i);
            if (val>=0)
                totalVal += val;
            str = String.format("%6.2f", val);
            designDataCell(table, str);
        }
        str = String.format("%6.2f", totalVal);
        designDataCell(table, str);
        summaryTables.add(table);
        
    }

    private void infoInInches(Document bwOutputSummaryFile1, SummaryReport summaryReport1) throws DocumentException {
        PdfPTable table;
        PdfPCell cell;
        double totalVal;
        String str;
        //bwOutputSummaryFile1.add(new Paragraph("\r\n"));
        table = new PdfPTable(14);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.setTotalWidth(new float[]{190, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 120});

        designTableTitleCell(table, "Details in Inches");
        createTableHeader(table);
        
        
        double [] soilArea = getSoilArea();
        double areaSum=0.0;
        
        for (double a: soilArea){
             areaSum+=a;
        }

        /**
         * *********Peak Evaporation Details************
         */
        designRowTitleCell(table, "Avg Irr Req");
        totalVal = 0.0;
        for (int i = 1; i <= 12; i++) {
            double val = summaryReport1.getAverageIrrigationRequired(i);
            if (val>=0) {
                double wIrr = (val * this.soil.getSoilTypeArea())/areaSum;
                summaryReport.setWeightedAvgIrrRequired(i, wIrr);                
                totalVal += val;
            }
            str = String.format("%6.2f", val);
            designDataCell(table, str);
        }
        str = String.format("%6.2f", totalVal);
        designDataCell(table, str);
        /**
         * *********2-in-10 Irrigation Required************
         */
        designRowTitleCell(table, "2-in-10 Irr Req");
        totalVal = 0.0;
        for (int i = 1; i <= 12; i++) {
            double val = summaryReport1.getTwoin10IrrigationRequired(i);
            if (val>=0) {
                double wIrr = (val * this.soil.getSoilTypeArea())/areaSum;
                summaryReport.setWeighted2In10IrrRequired(i, wIrr);
                totalVal += val;
            }

            str = String.format("%6.2f", val);
            designDataCell(table, str);
        }
        str = String.format("%6.2f", totalVal);
        designDataCell(table, str);
        /**
         * *********1-in-10 Irrigation Required************
         */
        designRowTitleCell(table, "1-in-10 Irr Req");
        totalVal = 0.0;
        for (int i = 1; i <= 12; i++) {
            double val = summaryReport1.getOnein10IrrigationRequired(i);
            if (val>=0) {
                double wIrr = (val * this.soil.getSoilTypeArea())/areaSum;
                summaryReport.setWeighted1In10IrrRequired(i, wIrr);                
                totalVal += val;
            }
        
            str = String.format("%6.2f", val);
            designDataCell(table, str);
        }
        str = String.format("%6.2f", totalVal);
        designDataCell(table, str);
        summaryTables.add(table);
    }

    private void createTableHeader(PdfPTable table) {
        for (String str : Messages.TABLE_HEADER) {
            designTableHeaderRowCell(table, str);
        }
    }

    private PdfPTable probablityInfoInGallons(Document bwOutputSummaryFile1, SummaryReport summaryReport1, double area) throws DocumentException {
        PdfPTable table;
        PdfPCell cell;
        double totalVal;
        String str;
        //bwOutputSummaryFile1.add(new Paragraph("\r\n"));
        table = new PdfPTable(14);

        table.setTotalWidth(new float[]{190, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 80, 120});
        designTableTitleCell(table, "Details in Million Gallons");
        createTableHeader(table);

        /**
         * *********Peak Evaporation Details************
         */
        designRowTitleCell(table, "Avg Irr Req");
        totalVal = 0.0;
        for (int i = 1; i <= 12; i++) {
            double val = summaryReport1.getAverageIrrigationRequired(i);
            if (val >= 0) {
                val = (val * area * 27154);
                val = (val / 1000000);
                str = String.format("%12.2f", val);
                totalVal += val;
            } else {
                str = String.format("%12.2f", val);
            }
            
            str = String.format("%6.2f", val);
            designDataCell(table, str);

        }
        str = String.format("%6.2f", totalVal);
        //str = "-";
        designDataCell(table, str);

        /**
         * *********2-in-10 Irrigation Required************
         */
        designRowTitleCell(table, "2-in-10 Irr Req");
        totalVal = 0.0;
        for (int i = 1; i <= 12; i++) {
            double val = summaryReport1.getTwoin10IrrigationRequired(i);
            if (val >= 0) {
                val = (val * area * 27154);
                val = (val / 1000000);
                str = String.format("%12.2f", val);
                totalVal += val;
            } else {
                str = String.format("%12.2f", val);
            }

            str = String.format("%6.2f", val);
            designDataCell(table, str);
        }
        str = String.format("%6.2f", totalVal);
        //str = "-";
        designDataCell(table, str);
        /**
         * *********1-in-10 Irrigation Required************
         */
        designRowTitleCell(table, "1-in-10 Irr Req");
        totalVal = 0.0;
        for (int i = 1; i <= 12; i++) {
            double val = summaryReport1.getOnein10IrrigationRequired(i);
            if (val >= 0) {
                val = (val * area * 27154);
                val = (val / 1000000);
                str = String.format("%12.2f", val);
                totalVal += val;
            } else {
                str = String.format("%12.2f", val);
            }
            
            str = String.format("%6.2f", val);
            designDataCell(table, str);
        }
        str = String.format("%6.2f", totalVal);
        //str = "-";
        designDataCell(table, str);
        summaryTables.add(table);
        //bwOutputSummaryFile1.add(table);
        return table;
    }

    public String appendSpace(int n, String str) {
        StringBuffer str1 = new StringBuffer();
        for (int i = 0; i < n; i++) {
            str1.append(' ');
        }

        return str1.append(str).toString();

    }
    
    public double[] getFractions(){
        return soilFractions;
    }
    
    public double [] getSoilArea() {
        return soilArea;
    }
    
}

class LSQResult {

    double slope;
    double intercept;
    double RSQ;
    double R;

    public LSQResult(double slope, double intercept, double RSQ, double R) {
        this.slope = slope;
        this.intercept = intercept;
        this.RSQ = RSQ;
        this.R = R;
    }
}

class PROBResult {

    double X00, X50, X80, X90, X95, RSQ;

    public PROBResult() {

    }

    public void setAll(double val, double rsq) {
        X00 = X50 = X80 = X90 = X95 = val;
        RSQ = rsq;
    }
}

class STATResult {

    double[] PROB = new double[64];
    double XMEAN, XVAR, XSDEV, XMAX, XMIN, XMED, XCV;

    public STATResult(int N) {
        for (int i = 0; i < N; i++) {
            PROB[i] = 1.0;
        }
    }

}

class PDAT {
    String soilName = "";
    Double[] PDATM = new Double[12];
    Double[] PDATBW = new Double[26];
    Double[] PDATW = new Double[52];
}