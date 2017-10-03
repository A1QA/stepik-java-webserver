package xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import xml.reflection.ReflectionHelper;

import java.util.logging.Logger;

public class XMLObjHandler extends DefaultHandler {

    private static final Logger logger = Logger.getLogger(XMLObjHandler.class.getName());
    private static final String CLASSNAME = "class";
    private String element = null;
    private Object object = null;
    private StringBuilder stringBuilder = null;

    public void startDocument() throws SAXException {
        logger.info("Start document");
    }

    public void endDocument() throws SAXException {
        logger.info("End document");
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (!qName.equals(CLASSNAME)) {
            element = qName;
        } else {
            String className = attributes.getValue(0);
            logger.info("Class name: " + className);
            object = ReflectionHelper.createInstance(className);
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (element != null){
            ReflectionHelper.setFieldValue(object, element, stringBuilder.toString());
            stringBuilder = null;
            element = null;
        }
    }

    // https://stackoverflow.com/a/4567652/6472224
    public void characters(char ch[], int start, int length) throws SAXException {
        if (element != null) {
            if (stringBuilder == null) {
                stringBuilder = new StringBuilder();
            }
            String chunk = new String(ch, start, length); // если прочтется лишь часть, см. ссылку
            stringBuilder.append(chunk);
            logger.info(element + " = " + chunk);


            /* Можно, конечно, просто так, но см. ссылку */
            //String value = new String(ch, start, length);
            //logger.info(element + " = " + value);
            //ReflectionHelper.setFieldValue(object, element, value);
        }
    }

    public Object getObject() {
        return object;
    }
}

