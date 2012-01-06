package org.data2semantics.vocabulary;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;

public class D2S_CTCAEVocabularyHandler implements D2S_VocabularyHandler {

	private Log log = LogFactory.getLog(D2S_CTCAEVocabularyHandler.class);

	// The available concepts found in current vocabulary
	List<D2S_Concept> availableConcepts;

	// This is the path where we find the vocabulary location
	URL CTCAE_LOCATION;

	// OWLOntology
	OWLOntology theOntology;

	
	public D2S_CTCAEVocabularyHandler() {
		
		
		setupCTCAELocation(false);
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		try {
			IRI ontologyIRI = IRI.create(CTCAE_LOCATION);
			theOntology = manager.loadOntology(ontologyIRI);
		}  
		catch (URISyntaxException e) {
			log.error("Failed to setup IRI for CTCAE");
		}
		catch (OWLOntologyCreationException e) {
			log.error("Failed to load CTCAE ontology");
		}
		prepareAvailableConcepts();
	}

	private void setupCTCAELocation(boolean original){
		if(original)
			try {
				CTCAE_LOCATION = new URL("http://ncicb.nci.nih.gov/xml/owl/EVS/ctcae.owl");
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		else
			CTCAE_LOCATION=getClass().getClassLoader().getResource("vocabulary/CTCAE_4.03_2010-06-14_v4.03.owl");

	}
	/**
	 * Here we prepare available concepts, by walking through the Ontology using
	 * visitor pattern. Code based on example from : http://bit.ly/owlapi-walker
	 * 
	 */
	private void prepareAvailableConcepts() {

		OWLOntologyWalker theWalker = new OWLOntologyWalker(Collections.singleton(theOntology));
		
		D2S_CTCAEClassVisitor visitor = new D2S_CTCAEClassVisitor(theWalker);
		
		theWalker.walkStructure(visitor);
		
		availableConcepts = visitor.getAvailableConcepts();

	}

	public List<D2S_Concept> getAvailableConcepts() {

		return availableConcepts;
	}

	public String getVocabularyName() {
		// TODO Auto-generated method stub
		return "CTCAE";
	}

}
