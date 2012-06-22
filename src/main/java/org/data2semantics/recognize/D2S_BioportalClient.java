package org.data2semantics.recognize;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

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

public class D2S_BioportalClient {
	public static final String annotatorUrl = "http://rest.bioontology.org/obs/annotator";
	DefaultHttpClient client  = new DefaultHttpClient();
	HttpPost method = new HttpPost(annotatorUrl);
	List <NameValuePair> params = new ArrayList <NameValuePair>();

	public D2S_BioportalClient() {

		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
				"D2S Bioportal - Annotator - Client");

		 


		
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
			System.out.println("Contacting BioPortal..");
			response = client.execute(method);
			entity = response.getEntity();
			status = response.getStatusLine();
			System.out.println(status);
			if (entity != null && status.getStatusCode() != 200) {
				EntityUtils.consume(entity);
				System.out.println("Retrying in 2 secs...");
				Thread.sleep(2000);
			}
			tries += 1;
		} while (status.getStatusCode() != 200 && tries < 10 ); 
		
		if (tries == 10) throw new Exception("Could not retrieve annotation from BioPortal Annotator");
		
		
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
			System.out.println("Retrieving results from BioPortal Annotator");
			
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
		params.add(new BasicNameValuePair("textToAnnotate",text));
		
		params.add(new BasicNameValuePair("format", format)); // Options are 'text', 'xml',
												// 'tabDelimited'
		
		method.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
		
		try {

			HttpEntity entity = callBioportal();
			System.out.println("Retrieving results from BioPortal Annotator");
			
			OutputStream outputStream =null;
			InputStream contentStream =null;
			
			
			
			System.out.println("Retrieving "+ entity.getContentLength() + " bytes");
			
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

}
