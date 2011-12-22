package org.data2semantics.indexer;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;

import org.junit.Test;

/**
 * 
 */

/**
 * @author wibisono
 *
 */
public class D2SIndexTest {

	@Test
	public void testAddPDF() throws IOException, ParseException {
		D2SIndexes testIndexes = new D2SIndexes();
		
		File  testFile				= new File("src\\test\\resources\\neutropenia.pdf");
		testIndexes.addPDFDocument(testFile);
		
		Document [] hits = testIndexes.simpleStringSearch("neutropeni", "summary"); 
		assert(hits != null);
		System.out.println("Number of hits " + hits.length);
		for(Document doc : hits){
			System.out.println("Hits "+doc.toString());
		}
	}

}
