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
		System.out.println("Preparing vocabulary");
		
		vocabularyHandler = new D2S_CTCAEVocabularyHandler();
		System.out.println("Done preparing vocabulary");
		
		try {
			System.out.println("Preparing indexes");
			currentIndex = new D2S_Indexer();
			currentIndex.addPDFDirectoryToIndex(PUB_DIR);
			currentIndex.addPDFDirectoryToIndex(GUIDE_DIR);
			System.out.println("Done preparing indexes");
			
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
				Document[] found = currentIndex.simpleStringSearch(mainTerm, "contents");
				if(found != null && found.length != 0) {
					//System.out.println("Found " + found.length + "  results for "+mainTerm);
					sanityCheck(found, mainTerm);
					count ++;

				}
				for(String term : synonyms){
					found = currentIndex.simpleStringSearch(term, "contents");
					if(found == null || found.length == 0) continue;
					//System.out.println("Found " + found.length + "  results for "+term);
					sanityCheck(found, term);
					count ++;
				}
					
		}
		log.info("Check count : "+count);
		log.info("Best score : "+currentIndex.bestScore);

		log.info("Best term : "+currentIndex.bestTerm);
		log.info("Best doc: "+currentIndex.bestDoc);
		
	}
	
	private void sanityCheck(Document[] found, String mainTerm){
		for (Document d : found) {
			
			String[] tokens = mainTerm.split(" ");
			String content = d.get("contents").toLowerCase();
			String fileName = d.get("filename");
			
			int checkIndex =content.indexOf(mainTerm.toLowerCase());
			
			if (checkIndex  >= 0) {
				System.out.println("=================================================");
				System.out.println("Found : "	+ mainTerm + " \nFile : " +fileName);
				System.out.println("     >>   CONTEXT:");
				String context =content.substring(Math.max(0,
								checkIndex - 50), Math.min(
								checkIndex + 50,
								content.length()));
				System.out.println(context.substring(0,Math.min(context.length(), 150)));
				System.out.println("=================================================");
				
			} 
		}
	}
	public int getNumberOfFiles(){
		return currentIndex.getNumberOfFiles();
	}
	
	
}
