/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.agmip.ui.afsirs.util;

/**
 *
 * @author rohit
 */
public class Messages {
    
    private final static String EOL = "\r\n";
    
    public static final int MAX_VERSION = 6;
    public static final int MIN_VERSION = 2;
    
    public static final String FILE_NOT_FOUND_MESSAGE = "\tRequired!\nNo Soil file found in Downloads folder. Download Soil file from map.";
    public static final String FILE_KEY = "soilfile";
    public static final String KEYBOARD_KEY = "keyboard";
    public static final String TABLE_HEADER [] = {"", "Jan",  "Feb",  "Mar",  "Apr",  "May",  "Jun",  "Jul", "Aug",  "Sep",  "Oct",  "Nov",  "Dec",  "Total"};
    
    public static final String DOC_HEADER [] = {
                        "AGRICULTURAL",
                        "FIELD",
                        "SCALE",
                        "IRRIGATION",
                        "REQUIREMENTS",
                        "SIMULATION",
                        "MODEL",
                        "AFSIRS MODEL: INTERACTIVE VERSION "+Messages.MAX_VERSION+"."+Messages.MIN_VERSION,
                        "THIS MODEL SIMULATES IRRIGATION REQUIREMENTS",
                        "FOR FLORIDA CROPS, SOILS, AND CLIMATE CONDITIONS.",
                        "PROBABILITIES OF OCCURRENCE OF IRRIGATION REQUIREMENTS",
                        "ARE CALCULATED USING HISTORICAL WEATHER DATA BASES",
                        "FOR NINE FLORIDA LOCATIONS.",
                        "INSTRUCTIONS FOR THE USE OF THIS MODEL ARE GIVEN",
                        "IN THE AFSIRS MODEL USER'S GUIDE.",
                        "DETAILS OF THE OPERATION OF THIS MODEL, ITS APPLICATIONS",
                        "AND LIMITATIONS ARE GIVEN IN THE AFSIRS MODEL TECHNICAL MANUAL.",
                        "AFSIRS MODEL: INTERACTIVE VERSION "+Messages.MAX_VERSION+"."+Messages.MIN_VERSION,
                        "THIS MODEL SIMULATES IRRIGATION REQUIREMENTS",
                        "FOR FLORIDA CROPS, SOILS, AND CLIMATE CONDITIONS.",
                        " ",
                        " "
    };
    
    
    public static final String DOC_HEADER_EXCEL = 
                        "AGRICULTURAL" + EOL + 
                        "FIELD"+ EOL +
                        "SCALE"+ EOL +
                        "IRRIGATION"+ EOL +
                        "REQUIREMENTS"+ EOL +
                        "SIMULATION" + EOL +
                        "MODEL"+ EOL +
                        "AFSIRS MODEL: INTERACTIVE VERSION " + Messages.MAX_VERSION+"."+Messages.MIN_VERSION+ EOL +
                        "THIS MODEL SIMULATES IRRIGATION REQUIREMENTS"+ EOL +
                        "FOR FLORIDA CROPS, SOILS, AND CLIMATE CONDITIONS."+ EOL +
                        "PROBABILITIES OF OCCURRENCE OF IRRIGATION REQUIREMENTS"+ EOL +
                        "ARE CALCULATED USING HISTORICAL WEATHER DATA BASES"+ EOL +
                        "FOR NINE FLORIDA LOCATIONS."+ EOL +
                        "INSTRUCTIONS FOR THE USE OF THIS MODEL ARE GIVEN"+ EOL +
                        "IN THE AFSIRS MODEL USER'S GUIDE."+ EOL +
                        "DETAILS OF THE OPERATION OF THIS MODEL, ITS APPLICATIONS"+ EOL +
                        "AND LIMITATIONS ARE GIVEN IN THE AFSIRS MODEL TECHNICAL MANUAL."+ EOL +
                        "AFSIRS MODEL: INTERACTIVE VERSION "+Messages.MAX_VERSION+"."+Messages.MIN_VERSION+ EOL +
                        "THIS MODEL SIMULATES IRRIGATION REQUIREMENTS"+ EOL +
                        "FOR FLORIDA CROPS, SOILS, AND CLIMATE CONDITIONS."+ EOL +
                        " "+ EOL +
                        " ";
    
    public static final String USER_DETAILS [] = {
                        "Owner : "  ,
                        "Site : ",
                        "Unit : ",
                        "Crop : ",
                        "Irrigation Method : ",
                        "Simulation Start Date : ",
                        "Simulation End Date : ",
                        "Area (ACRES): ",
                        "Climate Station : "
    };
    
        
    public static final String USER_DETAILS_EXCEL [] = {
                        "Owner"  ,
                        "Site",
                        "Unit",
                        "Crop",
                        "Irrigation Method",
                        "Simulation Start Date",
                        "Simulation End Date",
                        "Area (ACRES)",
                        "Climate Station"
    };
    
    
    public static final String FOOTNOTE [] = {
                        "Avg Irrigation Requirement refers to 50% Probablity."  ,
    };
    

}