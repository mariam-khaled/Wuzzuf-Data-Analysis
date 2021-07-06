/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo;

import java.awt.Color;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.style.Styler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smile.clustering.KMeans;
import smile.clustering.PartitionClustering;
import smile.data.DataFrame;
import smile.data.measure.NominalScale;
import smile.data.type.StructType;
import smile.data.vector.DoubleVector;
import smile.data.vector.IntVector;

@RestController()
@RequestMapping(path = "api/")
public class WebService {
    
    jobsDAO dao;
    DataFrame df;
    List<jobsPOJO> jobsList = null;
    Kmeans model = null;
    
    @Autowired
    public WebService()
    {
        this.dao = new jobsDAO();
        this.df = this.dao.read_clean_CSV("C:\\Users\\Marwan\\Documents\\NetBeansProjects\\smile_demo\\src\\main\\java\\com\\example\\demo\\Wuzzuf_Jobs.csv");
        this.jobsList = this.dao.getJobsList(df);
        this.model = new Kmeans();
    }
    
    @GetMapping(path="jobsdata")
    public String printData(){
        return this.df.toString();
    }
    
    @GetMapping(path="summary")
    public String printSummary(){
        return this.df.summary().toString();
    }
    
    @GetMapping(path="schema")
    public String printSchema(){
        return this.df.schema().toString();
    }
    
    @GetMapping(path="companies")
    public Map<String, Long> printCompanies()
    {
        Map<String, Long> companies = jobsList.stream().collect(
                Collectors.groupingBy(
                        jobsPOJO::getCompany,
                    Collectors.counting()));
    
        Map<String, Long> descSortedCompanies= new LinkedHashMap<>();
        companies.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> descSortedCompanies.put(x.getKey(), x.getValue()));; 
            
        return descSortedCompanies;
    }
    
    @GetMapping(path="titles")
    public Map<String, Long> printTitles()
    {
        Map<String, Long> companies = jobsList.stream().collect(
                Collectors.groupingBy(
                        jobsPOJO::getTitle,
                    Collectors.counting()));
    
        Map<String, Long> descSortedTitles= new LinkedHashMap<>();
        companies.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> descSortedTitles.put(x.getKey(), x.getValue()));; 
          
        return descSortedTitles;
    }
    
    @GetMapping(path="areas")
    public Map<String, Long> printAreas()
    {
        Map<String, Long> areas = jobsList.stream().collect(
                    Collectors.groupingBy(
                            jobsPOJO::getCountry,
                        Collectors.counting()));

        Map<String, Long> descSortedAreas= new LinkedHashMap<>();

        areas.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> descSortedAreas.put(x.getKey(), x.getValue()));;
                

        return descSortedAreas;
    }
    
    @GetMapping(path="allskills")
    public String[] printAllSkills(){
        String[] skills = df.apply("Skills").toStringArray();
        return skills;
    }
    
    @GetMapping(path="orderedskills")
    public Map<String, Long> printSortedSkills()
    {
        Map<String, Long> skills = jobsList.stream().collect(
                Collectors.groupingBy(
                        jobsPOJO::getSkills,
                    Collectors.counting()));
    
        Map<String, Long> descSortedSkills= new LinkedHashMap<>();
        skills.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> descSortedSkills.put(x.getKey(), x.getValue()));; 
            
           return descSortedSkills;
    }
    
    @GetMapping(path="factorization")
    public List<String> printFactorized()
    {
        List<String> factorized_years_exp = new LinkedList<String>();
        String [] years_exp = df.apply("YearsExp").toStringArray();
        for (String row: years_exp)
        {
            String[] splitted = row.split(" ");
            String numbers = splitted[0];
            if (numbers.indexOf('-') != -1)
                factorized_years_exp.add(numbers.substring(0, numbers.indexOf('-')));
            
            else if (numbers.indexOf('+') != -1)
                factorized_years_exp.add(numbers.substring(0, numbers.indexOf('+')));
        }
        
        return factorized_years_exp;
        
    } 
    
    @GetMapping(path="kmeans")
    public double[][] printKmeans()
    {   
        DataFrame data = this.df;
        double [][] in_data = new double[2][data.size()];
        int no_clusters = 2;
        return this.model.fit(in_data, no_clusters).centroids;
        
    }
    
    
    
    }
    
    
    
