package org.amplecode.staxwax.reader;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
import java.io.ByteArrayInputStream;
import javax.xml.stream.XMLStreamException;
import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import javax.xml.stream.XMLEventReader;

import org.amplecode.staxwax.factory.XMLFactory;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.validation.XMLValidationException;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 */
public class XMLReaderTest {

  private static final String COLLECTION_NAME = "dataElements";
  private static final String ELEMENT_NAME = "dataElement";
  private static final String FIELD_ID = "id";
  private static final String FIELD_UUID = "uuid";
  private static final String FIELD_NAME = "name";
  private static final String FIELD_ALTERNATIVE_NAME = "alternativeName";
  private static final String FIELD_SHORT_NAME = "shortName";
  private static final String FIELD_CODE = "code";
  private static final String FIELD_DESCRIPTION = "description";
  private static final String FIELD_ACTIVE = "active";
  private static final String FIELD_TYPE = "type";
  private static final String FIELD_AGGREGATION_OPERATOR = "aggregationOperator";
  private InputStream inputStreamA;
  private InputStream inputStreamB;
  private InputStream inputStreamC;
  private URL relaxNg;
  private String[] specialChars = {"&", "<", ">", "\"", "'"};


  private static int BIGTEXTSIZE = 5000; 
  // -------------------------------------------------------------------------
  // Fixture
  // -------------------------------------------------------------------------
  @Before
  public void setUp()
          throws Exception {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    inputStreamA = classLoader.getResourceAsStream("dataA.xml");
    inputStreamB = classLoader.getResourceAsStream("dataB.xml");
    inputStreamC = classLoader.getResourceAsStream("dataC.xml");
    relaxNg = classLoader.getResource("dataB.rng");
  }

  @After
  public void tearDown()
          throws Exception {
    inputStreamA.close();
    inputStreamB.close();
    inputStreamC.close();
  }

  // -------------------------------------------------------------------------
  // Tests
  // -------------------------------------------------------------------------
  @Test
  public void testReadElements() {
    XMLReader reader = XMLFactory.getXMLReader(inputStreamA);

    int i = 0;

    while (reader.moveToStartElement(ELEMENT_NAME, COLLECTION_NAME)) {
      int number = i + 1;

      Map<String, String> values = reader.readElements(ELEMENT_NAME);

      assertEquals(FIELD_ID + specialChars[i] + number, values.get(FIELD_ID));
      assertEquals(FIELD_UUID + specialChars[i] + number, values.get(FIELD_UUID));
      assertEquals(FIELD_NAME + specialChars[i] + number, values.get(FIELD_NAME));
      assertEquals(FIELD_ALTERNATIVE_NAME + specialChars[i] + number, values.get(FIELD_ALTERNATIVE_NAME));
      assertEquals(FIELD_SHORT_NAME + specialChars[i] + number, values.get(FIELD_SHORT_NAME));
      assertEquals(FIELD_CODE + specialChars[i] + number, values.get(FIELD_CODE));
      assertEquals(FIELD_DESCRIPTION + specialChars[i] + number, values.get(FIELD_DESCRIPTION));
      assertEquals(FIELD_ACTIVE + specialChars[i] + number, values.get(FIELD_ACTIVE));
      assertEquals(FIELD_TYPE + specialChars[i] + number, values.get(FIELD_TYPE));
      assertEquals(null, values.get(FIELD_AGGREGATION_OPERATOR));

      i++;
    }
  }

  @Test
  public void testMoveToStartElement() {
    XMLReader reader = XMLFactory.getXMLReader(inputStreamA);
    reader.moveToStartElement();
    assertEquals(COLLECTION_NAME,reader.getElementName());
    reader.closeReader();
  }

  @Test
  public void testEventMoveToStartElement() {
    XMLReader evReader = XMLFactory.getXMLEventReader( inputStreamA );
    evReader.moveToStartElement();
    assertEquals(COLLECTION_NAME,evReader.getElementName());
    evReader.closeReader();
  }

  @Test
  public void testEventReadElements() {
    XMLReader reader = XMLFactory.getXMLEventReader(inputStreamA);

    int i = 0;

    while (reader.moveToStartElement(ELEMENT_NAME, COLLECTION_NAME)) {
      int number = i + 1;

      Map<String, String> values = reader.readElements(ELEMENT_NAME);

      assertEquals(FIELD_ID + specialChars[i] + number, values.get(FIELD_ID));
      assertEquals(FIELD_UUID + specialChars[i] + number, values.get(FIELD_UUID));
      assertEquals(FIELD_NAME + specialChars[i] + number, values.get(FIELD_NAME));
      assertEquals(FIELD_ALTERNATIVE_NAME + specialChars[i] + number, values.get(FIELD_ALTERNATIVE_NAME));
      assertEquals(FIELD_SHORT_NAME + specialChars[i] + number, values.get(FIELD_SHORT_NAME));
      assertEquals(FIELD_CODE + specialChars[i] + number, values.get(FIELD_CODE));
      assertEquals(FIELD_DESCRIPTION + specialChars[i] + number, values.get(FIELD_DESCRIPTION));
      assertEquals(FIELD_ACTIVE + specialChars[i] + number, values.get(FIELD_ACTIVE));
      assertEquals(FIELD_TYPE + specialChars[i] + number, values.get(FIELD_TYPE));
      assertEquals(null, values.get(FIELD_AGGREGATION_OPERATOR));

      i++;
    }
  }

