package engagements.producttemplateservice.services;

public interface AddNewProductTemplateVersion {

    boolean addVersion(Long productTemplateId, String name, String version, String content);
}
