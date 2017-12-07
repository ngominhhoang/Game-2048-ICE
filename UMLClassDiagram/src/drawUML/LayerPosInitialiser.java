package drawUML;

import analyzingProject.ClassObj;
import analyzingProject.ClassRelation;

import javax.management.relation.Relation;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by VAIO on 03-Dec-17.
 */
public class LayerPosInitialiser {
    private int maxWidth = 6000;
    private int maxHeight = 6000;
    ArrayList<Box> listOfBox = new ArrayList<Box>();

    LayerPosInitialiser(ArrayList<Layer> listOfLayer) {

        listOfBox.add(new Box());
        for (Layer layer : listOfLayer) {
            Box box = new Box(layer);
            listOfBox.add(box);
            box.index = listOfBox.indexOf(box);
        }

        for (Box box : listOfBox) {
            if (box.layer == null) continue;
            ClassObj obj = box.layer.getObj();
            ArrayList<ClassRelation> listOfRela = obj.getListOfRelation();
            for (ClassRelation rela : listOfRela) {
                Layer targetLayer = rela.getObj().getLayer();
                for (Box box1 : listOfBox)
                    if (box1.layer == targetLayer) {
                        box.add(box1);
                        box1.add(box);
                        //System.out.println(box.layer.getObj().getName()+" "+box1.layer.getObj().getName());
                    }
            }
        }
    }

    public void initialisePos() {
        listOfBox = placingBox(listOfBox);
        for (Box box : listOfBox) {
            Layer layer = box.layer;
            if (layer == null) continue;
            layer.setXaxis(box.x);
            layer.setYaxis(box.y);
        }
    }

    private int calculateWidth(ArrayList<Box> List) {
        int output = 0;
        for (Box box : List) {
            output = Math.max(output,box.width+box.x);
        }
        return output;
    }

    private int calculateHeight(ArrayList<Box> List) {
        int output = 0;
        for (Box box : List) {
            output = Math.max(output,box.height+box.y);
        }
        return output;
    }

    private boolean checkDistance(int limitDistance,int xPos,int yPos,ArrayList<Box> groupList,Rectangle targetRec) {
        for (Box box : groupList) {
            Point sPoint = new Point(box.x+xPos+box.width/2,box.y+yPos+box.height/2);
            Point ePoint = new Point(targetRec.x+targetRec.width/2,targetRec.y+targetRec.height/2);
            if (sPoint.distance(ePoint) - Math.sqrt(Math.pow(box.width,2)+Math.pow(box.height,2))/2
                    - Math.sqrt(Math.pow(targetRec.width,2)+Math.pow(targetRec.height,2))/2  < limitDistance) return false;
        }
        return true;
    }

