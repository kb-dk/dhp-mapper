package dk.statsbiblioteket.doms.dhp.mapper;

public class PublisherMapping {

    private final String originalPublisher;
    private final String normalizeTo;
    private final String channelID;
    private final String mediaType;
    
    public PublisherMapping(String originalPublisher, String normalizeTo, String channelID, String mediaType) {
        this.originalPublisher = originalPublisher;
        this.normalizeTo = normalizeTo;
        this.channelID = channelID;
        this.mediaType = mediaType;
    }

    public String getOriginalPublisher() {
        return originalPublisher;
    }

    public String getNormalizeTo() {
        return normalizeTo;
    }

    public String getChannelID() {
        return channelID;
    }

    public String getMediaType() {
        return mediaType;
    }
    
    @Override
    public String toString() {
        return "PublisherMapping [originalPublisher=" + originalPublisher + ", normalizeTo=" + normalizeTo
                + ", channelID=" + channelID + ", mediaType=" + mediaType + "]";
    }
}
