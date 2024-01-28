/*
 * 20176867 ������
 * �����ν� ���б� ������Ʈ k-NN �˰��� ����
 */

import java.io.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

//���� Ŭ����
public class knn {
	static double[] accuracyList = new double[15]; //1���� �׽�Ʈ�� ���� 15���� k�� �����ؼ� ��Ȯ�� ����
	static double[] executionTime = new double[15]; 
	static double[][] performance = new double[10][3]; //�� �׽�Ʈ�� ����k�� ���� ��Ȯ�� ����
	static int kNum = 15; //15���� k��� 1~30������ Ȧ��
	
	public static void main(String[] args) throws IOException {	
		PrintWriter pw = new PrintWriter("classification\\result.txt"); //���Ͽ� ��� ����
		File file = new File("classification\\20176867.csv");
		if(file.exists()) {
			file.delete();
		}
		
		int index=0;
		//knn�˰��� ����
		for(int i=0; i<10; i++) {
			int idx=0;
			for(int k=1; k<kNum*2; k=k+2) {
				
				System.out.println("traing and testing"+(i+1)+" start! K="+k);
				pw.println("traing and testing"+(i+1)+" start! K="+k);
				final long startTime = System.currentTimeMillis();
				double accuracy = knn_result("classification\\training"+Integer.toString(i+1)+".csv" ,"classification\\testing" + Integer.toString(i+1)+".csv", k); //�˰��� ����
				final long endTime = System.currentTimeMillis();
				
				double execTime = (endTime-startTime)/(double)1000;
				executionTime[idx] = execTime; //1~29���� k�� ���� ����ð� ����
				accuracyList[idx] = accuracy; //1~29���� k�� ���� ��Ȯ�� ����
				idx++;
				//System.out.println("Total excution time: "+execTime+" sec\n");
				pw.println("The accuracy is "+accuracy+"%");
				pw.println("Total excution time: "+execTime+" sec\n");
			}
			
			// ������ k�� ã��
			int bestK=0;
			
			for(int j=0; j<kNum; j++) {
				if(Double.compare(accuracyList[j], accuracyList[j+1])>0) { // k�� �÷ȴµ� ��Ȯ���� ������ ���
					bestK = j*2+1;
//					System.out.println("bestK: "+bestK);
//					System.out.println("accuracy: "+accuracyList[j]+"%");
//					System.out.println("execTime: "+executionTime[j]+"sec\n\n");
					pw.println("bestK: "+bestK);
					pw.println("accuracy: "+accuracyList[j]+"%");
					pw.println("execTime: "+executionTime[j]+"sec\n\n");
					
					performance[index][0] = bestK;
					performance[index][1] = accuracyList[j];
					performance[index][2] = executionTime[j];
					
					knn_store("classification\\training"+Integer.toString(i+1)+".csv" ,"classification\\testing" + Integer.toString(i+1)+".csv", bestK);
					index++;
					
							
					break;
				}
			}
		}
		;
		
		// ������� ����
		for(int num=0; num<10; num++) {
			System.out.println("------------------------------------");
			System.out.println("For Training and Testing"+(num+1)+"");
			System.out.println("The bestK = "+performance[num][0]);
			System.out.println("Accuracy = "+performance[num][1]+"%");
			System.out.println("Execution Time = " + performance[num][2] + "sec");
			System.out.println("------------------------------------");
			
			pw.println("------------------------------------");
			pw.println("For Training and Testing"+(num+1)+"");
			pw.println("The bestK = "+performance[num][0]);
			pw.println("Accuracy = "+performance[num][1]+"%");
			pw.println("Execution Time = " + performance[num][2] + "sec");
			pw.println("------------------------------------");
		}
		
		System.out.println();
		pw.close();
	
	}
	
	
	public static double calcDistance(Record training, Record test) {
		int numOfAttributes = training.attributes.length;
		double dist = 0;
		
		for(int i = 0; i < numOfAttributes; i ++){
			dist += Math.pow(training.attributes[i] - test.attributes[i], 2);
		}
		
		return Math.sqrt(dist);
	}
	
	
	public static double knn_result(String trainingFile, String testFile, int K){

		//read trainingSet and testingSet
		TrainRecord[] trainingSet =  FileManager.readTrainCSV(trainingFile);
		TestRecord[] testingSet =  FileManager.readTestCSV(testFile);
		
		//z-score-normalize trainingSet
		double[] avg = new double[6];
		double[] std = new double[6];
		for (int i=0; i<FileManager.NumOfAttributes; i++) {
			long total=0;
			for (int j=0; j<FileManager.NumOfSamples; j++) {
				total += trainingSet[j].attributes[i];
			}
			avg[i] = total / FileManager.NumOfSamples; //���
			
			double temp=0;
			for (int j=0; j<FileManager.NumOfSamples; j++) {
				temp += Math.pow((trainingSet[j].attributes[i] - avg[i]),2);
			}
			
			double var = (double)temp / FileManager.NumOfSamples; //�л�
			std[i] = Math.sqrt(var); //ǥ������
			
			//z-scoring
			for(int j=0; j<FileManager.NumOfSamples; j++) {
				trainingSet[j].attributes[i] = (trainingSet[j].attributes[i] - avg[i]) / std[i];
			}
			
		}
		
		//z-score-normalize testingSet (����ȭ)
		double[] avg2 = new double[6];
		double[] std2 = new double[6];
		for (int i=0; i<FileManager.NumOfAttributes; i++) {
			long total=0;
			for (int j=0; j<FileManager.NumOfTestSamples; j++) {
				total += testingSet[j].attributes[i];
			}
			avg2[i] = total / FileManager.NumOfTestSamples; //���
			
			double temp=0;
			for (int j=0; j<FileManager.NumOfTestSamples; j++) {
				temp += Math.pow((testingSet[j].attributes[i] - avg2[i]),2);
			}
			
			double var = (double)temp / 2000; //�л�
			std2[i] = Math.sqrt(var); //ǥ������
			
			//z-scoring
			for(int j=0; j<2000; j++) {
				testingSet[j].attributes[i] = (testingSet[j].attributes[i] - avg2[i]) / std2[i];
			}
			
		}
		
		
		// �ϳ��� test data �׽���
		int numOfTestingRecord = testingSet.length;
		
		for(int i = 0; i < numOfTestingRecord; i ++){
			TrainRecord[] neighbors = findKNearestNeighbors(trainingSet, testingSet[i], K);
			int classLabel = classify(neighbors);
			testingSet[i].predictedLabel = classLabel; //assign the predicted label to TestRecord
		}
		
		//calculate the accuracy
		int correctPrediction = 0;
		for(int j = 0; j < numOfTestingRecord; j ++){
			if(testingSet[j].predictedLabel == testingSet[j].classLabel)
				correctPrediction ++;
		}
		
		double accuracy = ((double)correctPrediction / numOfTestingRecord)*100;
		//System.out.println("The accuracy is "+accuracy+"%");
			
		
		return accuracy;
		
	}
	
