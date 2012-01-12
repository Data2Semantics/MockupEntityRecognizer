package org.data2semantics.indexer;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.examples.pdmodel.Annotation;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDGamma;
import org.apache.pdfbox.pdmodel.interactive.action.type.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLine;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationSquareCircle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.TextPosition;

public class D2S_ChunkedPDFStripper extends PDFTextStripper {
	
	private Log log = LogFactory.getLog(D2S_ChunkedPDFStripper.class);
	Vector<D2S_DocChunk> documentChunks = new Vector<D2S_DocChunk>();
	
	public D2S_ChunkedPDFStripper() throws IOException {
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
	Annotation a;
	
	private void updateChunkBoundingBox(TextPosition text) {
		if(left > text.getX()) {
			left = text.getX();
		}
		
		if(top > text.getY()) {
			top = text.getY()-text.getHeight();
		}
		
		if(bottom < text.getY()) {
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
