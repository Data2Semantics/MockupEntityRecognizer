package org.data2semantics.recognize;

import java.util.List;
import java.util.Vector;

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
	List<D2S_Annotation> annotations = new Vector<D2S_Annotation>();

	// Whenever we enter a new annotation beans we will set this to some new
	// Annotation
	D2S_Annotation currentAnnotation = null;

	String textToAnnotate = "";
	String fullID = "";
	String preferredName;
	
	int from, to;

	boolean inTerm = false;

	String fileName;
	
	public D2S_BioPortalAnnotationHandler(String fileName) {
		this.fileName = fileName;
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
			currentAnnotation.setFileName(fileName);
			currentAnnotation.setFrom(from);
			currentAnnotation.setTo(to);
			annotations.add(currentAnnotation);
			currentAnnotation = null;
		}
		if (qName.equalsIgnoreCase("term")) {
			inTerm = false;
		}
		curQname ="";
	}

	// Getting 200 characters after the term, or the end of the text to Annotate
	private String getCurrentSuffix(int from2, int to2) {
		return textToAnnotate.substring(to2, Math.min(textToAnnotate.length(), to2+200));
	}
	
	// Getting 200 characters before the term
	private String getCurrentPrefix(int from2, int to2) {
		return textToAnnotate.substring(Math.max(0,from2-200),from2-1);
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
		System.out.println(annotations.size());
		System.out.println(textToAnnotate);
		System.out.println();
		super.endDocument();
	}
	
	public List<D2S_Annotation>getAnnotations(){
		return annotations;
	}
}
