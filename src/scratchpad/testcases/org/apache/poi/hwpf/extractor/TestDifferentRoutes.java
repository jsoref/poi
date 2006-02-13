package org.apache.poi.hwpf.extractor;

import java.io.FileInputStream;
import java.util.Iterator;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.TextPiece;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;

import junit.framework.TestCase;

/**
 * Test the different routes to extracting text
 *
 * @author Nick Burch (nick at torchbox dot com)
 */
public class TestDifferentRoutes extends TestCase {
	private String[] p_text = new String[] {
			"This is a simple word document\r",
			"\r",
			"It has a number of paragraphs in it\r",
			"\r",
			"Some of them even feature bold, italic and underlined text\r",
			"\r",
			"\r",
			"This bit is in a different font and size\r",
			"\r",
			"\r",
			"This bit features some red text.\r",
			"\r",
			"\r",
			"It is otherwise very very boring.\r"
	};
	
	private HWPFDocument doc;
	
    protected void setUp() throws Exception {
		String dirname = System.getProperty("HWPF.testdata.path");
		
		String filename = dirname + "/test2.doc";
		doc = new HWPFDocument(new FileInputStream(filename));
    }			
    
    /**
     * Test model based extraction
     */
    public void testExtractFromModel() {
    	Range r = doc.getRange();
    	
    	String[] text = new String[r.numParagraphs()];
    	for(int i=0; i < r.numParagraphs(); i++) {
    		Paragraph p = r.getParagraph(i);
    		text[i] = p.text();
    	}
    	
    	assertEquals(p_text.length, text.length);
    	for(int i=0; i<p_text.length; i++) {
    		assertEquals(p_text[i], text[i]);
    	}
    }
    
    /**
     * Test textPieces based extraction
     */
    public void testExtractFromTextPieces() throws Exception {
    	StringBuffer textBuf = new StringBuffer();
    	
    	Iterator textPieces = doc.getTextTable().getTextPieces().iterator();
    	while (textPieces.hasNext()) {
    		TextPiece piece = (TextPiece) textPieces.next();

    		String encoding = "Cp1252";
    		if (piece.usesUnicode()) {
    			encoding = "UTF-16LE";
    		}
    		String text = new String(piece.getRawBytes(), encoding);
    		textBuf.append(text);
    	}
    	
    	StringBuffer exp = new StringBuffer();
    	for(int i=0; i<p_text.length; i++) {
    		exp.append(p_text[i]);
    	}
    	assertEquals(exp.toString(), textBuf.toString());
    }
}
