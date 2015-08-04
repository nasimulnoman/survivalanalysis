//NSGAII_SurvivalAnalysis_main.java
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
//along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jmetal.metaheuristics.nsgaII;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ProblemFactory;
import jmetal.problems.SurvivalAnalysis;
import jmetal.problems.ZDT.ZDT3;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Configuration;
import jmetal.util.JMException;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/** 
 * Class to configure and execute the NSGA-II algorithm.  
 *     
 * Besides the classic NSGA-II, a steady-state version (ssNSGAII) is also
 * included (See: J.J. Durillo, A.J. Nebro, F. Luna and E. Alba 
 *                  "On the Effect of the Steady-State Selection Scheme in 
 *                  Multi-Objective Genetic Algorithms"
 *                  5th International Conference, EMO 2009, pp: 183-197. 
 *                  April 2009)
 */ 

public class NSGAII_SurvivalAnalysis_main {
	public static Logger      logger_ ;      // Logger object
	public static FileHandler fileHandler_ ; // FileHandler object

	/**
	 * @param args Command line arguments.
	 * @throws JMException 
	 * @throws IOException 
	 * @throws SecurityException 
	 * Usage: three options
	 *      - jmetal.metaheuristics.nsgaII.NSGAII_main
	 *      - jmetal.metaheuristics.nsgaII.NSGAII_main problemName
	 *      - jmetal.metaheuristics.nsgaII.NSGAII_main problemName paretoFrontFile
	 */
	public static void main(String [] args) throws 
	JMException, 
	SecurityException, 
	IOException, 
	ClassNotFoundException {
		Problem   problem   ; // The problem to solve
		Algorithm algorithm ; // The algorithm to use
		Operator  crossover ; // Crossover operator
		Operator  mutation  ; // Mutation operator
		Operator  selection ; // Selection operator
		Boolean pValue; // decides whether pValue to be use or statistic score to be used
		Boolean featureMaximization; //decides whether features to be maximized or minimized 

		HashMap<String, Double>  parameters ; // Operator parameters

		QualityIndicator indicators ; // Object to get quality indicators

		//String dataFileName = "basalSamplesRef33_169.arff";
		//String dataFileName = "filteredDataMSTkNNk=2.arff"; //"basalSamplesRef33_ClinicalData.nbi.final_389_25.arff";
		String dataFileName= "GBM-1740x196-Min-CMP.arff";//"GBM-1740x194-Max-L1S3-G2.arff";//"GBM-1740x196.arff";//"1740X38-Soln10-Soln05-Soln02-Soln17-G10.arff";//"1750X66-Soln10-Soln05-Soln10-G7.arff";//"1750X55-Soln10-Soln05-Soln10-G8.arff";//"1740X46-Sol10-Sol05-Sol02-G6.arff";//"1740X27-Soln10-G1.arff"; //"1740X48-Soln10-Soln05-G4.arff"; //"1740X48-Soln10-Soln05-G3.arff"; //"1740x168.NA.filtered.Soln09.G2.arff"; //"1740x196.NA.filtered.G2.arff";//"GBM-1740x196.arff"; //"GBM-Proneural-210x52.arff";//"GBM-Proneural1740x56.arff";//"_BC1000MostVar.arff";//"_BC500mostVar.arff"; //"BC1_MostVar.arff"; //"_BC3-ff.arff";
		DataSource source;

		
		// Logger object and file to store log messages
		logger_      = Configuration.logger_ ;
		fileHandler_ = new FileHandler("NSGAII_SurvivalAnalysis_main"+dataFileName+".log"); 
		logger_.addHandler(fileHandler_) ;

		indicators = null ;

		int numberOfBits = 25;

		Instances data = null;

		System.out.println("Data file name: " + dataFileName);
		try {
			source = new DataSource(dataFileName);
			data = source.getDataSet();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("Error in reading data file: " + dataFileName);
			e.printStackTrace();
		}

		numberOfBits = data.numAttributes() - 2; // Number of attributes omitting Time attribute and the censor attribute
		
		
		// Connect to R Engine
		
		if (!Rengine.versionCheck()) {
		    System.err.println("** Version mismatch - Java files don't match library version.");
		    System.exit(1);
		}
	    System.out.println("Creating Rengine (with arguments)");
			// 1) we pass the arguments from the command line
			// 2) we won't use the main loop at first, we'll start it later
			//    (that's the "false" as second argument)
			// 3) the callbacks are implemented by the TextConsole class above
		Rengine re=new Rengine(args, false, new TextConsole());
	    System.out.println("Rengine created, waiting for R");
			// the engine creates R is a new thread, so we should wait until it's ready
	    if (!re.waitForR()) {
	            System.err.println("Cannot load R");
	            return;
	    }
	    pValue = true;
	    featureMaximization = false;
	    String linkType = "COMPLETE"; 
	    
	    
	    // Hieararchical Clustering Link type (Single, Complete, Average, Mean, Centroid, Ward, Adjusted complete, Neighbor Joining)
	  //[SINGLE|COMPLETE|AVERAGE|MEAN|CENTROID|WARD|ADJCOMPLETE|NEIGHBOR_JOINING]
	    
	    int noOfObj = 2;
	    problem = new SurvivalAnalysis("Binary",numberOfBits,noOfObj, dataFileName, re, pValue, featureMaximization, linkType);
		System.out.println ("Hierarchical Clustering LInktype: " + linkType);
		algorithm = new NSGAII(problem);
		//algorithm = new ssNSGAII(problem);

		// Algorithm parameters
		algorithm.setInputParameter("populationSize",200); //100
		algorithm.setInputParameter("maxEvaluations",200); //25000

		// Mutation and Crossover for Real codification 
		parameters = new HashMap<String, Double>() ;
		parameters.put("probability", 0.9) ;
		parameters.put("distributionIndex", 20.0) ;

		crossover = CrossoverFactory.getCrossoverOperator("HUXCrossover", parameters);                   

		parameters = new HashMap<String, Double>() ;
		parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
		mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);                    

