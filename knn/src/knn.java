/*
 * 20176867 전현욱
 * 패턴인식 봄학기 프로젝트 k-NN 알고리즘 구현
 */

import java.io.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

//메인 클래스
public class knn {
	static double[] accuracyList = new double[15]; //1개의 테스트에 대해 15개의 k를 실험해서 정확도 저장
	static double[] executionTime = new double[15]; 
	static double[][] performance = new double[10][3]; //각 테스트의 최적k일 때의 정확도 저장
	static int kNum = 15; //15개의 k사용 1~30사이의 홀수
	
	public static void main(String[] args) throws IOException {	
		PrintWriter pw = new PrintWriter("classification\\result.txt"); //파일에 결과 저장
		File file = new File("classification\\20176867.csv");
		if(file.exists()) {
			file.delete();
		}
		
		int index=0;
		//knn알고리즘 수행
		for(int i=0; i<10; i++) {
			int idx=0;
			for(int k=1; k<kNum*2; k=k+2) {
				
				System.out.println("traing and testing"+(i+1)+" start! K="+k);
				pw.println("traing and testing"+(i+1)+" start! K="+k);
				final long startTime = System.currentTimeMillis();
				double accuracy = knn_result("classification\\training"+Integer.toString(i+1)+".csv" ,"classification\\testing" + Integer.toString(i+1)+".csv", k); //알고리즘 수행
				final long endTime = System.currentTimeMillis();
				
				double execTime = (endTime-startTime)/(double)1000;
				executionTime[idx] = execTime; //1~29까지 k에 대한 실행시간 저장
				accuracyList[idx] = accuracy; //1~29까지 k에 대한 정확도 저장
				idx++;
				//System.out.println("Total excution time: "+execTime+" sec\n");
				pw.println("The accuracy is "+accuracy+"%");
				pw.println("Total excution time: "+execTime+" sec\n");
			}
			
			// 최적의 k값 찾기
			int bestK=0;
			
			for(int j=0; j<kNum; j++) {
				if(Double.compare(accuracyList[j], accuracyList[j+1])>0) { // k를 늘렸는데 정확도가 떨어진 경우
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
		
		// 최종결과 도출
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
			avg[i] = total / FileManager.NumOfSamples; //평균
			
			double temp=0;
			for (int j=0; j<FileManager.NumOfSamples; j++) {
				temp += Math.pow((trainingSet[j].attributes[i] - avg[i]),2);
			}
			
			double var = (double)temp / FileManager.NumOfSamples; //분산
			std[i] = Math.sqrt(var); //표준편차
			
			//z-scoring
			for(int j=0; j<FileManager.NumOfSamples; j++) {
				trainingSet[j].attributes[i] = (trainingSet[j].attributes[i] - avg[i]) / std[i];
			}
			
		}
		
		//z-score-normalize testingSet (정규화)
		double[] avg2 = new double[6];
		double[] std2 = new double[6];
		for (int i=0; i<FileManager.NumOfAttributes; i++) {
			long total=0;
			for (int j=0; j<FileManager.NumOfTestSamples; j++) {
				total += testingSet[j].attributes[i];
			}
			avg2[i] = total / FileManager.NumOfTestSamples; //평균
			
			double temp=0;
			for (int j=0; j<FileManager.NumOfTestSamples; j++) {
				temp += Math.pow((testingSet[j].attributes[i] - avg2[i]),2);
			}
			
			double var = (double)temp / 2000; //분산
			std2[i] = Math.sqrt(var); //표준편차
			
			//z-scoring
			for(int j=0; j<2000; j++) {
				testingSet[j].attributes[i] = (testingSet[j].attributes[i] - avg2[i]) / std2[i];
			}
			
		}
		
		
		// 하나씩 test data 테스팅
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
	
	//오버로딩 -> 최적의 k에 대해서 결과 저장 용도
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
			avg[i] = total / FileManager.NumOfSamples; //평균
			
			double temp=0;
			for (int j=0; j<FileManager.NumOfSamples; j++) {
				temp += Math.pow((trainingSet[j].attributes[i] - avg[i]),2);
			}
			
			double var = (double)temp / FileManager.NumOfSamples; //분산
			std[i] = Math.sqrt(var); //표준편차
			
			//z-scoring
			for(int j=0; j<FileManager.NumOfSamples; j++) {
				trainingSet[j].attributes[i] = (trainingSet[j].attributes[i] - avg[i]) / std[i];
			}
			
		}
		
		//z-score-normalize testingSet (정규화)
		double[] avg2 = new double[6];
		double[] std2 = new double[6];
		for (int i=0; i<FileManager.NumOfAttributes; i++) {
			long total=0;
			for (int j=0; j<FileManager.NumOfTestSamples; j++) {
				total += testingSet[j].attributes[i];
			}
			avg2[i] = total / FileManager.NumOfTestSamples; //평균
			
			double temp=0;
			for (int j=0; j<FileManager.NumOfTestSamples; j++) {
				temp += Math.pow((testingSet[j].attributes[i] - avg2[i]),2);
			}
			
			double var = (double)temp / 2000; //분산
			std2[i] = Math.sqrt(var); //표준편차
			
			//z-scoring
			for(int j=0; j<2000; j++) {
				testingSet[j].attributes[i] = (testingSet[j].attributes[i] - avg2[i]) / std2[i];
			}
			
		}
		
		// 하나씩 test data 테스팅
		int numOfTestingRecord = testingSet.length;
		
		for(int i = 0; i < numOfTestingRecord; i ++){
			TrainRecord[] neighbors = findKNearestNeighbors(trainingSet, testingSet[i], k);
			int classLabel = classify(neighbors);
			testingSet[i].predictedLabel = classLabel; //assign the predicted label to TestRecord
		}
		
		
		FileManager.writeCSV(testingSet);
			
			
	}
	
	// test데이터에 대해 k-nearest neighbor계산
	static TrainRecord[] findKNearestNeighbors(TrainRecord[] trainingSet, TestRecord testRecord,int K){
		int NumOfTrainingSet = trainingSet.length;
		TrainRecord[] neighbors = new TrainRecord[K];
		
		//초기화
		int index;
		for(index = 0; index < K; index++){
			trainingSet[index].distance = calcDistance(trainingSet[index], testRecord);
			neighbors[index] = trainingSet[index];
		}
		
		
		//나머지 데이터에 대해서 k이웃 구하기
		for(index = K; index < NumOfTrainingSet; index ++){
			trainingSet[index].distance = calcDistance(trainingSet[index], testRecord);
			// 배열에서 distance가 가장 큰 인덱스 구하기
			int maxIndex = 0;
			for(int i = 1; i < K; i ++){
				if(neighbors[i].distance > neighbors[maxIndex].distance)
					maxIndex = i;
			}
			// 현재 계산한 거리와, 배열에서의 가장 큰 distance값 비교 -> 이웃 업데이트
			if(neighbors[maxIndex].distance > trainingSet[index].distance)
				neighbors[maxIndex] = trainingSet[index];
		}
		
		return neighbors;
	}
	
	// neighbor사용해서 class 구하기
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
		
		// 좀 더 가까운 클래스 선택
		double maxSimilarity = 0;
		int returnLabel = -1;
		Set<Integer> labelSet = map.keySet();
		Iterator<Integer> it = labelSet.iterator();
		
		//해시맵을 순회 -> 가장 weight가 큰 key가 class가 됨.
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
