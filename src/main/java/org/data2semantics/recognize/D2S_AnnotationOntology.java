package org.data2semantics.recognize;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.turtle.TurtleWriter;
import org.openrdf.vocabulary.RDF;



/**
 * This class will be responsible for handling output properly.
 * @author wibisono
 *

 */
public class D2S_AnnotationOntology {
	private Log log = LogFactory.getLog(D2S_AnnotationOntology.class);
	

	TurtleWriter docWriter;
	FileOutputStream outputStream = null;
	
	public D2S_AnnotationOntology(String outputFile) {
		try {
			outputStream = new FileOutputStream(new File(outputFile));
		} catch (FileNotFoundException e) {
			log.error("Failed to create output file");
		}
		
		docWriter = new TurtleWriter(outputStream);

		
		setupNameSpace();
		

	}

	public void startWriting() {
		try {
			docWriter.startDocument();
		} catch (IOException e) {
			log.error("Failed to start writing document");
		}
	}

	public void stopWriting(){
		try {
			docWriter.endDocument();
			outputStream.close();
		} catch (IOException e) {

			log.error("Failed to stop writing document");
		}
	}
	
	private void setupNameSpace() {
		
		try {
			docWriter.setNamespace("ao", "http://purl.org/ao/core#");
			docWriter.setNamespace("aot", "http://purl.org/ao/types#");
			docWriter.setNamespace("aos", "http://purl.org/ao/selectors#");
			docWriter.setNamespace("aof", "http://purl.org/ao/foaf#");
			docWriter.setNamespace("pav", "http://purl.org/pav#");
			docWriter.setNamespace("ann", "http://www.w3.org/2000/10/annotation-ns#");
			docWriter.setNamespace("pro", "http://purl.obolibrary.org/obo#");
			docWriter.setNamespace("foaf", "http://xmlns.com/foaf/0.1#");
			
				
		} catch (IOException e) {
			log.error("Failed to setup namespaces for Annotation Ontology");
		}
	}
	
	public void addStatement(Resource subj, URI pred, Value obj ){
		  try {
			docWriter.writeStatement(subj, pred, obj);
		} catch (IOException e) {
			log.error("Failed to add statement: "+subj+" "+pred+" "+obj);
		}
	}
	
	public OutputStream getOS(){
		return outputStream;
	}
	
	
}
