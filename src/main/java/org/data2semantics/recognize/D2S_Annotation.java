package org.data2semantics.recognize;

/**
 * 
 * I need a data structure to keep the results of recognition, and as needed by
 * visualizer this should contains at least: - The highlighted terms found in
 * the document - Vocabulary used to match the term - Page and position where
 * the text is found (I'll return position based on TextPosition from pdfbox,
 * maybe some conversion for absolute pixel position will be needed).
 * 
 * 
 * @author wibisono
 * 
 */
public class D2S_Annotation {

	// This is the term found on the document
	String termFound;

	String preferredName;

	// Text preceding term found
	String prefix;

	// Text following term found
	String suffix;

	// Original location where this term is found.
	String onDocument;
	
	// Local instance of file, where we did annotation
	String sourceDocument;

	// Page number in PDF
	int page_number;

	// Location as returned by TextPosition from pdfbox
	int x_offset, y_offset;

	// Index location, from to for text from bioportal
	int from, to;
	
	@Override
	public String toString() {
		return "Term: " + termFound + "\nPreferredName: " + preferredName
				+ "\nPrefix: " + prefix + "\nSuffix: " + suffix
				+ "\nFileName: " + onDocument + "\nPage " + page_number
				+ "\nx,y " + x_offset + "," + y_offset + "\n";
	}

	/**
	 * @return the termFound
	 */
	public String getTermFound() {
		return termFound;
	}

	/**
	 * @param termFound the termFound to set
	 */
	public void setTermFound(String termFound) {
		this.termFound = termFound;
	}

	/**
	 * @return the preferredName
	 */
	public String getPreferredName() {
		return preferredName;
	}

	/**
	 * @param preferredName the preferredName to set
	 */
	public void setPreferredName(String preferredName) {
		this.preferredName = preferredName;
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

	/**
	 * @return the fileName
	 */
	public String getOnDocument() {
		return onDocument;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setOnDocument(String fileName) {
		this.onDocument = fileName;
	}

	/**
	 * @return the page_number
	 */
	public int getPage_number() {
		return page_number;
	}

	/**
	 * @param page_number the page_number to set
	 */
	public void setPage_number(int page_number) {
		this.page_number = page_number;
	}

	/**
	 * @return the x_offset
	 */
	public int getX_offset() {
		return x_offset;
	}

	/**
	 * @param x_offset the x_offset to set
	 */
	public void setX_offset(int x_offset) {
		this.x_offset = x_offset;
	}

	/**
	 * @return the y_offset
	 */
	public int getY_offset() {
		return y_offset;
	}

	/**
	 * @param y_offset the y_offset to set
	 */
	public void setY_offset(int y_offset) {
		this.y_offset = y_offset;
	}

	public void setFrom(int from) {
		this.from = from;
	}
	public void setTo(int to) {
		this.to = to;
	}

	public int getTo() {
		return to;
	}
	public int getFrom(){
		return from;
	}

	/**
	 * @return the sourceDocument
	 */
	public String getSourceDocument() {
		return sourceDocument;
	}

	/**
	 * @param sourceDocument the sourceDocument to set
	 */
	public void setSourceDocument(String sourceDocument) {
		this.sourceDocument = sourceDocument;
	}
}
