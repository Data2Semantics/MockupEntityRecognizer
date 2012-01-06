package org.data2semantics.vocabulary;

import java.util.List;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class D2S_CTCAEVocabularyHandler implements D2S_VocabularyHandler {
	
	private Log log = LogFactory.getLog(D2S_CTCAEVocabularyHandler.class);
	
	// The available concepts found in current vocabulary
	List<D2S_Concept> availableConcepts;
	
	//This is the path where we find the vocabulary location
	String CTCAE_LOCATION="http://ncicb.nci.nih.gov/xml/owl/EVS/ctcae.owl";
	
	//OWLOntology
	OWLOntology theOntology;
	
	public D2S_CTCAEVocabularyHandler() {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			IRI ontologyIRI = IRI.create(CTCAE_LOCATION);
			try {
				theOntology = manager.loadOntology(ontologyIRI);
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				log.error("Failed to load CTCAE ontology");
			}
	}
	
	public List<D2S_Concept> getAvailableConcepts() {
		// TODO Auto-generated method stub
		return availableConcepts;
	}

	public String getVocabularyName() {
		// TODO Auto-generated method stub
		return "CTCAE";
	}

}