  @Test
  public void testReadElementsWithAttributes1() {
    XMLReader reader = XMLFactory.getXMLReader(inputStreamB);

    int i = 0;

    while (reader.moveToStartElement(ELEMENT_NAME, COLLECTION_NAME)) {
      int number = i + 1;

      Map<String, String> values = reader.readElements(ELEMENT_NAME);

      assertEquals(FIELD_CODE + number, values.get(FIELD_CODE));
      assertEquals(FIELD_UUID + number, values.get(FIELD_UUID));
      assertEquals(FIELD_SHORT_NAME + number, values.get(FIELD_SHORT_NAME));
      assertEquals(FIELD_ALTERNATIVE_NAME + number, values.get(FIELD_ALTERNATIVE_NAME));
      assertEquals(FIELD_NAME + number, values.get(FIELD_NAME));
      assertEquals(FIELD_TYPE + number, values.get(FIELD_TYPE));
      assertEquals(FIELD_DESCRIPTION + number, values.get(FIELD_DESCRIPTION));
      assertEquals(null, values.get(FIELD_ACTIVE));

      i++;
    }
  }

  @Test
  public void testReadElementsWithAttributes2() {
    XMLReader reader = XMLFactory.getXMLReader(inputStreamC);

    int i = 0;

    while (reader.moveToStartElement(ELEMENT_NAME, COLLECTION_NAME)) {
      int number = i + 1;

      Map<String, String> values = reader.readElements(ELEMENT_NAME);

      assertEquals(FIELD_NAME + number, values.get(FIELD_NAME));
      assertEquals(FIELD_SHORT_NAME + number, values.get(FIELD_SHORT_NAME));
      assertEquals(FIELD_CODE + number, values.get(FIELD_CODE));

      i++;
    }
  }

  @Test
  public void testRead() {
    XMLReader reader = XMLFactory.getXMLReader(inputStreamA);

    int i = 0;

    while (reader.moveToStartElement(ELEMENT_NAME, COLLECTION_NAME)) {
      int number = i + 1;

      reader.moveToStartElement(FIELD_ID);
      assertEquals(FIELD_ID + specialChars[i] + number, reader.getElementValue());

      reader.moveToStartElement(FIELD_UUID);
      assertEquals(FIELD_UUID + specialChars[i] + number, reader.getElementValue());

      reader.moveToStartElement(FIELD_NAME);
      assertEquals(FIELD_NAME + specialChars[i] + number, reader.getElementValue());

      reader.moveToStartElement(FIELD_ALTERNATIVE_NAME);
      assertEquals(FIELD_ALTERNATIVE_NAME + specialChars[i] + number, reader.getElementValue());

      reader.moveToStartElement(FIELD_SHORT_NAME);
      assertEquals(FIELD_SHORT_NAME + specialChars[i] + number, reader.getElementValue());

      reader.moveToStartElement(FIELD_CODE);
      assertEquals(FIELD_CODE + specialChars[i] + number, reader.getElementValue());

      reader.moveToStartElement(FIELD_DESCRIPTION);
      assertEquals(FIELD_DESCRIPTION + specialChars[i] + number, reader.getElementValue());

      reader.moveToStartElement(FIELD_ACTIVE);
      assertEquals(FIELD_ACTIVE + specialChars[i] + number, reader.getElementValue());

      reader.moveToStartElement(FIELD_TYPE);
      assertEquals(FIELD_TYPE + specialChars[i] + number, reader.getElementValue());

      reader.moveToStartElement(FIELD_AGGREGATION_OPERATOR);
      assertEquals(null, reader.getElementValue());

      i++;
    }
  }

  @Test
  public void testReadWithAttributes() {
    XMLReader reader = XMLFactory.getXMLReader(inputStreamB);

    int i = 0;

    while (reader.moveToStartElement(ELEMENT_NAME, COLLECTION_NAME)) {
      int number = i + 1;

      assertEquals(FIELD_CODE + number, reader.getAttributeValue(FIELD_CODE));

      reader.moveToStartElement(FIELD_NAME);

      assertEquals(2, reader.getAttributeCount());

      assertEquals(FIELD_SHORT_NAME + number, reader.getAttributeValue(FIELD_SHORT_NAME));
      assertEquals(FIELD_ALTERNATIVE_NAME + number, reader.getAttributeValue(FIELD_ALTERNATIVE_NAME));
      assertEquals(FIELD_NAME + number, reader.getElementValue());

      reader.moveToStartElement(FIELD_DESCRIPTION);

      assertEquals(1, reader.getAttributeCount());

      assertEquals(FIELD_TYPE + number, reader.getAttributeValue(FIELD_TYPE));
      assertEquals(FIELD_DESCRIPTION + number, reader.getElementValue());

      i++;
    }
  }

