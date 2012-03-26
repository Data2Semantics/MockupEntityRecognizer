package org.data2semantics.recognize;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.junit.Test;

public class BioPortalClientTest {
	
	
	public static final String SAMPLE_DIR = "sample-xml";
	
	@Test
	public void testElsevierXMLText() throws IOException{
		String sampleFilePath =getClass().getClassLoader().getResource(SAMPLE_DIR+"/content1.xml").getFile(); 
		File xmlSample1 = new File(sampleFilePath);
		StringBuilder stringBuilder = new StringBuilder();
		Scanner fileScanner = new Scanner(new FileInputStream(xmlSample1));
		D2S_BioportalClient client  = new D2S_BioportalClient();
		FileWriter writer = new FileWriter("output1.xml");
			
		try {
			//Refactor only to pick the body of the xml, but for now, everything is passed just to see.
			while(fileScanner.hasNextLine()){
				stringBuilder.append(fileScanner.nextLine() + "\n");
			}
			String textToAnnotate =stringBuilder.toString(); 
			String annotationResult = client.annotateText(textToAnnotate);	
			writer.write(annotationResult);
		} finally{
			fileScanner.close();
			writer.close();
		}
	}
}
