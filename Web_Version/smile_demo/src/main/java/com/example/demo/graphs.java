/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.style.Styler;

public class graphs {
    
    public static void main(String[] args)
    {
        graphs graph = new graphs();
        WebService web = new WebService();
        graph.graphJobsPerCompany(web.printCompanies());
        graph.graphPopularAreas(web.printAreas());
        graph.graphPopularJobTitles(web.printTitles());

    }

    
    public void graphJobsPerCompany(Map<String, Long> companiesList) {
       
        // Create Chart
        PieChart chart = new PieChartBuilder().width (800).height (600).title (getClass().getSimpleName()).build();

        // Customize Chart
        Color[] sliceColors = new Color[]{
                new Color (130, 105, 120),
                new Color (180, 68, 50),
                new Color (70, 80, 130),
                new Color (80, 143, 160),
                new Color (200, 182, 90)
        };
        chart.getStyler ().setSeriesColors (sliceColors);

        // Series
        chart.addSeries ((String)companiesList.keySet().toArray()[0], (Number)companiesList.values().toArray()[0]);
        chart.addSeries ((String)companiesList.keySet().toArray()[1], (Number)companiesList.values().toArray()[1]);
        chart.addSeries ((String)companiesList.keySet().toArray()[2], (Number)companiesList.values().toArray()[2]);
        chart.addSeries ((String)companiesList.keySet().toArray()[3], (Number)companiesList.values().toArray()[3]);
        chart.addSeries ((String)companiesList.keySet().toArray()[4], (Number)companiesList.values().toArray()[4]);

        // Show it
        new SwingWrapper(chart).displayChart ();
    }

    public void graphPopularJobTitles(Map<String, Long> jobTitlesList) {
        //filter to get an array of passenger ages
        List<String> jobs = jobTitlesList.keySet().stream().limit(5).collect(Collectors.toList());
        List<Long> count = jobTitlesList.values().stream().limit(5).collect(Collectors.toList());

        //Using XChart to graph the job counter
        // Create Chart
        CategoryChart chart = new CategoryChartBuilder().width(1024).height(768).title("Most popular job titles")
                .xAxisTitle("Job titles").yAxisTitle("Count").build();

        // Customize Chart
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setHasAnnotations(true);
        chart.getStyler().setStacked(true);

        // Series
        chart.addSeries("Most popular job titles", jobs, count);

        // Show it
        new SwingWrapper(chart).displayChart();
    }

    public void graphPopularAreas(Map<String, Long> areasList) {
        //filter to get an array of passenger ages
        List<String> areas = areasList.keySet().stream().limit(5).collect(Collectors.toList());
        List<Long> count = areasList.values().stream().limit(5).collect(Collectors.toList());

        //Using XChart to graph the job counter
        // Create Chart
        CategoryChart chart = new CategoryChartBuilder().width(1024).height(768).title("Most popular areas")
                .xAxisTitle("Areas").yAxisTitle("Count").build();

        // Customize Chart
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setHasAnnotations(true);
        chart.getStyler().setStacked(true);

        // Series
        chart.addSeries("Most popular job titles", areas, count);

        // Show it
        new SwingWrapper(chart).displayChart();
    }
        
    
}
