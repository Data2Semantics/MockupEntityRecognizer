package org.data2semantics.recognize;

import org.data2semantics.modules.D2S_AnnotationRenderer;
import org.eclipse.jetty.util.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class which handles XML results of bioportal annotation 
 * @author wibisono
 *
 */
public class D2S_BioPortalAnnotationHandler extends DefaultHandler {

	String curQname;
	
	private Logger log = LoggerFactory.getLogger(D2S_BioPortalAnnotationHandler.class);
	
	// Whenever we enter a new annotation beans we will set this to some new
	// Annotation
	D2S_Annotation currentAnnotation = null;

	String textToAnnotate = "";
	String fullID = "";
	String preferredName;
	String piiUnformatted="";
	
	int from, to;

	boolean inTerm = false;

	String originalSource;
	String localFileName;
	
	D2S_AnnotationWriter writer;
	
	public D2S_BioPortalAnnotationHandler(String localFileName, String originalSource, D2S_AnnotationWriter writer) {
		this.originalSource = originalSource;
		this.localFileName= localFileName ;
		this.writer = writer;
		log.info("Start handling annotation");
	}
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		
		if (qName.equalsIgnoreCase("AnnotationBean")) {
			currentAnnotation = new D2S_Annotation();
			fullID = "";
			preferredName = "";
		}

		if (qName.equalsIgnoreCase("term")) {
			inTerm = true;
		}

		curQname = qName;

	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equalsIgnoreCase("AnnotationBean")) {
			currentAnnotation.setTermFound(fullID);
			currentAnnotation.setPreferredName(preferredName);
			currentAnnotation.setPrefix(getCurrentPrefix(from,to));
			currentAnnotation.setSuffix(getCurrentSuffix(from,to));
			currentAnnotation.setOnDocument(originalSource);
			currentAnnotation.setSourceDocument(localFileName);
			currentAnnotation.setFrom(from);
			currentAnnotation.setTo(to);
			writer.addAnnotation(currentAnnotation);
			currentAnnotation = null;
		}
		if (qName.equalsIgnoreCase("term")) {
			inTerm = false;
		}
		curQname ="";
	}

	// Getting 200 characters after the term, or the end of the text to Annotate
	private String getCurrentSuffix(int from2, int to2) {
		if(from2 > textToAnnotate.length() ) return "";
		return textToAnnotate.substring(to2, Math.min(textToAnnotate.length(), to2+200));
	}
	
	// Getting 200 characters before the term
	private String getCurrentPrefix(int from2, int to2) {
		if(from2 > textToAnnotate.length()) return "";
		return textToAnnotate.substring(Math.max(0,from2-200),Math.max(0, from2-1));
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {

		if (curQname.equalsIgnoreCase("textToAnnotate")) {
			textToAnnotate += new String(ch, start, length);
			return;
		}

		
		if (currentAnnotation == null) {
			return;
		}

		if (curQname.equalsIgnoreCase("from")) {
			from = new Integer(new String(ch, start, length));
			return;
		}

		if (curQname.equalsIgnoreCase("to")) {
			to = new Integer(new String(ch, start, length));
			return;
		}

		
		if (inTerm) {
			if (curQname.equalsIgnoreCase("fullId")) {
				fullID += new String(ch, start, length);
				return;
			}

			if (curQname.equalsIgnoreCase("preferredName")) {
				preferredName += new String(ch, start, length);
				return;
			}
		}

	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}
	

}