	//�����ε� -> ������ k�� ���ؼ� ��� ���� �뵵
	public static void knn_store(String trainingFile, String testFile, int k){

		//read trainingSet and testingSet
		TrainRecord[] trainingSet =  FileManager.readTrainCSV(trainingFile);
		TestRecord[] testingSet =  FileManager.readTestCSV(testFile);
		
		//z-score-normalize trainingSet
		double[] avg = new double[6];
		double[] std = new double[6];
		for (int i=0; i<FileManager.NumOfAttributes; i++) {
			long total=0;
			for (int j=0; j<FileManager.NumOfSamples; j++) {
				total += trainingSet[j].attributes[i];
			}
			avg[i] = total / FileManager.NumOfSamples; //���
			
			double temp=0;
			for (int j=0; j<FileManager.NumOfSamples; j++) {
				temp += Math.pow((trainingSet[j].attributes[i] - avg[i]),2);
			}
			
			double var = (double)temp / FileManager.NumOfSamples; //�л�
			std[i] = Math.sqrt(var); //ǥ������
			
			//z-scoring
			for(int j=0; j<FileManager.NumOfSamples; j++) {
				trainingSet[j].attributes[i] = (trainingSet[j].attributes[i] - avg[i]) / std[i];
			}
			
		}
		
		//z-score-normalize testingSet (����ȭ)
		double[] avg2 = new double[6];
		double[] std2 = new double[6];
		for (int i=0; i<FileManager.NumOfAttributes; i++) {
			long total=0;
			for (int j=0; j<FileManager.NumOfTestSamples; j++) {
				total += testingSet[j].attributes[i];
			}
			avg2[i] = total / FileManager.NumOfTestSamples; //���
			
			double temp=0;
			for (int j=0; j<FileManager.NumOfTestSamples; j++) {
				temp += Math.pow((testingSet[j].attributes[i] - avg2[i]),2);
			}
			
			double var = (double)temp / 2000; //�л�
			std2[i] = Math.sqrt(var); //ǥ������
			
			//z-scoring
			for(int j=0; j<2000; j++) {
				testingSet[j].attributes[i] = (testingSet[j].attributes[i] - avg2[i]) / std2[i];
			}
			
		}
		
		// �ϳ��� test data �׽���
		int numOfTestingRecord = testingSet.length;
		
		for(int i = 0; i < numOfTestingRecord; i ++){
			TrainRecord[] neighbors = findKNearestNeighbors(trainingSet, testingSet[i], k);
			int classLabel = classify(neighbors);
			testingSet[i].predictedLabel = classLabel; //assign the predicted label to TestRecord
		}
		
		
		FileManager.writeCSV(testingSet);
			
			
	}
	
