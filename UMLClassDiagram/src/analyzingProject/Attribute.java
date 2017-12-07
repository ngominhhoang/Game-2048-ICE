/**
 * Created by VAIO on 07-Oct-17.
 */
package analyzingProject;
public class Attribute extends Element {

    private boolean arrayProperty;

    Attribute() {
        arrayProperty = false;
    }

    Attribute(Element ele) {
        this.setName(ele.getName());
        this.setAccessLevel(ele.getAccessLevel());
        this.setType(ele.getType());
        this.setStaticProperty(ele.isStaticProperty());
        arrayProperty = false;
    }

    Attribute(Attribute ele) {
        this.setName(ele.getName());
        this.setAccessLevel(ele.getAccessLevel());
        this.setType(ele.getType());
        this.setStaticProperty(ele.isStaticProperty());
        this.setArrayProperty(ele.isArrayProperty());
    }

    public void setArrayProperty(boolean arrayProperty) {
        this.arrayProperty = arrayProperty;
    }

    public boolean isArrayProperty() {
        return arrayProperty;
    }
}
