package engagements.firmservice.domain;

public class CustomerProductTemplates {

    private Long firmId;
    private Long customerId;
    private Long productTemplateId;
    private String currentVersion;

    public CustomerProductTemplates(Long firmId, Long customerId, Long productTemplateId, String currentVersion) {
        this.firmId = firmId;
        this.customerId = customerId;
        this.productTemplateId = productTemplateId;
        this.currentVersion = currentVersion;
    }

    public Long getFirmId() {
        return firmId;
    }

    public void setFirmId(Long firmId) {
        this.firmId = firmId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getProductTemplateId() {
        return productTemplateId;
    }

    public void setProductTemplateId(Long productTemplateId) {
        this.productTemplateId = productTemplateId;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }
}
