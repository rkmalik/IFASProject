/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dssat;

import com.opencsv.CSVReader;
import static dssat.DSSATMain.curdirpath;
import static dssat.DSSATMain.datadir;
import static dssat.DSSATMain.dirseprator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;

/**
 *
 * @author rkmalik
 */
public class SoilDataGenerator {   
    
    public String ReadSoilData (String countyName, String soilid, String outputPath) {        

        String filepath = datadir+dirseprator+soilid+".SOL";
        System.out.println ("Soil File = " + filepath);
        String datastring = "*" + soilid;
        System.out.println(filepath);
        boolean isdatablock = false;
        
        outputPath += dirseprator+soilid+".SOL";
                
        /*String finalOutputPath = readAttribute("DssatFolder");
        finalOutputPath = finalOutputPath + "\\" + readAttribute("Crop");*/
        
        
        try {
            
	    BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/properties/csv/FL.sol")));
            // Open  the file to write the data to the file 
            File outputfile = new File (outputPath);
            
            System.out.println ("Rohit Created File : " + outputPath);
            if (!outputfile.exists()) {
                outputfile.createNewFile();
            }
            FileWriter fw = new FileWriter(outputfile.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            
            
            String str;
	    while ((str = in.readLine()) != null){	    		
                // Check if a line starts with datastring the we need to start re
                if (isdatablock == false) {
                    
                    if (str.startsWith(datastring)){
                        isdatablock = true;
                        bw.write(str + "\r\n");
                    }                  
                   
                } else {
                    
                    if (str.startsWith("*")) {
                        break;
                    }
                    // Break when we encounter another astrix in the begining of the line 
                    bw.write(str + "\r\n");                    
                }
            }	 
            
            bw.close();
	    in.close();
	} catch (Exception e) {
            e.printStackTrace();		
	}
        return outputPath;
    }    
}
