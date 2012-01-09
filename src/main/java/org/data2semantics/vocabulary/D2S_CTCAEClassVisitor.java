package org.data2semantics.vocabulary;

import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;
import org.w3c.dom.Document;


public class D2S_CTCAEClassVisitor extends OWLOntologyWalkerVisitor<Object> {
	private Log log = LogFactory.getLog(D2S_CTCAEClassVisitor.class);

	
	DocumentBuilderFactory factory =null;
	DocumentBuilder builder=null;
	Document xmlDoc;

	
	public D2S_CTCAEClassVisitor(OWLOntologyWalker walker) {
		super(walker);
		
		//Proper way of handling xml later, i'll just use regex for now.
		//initializeXMLDocumentBuilder();
	}

	Vector<D2S_Concept> availableConcepts = new Vector<D2S_Concept>();
	
	public Object visit(OWLClass owlClass) {
		D2S_Concept currentConcept = new D2S_Concept();
		currentConcept.setStringID(owlClass.toStringID());
		availableConcepts.add(currentConcept);
		
		//Synonyms datatype property happened to be Annotation Property
		//Thus we can get synonyms by accessing annotations associated with current owlClass
		Set<OWLAnnotation> currentAnnotations = owlClass.getAnnotations(getCurrentOntology());
		
		//Checking annotation properties and filtering out synonyms
		for(OWLAnnotation ann : currentAnnotations){
			OWLAnnotationProperty annProperty = ann.getProperty();
			if(isCTCAEFullSynonym(annProperty)){
				String currentSynonym = extractNCICPTermName(ann.getValue().toString());
				currentConcept.addSynonym(currentSynonym);
			}
			if(isCTCAEPreferredName(annProperty)){
				String mainTerm =ann.getValue().toString();
				currentConcept.setMainTerm(mainTerm);
			}
		}
		return null;
	}
	
	/**
	 * I need to know if this Annotation property is a FULL_SYN 
	 * Somebody tell me how to do this properly
	 * @param annProperty
	 * @return
	 */
	private boolean isCTCAEFullSynonym(OWLAnnotationProperty annProperty){
			return annProperty.getIRI().toString().endsWith("FULL_SYN");
	}
	
	private boolean isCTCAEPreferredName(OWLAnnotationProperty annProperty){
		return annProperty.getIRI().toString().endsWith("Preferred_Name");
}
	private String extractNCICPTermName(String xmlLiteral){
		return extractToken(xmlLiteral, "<ncicp:term-name>", "</ncicp:term-name>");
	}
	
	private String extractToken(String stringToSearch, String startToken, String endToken) {
		int startIndex = stringToSearch.indexOf(startToken);
		int stopIndex = stringToSearch.indexOf(endToken);
		return stringToSearch.substring(startIndex+startToken.length(), stopIndex);
	}

	public List<D2S_Concept> getAvailableConcepts(){
		return availableConcepts;
	}
}
