package dk.statsbiblioteket.doms.dhp.mapper;

import java.net.URISyntaxException;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import dk.statsbiblioteket.doms.dhp.mapper.PublisherMappingException.FailureType;

public class PublisherMappingLoaderTest {

    @Test
    public void loadDefaultMappingsTest() throws URISyntaxException {
        String defaultMappingFile = "publishers.txt";
        
        Map<String, PublisherMapping> mappings = PublisherMappingLoader.loadMappings(defaultMappingFile);
        
        String testRadioMappingKey = "23.00";
        PublisherMapping pm1 = mappings.get(testRadioMappingKey);
        Assert.assertEquals(pm1.getOriginalPublisher(), testRadioMappingKey);
        Assert.assertEquals(pm1.getNormalizeTo(), "DR Klassisk");
        Assert.assertEquals(pm1.getChannelID(), "drpk");
        Assert.assertEquals(pm1.getMediaType(), PublisherMappingLoader.RADIO_MEDIA_TYPE);
     
        
        String testTVMappingKey = "TV-SYD HADERSLEV (04) 53 0511";
        PublisherMapping pm2 = mappings.get(testTVMappingKey);
        Assert.assertEquals(pm2.getOriginalPublisher(), testTVMappingKey);
        Assert.assertEquals(pm2.getNormalizeTo(), "TV-Syd Haderslev");
        Assert.assertEquals(pm2.getChannelID(), "drtvsyd");
        Assert.assertEquals(pm2.getMediaType(), PublisherMappingLoader.TV_MEDIA_TYPE);
    }
    
    @Test
    public void malformedMappingLineTest() throws URISyntaxException {
        String malformedFile = "publishers.txt-malformed-1";
        
        try{
            Map<String, PublisherMapping> mappings = PublisherMappingLoader.loadMappings(malformedFile);
            Assert.fail("An exception was expected when loading a malformed mapping file");
        } catch (RuntimeException e) {
            PublisherMappingException pem = (PublisherMappingException) e.getCause();
            Assert.assertTrue(pem.getFailureType().equals(FailureType.NUMBER_OF_PARTS));
        }
    }
    
    @Test
    public void badMediatypeMappingLineTest() throws URISyntaxException {
        String malformedFile = "publishers.txt-malformed-2";
        
        try{
            Map<String, PublisherMapping> mappings = PublisherMappingLoader.loadMappings(malformedFile);
            Assert.fail("An exception was expected when loading a malformed mapping file");
        } catch (RuntimeException e) {
            PublisherMappingException pem = (PublisherMappingException) e.getCause();
            Assert.assertTrue(pem.getFailureType().equals(FailureType.MEDIA_TYPE));
        }
    }
    
    @Test
    public void loadWellFormedRadioMappingLineTest() throws PublisherMappingException {
        String originalPublisher = "publisher";
        String normalizeTo = "normalized";
        String channelID = "testChannelID";
        String mediaType = "r";
        String note = "note that should be ignored";
        
        String line = String.format("%s\t%s\t%s\t%s\t", originalPublisher, normalizeTo, channelID, mediaType, note);
        
        PublisherMapping mapping = PublisherMappingLoader.stringToPublisherMapping(line);
        Assert.assertEquals(mapping.getOriginalPublisher(), originalPublisher);
        Assert.assertEquals(mapping.getNormalizeTo(), normalizeTo);
        Assert.assertEquals(mapping.getChannelID(), channelID);
        Assert.assertEquals(mapping.getMediaType(), PublisherMappingLoader.RADIO_MEDIA_TYPE);
    }
    
    @Test
    public void loadWellFormedTVMappingLineTest() throws PublisherMappingException {
        String originalPublisher = "publisher";
        String normalizeTo = "normalized";
        String channelID = "testChannelID";
        String mediaType = "t";
        String note = "note that should be ignored";
        
        String line = String.format("%s\t%s\t%s\t%s\t", originalPublisher, normalizeTo, channelID, mediaType, note);
        
        PublisherMapping mapping = PublisherMappingLoader.stringToPublisherMapping(line);
        Assert.assertEquals(mapping.getOriginalPublisher(), originalPublisher);
        Assert.assertEquals(mapping.getNormalizeTo(), normalizeTo);
        Assert.assertEquals(mapping.getChannelID(), channelID);
        Assert.assertEquals(mapping.getMediaType(), PublisherMappingLoader.TV_MEDIA_TYPE);
    }
    
    @Test(expectedExceptions = {PublisherMappingException.class})
    public void failLoadOfMalformedMediaTypeMappingLineTest() throws PublisherMappingException {
        String originalPublisher = "publisher";
        String normalizeTo = "normalized";
        String channelID = "testChannelID";
        String mediaType = "a";
        String note = "note that should be ignored";
        
        String line = String.format("%s\t%s\t%s\t%s\t", originalPublisher, normalizeTo, channelID, mediaType, note);
        
        PublisherMapping mapping = PublisherMappingLoader.stringToPublisherMapping(line);
    }
    
}
