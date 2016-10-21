package dk.statsbiblioteket.doms.dhp.mapper;

public class PublisherMappingException extends Exception {
    
    public static enum FailureType {NUMBER_OF_PARTS, MEDIA_TYPE}
    
    private final FailureType type;
    
    public PublisherMappingException(String message, FailureType type) {
        super(message);
        this.type = type;
    }
    
    public FailureType getFailureType() {
        return type;
    }

}
