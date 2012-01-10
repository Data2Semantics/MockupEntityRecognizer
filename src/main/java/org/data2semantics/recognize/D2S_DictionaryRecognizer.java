package org.data2semantics.recognize;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.pdfbox.examples.util.ExtractTextByArea;
import org.data2semantics.indexer.D2S_Indexer;
import org.data2semantics.indexer.D2S_PDFHandler;
import org.data2semantics.vocabulary.D2S_CTCAEVocabularyHandler;
import org.data2semantics.vocabulary.D2S_Concept;
import org.data2semantics.vocabulary.D2S_VocabularyHandler;

public class D2S_DictionaryRecognizer {
	private Log log = LogFactory.getLog(D2S_DictionaryRecognizer.class);

	// Here is where we get all the vocabularies
	D2S_VocabularyHandler vocabularyHandler;
	
	// List of PDF handlers where we can get the contents
	List<D2S_PDFHandler> pdfHandlers;
	
	
	//List of annotation results
	List<D2S_Annotation> results;

	// Think of a better way to provide this directory as a configuration
	static final String PUB_DIR="E:\\Projects\\COMMIT\\Philips-Elsevier-Usecase\\NERExperiment\\data\\Publications";
	static final String GUIDE_DIR="E:\\Projects\\COMMIT\\Philips-Elsevier-Usecase\\NERExperiment\\data\\Guidelines";

	// Coba bikin dari publication dulu
	D2S_Indexer currentIndex=null;
	
	public D2S_DictionaryRecognizer() {
		vocabularyHandler = new D2S_CTCAEVocabularyHandler();
	
		try {
			currentIndex = new D2S_Indexer();
			currentIndex.addPDFDirectoryToIndex(PUB_DIR);
			currentIndex.addPDFDirectoryToIndex(GUIDE_DIR);
		
		} catch (IOException e) {
			log.error("Failed to add files to index");
		}
		
	}
	


	//Let's just do it and see how far we go.
	public void doIt() throws CorruptIndexException, IOException, ParseException{
		
		results = new Vector<D2S_Annotation>();
		int count =0;
		for(D2S_Concept currentConcept : vocabularyHandler.getAvailableConcepts()){
				List<String> synonyms = currentConcept.getSynonyms();
				String mainTerm = currentConcept.getMainTerm();
				Document[] found = currentIndex.simpleStringSearch("\""+mainTerm+"\"", "contents");
				if(found != null && found.length != 0) {
					System.out.println("Found " + found.length + "  results for "+mainTerm);
					count ++;
				}
				for(String term : synonyms){
					found = currentIndex.simpleStringSearch("\""+term+"\"", "contents");
					if(found == null || found.length == 0) continue;
					System.out.println("Found " + found.length + "  results for "+term);
//					for(Document d : found){
//						System.out.println("   "+d.getField("Title")+d.getField("contents"));
//					}
					count ++;
				}
					
		}
		log.info("Check count : "+count);
		log.info("Best score : "+currentIndex.bestScore);

		log.info("Best term : "+currentIndex.bestTerm);
		log.info("Best doc: "+currentIndex.bestDoc);
		
	}
	
	public int getNumberOfFiles(){
		return currentIndex.getNumberOfFiles();
	}
	
	
}
