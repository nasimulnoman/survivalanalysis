package jmetal.test.survivalanalysis;

//GenerateSurvivalGraph.java
//
//Author:
// Nasimul Noman <nasimul.noman@newcastle.edu.au>
//
//Copyright (c) 2015 Nasimul Noman
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

import java.io.File;
import java.util.Enumeration;

import org.rosuda.JRI.Rengine;
import org.rosuda.JRI.REXP;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.HierarchicalClusterer;
import weka.core.Attribute;
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
//import javastat.survival.inference.LogRankTest;
//import javastat.survival.inference.WilcoxonTest;
//import javastat.StatisticalAnalysis;
import survivalanalysis.*;

/**
*  Class for generating data for Survival Analysis Curve. 
* After MOGA selects the feature the selected features are represented by chromosome  
* This class takes the chromosome as input and then creates separate files for creating survival curves 
*/

public class GenerateSurvivalGraph extends Problem {

	private String dataFileName;
	private String testDataFileName;
	private Attribute attTime;
	private Attribute attCensor;
	private Boolean printClusterAssignment=true;
	
	
	public Rengine re;
	
	public String SolutionID = "Soln-1";
	
	private Boolean pValueFlag; // Flag to determine which score to be used true: pvalue and false: statisticscore
	private Boolean featureMax; // Flag to determine feature to be minimized or maximized: true: maximized, false: minimized
	
	private String HC_LinkType;
	
	
	
	public GenerateSurvivalGraph(String solutionType, Integer numberOfBits, Integer numberOfObjectives, String dataFileName, String testDataFile, Rengine rEng, Boolean pVal, Boolean fMax, String linkType) {
		numberOfVariables_  = 1;
		numberOfObjectives_ = numberOfObjectives;
		numberOfConstraints_= 0;
		problemName_        = "SurvivalAnalysisGraph: " + dataFileName;
		this.dataFileName = dataFileName;
		this.testDataFileName = testDataFile;
		this.re=rEng;
		
		this.pValueFlag = pVal;
		this.featureMax = fMax;
		this.HC_LinkType = linkType;
		
		solutionType_ = new BinarySolutionType(this) ;

		length_       = new int[numberOfVariables_];
		length_      [0] = numberOfBits ;

		
		if (solutionType.compareTo("Binary") == 0)
			solutionType_ = new BinarySolutionType(this) ;
		else {
			System.out.println("SurvivalAnalaysisGraph: solution type " + solutionType + " invalid") ;
			System.exit(-1) ;
		}  
		
		if (this.pValueFlag){
			System.out.print("Survival Analysis.Obj 1: pValue minimization. "); 
		}
		else{
			System.out.print("Survival Analysis.Obj 1: statScore maximization. "); 
		}
		if (this.featureMax){
			System.out.print("Obj 2: feature maximization.");
		}
		else{
			System.out.print("Obj 2: feature minimization.");
		}
		if (this.numberOfObjectives_==3){
			System.out.println("Obj 3: Arithetic Hermonic Cut Maximization");
		}
		else{
			System.out.println();
		}

	} // SurvivalAnalysisGraph

