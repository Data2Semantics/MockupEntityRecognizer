package org.data2semantics.recognize;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.turtle.TurtleWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.data2semantics.recognize.Vocab;

public class D2S_OpenAnnotationWriter implements D2S_AnnotationWriter {

	private Log log = LogFactory.getLog(D2S_OpenAnnotationWriter.class);
	
	private TurtleWriter docWriter;
	private FileOutputStream outputStream = null;
	
	public D2S_OpenAnnotationWriter(String outputFile) {
		try {
			outputStream = new FileOutputStream(new File(outputFile));
		} catch (FileNotFoundException e) {
			log.error("Failed to create output file");
		}
		
		docWriter = new TurtleWriter(outputStream);

		
		handleNamespaces();
	}
	
	
	private void handleNamespaces() {
		try {
			docWriter.handleNamespace("oa",   Vocab.OA);
			docWriter.handleNamespace("oax",  Vocab.OAX);
		} catch (RDFHandlerException e) {
			log.error("Failed to handle namespaces for Open Annotation model");
		}
	}
	
	public void startWriting() {
		try {
			docWriter.startRDF();
		} catch (RDFHandlerException e) {
			log.error("Failed to start writing document");
		}
	}

	public void stopWriting(){
		try {
			docWriter.endRDF();
			outputStream.close();
		} catch (IOException e) {

			log.error("Failed to stop writing document");
		} catch (RDFHandlerException e) {
			log.error("Failed to write document");
		}
	}


	public void addAnnotation(D2S_Annotation curAnnotation) {
		String exact="", prefix="", suffix="", source="", cachedSource = "", topic="";
		
		exact = curAnnotation.getPreferredName();
		prefix = curAnnotation.getPrefix();
		suffix = curAnnotation.getSuffix();
		source = curAnnotation.getOnDocument();
		cachedSource = curAnnotation.getSourceDocument();
		topic = curAnnotation.getTermFound();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		 
		String timestamp = sdf.format(new Date());
		Literal timestampLiteral = new LiteralImpl(timestamp.toString(), Vocab.xsd("dateTime"));
		
		String cachedSourceID = timestamp + "/" + cachedSource;
		String stateID = cachedSource + "/" + timestamp;
		String fragmentID = cachedSource + "_" + curAnnotation.getFrom() + "_"+curAnnotation.getTo() + "/" + timestamp;
		
		URI annotationURI = Vocab.annotation(fragmentID);
		URI selectorURI = Vocab.selector(fragmentID);
		URI targetURI = Vocab.target(fragmentID);
		URI stateURI = Vocab.state(stateID);
		URI cachedSourceURI = Vocab.doc(cachedSourceID);
		URI sourceURI = new URIImpl(source);
		
		/* Write the Annotation / Tag */
		
		addTriple(annotationURI, RDF.TYPE, Vocab.oa("Annotation"));
		addTriple(annotationURI, RDF.TYPE, Vocab.oax("Tag"));
		addTriple(annotationURI, Vocab.oax("hasSemanticTag"), new URIImpl(topic));
		addTriple(annotationURI, Vocab.oa("hasTarget"), targetURI);
		
		// Some provenance stuff
		addTriple(annotationURI, Vocab.oa("generator"), new URIImpl("http://github.com/Data2Semantics/MockupEntityRecognizer"));
		addTriple(annotationURI, Vocab.oa("generated"), timestampLiteral);
		addTriple(annotationURI, Vocab.oa("modelVersion"), new URIImpl("http://www.openannotation.org/spec/core/20120509.html"));
		
		/* Write the Target */
		
		addTriple(targetURI, RDF.TYPE, Vocab.oa("SpecificResource"));
		addTriple(targetURI, Vocab.oa("hasState"), stateURI);
		addTriple(targetURI, Vocab.oa("hasSelector"), selectorURI);
		addTriple(targetURI, Vocab.oa("hasSource"), sourceURI);
		
		/* Write the State */
		
		addTriple(stateURI, RDF.TYPE, Vocab.oa("State"));
		addTriple(stateURI, Vocab.oa("cachedSource"), cachedSourceURI);
		addTriple(stateURI, Vocab.oa("when"), timestampLiteral);
		
		/* Write the TextQuoteSelector */
		
		addTriple(selectorURI, RDF.TYPE, Vocab.oax("TextQuoteSelector"));
		addTriple(selectorURI, Vocab.oax("prefix"), new LiteralImpl(prefix));
		addTriple(selectorURI, Vocab.oax("exact"), new LiteralImpl(exact));
		addTriple(selectorURI, Vocab.oax("suffix"), new LiteralImpl(suffix));

	}
	
	private void addTriple(Resource subj, URI pred, Value obj ){
		try {
			Statement s = new StatementImpl(subj,pred,obj);
			docWriter.handleStatement(s);
		} catch (RDFHandlerException e) {
			log.error("Failed to add statement: "+subj+" "+pred+" "+obj);
		}
	}

	public OutputStream getOS() {
		return outputStream;
	}



	
}
