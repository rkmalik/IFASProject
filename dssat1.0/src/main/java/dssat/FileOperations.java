/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dssat;

//import static dssat.DSSATMain.weather_historic_daily;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author rkmalik
 * This class is a Singleton Class as Only one class needs to update all the data for all the frames. 
 * This May need to connect the DB Multiple time. So I may Need to create the CB class also a singleton class. 
 */
public class FileOperations {
    
    private static FileOperations instance = null;
    private static String fileName;  
    private static String fileLocation;
    private static String fileType;        // Specifies the file type, this can be a .PTX, .WTH, .CUL etc. 
    private static HashMap<String, String> cache;
    
    // This will hold the Hash Table which will be holding all the data from all the screens. 
    
    // Update table will update the data for the required keys 
    // UpdateHashTable ()
    public static FileOperations getInstance ()
    {
        if (instance == null) {
            
            instance = new FileOperations();
        }
        return instance; 
    }
    
    
    protected FileOperations () {        
        fileName = null; 
        fileLocation = null;
        fileType = null;
        cache = new HashMap <String, String>();
    }
    
    // This method is called on the next and finish button to update all the data 
    // from the screen and before writing to the file. 
    // This goig to be an overridden method which will take different parameters to update the cache 
    public void UpdateCache (String key, String value) {        
        cache.put(key, value);        
    }
    
    // This mehtod take a comma seperated key value pair, parse those keyvalue pairs and then updaate the cache
    public void UpdateCache (String keyvalue) {  
        String [] tokens  = keyvalue.split(",");        
        for (int i = 0; i < tokens.length; i++)
        {
            String key = tokens[i];
            i++;
            String value = tokens[i];
            
            
            System.out.printf(key + " " + value + " ---> ");
            cache.put(key, value);           
        }
               
    }
    public void UpdateCache (ArrayList <String> keyValueList) {        
        
        // Iterate through the list and update the cache 
        
    }
    
    
    // This method is called on the Finish Button to create the weather file. 
    public void WriteToWeatherFile () {
        
        // prepare the file name from the organization name and the site index number and then write to that file
        
        String orgName= cache.get("Organization");
        String siteIndex =  cache.get("SiteIndex");
        String year = cache.get("PlantingYear");
        
        Integer plantingyear = Integer.parseInt(year);
        Integer twodigyr = plantingyear % 100;
        String wthFileName = orgName + siteIndex + twodigyr + "01" +  ".WTH";
        System.out.println(wthFileName);
        File file = new File (wthFileName);
        String writeBuffer = null;
        
        PrintWriter pr = null;
        try {
             pr = new PrintWriter(file);   
             writeBuffer = new String ("*WEATHER : \n");
             pr.println(writeBuffer);
             writeBuffer = new String ("@ INSI      LAT     LONG  ELEV   TAV   AMP REFHT WNDHT");
             pr.println(writeBuffer);  
             
             double latitude = -34.23; double longitude = -34.23;
             double elevation = -99; double tavg = -99; double tamp = -99; 
             double tmht = -99; double wmht = -99;
             
             writeBuffer =  new String ();
                                        //Empty 2 Char, Org 2 Char, Site 2Char, Space 1 Char, latitude 8Chars with 3 dec
             writeBuffer = String.format("%.2s%.2s%.2s%.1s%8.3f%.1s%8.3f%.1s%5.0f%.1s%5.1f%.1s%5.1f%.1s%5.1f%.1s%5.1f", "  ", orgName, siteIndex, " ", latitude, " ", longitude, " ", elevation, " ", tavg, " ", tamp, " ", tmht, " ", wmht);
             pr.println(writeBuffer);
             
             
             int julianday = 56001; double solarrad = -99;  
             double tmax = -99; double tmin = -99; double rain = -99; 
             double dewp = -99; double wind = -99; double par = -99;
             
             
             writeBuffer = new String ("@DATE  SRAD  TMAX  TMIN  RAIN  DEWP  WIND  PAR");
             pr.println(writeBuffer);
             writeBuffer = new String ();

             /*DBConnect weather_historic_daily = new DBConnect (ServerDetails.SERVER_NUM_RONLY, ServerDetails.weather_historic_daily_dbname);
             StringBuilder query = new StringBuilder (
                                    "SELECT * FROM FAWN_historic_daily_20140212 " +
                                    "WHERE ( yyyy BETWEEN " + (plantingyear-10) + " AND "  + plantingyear.toString() + ")" +
                                    " AND ( LocId = " + cache.get("StationLocationId") + ")" +
                                    " ORDER BY yyyy DESC ");

            System.out.println (query);
             ResultSet result = weather_historic_daily.Execute (ServerDetails.weather_historic_daily_dbname, query.toString());
             
             try {
                 while (result.next()) {      
                    String julian = result.getString("yyyy");
                    julian = julian.substring(2);                   

                    int yr = Integer.parseInt(julian);
                    int day = Integer.parseInt(result.getString("doy"));
                    julian = String.format("%02d%03d", yr, day);
                    

                    if (result.getString("Tmin") != null)
                        tmin = Double.parseDouble(result.getString("Tmin"));
                    else 
                        tmin = -99.0;
                    
                    if (result.getString("Tmax") != null)
                        tmax = Double.parseDouble(result.getString("Tmax"));
                    else 
                        tmax = -99.0;
                    
                    if (result.getString("Tavg") != null)
                        tavg = Double.parseDouble(result.getString("Tavg"));
                    else 
                        tavg = -99.0;

                    
                    //writeBuffer = String.format("%5d%.1s%5.1f%.1s%5.1f%.1s%5.1f%.1s%5.1f%.1s%5.1f%.1s%5.1f%.1s%5.1f",  julianday, " ", solarrad, " ", tmax, " ", tmin, " ", rain, " ", dewp, " ", wind, " ", par);
                    writeBuffer = String.format("%.5s%.1s%5.1f%.1s%5.1f%.1s%5.1f%.1s%5.1f%.1s%5.1f%.1s%5.1f%.1s%5.1f",  julian, " ", solarrad, " ", tmax, " ", tmin, " ", rain, " ", dewp, " ", wind, " ", par);
                    pr.println(writeBuffer);
                }                
                 
             } catch (SQLException e) {                 
                 e.printStackTrace();
             }*/
  
        } catch (IOException e) {        
            e.printStackTrace();
        } finally {
            
            if (pr!= null)
                pr.close ();
        }
       
        
        // Here I need to read all the data from the controls and write the data to the files. 
        for (int i = 0; i < cache.size(); i++) {
            
            
            
        }       
    }
    
    public static void WriteToSiteFile (DSSATMain main)
    {
        
        
        
    }
    
    
}
