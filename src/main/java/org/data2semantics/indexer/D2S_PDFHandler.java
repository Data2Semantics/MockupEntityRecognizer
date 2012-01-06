package org.data2semantics.indexer;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.lucene.LucenePDFDocument;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

/**
 * Handling PDF, getting the contents using text stripper.
 * 
 * @author wibisono
 * 
 */
public class D2S_PDFHandler {

	File sourceFile;
	PDDocument pdDocument;

	public D2S_PDFHandler(File sourceFile) {

		this.sourceFile = sourceFile;
		try {
			pdDocument = PDDocument.load(sourceFile);
		} catch (IOException e) {

		}

	}

	/**
	 * Getting stripped text content of the PDF file.
	 * 
	 * @return
	 */
	public String getStrippedTextContent() {
		String textContent = "";
		try {
			PDFTextStripper source = new PDFTextStripper();
			StringWriter target = new StringWriter();
			source.writeText(pdDocument, target);
			textContent = target.toString();
			target.close();
			pdDocument.close();
		} catch (IOException ex) {
		}
		return textContent;
	}

	/**
	 * @return the sourceFile
	 */
	public File getSourceFile() {
		return sourceFile;
	}

	/**
	 * @param sourceFile
	 *            the sourceFile to set
	 */
	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}

	public static void main(String[] args) throws IOException,
			COSVisitorException {

		File testFile = new File("src\\test\\resources\\neutropenia.pdf");
		System.out.println(new D2S_PDFHandler(testFile).getStrippedTextContent());
	}
}
