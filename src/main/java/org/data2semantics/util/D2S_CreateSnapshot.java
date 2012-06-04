package org.data2semantics.util;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


/**
 * This is the class responsible for getting local snapshot/files from original html sources
 * Assumes an existing source.txt files containing <local name> <remote url> on every line
 * @author wibisono
 *
 */
public class D2S_CreateSnapshot {
		
		// List of local files and urls from which snapshot will be created
		Vector<String[]> fileURLList = new Vector<String[]>();

		static final String SNAPSHOT_DIRECTORY="results/snapshots";
		
		/**
		 * 
		 * @param sourcePath the path to source files containing space/tab separated local snapshot file + url
		 */
		public D2S_CreateSnapshot(String sourcePath) {
			File sourceFile = new File(sourcePath);
			initializeFileURLList(sourceFile);
			
		}

		/**
		 * Read from source file, split file and source URL into fileURLList
		 * @param sourceFile
		 */
		private void initializeFileURLList(File sourceFile) {
			try {
				Scanner scanner = new Scanner(sourceFile);
				while(scanner.hasNextLine()){
					String [] fileURL = scanner.nextLine().split(" ");
					fileURLList.add(fileURL);
				}
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			}
		}
		
		public List<String[]> getFileList(){
			return fileURLList;
		}
		
		/**
		 * Generating snapshot using apache commons-io copyURLToFile
		 */
		public void generateSnapshot(){
			File resultDir =new File(SNAPSHOT_DIRECTORY);
			if(!resultDir.exists()){
				resultDir.mkdirs();
			}
			for(String[] fileURL : fileURLList){
				
				try {
				
					File localFile = new File(resultDir,fileURL[0]);
					URL  sourceURL = new URL(fileURL[1]);
					FileUtils.copyURLToFile(sourceURL, localFile);
				
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
	
			}
		}
		
		/**
		 * Source file argument actually provided by maven pom, profile.
		 * It will actually be the file on src/main/resources/sources.txt
		 * Leave it as parameter here in case we wanted to change, just change the pom.
		 * @param args
		 */
		public static void main(String[] args) {
			if(args.length < 1){
				System.out.println("Please supplly the path to source file that you wanted to process");
				return;
			}
			
			String fileName = args[0];
			D2S_CreateSnapshot snapshot= new D2S_CreateSnapshot(fileName);
			snapshot.generateSnapshot();
			
		}
		
}
