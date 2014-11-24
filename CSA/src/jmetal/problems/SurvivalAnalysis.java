//SurvivalAnalsys.java
//
//Author:
//   Nasimul Noman <nasimul.noman@newcastle.edu.au>
//
//Copyright (c) 2014 Nasimul Noman
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU Lesser General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>. * SurvivalAnalysis.java

package jmetal.problems ;

import java.io.File;

import weka.clusterers.HierarchicalClusterer;
import weka.core.EuclideanDistance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.BinarySolutionType;
import jmetal.encodings.variable.Binary;

/**
* Class representing problem SurvivalAnalysis. The problem consist of feature selection
* using Survival Analysis curve. The features are selected using a binary string
* where '1's and '0's represents the selected and non-selected features respectively.
*/

public class SurvivalAnalysis extends Problem {

	private String dataFileName;
 /**
  * Creates a new SurvivalAnalysis problem instance
* @param solutionType Solution type
 * @throws ClassNotFoundException 
 * default problem size 1000
  */
public SurvivalAnalysis(String solutionType) throws ClassNotFoundException {
	this(solutionType, 1000, null) ;
}

/**
* Creates a new SurvivalAnalysis problem instance
* @param solutionType Solution type
* @param numberOfBits Length of the problem
*/
public SurvivalAnalysis(String solutionType, Integer numberOfBits, String dataFileName) {
numberOfVariables_  = 1;
numberOfObjectives_ = 2;
numberOfConstraints_= 0;
problemName_        = "SurvivalAnalysis";
this.dataFileName = dataFileName;
         
solutionType_ = new BinarySolutionType(this) ;
	    
length_       = new int[numberOfVariables_];
length_      [0] = numberOfBits ;

if (solutionType.compareTo("Binary") == 0)
	solutionType_ = new BinarySolutionType(this) ;
else {
	System.out.println("SurvivalAnalaysis: solution type " + solutionType + " invalid") ;
	System.exit(-1) ;
}  

} // SurvivalAnalysis

/** 
* Evaluates a solution 
* @param solution The solution to evaluate
*/      
public void evaluate(Solution solution) {
Binary variable ;
int    counterOnes   ;
int    counterZeroes ;
DataSource source;

variable = ((Binary)solution.getDecisionVariables()[0]) ;

counterOnes = 0 ;
counterZeroes = 0 ;



try {
	// read the data file 
	source = new DataSource(this.dataFileName);
	Instances data = source.getDataSet();
	//System.out.print("Data read successfully. ");
	//System.out.print("Number of attributes: " + data.numAttributes());
	//System.out.println(". Number of instances: " + data.numInstances());

	
	// First filter the data based on chromosome
	Instances tmpData = this.filterByChromosome(data, solution);
	
	
	// Again Filter the attribute 'T'
	Remove filter = new Remove();
	filter.setAttributeIndices(""+tmpData.numAttributes()); // remove the last attribute : 'T'
	//System.out.println("After chromosome filtering no of attributes: " + tmpData.numAttributes());
	filter.setInputFormat(tmpData);
	Instances dataClusterer = Filter.useFilter(tmpData, filter);
	
	// filtering complete
	
	
	
	// Debug: write the filtered dataset
	 /*
	 ArffSaver saver = new ArffSaver();
	 saver.setInstances(dataClusterer);
	 saver.setFile(new File("filteered-data.arff"));
	 saver.writeBatch();
    */
	
	
	
	// train hierarchical clusterer
	
	HierarchicalClusterer clusterer = new HierarchicalClusterer();
	clusterer.setOptions(new String[] {"-L", "COMPLETE"});  // complete linkage clustering
	clusterer.setDebug(true);
	clusterer.setNumClusters(2);
	clusterer.setDistanceFunction(new EuclideanDistance());
	clusterer.setDistanceIsBranchLength(true);
	
	clusterer.buildClusterer(dataClusterer);
	
	// save the cluster assignments
	int[] clusterAssignment = new int[dataClusterer.numInstances()];	
	for (int i=0; i<dataClusterer.numInstances(); ++i){
		clusterAssignment[i] = clusterer.clusterInstance(dataClusterer.get(i));
		//System.out.println("Instance " + i + ": " + clusterAssignment[i]);
	}
	
	
	Instances[] classInstances = separateClassInstances(clusterAssignment, this.dataFileName,solution);
	//System.out.println("Class instances seperated");
	
	
	
				
} catch (Exception e) {
	// TODO Auto-generated catch block
	System.err.println("Can't open the data file.");
	e.printStackTrace();
	System.exit(1);
}

// Currently this section implements the OneZeroMax problem - need to modify it
for (int i = 0; i < variable.getNumberOfBits() ; i++) 
  if (variable.bits_.get(i))
    counterOnes ++ ;
  else
  	counterZeroes ++ ;

// OneZeroMax is a maximization problem: multiply by -1 to minimize
solution.setObjective(0, -1.0*counterOnes);            
solution.setObjective(1, -1.0*counterZeroes);            
} // evaluate


/*******************************************************
 * Separates the data file into two based on class assignments of instances 
 * @param classAssignment
 * @param fileName
 * @return
 * @throws Exception
 */

private Instances[] separateClassInstances(int[] classAssignment, String fileName, Solution solution) throws Exception{
	Instances classInstances[] = new Instances[2];
	Instances tmpInstances[] = new Instances[2];
	DataSource source = new DataSource(fileName);
	
	tmpInstances[0] = source.getDataSet();
	tmpInstances[1] = source.getDataSet();
	
	
	// First filter the attributes based on chromosome
	classInstances[0] = filterByChromosome(tmpInstances[0],solution);
	classInstances[1] = filterByChromosome(tmpInstances[1],solution);
	
	
	// Now filter instances into two files based on class assignment
	
	// filter class 1 instances : remains class 0 instances
	for (int i= classAssignment.length-1; i>=0; --i) {
	    //Instance inst = classInstances[0].get(i);
	    if (classAssignment[i] == 0) {
	        classInstances[0].delete(i);
	    }
	}
	
	// filter class 0 instances : remains class 1 instances
	for (int i= classAssignment.length-1; i>=0; --i) {
	    //Instance inst = classInstances[0].get(i);
	    if (classAssignment[i] == 1) {
	        classInstances[1].delete(i);
	    }
	}
	
	// Save instances
	
	 ArffSaver saver = new ArffSaver();
	 saver.setInstances(classInstances[0]);
	 saver.setFile(new File("class-0.arff"));
	 //saver.setDestination(new File("./data/test.arff"));   // **not** necessary in 3.5.4 and later
	 saver.writeBatch();

	 saver = new ArffSaver();
	 saver.setInstances(classInstances[1]);
	 saver.setFile(new File("class-1.arff"));
	 //saver.setDestination(new File("./data/test.arff"));   // **not** necessary in 3.5.4 and later
	 saver.writeBatch();

	 
	return classInstances;
}

/******************************************************
 * Filters the features based on the current chromosome/solution
 *  
 */
  Instances filterByChromosome(Instances data, Solution solution){
	  
	  Binary variable = ((Binary)solution.getDecisionVariables()[0]) ;
	  Instances dataClusterer = null;
	  
	// Use the individual chromosome as the selected features: '1' selected, '0' filtered
		// i.e. Select attributes to be removed based on Individual's chromosome
		Remove filter = new Remove();
		int cntFilteredAttr = 0;  // count the number of attributes to be removed
		for (int i=0; i<variable.getNumberOfBits(); ++i){
			if (!variable.bits_.get(i)){
				++cntFilteredAttr;
			}
		}
		
		int[] filteredAttributes = new int[cntFilteredAttr];
		for (int i=0,j=0; i<variable.getNumberOfBits(); ++i){
			if (!variable.bits_.get(i)){
				filteredAttributes[j] = i;
				++j;
			}
		}
		
		filter.setAttributeIndicesArray(filteredAttributes);
		try {
			filter.setInputFormat(data);
			dataClusterer = Filter.useFilter(data, filter);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.print("Problem in filtering attributes according to chromosome");
			e.printStackTrace();
		}
		
		// filtering complete
		return (dataClusterer);
  }
  
  
} // SurvivalAnalysis
