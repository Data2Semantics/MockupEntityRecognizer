package org.data2semantics.recognize;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class D2S_BioPortalClientTest {

	// This file in src/test/resources
	public static final String GUIDELINE_DIR = "guideline-html";

	public static final String SAMPLE_DIR = "sample-xml";

	// This thing should not be empty, amazingly.
	public static final String PROCESSED_DIR = "processed-xml";
	
	
	File guidelineDir = new File(getClass().getClassLoader()
			.getResource(GUIDELINE_DIR).getFile());
	
	File sampleDir = new File(getClass().getClassLoader()
			.getResource(SAMPLE_DIR).getFile());

	
	File processedDir = new File(getClass().getClassLoader()
			.getResource(PROCESSED_DIR).getFile());
	
	//@Test
	public void processGuidelinesHTML() throws IOException, InterruptedException {


		FilenameFilter fileFilter = new FilenameFilter() {
			
			public boolean accept(File arg0, String name) {
				return name.endsWith("html") && name.startsWith("Q00");
			}
		};
		
		File[] xmlFiles = guidelineDir.listFiles(fileFilter);

		int counter = 0;
		
		for (File currentHTMLFile : xmlFiles) {
			String outputName = currentHTMLFile.getName();
			outputName=outputName.replaceAll("html","xml");
			outputName="output-"+outputName;
			counter ++;
			String outputFilePath = processedDir.getPath()+"\\"+outputName;
			
			// IF this file already created, move on.
			if(new File(outputFilePath).exists()) continue;
			
			Scanner fileScanner = new Scanner(new FileInputStream(currentHTMLFile));
			
			StringBuilder stringBuilder = new StringBuilder();
			D2S_BioportalClient client = new D2S_BioportalClient();
			
			try {
				System.out.println("Start writing"+outputFilePath);
				while (fileScanner.hasNextLine()) {
					stringBuilder.append(fileScanner.nextLine() + "\n");
				}
				String textToAnnotate = Jsoup.clean(stringBuilder.toString(), Whitelist.none());
				int quarter = textToAnnotate.length()/4;
				for(int i=0;i<4;i++){
					client.annotateToFile(textToAnnotate.substring(i*quarter, (i+1)*quarter),"xml",new File(outputFilePath.replace(".xml","."+i+".xml")));
				}
				System.out.println("Done writing"+outputFilePath);
			} finally {
				fileScanner.close();
			}
			//Thread.sleep(20000);
		}
	}
	
	//@Test
	/**
	 * Get the files from sample dir, send it to Bioportal for annotation.
	 * Store the result in processed directory.
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws InterruptedException
	 */
	public void parseXMLUsingDOMTest() throws SAXException, IOException, ParserConfigurationException, InterruptedException{
		File sampleDir = new File(getClass().getClassLoader()
				.getResource(SAMPLE_DIR).getFile());
		
		FilenameFilter xmlFileFilter = new FilenameFilter() {
			
			public boolean accept(File arg0, String name) {
				return name.endsWith("xml") ;
			}
		};
		
		File[] xmlFiles = sampleDir.listFiles(xmlFileFilter);
		
		
		for (File currentXMLFile : xmlFiles) {
			
			// Append output- to the content file name
			String outputFilePath = processedDir.getPath()+"\\output-"+currentXMLFile.getName();
			
			// IF this file already created, move on.
			if(new File(outputFilePath).exists()) continue;
		
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document doc = builder.parse(currentXMLFile);
			
			// We are annotating only the body part
			NodeList list = doc.getElementsByTagName("body");
			Element el = (Element)list.item(0);
			String textContent = el.getTextContent();
			
		
			FileWriter writer = new FileWriter(outputFilePath);
			System.out.println("Annotating \n" + textContent);
			D2S_BioportalClient client = new D2S_BioportalClient();
			String annotationResult = client.annotateText(textContent,"xml");
			writer.write(annotationResult);
			writer.close();
			
			Thread.sleep(20000);
			
		}
	}
}
