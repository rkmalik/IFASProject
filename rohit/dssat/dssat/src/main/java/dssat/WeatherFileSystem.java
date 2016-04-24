/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dssat;

import static dssat.DSSATMain.datadir;
import static dssat.DSSATMain.dirseprator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JOptionPane;
import jdk.nashorn.internal.objects.NativeString;
import org.agmip.acmo.util.AcmoUtil;
import org.agmip.functions.DataCombinationHelper;
import org.agmip.translators.dssat.DssatControllerOutput;
import org.agmip.translators.dssat.DssatXFileInput;
import org.agmip.translators.dssat.DssatXFileOutput;
import org.agmip.util.JSONAdapter;
import static org.agmip.util.MapUtil.getObjectOr;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author rkmalik
 */
public class WeatherFileSystem {

    private static WeatherFileSystem instance = null;
    private HashMap<String, String> incache;
    private HashMap<String, String> fertilizerMap;
    private File jSon;

    private WeatherFileSystem() {
        incache = new HashMap<String, String>();
        fertilizerMap = null;
    }

    public void initFertilizerMap (HashMap<String, String> fert) {
        this.fertilizerMap = fert;
    }
    // This mehto// Update table will update the data for the required keys 
    // UpdateHashTable ()
    public static WeatherFileSystem getInstance() {
        if (instance == null) {

            instance = new WeatherFileSystem();
        }
        return ((WeatherFileSystem) instance);
    }

    // This mehtod take a comma seperated key value pair, parse those keyvalue pairs and then updaate the incache
    public void UpdateCache(String keyvalue) {
        String[] tokens = keyvalue.split(",");
        for (int i = 0; i < tokens.length; i++) {
            String key = tokens[i];
            i++;
            String value = tokens[i];

            System.out.printf(key + " " + value + " ---> ");
            incache.put(key, value);
        }

    }

    public void writeAttribute(String key, String value) {
        incache.put(key, value);
    }

    public HashMap<String, String> ReadFromFile(String attributName) {

        return null;
    }

    public String readAttribute(String key) {
        String ret =  incache.get(key);
        return ret;
    }

    // This method is called on the Finish Button to create the weather file. 
    public String getFileName() {
        String wthFileName;
        try {
            String orgName = incache.get("Site");
            String siteIndex = incache.get("SiteIndex");
            String year = incache.get("PlantingYear");

            Integer plantingyear = Integer.parseInt(year);
            Integer twodigyr = plantingyear % 100;
            wthFileName = orgName + twodigyr + "01" + ".WTH";
            System.out.println(wthFileName);

            wthFileName = datadir + dirseprator + wthFileName;
        } catch (Exception e) {
            wthFileName = "WeatherData.WTH";
        }

        return wthFileName;
    }

