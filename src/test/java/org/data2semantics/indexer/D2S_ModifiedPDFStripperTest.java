package org.data2semantics.indexer;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Vector;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Test;

public class D2S_ModifiedPDFStripperTest {
	File  fileTest				= new File("src\\test\\resources\\neutropenia.pdf");
	PDDocument doc;
	
	@Test
	public void testCharactersByArticle() throws IOException{
		D2S_ModifiedPDFStripper mStripper = new D2S_ModifiedPDFStripper();
		StringWriter writer = new StringWriter();
		doc = PDDocument.load(fileTest);
		mStripper.writeText(doc,writer);
		
		Vector<D2S_DocChunk> chunks = mStripper.getDocumentChunks();
		for(D2S_DocChunk chunk : chunks)
			System.out.println(chunk);
	
	}
}
