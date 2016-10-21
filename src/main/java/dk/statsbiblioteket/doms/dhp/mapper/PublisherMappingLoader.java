package dk.statsbiblioteket.doms.dhp.mapper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import dk.statsbiblioteket.doms.dhp.mapper.PublisherMappingException.FailureType;

public class PublisherMappingLoader {

    final static String RADIO_MEDIA_TYPE = "Sound";
    final static String TV_MEDIA_TYPE = "Moving image";
    
    /**
     * Load a publishers file that is found on the classpath and parse each line to PublisherMapping's 
     * First line is considered a header and is skipped. 
     * @param publisherFile Name of the file on the classpath to load and parse
     * @return mapping between original publisher and {@link PublisherMapping} objects 
     * @throws URISyntaxException 
     */
    public static Map<String, PublisherMapping> loadMappings(String publisherFile) throws URISyntaxException {
        
        Map<String, PublisherMapping> mapping = new HashMap<>();
        
        Path path = Paths.get(ClassLoader.getSystemResource(publisherFile).toURI());
        try (Stream<String> lines = Files.lines(path)) {
            lines.skip(1).forEach(line -> {
                try {
                    PublisherMapping pm = stringToPublisherMapping(line);
                    mapping.put(pm.getOriginalPublisher(), pm);
                } catch(PublisherMappingException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException ex) {

        }
        
        return mapping;
    }
    
    
    /**
     * Reads a mapping line from the publishers file provided by LLO/SRE
     * The input line is expected to have the following format:
     * <originalPublisher>\t<normalizeTo>\t<channelID>\t<mediaType>[\t<optionalNotes>]
     * @param mappingLine The string to parse and map
     * @return {@link PublisherMapping} The mapping contained in the line. 
     * @throws PublisherMappingException if the input was malformed 
     */
    static PublisherMapping stringToPublisherMapping(String mappingLine) throws PublisherMappingException {
        String[] parts = mappingLine.split("\t");
        if(parts.length < 4) {
            throw new PublisherMappingException("Malformed mappingline '" + mappingLine +  "'. "
                    + "Minimum 4 parts required (" + parts.length + " found), expected seperator is '\t'", 
                    FailureType.NUMBER_OF_PARTS);
        }
        
        String mediaType;
        if(parts[3].trim().equals("r")) {
            mediaType = RADIO_MEDIA_TYPE;
        } else if(parts[3].trim().equals("t")) {
            mediaType = TV_MEDIA_TYPE;
        } else {
            throw new PublisherMappingException("Malformed mappingline '" + mappingLine +  "'. "
                    + "part 4 of the line was neither 'r' or 't', but was '" + parts[3].trim() + "'", FailureType.MEDIA_TYPE);
        }
        
        return new PublisherMapping(parts[0], parts[1], parts[2], mediaType);
    }
}
