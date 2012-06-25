package org.data2semantics.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.turtle.TurtleWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryWriter {

	private Logger log = LoggerFactory.getLogger(RepositoryWriter.class);

	private Writer writer;
	private TurtleWriter docWriter;
	private Vocab vocab;
	private Repository repo;

	public RepositoryWriter(Repository repo, Writer writer) {
		this.vocab = new Vocab(repo.getValueFactory());
		this.repo = repo;
		this.writer = writer;
		

		docWriter = new TurtleWriter(writer);

	}

	private void handleNamespaces() {
		try {
			docWriter.handleNamespace("oa", vocab.OA);
			docWriter.handleNamespace("oax", vocab.OAX);
			docWriter.handleNamespace("skos", vocab.SKOS);
			docWriter.handleNamespace("d2s", vocab.D2S);
		} catch (RDFHandlerException e) {
			log.error("Failed to handle namespaces for Open Annotation model");
		}
	}

	public void write() {
		try {
			docWriter.startRDF();
		} catch (RDFHandlerException e) {
			log.error("Failed to start writing document");
			return;
		}

		handleNamespaces();

		try {
			RepositoryConnection con = repo.getConnection();
			try {
				RepositoryResult<Statement> statementIterator = con
						.getStatements(null, null, null, true);

				while (statementIterator.hasNext()) {
					Statement s = statementIterator.next();
					try {
						docWriter.handleStatement(s);
					} catch (RDFHandlerException e) {
						// TODO Auto-generated catch block
						log.error("Unable to handle statement:\n" + s);
						e.printStackTrace();
					}
				}
			} finally {
				con.close();
			}
		} catch (RepositoryException e) {
			log.error("Could not iterate over all statements in repository");
			e.printStackTrace();
		}

		try {
			docWriter.endRDF();
			writer.close();
		} catch (IOException e) {
			log.error("Failed to stop writing document");
		} catch (RDFHandlerException e) {
			log.error("Failed to write document");
		}

	}
}
