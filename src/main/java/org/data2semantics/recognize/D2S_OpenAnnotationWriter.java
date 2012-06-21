package org.data2semantics.recognize;



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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.data2semantics.util.RepositoryWriter;
import org.data2semantics.util.Vocab;

public class D2S_OpenAnnotationWriter implements D2S_AnnotationWriter {

	private Logger log = LoggerFactory.getLogger(D2S_OpenAnnotationWriter.class);
	

	private String outputFile;
	private ValueFactory vf;
	private Vocab vocab;
	private Repository repo;
	private String annotationTimestamp, snapshotTimestamp;
	
	public D2S_OpenAnnotationWriter(String outputFile, String annotationTimestamp, String snapshotTimestamp) throws RepositoryException {
		this.outputFile = outputFile;
		
		repo = new SailRepository(new MemoryStore());
		repo.initialize();
		
		this.annotationTimestamp = annotationTimestamp;
		this.snapshotTimestamp = snapshotTimestamp;

		
		vf = repo.getValueFactory();
		vocab = new Vocab(vf);
		

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
		
		
		// The target digest is unique for the target of every annotation (prefix, exact, suffix)
		String targetDigest = DigestUtils.md5Hex(prefix + exact + suffix);
		// Make sure that we have a digest for the annotation that encodes both the body (topic) and the target (prefix, exact, suffix) of the annotation
		String annotationDigest = DigestUtils.md5Hex(topic + prefix + exact + suffix);
		
		Literal annotationTimestampLiteral = vf.createLiteral(annotationTimestamp, vocab.xsd("dateTime"));
		Literal snapshotTimestampLiteral = vf.createLiteral(snapshotTimestamp, vocab.xsd("dateTime"));
		
		
		String cachedSourceID = snapshotTimestamp + "/" + cachedSource;
		String stateID = source.substring(7) + "/" + snapshotTimestamp;
		String fragmentID = source.substring(7) + "/" + snapshotTimestamp + "/" + targetDigest ;
		String annotationID = source.substring(7) + "/" + annotationTimestamp + "/" + annotationDigest;
		
		URI annotationURI = vocab.annotation(annotationID);

		
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
		addTriple(annotationURI, vocab.oa("generated"), annotationTimestampLiteral);
		addTriple(annotationURI, vocab.oa("modelVersion"), vf.createURI("http://www.openannotation.org/spec/core/20120509.html"));
		
		/* Write the Target */
		
		addTriple(targetURI, RDF.TYPE, vocab.oa("SpecificResource"));
		addTriple(targetURI, vocab.oa("hasState"), stateURI);
		addTriple(targetURI, vocab.oa("hasSelector"), selectorURI);
		addTriple(targetURI, vocab.oa("hasSource"), sourceURI);
		
		/* Write the State */
		
		addTriple(stateURI, RDF.TYPE, vocab.oa("State"));
		addTriple(stateURI, vocab.oa("cachedSource"), cachedSourceURI);
		addTriple(stateURI, vocab.oa("when"), snapshotTimestampLiteral);
		
		/* Write the TextQuoteSelector */
		
		addTriple(selectorURI, RDF.TYPE, vocab.oax("TextQuoteSelector"));
		addTriple(selectorURI, vocab.oax("prefix"), vf.createLiteral(prefix, "en"));
		addTriple(selectorURI, vocab.oax("exact"), vf.createLiteral(exact, "en"));
		addTriple(selectorURI, vocab.oax("suffix"), vf.createLiteral(suffix, "en"));
		
		addTriple(selectorURI, RDF.TYPE, vocab.oax("TextOffsetSelector"));
		addTriple(selectorURI, vocab.oax("offset"), vf.createLiteral(curAnnotation.getFrom()));
		addTriple(selectorURI, vocab.oax("range"), vf.createLiteral(curAnnotation.getTo()-curAnnotation.getFrom()));


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
