package analyzingProject;
import java.io.File;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by VAIO on 07-Oct-17.
 */
public class analyzeFileAssistant {
    private ArrayList<String> listOfWord;
    private ArrayList<String> accessLevelWord = new ArrayList<String>(Arrays.asList(new String[] {"public","private","protected"}));
    private ArrayList<String> keyWord = new ArrayList<String>(Arrays.asList(new String[] {"class","interface"}));
    private ArrayList<String> abandonedWord = new ArrayList<String>(Arrays.asList(new String[] {"synchronized","transient","volatile","native","final"}));
    private File folder;
    analyzeFileAssistant(File folder) {
        this.folder = folder;
        listOfWord = new ArrayList<String>(separateFile());
    }

    private boolean isNormalWord(char x) {
        int ASCnum = (int) x;
        if ((x == '_') || (ASCnum>=65&&ASCnum<=90) || (ASCnum>=97&&ASCnum<=122) || (ASCnum>=48&&ASCnum<=57)) return true;
        return false;
    }

    private ArrayList<String> separateFile() {

        ArrayList<String> currS = new ArrayList<String>();
        try {
            Scanner scanner = new Scanner(folder);
            int cmtFlag = 1;
            int isQuoMark = 0;
            while (scanner.hasNextLine()) {
                String temS = new String(scanner.nextLine());
                if (temS.length() == 0) continue;
                temS = temS + " ";
                int beginPos = 0,wordFlag = 1,parenthesesFlag = 0,braceFlag = 0;
                String currWord = new String("");
                isQuoMark = 0;

                //while (beginPos<temS.length()&&temS.charAt(beginPos) == ' ') ++beginPos;
                //System.out.println(temS);
                for (int currPos = 0; currPos < temS.length() ; ++currPos ) {
                    //    if (temS == "//Method ") System.out.println(currPos);
                    // Xu li cmt . isQuoMark dung de fix bug khi String co ki tu // hoac /*
                    if (temS.charAt(currPos) == '"') isQuoMark = 1-isQuoMark;
                    if (currPos < temS.length() - 1 && temS.substring(currPos, currPos + 2).equals("//") && isQuoMark == 0) break;
                    if (currPos < temS.length() - 1 && temS.substring(currPos, currPos + 2).equals("/*") && isQuoMark == 0) {
                        cmtFlag = 0;
                        continue;
                    }
                    if (cmtFlag == 0) {
                        if (currPos < temS.length() - 1 && temS.substring(currPos, currPos + 2).equals("*/")) {
                            cmtFlag = 1;
                            ++currPos;
                            currWord = "";
                        }
                        continue;
                    }
                    if (temS.charAt(currPos) == '@') break;

                    //Xu li thong thuong
                    if (!isNormalWord(temS.charAt(currPos))) {
                        if (!currWord.isEmpty() && temS.charAt(currPos) != '\t') currS.add(currWord);

                        currWord = "";
                        currWord += temS.charAt(currPos);

                        if (temS.charAt(currPos) == '[' && temS.charAt(currPos+1) == ']') {
                            currWord += ']';
                            currPos++;
                            //System.out.println(currWord);
                            currS.add(currWord);
                            continue;
                        }
                        // Kiem tra ki tu ' ' va ki tu TAB
                        if (temS.charAt(currPos) != ' ' && temS.charAt(currPos) != '\t') currS.add(currWord);
                        currWord = "";
                    } else {
                        currWord = currWord + temS.charAt(currPos);
                    }
                }
            }

            scanner.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //for (int i=0; i<currS.size(); ++i)
        //    System.out.println(currS.get(i));
        return currS;
    }

    public ArrayList<ClassObj> analyzeElement() {
        /*if (folder.getName().equals("FileManager.java")) {
            for (String word : listOfWord)
                System.out.println(word);
        }*/

        int currPos = 0;
        Stack<AnalyzingTool> stack = new Stack<AnalyzingTool>();
        AnalyzingTool aTool = new AnalyzingTool();
        ArrayList<ClassObj> listOfClassObj = new ArrayList<ClassObj>();
        int isQuoMark = 0;
        int isSingleQuoMark = 0;
        int commaFlag = 0;
        int equalFlag = 0;
        int packageFlag = 0;
        int importFlag = 0;
        String packageString = new String(""), importString = new String("");
        ArrayList<String> listOfImport = new ArrayList<String>();
        Element subAtt = new Attribute();

        boolean getClassName = false;
        while (currPos < listOfWord.size()) {

            String currWord = listOfWord.get(currPos);
            if (!stack.empty()) aTool.addListOfWord(currWord);
            //System.out.println(currWord);

            if (currWord.charAt(0) == '"') isQuoMark = 1 - isQuoMark;
            if (currWord.charAt(0) == '\'') isSingleQuoMark = 1 - isSingleQuoMark;

            // tim tu khoa class hoac interface
            if (keyWord.contains(currWord) && (stack.empty()||isQuoMark == 0) && (equalFlag == 0 && isSingleQuoMark == 0 && commaFlag == 0)) {
                if (!stack.empty() && aTool.isInside > 1) {
                    ++currPos;
                    continue;
                }
                aTool = new AnalyzingTool();
                aTool.keyWord = currWord;
                aTool.packageName = packageString;
                aTool.listOfImport = listOfImport;
                packageString = new String("");
                listOfImport = new ArrayList<String>();
                stack.add(aTool);
                getClassName = true;
                ++currPos;
                continue;
            }

            if (stack.empty()) {
                // Xu ly trung ten package import
                if (currWord.equals("package")) {
                    packageFlag = 1;++currPos;continue;
                }
                if (currWord.equals("import")) {
                    importFlag = 1;++currPos;continue;
                }
                if (currWord.equals(";")) {
                    if (importFlag == 1) {
                        listOfImport.add(importString);
                        importFlag = 0;
                        importString = new String();
                    }
                    packageFlag = 0;
                }

                if (packageFlag == 1) {
                    packageString = packageString + currWord;
                }
                if (importFlag == 1) {
                    importString = importString + currWord;
                }
                ++currPos;
                continue;
            }

            // tim ten cua object
            if (getClassName) {
                stack.peek().name = new String(currWord);
                getClassName = false;
                ++currPos;
                continue;
            }

            if (currWord.equals("{") && equalFlag == 0) {
                aTool.stepFlag = 0;
                ++aTool.isInside;
                ++currPos;
                //System.out.println('{');
                continue;
            }

            if (currWord.equals("}") && equalFlag == 0) {
                --aTool.isInside;
                //System.out.println('}');
                //System.out.println(currWord+" "+aTool.isInside);
                ++currPos;
                if (aTool.isInside == 0) {
                    ClassObj obj = new ClassObj();
                    obj.setName(aTool.name);
                    obj.setListOfElement(aTool.listOfElement);
                    obj.setPath(folder.getPath());
                    obj.setListOfWord(aTool.getListOfWord());
                    obj.setKeyWord(aTool.keyWord);
                    obj.setPackageName(aTool.packageName);
                    obj.setListOfImport(aTool.listOfImport);
                    listOfClassObj.add(obj);
                    //System.out.println(obj.getListOfWord().size());

                    AnalyzingTool subTool = aTool;
                    stack.pop();
                    if (!stack.empty()) {
                        aTool = stack.peek();
                        aTool.addListOfWord(subTool.getListOfWord());

                    }
                }
                continue;
            }

            if (currWord.equals(";")) {
                ++currPos;
                commaFlag = 0;
                equalFlag = 0;
                aTool.stepFlag = 0;
                continue;
            }

            //stepFlag = 2 : da hoan thanh xong 1 attribute or method va dang xu ly nhung phan sau . Vi du int x = 5,y,z=3;
            if (aTool.stepFlag == 2 && aTool.isInside == 1) {

                if (currWord.equals("=")) ++equalFlag;
                if (currWord.equals("{") || currWord.equals("[") || currWord.equals("<") || currWord.equals("(") ) ++commaFlag;
                if (currWord.equals("}") || currWord.equals("]") || currWord.equals(">") || currWord.equals(")")) --commaFlag;
                //System.out.println(currWord + commaFlag);
                if (currWord.equals(",") && commaFlag == 0 && isQuoMark == 0 && isSingleQuoMark == 0) {
                    subAtt.setName(listOfWord.get(currPos+1));
                    aTool.listOfElement.add(subAtt);
                    subAtt = new Attribute(subAtt);
                    equalFlag = 0;
                }
                ++currPos;
                continue;
            }

            //Neu trong function thi ko xet , chi xet ngoai function
            if (aTool.isInside != 1) {++currPos;continue;}

            //System.out.println(currWord);
            //System.out.println(aTool.isInside);
            //identify Constructor
            if (currWord.equals(aTool.name) && listOfWord.get(currPos+1).charAt(0) == '(') aTool.stepFlag = 1;

            if (accessLevelWord.contains(currWord)) {
                aTool.ele.setAccessLevel(currWord);
                ++currPos;
                continue;
            }

            if (currWord.equals("static")) {
                aTool.ele.setStaticProperty(true);
                ++currPos;
                continue;
            }

            if (currWord.equals("abstract")) {
                aTool.ele.setAbstractProperty(true);
                ++currPos;
                continue;
            }

            if (currWord.equals("final")) {
                aTool.ele.setFinalProperty(true);
                ++currPos;
                continue;
            }

            if (abandonedWord.contains(currWord)) {
                ++currPos;
                continue;
            }

            if (currWord.equals("[]")) {
                aTool.isArray = true;
                ++currPos;
                continue;
            }

            //Neu aTool.stepFlag == 1 thi ko xet type vi la constructor
            if (aTool.stepFlag == 0) {
                String currType = "";
                //loai bo ten package . Vi du drawUML.Layer.getName() = ....
                currType += currWord;
                while (listOfWord.get(currPos+1).equals(".")) {
                    currType += ".";
                    currType += listOfWord.get(currPos+2);
                    currPos = currPos + 2;
                    aTool.addListOfWord(".");
                    aTool.addListOfWord(listOfWord.get(currPos));
                }
                currWord = listOfWord.get(currPos);
                //System.out.println(currWord + currPos);
                if (listOfWord.get(currPos+1).charAt(0) == '<') {
                    int i = currPos +1;

                    while (listOfWord.get(i).charAt(0) != '>') {
                        currType += listOfWord.get(i) + ' ';
                        ++i;
                        aTool.addListOfWord(listOfWord.get(i));
                    }
                    currType += listOfWord.get(i);
                    currPos = i+1;
                }
                else currPos ++;
                //System.out.println(currType);
                aTool.ele.setType(currType);
                ++aTool.stepFlag;
                continue;
            }

            aTool.ele.setName(currWord);
            if (listOfWord.get(currPos+1).charAt(0) == '(') {
                String currPara = "";
                int i = currPos +1;
                aTool.addListOfWord(listOfWord.get(i));
                while (listOfWord.get(i).charAt(0) != ')') {
                    currPara += listOfWord.get(i) + ' ';
                    ++i;
                    aTool.addListOfWord(listOfWord.get(i));
                }
                currPara += listOfWord.get(i);
                currPos = i+1;

                Method currMed = new Method(aTool.ele);
                currMed.setParameter(currPara);
                aTool.listOfElement.add(currMed);
                aTool.stepFlag = 2;
            }
            else {
                currPos ++;

                Attribute currAtt = new Attribute(aTool.ele);
                currAtt.setArrayProperty(aTool.isArray);
                subAtt = new Attribute(currAtt);
                aTool.listOfElement.add(currAtt);
                aTool.stepFlag = 2;
            }
            aTool.ele = new Element();
        }
        //System.out.println(listOfClassObj.size());
        return listOfClassObj;
    }
}
