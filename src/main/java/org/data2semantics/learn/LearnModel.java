package org.data2semantics.learn;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.util.CoreMap;

public class LearnModel {
	CRFClassifier<CoreMap> classifier;
	PTBTokenizer<HasWord> tokenizer;
	
}