   @Test
  public void testEventRead() {
    XMLReader reader = XMLFactory.getXMLEventReader(inputStreamA);

    int i = 0;

    while (reader.moveToStartElement(ELEMENT_NAME, COLLECTION_NAME)) {
      int number = i + 1;

      reader.moveToStartElement(FIELD_ID);
      assertEquals(FIELD_ID + specialChars[i] + number, reader.getElementValue());

      reader.moveToStartElement(FIELD_UUID);
      assertEquals(FIELD_UUID + specialChars[i] + number, reader.getElementValue());

      reader.moveToStartElement(FIELD_NAME);
      assertEquals(FIELD_NAME + specialChars[i] + number, reader.getElementValue());

      reader.moveToStartElement(FIELD_ALTERNATIVE_NAME);
      assertEquals(FIELD_ALTERNATIVE_NAME + specialChars[i] + number, reader.getElementValue());

      reader.moveToStartElement(FIELD_SHORT_NAME);
      assertEquals(FIELD_SHORT_NAME + specialChars[i] + number, reader.getElementValue());

      reader.moveToStartElement(FIELD_CODE);
      assertEquals(FIELD_CODE + specialChars[i] + number, reader.getElementValue());

      reader.moveToStartElement(FIELD_DESCRIPTION);
      assertEquals(FIELD_DESCRIPTION + specialChars[i] + number, reader.getElementValue());

      reader.moveToStartElement(FIELD_ACTIVE);
      assertEquals(FIELD_ACTIVE + specialChars[i] + number, reader.getElementValue());

      reader.moveToStartElement(FIELD_TYPE);
      assertEquals(FIELD_TYPE + specialChars[i] + number, reader.getElementValue());

      reader.moveToStartElement(FIELD_AGGREGATION_OPERATOR);
      assertEquals(null, reader.getElementValue());

      i++;
    }
  }

  @Test
  public void testEventReadWithAttributes() {
    XMLReader reader = XMLFactory.getXMLEventReader(inputStreamB);

    int i = 0;

    while (reader.moveToStartElement(ELEMENT_NAME, COLLECTION_NAME)) {
      int number = i + 1;

      assertEquals(FIELD_CODE + number, reader.getAttributeValue(FIELD_CODE));

      reader.moveToStartElement(FIELD_NAME);

      assertEquals(2, reader.getAttributeCount());

      assertEquals(FIELD_SHORT_NAME + number, reader.getAttributeValue(FIELD_SHORT_NAME));
      assertEquals(FIELD_ALTERNATIVE_NAME + number, reader.getAttributeValue(FIELD_ALTERNATIVE_NAME));
      assertEquals(FIELD_NAME + number, reader.getElementValue());

      reader.moveToStartElement(FIELD_DESCRIPTION);

      assertEquals(1, reader.getAttributeCount());

      assertEquals(FIELD_TYPE + number, reader.getAttributeValue(FIELD_TYPE));
      assertEquals(FIELD_DESCRIPTION + number, reader.getElementValue());

      i++;
    }
  }

  @Test
  public void testValidation() throws XMLStreamException, XMLValidationException  {
        XMLValidationSchemaFactory sf = XMLValidationSchemaFactory.newInstance(XMLValidationSchema.SCHEMA_ID_RELAXNG);
        XMLValidationSchema rng =  sf.createSchema(relaxNg);

        XMLReader reader = XMLFactory.getXMLReader(inputStreamB);

        XMLStreamReader2 stax2Reader = (XMLStreamReader2) reader.getXmlStreamReader();
        stax2Reader.validateAgainst(rng);
        XMLEventReader er = reader.getXmlEventReader();
        // just iterate through document
        while (er.hasNext()) {
          er.next();
        }
    }

  @Test
  public void testBigTextRead() {

    char[] bigText = new char[BIGTEXTSIZE];
    for (int i=0; i<BIGTEXTSIZE; ++i) {
        bigText[i]='A';
    }

    //ByteArrayInputStream xmlStream = new ByteArrayInputStream(xmlBuf);

    StringBuilder xml = new StringBuilder(BIGTEXTSIZE*2+40);
    xml.append( "<root>");
    xml.append( "<node>"); xml.append(bigText); xml.append( "</node>");
    xml.append( "<node>"); xml.append(bigText); xml.append( "</node>");
    xml.append( "</root>");

    ByteArrayInputStream xmlStream = new ByteArrayInputStream(xml.toString().getBytes());
    XMLReader reader = XMLFactory.getXMLReader(xmlStream);

    reader.moveToStartElement( "node");
    assertEquals(BIGTEXTSIZE, reader.getElementValue().length());
    reader.moveToStartElement( "node");
    assertEquals(BIGTEXTSIZE, reader.getElementValue().length());
  }

}

