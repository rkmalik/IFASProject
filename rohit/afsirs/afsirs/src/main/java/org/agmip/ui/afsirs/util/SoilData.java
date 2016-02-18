/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.agmip.ui.afsirs.util;

import java.util.ArrayList;

/**
 *
 * @author rohit
 */
public class SoilData {
    
    private ArrayList<Soil> soils = new ArrayList<> ();
    
    public SoilData () {
    }
   
    public void addSoil (Soil soil) {
        soils.add(soil);
    }

    public ArrayList<Soil> getSoils () {
        return soils;
    }
}