	/** 
	 * Evaluates a solution 
	 * @param solution The solution to evaluate
	 */      
	public void evaluate(Solution solution) {
		Binary variable ;
		int    counterSelectedFeatures;

		DataSource source;

		double testStatistic = Double.MAX_VALUE;
		double pValue= Double.MAX_VALUE;
		double ArithmeticHarmonicCutScore = Double.MAX_VALUE;
		//double statScore;
		REXP x;

		
		variable = ((Binary)solution.getDecisionVariables()[0]) ;

		counterSelectedFeatures = 0 ;



		try {
			// read the data file 
			source = new DataSource(this.dataFileName);
			Instances data = source.getDataSet();
			//System.out.print("Data read successfully. ");
			//System.out.print("Number of attributes: " + data.numAttributes());
			//System.out.println(". Number of instances: " + data.numInstances());


			// save the attribute 'T' and 'Censor'
			attTime = data.attribute(data.numAttributes()-2);
			attCensor = data.attribute(data.numAttributes()-1);

			
			// First filter the attributes based on chromosome
			Instances tmpData = this.filterByChromosome(data, solution);


			// Now filter the attribute 'T' and 'Censor'
			Remove filter = new Remove();
			 // remove the two last attributes : 'T' and 'Censor'
			filter.setAttributeIndices(""+(tmpData.numAttributes()-1)+","+tmpData.numAttributes());
			//System.out.println("After chromosome filtering no of attributes: " + tmpData.numAttributes());
			filter.setInputFormat(tmpData);
			Instances dataClusterer = Filter.useFilter(tmpData, filter);

			// filtering complete

			// List the selected features/attributes
			Enumeration<Attribute> attributeList = dataClusterer.enumerateAttributes();
			System.out.println("Selected attributes/features: ");
			while(attributeList.hasMoreElements()){
				Attribute att = attributeList.nextElement();
				System.out.print(att.name()+ ",");
			}
			
			System.out.println();


			/*
			// debug: write the filtered dataset

	 		ArffSaver saver = new ArffSaver();
	 		saver.setInstances(dataClusterer);
	 		saver.setFile(new File("filteered-data.arff"));
	 		saver.writeBatch();
			// end debug
			
			*/



			// train hierarchical clusterer

			HierarchicalClusterer clusterer = new HierarchicalClusterer();
			clusterer.setOptions(new String[] {"-L", this.HC_LinkType});  
			//Link type (Single, Complete, Average, Mean, Centroid, Ward, Adjusted complete, Neighbor Joining)
			//[SINGLE|COMPLETE|AVERAGE|MEAN|CENTROID|WARD|ADJCOMPLETE|NEIGHBOR_JOINING]
			
			//clusterer.setDebug(true);
			clusterer.setNumClusters(2);
			clusterer.setDistanceFunction(new EuclideanDistance());
			clusterer.setDistanceIsBranchLength(false);  // ?? Should it be changed to false? (Noman)

			clusterer.buildClusterer(dataClusterer);
			
			double[][] distanceMatrix = clusterer.getDistanceMatrix();

			
			// Cluster evaluation:
			ClusterEvaluation eval = new ClusterEvaluation();
			eval.setClusterer(clusterer);
			
			if (this.testDataFileName != null){
			
				DataSource testSource = new DataSource(this.testDataFileName);
			
				Instances tmpTestData = testSource.getDataSet();
				tmpTestData.setClassIndex(tmpTestData.numAttributes()-1);
				//testSource.
			
				// First filter the attributes based on chromosome
				Instances testData = this.filterByChromosome(tmpTestData, solution);
				//String[] options = new String[2];
				//options[0] = "-t";
				//options[1] = "/some/where/somefile.arff";
				//eval.
				//System.out.println(eval.evaluateClusterer(testData, options));
				eval.evaluateClusterer(testData);
				System.out.println("\nCluster evluation for this solution("+this.testDataFileName+"): " + eval.clusterResultsToString());
			}

			// First analyze using my library function

			
			// save the cluster assignments
			
				int[] clusterAssignment = new int[dataClusterer.numInstances()];
				int classOneCnt = 0;
				int classTwoCnt = 0;
				for (int i=0; i<dataClusterer.numInstances(); ++i){
					clusterAssignment[i] = clusterer.clusterInstance(dataClusterer.get(i));
					if (clusterAssignment[i]==0){
						++classOneCnt;
					}
					else if (clusterAssignment[i]==1){
						++classTwoCnt;
					}
					//System.out.println("Instance " + i + ": " + clusterAssignment[i]);
				}

				System.out.println("Class 1 cnt: " + classOneCnt + " Class 2 cnt: " + classTwoCnt);


				// create arrays with time (event occurrence time) and censor data for use with jstat LogRankTest
				double[] time1 = new double[classOneCnt];	
				double[] censor1 = new double[classOneCnt];
				double[] time2 = new double[classTwoCnt];
				double[] censor2 = new double[classTwoCnt];


				//data = source.getDataSet();
				for (int i=0, cnt1=0, cnt2=0; i<dataClusterer.numInstances(); ++i){
					//clusterAssignment[i] = clusterer.clusterInstance(dataClusterer.get(i));
					if (clusterAssignment[i]==0){
						time1[cnt1] = data.get(i).value(attTime);
						censor1[cnt1++] = data.get(i).value(attCensor);
						//System.out.println("i: " + i + " T: " + time1[cnt1-1]);
					}
					else if (clusterAssignment[i]==1){
						time2[cnt2] = data.get(i).value(attTime);
						//System.out.println("i: " + i + " T: " + time2[cnt2-1]);
						censor2[cnt2++] = data.get(i).value(attCensor);;
					}
					//System.out.println("Instance " + i + ": " + clusterAssignment[i]);
				}


				//Instances[] classInstances = separateClassInstances(clusterAssignment, this.dataFileName,solution);
				//System.out.println("Class instances seperated");

				// calculate log rank test and p values

				LogRankTest testclass1 = new LogRankTest(time1, time2, censor1, censor2);
				double [] scores = testclass1.logRank();
				testStatistic = scores[0];
				pValue = scores[2];
				
				
				ArithmeticHarmonicCutScore = this.getArithmeticHarmonicCutScore(distanceMatrix, clusterAssignment);
				//debug:
				System.out.println("Calculation by myLibrary:\n testStatistic: " + scores[0] + " pValue: " + scores[2] + " Arithmetic Harmonic Cut Score: " + ArithmeticHarmonicCutScore);
				//end debug
				//WilcoxonTest testclass1 = new WilcoxonTest(time1, censor1, time2, censor2);
				//testStatistic = testclass1.testStatistic;
				//pValue = testclass1.pValue;true
			
			// Now analyze calling R for Log Rank test, Parallelization not possible

				String strT = "time <- c(";
				String strC = "censor <- c(";
				String strG = "group <- c(";


				for (int i=0; i<dataClusterer.numInstances()-1; ++i){
					strT = strT + (int) data.get(i).value(attTime) + ",";
					strG = strG + clusterer.clusterInstance(dataClusterer.get(i)) + ",";
					strC = strC + (int) data.get(i).value(attCensor) + ",";
				}

				int tmpi = dataClusterer.numInstances() -1;
				strT = strT + (int) data.get(tmpi).value(attTime) + ")";
				strG = strG + clusterer.clusterInstance(dataClusterer.get(tmpi)) + ")";
				strC = strC + (int) data.get(tmpi).value(attCensor)+")";


				this.re.eval(strT);
				this.re.eval(strC);
				this.re.eval(strG);

				//debug
				//System.out.println(strT);
				//System.out.println(strC);
				//System.out.println(strG);
				//end debug


				/** If you are calling surv_test from coin library */
				/*v
				re.eval("library(coin)");
				re.eval("grp <- factor (group)");
				re.eval("result <- surv_test(Surv(time,censor)~grp,distribution=\"exact\")");

				x=re.eval("statistic(result)");
				testStatistic = x.asDouble();
				//x=re.eval("pvalue(result)");
				//pValue = x.asDouble();
				//System.out.println("StatScore: " + statScore + "pValue: " + pValue);
				 */

				/** If you are calling survdiff from survival library (much faster) */
				re.eval("library(survival)");
				re.eval("res2 <- survdiff(Surv(time,censor)~group,rho=0)");
				x=re.eval("res2$chisq");
				testStatistic=x.asDouble();
				//System.out.println(x);
				x = re.eval("pchisq(res2$chisq, df=1, lower.tail = FALSE)");
				//x = re.eval("1.0 - pchisq(res2$chisq, df=1)");
				pValue = x.asDouble();
				//debug:
				//System.out.println("Calculation by R: StatScore: " + testStatistic + "pValue: " + pValue);
				//end debug
				
				
				System.out.println("Calculation by R:");
				System.out.println("StatScore: " + testStatistic + "  pValue: " + pValue);

				re.eval("timestrata1.surv <- survfit( Surv(time, censor)~ strata(group), conf.type=\"log-log\")");
				re.eval("timestrata1.surv1 <- survfit( Surv(time, censor)~ 1, conf.type=\"none\")");
				String evalStr = "jpeg('SurvivalPlot-"+this.SolutionID+".jpg')";
				re.eval(evalStr);
				re.eval("plot(timestrata1.surv, col=c(2,3), xlab=\"Time\", ylab=\"Survival Probability\")");
				re.eval("par(new=T)");
				re.eval("plot(timestrata1.surv1,col=1)");
				re.eval("legend(0.2, c(\"Group1\",\"Group2\",\"Whole\"))");
				re.eval("dev.off()");

			
				System.out.println("\nCluster Assignments:");
				for (int i=0; i<dataClusterer.numInstances(); ++i){
					System.out.println("Instance " + i + ": " + clusterAssignment[i]);
				}
				
	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("Can't open the data file.");
			e.printStackTrace();
			System.exit(1);
		}


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
		// i.e. Select attributes to be removed based on Individual's chromosMome
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

	
	public String getDataFileName(){
		return this.dataFileName;
	}
	
	
	/***
	 * Calculate the Arithmetic Harmonic Cut score
	 * @return
	 */
	public double getArithmeticHarmonicCutScore(double[][] distMatrix, int[] clusterAssignment){
		double AHCS=0;
		
		double ahcsT1 =0;
		double ahcsT2 = 0;
		
		for (int i=0; i<clusterAssignment.length; ++i){
			
			for (int j=i+1; j<clusterAssignment.length; ++j){
				if (clusterAssignment[i]==clusterAssignment[j]){ // both belongs the the same cluster
					ahcsT2 += 1.0/(distMatrix[i][j]);
				}
				else{
					ahcsT1 += distMatrix[i][j];
				}
			}
		}
		
		AHCS = ahcsT1 * ahcsT2;
		
		return AHCS;
	}
	
	public void setSolnID(String id){
		this.SolutionID = id;
	}

} // SurvivalAnalysis
