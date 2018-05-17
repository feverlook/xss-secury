/**
 * 
 */
package com.pengl.secury.tools;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author： pengl
 * @Date：2017年8月16日 上午9:19:45 
 * @Description：
 */
public class XmlParse {
	private static final Logger logger = LoggerFactory.getLogger(XmlParse.class);
	public static Document loadXML(String xmlStr)
	  {
	    Document result = null;
	    try {
	      SAXReader sax = new SAXReader();
	      result = sax.read(new StringReader(xmlStr));
	    }
	    catch (Exception ex) {
	      logger.info("Input XML String Err:" + ex.toString(), ex);

	      throw new RuntimeException(ex);
	    }

	    return result;
	  }

	  public static Document loadXML(String xmlStr, String encoding)
	  {
	    Document result = null;
	    try {
	      SAXReader sax = new SAXReader();
	      sax.setEncoding(encoding);
	      result = sax.read(new StringReader(xmlStr));
	    }
	    catch (Exception ex) {
	      logger.info("Input XML String Err:" + ex.toString(), ex);

	      throw new RuntimeException(ex);
	    }

	    return result;
	  }

	  public static Document loadXMLFile(String FileName)
	  {
	    Document result = null;
	    try {
	      ClassLoader cl = Thread.currentThread().getContextClassLoader();
	      InputStream is = cl.getResourceAsStream(FileName);

	      SAXReader sax = new SAXReader();

	      result = sax.read(is);
	    }
	    catch (Exception ex) {
	      logger.info("Open File Err:" + ex.toString(), ex);
	      throw new RuntimeException(ex);
	    }

	    return result;
	  }

	  public static Document loadXMLFile(String fileName, String encoding)
	  {
	    Document result = null;
	    try {
	      ClassLoader cl = Thread.currentThread().getContextClassLoader();
	      InputStream is = cl.getResourceAsStream(fileName);

	      SAXReader sax = new SAXReader();
	      sax.setEncoding(encoding);
	      result = sax.read(is);
	    }
	    catch (Exception ex) {
	      logger.info("Open File Err:" + ex.toString(), ex);
	      throw new RuntimeException(ex);
	    }

	    return result;
	  }

	  public static Document loadXmlStream(InputStream is)
	  {
	    Document result = null;
	    try {
	      SAXReader sax = new SAXReader();

	      result = sax.read(is);
	    }
	    catch (Exception ex) {
	      logger.info("Input XML String Err:" + ex.toString(), ex);

	      throw new RuntimeException(ex);
	    }

	    return result;
	  }

	  public static Document loadXmlStream(InputStream is, String encoding)
	  {
	    Document result = null;
	    try {
	      SAXReader sax = new SAXReader();
	      sax.setEncoding(encoding);
	      result = sax.read(is);
	    }
	    catch (Exception ex) {
	      logger.info("Input XML String Err:" + ex.toString(), ex);

	      throw new RuntimeException(ex);
	    }

	    return result;
	  }

	  public static Document loadXMLFile(File file)
	  {
	    Document result = null;
	    try {
	      SAXReader sax = new SAXReader();
	      result = sax.read(file);
	    }
	    catch (Exception ex) {
	      logger.info("Open File Err:" + ex.toString(), ex);
	      throw new RuntimeException(ex);
	    }

	    return result;
	  }

	  private static String getXmlTree(Document doc, int iBlankNum) {
	    String result = null;
	    try
	    {
	      ByteArrayOutputStream os = new ByteArrayOutputStream();
	      XMLWriter xmlOut = new XMLWriter(os, getXmlFormat(iBlankNum));
	      xmlOut.write(doc);

	      result = os.toString();
	    }
	    catch (IOException ex) {
	      logger.info(ex.getMessage(), ex);
	    }

	    return result;
	  }

	  private static OutputFormat getXmlFormat(int iBlankNum) {
	    OutputFormat xmlFormat = OutputFormat.createCompactFormat();
	    xmlFormat.setEncoding("GBK");
	    xmlFormat.setExpandEmptyElements(true);
	    xmlFormat.setTrimText(false);

	    if (iBlankNum == 0) {
	      xmlFormat.setIndent(false);
	    }
	    else if (iBlankNum > 0) {
	      xmlFormat.setIndent(true);
	      xmlFormat.setIndentSize(iBlankNum);
	    }
	    else {
	      xmlFormat.setIndent(true);
	      xmlFormat.setIndentSize(2);
	    }

	    return xmlFormat;
	  }

	  public static void writeFile(Document doc, String FileName)
	  {
	    try
	    {
	      if (FileName == null) throw new RuntimeException("FileName is null!");

	      File file = new File(FileName);
	      DataOutputStream out = new DataOutputStream(new FileOutputStream(file));

	      XMLWriter xmlOut = new XMLWriter(out, getXmlFormat(0));
	      xmlOut.write(doc);
	      xmlOut.close();
	    }
	    catch (Exception ex) {
	      logger.info("Write XML File Err:" + ex.toString(), ex);

	      throw new RuntimeException(ex);
	    }
	  }

	  public static void writeFile(String xmlStr, String fileName)
	  {
	    if (xmlStr == null) throw new RuntimeException("xmlStr is null！");

	    writeFile(loadXML(xmlStr), fileName);
	  }

	  public static String getXmlStr(Document doc)
	  {
	    return getXmlTree(doc, 0);
	  }
}
