package lakhmani.neelabh.accelerator.dto;
import java.util.List;

public class    GenerationRequest {
    private String entityName;
    private List<Property> properties;

    // Constructors, getters, and setters


    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }
}
