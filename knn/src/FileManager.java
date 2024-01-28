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
	            while ((line = br.readLine()) != null) { // readLine()�� ���Ͽ��� ����� �� ���� �����͸� �о�´�.
	                double[] attributes = new double[NumOfAttributes];
	            	String[] lineArr = line.split(","); // ������ �� ���� ,�� ������ �迭�� ���� �� ����Ʈ�� ��ȯ�Ѵ�.
	            	
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
	                    br.close(); // ��� �� BufferedReader�� �ݾ��ش�.
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
	            while ((line = br.readLine()) != null) { // readLine()�� ���Ͽ��� ����� �� ���� �����͸� �о�´�.
	                double[] attributes = new double[NumOfAttributes];
	            	String[] lineArr = line.split(","); // ������ �� ���� ,�� ������ �迭�� ���� �� ����Ʈ�� ��ȯ�Ѵ�.
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
	                    br.close(); // ��� �� BufferedReader�� �ݾ��ش�.
	                }
	            } catch(IOException e) {
	                e.printStackTrace();
	            }
	        }
	        return records;
	    }
	 
	 //classification ��� ����
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
	                    bw.flush(); // �����ִ� �����ͱ��� ���� �ش�
	                    bw.close(); // ����� BufferedWriter�� �ݾ� �ش�
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
		 
	 }
	 
}