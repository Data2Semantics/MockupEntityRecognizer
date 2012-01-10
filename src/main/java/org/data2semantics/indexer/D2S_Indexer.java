package org.data2semantics.indexer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
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
public class D2S_Indexer {

	private Log log = LogFactory.getLog(D2S_Indexer.class);

	Directory theIndex ;

	IndexWriter indexWriter=null;
	IndexReader indexReader=null;
	
	
	StandardAnalyzer analyzer;

	public D2S_Indexer() throws IOException {
		theIndex = new RAMDirectory();
		analyzer = new StandardAnalyzer(Version.LUCENE_35);
	
	}
	
	IndexWriterConfig writerConfig = null;
	
	public void startAddingFiles() throws IOException {

		writerConfig = new IndexWriterConfig(Version.LUCENE_35, analyzer);

		try {
			indexWriter = new IndexWriter(theIndex, writerConfig);
		} catch (CorruptIndexException e) {
			throw new IOException("Index is corrupted, failed to open");
		} catch (LockObtainFailedException e) {
			throw new IOException("No lock obtained, failed to open");
		}

	}
	
	public void stopAddingFiles() throws CorruptIndexException, IOException{
		indexWriter.close();
	}
	
	public void addPDFDocument(File pdfFile) throws IOException {
		Document luceneDocument = null;
		try {
			luceneDocument = LucenePDFDocument.getDocument(pdfFile);
			String textContent = new D2S_PDFHandler(pdfFile).getStrippedTextContent();
			luceneDocument.add(new Field("contents", textContent, Field.Store.YES, Field.Index.ANALYZED));
			luceneDocument.add(new Field("filename", pdfFile.getName(), Field.Store.YES, Field.Index.ANALYZED));
			
		
			indexWriter.addDocument(luceneDocument);
		} catch (IOException e) {
			if (luceneDocument == null)
				throw new IOException(
						"Failed to create Lucene Document from pdf file");
			throw new IOException("Failed to add lucene Document to Index");
		}
	}
	public void addPDFDirectoryToIndex(String directory) {
		File publicationDir = new File(directory);
		
		File[] pubFiles  = publicationDir.listFiles();
		assert(pubFiles != null);
		
		try {

			startAddingFiles();
			
			for(File currentPDF : pubFiles){
				if(currentPDF.getName().endsWith(".pdf")){
					addPDFDocument(currentPDF);
				}
			}
			stopAddingFiles();
		} catch(Exception e){
			log.error("Failed to add pdf files to indexes");
		}
	}
	public int getNumberOfFiles(){
		try {
			String[] docs =theIndex.listAll();
			return docs.length;
		} catch (Exception e) {
			return 0;
		}
	}
	

/**
 * 
 * Simple search of string within some field in the current index.
 * 
 * @param query string that you wanted to search
 * @param field from which you will search the index.
 * @return
 * @throws CorruptIndexException
 * @throws IOException
 * @throws ParseException
 */
	public Document[] simpleStringSearch(String query, String field)
			throws CorruptIndexException, IOException, ParseException {
		
		IndexSearcher searcher = null;
		Query simpleQuery = null;
		QueryParser p;
		
		// This is needed for multiple word query Am I allowed to do this? What is the proper form of query?
		query = "\"" + query + "\"";
		simpleQuery = new QueryParser(Version.LUCENE_35, field, analyzer).parse(query);

		//We have much fewer documents, this should not matter.
		int hitsPerPage = 50;
		indexReader = IndexReader.open(theIndex);

		searcher = new IndexSearcher(indexReader);

		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(simpleQuery, collector);
		
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		List<Document> results = new Vector<Document>();
		
		for(int i=0;i<hits.length;i++){
			if(hits[i].score > 0.1)
				results.add(searcher.doc(hits[i].doc));
			if(bestScore < hits[i].score){
				bestScore = hits[i].score;
				bestDoc = hits[i];
				bestTerm = query;
			}
		}
		
		Document ret[] = new Document[results.size()];
		for(int i=0;i<results.size();i++)
			ret[i] = results.get(i);
		return ret;
	}
	public static String bestTerm;
	public static ScoreDoc bestDoc;
	public static float bestScore=0;
	// TODO, test to handle multiple PDF's, perform some searching.
	// Think about how to store the result found when searching indexes.
}
