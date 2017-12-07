package analyzingProject;
import java.util.ArrayList;

/**
 * Created by VAIO on 12-Oct-17.
 */
public class AnalyzingTool {
    public Element ele = new Element();
    public ArrayList<Element> listOfElement = new ArrayList<Element>();
    public int stepFlag = 0;
    public int isInside = 0;
    public boolean isArray = false;
    public String name ;
    public String keyWord;
    public String packageName;
    public ArrayList<String> listOfImport;
    private ArrayList<String> listOfWord;

    AnalyzingTool() {
        listOfWord = new ArrayList<String>();
    }

    public void addListOfWord(String word) {
        listOfWord.add(word);
    }

    public void addListOfWord(ArrayList<String> wordList) {
        for (String word : wordList)
            listOfWord.add(word);
    }

    public ArrayList<String> getListOfWord() {
        return listOfWord;
    }
}
