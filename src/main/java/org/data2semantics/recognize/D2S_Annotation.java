package org.data2semantics.recognize;
/**
 *
 * I need a data structure to keep the results of recognition, and as needed by visualizer this should contains at least:
 * 		- The highlighted terms found in the document
 *		- Vocabulary used to match the term
 *		- Page and  position where the text is found
  		  (I'll return position based on TextPosition from pdfbox, maybe some conversion for absolute pixel position will be needed).
 * 
 * 
 * @author wibisono
 *
 */
public class D2S_Annotation {
	
	
	//This is the term found on the document
	String termFound;
	
	//Text preceding term found
	String prefix;
	
	//Text following term found
	String suffix;
	
	// Page number in PDF
	int page_number;
	
	// Location as returned by TextPosition from pdfbox
	int x_offset, y_offset;
	
}
