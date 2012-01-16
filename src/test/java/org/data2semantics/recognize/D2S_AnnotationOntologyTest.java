package org.data2semantics.recognize;

import org.junit.Test;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.URIImpl;

public class D2S_AnnotationOntologyTest {

	@Test
	public void testAnnotationOntology(){
		D2S_AnnotationOntology ao  = new D2S_AnnotationOntology("result.rdf");
		ao.startWriting();
		ao.addStatement(new BNodeImpl("Some Node"),URIImpl.RDF_TYPE, URIImpl.RDFS_CLASS);
		ao.stopWriting();
		
		
	}
}
