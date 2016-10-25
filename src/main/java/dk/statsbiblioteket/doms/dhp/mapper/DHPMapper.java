package dk.statsbiblioteket.doms.dhp.mapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import dk.statsbiblioteket.util.xml.DOM;
import dk.statsbiblioteket.util.xml.XPathSelector;

public class DHPMapper {

    private final Map<String, PublisherMapping> publisherMappings;
    
    private static final XPathSelector XPATH_SELECTOR = DOM
            .createXPathSelector("p", "http://www.pbcore.org/PBCore/PBCoreNamespace.html");
    
    public DHPMapper() throws URISyntaxException {
        publisherMappings = PublisherMappingLoader.loadMappings("publishers.txt");
    }
    
    public Document buildBaseDocument(InputStream template, Document source) throws IOException, ParseException {
        Document baseDoc = DOM.streamToDOM((template), true);
        
        String fullIdentifier = getStringNode(source, "/p:PBCoreDescriptionDocument/p:pbcoreIdentifier[1]/p:identifier");
        String identifier = fullIdentifier.substring(fullIdentifier.indexOf(":") + 1);
        String formatIdentifier = getStringNode(source, 
                "/p:PBCoreDescriptionDocument/p:pbcoreInstantiation[2]/p:pbcoreFormatID[1]/p:formatIdentifier");
        String description = getStringNode(source, "/p:PBCoreDescriptionDocument/p:pbcoreDescription[1]/p:description"); 
        String title = getStringNode(source, "/p:PBCoreDescriptionDocument/p:pbcoreTitle[1]/p:title");
        String dateAvailableStart = getStringNode(source, 
                "/p:PBCoreDescriptionDocument/p:pbcoreInstantiation[1]/p:pbcoreDateAvailable[1]/p:dateAvailableStart");
        String dateAvailableEnd = getStringNode(source, 
                "/p:PBCoreDescriptionDocument/p:pbcoreInstantiation[1]/p:pbcoreDateAvailable[1]/p:dateAvailableEnd");
        String annotation = getStringNode(source, 
                "/p:PBCoreDescriptionDocument/p:pbcoreInstantiation[2]/p:pbcoreAnnotation[1]/p:annotation");
        String extension = getStringNode(source, "/p:PBCoreDescriptionDocument/p:pbcoreExtension[1]/p:extension");

        setNodeContent(baseDoc, "/p:PBCoreDescriptionDocument/p:pbcoreIdentifier[1]/p:identifier", identifier); 
        setNodeContent(baseDoc, "/p:PBCoreDescriptionDocument/p:pbcoreIdentifier[2]/p:identifier", formatIdentifier);
        setNodeContent(baseDoc, "/p:PBCoreDescriptionDocument/p:pbcoreTitle[1]/p:title", title);
        setNodeContent(baseDoc, "/p:PBCoreDescriptionDocument/p:pbcoreDescription[1]/p:description", description);
        setNodeContent(baseDoc, 
                "/p:PBCoreDescriptionDocument/p:pbcoreInstantiation[1]/p:pbcoreDateAvailable[1]/p:dateAvailableStart", 
                mapInputToOutPutDate(dateAvailableStart)); 
        setNodeContent(baseDoc, 
                "/p:PBCoreDescriptionDocument/p:pbcoreInstantiation[1]/p:pbcoreDateAvailable[1]/p:dateAvailableEnd", 
                mapInputToOutPutDate(dateAvailableEnd));
        setNodeContent(baseDoc, 
                "/p:PBCoreDescriptionDocument/p:pbcoreInstantiation[1]/p:pbcoreFormatID[1]/p:formatIdentifier", 
                identifier); 
        setNodeContent(baseDoc, 
                "/p:PBCoreDescriptionDocument/p:pbcoreInstantiation[1]/p:pbcoreAnnotation[1]/p:annotation", annotation);
        setNodeContent(baseDoc, "/p:PBCoreDescriptionDocument/p:pbcoreExtension[1]/p:extension", extension);
        
                
        return baseDoc;
    }
    
    public void buildObjectsFromFile(Path sourceFile) throws IOException, ParseException {
        Document programSource = DOM.streamToDOM(Files.newInputStream(sourceFile), true); 
        InputStream template = getClass().getClassLoader().getResourceAsStream("pbcore-dhp-dr-template.xml"); 
        
        Document baseDoc = buildBaseDocument(template, programSource);
        
        NodeList nodes = XPATH_SELECTOR.selectNodeList(programSource, "/p:PBCoreDescriptionDocument/p:pbcorePublisher");
        if(nodes != null) {
            for(int i=0; i<nodes.getLength(); i++) {
                Node node = nodes.item(i);
                String pub = XPATH_SELECTOR.selectNode(node, "p:publisher").getTextContent();
                PublisherMapping pubMap = publisherMappings.get(pub);
                setNodeContent(baseDoc, "/p:PBCoreDescriptionDocument/p:pbcorePublisher[1]/p:publisher", pubMap.getNormalizeTo());
                setNodeContent(baseDoc, "/p:PBCoreDescriptionDocument/p:pbcorePublisher[2]/p:publisher", pubMap.getChannelID());
                setNodeContent(baseDoc, "/p:PBCoreDescriptionDocument/p:pbcoreInstantiation[1]/p:formatMediaType", pubMap.getMediaType());
                
                writeProgram(sourceFile, baseDoc, i);
            }
        }
        
    }
    
    protected void writeProgram(Path sourceFile, Document program, int publisherNumber) {
        // Figure out how to write out the 'ingestable object file'...
    }
    
    private String mapInputToOutPutDate(String inputDate) throws ParseException {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", new Locale("da_DK"));
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", new Locale("da_DK"));
        
        return outputDateFormat.format(inputDateFormat.parse(inputDate));
    }
    
    private void setNodeContent(Document doc, String nodeXpath, String value) {
        XPATH_SELECTOR.selectNode(doc, nodeXpath).setTextContent(value);
    }
    
    private String getStringNode(Document doc, String xpath) {
        return XPATH_SELECTOR.selectNode(doc, xpath).getTextContent();
    }
}
