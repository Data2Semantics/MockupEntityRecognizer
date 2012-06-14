package org.data2semantics.recognize;

import org.openrdf.model.impl.URIImpl;

public class Vocab {

	
	/*
	 * The Annotation Ontology namespaces
	 */
	
	public static final String AO = "http://purl.org/ao/";
	
	public static final String AO_CORE = AO + "core#";

	public static final String AO_FOAF = AO + "foaf#";

	public static final String AO_TYPE = AO + "types#";
	
	public static final String AO_SELECTOR = AO + "selectors#";
	
	
	public static URIImpl ao(String suffix) {
		return new URIImpl(AO_CORE + suffix);
	}
	
	public static URIImpl aof(String suffix) {
		return new URIImpl(AO_FOAF + suffix);
	}
	
	public static URIImpl aot(String suffix) {
		return new URIImpl(AO_TYPE + suffix);
	}
	
	public static URIImpl aos(String suffix) {
		return new URIImpl(AO_SELECTOR + suffix);
	}
	
	
	/*
	 * The Open Annotation namespaces
	 */
	
	public static final String OA = "http://www.w3.org/ns/openannotation/core/";
	
	public static final String OAX = "http://www.w3.org/ns/openannotation/extension/";
	
	
	public static URIImpl oa(String suffix) {
		return new URIImpl(OA + suffix);
	}
	
	public static URIImpl oax(String suffix) {
		return new URIImpl(OAX + suffix);
	}	
	

	
	/*
	 * Various namespaces
	 */
	
	public static final String ANNOTEA = "http://www.w3.org/2000/10/annotation-ns#";
	
	public static final String FOAF = "http://xmlns.com/foaf/0.1#";

	public static final String PAV = "http://purl.org/pav/";

	public static final String OBO = "http://purl.obolibrary.org/obo#";
	
	public static final String XSD = "http://www.w3.org/TR/xmlschema-2/#";
	
	public static URIImpl annotea(String suffix) {
		return new URIImpl(ANNOTEA + suffix);
	}
	
	public static URIImpl pav(String suffix) {
		return new URIImpl(PAV + suffix);
	}
	public static URIImpl foaf(String suffix) {
		return new URIImpl(FOAF + suffix);
	}
	public static URIImpl obo(String suffix) {
		return new URIImpl(OBO + suffix);
	}
	public static URIImpl xsd(String suffix) {
		return new URIImpl(XSD + suffix);
	}
	
	public static URIImpl d2s(String suffix){
		return new URIImpl(D2S + suffix);
	}
	
	/*
	 * The D2S namespace
	 */
	
	public static final String D2S = "http://annotations.data2semantics.org/resource/";
	
	public static URIImpl doc(String suffix){
		return d2s("doc/"+suffix);
	}
	
	public static URIImpl selector(String suffix){
		return d2s("selector/"+suffix);
	}	
	
	public static URIImpl annotation(String suffix){
		return d2s("annotation/"+suffix);
	}
	
	public static URIImpl state(String suffix){
		return d2s("state/"+suffix);
	}
	
	public static URIImpl target(String suffix){
		return d2s("target/"+suffix);
	}
	
	public static URIImpl annotator(String suffix){
		return d2s("D2SAnnotator/"+suffix);
	}
	
}
