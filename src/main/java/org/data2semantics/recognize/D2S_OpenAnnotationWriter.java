package org.data2semantics.recognize;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.turtle.TurtleWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.data2semantics.util.RepositoryWriter;
import org.data2semantics.util.Vocab;

public class D2S_OpenAnnotationWriter implements D2S_AnnotationWriter {

	private Logger log = LoggerFactory.getLogger(D2S_OpenAnnotationWriter.class);
	

	private String outputFile;
	private ValueFactory vf;
	private Vocab vocab;
	private String timestamp;
	private Repository repo;
	
	public D2S_OpenAnnotationWriter(String outputFile) throws RepositoryException {
		this.outputFile = outputFile;
		
		repo = new SailRepository(new MemoryStore());
		repo.initialize();
		

		
		vf = repo.getValueFactory();
		vocab = new Vocab(vf);
		
		// Make sure that we have a single timestamp for the entire run (since URIs are timestamp specific)
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		 
		timestamp = sdf.format(new Date());
	}  
	
	

	
	public void startWriting() {
		// I won't actually start writing here: the graph is serialized once stopWriting is called.
		
//		try {
//			docWriter.startRDF();
//		} catch (RDFHandlerException e) {
//			log.error("Failed to start writing document");
//		}
	}

	public void stopWriting(){
		RepositoryWriter rw = new RepositoryWriter(repo, outputFile);
		
		rw.write();
		
	}


	public void addAnnotation(D2S_Annotation curAnnotation) {
		String exact="", prefix="", suffix="", source="", cachedSource = "", topic="";
		

		
		exact = curAnnotation.getPreferredName();
		prefix = curAnnotation.getPrefix();
		suffix = curAnnotation.getSuffix();
		source = curAnnotation.getOnDocument();
		cachedSource = curAnnotation.getSourceDocument();
		topic = curAnnotation.getTermFound();
		
		log.info("Source "+source);

		String exactDigest = DigestUtils.md5Hex(exact);
		
		Literal timestampLiteral = vf.createLiteral(timestamp.toString(), vocab.xsd("dateTime"));
		
		String cachedSourceID = timestamp + "/" + cachedSource;
		String stateID = cachedSource + "/" + exactDigest;
		String fragmentID = cachedSource + "/" + curAnnotation.getFrom() + "/"+curAnnotation.getTo() + "/" + exactDigest;
		
		
		URI annotationURI = vocab.annotation(fragmentID);

		
		try {
			RepositoryConnection con = repo.getConnection();

			try {
				if(con.hasStatement(annotationURI, null, null, true)) {
					// If we've already visited the annotation (the annotationURI contains the range of characters in the source file)
					// we retrieve the previously added semantic tag, and create a skos:exactMatch relation between the tags.
					
					RepositoryResult<Statement> statements = con.getStatements(annotationURI, vocab.oax("hasSemanticTag"), null, true);
					
					while (statements.hasNext()) {
						Statement s = statements.next();
						URI concept = (URI) s.getObject();
						
						Statement exactMatch = vf.createStatement(concept, vocab.skos("exactMatch"), vf.createURI(topic));
						
						con.add(exactMatch);
					}
					
					return;
				}
			} finally {
				con.close();
			}
		} catch (RepositoryException e) {
			log.error("Whoops, couldn't connect to repository");
			e.printStackTrace();
		}
//		if (graph.contains(annotationURI)) {
//			// If we've already visited the annotation (the annotationURI contains the range of characters in the source file)
//			// we can simply add the semantic tag, and return.
//			addTriple(annotationURI, vocab.oax("hasSemanticTag"), vf.createURI(topic));
//			return;
//		}
		
		URI selectorURI = vocab.selector(fragmentID);
		URI targetURI = vocab.target(fragmentID);
		URI stateURI = vocab.state(stateID);
		URI cachedSourceURI = vocab.doc(cachedSourceID);
		URI sourceURI = vf.createURI(source);
		

		
		/* Write the Annotation / Tag */
		
		addTriple(annotationURI, RDF.TYPE, vocab.oa("Annotation"));
		addTriple(annotationURI, RDF.TYPE, vocab.oax("Tag"));
		addTriple(annotationURI, vocab.oax("hasSemanticTag"), vf.createURI(topic));
		addTriple(annotationURI, vocab.oa("hasTarget"), targetURI);
		
		// Some provenance stuff
		addTriple(annotationURI, vocab.oa("generator"), vf.createURI("http://github.com/Data2Semantics/MockupEntityRecognizer"));
		addTriple(annotationURI, vocab.oa("generated"), timestampLiteral);
		addTriple(annotationURI, vocab.oa("modelVersion"), vf.createURI("http://www.openannotation.org/spec/core/20120509.html"));
		
		/* Write the Target */
		
		addTriple(targetURI, RDF.TYPE, vocab.oa("SpecificResource"));
		addTriple(targetURI, vocab.oa("hasState"), stateURI);
		addTriple(targetURI, vocab.oa("hasSelector"), selectorURI);
		addTriple(targetURI, vocab.oa("hasSource"), sourceURI);
		
		/* Write the State */
		
		addTriple(stateURI, RDF.TYPE, vocab.oa("State"));
		addTriple(stateURI, vocab.oa("cachedSource"), cachedSourceURI);
		addTriple(stateURI, vocab.oa("when"), timestampLiteral);
		
		/* Write the TextQuoteSelector */
		
		addTriple(selectorURI, RDF.TYPE, vocab.oax("TextQuoteSelector"));
		addTriple(selectorURI, vocab.oax("prefix"), vf.createLiteral(prefix));
		addTriple(selectorURI, vocab.oax("exact"), vf.createLiteral(exact));
		addTriple(selectorURI, vocab.oax("suffix"), vf.createLiteral(suffix));

	}
	
	private void addTriple(Resource subj, URI pred, Value obj ){
		Statement s = vf.createStatement(subj, pred, obj);
//		log.info(s);
		
		try {
			RepositoryConnection con = repo.getConnection();
			try {
				con.add(s);
			}  finally {
				con.close();
			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			log.error("Unable not add statement to repository:\n "+s);
			e.printStackTrace();
		}  catch (NullPointerException e) {
			log.error("Hmmm... nullpointer.");
			e.printStackTrace();
		}
		
	}





	
}
