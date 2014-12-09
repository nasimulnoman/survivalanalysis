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
		
		int numberOfBits = 40;
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

			
			String dataFileName = "basalSamplesRef33_MSTkNNDefault-MSTkNNk=1.arff"; //"basalSamplesRef33_ClinicalData.nbi.final_389_25.arff";

			GenerateSurvivalGraph problem = new GenerateSurvivalGraph("Binary",numberOfBits, dataFileName, re);
		
			Binary[] vars =null;
			//String solnString = "1000110011100001100010100100001101101000";
			String solnString = "001000111001010100100010011100110001010001100000011110010100100111101001000101010001001001011011000001000011101101010100010000000001001001100001110001101111001011010100000000011101000010001100010011110001110000101101111011001010111101011110010";
			Solution solution = new Solution(problem);
			
			vars= new Binary[1];
			vars[0] = new Binary(solnString.length());
			for (int i=0; i<solnString.length();++i){
				if(solnString.charAt(i)=='1'){
					vars[0].bits_.set(i, true);
				}
				else if(solnString.charAt(i)=='0'){
					vars[0].bits_.set(i, false);
				}
				else{
					System.err.println("Invalid character in solution string.");
					System.exit(1);
				}
			}
			
			solution.setDecisionVariables(vars);
			
			problem.evaluate(solution);
			
			
		
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
