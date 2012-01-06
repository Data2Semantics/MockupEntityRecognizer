package org.data2semantics.recognize;

import java.util.List;

import org.data2semantics.indexer.D2S_PDFHandler;
import org.data2semantics.vocabulary.D2S_VocabularyHandler;

public class D2S_DictionaryRecognizer {
	
	// Here is where we get all the vocabularies
	D2S_VocabularyHandler vocabularyHandlers;
	
	// List of PDF handlers where we can get the contents
	List<D2S_PDFHandler> pdfHandlers;
	
	// Where to put the results ? What should happen here ?
	// Should the D2S Indexer be loaded here also ? 
	// Should there be some preprocessing performed first?
	
	public D2S_DictionaryRecognizer() {
		// TODO Auto-generated constructor stub
	}
	
}
