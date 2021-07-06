/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo;

import java.io.IOException;
import smile.data.DataFrame;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import smile.data.Tuple;
import smile.io.Read;


public class jobsDAO {
    
    DataFrame df = null;
    
    public DataFrame read_clean_CSV(String path) {
        CSVFormat format = CSVFormat.DEFAULT.withFirstRecordAsHeader ();
        try {
            df = Read.csv (path, format);
        } catch (IOException e) {
            e.printStackTrace ();
        } catch (URISyntaxException e) {
            e.printStackTrace ();
        }
        
        // REMOVE NULLS AND DUPLICATES
        df = this.preprocess(df);
        return df;
    }
    
    public DataFrame preprocess(DataFrame df)
    {   
        //REMOVE DUPLICATES
        df.stream().distinct();
        
        //REMOVE NULLS
        df = df.omitNullRows();
        
        return df;
    }
    
    public List<jobsPOJO> getJobsList(DataFrame df) {
        assert df != null;
        List<jobsPOJO> jobs = new ArrayList<> ();
        ListIterator<Tuple> iterator = df.stream ().collect (Collectors.toList ()).listIterator ();
        while (iterator.hasNext ()) {
            Tuple t = iterator.next ();
            jobsPOJO j = new jobsPOJO();
        
            j.setTitle((String) t.get ("Title"));
            j.setCompany((String) t.get ("Company"));
            j.setLocation ((String) t.get ("Location"));
            j.setType((String) t.get ("Type"));
            j.setLevel((String) t.get ("Level"));
            j.setYearsExp((String) t.get ("YearsExp"));
            j.setCountry((String) t.get ("Country"));
            j.setSkills((String) t.get ("Skills"));
            
            jobs.add (j);
        }
        return jobs;
    }

   
}