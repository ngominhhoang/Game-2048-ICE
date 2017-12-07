package analyzingProject;
/**
 * Created by VAIO on 17-Oct-17.
 */
public class ClassRelation {
    private ClassObj obj;
    private String typeOfRelation;

    ClassRelation () {
        typeOfRelation = "none";
    }

    public void setObj(ClassObj obj) {
        this.obj = obj;
    }

    public void setTypeOfRelation(String typeOfRelation) {
        this.typeOfRelation = typeOfRelation;
    }

    public ClassObj getObj() {
        return obj;
    }

    public String getTypeOfRelation() {
        return typeOfRelation;
    }
}
