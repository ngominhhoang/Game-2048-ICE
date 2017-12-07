package analyzingProject;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by VAIO on 12-Oct-17.
 */
public class ClassObjManager {
    private ArrayList<ClassObj> classObjList ;
    private ArrayList<String> nameList;

    private boolean isNormalWord(char x) {
        int xNum = (int) x;
        return  ((xNum>=48 && xNum<=57) || (xNum>=65 && xNum<=90) || (xNum>=97&&xNum<=122) || (xNum == 95)) ;
    }
    public ClassObjManager(String path) {
        classObjList = new ArrayList<ClassObj>();
        nameList = new ArrayList<String>();

        File folder = new File(path);
        //System.out.println(path);

        Queue<File> Q = new LinkedList<File>();
        Q.add(folder);
        while (!Q.isEmpty()) {

            File fo = Q.peek();
            Q.remove();

            File[] listOfFiles = fo.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {

                String extentPath = listOfFiles[i].getName().substring(listOfFiles[i].getName().indexOf(".")+1);

                if (listOfFiles[i].isFile() && extentPath.equals("java")) {
                    analyzeFileAssistant assistant = new analyzeFileAssistant(listOfFiles[i]);
                    addClassObjList(assistant.analyzeElement());
                }
                if (listOfFiles[i].isDirectory()) Q.add(listOfFiles[i]);
            }

        }

        addNameList(getClassObjList());
        ArrayList<String> nameList = getNameList();
        //for (String s : nameList) System.out.println(s);

        ArrayList<ClassObj> listOfClassObj = getClassObjList();

        for (ClassObj obj : listOfClassObj) {
            //System.out.println(obj.getListOfWord().size());
            ArrayList<String> listOfWord = obj.getListOfWord();
            ArrayList<String> abandonedWord = new ArrayList<String>();
            abandonedWord.add(obj.getName());

            int isInside = 0, quoMark = 0, singleQuoMark = 0, implementsFlag = 0,extendsFlag = 0,innerFlag = 0,parenthesisFlag = 0;//mo ngoac;
            String suffixString = new String("");
            for (int i=0 ; i< listOfWord.size(); ++i) {
                String currWord = listOfWord.get(i);
                suffixString = suffixString + currWord;

                if (currWord.equals("implements") && isInside == 0) {implementsFlag = 1;extendsFlag =0;continue;}
                if (currWord.equals("extends") && isInside == 0) {extendsFlag = 1;implementsFlag = 0;continue;}
                if (currWord.equals("{")) {++ isInside;suffixString = "";implementsFlag = 0;extendsFlag = 0;continue;}
                if (currWord.equals("}")) {-- isInside;suffixString = "";continue;}
                if (currWord.equals("(")) {++ parenthesisFlag;suffixString = "";continue;}
                if (currWord.equals(")")) {-- parenthesisFlag;suffixString = "";continue;}
                if ((currWord.equals("class") || currWord.equals("interface")) && (isInside != 0)) {innerFlag = 1;continue;}
                if (currWord.charAt(0) == '"') {quoMark = 1 - quoMark;continue;}
                if (currWord.charAt(0) == '\'') {singleQuoMark = 1 - singleQuoMark;continue;}

                if (implementsFlag == 1) {
                    if (listOfWord.get(i+1).equals(".")||listOfWord.get(i).equals(".")) continue;
                    ClassRelation rela = new ClassRelation();
                    rela.setTypeOfRelation("implementation");
                    rela.setObj(findObj(listOfClassObj,currWord,obj,suffixString));
                    if (rela.getObj() != null && !rela.getObj().getName().equals(obj.getName())) {
                        //abandonedWord.add(currWord);
                        obj.addRelation(rela);
                    }
                    suffixString = "";
                    continue;
                }

                if (extendsFlag == 1 ) {
                    if (listOfWord.get(i+1).equals(".")||listOfWord.get(i).equals(".")) continue;
                    ClassRelation rela = new ClassRelation();
                    rela.setTypeOfRelation("inheritance");
                    rela.setObj(findObj(listOfClassObj,currWord,obj,suffixString));

                    if (rela.getObj() != null && !rela.getObj().getName().equals(obj.getName())) {
                        //abandonedWord.add(currWord);
                        obj.addRelation(rela);
                    }
                    suffixString = "";
                    continue;
                }

                if (innerFlag == 1) {
                    ClassRelation rela = new ClassRelation();
                    rela.setTypeOfRelation("inner");
                    rela.setObj(findObj(listOfClassObj,currWord,obj,suffixString));
                    if (rela.getObj() != null && !rela.getObj().getName().equals(obj.getName())) {
                        //abandonedWord.add(currWord);
                        obj.addRelation(rela);
                    }
                    innerFlag = 0;
                    continue;
                }

                if (currWord.equals(";")) {
                    quoMark = 0;
                    singleQuoMark = 0;
                    suffixString = "";
                    continue;
                }


                /*if (isInside == 1 && parenthesisFlag == 0 && !abandonedWord.contains(currWord) && nameList.contains(currWord) &&
                        quoMark == 0 && singleQuoMark == 0) {

                    ClassRelation rela = new ClassRelation();
                    rela.setTypeOfRelation("association");
                    if (obj.getName().equals("analyzeFileAssistant"))
                        System.out.println(currWord);

                    rela.setObj(findObj(listOfClassObj,currWord,obj,suffixString));

                    if (rela.getObj() != null) {
                        abandonedWord.add(currWord);
                        obj.addRelation(rela);
                    }
                    continue;
                }*/

            }
            ArrayList<Element> listOfElement = obj.getListOfElement();
            for (Element element : listOfElement) {
                ClassRelation rela = new ClassRelation();
                rela.setTypeOfRelation("association");
                String elementType = element.getType();

                for (String objName : nameList) {

                    if (elementType.contains(objName)) {
                        int bPos = elementType.indexOf(objName);
                        int ePos = bPos + objName.length()-1;
                        if ( ( bPos==0 || (bPos>0 &&  !isNormalWord(elementType.charAt(bPos-1))) )
                            && ( ePos==elementType.length()-1 || (ePos<elementType.length()-1 &&  !isNormalWord(elementType.charAt(ePos+1))) ) ) {
                            elementType = objName;
                            break;
                        }
                    }
                }

                rela.setObj(findObj(listOfClassObj,elementType,obj,element.getType()));

                if (nameList.contains(elementType) && !abandonedWord.contains(elementType) && rela.getObj() != null) {
                    abandonedWord.add(elementType);
                    obj.addRelation(rela);
                }
            }
            //System.out.println(obj.getListOfRelation().size());
        }
    }

