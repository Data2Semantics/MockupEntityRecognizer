package org.data2semantics.recognize;

import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;

import java.util.List;
import java.io.File;
import java.io.IOException;

import org.data2semantics.indexer.D2S_PDFHandler;
import org.junit.Test;

public class D2S_NERTest {
	/**
	 * Just a test, recognizing using existing training test.
	 * @param args
	 * @throws IOException
	 */
	@Test
	public void testFromNERSampleDemo() throws IOException {

		String serializedClassifier = "src\\main\\resources\\all.3class.distsim.crf.ser.gz";
		File  fileTest				= new File("src\\test\\resources\\neutropenia.pdf");
		D2S_PDFHandler extractor = new D2S_PDFHandler(fileTest);
		
		AbstractSequenceClassifier classifier = CRFClassifier
				.getClassifierNoExceptions(serializedClassifier);

		String fileContents = extractor.getStrippedTextContent();
		//System.out.println(fileContents);
		
		List<List<CoreLabel>> out = classifier.classify(fileContents);
		for (List<CoreLabel> sentence : out) {
			for (CoreLabel word : sentence) {
				System.out.print(word.word() + '/'+ word.get(AnswerAnnotation.class) + ' ');
			}
			System.out.println();
		}
		
		// Not really a test, move along, nothing here.
		assert(out != null);
		
	}

}
