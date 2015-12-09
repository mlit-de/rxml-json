package de.mlit.rxml.json;

import de.mlit.rxml.api.SaxResource;
import de.mlit.rxml.api.StreamResource;
import de.mlit.rxml.api.helper.AbstractResource;
import de.mlit.rxml.util.AttributeAdapter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import javax.json.Json;
import javax.json.stream.JsonParser;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mlauer on 01/10/14.
 */
public class ParsedJsonStreamResource extends AbstractResource implements SaxResource {

    protected StreamResource source;

    public ParsedJsonStreamResource(StreamResource source) {
        this.source = source;
    }

    public void runOn(ContentHandler ch) throws SAXException, IOException {
        AttributeAdapter aa = new AttributeAdapter(ch);
        aa.startDocument();
        InputStream in = source.openStream();
        try {
            JsonParser parser = Json.createParser(in);
            while (parser.hasNext()) {
                JsonParser.Event event = parser.next();
                switch (event) {
                    case START_OBJECT:
                        aa.startElement("object");
                        break;
                    case START_ARRAY:
                        aa.startElement("array");
                        break;
                    case END_OBJECT:
                        aa.endElement("object");
                        break;
                    case END_ARRAY:
                        aa.endElement("array");
                        break;
                    case KEY_NAME:
                        aa.addAttribute("key", parser.getString());
                        break;
                    case VALUE_FALSE:
                        aa.startElement("boolean");
                        aa.text("false");
                        aa.endElement("boolean");
                        break;
                    case VALUE_TRUE:
                        aa.startElement("boolean");
                        aa.text("true");
                        aa.endElement("boolean");
                        break;
                    case VALUE_NULL:
                        aa.emptyElement("null");
                        break;
                    case VALUE_NUMBER:
                        aa.addAttribute("type", parser.isIntegralNumber() ? "integral" : "decimal");
                        aa.startElement("number");
                        aa.text(parser.getString());
                        aa.endElement("number");
                        break;
                    case VALUE_STRING:
                        aa.startElement("string");
                        aa.text(parser.getString());
                        aa.endElement("string");
                        break;
                }
            }
            aa.endDocument();
        } finally {
            if(in!=null) {
                in.close();
            }
        }

    }


}
