package com.git.hui.offer.web.config.init;

import org.springframework.core.io.ClassPathResource;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author YiHui
 * @date 2023/3/2
 */
public class DbChangeSetLoader {
    public static XMLReader getInstance() throws Exception {
        // javax.xml.parsers.SAXParserFactory 原生api获取factory
        SAXParserFactory factory = SAXParserFactory.newInstance();
        // javax.xml.parsers.SAXParser 原生api获取parse
        SAXParser saxParser = factory.newSAXParser();
        // 获取xml
        return saxParser.getXMLReader();
    }

    public static List<ClassPathResource> loadDbChangeSetResources(String source) {
        try {
            XMLReader xmlReader = getInstance();
            ChangeHandler setHandler = new ChangeHandler("sqlFile", "path");
            xmlReader.setContentHandler(setHandler);
            xmlReader.parse(new ClassPathResource(source.replace("classpath:", "").trim()).getFile().getPath());
            List<String> changeSetFiles = setHandler.getSets();

            List<ClassPathResource> result = new ArrayList<>();
            result.addAll(changeSetFiles.stream().map(ClassPathResource::new).collect(Collectors.toList()));
            return result;
        } catch (Exception e) {
            throw new IllegalStateException("加载初始化脚本异常!");
        }
    }


    public static class ChangeHandler extends DefaultHandler {
        private List<String> sets = new ArrayList<>();

        private final String tag;
        private final String attr;

        public ChangeHandler(String tag, String attr) {
            this.tag = tag;
            this.attr = attr;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (tag.equals(qName)) {
                sets.add(attributes.getValue(attr));
            }
        }

        public List<String> getSets() {
            return sets;
        }

        public void reset() {
            sets.clear();
        }
    }
}
