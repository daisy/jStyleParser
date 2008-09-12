package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.tidy.Tidy;

import cz.vutbr.web.css.CSSFactory;
import cz.vutbr.web.css.CSSProperty;
import cz.vutbr.web.css.NodeData;
import cz.vutbr.web.css.StyleSheet;
import cz.vutbr.web.css.TermColor;
import cz.vutbr.web.css.TermFactory;
import cz.vutbr.web.css.CSSProperty.TextDecoration;
import cz.vutbr.web.domassign.Analyzer;

public class UAConformancy {
	private static Logger log = LoggerFactory.getLogger(UAConformancy.class);

	private static TermFactory tf = CSSFactory.getTermFactory();
	private static Map<Element, NodeData> decl;
	
	@BeforeClass
	public static void init() throws FileNotFoundException {
		log.info("\n\n\n == UAConformancy test at {} == \n\n\n", new Date());
		
		Tidy parser = new Tidy();
		parser.setCharEncoding(org.w3c.tidy.Configuration.UTF8);

		Document doc = parser.parseDOM(new FileInputStream("data/invalid/style.html"),
				null);

		StyleSheet sheet = CSSFactory.parse("data/invalid/style.css", null);

		Analyzer analyzer = new Analyzer(sheet);
		decl = analyzer.evaluateDOM(doc, "screen", true);

	}
	
	@Test
	public void testNotEmptyness() {
		
		Assert.assertNotNull("Declarations parsed", decl);
		Assert.assertNotSame("There are some declarations", Collections.emptyMap(), decl);
		
		for(Element e: decl.keySet())
			log.debug("{} : {} ", e.getNodeName(), decl.get(e));
	}
	
	@Test 
	public void unknownProperties() {

		NodeData nd = retrieve("h1");
		
		Assert.assertEquals("Color is red", tf.createColor("#ff0000"), 
				nd.getValue(TermColor.class, "color"));
		
		Assert.assertNull("There is no rotatation", 
				nd.getProperty(CSSProperty.class, "rotation"));
	}
	
	@Test 
	public void illegalValues() {
		NodeData nd = retrieve("img");
		
		Assert.assertEquals("Float is left", CSSProperty.Float.LEFT, 
				nd.getProperty(CSSProperty.Float.class, "float"));
	}
	

	@Test
	public void malformedDeclaration() {
		NodeData nd = retrieve("p");
		
		Assert.assertEquals("Color is green", tf.createColor("#008000"), 
				nd.getValue(TermColor.class, "color"));
	}
	
	private NodeData retrieve(String elementName) {
		for(Element e: decl.keySet()) {
			if(elementName.equalsIgnoreCase(e.getNodeName()))
				return decl.get(e);
		}
		return null;
	}
	
	@Test 
	public void ignoreUnknownAtRule() {
		NodeData nd = retrieve("h2");
		
		Assert.assertEquals("Color is blue", tf.createColor(0,0,0xff),
				nd.getValue(TermColor.class, "color"));

		Assert.assertNull("Text-decoration is not set", 
				nd.getProperty(TextDecoration.class, "text-decoration"));
				
	}
	
	@Test
	public void unclosedString() {
		NodeData nd = retrieve("div");
		
		Assert.assertEquals("Color is green", tf.createColor(0, 0x80, 0),
				nd.getValue(TermColor.class, "color"));
		
		Assert.assertEquals("Background-color is white", tf.createColor("#ffffff"),
				nd.getValue(TermColor.class, "background-color"));
	}
	
	public void unexpectedEOF() {
		
	}
}
