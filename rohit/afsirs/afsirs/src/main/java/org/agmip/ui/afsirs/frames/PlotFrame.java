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
import java.util.InputMismatchException;
import javax.swing.JFrame;
import org.agmip.ui.afsirs.util.AFSIRSUtils;
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
public class PlotFrame extends javax.swing.JFrame {

    /**
     * Creates new form PlotFrame
     */
    AFSIRSUtils utils;

    public PlotFrame() {
        initComponents();
        setLocation(400, 50);
        utils = AFSIRSUtils.getInstance();
        //setOutput();
        init();
        setVisible(true);
    }
    
    public void init(){
        addGraph();
        addWeather();
    }
    

    private CategoryDataset createDataset(int type) {

        double[] PDATA = utils.getGraphData(type);

        double avg = 0.0;
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < PDATA.length; i++) {
            dataset.addValue(PDATA[i], "", "" + (i + 1));
            if(type == 0)
                avg+=PDATA[i];
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
    
    public void addWeather(){
        int nyr = utils.getNYR();
        XYSeries etSeries = new XYSeries("ET");
        double[][] ET = utils.getET();
        for(int i = 0;i<10/*ET.length*/;i++){
            for(int j = 0;j<10/*ET[0].length*/;j++){
                etSeries.add(i*ET[0].length + j, ET[i][j]);
            }
        }
        
        XYSeries rainSeries = new XYSeries("RAIN");
        double[][] RAIN = utils.getRain();
        for(int i = 0;i<10/*RAIN.length*/;i++){
            for(int j = 0;j<10/*RAIN[0].length*/;j++){
                etSeries.add(i*RAIN[0].length + j, RAIN[i][j]);
            }
        }
        
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(etSeries);
        dataset.addSeries(rainSeries);
        
        JFreeChart chart = ChartFactory.createXYLineChart("Weather Data", "Days", "Inches", dataset,
                            PlotOrientation.VERTICAL, true, true, false);
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
        jPanel1 = new javax.swing.JPanel();
        todayDate = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        todayDate1 = new com.toedter.calendar.JDateChooser();
        jButton1 = new javax.swing.JButton();
        outputButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("RESULT");

        jTabbedPane1.setPreferredSize(new java.awt.Dimension(200, 800));

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        todayDate.setDateFormatString("MMM, dd, yyyy");

        jLabel1.setText("Starting Date");

        jLabel2.setText("End Date");

        todayDate1.setDateFormatString("MMM, dd, yyyy");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(todayDate, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(todayDate1, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(todayDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(todayDate1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1150, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(outputButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(outputButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 537, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        AFSIRSUtils.resetData();
        JFrame startFrame = new SiteInfoFrame();
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton outputButton;
    private com.toedter.calendar.JDateChooser todayDate;
    private com.toedter.calendar.JDateChooser todayDate1;
    // End of variables declaration//GEN-END:variables
}
