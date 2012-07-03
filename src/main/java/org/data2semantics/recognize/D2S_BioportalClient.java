package org.data2semantics.recognize;

import info.aduna.xml.XMLReaderFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.data2semantics.filters.D2S_FilterAnnotationBeans;
import org.data2semantics.filters.D2S_FilterHeaderAnnotation;
import org.data2semantics.filters.D2S_PrefixShiftXMLFilter;
import org.data2semantics.util.D2S_Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

import edu.stanford.nlp.io.StringOutputStream;

public class D2S_BioportalClient {
	public static final String annotatorUrl = "http://rest.bioontology.org/obs/annotator";
	DefaultHttpClient client  = null;
	HttpPost method = null;
	List <NameValuePair> params = null;
	
	private Logger log = LoggerFactory.getLogger(D2S_BioportalClient.class);

	public D2S_BioportalClient() {
		initialize();
	}
	public void initialize(){
		client = new DefaultHttpClient();
		method = new HttpPost(annotatorUrl);
		params = new ArrayList<NameValuePair>();
		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
				"D2S Bioportal - Annotator - Client");

		 
		client.getParams().setParameter("http.socket.timeout", new Integer(20000));
		method.getParams().setParameter("http.socket.timeout", new Integer(20000));

		
		// Configure the form parameters
		params.add(new BasicNameValuePair("longestOnly", "false"));
		params.add(new BasicNameValuePair("wholeWordOnly", "true"));
		params.add(new BasicNameValuePair("filterNumber", "true"));
		params.add(new BasicNameValuePair("stopWords", ""));
		params.add(new BasicNameValuePair("withDefaultStopWords", "true"));
		params.add(new BasicNameValuePair("isTopWordsCaseSensitive", "false"));
		params.add(new BasicNameValuePair("mintermSize", "3"));
		params.add(new BasicNameValuePair("scored", "true"));
		params.add(new BasicNameValuePair("withSynonyms", "true"));
		params.add(new BasicNameValuePair("ontologiesToExpand", ""));
		params.add(new BasicNameValuePair("ontologiesToKeepInResult", ""));
		params.add(new BasicNameValuePair("isVirtualOntologyId", "true"));
		params.add(new BasicNameValuePair("semanticTypes", ""));
		params.add(new BasicNameValuePair("levelMax", "0"));
		params.add(new BasicNameValuePair("mappingTypes", "null")); // null, Automatic
		params.add(new BasicNameValuePair("apikey", "4598fcf2-613c-4a36-af4f-98faa1e24a56"));
		
	}

	

	private HttpEntity callBioportal() throws IOException,
			ClientProtocolException, InterruptedException, Exception {
		StatusLine status;
		HttpResponse response;
		HttpEntity entity;
		int tries = 0;
		
		do {
			log.info("Contacting BioPortal..");
			response = client.execute(method);
			entity = response.getEntity();
			status = response.getStatusLine();
			log.info(status.toString());
			if (entity != null && status.getStatusCode() != 200) {
				EntityUtils.consume(entity);
				log.info("Retrying in 2 secs...");
				Thread.sleep(2000);
			}
			tries += 1;
		} while (status.getStatusCode() != 200 && tries < 5 ); 
		
		if (tries == 10) throw new Exception("Could not retrieve annotation from BioPortal Annotator, even after 5 tries!");
		
		
		return entity;
	}
	
	/**
	 * 
	 * This is where you annotate a text and get the result. 
	 * @param text
	 * @param format : can be 'xml', text or 'tabDelimited'
	 * @return
	 */
	
	public String annotateText(String text, String format) {

		params.add(new BasicNameValuePair("textToAnnotate",text));
		
		params.add(new BasicNameValuePair("format", format)); // Options are 'text', 'xml',
												// 'tabDelimited'
        method.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));

		
		StringBuffer result = new StringBuffer();
		
		try {

			HttpEntity entity = callBioportal();
			log.info("Retrieving results from BioPortal Annotator");
			
			if (entity != null) {
				InputStream contentStream = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(contentStream));
				String line = reader.readLine();
				while(line != null){
					result.append(line);
					line = reader.readLine();
				}
				
				client.getConnectionManager().shutdown();
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}

	
	public void annotateToFile(String text, String format, File outputFile){
		String splitted[] = text.split(" ");
		
		// Assuming document with 10000 words still can be handled by Bioportal
		if(splitted.length > 10000){
			log.info("Document containing "+splitted.length+" words, proceed with split, annotate and merge ");
			splitAnnotateAndMerge(text, format, outputFile, 5000);
			return;
		}
		
		params.add(new BasicNameValuePair("textToAnnotate",text));
		
		params.add(new BasicNameValuePair("format", format)); // Options are 'text', 'xml',
												// 'tabDelimited'
		
		method.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
		
		try {

			HttpEntity entity = callBioportal();
			log.info("Retrieving results from BioPortal Annotator");
			
			OutputStream outputStream =null;
			InputStream contentStream =null;
			
			
			
			log.info("Retrieving "+ entity.getContentLength() + " bytes");
			
			if (entity != null) {
				contentStream = entity.getContent();
				outputStream = new FileOutputStream(outputFile);
				
				int len;
				byte buf[] = new byte[1024];
				while((len=contentStream.read(buf))>0){
					outputStream.write(buf,0,len);
				}
				
			}
			
			client.getConnectionManager().shutdown();
			
			outputStream.close();
			contentStream.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}



	public void splitAnnotateAndMerge(String longText, String format,
			File outputSplitMerge, int wordCountPerSplit) {

		String[] splits = splitLongText(longText, wordCountPerSplit);
		
		log.info("Splitting the documents into " +splits.length+" splits. Each containing "+wordCountPerSplit+" words (not characters). ");
		// Accumulative length of previous splits to offset the prefix of
		// current result
		int splitOffset = 0;
		
		// With additional original header
		File[] headerAndAnnotatedBeans = new File[splits.length+1];

		try {
			for (int countSplit = 0; countSplit < splits.length; countSplit++) {

				// Maybe I need to think of another way to do this instead of
				// initializing every time
				initialize();

				log.info("Annotating split number " + countSplit
						+ " with character offset " + splitOffset);

				File bioportalOutput = File.createTempFile("annotation-" + countSplit+"-", ".xml");

				// Annotate current split
				annotateToFile(splits[countSplit], "xml", bioportalOutput);

				// Fix the prefix and suffix, update according to the length of
				// accumulated previous split.

				log.info("Shifting prefix and suffix according to accumulated splitOffset");
				File shiftedAnnotatedSplit = File.createTempFile("shiftedAnnotation-" + countSplit+"-", ".xml");

				shiftAnnotatedSplitPrefixAndPostfix(bioportalOutput,
						splitOffset, shiftedAnnotatedSplit);

				// Only for the first split, initialize the relevant header and
				// modify the textToAnnotatePart with original text
				if (countSplit == 0) {
					log.info("Extracting relevant header");
					headerAndAnnotatedBeans[0] = File.createTempFile("header" + countSplit+"-", ".xml");
					extractRelevantHeader(shiftedAnnotatedSplit, longText,	headerAndAnnotatedBeans[0]);
				}

				// Merging corrected split, do I need to fix anything else
				// besides the from/to
				log.info("Extracting annotation beans");
				headerAndAnnotatedBeans[countSplit+1] = File.createTempFile("annotatedbeans" + countSplit+"-", ".xml");
				extractAnnotationBeans(shiftedAnnotatedSplit,	headerAndAnnotatedBeans[countSplit+1]);

				// Don't forget to update current split
				splitOffset += splits[countSplit].length();
				
				D2S_Utils.sleep(1000);
				bioportalOutput.delete();
				shiftedAnnotatedSplit.delete();
			}
		} catch (IOException e) {

		}

		log.info("Merging annotation results, directly writing to file");

		mergeAnnotationResults(headerAndAnnotatedBeans, outputSplitMerge);

	}


	private void mergeAnnotationResults(File[] annotationResults, File outputSplitMerge) {

		FileOutputStream fos = null;
		log.info("Merging annotation result");
		try {
			fos = new FileOutputStream(outputSplitMerge);	
			StreamResult finalHandler = new StreamResult(fos);

			SAXTransformerFactory transformerFactory = (SAXTransformerFactory) TransformerFactory
					.newInstance();
			
			TransformerHandler serializer;

			serializer = transformerFactory.newTransformerHandler();

			serializer.setResult(finalHandler);

			Transformer t = serializer.getTransformer();
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			serializer.startDocument();
			serializer.startElement("", "annotatorResultBean", "annotatorResultBean",null);
			for(int i=0;i<annotationResults.length;i++){
				log.info("Merging file "+annotationResults[i].getAbsolutePath());
				XMLReader reader = XMLReaderFactory.createXMLReader();
				reader.setContentHandler(serializer);
				FileInputStream inputStream =new FileInputStream(annotationResults[i]); 
				reader.parse(new InputSource(inputStream));
				inputStream.close();
			}
			serializer.endElement("", "annotatorResultBean", "annotatorResultBean");
			serializer.endDocument();
		
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(fos != null)
				try {
					fos.close();
				} catch (IOException e) {

				}
		}

	
	}
	private void  extractRelevantHeader(File correctedSplit, String originalText, File filteredOutput) {
	    XMLFilter headerFilter = new D2S_FilterHeaderAnnotation(originalText);
		 applyXMLFilter(correctedSplit, headerFilter, filteredOutput);
	}
	
	private void  extractAnnotationBeans(File correctedSplit, File filteredOutput) {
		XMLFilter annotationBeanFilter = new D2S_FilterAnnotationBeans();
		
		applyXMLFilter(correctedSplit, annotationBeanFilter, filteredOutput);
	}
	/**
	 * This function should look for <from> <to> tag from existing string and replace it with the correct prefix.
	 * We want also to strip the header containing text to Annotate
	 * @param annotatedSplit
	 * @param splitOffset
	 */
	private void  shiftAnnotatedSplitPrefixAndPostfix(File annotatedSplit,
			int splitOffset, File shiftedOutput) {
			XMLFilter shiftAndAnnotateFilter = new D2S_PrefixShiftXMLFilter(splitOffset);
			applyXMLFilter(annotatedSplit, shiftAndAnnotateFilter, shiftedOutput);
	}
	
	private void applyXMLFilter(File inputXMLFile,
			XMLFilter filterTobeApplied, File filteredOutput)
			throws TransformerFactoryConfigurationError {
		
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		try {
			inputStream = new FileInputStream(inputXMLFile);
			InputSource inputSource  = new InputSource(inputStream);
			 outputStream = new FileOutputStream(filteredOutput);
			
			StreamResult finalResultHandler =  new StreamResult(outputStream);
			
			//This is the writer that will generate the final xml output.
			SAXTransformerFactory transformerFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
			TransformerHandler serializer = transformerFactory.newTransformerHandler();
			serializer.setResult(finalResultHandler);

			Transformer t = serializer.getTransformer();
			t.setOutputProperty(OutputKeys.INDENT,"yes");
			t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			XMLReader reader = XMLReaderFactory.createXMLReader();
			filterTobeApplied.setParent(reader);
			filterTobeApplied.setContentHandler(serializer);	
			
			filterTobeApplied.parse (inputSource);
	
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
				outputStream.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}



	private String[] splitLongText(String longText, int wordCountPerSplit) {
		
		Vector<	String > result = new Vector<String>();

		String [] words = longText.split(" ");
	
		for(int i=0;i<words.length;i++){
			StringBuffer currentSplit = new StringBuffer();
			for(int j=0;j<wordCountPerSplit && i < words.length;j++,i++){
				currentSplit.append(' '); 
				currentSplit.append(words[i]);
			}
			result.add(currentSplit.toString());
		}
		
		return result.toArray(new String[result.size()]);
	}

}
