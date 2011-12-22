package org.data2semantics.indexer;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.apache.pdfbox.lucene.LucenePDFDocument;

/**
 * First attempt to have a lucene index of existing pdf guidelines. Currently
 * this is a RAM index, later on we switch to something more sensible.
 * 
 * @author wibisono
 * 
 */
public class D2SIndexes {

	Directory theIndex ;

	IndexWriter indexWriter;
	IndexReader indexReader;

	StandardAnalyzer analyzer;

	public D2SIndexes() throws IOException {
		theIndex = new RAMDirectory();
		analyzer = new StandardAnalyzer(Version.LUCENE_35);
		IndexWriterConfig writerConfig = new IndexWriterConfig(
				Version.LUCENE_35, analyzer);

		try {
			indexWriter = new IndexWriter(theIndex, writerConfig);
		} catch (CorruptIndexException e) {
			throw new IOException("Index is corrupted, failed to open");
		} catch (LockObtainFailedException e) {
			throw new IOException("No lock obtained, failed to open");
		}
	}

	public void addPDFDocument(File pdfFile) throws IOException {
		Document luceneDocument = null;
		try {
			luceneDocument = LucenePDFDocument.getDocument(pdfFile);
			for(Fieldable f : luceneDocument.getFields())
				System.out.println(f + "  stored " + f.isStored());
			
			
			indexWriter.addDocument(luceneDocument);
		} catch (IOException e) {
			if (luceneDocument == null)
				throw new IOException(
						"Failed to create Lucene Document from pdf file");
			throw new IOException("Failed to add lucene Document to Index");
		}
		indexWriter.close();
	}

	public Document[] simpleStringSearch(String query, String field)
			throws CorruptIndexException, IOException, ParseException {
		
		IndexSearcher searcher = null;
		Query simpleQuery = null;

		simpleQuery = new QueryParser(Version.LUCENE_35, field, analyzer).parse(query);

		if (simpleQuery == null)
			return null;

		int hitsPerPage = 10;
		indexReader = IndexReader.open(theIndex);

		searcher = new IndexSearcher(indexReader);

		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(simpleQuery, collector);
		
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		Document[] results = new Document[hits.length];
		
		for(int i=0;i<hits.length;i++){
			results[i] = searcher.doc(hits[i].doc);
		}
		
		return results;
	}

	// TODO, test to handle multiple PDF's, perform some searching.
	// Think about how to store the result found when searching indexes.
}