    private ArrayList<Box> placingBox(ArrayList<Box> inputList) {

        ArrayList<ArrayList<Box> > mixGroupList = new ArrayList<ArrayList<Box> >() ;
        ArrayList<Box> outputList = new ArrayList<Box>();
        int listIndex = 0;
        Box centralBox = new Box();
        for (Box box : inputList) {
            if (box.layer == null) {
                centralBox = box;
                break;
            }
            if (centralBox.getListOfConnectedBox().size() <= box.getListOfConnectedBox().size() ) {
                centralBox = box;
            }
        }

        boolean[] dx = new boolean[101];
        Arrays.fill(dx,false);
        dx[centralBox.index] = true;
        if (centralBox.layer != null) {
            centralBox.x = 100;centralBox.y = 100;
        }
            else {
            centralBox.x = 6000;centralBox.y = 6000;
        }
        outputList.add(centralBox);
        if (inputList.size() == 1) return outputList;

        for (Box box : inputList) {
            int boxIndex = box.index;
            if (dx[boxIndex] || box == centralBox || !inputList.contains(box)) continue;
            ArrayList<Box> groupBox = new ArrayList<Box>();
            Queue<Box> queue = new LinkedList<Box>();
            queue.add(box);
            groupBox.add(box);
            dx[boxIndex] =true;

            while (!queue.isEmpty()) {
                Box head = queue.peek();
                queue.remove();

                for (Box box1 : head.getListOfConnectedBox()) {
                    int index = box1.index;
                    if (dx[index] || box1 == centralBox || !inputList.contains(box1)) continue;
                    dx[index] = true;
                    queue.add(box1);
                    groupBox.add(box1);
                }
            }

            groupBox = placingBox(groupBox);
            mixGroupList.add(groupBox);
        }

        for (int i=0; i < mixGroupList.size() ;++i)
            for (int j=i+1; j< mixGroupList.size() ;++j) {
                ArrayList<Box> groupList1 = mixGroupList.get(i);
                ArrayList<Box> groupList2 = mixGroupList.get(j);
                int square1 = calculateWidth(groupList1)*calculateHeight(groupList1);
                int square2 = calculateWidth(groupList2)*calculateHeight(groupList2);
                if (square1 < square2) {
                    mixGroupList.set(i,groupList2);
                    mixGroupList.set(j,groupList1);
                }
            }

        int base = 15;
        ArrayList<Integer> incX = new ArrayList<Integer>(Arrays.asList(base,base,-2*base,-2*base));
        ArrayList<Integer> incY = new ArrayList<Integer>(Arrays.asList(-2*base,2*base,base,-base));
        int limitDistance = 50;

        for (ArrayList<Box> groupList : mixGroupList) {

            int groupWidth = calculateWidth(groupList);
            int groupHeight = calculateHeight(groupList);
            Queue<Point> indexQueue = new LinkedList<Point>();
            int Xpos = 0 , Ypos = 0 , grIndex = mixGroupList.indexOf(groupList)%4;

            boolean[][] checkIndex = new boolean[1501][1501];
            for (int i=0 ; i<1501; ++i)
                for (int j=0; j<1501; ++j)
                    checkIndex[i][j] = false;

            indexQueue.add(new Point(incX.get(grIndex),0));
            indexQueue.add(new Point(0,incY.get(grIndex)));
            checkIndex[Math.abs(incX.get(grIndex))/base][0] = true;
            checkIndex[0][Math.abs(incY.get(grIndex))/base] = true;

            Rectangle recGroup = new Rectangle(Xpos,Ypos,groupWidth,groupHeight);


            while (Xpos < maxWidth && Ypos < maxHeight && Xpos > -maxWidth && Ypos > -maxHeight) {
                boolean check = true;
                //System.out.println(Xpos+" "+Ypos);
                for (Box box : outputList)  {
                    Rectangle recBox = new Rectangle(box.x,box.y,box.width,box.height);
                    if (recBox.intersects(recGroup) || !checkDistance(limitDistance,Xpos,Ypos,groupList,recBox)) {
                        recGroup = new Rectangle(Xpos,Ypos,groupWidth,groupHeight);
                        check = false;
                        break;
                    }
                }
                if (!check) {
                    Xpos = indexQueue.peek().x;
                    Ypos = indexQueue.peek().y;
                    indexQueue.remove();
                    if (Math.random()%2 == 0) {
                        if (!checkIndex[Math.abs(Xpos + incX.get(grIndex))/base][Math.abs(Ypos)/base]) {
                            indexQueue.add(new Point(Xpos + incX.get(grIndex), Ypos));
                            checkIndex[Math.abs(Xpos + incX.get(grIndex))/base][Math.abs(Ypos)/base] = true;
                        }
                        if (!checkIndex[Math.abs(Xpos)/base][Math.abs(Ypos + incY.get(grIndex))/base]) {
                            indexQueue.add(new Point(Xpos, Ypos + incY.get(grIndex)));
                            checkIndex[Math.abs(Xpos)/base][Math.abs(Ypos + incY.get(grIndex))/base] = true;
                        }
                    }
                        else {
                        if (!checkIndex[Math.abs(Xpos)/base][Math.abs(Ypos + incY.get(grIndex))/base]) {
                            indexQueue.add(new Point(Xpos, Ypos + incY.get(grIndex)));
                            checkIndex[Math.abs(Xpos)/base][Math.abs(Ypos + incY.get(grIndex))/base] = true;
                        }

                        if (!checkIndex[Math.abs(Xpos + incX.get(grIndex))/base][Math.abs(Ypos)/base]) {
                            indexQueue.add(new Point(Xpos + incX.get(grIndex), Ypos));
                            checkIndex[Math.abs(Xpos + incX.get(grIndex))/base][Math.abs(Ypos)/base] = true;
                        }
                    }
                    recGroup = new Rectangle(Xpos,Ypos,groupWidth,groupHeight);
                }
                    else {

                    for (Box box : groupList) {
                        box.x += Xpos;
                        box.y += Ypos;
                        outputList.add(box);
                    }
                    break;
                }
            }
        }
        int negaX = 10000 , negaY = 10000;
        for (Box box : outputList) {
            negaX = Math.min(negaX,box.x);
            negaY = Math.min(negaY,box.y);
        }

        for (Box box : outputList) {
            box.x = box.x - negaX + 20;
            box.y = box.y - negaY + 20;
        }
        return outputList;
    }
}

class Box {
    int x;
    int y;
    int index;
    int width;
    int height;
    Layer layer;
    ArrayList<Box> listOfConnectedBox;
    Box(Layer layer) {
        listOfConnectedBox = new ArrayList<Box>();
        this.layer = layer;
        this.x = 0;
        this.y = 0;
        this.index = 0;
        this.width = layer.getWidth();
        this.height = layer.getHeight();
    }
    Box() {
        this.x = 0; this.y = 0; this.width = 0 ; this.height = 0 ; layer = null;
        listOfConnectedBox = new ArrayList<Box>();
    }
    public void add(Box box) {
        listOfConnectedBox.add(box);
    }

    public void setListOfConnectedBox(ArrayList<Box> listOfConnectedBox) {
        this.listOfConnectedBox = listOfConnectedBox;
    }

    public ArrayList<Box> getListOfConnectedBox() {
        return listOfConnectedBox;
    }
}
