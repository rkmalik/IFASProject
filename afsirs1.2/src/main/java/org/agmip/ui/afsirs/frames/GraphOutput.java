/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.agmip.ui.afsirs.frames;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import javax.swing.JFrame;
import org.agmip.ui.afsirs.util.AFSIRSUtils;
import org.agmip.ui.afsirs.util.FrameTracker;
import org.agmip.ui.afsirs.util.SoilSpecificPeriodData;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Rohit Kumar Malik
 */
public class GraphOutput extends javax.swing.JFrame {

    /**
     * Creates new form GraphOutput
     */
    AFSIRSUtils utils;

    public GraphOutput() {
        initComponents();
        setLocation(400, 50);
        utils = AFSIRSUtils.getInstance();
        //setOutput();
        init();
        setVisible(true);
    }
    
    public void init(){
        addGraph();
        addEvaporationAndTranspiration();
        addRain();
        //addWeather();
    }
    

    private CategoryDataset createDataset(int type) {

        ArrayList<SoilSpecificPeriodData> PDATA = utils.getGraphData(type);
        //double[] PDATA = utils.getGraphData(type);

        double avg = 0.0;
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        double[] arr = utils.getFractions();
        double [] soilArea = utils.getSoilArea();
        double areaSum=0.0;
        
        for (double a: soilArea)
            areaSum+=a;
        
        for (int i = 0; i < PDATA.get(0).getSoilDataPoints().length; i++) {
            for (int j = 0; j < PDATA.size(); j++) {
                dataset.addValue(PDATA.get(j).getSoilDataPoints()[i], PDATA.get(j).getSoilName(), "" + (i + 1));
                avg+=PDATA.get(j).getSoilDataPoints()[i]*soilArea[j];
            }
            avg /= areaSum;
            dataset.addValue(avg, "Weighted Avg", ""+(i+1));
            avg=0.0;
        }

        return dataset;
    }

    public void addGraph() {

        JFreeChart chart = ChartFactory.createBarChart("IRRIGATION REQUIREMENTS(INCHES)", "MONTH", "IRR", createDataset(0), PlotOrientation.VERTICAL, true, true, false);
        chart.setBackgroundPaint(Color.white);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(600, 270));
        jTabbedPane1.addTab("Month-Graph", chartPanel);
        
        if (utils.getICODE() >= 0) {

            JFreeChart chart1 = ChartFactory.createBarChart("IRRIGATION REQUIREMENTS(INCHES)", "BI-WEEK", "IRR", createDataset(1), PlotOrientation.VERTICAL, true, true, false);
            JFreeChart chart2 = ChartFactory.createBarChart("IRRIGATION REQUIREMENTS(INCHES)", "WEEK", "IRR", createDataset(2), PlotOrientation.VERTICAL, true, true, false);
            chart1.setBackgroundPaint(Color.white);
            chart2.setBackgroundPaint(Color.white);

            plot = chart1.getCategoryPlot();
            plot.setBackgroundPaint(Color.lightGray);
            plot.setDomainGridlinePaint(Color.white);
            plot.setRangeGridlinePaint(Color.white);

            plot = chart2.getCategoryPlot();
            plot.setBackgroundPaint(Color.lightGray);
            plot.setDomainGridlinePaint(Color.white);
            plot.setRangeGridlinePaint(Color.white);

            ChartPanel chartPanel1 = new ChartPanel(chart1);
            chartPanel1.setPreferredSize(new java.awt.Dimension(600, 270));
            jTabbedPane1.addTab("Bi-Week Graph", chartPanel1);

            ChartPanel chartPanel2 = new ChartPanel(chart2);
            chartPanel2.setPreferredSize(new java.awt.Dimension(600, 270));
            jTabbedPane1.addTab("Weekly Graph", chartPanel2);
        }
    }
    
    public void addRain () {
        
        XYSeries rainSeries = new XYSeries("RAIN");
        double[][] RAIN = utils.getRain();
        for (int i = 0; i < 10/*RAIN.length*/; i++) {
            for (int j = 0; j < 10/*RAIN[0].length*/; j++) {
                rainSeries.add(i * RAIN[0].length + j, RAIN[i][j]);
            }
        }
        
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(rainSeries);
        
        JFreeChart chart = ChartFactory.createXYLineChart("Rain", "Days", "Inches", dataset/*,
                            PlotOrientation.VERTICAL, true, true, false*/);
        chart.setBackgroundPaint(Color.white);
        
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.GRAY);
        plot.setDomainGridlinePaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.white);
        
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(1, false);
        plot.setRenderer(renderer);
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
        ChartPanel panel = new ChartPanel(chart);
        jTabbedPane1.addTab("Rain", panel);
        
        
    }
    
    public void addEvaporationAndTranspiration(){
        
         XYSeries etSeries = new XYSeries("ET");
        double[][] ET = utils.getET();
        for (int i = 0; i < 10/*ET.length*/; i++) {
            for (int j = 0; j < 10/*ET[0].length*/; j++) {
                etSeries.add(i * ET[0].length + j, ET[i][j]);
            }
        }
        
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(etSeries);
        
        JFreeChart chart = ChartFactory.createXYLineChart("Evaporation Transpiration", "Days", "Inches", dataset/*,
                            PlotOrientation.VERTICAL, true, true, false*/);
        chart.setBackgroundPaint(Color.white);
        
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.GRAY);
        plot.setDomainGridlinePaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.white);
        
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(1, false);
        plot.setRenderer(renderer);
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
        ChartPanel panel = new ChartPanel(chart);
        jTabbedPane1.addTab("Evaporation Transpiration", panel);
        
    }
    
    public void addWeather(){
        int nyr = utils.getNYR();
        XYSeries etSeries = new XYSeries("ET");
        double[][] ET = utils.getET();
        for (int i = 0; i < 10/*ET.length*/; i++) {
            for (int j = 0; j < 10/*ET[0].length*/; j++) {
                etSeries.add(i * ET[0].length + j, ET[i][j]);
            }
        }
        
        XYSeries rainSeries = new XYSeries("RAIN");
        double[][] RAIN = utils.getRain();
        for (int i = 0; i < 10/*RAIN.length*/; i++) {
            for (int j = 0; j < 10/*RAIN[0].length*/; j++) {
                rainSeries.add(i * RAIN[0].length + j, RAIN[i][j]);
            }
        }
        
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(etSeries);
        dataset.addSeries(rainSeries);
        
        JFreeChart chart = ChartFactory.createXYLineChart("Weather Data", "Days", "Inches", dataset/*,
                            PlotOrientation.VERTICAL, true, true, false*/);
        chart.setBackgroundPaint(Color.white);
        
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.GRAY);
        plot.setDomainGridlinePaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.white);
        
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(1, false);
        plot.setRenderer(renderer);
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
        ChartPanel panel = new ChartPanel(chart);
        jTabbedPane1.addTab("Weather", panel);
        
    }

