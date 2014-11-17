package jmetal.test.survivalanalysis;

import jmetal.problems.SurvivalAnalysis;
import jmetal.core.Solution;

public class TestSurvivalAnalysis {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int numberOfBits = 40;
		try{
			SurvivalAnalysis problem = new SurvivalAnalysis("Binary",numberOfBits,"basalSamplesRef33_169.arff");
		
			Solution solution = new Solution(problem);
			
			System.out.println("Current Solution: " + solution.getDecisionVariables()[0].toString());
			
			problem.evaluate(solution);
			
			
		
		}
		catch(Exception e){
			System.err.println("Error in problem / solution creation.");
			e.printStackTrace();
		}

	}

}
