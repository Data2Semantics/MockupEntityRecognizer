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
	String prefix, suffix;
	
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
	
	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return the suffix
	 */
	public String getSuffix() {
		return suffix;
	}

	/**
	 * @param suffix the suffix to set
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	private int toInt(float f){
			return new Float(Math.round(f)).intValue();
	}
	public String getPosition(){
		
		return "("+toInt(left)+","+toInt(top)+")-("+toInt(bottom)+","+toInt(right)+")";
	}
	
	@Override
	public String toString() {
		String result = "";
		result += "\nPage No : "+pageNumber + "  Chunk No : "+chunkNumber;
		result += "\nText    : "+textChunk;
		result += "\nPos     : "+top + ","+left+" - "+bottom+","+right;
		return result;
	}

	/**
	 * @return the pageNumber
	 */
	public int getPageNumber() {
		return pageNumber;
	}

	/**
	 * @param pageNumber the pageNumber to set
	 */
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	/**
	 * @return the chunkNumber
	 */
	public int getChunkNumber() {
		return chunkNumber;
	}

	/**
	 * @param chunkNumber the chunkNumber to set
	 */
	public void setChunkNumber(int chunkNumber) {
		this.chunkNumber = chunkNumber;
	}

	/**
	 * @return the textChunk
	 */
	public String getTextChunk() {
		return textChunk;
	}

	/**
	 * @param textChunk the textChunk to set
	 */
	public void setTextChunk(String textChunk) {
		this.textChunk = textChunk;
	}

	/**
	 * @return the top
	 */
	public float getTop() {
		return top;
	}

	/**
	 * @param top the top to set
	 */
	public void setTop(float top) {
		this.top = top;
	}

	/**
	 * @return the left
	 */
	public float getLeft() {
		return left;
	}

	/**
	 * @param left the left to set
	 */
	public void setLeft(float left) {
		this.left = left;
	}

	/**
	 * @return the bottom
	 */
	public float getBottom() {
		return bottom;
	}

	/**
	 * @param bottom the bottom to set
	 */
	public void setBottom(float bottom) {
		this.bottom = bottom;
	}

	/**
	 * @return the right
	 */
	public float getRight() {
		return right;
	}

	/**
	 * @param right the right to set
	 */
	public void setRight(float right) {
		this.right = right;
	}
}
