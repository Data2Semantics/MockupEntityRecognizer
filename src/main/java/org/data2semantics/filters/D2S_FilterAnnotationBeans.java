package org.data2semantics.filters;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

public class D2S_FilterAnnotationBeans extends XMLFilterImpl{

	String currentQName;
	boolean inAnnotationBeans=false;
	

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {

		currentQName = qName;
		if(currentQName.equalsIgnoreCase("annotations"))
			inAnnotationBeans = true;
		if(inAnnotationBeans){
			super.startElement(uri, localName, qName, atts);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		if(inAnnotationBeans){
			super.endElement(uri, localName, qName);
			if(qName.toLowerCase().contains("annotations")){
				inAnnotationBeans=false;
				currentQName = "";
			}
		}
	}


	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {

		// We are going to pass the integer value of this character, add it with
		// split offset and return it back to the serializer

		if (inAnnotationBeans)
			super.characters(ch, start, length);
	}
}
