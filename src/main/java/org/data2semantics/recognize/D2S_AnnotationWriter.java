package org.data2semantics.recognize;

import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import org.openrdf.model.URI;

public interface D2S_AnnotationWriter {

	
	
	public void addAnnotation(D2S_Annotation curAnnotation);

	public String getAnnotationFileName();
	
	public String getAnnotationSourceLocation();
	
	public URI getDocumentURI();
	
	public Boolean hasAnnotations();
}
