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
public class SummaryReport {
    private int curMonth;
    private ArrayList<Double> totalRainFall;
    private ArrayList<Double> totalEvaporation;
    
    private ArrayList<Double> peakMonthlyEvaporation;
    private ArrayList<Double> totalIrrigationRequired;
    
    private ArrayList<Double> averageIrrigationRequired;
    private ArrayList<Double> twoin10IrrigationRequired;
    private ArrayList<Double> onein10IrrigationRequired;
    
    private ArrayList<Double> weightedAverageIrrRequired;
    private ArrayList<Double> weighted2In10IrrRequired;
    private ArrayList<Double> weighted1In10IrrRequired;

    

    public SummaryReport() {
        curMonth = 1;

        this.totalRainFall = new ArrayList<Double>();
        this.totalEvaporation = new ArrayList<Double> ();

        this.peakMonthlyEvaporation = new ArrayList<Double> ();

        this.totalIrrigationRequired = new ArrayList<Double> ();
        this.averageIrrigationRequired = new ArrayList<Double> ();
        this.twoin10IrrigationRequired = new ArrayList<Double> ();
        this.onein10IrrigationRequired = new ArrayList<Double> ();
        
        this.weightedAverageIrrRequired = new ArrayList<Double> ();
        this.weighted2In10IrrRequired = new ArrayList<Double> ();
        this.weighted1In10IrrRequired = new ArrayList<Double> ();        
        
        
        for (int i = 0; i < 12; i++) {

            this.totalRainFall.add(0.0);
            this.totalEvaporation.add(0.0);

            this.peakMonthlyEvaporation.add(0.0);
            
            
            this.totalIrrigationRequired.add(0.0);
            this.averageIrrigationRequired.add(0.0);
            this.twoin10IrrigationRequired.add(0.0);
            this.onein10IrrigationRequired.add(0.0);
            
            this.weightedAverageIrrRequired.add(0.0);
            this.weighted2In10IrrRequired.add(0.0);
            this.weighted1In10IrrRequired.add(0.0);
        }
        
    }
    
    public void reset() {
        for (int i = 0; i < 12; i++) {

            this.totalRainFall.add(0.0);
            this.totalEvaporation.add(0.0);

            this.peakMonthlyEvaporation.add(0.0);
            
            
            this.totalIrrigationRequired.add(0.0);
            this.averageIrrigationRequired.add(0.0);
            this.twoin10IrrigationRequired.add(0.0);
            this.onein10IrrigationRequired.add(0.0);
            
            this.weightedAverageIrrRequired.add(0.0);
            this.weighted2In10IrrRequired.add(0.0);
            this.weighted1In10IrrRequired.add(0.0);
        }
    }

    public int getCurMonth() {
        return curMonth;
    }

    public void setCurMonth(int curMonth) {
        this.curMonth = curMonth;
    }

    public Double getTotalRainFallByMonth(int month) {
        if (month <1|| month>12) {
            throw new IllegalArgumentException ("Month "+ month + " is not in the valid range. It should be 1-12.");
        }
        return totalRainFall.get(month-1);
    }

    public void setTotalRainFall(int month, double rainFall) throws IllegalArgumentException {
        
        if (month <1|| month>12) {
            throw new IllegalArgumentException ("Month "+ month + " is not in the valid range. It should be 1-12.");
        }
        this.totalRainFall.set(month-1, rainFall);
    }

    public Double getTotalEvaporationByMonth(int month) {
        if (month <1|| month>12) {
            throw new IllegalArgumentException ("Month "+ month + " is not in the valid range. It should be 1-12.");
        }
        return this.totalEvaporation.get(month-1);
    }

    public void setTotalEvaporation(int month, double evaporation) throws IllegalArgumentException {
        if (month <1|| month>12) {
            throw new IllegalArgumentException ("Month "+ month + " is not in the valid range. It should be 1-12.");
        }
        this.totalEvaporation.set(month-1, evaporation);
    }

