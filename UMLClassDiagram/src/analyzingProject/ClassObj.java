package analyzingProject;
import drawUML.*;
import java.util.ArrayList;

/**
 * Created by VAIO on 07-Oct-17.
 */
public class ClassObj {
    private String name;
    private String path;
    private Layer layer;
    private String keyWord;
    private ArrayList<Element> listOfElement;
    private ArrayList<String> listOfWord;
    private ArrayList<ClassRelation> listOfRelation;
    private ArrayList<String> listOfImport;
    private String packageName;

    ClassObj() {
        listOfWord = new ArrayList<String>();
        listOfRelation = new ArrayList<ClassRelation>();
    }

    public ArrayList<ClassRelation> getListOfRelation() {
        return listOfRelation;
    }

    public void addRelation(ClassRelation rela) {
        this.listOfRelation.add(rela);
    }

    public ArrayList<String> getListOfWord() {
        return listOfWord;
    }

    public void setListOfWord(ArrayList<String> listOfWord) {
        this.listOfWord = listOfWord;
    }

    public void setName(String name) {
        this.name = new String(name);
    }

    public String getName() {
        return name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setListOfElement(ArrayList<Element> listOfElement) {
        this.listOfElement = listOfElement;
    }

    public ArrayList<Element> getListOfElement() {
        return listOfElement;
    }

    public void setListOfImport(ArrayList<String> listOfImport) {
        this.listOfImport = listOfImport;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public ArrayList<String> getListOfImport() {
        return listOfImport;
    }

    public void setLayer(Layer layer) {
        this.layer = layer;
    }

    public Layer getLayer() {
        return layer;
    }
}
