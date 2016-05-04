/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.agmip.ui.afsirs.util;

import javax.swing.JFrame;

/**
 *
 * @author rohit
 */
public class FrameTracker {
    
    public static JFrame siteInfoFrame = null;
    public static JFrame soilData = null;
    public static JFrame soilDataNext = null;
   
    
    public static void resetFrames () {
        siteInfoFrame = null;
        soilData = null;
        soilDataNext = null;
    }
}
