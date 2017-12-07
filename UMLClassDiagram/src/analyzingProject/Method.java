package analyzingProject;
import java.util.ArrayList;
import java.util.Queue;

/**
 * Created by VAIO on 07-Oct-17.
 */
public class Method extends Element {
    private String parameter;

    Method() {
        parameter = new String();
    }
    Method(Element ele) {
        this.setName(ele.getName());
        this.setAccessLevel(ele.getAccessLevel());
        this.setType(ele.getType());
        this.setStaticProperty(ele.isStaticProperty());
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
