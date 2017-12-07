package analyzingProject;
/**
 * Created by VAIO on 07-Oct-17.
 */


public class Element {
    private String name;
    private String accessLevel;
    private String type;
    private boolean staticProperty;
    private boolean abstractProperty;
    private boolean finalProperty;

    Element() {
        this.accessLevel = new String("no modifiers");
        this.staticProperty = false;
        this.abstractProperty = false;
        this.type = "Constructor";
    }

    public void setFinalProperty(boolean finalProperty) {
        this.finalProperty = finalProperty;
    }

    public void setAbstractProperty(boolean abstractProperty) {
        this.abstractProperty = abstractProperty;
    }

    public void setStaticProperty(boolean staticProperty) {
        this.staticProperty = staticProperty;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public String getType() {
        return type;
    }

    public boolean isStaticProperty() {
        return staticProperty;
    }

    public boolean isAbstractProperty() {
        return abstractProperty;
    }

    public boolean isFinalProperty() {
        return finalProperty;
    }
}
