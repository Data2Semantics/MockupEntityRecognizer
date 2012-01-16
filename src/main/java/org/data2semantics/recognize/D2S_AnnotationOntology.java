package org.data2semantics.recognize;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.turtle.TurtleWriter;



/**
 * This class will be responsible for handling output properly.
 * @author wibisono
 *

 */
public class D2S_AnnotationOntology {
	
	
	private Log log = LogFactory.getLog(D2S_AnnotationOntology.class);

	
	/**
	 * Namespaces
	 */


	public static final String AO = "http://purl.org/ao/";
	
	public static final String AO_CORE = AO + "core#";

	public static final String AO_FOAF = AO + "foaf#";

	public static final String AO_TYPE = AO + "types#";
	
	public static final String AO_SELECTOR = AO + "selectors#";
	
	
	
	public static final String ANNOTEA = "http://www.w3.org/2000/10/annotation-ns#";
	
	public static final String FOAF = "http://xmlns.com/foaf/0.1#";

	public static final String PAV = "http://purl.org/pav/";

	public static final String OBO = "http://purl.obolibrary.org/obo#";
	
	public static final String XSD = "http://www.w3.org/TR/xmlschema-2/#";
	
	public static final String D2S_EXAMPLE = "http://www.data2semantics.org/example/";

	public static final String D2S_SOURCEDOC = D2S_EXAMPLE+"sourcedocs/";
	
	public static final String D2S_PREFIX_SELECTOR = D2S_EXAMPLE+"prefixpostfixtextselector/";
	
	
	public static final String D2S_DOCS = D2S_EXAMPLE+"docs/";

	
	
	private void setupNameSpace() {
		
		try {
			docWriter.setNamespace("ao",   D2S_AnnotationOntology.AO_CORE);
			docWriter.setNamespace("aof",  D2S_AnnotationOntology.AO_FOAF);
			docWriter.setNamespace("aot",  D2S_AnnotationOntology.AO_TYPE);
			docWriter.setNamespace("aos",  D2S_AnnotationOntology.AO_SELECTOR);
			
			docWriter.setNamespace("pav",  D2S_AnnotationOntology.PAV);
			docWriter.setNamespace("ann",  D2S_AnnotationOntology.ANNOTEA);
			docWriter.setNamespace("pro",  D2S_AnnotationOntology.OBO);
			docWriter.setNamespace("foaf", D2S_AnnotationOntology.FOAF);
			
				
		} catch (IOException e) {
			log.error("Failed to setup namespaces for Annotation Ontology");
		}
	}

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
	
	
   public static Literal dateToLiteral(Date date) {

           LiteralImpl timeLiteral = new LiteralImpl(date.toString(), new URIImpl(XSD, "dateTime"));
           return timeLiteral;
   }
	
	/**
	 * Add file header statements
	 * @param files
	 */
	public void addFiles(List <File> files){
			
		for(File curFile : files){
			String fileName = curFile.getName().replaceAll(" ","_");
			try {
				
				docWriter.writeStatement(new URIImpl(D2S_SOURCEDOC+fileName), URIImpl.RDF_TYPE, 
										 new URIImpl(PAV+"SourceDocument"));
			
				docWriter.writeStatement(new URIImpl(D2S_SOURCEDOC+fileName), new URIImpl(PAV+"retrievedFrom"), 
						 new URIImpl(D2S_DOCS+fileName));
				
				docWriter.writeStatement(new URIImpl(D2S_SOURCEDOC+fileName), new URIImpl(PAV+"sourceAccessedOn"), 
						 dateToLiteral(new Date()));
				

			} catch (IOException e) {
				log.error("Failed to add document "+fileName);
			}
			
		}
		
	}
	
	/**
	 * This function will add annotation ontology found in PDF.
	 * @param mainTerm
	 * @param prefix
	 * @param postfix
	 * @param fileName
	 * @param annotation
	 * @param position
	 */
	public void addAnnotation(String mainTerm, String prefix, String postfix, String fileName, 
			String annotation, String position, String page_nr, String chunk_nr, String termLocation){
			
			String selectorID = fileName+"_"+page_nr+"_"+chunk_nr+"_"+termLocation;
			try{
				
				// Ideally I think these two more general selector should be inferred, not explicitly stated here.
				docWriter.writeStatement(new URIImpl(D2S_PREFIX_SELECTOR+selectorID), URIImpl.RDF_TYPE, 
										 new URIImpl(AO_CORE,"Selector"));

				docWriter.writeStatement(new URIImpl(D2S_PREFIX_SELECTOR+selectorID), URIImpl.RDF_TYPE, 
						 new URIImpl(AO_SELECTOR+"TextSelector"));
	
				docWriter.writeStatement(new URIImpl(D2S_PREFIX_SELECTOR+selectorID), URIImpl.RDF_TYPE, 
						 new URIImpl(AO_SELECTOR,"PrefixPostFixSelector"));
				
				docWriter.writeStatement(new URIImpl(D2S_PREFIX_SELECTOR+selectorID), 
						new URIImpl(AO_SELECTOR,"exact"),
						new LiteralImpl(mainTerm));
				
				docWriter.writeStatement(new URIImpl(D2S_PREFIX_SELECTOR+selectorID), 
						new URIImpl(AO_SELECTOR,"prefix"),
						new LiteralImpl(prefix));
				
				docWriter.writeStatement(new URIImpl(D2S_PREFIX_SELECTOR+selectorID), 
						new URIImpl(AO_SELECTOR,"postfix"),
						new LiteralImpl(postfix));
				
				
	
			}catch(IOException e){
				log.error("Failed to add term "+mainTerm+ "  "+prefix + "  "+postfix + " "+fileName);
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
