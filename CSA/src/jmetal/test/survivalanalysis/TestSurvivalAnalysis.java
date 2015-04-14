package jmetal.test.survivalanalysis;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

import jmetal.problems.SurvivalAnalysis;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.variable.Binary;

public class TestSurvivalAnalysis {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//String dataFileName = "basalSamplesRef33_MSTkNNDefault-MSTkNNk=1.arff"; //"basalSamplesRef33_ClinicalData.nbi.final_389_25.arff";
		//String dataFileName = "filteredDataMSTkNNk=2.arff";
		String dataFileName = "GBM-1740x196-Min-ADJC.arff";//"GBM-1740x196 -Max.arff";//"GBM-1740x194-Min-L1S2-G2.arff"; //"GBM-1740x194-Max-L1S3-G2.arff";//"1740x168.NA.filtered.Soln09.G2.arff";//"1740x168.NA.filtered.Soln09.G2.arff";//"1740X38-Soln10-Soln05-Soln02-Soln17-G10.arff";//"1750X55-Soln10-Soln05-Soln10-G8.arff";//"1750X66-Soln10-Soln05-Soln10-G7.arff";//"1740X46-Sol10-Sol05-Sol02-G6.arff";//"1740X48-Soln10-Soln05-G3.arff";//"1740X48-Soln10-Soln05-G4.arff";//"1740X27-Soln10-G1.arff";//"1740x196.NA.filtered.G2.arff";//"GBM-1740x196.arff";;//"GBM-Proneural-210x52.arff";//"GBM-Proneural1740x56.arff";//"_BC1000MostVar.arff";//"_BC500mostVar.arff"; //"_BC3-ff.arff";
		String testDataFileName = null;//"GBM-Proneural-210x52-class.arff";//"_BC1000MostVar_class.arff";//"_BC500mostVar_class.arff"; //"_BC3-ff_class.arff";
		//String solnString = "1000110011100001100010100100001101101000";
		//String solnString = "001000111001010100100010011100110001010001100000011110010100100111101001000101010001001001011011000001000011101101010100010000000001001001100001110001101111001011010100000000011101000010001100010011110001110000101101111011001010111101011110010";
		//String solnString = "101110000000011101110100000000010100011000001100010100000010101101101001000010100111101111010010001100001000001000001101010000001100010110101010010000100011010001110100110000011000101000010110001000101001001000100111000110100100000011101001101";
		//String solnString = "0010000101110100110100101100000010111111100100100011111001010001101111001100101100110100111111001100100001011100011011010011001010111101100101111010010010000100111110101110001001010000000110010011111000110101000000000100100110001010001010111010100001000011110100011111110011001101000101000000100101111011110111111101010010001001010100111000110001110100101010000111011010011111001001000100001111010000110000111101110000000111101011101010011101010100110010001011100001010001100001010111100001000100011110010000011001111100010011011110001110001000111011010001011100110111010010010010010110000101010110110010111000111000011000000100010111101010001011101101000001011010001101111001010110111000000111111001100111001111101010110010001001100000100010101110000001000111100100111100000010000010011011001011101110011101000110101100000101000110100111100101101101000010011010010001111101111110110001010001100011000011101010010001111111101111110110111010100000001111001000001010110110000011000001110000111011010011000001100000000111001100100111110010100101011100010110001100011110010001011110000001100000111101000110000000011001000101101000000000001100010000100010000011111000010010101011100100001000110000010000100101110001110101000000111110010000010100110111111000100111010001000100001100010101011111001010001010010100010101000100110101001110111101111010001011101010101011100001110110100111001000000011100100000110011111110101100010110010100010000001000100010101111000011110010011101010100110011111111000011110011010010100011000011001100011000100000111101010110011001110011010000001000001100011001111110001001011101001111101000101000101000110101111100010100000000110110101111001110101110011111101111010110001101010100000111001010110000010011110011011101111101110001101100101100001010010001110001001011101000001110001101001010000111110010011011001011100111011000010010101011001010110011000010000000010111100101100110000000000111111100100111001111110010100100000010110100001110111001011011110000110000101000101111001110100110000011110010100111110110001010011110101000110110010011100110001000110110100000000100000001110000010101010101100011010100001111000110101010111101101110100111010100001101101010101100111000110101001000110010011010100001111010111110010110110010111111110110011011110001000011000000011100011110000100100111001110001010011111111110111110001010100101111110111110111111101001000100000101000110100101111101101000010001111111100011110111100101010100111100001011000111100110110001100001000101001101101101111001100011000100000100010110011010011101111110001000110100100011010111010110010111011101000000101000010011100110111111100110101101100001011001100111001011000100011010010101101110001100000000110001100110100110011101011001000110111111101001010001111101000001010011001100101100010101111000000010110010111100011100100110001100001100010111111010011000110101000000101100110110100000101000001010100110101100000000010010101111101111010101100001001100100010101101101111000011010111111100010100011101000100111111110100000010100101000100000";
          // Results for Van de Vijver Data
		//String solnString = "10111110101100111101111111001111000111000100011100110100101111111010111010110011011111111010110110";
		
