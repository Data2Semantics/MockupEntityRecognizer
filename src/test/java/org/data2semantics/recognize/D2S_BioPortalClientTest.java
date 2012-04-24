package org.data2semantics.recognize;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class D2S_BioPortalClientTest {

	// This file in src/test/resources
	public static final String SAMPLE_DIR = "sample-xml";

	// This thing should not be empty, amazingly.
	public static final String PROCESSED_DIR = "processed-xml";
	
	
	File sampleDir = new File(getClass().getClassLoader()
			.getResource(SAMPLE_DIR).getFile());

	
	File processedDir = new File(getClass().getClassLoader()
			.getResource(PROCESSED_DIR).getFile());
	//@Test
	public void testElsevierXMLText() throws IOException, InterruptedException {


		
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
				// everything is passed  just to see.
				while (fileScanner.hasNextLine()) {
					stringBuilder.append(fileScanner.nextLine() + "\n");
				}
				String textToAnnotate = stringBuilder.toString();
				String annotationResult = client.annotateText(textToAnnotate,"xml");
				writer.write(annotationResult);
				System.out.println("Done writing output no "+counter);
			} finally {
				fileScanner.close();
				writer.close();
			}
			Thread.sleep(20000);
		}
	}
	@Test
	public void parseXMLUsingDOMTest() throws SAXException, IOException, ParserConfigurationException, InterruptedException{
		File sampleDir = new File(getClass().getClassLoader()
				.getResource(SAMPLE_DIR).getFile());
		
		File[] xmlFiles = sampleDir.listFiles();
		
		int counter = 0;
		D2S_BioportalClient client = new D2S_BioportalClient();
		
		for (File currentXMLFile : xmlFiles) {
			if(!currentXMLFile.getPath().endsWith("xml")) continue;
			counter ++;
			String outputFilePath = processedDir.getPath()+"\\output-"+counter+".xml";
			
			// IF this file already created, move on.
			if(new File(outputFilePath).exists()) continue;
		
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document doc = builder.parse(currentXMLFile);
			
			NodeList list = doc.getElementsByTagName("body");
			Element el = (Element)list.item(0);
			String textContent = el.getTextContent();
			
		
			FileWriter writer = new FileWriter(outputFilePath);
			System.out.println("Annotating \n" + textContent);
			String annotationResult = client.annotateText(textContent,"xml");
			writer.write(annotationResult);
			writer.close();
			
			Thread.sleep(20000);
			
		}
	}
}
