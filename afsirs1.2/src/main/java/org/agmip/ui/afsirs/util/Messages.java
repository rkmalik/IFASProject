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
    
    public static final String USER_DETAILS [] = {
                        "Owner : "  ,
                        "Site : ",
                        "Unit : ",
                        "Crop : ",
                        "Irrigation Method : ",
                        "Planting Date : ",
                        "Harvest Date : ",
                        "Area (ACRES): ",
                        ""
    };

}