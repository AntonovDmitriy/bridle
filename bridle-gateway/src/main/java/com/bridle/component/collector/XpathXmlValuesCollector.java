package com.bridle.component.collector;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Сборщик значений для XML-формата.
 * <p>Использует XPath-выражения для поиска значений.
 */
public class XpathXmlValuesCollector implements ValuesCollector<String> {

    private static final XPathFactory xPathFactory = XPathFactory.newInstance();

    private static final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

    static {
        documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
    }

    @Override
    public Optional<Map<String, Object>> collectValues(String body, Map<String, String> queryExpressionsByName) {
        if (body == null) {
            throw new XPathCollectorException("body is null");
        }
        Optional<Map<String, Object>> result = Optional.empty();
        if (!queryExpressionsByName.isEmpty()) {
            Map<String, Object> valuesByName = new HashMap<>();
            try {
                final Document document = createXmlDomDocument(body);
                for (Map.Entry<String, String> entry : queryExpressionsByName.entrySet()) {
                    XPathExpression xPathExpression = xPathFactory.newXPath().compile(entry.getValue());
                    Object xpathResultNode = xPathExpression.evaluate(document, XPathConstants.NODE);
                    valuesByName.put(entry.getKey(),
                                     xpathResultNode != null ? ((Node) xpathResultNode).getNodeValue() : null);
                }
            } catch (Exception e) {
                throw new XPathCollectorException("Error during evaluate XPath expression: " + e.getMessage(), e);
            }
            result = Optional.of(valuesByName);
        }
        return result;
    }

    private Document createXmlDomDocument(String body) throws SAXException, IOException, ParserConfigurationException {
        return documentBuilderFactory
                .newDocumentBuilder()
                .parse(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));
    }
}