		// Selection Operator 
		parameters = null ;
		selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters) ;                           

		// Add the operators to the algorithm
		algorithm.addOperator("crossover",crossover);
		algorithm.addOperator("mutation",mutation);
		algorithm.addOperator("selection",selection);

		// Add the indicator object to the algorithm
		algorithm.setInputParameter("indicators", indicators) ;

		// Execute the Algorithm
		long initTime = System.currentTimeMillis();
		SolutionSet population = algorithm.execute();
		long estimatedTime = System.currentTimeMillis() - initTime;

		// Result messages 
		logger_.info("Total execution time: "+estimatedTime + "ms");
		logger_.info("Variables values have been writen to file VAR"+dataFileName);
		population.printVariablesToFile("VAR-"+dataFileName);    
		logger_.info("Objectives values have been writen to file FUN"+dataFileName);
		population.printObjectivesToFile("FUN-"+dataFileName);
		/*
		if (indicators != null) {
  			logger_.info("Quality indicators") ;
  			logger_.info("Hypervolume: " + indicators.getHypervolume(population)) ;
  			logger_.info("GD         : " + indicators.getGD(population)) ;
  			logger_.info("IGD        : " + indicators.getIGD(population)) ;
  			logger_.info("Spread     : " + indicators.getSpread(population)) ;
  			logger_.info("Epsilon    : " + indicators.getEpsilon(population)) ;  

  			int evaluations = ((Integer)algorithm.getOutputParameter("evaluations")).intValue();
  			logger_.info("Speed      : " + evaluations + " evaluations") ;      
		} 
		*/// if
	} //main
} // NSGAII_main


class TextConsole implements RMainLoopCallbacks
{
    public void rWriteConsole(Rengine re, String text, int oType) {
        System.out.print(text);
    }
    
    public void rBusy(Rengine re, int which) {
        System.out.println("rBusy("+which+")");
    }
    
    public String rReadConsole(Rengine re, String prompt, int addToHistory) {
        System.out.print(prompt);
        try {
            BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
            String s=br.readLine();
            return (s==null||s.length()==0)?s:s+"\n";
        } catch (Exception e) {
            System.out.println("jriReadConsole exception: "+e.getMessage());
        }
        return null;
    }
    
    public void rShowMessage(Rengine re, String message) {
        System.out.println("rShowMessage \""+message+"\"");
    }
	
    public String rChooseFile(Rengine re, int newFile) {
	FileDialog fd = new FileDialog(new Frame(), (newFile==0)?"Select a file":"Select a new file", (newFile==0)?FileDialog.LOAD:FileDialog.SAVE);
	fd.show();
	String res=null;
	if (fd.getDirectory()!=null) res=fd.getDirectory();
	if (fd.getFile()!=null) res=(res==null)?fd.getFile():(res+fd.getFile());
	return res;
    }
    
    public void   rFlushConsole (Rengine re) {
    }
	
    public void   rLoadHistory  (Rengine re, String filename) {
    }			
    
    public void   rSaveHistory  (Rengine re, String filename) {
    }			
}