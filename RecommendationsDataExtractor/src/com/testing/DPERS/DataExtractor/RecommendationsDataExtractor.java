package com.testing.DPERS.DataExtractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.io.*;


public class RecommendationsDataExtractor {
	public static File recommendationsDirectory = new File("F:\\Recommendations");
	public static File resultsDirectory = new File("F:\\RecommendationsAnalysis");
	public static String OutputFileHeader = "Query No, FileName, TotalNumberofRelatedDocs, NoofRelatedAt1, NoofRelatedAt2, NoofRelatedAt3, NoofRelatedAt4,NoofRelatedAt5\n"; 
	public static String[] origCategories = {"Puzzle and Card", "Puzzle and Card", "Board", "Not a Game", "Action"};
	public static int numRecommendationstoGet = 5;
	
	public static void main(String[] args) throws IOException {
		
		generateAnalysisFiles();
		
		
	}
	
	
	public static void generateAnalysisFiles() throws IOException {
//		
		File[] allRecommendationFiles = recommendationsDirectory.listFiles();
		Arrays.sort(allRecommendationFiles);
		
		int prevprojectID = 0;		
		int newprojectID = 1;
		int queryNo = 1;
		
		String dataExtracted = "";
		File file = new File(recommendationsDirectory.getPath()+"//"+Integer.toString(newprojectID)+".txt");
		FileWriter fw = new FileWriter(file.getAbsolutePath());
		BufferedWriter bw = new BufferedWriter(fw);
		for (File recommendationFile : allRecommendationFiles) {
			
			newprojectID = extractProjectIDfromFile(recommendationFile.getName());
			if (newprojectID != prevprojectID) {
				bw.close();				
				//create new File
				prevprojectID = newprojectID;
				file = new File(resultsDirectory.getPath()+"//"+Integer.toString(newprojectID)+".txt");
				if (!file.exists()) {
					file.createNewFile();
				}
				fw = new FileWriter(file.getAbsolutePath());
				bw = new BufferedWriter(fw);
				bw.write(OutputFileHeader);
				queryNo = 1;
				
			}
			// get all fields for that file and write to file;
			dataExtracted = extractDataFromFile(recommendationFile, newprojectID);
			dataExtracted = Integer.toString(queryNo) + "," + recommendationFile.getName() + "," + dataExtracted + "\n";// add query number and file name here
			bw.write(dataExtracted);
			dataExtracted = "";
			queryNo+=1;		

		}
		bw.close();
	}

	public static String extractDataFromFile(File recommendationFile, int projectID) throws IOException {
		String datatoReturn = "";
		String realCategory = origCategories[projectID - 1];
		int[] relatedAt = new int[numRecommendationstoGet];
		ArrayList<String> recommendedCategories = new ArrayList<String>();
		// get categories of top 5 matching projects
		BufferedReader br = new BufferedReader (new FileReader(recommendationFile));
		int numberofRecommendationsGot = 0;
		String line = "";
		while (numberofRecommendationsGot < numRecommendationstoGet) {
			line = br.readLine();
			line = line.trim();
			if (line.startsWith("Recommended project's category is:")) {
				 recommendedCategories.add((line.split(": "))[1]);
				 numberofRecommendationsGot+=1;
			}
		}
		int totalNumberofRelatedRecs = Collections.frequency(recommendedCategories, realCategory);
		datatoReturn = datatoReturn + Integer.toString(totalNumberofRelatedRecs) + ",";
		int index = 0;
		int relevantCount = 0;
		for (String recommendation : recommendedCategories) {
			if (recommendation.equals(realCategory)) {
				relevantCount = relevantCount+1;
				relatedAt[index] = relevantCount;
						
			}else{
				relatedAt[index] = 0;
			}
			index = index+1;
		}
		for (int i =0; i< relatedAt.length; i++) {
			datatoReturn = datatoReturn + relatedAt[i] + ",";
		}
		br.close();
		// TODO Auto-generated method stub
		return datatoReturn;
	}

	public static int extractProjectIDfromFile(String fileName) {
		return Integer.parseInt(fileName.split("_")[0]);
	}

	
}
