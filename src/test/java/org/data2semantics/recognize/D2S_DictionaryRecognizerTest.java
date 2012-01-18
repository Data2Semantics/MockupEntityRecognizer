package org.data2semantics.recognize;

import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.junit.Test;

public class D2S_DictionaryRecognizerTest {
	
//	@Test
//	public void creationTest(){
//		D2S_DictionaryRecognizer recognizer = new D2S_DictionaryRecognizer();
//		assert(recognizer!=null);
//	}
//	
//	@Test
//	public void searchTerm() throws CorruptIndexException, IOException, ParseException{
//		D2S_DictionaryRecognizer recognizer = new D2S_DictionaryRecognizer();
//		recognizer.doIt();
//	}
//	
	@Test
	public void searchTermWithRIO() throws CorruptIndexException, IOException, ParseException{
		D2S_DictionaryRecognizer recognizer = new D2S_DictionaryRecognizer();
		recognizer.doItWithRIO("output.rdf");
	}
}
