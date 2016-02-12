/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.agmip.ui.afsirs.util;

/**
 *
 * @author  Rohit Kumar Malik
 * 
 */
public class SoilData {
    int ID;
    String SNAME;
    String[] TXT = new String[3];
    int NL;
    double[] WC;
    double[] WCL;
    double[] WCU;
    double[] DU;
    
    double soilTypeArea;
    

    public SoilData(int id, String name, int nl){
        ID = id;
        SNAME = name;
        NL = nl;
    }
    
    public void setValues(double[] wc, double[] wcl, double[] wcu, double[] du, String[] txt){
        WC = wc;
        WCL = wcl;
        WCU = wcu;
        DU = du;
        TXT = txt;
    }

    public String getName(){
        return SNAME;
    }
    
    public String[] getTXT(){
        return TXT;
    }
    public double[] getWC(){
        return WC;
    }
    
    public double[] getWCL(){
        return WCL;
    }
    
    public double[] getWCU(){
        return WCU;
    }
    
    public double[] getDU(){
        return DU;
    }
    
    public int getNL(){
        return NL;
    }
    
        
    public void setSoilTypeArea (double soilTypeArea) {
        this.soilTypeArea = soilTypeArea;
    }
    
    public double getSoilTypeArea () {
        return this.soilTypeArea;
    }
}