    public String WriteToFile(String wthFileName) {

        // prepare the file name from the organization name and the site index number and then write to that file
        String orgName = incache.get("Farm");
        String siteIndex = incache.get("SiteIndex");
        String finalOutputPath = readAttribute("DssatFolder");
        finalOutputPath = finalOutputPath + "\\" + readAttribute("Crop");
        
        File file = new File(finalOutputPath + dirseprator + wthFileName);
        
        String writeBuffer = null;

        PrintWriter pr = null;
        try {
            file.createNewFile();
            pr = new PrintWriter(file);
            writeBuffer = new String("*WEATHER : \n");
            pr.println(writeBuffer);
            writeBuffer = new String("@ INSI      LAT     LONG  ELEV   TAV   AMP REFHT WNDHT");
            pr.println(writeBuffer);

            double latitude = -34.23;
            double longitude = -34.23;
            double elevation = -99;
            double tavg = -99;
            double tamp = -99;
            double tmht = -99;
            double wmht = -99;

            writeBuffer = new String();
            //Empty 2 Char, Org 2 Char, Site 2Char, Space 1 Char, latitude 8Chars with 3 dec
            writeBuffer = String.format("%.2s%.2s%.2s%.1s%8.3f%.1s%8.3f%.1s%5.0f%.1s%5.1f%.1s%5.1f%.1s%5.1f%.1s%5.1f", "  ", orgName, siteIndex, " ", latitude, " ", longitude, " ", elevation, " ", tavg, " ", tamp, " ", tmht, " ", wmht);
            pr.println(writeBuffer);

            int julianday = 56001;
            double solarrad = -99;
            double tmax = -99;
            double tmin = -99;
            double rain = -99;
            double dewp = -99;
            double wind = -99;
            double par = -99;

            writeBuffer = new String("@DATE  SRAD  TMAX  TMIN  RAIN  RHUM  WIND  TDEW  PAR");
            pr.println(writeBuffer);
            writeBuffer = new String();

            URL url = null;
            InputStream is = null;
            Integer plantingyear;
            if (incache.get("PlantingYear") != null) {
                plantingyear = Integer.parseInt(incache.get("PlantingYear"));
            } else {
                Date d = new Date();
                plantingyear = d.getYear() + 1900;
            }
            /*Bring the Weather details for last 10 Years and write the details to the WTH File. */
            try {

                String query = new String(
                        "SELECT%20*%20FROM%20FAWN_historic_daily_20140212%20"
                        + "WHERE%20(%20yyyy%20BETWEEN%20" + (plantingyear - 10) + "%20AND%20" + plantingyear.toString() + ")"
                        + "%20AND%20(%20LocId%20=%20" + incache.get("StationLocationId") + ")"
                        + "%20ORDER%20BY%20yyyy%20DESC%20");

                String urlStr = "http://abe.ufl.edu/bmpmodel/xmlread/rohit.php?sql=" + query;

                System.out.println("********************************");
                System.out.println(urlStr);

                //System.out.println(urlStr);
                URL uflconnection = new URL(urlStr);
                HttpURLConnection huc = (HttpURLConnection) uflconnection.openConnection();
                HttpURLConnection.setFollowRedirects(false);
                huc.setConnectTimeout(15 * 1000);
                huc.setRequestMethod("GET");
                huc.connect();
                InputStream input = huc.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(input));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    if (!(inputLine.startsWith("S") || inputLine.startsWith("s"))) {
                        String[] fields = inputLine.split(",");

                        int M = Integer.parseInt(fields[3]);
                        int D = Integer.parseInt(fields[4]);
                        int a = (14 - M) / 12;
                        int y = Integer.parseInt(fields[2]) + 4800 - a;
                        int m = M + 12 * a - 3;

                        long JD = D + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045;

                        String tempDate = String.format("%05d", Integer.parseInt(fields[2].substring(2)) * 1000 + Integer.parseInt(fields[5]));
                        String SRad;
                        if (fields[13].length() > 1) {
                            SRad = String.format("%.1f", Double.parseDouble(fields[13]));
                        } else {
                            SRad = "";
                        }
                        String TMax = String.format("%.1f", Double.parseDouble(fields[9]));
                        String TMin = String.format("%.1f", Double.parseDouble(fields[8]));
                        String RAIN = String.format("%.1f", Double.parseDouble(fields[12]));
                        String RHUM = String.format("%.1f", Double.parseDouble(fields[11]));
                        String WIND = String.format("%.1f", Double.parseDouble(fields[14]));
                        String TDEW = String.format("%.1f", Double.parseDouble(fields[10]));
                        String PAR = "";

                        String line = String.format("%5s %5s %5s %5s %5s %5s %5s %5s", tempDate, SRad, TMax, TMin, RAIN, RHUM, WIND, TDEW);
                        pr.println(line);

                    }
                }
                in.close();
                //System.out.println("Created teh connection successfully");

            } catch (MalformedURLException me) {
                me.printStackTrace();
                //return 21.4;
            } catch (Exception e) {
                e.printStackTrace();
            }
         } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (pr != null) {
                pr.close();
            }
        }

        // Here I need to read all the data from the controls and write the data to the files. 
        for (int i = 0; i < incache.size(); i++) {

        }
        return file.getAbsolutePath();
    }

    public void copyWeatherFile(String localFile, String dssatPath) throws IOException {
        
        String finalOutputPath = readAttribute("DssatFolder");
        finalOutputPath = finalOutputPath + "\\" + readAttribute("Crop");
        
        File fout = new File(dssatPath + dirseprator + localFile);
        
        File fin = new File(finalOutputPath + dirseprator + localFile);

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fin));
        } catch (IOException e){
            String msg = e.getMessage();
            JOptionPane.showMessageDialog(null, msg);
            e.printStackTrace();
        }
        
        BufferedWriter bw = null;
        
        try {
            bw = new BufferedWriter(new FileWriter(fout));
        } catch (IOException e){
            String msg = e.getMessage();
            JOptionPane.showMessageDialog(null, msg);
            e.printStackTrace();
        }
        
        String line = null;
        while ((line = br.readLine()) != null) {
            bw.write(line + "\n");
        }
        br.close();
        bw.close();

    }

    /*public void saveFile() throws IOException {
        File f = new File("Sample.txt");
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        for (String key : incache.keySet()) {
            String value = incache.get(key);
            bw.append(key + " : " + value + "\n");
        }
        bw.close();
    }*/

    public void createJson() throws IOException {
        String[] date = readAttribute("Planting Date").split("/");
        int year = Integer.parseInt(date[2]);
        JSONObject object = new JSONObject();
        JSONArray experiments = new JSONArray();
        for (int j = 0; j <= 0; j++) {
            JSONObject experiment = new JSONObject();
            Location countypos = new Location();
            CSVFileHandler mycsvfile = new CSVFileHandler();
            mycsvfile.getCountyLocation_GlobalDB(readAttribute("Location"), countypos);
            experiment.put("data_source", "DSSAT");
            experiment.put("crop_model_version","v4.6");
            
            // Enable the Site Name
            if (incache.containsKey("Farm"))
                experiment.put("site_name", incache.get("Farm"));
            experiment.put("in", "UF");
            experiment.put("person_notes", "Kelly Morgan, L.C. Jones, J.W.(Should be changed to Farmers Name)");
            experiment.put("institution", "UNIVERSITY OF FLORIDA, GAINESVILLE, FL, USA");
            
            
            experiment.put("wst_id", readAttribute("Station Code").substring(0,3));
            experiment.put("fl_lat", "" + countypos.latitude);
            experiment.put("fl_long", "" + countypos.longitude);
            experiment.put("sc_year", "" + (year + j));
            experiment.put("id_field", readAttribute("Station Code"));
            experiment.put("ireff", String.valueOf(Double.valueOf(readAttribute("ireff"))/100));
            
            // Add bed width and bed height
            experiment.put("bed_h", readAttribute("Bed Height"));
            experiment.put("bed_w", readAttribute("Bed Width"));
            experiment.put("pmalb", readAttribute ("PlasticMulch Color"));

            experiment.put("exname", readAttribute("Station Code") + "_" + (year + j));
            String soilId = mycsvfile.getSoilid(readAttribute("Location"), readAttribute("Soil"));
            experiment.put("soil_id", soilId);

            JSONObject management = new JSONObject();
            JSONArray events = new JSONArray();

            JSONObject event1 = new JSONObject();
            event1.put("event", "planting");
            event1.put("dssat_cul_id", readAttribute("dssat_cul_id"));
            event1.put("crid", readAttribute("crid"));
            event1.put("plma", readAttribute("plma"));
            event1.put("plpop", readAttribute("plpop"));
            event1.put("plrs", readAttribute("plrs").trim());
            event1.put("date", (year + j) + "" + date[0] + date[1]);
            event1.put("pldp", readAttribute("pldp"));
            events.put(event1);

            // Add fertilier events
            int count = Integer.parseInt(incache.get("fert_row_count"));
            
            for (int i = 0; i < count; i++) {
                JSONObject event = new JSONObject();
                event.put("event", "fertilizer");
                event.put("feamn", readAttribute("feamn" + i));
                event.put("fecd", readAttribute("fecd" + i));
                String fertKey = readAttribute("feacd" + i);
                
                if (fertilizerMap.containsKey(fertKey)) {
                    String fertCode = fertilizerMap.get(fertKey);
                     event.put("feacd", fertCode);
                } else {
                    event.put("feacd", readAttribute("feacd" + i));
                }
                
                String date_i = (year + j) + readAttribute("ftdate" + i).substring(4);
                event.put("date", date_i);
                events.put(event);
            }
            
            // Add irrigation events
            count = Integer.parseInt(incache.get("irr_row_count"));
            for (int i = 0; i < count; i++) {
                JSONObject event = new JSONObject();
                event.put("event", "irrigation");
                String eff = readAttribute("ireff");
                event.put("ireff", String.valueOf(Double.valueOf(readAttribute("ireff"))/100));
                event.put("irop", readAttribute("irop" + i));
                event.put("irval", readAttribute("irval" + i));
                event.put("irstr", readAttribute("irstr" + i));
                event.put("irdur", readAttribute("irdur" + i));
                event.put("irint", readAttribute("irint" + i));
                event.put("irnum", readAttribute("irnum" + i));
                event.put("irspc", readAttribute("irspc"));
                event.put("irdep", readAttribute("irdep"));
                event.put("irofs", readAttribute("irofs"));
                String date_i = (year + j) + readAttribute("irdate" + i).substring(4);
                event.put("date", date_i);
                events.put(event);
            }

            management.put("events", events);
            experiment.put("management", management);
            experiments.put(experiment);
        }
        object.put("experiments", experiments);

        jSon = new File(datadir + dirseprator + readAttribute("Station Code") + ".json");
        FileWriter fw = new FileWriter(jSon);
        
        String msg = "Created following JSON File!\n" + jSon;
        JOptionPane.showMessageDialog(null, msg);
        
        fw.write(object.toString());
        fw.close();
    }

    public void createXFileOutput() throws IOException {
        
        System.out.println("Calling DSSATControllerOutput Class to Create X File.");
        DssatControllerOutput translator = new DssatControllerOutput();
        ArrayList<String> inputPaths = new ArrayList<>();
        inputPaths.add(jSon.getPath());
        String defaultOutputPath = "";
        HashMap data = DataCombinationHelper.combine(inputPaths);
        DataCombinationHelper.fixData(data);
        
        String finalOutputPath = readAttribute("DssatFolder");
        finalOutputPath = finalOutputPath + "\\" + readAttribute("Crop");
        
        System.out.println ("Output Path : " + defaultOutputPath);
        System.out.println ("Output Path : " + finalOutputPath);
        
        translator.writeFile(finalOutputPath, data);
        AcmoUtil.writeAcmo(defaultOutputPath, data, "dssat", new HashMap());
    }

}
