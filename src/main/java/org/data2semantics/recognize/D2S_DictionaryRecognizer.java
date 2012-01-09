package org.data2semantics.recognize;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.data2semantics.indexer.D2S_Indexer;
import org.data2semantics.indexer.D2S_PDFHandler;
import org.data2semantics.vocabulary.D2S_CTCAEVocabularyHandler;
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

	public D2S_DictionaryRecognizer() {
		vocabularyHandler = new D2S_CTCAEVocabularyHandler();
		initializeIndexes();
	}
	
	private void initializeIndexes() {
		File publicationDir = new File(PUB_DIR);
		
		File[] pubFiles  = publicationDir.listFiles();
		assert(pubFiles != null);
		
		try {
		D2S_Indexer testIndexes = new D2S_Indexer();
			testIndexes.startAddingFiles();
			
			
			for(File currentPDF : pubFiles){
				if(currentPDF.getName().endsWith(".pdf")){
					testIndexes.addPDFDocument(currentPDF);
				}
			}
			testIndexes.stopAddingFiles();
		} catch(Exception e){
			log.error("Failed to add pdf files to indexes");
		}
	}

	//Let's just do it and see how far we go.
	public void doIt(){
	   
		
		
	}
	
	
}
