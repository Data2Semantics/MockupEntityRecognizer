package org.data2semantics.filters;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * When merging splitted annotations, I need to get a modified header where the text to Annotate contains the whole text
 * instead of individual split.
 * @author wibisono
 *
 */
public class D2S_FilterHeaderAnnotation extends XMLFilterImpl{

	String currentQName;
	boolean relevantHeaders=false;
	String originalText;
	public D2S_FilterHeaderAnnotation(String originalText) {
		this.originalText = originalText;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {

		currentQName = qName;
		if(currentQName.equalsIgnoreCase("resultid") || currentQName.equalsIgnoreCase("statistics")
		 ||currentQName.equalsIgnoreCase("parameters"))
			
			relevantHeaders = true;
		if(relevantHeaders){
			super.startElement(uri, localName, qName, atts);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		if(relevantHeaders){
			super.endElement(uri, localName, qName);
			if(	qName.toLowerCase().contains("resultid") ||
				qName.toLowerCase().contains("statistics") ||
				qName.toLowerCase().contains("parameters") 
						
			   )
				relevantHeaders=false;
		}
	}

	/**
	 * Here is where actually transformation/offset happened
	 */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {

		// We are going to pass the integer value of this character, add it with
		// split offset and return it back to the serializer

		if (relevantHeaders){
			if(currentQName.equalsIgnoreCase("texttoannotate"))
				super.characters(originalText.toCharArray(), 0, originalText.length());
			else
			super.characters(ch, start, length);
			
		}
	}
}
