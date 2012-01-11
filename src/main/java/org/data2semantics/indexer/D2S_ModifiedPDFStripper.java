package org.data2semantics.indexer;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.TextPosition;

public class D2S_ModifiedPDFStripper extends PDFTextStripper {
	
	private Log log = LogFactory.getLog(D2S_ModifiedPDFStripper.class);
	Vector<D2S_DocChunk> documentChunks = new Vector<D2S_DocChunk>();
	
	public D2S_ModifiedPDFStripper() throws IOException {
		super();
		
	}
	
	
	float top, left, bottom, right;
	int countPage=0, countChunk = 0;

	StringBuffer currentChunk;
	
	@Override
	protected void processTextPosition(TextPosition text) {
		updateChunkBoundingBox(text);
		currentChunk.append(text.getCharacter());
		if(text.getCharacter().equals(".")){
			++countChunk;
			documentChunks.add(new D2S_DocChunk(countPage, countChunk, currentChunk.toString(), top, left, bottom, right));
			initializeChunk();
		}
	}
	
	private void updateChunkBoundingBox(TextPosition text) {
		if(left > text.getX()) {
			left = text.getX();
		}
		
		if(top > text.getY()) {
			top = text.getY();
		}
		
		if(bottom < text.getY()+text.getHeight()) {
			bottom = text.getY()+text.getHeight();
		}
		
		if(right < text.getX() + text.getWidth()){
			right = text.getX()+text.getWidth();
		}
		
	}

	

	Vector<List<TextPosition>> getCharacterByArticles(){
		return charactersByArticle;
	}
	
	
	@Override
	protected void startPage(PDPage page) throws IOException {
		// TODO Auto-generated method stub
		super.startPage(page);
		countPage ++;
		initializeChunk();
	}

	private void initializeChunk() {
		top = 10000; left = 10000; 
		bottom = 0; right = 0;
		currentChunk = new StringBuffer();		
	}
	
	public Vector<D2S_DocChunk> getDocumentChunks(){
		return documentChunks;
	}
}