    public Double getPeakEvaporationByMonth(int month){
        return this.peakMonthlyEvaporation.get(month-1);
    }

    public void setPeakMonthlyEvaporation(int month, double evaporation) {
        if (month <1|| month>12) {
            throw new IllegalArgumentException ("Month "+ month + " is not in the valid range. It should be 1-12.");
        }
        this.peakMonthlyEvaporation.set(month-1, Math.max(this.peakMonthlyEvaporation.get(month-1), evaporation));
    }

    
    public Double getTotalIrrigationRequiredByMonth(int month) {
        if (month <1|| month>12) {
            throw new IllegalArgumentException ("Month "+ month + " is not in the valid range. It should be 1-12.");
        }
        return this.totalIrrigationRequired.get(month-1);
    }

    public void addTotalIrrigationRequiredByMonth(int month, double irrigationRequired) {
        if (month <1|| month>12) {
            throw new IllegalArgumentException ("Month "+ month + " is not in the valid range. It should be 1-12.");
        }
        this.totalIrrigationRequired.set(month-1, this.totalIrrigationRequired.get(month-1)+irrigationRequired);
    }

    public Double getAverageIrrigationRequired(int month) {
        if (month <1|| month>12) {
            throw new IllegalArgumentException ("Month "+ month + " is not in the valid range. It should be 1-12.");
        }
        return this.averageIrrigationRequired.get(month-1);
    }

    public void setAverageIrrigationRequired(int month, double irrigationRequired) {
        if (month <1|| month>12) {
            throw new IllegalArgumentException ("Month "+ month + " is not in the valid range. It should be 1-12.");
        }
        this.averageIrrigationRequired.set(month-1, irrigationRequired);
    }

    public Double getTwoin10IrrigationRequired(int month) {
        if (month <1|| month>12) {
            throw new IllegalArgumentException ("Month "+ month + " is not in the valid range. It should be 1-12.");
        }
        return this.twoin10IrrigationRequired.get(month-1);
    }

    public void setTwoin10IrrigationRequired(int month, Double irrigationRequired) {
        if (month <1|| month>12) {
            throw new IllegalArgumentException ("Month "+ month + " is not in the valid range. It should be 1-12.");
        }
        this.twoin10IrrigationRequired.set(month-1, irrigationRequired);
    }

    public Double getOnein10IrrigationRequired(int month) {
        if (month <1|| month>12) {
            throw new IllegalArgumentException ("Month "+ month + " is not in the valid range. It should be 1-12.");
        }
        return this.onein10IrrigationRequired.get(month-1);
    }

    public void setOnein10IrrigationRequired(int month, Double irrigationRequired) {
        
        if (month <1|| month>12) {
            throw new IllegalArgumentException ("Month "+ month + " is not in the valid range. It should be 1-12.");
        }
        this.onein10IrrigationRequired.set(month-1, irrigationRequired);
    }
     
    public void setWeightedAvgIrrRequired (int month, double val) {
        this.weightedAverageIrrRequired.set(month-1, weightedAverageIrrRequired.get(month-1) + val);
    }
    public void setWeighted2In10IrrRequired (int month, double val) {
        this.weighted2In10IrrRequired.set(month-1, weighted2In10IrrRequired.get(month-1) + val);
    }
    public void setWeighted1In10IrrRequired (int month, double val) {
        this.weighted1In10IrrRequired.set(month-1, weighted1In10IrrRequired.get(month-1) + val);
    }
    
    public double getWeightedAvgIrrRequired (int month) {
        return this.weightedAverageIrrRequired.get(month-1);
    }
    public double getWeighted2In10IrrRequired (int month) {
        return this.weighted2In10IrrRequired.get(month-1);
    }
    public double getWeighted1In10IrrRequired (int month) {
        return this.weighted1In10IrrRequired.get(month-1);
    }
    
        
    
}
