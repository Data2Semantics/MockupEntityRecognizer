package org.data2semantics.vocabulary;

import java.util.List;

/**
 * This will be the class responsible for handling vocabularies from existing concepts.
 * 
 * User of this vocabulary handler is interested in :
 * 		- getting list of string containing concepts.
 * 		- knowing the shorthand tag for this 
 * 
 * @author wibisono
 *
 */
public interface D2S_VocabularyHandler {
	// List of available concepts as string, that will be used as dictionary
	List<D2S_Concept> getAvailableConcepts();
	
	// Tag name that will be used for this vocabulary
	String getVocabularyName();
}
