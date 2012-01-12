package org.data2semantics.indexer;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.pdfbox.examples.pdmodel.Annotation;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageNode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDGamma;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;
import org.junit.Test;

public class D2S_ChunkedPDFStripperTest {
	File  fileTest				= new File("src\\test\\resources\\neutropenia.pdf");
	PDDocument doc;
	
	@Test
	public void testCharactersByArticle() throws IOException, COSVisitorException{
		D2S_ChunkedPDFStripper mStripper = new D2S_ChunkedPDFStripper();
		StringWriter writer = new StringWriter();
		doc = PDDocument.load(fileTest);
		mStripper.writeText(doc,writer);
		
		List<D2S_DocChunk> chunks = mStripper.getDocumentChunks();
		PDGamma yellow = new PDGamma();
		yellow.setR(1);
		yellow.setG(1);
		
		PDPageNode rootPage = doc.getDocumentCatalog().getPages();
        List<PDPage> pages = new ArrayList<PDPage>();
        rootPage.getAllKids(pages);
   
		for(D2S_DocChunk chunk : chunks){
			System.out.println(chunk);
			
			PDPage currentPage = pages.get(chunk.getPageNumber()-1);
            List <PDAnnotation> annotations = currentPage.getAnnotations();
            PDRectangle mediaBox = currentPage.getMediaBox();
            System.out.println(" Width : "+mediaBox.getWidth());
            System.out.println(" Height : "+mediaBox.getHeight());
            System.out.println(" Upper right : "+mediaBox.getUpperRightX()+","+mediaBox.getUpperRightY());
            System.out.println(" Lower left  : "+mediaBox.getLowerLeftX()+","+mediaBox.getLowerLeftY());
             
			PDAnnotationTextMarkup txtMark = new PDAnnotationTextMarkup(PDAnnotationTextMarkup.SUB_TYPE_HIGHLIGHT);
            txtMark.setColour(yellow); 
            txtMark.setConstantOpacity((float)0.2);   // Make the highlight 20% transparent

            // Slightly confused here, I have to mirror in Y axis
            PDRectangle position = new PDRectangle();
            position.setLowerLeftX(chunk.getLeft());
            position.setLowerLeftY(mediaBox.getHeight()-chunk.getTop());
            position.setUpperRightX(chunk.getRight());
            position.setUpperRightY(mediaBox.getHeight()-chunk.getBottom());
            
            float[] quads = new float[8];

            quads[0] = position.getLowerLeftX();  // x1
            quads[1] = position.getUpperRightY()-2; // y1
            quads[2] = position.getUpperRightX(); // x2
            quads[3] = quads[1]; // y2
            quads[4] = quads[0];  // x3
            quads[5] = position.getLowerLeftY()-2; // y3
            quads[6] = quads[2]; // x4
            quads[7] = quads[5]; // y5

            txtMark.setQuadPoints(quads);
            txtMark.setContents(chunk.toString());
            txtMark.setRectangle(position);
            
            
            annotations.add(txtMark);
           
		}
		doc.save("chunked.pdf");
		doc.close();
	}
}
