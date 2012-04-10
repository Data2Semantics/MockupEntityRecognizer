package org.data2semantics.recognize;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.junit.Test;

public class D2S_BioPortalClientTest {

	// This file in src/test/resources
	public static final String SAMPLE_DIR = "sample-xml";

	// This thing should not be empty, amazingly.
	public static final String PROCESSED_DIR = "processed-xml";

	@Test
	public void testElsevierXMLText() throws IOException, InterruptedException {

		File sampleDir = new File(getClass().getClassLoader()
				.getResource(SAMPLE_DIR).getFile());

		
		File processedDir = new File(getClass().getClassLoader()
				.getResource(PROCESSED_DIR).getFile());
		
		File[] xmlFiles = sampleDir.listFiles();

		int counter = 0;
		D2S_BioportalClient client = new D2S_BioportalClient();
		
		for (File currentXMLFile : xmlFiles) {
			if(!currentXMLFile.getPath().endsWith("xml")) continue;
			
			counter ++;
			String outputFilePath = processedDir.getPath()+"\\output"+counter+".xml";
			
			// IF this file already created, move on.
			if(new File(outputFilePath).exists()) continue;
			
			Scanner fileScanner = new Scanner(new FileInputStream(currentXMLFile));
			
			FileWriter writer = new FileWriter(outputFilePath);
			StringBuilder stringBuilder = new StringBuilder();
			try {
				// Refactor only to pick the body of the xml, but for now,
				// everything is passed just to see.
				while (fileScanner.hasNextLine()) {
					stringBuilder.append(fileScanner.nextLine() + "\n");
				}
				String textToAnnotate = stringBuilder.toString();
				String annotationResult = client.annotateText(textToAnnotate);
				writer.write(annotationResult);
				System.out.println("Done writing output no "+counter);
			} finally {
				fileScanner.close();
				writer.close();
			}
			Thread.sleep(20000);
		}
	}
}