	// test�����Ϳ� ���� k-nearest neighbor���
	static TrainRecord[] findKNearestNeighbors(TrainRecord[] trainingSet, TestRecord testRecord,int K){
		int NumOfTrainingSet = trainingSet.length;
		TrainRecord[] neighbors = new TrainRecord[K];
		
		//�ʱ�ȭ
		int index;
		for(index = 0; index < K; index++){
			trainingSet[index].distance = calcDistance(trainingSet[index], testRecord);
			neighbors[index] = trainingSet[index];
		}
		
		
		//������ �����Ϳ� ���ؼ� k�̿� ���ϱ�
		for(index = K; index < NumOfTrainingSet; index ++){
			trainingSet[index].distance = calcDistance(trainingSet[index], testRecord);
			// �迭���� distance�� ���� ū �ε��� ���ϱ�
			int maxIndex = 0;
			for(int i = 1; i < K; i ++){
				if(neighbors[i].distance > neighbors[maxIndex].distance)
					maxIndex = i;
			}
			// ���� ����� �Ÿ���, �迭������ ���� ū distance�� �� -> �̿� ������Ʈ
			if(neighbors[maxIndex].distance > trainingSet[index].distance)
				neighbors[maxIndex] = trainingSet[index];
		}
		
		return neighbors;
	}
	
	// neighbor����ؼ� class ���ϱ�
	static int classify(TrainRecord[] neighbors){
		HashMap<Integer, Double> map = new HashMap<Integer, Double>();
		int num = neighbors.length;		
		for(int idx = 0; idx < num; idx ++){
			TrainRecord temp = neighbors[idx];
			int key = temp.classLabel;
		
			if(!map.containsKey(key)) {
				map.put(key, 1 / temp.distance);
			}
			else{
				double value = map.get(key);
				value += 1 / temp.distance;
				map.put(key, value);
			}
		}	
		
		// �� �� ����� Ŭ���� ����
		double maxSimilarity = 0;
		int returnLabel = -1;
		Set<Integer> labelSet = map.keySet();
		Iterator<Integer> it = labelSet.iterator();
		
		//�ؽø��� ��ȸ -> ���� weight�� ū key�� class�� ��.
		while(it.hasNext()){
			int label = it.next();
			double value = map.get(label);
			if(value > maxSimilarity){
				maxSimilarity = value;
				returnLabel = label;
			}
		}
		
		return returnLabel;
	}
	

}