    private ClassObj findObj(ArrayList<ClassObj> listOfObj,String name,ClassObj targetObj,String suffixString) {
        ClassObj res = null;
        int finalLevel = 0;
        for (ClassObj obj: listOfObj) {
            int presentLevel = 0;
            if (obj.getName().equals(name)) {
                presentLevel = 1;
                String packageName = obj.getPackageName();
                String objName = obj.getName();

                if (!packageName.equals("")) {
                    ArrayList<String> listOfImport = targetObj.getListOfImport();
                    for (String importName : listOfImport) {
                        if (importName.equals(packageName + ".*")) {
                            presentLevel = 2;
                            break;
                        }
                    }

                    if (packageName.equals(targetObj.getPackageName())) {
                        presentLevel = 3;
                    }

                    for (String importName : listOfImport) {
                        if (importName.equals(packageName + "." + objName)) {
                            presentLevel = 4;
                            break;
                        }
                    }
                    if (suffixString.contains(packageName)) {
                        presentLevel = 5;
                    }
                }
            }
            if (presentLevel > finalLevel) {
                finalLevel = presentLevel;
                res = obj;
            }
        }
        return res;
    }

    public void setNameList(ArrayList<String> nameList) {
        this.nameList = nameList;
    }

    public ArrayList<ClassObj> getClassObjList() {
        return classObjList;
    }

    public ArrayList<String> getNameList() {
        return nameList;
    }

    public void addClassObjList(ClassObj obj) {
        classObjList.add(obj);
    }

    public void addClassObjList(ArrayList<ClassObj> classObjList) {
        for (ClassObj obj : classObjList)
            this.classObjList.add(obj);
    }

    public void addNameList(String name) {
        nameList.add(name);
    }

    public void addNameList(ArrayList<ClassObj> objList) {
        for (ClassObj obj : objList)
            nameList.add(obj.getName());
    }
}
