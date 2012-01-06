package org.data2semantics.vocabulary;

import java.util.List;
import java.util.Vector;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;

public class D2S_CTCAEClassVisitor extends OWLOntologyWalkerVisitor<Object> {

	public D2S_CTCAEClassVisitor(OWLOntologyWalker walker) {
		super(walker);
	}

	Vector<D2S_Concept> availableConcepts = new Vector<D2S_Concept>();

	public Object visit(OWLClass ca) {
		D2S_Concept currentConcept = new D2S_Concept();
		currentConcept.setStringID(ca.toStringID());
		availableConcepts.add(currentConcept);
		return null;
	}

	public List<D2S_Concept> getAvailableConcepts(){
		return availableConcepts;
	}
}
