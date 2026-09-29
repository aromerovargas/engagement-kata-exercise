package engagements.firmservice.domain;

public class Customer {

    private Long id;
    private Long firmId;
    private String name;

    public Customer(Long id, Long firmId, String name) {
        this.id = id;
        this.firmId = firmId;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFirmId() {
        return firmId;
    }

    public void setFirmId(Long firmId) {
        this.firmId = firmId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
