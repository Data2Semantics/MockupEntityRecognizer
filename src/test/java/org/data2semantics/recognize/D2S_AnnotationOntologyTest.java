package org.data2semantics.recognize;

import org.data2semantics.indexer.D2S_Indexer;
import org.junit.Test;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.URIImpl;

public class D2S_AnnotationOntologyTest {
	static final String PUB_DIR="E:\\Projects\\COMMIT\\Philips-Elsevier-Usecase\\NERExperiment\\data\\Publications";
	
	@Test
	public void testAnnotationOntology() throws Exception{
		D2S_Indexer indexer = new D2S_Indexer();
		indexer.addPDFDirectoryToIndex(PUB_DIR);
		
		D2S_AnnotationOntology ao  = new D2S_AnnotationOntology("result.rdf");
		ao.startWriting();
		ao.addStatement(new BNodeImpl("Some Node"),URIImpl.RDF_TYPE, URIImpl.RDFS_CLASS);
		ao.addFiles(indexer.getIndexedFiles());
		ao.stopWriting();
		
		
	}
}
