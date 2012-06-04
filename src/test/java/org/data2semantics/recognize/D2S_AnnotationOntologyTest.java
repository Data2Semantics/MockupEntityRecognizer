package org.data2semantics.recognize;

import org.data2semantics.indexer.D2S_Indexer;
import org.junit.Test;

public class D2S_AnnotationOntologyTest {
	static final String PUB_DIR="E:\\Projects\\COMMIT\\Philips-Elsevier-Usecase\\NERExperiment\\data\\Publications";
	
	//@Test
	public void testAnnotationOntology() throws Exception{
		D2S_Indexer indexer = new D2S_Indexer();
		indexer.addPDFDirectoryToIndex(PUB_DIR);
		
		D2S_AnnotationOntologyWriter ao  = new D2S_AnnotationOntologyWriter("result.rdf");
		ao.startWriting();
		ao.addFiles(indexer.getIndexedFiles());
		ao.addPDFAnnotation("mainTerm", "prefix", "postfix", "fileName", "http://meddra.com", "position-position", 
				"page_nr", "chunk_nr", "termLocation");
		ao.stopWriting();
		
		
	}
}
