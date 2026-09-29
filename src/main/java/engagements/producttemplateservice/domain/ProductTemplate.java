package engagements.producttemplateservice.domain;

public class ProductTemplate {

    private Long id;
    private String name;
    private String version;
    private String content;

    public ProductTemplate(Long id, String name, String version, String content) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
