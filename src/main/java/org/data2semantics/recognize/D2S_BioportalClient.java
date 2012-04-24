package org.data2semantics.recognize;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class D2S_BioportalClient {
	public static final String annotatorUrl = "http://rest.bioontology.org/obs/annotator";
	HttpClient client = new HttpClient();
	PostMethod method = new PostMethod(annotatorUrl);

	public D2S_BioportalClient() {
		HttpClient client = new HttpClient();

		client.getParams().setParameter(HttpMethodParams.USER_AGENT,
				"D2S Bioportal - Annotator - Client");

		// Configure the form parameters
		method.addParameter("longestOnly", "true");
		method.addParameter("wholeWordOnly", "true");
		method.addParameter("filterNumber", "true");
		method.addParameter("stopWords", "");
		method.addParameter("withDefaultStopWords", "true");
		method.addParameter("isTopWordsCaseSensitive", "false");
		method.addParameter("mintermSize", "3");
		method.addParameter("scored", "true");
		method.addParameter("withSynonyms", "true");
		method.addParameter("ontologiesToExpand", "");
		method.addParameter("ontologiesToKeepInResult", "");
		method.addParameter("isVirtualOntologyId", "true");
		method.addParameter("semanticTypes", "");
		method.addParameter("levelMax", "0");
		method.addParameter("mappingTypes", "null"); // null, Automatic
		method.addParameter("apikey", "4598fcf2-613c-4a36-af4f-98faa1e24a56");
		
	}

	/**
	 * 
	 * This is where you annotate a text and get the result. 
	 * @param text
	 * @param format : can be 'xml', text or 'tabDelimited'
	 * @return
	 */
	
	public String annotateText(String text, String format) {

		method.addParameter("textToAnnotate",text);
		
		method.addParameter("format", format); // Options are 'text', 'xml',
												// 'tabDelimited'
		
		StringBuffer result = new StringBuffer();

		try {

			int statusCode = client.executeMethod(method);

			if (statusCode != -1) {
				InputStream contentStream = method.getResponseBodyAsStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(contentStream));
				String line = reader.readLine();
				while(line != null){
					result.append(line);
					line = reader.readLine();
				}
				
				method.releaseConnection();
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}

}