//    public void setOutput() {
//        try {
//            BufferedReader br = new BufferedReader(new FileReader(utils.getOutFile()));
//            String line;
//            while ((line = br.readLine()) != null) {
//                outputText.append(line + "\n");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    public void addSoilInfo() {
//        SoilData soil = utils.getSoilData();
//        String[] TXT = soil.getTXT();
//        double[] WC = soil.getWC();
//        double[] DU = soil.getDU();
//        String str = "			\n"
//                + "	SOIL SERIES NAME : " + soil.getName() + "\n"
//                + "\n"
//                + "                                                  WC     DU     Texture\n";
//
//        for (int i = 0; i < soil.getNL(); i++) {
//            WC[i] = ((int)(WC[i]*100))/100.0;
//            str += "                                              " + (i + 1) + "  " + WC[i] + "        " + DU[i] + "     " + TXT[i] + "\n";
//        }
//
//        soilText.setText(str);
//    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jButton1 = new javax.swing.JButton();
        outputButton = new javax.swing.JButton();
        rerunsimulation = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("RESULT");

        jTabbedPane1.setPreferredSize(new java.awt.Dimension(200, 800));

        jButton1.setText("New Project");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        outputButton.setText("Output");
        outputButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputButtonActionPerformed(evt);
            }
        });

        rerunsimulation.setText("Re-Run Simulation");
        rerunsimulation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rerunsimulationActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1332, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(outputButton)
                        .addGap(18, 18, 18)
                        .addComponent(rerunsimulation)
                        .addGap(30, 30, 30)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(outputButton)
                    .addComponent(rerunsimulation))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 537, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        AFSIRSUtils.resetData();
        FrameTracker.resetFrames();
        JFrame startFrame = new SiteInfoFrame();
        FrameTracker.siteInfoFrame = startFrame;
        this.setVisible(false);
        
        startFrame.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void outputButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputButtonActionPerformed
        // TODO add your handling code here:
        File file = new File(utils.getOutFile());
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            System.out.println("Error Opening the outpt file !!");
        }
    }//GEN-LAST:event_outputButtonActionPerformed

    private void rerunsimulationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rerunsimulationActionPerformed
        AFSIRSUtils.resetData();
        this.setVisible(false);
        FrameTracker.soilData=null;
        FrameTracker.soilDataNext=null;
        ((SiteInfoFrame)(FrameTracker.siteInfoFrame)).initializeUtilities();
        FrameTracker.siteInfoFrame.setVisible(true);
    }//GEN-LAST:event_rerunsimulationActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton outputButton;
    private javax.swing.JButton rerunsimulation;
    // End of variables declaration//GEN-END:variables
}