		String[] solnStrings = {
				"000000000000000001000000000000011000001010000000000000000000000000000100000100000010000000001000000010000000000000001000000000001000010000000000000000010001000010110000000000000000000010000000000000001000000100010000001000100000010001001000100000000010000000000000000000000000000100000000001000010010000000000000000000100001000000000000000000000000000000000000000000000000000001000000100010000000000000000000000000001000000010000010000000000000000000000000001000000000000100100010000000000000010000000000000000000000000000000000000000000000001000000000000000000010000000000000110001000000000000000000000000000000100000000000010000000000000000000000000110100000000000000000000000000000001000000001000010000100000000000000000000101000000000000000100000000000010000001000000000000000000000000000000000000000000000000000001000001000000000000000000000000001000000000000000001000000000100000100000000000010000000000000000000001000100000000000000000000100100010010000000000000000000000000001000010000010000000000000000000000001000000000000000000001100000000000000001000000000100000000000000000100001000000000000000100000000000000000000100000000000000000000100000000000100000000010000000010000000000000001000100000000000000000001000000010100000010000000000000100100000000000010000101000000000000010000001001000110000000000000000000100100001000000000000010000010010000010001000000010000000000000000000000001000000000000000000000000000000000100000000000000000000100000000000010010000000000000000010000010000000000000000000001000000001000000010000000000010010001000000001000000000000000000000000000000000000100000000000100000001000000000000000000000100000000000000000100000000000000000000000000001000000000010000000000000000000000000100000100000000000",
				"001000100000000000000000000000000000000011000000000000000001000001000000000001100000011000000000000000100010001000010010010000000000000000100000000100000100001100000010000001000100001000000001010010000100000000011000000000000000000000001000000001010100010100000001000000000000000001101000100000000010000010000000011000000000000000100100001000001000000000000000000000011000000001000000000000000000000000000000000000010000000000000000010000000000000100000101000000000000000000000000000000010001000000000000000000000000100000000010010000100000000000100000001000000000001000000010000000000000000010010000000000010000100010000000000000000000000000000000000000000000000000000000000000000000000000000001000100100000000001000010010000000000000000010001000001000000000000000101000000000000001000000000000000000000000000000000001000100000000000010001010000010000000000000000001000000000000000000001100000001000000000000001000000000010000000000000000000000000000000000001000000000010101000010001000001000000000001000000101010000000000000000000010000001000000000100000100010010000011000100000000000010000001000001001000100000001000000000001000000000000000000000000000000001000000100000000000000011000000000000000000000000000000000000000000000000000000000000000000000100100010000000000001000000000000000101000000000000000010000000100000010000010000010000001000000000000010000010000000000100001000001000000000000100000011000001000000000100100000000000000010000010000000000000000000000000010000000000000010000100000100000000000000000110000000000000000000000010000010000000000000011000000100100000000010000100110000000000001000000010001010000000000000000010000000000000000000000000010000000001000000000010001000000010000000000000000000010001000000001000000", 
				"000100101000000000000100101000000000000100000000000000000000001000100000001000000000000000000000010100010000000000100000000000010000000010010000000000010011000011010001001010000000000000000000000000000000000000000100000100000000000000000010000000000000000000001000001000100000000000000000000000011001000000000110000000010000000000100000100001001100000010000000100000000000001000000000000000011000001001000000001000000000010000000000000000000000100100000001000000000000000110000000000000000000000000000100001000000000000110010000000000000000000000010010000000000000000000000000001001000100000001000000000010000100100001000000000100000000000100000010000000000000000110000000000010000000000000100000000000001000000000000001000000001000100001010000000000100000000000000100100010000000000000000000000001000000000100000000000000000000010100000100000000000001000000010000000000100000000000000000001000000000001100000000010001001000000000000000000000001000000000000000000000000010000000001001000000000010000001000000100000001001000010000000000010000000011010010000001000100000001000001100000000000000000000000000000000000000000000100000000000001000000000000001000000000000000000000000000000010000000000000000000000000000000000001000000000000001000000000000110000000000000100000000000000000000000000000001000000001010100000000000010001000000100100100000000000000000000000001000001001000100000000000010000000000100001010000010000000000010000001000000000000000001000000000010100000000000100000000000000001000000000010000000100010000000000000000000000000000000000000000000010010000000100000000000100000000010000100000000000000000000010000000000000000000010100001000000000000000000000000000100000000000001000000000000000000001000000000000000000000110000", 
				"000000000000000000000100100010000000100000000000000000000101000001000000000000000100000010000000101000010000001000000000001000000100000000000000100000000000100000100010000000100000000000000010000000000000001000010000000000000000000000000000000001000010100000000000010100000000000010000000000000000000000000000000100000000000000001000000000000000001011100000000000000000001001000000000001100101000000000000010100000000000000000000000000000000000000000000000010010000000000001000000000000001001010000000000000000011000000000001000000000100000000000000000000000000010000000000100000000001000100000000000000000000000001000000000100000000000000000000000000000000000000000000000000110000000000100000000000001000000000000000000000000000101000000000000000000000001110000010010000000000000000000000000000000000000010000000010001000000000000100100000000101100000000100000000000000010000000000000000000000001000100000000000000010000000000000001000000000000000000001000000000000000000000000000000010100001100000101001000000010000000000010000001000000000000000000000000000000000000000000000000001000000000000000000000000000000000100000000000000000000000000000100000100000000000000000000000000000000000000000000000000000000000000001000100000000000010000000000000000000000000000000000001000000000000000000000000000000000000000000000000010000100001000000000000000000010000000000000000000000010000000000100100000000000000010000000010000101100000000000000001000000000000010000000000000001000000000010000000000010000000000000000000000100000000000000000000001010000000000000000000000000000000100000000000000000010000000000000000000000000000001000000000000011000000110001001011000100000000100000000000000100000000000000010000000000000000000000000000001000000000" 
				
		};		
		int numberOfBits = solnStrings[0].length();
		try{
			
			
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
		    
			//problem = new SurvivalAnalysis("Binary",numberOfBits,"basalSamplesRef33_169.arff", re, pValue, featureMaximization);

			


			GenerateSurvivalGraph problem = new GenerateSurvivalGraph("Binary",numberOfBits, dataFileName, testDataFileName, re);
		
			Binary[] vars =null;
			Solution solution = new Solution(problem);
			
			for (int ind=0; ind<solnStrings.length; ++ind){

				vars= new Binary[1];
				vars[0] = new Binary(solnStrings[ind].length());
				for (int i=0; i<solnStrings[ind].length();++i){
					if(solnStrings[ind].charAt(i)=='1'){
						vars[0].bits_.set(i, true);
					}
					else if(solnStrings[ind].charAt(i)=='0'){
						vars[0].bits_.set(i, false);
					}
					else{
						System.err.println("Invalid character in solution string.");
						System.exit(1);
					}
				}

				solution.setDecisionVariables(vars);
				problem.SolutionID = ind+1;
				problem.evaluate(solution);
			}
			
			
		
		}
		catch(Exception e){
			System.err.println("Error in problem / solution creation.");
			e.printStackTrace();
		}

	}

}



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