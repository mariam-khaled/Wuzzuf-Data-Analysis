/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo;

import smile.clustering.KMeans;
import smile.clustering.PartitionClustering;
import smile.data.DataFrame;
import smile.data.measure.NominalScale;
import smile.data.vector.DoubleVector;

public class Kmeans {

    Kmeans() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public KMeans fit(double [][]in_data, int no_clusters)
    {   
        WebService web = new WebService();
        DataFrame data = web.df;
        data = data.merge (DoubleVector.of ("NewTitles", encodeCategory (data, "Title")));
        data = data.merge (DoubleVector.of ("NewCompanies", encodeCategory (data, "Company")));
        
        
        for(int i=0; i<data.size(); i++)
        {
            in_data[0][i] = (double) data.column("NewTitles").get(i);
            in_data[1][i] = (double) data.column("NewCompanies").get(i);
        }
        
        int run = 10;
        return PartitionClustering.run(10, ()-> KMeans.fit(in_data, no_clusters));
        
    }
    
    
    
    public double[] encodeCategory(DataFrame df, String columnName) {
        String[] values = df.stringVector (columnName).distinct ().toArray (new String[]{});
        double[] pclassValues = df.stringVector (columnName).factorize (new NominalScale (values)).toDoubleArray ();
        return pclassValues;
    }
    
}
