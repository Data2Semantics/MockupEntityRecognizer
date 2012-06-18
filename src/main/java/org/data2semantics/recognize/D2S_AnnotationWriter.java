package org.data2semantics.recognize;

import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

public interface D2S_AnnotationWriter {

	
	public void startWriting();
	public void stopWriting();
	public void addAnnotation(D2S_Annotation curAnnotation);

}
