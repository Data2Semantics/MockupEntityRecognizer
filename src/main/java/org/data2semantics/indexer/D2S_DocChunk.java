package org.data2semantics.indexer;

/**
 * The idea is that this is a small chunk of text extracted from PDF which contains a whole sentece of text (delimited by .)
 * The base of extraction is using PDFTextStripper, by extending and using the processTextPosition streaming.
 * 
 * @author wibisono
 *
 */

public class D2S_DocChunk {
	int pageNumber;
	int chunkNumber;
	
	String textChunk;
	
	float top;
	float left;
	float bottom;
	float right;
	


	public D2S_DocChunk(int pageNumber, int chunkNumber, String textChunk,
			float top, float left, float bottom, float right) {
		super();
		this.pageNumber = pageNumber;
		this.chunkNumber = chunkNumber;
		this.textChunk = textChunk;
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;

	}
	
	@Override
	public String toString() {
		String result = "";
		result += "\nPage No : "+pageNumber + "  Chunk No : "+chunkNumber;
		result += "\nText    : "+textChunk;
		result += "\nPos     : "+top + ","+left+" - "+bottom+","+right;
		return result;
	}
}
