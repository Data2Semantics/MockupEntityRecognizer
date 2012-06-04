package org.data2semantics.indexer;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.queryParser.ParseException;
import org.apache.pdfbox.examples.util.PrintTextLocations;
import org.junit.Test;

/**
 * 
 */

/**
 * @author wibisono
 *
 */
public class D2S_IndexTest {

	static final String PUB_DIR="E:\\Projects\\COMMIT\\Philips-Elsevier-Usecase\\NERExperiment\\data\\Publications";
	static final String GUIDE_DIR="E:\\Projects\\COMMIT\\Philips-Elsevier-Usecase\\NERExperiment\\data\\Guidelines";
	
	//@Test
	public void testSinglePDF() throws IOException, ParseException {
		D2S_Indexer testIndexes = new D2S_Indexer();
	
		File  testFile				= new File("src\\test\\resources\\neutropenia.pdf");
		testIndexes.startAddingFiles();
		testIndexes.addPDFDocument(testFile);
		testIndexes.stopAddingFiles();
		
		Document [] hits = testIndexes.simpleStringSearch("UPHS", "Author"); 
		assert(hits != null);
		System.out.println("Number of hits " + hits.length);
		for(Document doc : hits){
			System.out.println("Hits "+doc.get("Author"));
		}
	}
	
	//@Test
	public void testMultiplePDF() throws IOException, ParseException{
		File publicationDir = new File(PUB_DIR);
		
		File[] pubFiles  = publicationDir.listFiles();
		assert(pubFiles != null);
		
		D2S_Indexer testIndexes = new D2S_Indexer();
		testIndexes.startAddingFiles();
		
		
		for(File currentPDF : pubFiles){
			if(currentPDF.getName().endsWith(".pdf")){
				testIndexes.addPDFDocument(currentPDF);
			}
		}
		testIndexes.stopAddingFiles();

		Document [] hits = testIndexes.simpleStringSearch("neutropenic fever", "contents");

		
		assert(hits != null);
		for(Document doc : hits){
			List<Fieldable> fields = doc.getFields();
			System.out.println(doc.get("Subject"));
			for(Fieldable f : fields){
				if(f.name().equals("summary")) continue;
				if(f.name().equals("contents")) continue;
				
				System.out.println("   " +f.name() + " : " + f.stringValue());
			}
		}
	}
	
	//@Test
	public void checkNumberOfFiles() throws IOException{
		D2S_Indexer testIndexes = new D2S_Indexer();
		testIndexes.addPDFDirectoryToIndex(PUB_DIR);
		testIndexes.addPDFDirectoryToIndex(GUIDE_DIR);
		
		assert(testIndexes.getNumberOfFiles() != 0);
		System.out.println("Number of files" + testIndexes.getNumberOfFiles());
	}

}
