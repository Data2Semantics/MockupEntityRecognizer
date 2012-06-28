package org.data2semantics.recognize;

import java.io.File;

import org.junit.Test;

/**
 * Testing implementation of split and merge of large files.
 * @author wibisono
 *
 */
public class D2S_BioportalClientSplitMergeTest {

		@Test 
		public void testSplitMergeBigFile(){
				
				String longText= "This is just some words fibral neutropenia, diagnosis, something illness blood pressure rising high in the sky " +
						"that I am going to use to test if they really can do 10 words per split, one test case usually is not enough but it is better than nothing.";
				//Initialize long text from big file, guideline
				
				String format = "xml";
				
				File outputWholeFile = new File("outputWholeFile");
				File outputSplitMerge = new File("outputSplitMerge");
				
				D2S_BioportalClient client = new D2S_BioportalClient();
				
				client.annotateToFile(longText, format, outputWholeFile);
				
				client = new D2S_BioportalClient();
				client.splitAnnotateAndMerge(longText, format, outputSplitMerge, 10);
		}
}
