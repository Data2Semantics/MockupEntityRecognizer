package org.data2semantics.util;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class PDFExtractor {

	    public String getText(File sourceFile) {
	        String result = "";
	    	try {
	            PDDocument doc = PDDocument.load(sourceFile);
	            PDFTextStripper source=new PDFTextStripper();
	            StringWriter target = new StringWriter();
	            source.writeText(doc, target);
	            result = target.toString();
	            target.close();
	            doc.close();
	        } catch (IOException ex) {
	        }
	        return result;
	    }
	
	public static void main(String[] args) throws IOException, COSVisitorException {
		
		//LucenePDFDocument lucenePDFReader = new LucenePDFDocument();
		File testFile = new File("src\\test\\resources\\neutropenia.pdf");
		System.out.println(new PDFExtractor().getText(testFile));
	}
}
