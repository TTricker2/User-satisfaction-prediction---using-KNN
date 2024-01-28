import java.io.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {
    
    static int NumOfSamples = 18000;
    static int NumOfTestSamples = 2000;
	static int NumOfAttributes = 6;
	
	//read training files
	 public static TrainRecord[] readTrainCSV(String filename) { 
	        File csv = new File(filename);
	        BufferedReader br = null;
	        String line = "";
	        
			int index = 0;
			TrainRecord[] records = new TrainRecord[NumOfSamples];
			
	        try {
	            br = new BufferedReader(new FileReader(csv));
	            while ((line = br.readLine()) != null) { // readLine()은 파일에서 개행된 한 줄의 데이터를 읽어온다.
	                double[] attributes = new double[NumOfAttributes];
	            	String[] lineArr = line.split(","); // 파일의 한 줄을 ,로 나누어 배열에 저장 후 리스트로 변환한다.
	            	
	                for (int i=0; i<NumOfAttributes; i++) {
	                	attributes[i] = Integer.parseInt(lineArr[i]);
	                }
	                
	                int classLabel;
	                String c = lineArr[6]; //class name
	    			if (c.equals("satisfied")) {
	    				classLabel = 1;
	    			}
	    			else { //"unsatisfied"
	    				classLabel = 0;
	    			}
	    			
	    			records[index] = new TrainRecord(attributes, classLabel);
	    			index ++;
	                
	            }
	        } catch (FileNotFoundException e) {
	        } catch (IOException e) {  
	        } finally {
	            try {
	                if (br != null) { 
	                    br.close(); // 사용 후 BufferedReader를 닫아준다.
	                }
	            } catch(IOException e) {             
	            }
	        }
	        
	        
	        
	        return records;
	    }
	 
	 public static TestRecord[] readTestCSV(String filename) { 
	        File csv = new File(filename);
	        BufferedReader br = null;
	        String line = "";
	        
			int index = 0;
			TestRecord[] records = new TestRecord[NumOfTestSamples];
			
	        try {
	            br = new BufferedReader(new FileReader(csv));
	            while ((line = br.readLine()) != null) { // readLine()은 파일에서 개행된 한 줄의 데이터를 읽어온다.
	                double[] attributes = new double[NumOfAttributes];
	            	String[] lineArr = line.split(","); // 파일의 한 줄을 ,로 나누어 배열에 저장 후 리스트로 변환한다.
	                for (int i=0; i<NumOfAttributes; i++) {
	                	attributes[i] = Integer.parseInt(lineArr[i]);
	                }
	                
	                int classLabel;
	                String c = lineArr[6]; //class name
	    			if (c.equals("satisfied")) {
	    				classLabel = 1;
	    			}
	    			else {
	    				classLabel = 0;
	    			}
	    			
	    			records[index] = new TestRecord(attributes, classLabel);
	    			index ++;
	            }
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                if (br != null) { 
	                    br.close(); // 사용 후 BufferedReader를 닫아준다.
	                }
	            } catch(IOException e) {
	                e.printStackTrace();
	            }
	        }
	        return records;
	    }
	 
	 //classification 결과 저장
	 public static void writeCSV(TestRecord[] testRecords) {
		 String name = "classification\\20176867.csv";
		 File file = new File(name); 
		 BufferedWriter bw = null;
		 
		 
		 try {
			 bw = new BufferedWriter(new FileWriter(file, true));
			 
			 for(int i=0; i<2000; i++) {
				 TestRecord tr = testRecords[i];
				 if (tr.predictedLabel == 1)
					 bw.write("Satisfied");
				 else {
					 bw.write("Unsatisfied");
				 }
				 bw.newLine();
		}
			 
			 
			 
		 } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                if (bw != null) {
	                    bw.flush(); // 남아있는 데이터까지 보내 준다
	                    bw.close(); // 사용한 BufferedWriter를 닫아 준다
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
		 
	 }
	 
}