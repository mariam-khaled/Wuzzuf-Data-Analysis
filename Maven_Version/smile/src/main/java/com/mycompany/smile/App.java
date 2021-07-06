/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.smile;

import java.awt.Color;
import smile.data.DataFrame;
import smile.data.formula.Formula;
import smile.data.measure.NominalScale;
import smile.data.vector.IntVector;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.javatuples.Triplet;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.style.Styler;
import smile.clustering.KMeans;
import smile.clustering.PartitionClustering;
import smile.data.Tuple;
import smile.data.vector.DoubleVector;
import smile.data.vector.StringVector;
import smile.validation.*;
import smile.validation.metric.Accuracy;
//import joinery.DataFrame;

public class App {
    
    public static void main(String[] args) throws IOException {
        
    App ap = new App();
    jobsDAO wuzzuf_reader = new jobsDAO();
    DataFrame jobs = wuzzuf_reader.read_clean_CSV("src/main/resources/Wuzzuf_Jobs.csv");
    
    //DISPLAY FIRST 10 LINES
    for(int i=0; i<10; i++)
    {
        System.out.println(jobs.get(i));
    }
    
    //DISPLAY SCHEMA
    System.out.println(jobs.structure());
    
    //DISPLAY SUMMARY
    System.out.println(jobs.summary());
    
    // JOBS PER COMPANIES 
    List<jobsPOJO> jobsList = wuzzuf_reader.getJobsList();
    
    Map<String, Long> companies = jobsList.stream().collect(
                Collectors.groupingBy(
                        jobsPOJO::getCompany,
                    Collectors.counting()));
    
    Map<String, Long> descSortedCompanies= new LinkedHashMap<>();
    companies.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .forEachOrdered(x -> descSortedCompanies.put(x.getKey(), x.getValue()));; 
    descSortedCompanies.forEach((key, value) -> System.out.println(key + ":" + value));
    
    //PIE CHART
    ap.graphJobsPerCompany(descSortedCompanies);
    
    //POPULAR JOB TITLES
    Map<String, Long> titles = jobsList.stream().collect(
                Collectors.groupingBy(
                        jobsPOJO::getTitle,
                    Collectors.counting()));
    
    Map<String, Long> descSortedTitles= new LinkedHashMap<>();
    titles.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .forEachOrdered(x -> descSortedTitles.put(x.getKey(), x.getValue()));; 
    descSortedTitles.forEach((key, value) -> System.out.println(key + ":" + value));
    
    //BAR CHART
    ap.graphPopularJobTitles(descSortedTitles);
    
    // AREAS
    Map<String, Long> areas = jobsList.stream().collect(
                Collectors.groupingBy(
                        jobsPOJO::getCountry,
                    Collectors.counting()));
    
    Map<String, Long> descSortedAreas= new LinkedHashMap<>();
   
    areas.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .forEachOrdered(x -> descSortedAreas.put(x.getKey(), x.getValue()));; 
    descSortedAreas.forEach((key, value) -> System.out.println(key + ":" + value));
    
    //BAR CHART
    ap.graphPopularAreas(descSortedAreas);
    
    // ALL SKILLS
    String[] skills = jobs.apply("Skills").toStringArray();
    for(String skill: skills)
        System.out.println(skill);
    
    // ORDERED SKILLS
    Map<String, Long> grouped_skills = jobsList.stream().collect(
                Collectors.groupingBy(
                        jobsPOJO::getSkills,
                    Collectors.counting()));
    
        Map<String, Long> descSortedSkills= new LinkedHashMap<>();
        grouped_skills.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).distinct()
                .forEachOrdered(x -> descSortedSkills.put(x.getKey(), x.getValue()));;
        descSortedSkills.forEach((key, value) -> System.out.println(key + ":" + value));
        
    //YEARS EXP. FACTORIZATION
    List<String> factorized_years_exp = new LinkedList<String>();
    String [] years_exp = jobs.apply("YearsExp").toStringArray();
    for (String row: years_exp)
    {
        String[] splitted = row.split(" ");
        String numbers = splitted[0];
        if (numbers.indexOf('-') != -1)
            factorized_years_exp.add(numbers.substring(0, numbers.indexOf('-')));

        else if (numbers.indexOf('+') != -1)
            factorized_years_exp.add(numbers.substring(0, numbers.indexOf('+')));
    }
    System.out.println(factorized_years_exp.toString());

    // KMEANS
    DataFrame data = jobs;
    
    data = data.merge (DoubleVector.of ("NewTitles", ap.encodeCategory (data, "Title")));
       
    data = data.merge (DoubleVector.of ("NewCompanies", ap.encodeCategory (data, "Company")));

    double [][] in_data = new double[2][data.size()];
    int no_clusters = 2;

    for(int i=0; i<data.size(); i++)
    {
        in_data[0][i] = (double) data.column("NewTitles").get(i);
        in_data[1][i] = (double) data.column("NewCompanies").get(i);
    }

    int run = 10;
    double [][] cen = PartitionClustering.run(10, ()-> KMeans.fit(in_data, no_clusters)).centroids;
    for (double[] row : cen)
            System.out.println(Arrays.toString(row));

    }
    
    public double[] encodeCategory(DataFrame df, String columnName)
    {
        String[] values = df.stringVector (columnName).distinct ().toArray (new String[]{});
        double[] pclassValues = df.stringVector (columnName).factorize (new NominalScale (values)).toDoubleArray ();
        return pclassValues;
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
