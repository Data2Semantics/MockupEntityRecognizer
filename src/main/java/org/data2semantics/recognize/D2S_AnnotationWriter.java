package org.data2semantics.recognize;

import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

public interface D2S_AnnotationWriter {

	
	public void startWriting();
	public void stopWriting();
	public void addFiles(List<File> files);
	public void addFileAndURLs(List<File> files, HashMap<String, String> originalURL) ;
	public void addAnnotation(D2S_Annotation curAnnotation);
	public OutputStream getOS();
}
