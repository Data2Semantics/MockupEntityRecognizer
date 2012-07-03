package org.data2semantics.recognize;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * 
 * Started producing annotation ontology based on XML result produced by Bioportal annotator.
 * @author wibisono
 *
 */
public class D2S_Bioportal2AOTest {
	

	// Assumes some processed-xml directory existed, containing xml files 
	// these xml files are annotated using bioportal. It should be located on src/test/resources/processsed-xml
	public static final String PROCESSED_DIR = "processed-xml";
	ClassLoader currentClassLoader =getClass().getClassLoader();
	
	File processedDir = new File(currentClassLoader.getResource(PROCESSED_DIR).getFile());
	
	HashMap<String, String> originalFileSources = new HashMap<String, String>();
	
	
	//@Test
	public void generateAOFromProcessedBioPortal() throws SAXException, IOException, ParserConfigurationException{
		initializeSourceFiles();
		
		FilenameFilter xmlFileFilter = new FilenameFilter() {
			
			public boolean accept(File arg0, String name) {
				return name.endsWith("xml");
			}
		};
		
		File [] bpresults = processedDir.listFiles(xmlFileFilter);
		
		D2S_AnnotationOntologyWriter aoWriter = new D2S_AnnotationOntologyWriter("output.rdf");
		aoWriter.startWriting();
		aoWriter.addFileAndURLs(Arrays.asList(bpresults),originalFileSources);
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		
		
		D2S_BioPortalAnnotationHandler bioPortalAnnotationSAXHandler;
		SAXParser parser ;
		Reader reader ;
		InputSource is; 
		List<D2S_Annotation> currentAnnotations;
	
		
		
		for(File currentResultFile : bpresults){
			
			parser = saxParserFactory.newSAXParser();
			reader = new InputStreamReader(new FileInputStream(currentResultFile),"UTF-8");
			is = new InputSource(reader);
			is.setEncoding("UTF-8");
			
			String originalSource = originalFileSources.get(currentResultFile.getName());
			bioPortalAnnotationSAXHandler = new D2S_BioPortalAnnotationHandler(currentResultFile.getName(), originalSource, aoWriter);
			parser.parse(is, bioPortalAnnotationSAXHandler);
			
//			currentAnnotations = bioPortalAnnotationSAXHandler.getAnnotations();
//			for(D2S_Annotation currentAnnotation : currentAnnotations)
//				aoWriter.addAnnotation(currentAnnotation);
			
		}
		
		aoWriter.stopWriting();
	}


	private void initializeSourceFiles() {
		File sourceList = new File(currentClassLoader.getResource(PROCESSED_DIR+"/sources.txt").getFile());
		try {
			Scanner scanner = new Scanner(sourceList);
			while(scanner.hasNextLine()){
				String fileAndURL = scanner.nextLine();
				String fileURL[] = fileAndURL.split(" ");
				System.out.println(fileAndURL);
				originalFileSources.put("output-"+fileURL[0]+".xml", fileURL[1]);
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
