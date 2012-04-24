package org.data2semantics.recognize;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class D2S_Bioportal2AOTest {
	
	public static final String PROCESSED_DIR = "processed-xml";
	ClassLoader currentClassLoader =getClass().getClassLoader();
	
	File processedDir = new File(currentClassLoader.getResource(PROCESSED_DIR).getFile());
	
	@Test
	public void generateAOFromProcessedBioPortal() throws SAXException, IOException, ParserConfigurationException{
		File [] bpresults = processedDir.listFiles();
		D2S_AnnotationOntologyWriter aoWriter = new D2S_AnnotationOntologyWriter("output.rdf");
		aoWriter.startWriting();
		aoWriter.addFiles(Arrays.asList(bpresults));
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		
		for(File currentResult : bpresults){
			if(!currentResult.getName().endsWith("xml")) continue;
			if(!currentResult.getName().startsWith("output-")) continue;
			
			SAXParser parser = saxParserFactory.newSAXParser();
			Reader reader = new InputStreamReader(new FileInputStream(currentResult),"UTF-8");
			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");
			System.out.println(currentResult.getName());
			D2S_BioPortalAnnotationHandler myHandler = new D2S_BioPortalAnnotationHandler(currentResult.getName());
			parser.parse(is, myHandler);
			List<D2S_Annotation> testAnn= myHandler.getAnnotations();
			System.out.println(testAnn.get(0));
			
		}
		
		aoWriter.stopWriting();
	
	}
	
}
