package org.data2semantics.vocabulary;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class D2S_CTCAEVocabularyHandlerTest {

	@Test
	public void constructionTest() {
		D2S_CTCAEVocabularyHandler myHandler = new D2S_CTCAEVocabularyHandler();
		assert(myHandler!=null);
	}
	
	@Test
	public void listConceptsTest(){
		D2S_CTCAEVocabularyHandler myHandler = new D2S_CTCAEVocabularyHandler();
		List<D2S_Concept> availableConcepts = myHandler.getAvailableConcepts();
		int i=0;
		for(D2S_Concept concept : availableConcepts){
			System.out.println(concept);
			if (i++ > 50 ) break;
		}
	}

}
