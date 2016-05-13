/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.agmip.ui.afsirs.others;

import java.io.*;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author rohit
 */
public class ParseExcelToWeatherFileFormat {
    
    private static PrintWriter writer = null;
    private static POIFSFileSystem fs = null;
    private static String filePath = "C:\\Users\\rohit\\Desktop\\File\\fsdfs\\ASCE-ETo-Orlando_1952-2015.xlsx";
    private static final String EOL = "\r\n";
            
    
    public ParseExcelToWeatherFileFormat () {
        
        try {
            File file  = new File (filePath);
            fs = new POIFSFileSystem(new FileInputStream(file));
            writer = new PrintWriter("CLIM.ORL", "UTF-8");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ParseExcelToWeatherFileFormat.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ParseExcelToWeatherFileFormat.class.getName()).log(Level.SEVERE, null, ex);
        }catch (IOException ex) {
                Logger.getLogger(ParseExcelToWeatherFileFormat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    
    public static void main (String [] args)  {

        try {
            FileInputStream fs = new FileInputStream(filePath);
            Workbook wb = new XSSFWorkbook(fs);
            Sheet sheet = wb.getSheetAt(0);
            writer = new PrintWriter("CLIM.ORL", "UTF-8");

            String str = "ORLANDO     DAILY POTENTIAL ET DATA (INCHES), 1952-2015"+EOL;
            writer.print(str);

            
            Iterator rowIterator = sheet.iterator();
            
            
            // We will skip the leap year Entry
            
            int outputcol = 0;          // This will go from 1 - 14 in a row
            int outputElems = 0;        // This will go from 1-365 and once this is 
            
            int overallRow = 1;         // Total Number of Row processed
            int startYr = 0;            // This will hold the First Year
            int endYr = 0;              // This will hold the last Year
            
            //int yrEntry = 0;        // This should go till 365;
            str = "";
            while (rowIterator.hasNext()) {
                
                

                
                Row row = (Row)rowIterator.next();
                Iterator cellIterator = row.cellIterator();
                
                int month = 0;
                int date = 0;
                int year = 0;
                
                if (overallRow<=4) {
                    overallRow++;
                    continue;
                }
                
                while (cellIterator.hasNext()) {
                    

                    
                    Cell cell = (Cell)cellIterator.next();
                        //The Cell Containing String will is name.
                        if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
                            //student.setName(cell.getStringCellValue());
                            String val = cell.getStringCellValue();
                            writer.print(val + " ");

                            //The Cell Containing numeric value will contain marks
                        } else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {

                            int colIndex = cell.getColumnIndex();
                            
                            //This holds the Month Number
                            if (colIndex== 0) {
                                month = (int)cell.getNumericCellValue();
                            }
                            
                            // This Date of the month // Skip if this is a Leap yeaar
                            else if (colIndex == 1) {
                                date = (int)cell.getNumericCellValue();
                            }
                            
                            // Year
                            else if (colIndex == 2) {
                                year = (int)cell.getNumericCellValue();
                                if (startYr==0) {
                                    startYr = year;
                                    endYr = year;
                                } else {
                                    endYr = year;
                                }
                                
                                if (outputElems==0) {
                                    str += year + EOL;
                                }
                                
                                if (year%4==0 && month ==2 && date == 29) {
                                    break;
                                }
                                //student.setEnglish(String.valueOf(cell.getNumericCellValue()));
                            } else if (colIndex == 3) {
                                
                                Double val = cell.getNumericCellValue();
                                str+= " " + String.format("%.3f", val);
                                outputcol++;
                                outputElems++;
                                
                                
                                // if 365
                                
                                // if 14
                                if (outputcol==14) {
                                    str+=EOL;
                                    outputcol=0;
                                }
                                
                                if (outputElems==365){
                                    outputElems = 0;
                                    outputcol = 0;
                                    str+=EOL;
                                }
                            }
                        }
                }
                overallRow++;
            }
            String line2 = String.format("   %d   YEARS OF RECORD"+EOL, (endYr-startYr+1));
            str = line2+str;
            writer.print(str);
        } catch(Exception ioe) {
            ioe.printStackTrace();
        } finally {
            writer.close();
        }
    }
}



