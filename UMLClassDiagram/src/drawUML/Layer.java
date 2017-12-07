package drawUML;

import analyzingProject.*;
import com.sun.org.apache.xpath.internal.SourceTree;

import javax.imageio.ImageIO;
import javax.sound.sampled.Line;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by VAIO on 16-Nov-17.
 */
public class Layer {
    ClassObj obj;
    Graphics2D g2D;
    private int line_stroke;
    private Font headerFont ;
    private Font otherFont ;
    private int headerFontSize ;
    private int otherFontSize ;
    private int width;
    private int headerHeight;
    private int attributeHeight;
    private int methodHeight;
    private int line_spacing;
    private int line_indent;
    private int Xaxis;
    private int Yaxis;
    private int letterHeight = 0;
    private final int iconSize = 20;
    private BufferedImage imagePublic;
    private BufferedImage imagePrivate;
    private BufferedImage imageProtected;
    private BufferedImage imagePackage;
    private BufferedImage imageClass;
    private BufferedImage imageInterface;
    private boolean optimizeRight = true;
    private boolean showParameter = false;
    private boolean isVisible = true;

    private int optimizeWidth() {
        ArrayList<Element> listOfElement = obj.getListOfElement();
        int optimizedWidth = 0;
        g2D.setFont(headerFont);
        optimizedWidth = g2D.getFontMetrics().stringWidth(obj.getName());
        g2D.setFont(otherFont);
        for (Element element : listOfElement) {
            String printedString;
            printedString = element.getName() + " : " + element.getType();
            optimizedWidth = Math.max(optimizedWidth,g2D.getFontMetrics().stringWidth(printedString));
        }
        return optimizedWidth+line_indent*2+iconSize;
    }

    private int optimizeHeaderHeight() {
        int optimizedHeight = g2D.getFontMetrics(headerFont).charWidth('A') + line_spacing*2 ;
        return optimizedHeight;
    }

    private int optimizeAttributeHeight() {
        ArrayList<Element> listOfElement = obj.getListOfElement();
        int numOfAtt = 0;
        for (Element element : listOfElement) {
            if (element instanceof Attribute) ++numOfAtt;
        }

        //System.out.println(g2D.getFontMetrics(otherFont).charWidth('A'));
        return numOfAtt*(g2D.getFontMetrics(otherFont).charWidth('A')+line_spacing)+line_spacing;
    }

    private int optimizeMethodHeight() {
        ArrayList<Element> listOfElement = obj.getListOfElement();
        int numOfMed = 0;
        for (Element element : listOfElement) {
            if (element instanceof Method) ++numOfMed;
        }
        return numOfMed*(g2D.getFontMetrics(otherFont).charWidth('A')+line_spacing)+line_spacing;
    }

    Layer(ClassObj obj) {
        this.obj = obj;
        obj.setLayer(this);
        Xaxis = 50;
        Yaxis = 50;
        line_spacing = 14;
        line_indent = 8;
        headerFontSize = 22;
        otherFontSize = 18;
        headerFont = new Font("Arial",Font.BOLD,headerFontSize);
        otherFont = new Font("Arial",Font.PLAIN,otherFontSize);

        try {
            imagePublic = ImageIO.read(new File("src\\drawUML\\Icon\\Public.png"));
            imagePrivate = ImageIO.read(new File("src\\drawUML\\Icon\\Private.png"));
            imageProtected = ImageIO.read(new File("src\\drawUML\\Icon\\Protected.png"));
            imagePackage = ImageIO.read(new File("src\\drawUML\\Icon\\Package.png"));
            imageClass = ImageIO.read(new File("src\\drawUML\\Icon\\Class.png"));
            imageInterface = ImageIO.read(new File("src\\drawUML\\Icon\\Interface.png"));
        }
        catch (Exception e) {
            System.out.println(e);
        }

    }

    public void setWidth(int width) {
        g2D.setFont(headerFont);
        if (width>=g2D.getFontMetrics().stringWidth(obj.getName())+line_indent*2+iconSize+2) this.width = width;
    }

    public void setHeight(int height) {

        if (this.methodHeight > 0) {
            this.methodHeight = this.methodHeight + (height - this.getHeight());
            if (this.methodHeight < 0) {
                this.attributeHeight = Math.max(0, this.attributeHeight + this.methodHeight);
                this.methodHeight = 0;
            }
        }
            else {
            this.attributeHeight = Math.max(0,this.attributeHeight + (height - this.getHeight()));
            if (this.attributeHeight > optimizeAttributeHeight()) {
                this.methodHeight += this.attributeHeight - optimizeAttributeHeight();
                this.attributeHeight = optimizeAttributeHeight();
            }
        }
    }

    public void fixScaleHeight() {
        int writtenMethod = (int)(methodHeight/(line_spacing+letterHeight));
        int writtenAttribute = (int)(attributeHeight/(line_spacing+letterHeight));
        g2D.setFont(otherFont);
        int newHeight = g2D.getFontMetrics().charWidth('A');

        if (this.letterHeight != 0) {
            this.attributeHeight += (int) (newHeight - this.letterHeight) * writtenAttribute;
            this.methodHeight += (int) (newHeight - this.letterHeight) * writtenMethod;
        }
        //System.out.println(this.letterHeight+" "+newHeight);
        this.letterHeight = newHeight;
    }

    public void setXaxis(int xaxis) {
        Xaxis = xaxis;
    }

    public void setYaxis(int yaxis) {
        Yaxis = yaxis;
    }

    public void setLine_spacing(int line_spacing) {
        this.line_spacing = line_spacing;
    }

    public void setHeaderFontSize(int headerFontSize) {
        this.headerFontSize = headerFontSize;
    }

    public void setOtherFontSize(int otherFontSize) {
        this.otherFontSize = otherFontSize;
    }

    public void setG2D(Graphics2D g2D) {
        this.g2D = g2D;
    }

    public void setLine_stroke(int line_stroke) {
        this.line_stroke = line_stroke;
    }

    public int getXaxis() {
        return Xaxis;
    }

    public int getYaxis() {
        return Yaxis;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return headerHeight + attributeHeight + methodHeight ;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setShowParameter(boolean showParameter) {
        this.showParameter = showParameter;
    }

    public boolean isShowingParameter() {
        return showParameter;
    }

    public int getLine_stroke() {
        return line_stroke;
    }

    public ClassObj getObj() {
        return obj;
    }

    public void optimizeSize() {
        if (!optimizeRight) return;
        width = optimizeWidth();
        headerHeight = optimizeHeaderHeight();
        attributeHeight = optimizeAttributeHeight();
        methodHeight = optimizeMethodHeight();
        optimizeRight = false;
    }

    private String minimizeString(String S) {
        String P = "";
        for (int i=0; i<S.length()-4; ++i) {
            P = P + S.charAt(i);
            if (g2D.getFontMetrics().stringWidth(P) + iconSize + line_indent + g2D.getFontMetrics().stringWidth("...") + line_indent*2 > this.width) return P+"...";
        }
        return P + S.charAt(S.length()-4) + S.charAt(S.length()-3) + S.charAt(S.length()-2) + S.charAt(S.length()-1);
    }

    public void renderBlock() {
        //System.out.println(this.attributeHeight);
        g2D.setStroke(new BasicStroke(line_stroke,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g2D.setColor(new Color(0xFFF194));
        g2D.fillRect(Xaxis,Yaxis,width,headerHeight+methodHeight+attributeHeight);
        g2D.setColor(Color.BLACK);
        g2D.drawRect(Xaxis,Yaxis,width,headerHeight+methodHeight+attributeHeight);

        g2D.setStroke(new BasicStroke(1,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g2D.drawLine(Xaxis,Yaxis+headerHeight,Xaxis+width,Yaxis+headerHeight);
        g2D.drawLine(Xaxis,Yaxis+headerHeight+attributeHeight,Xaxis+width,Yaxis+headerHeight+attributeHeight);
        g2D.drawLine(Xaxis,Yaxis+headerHeight+attributeHeight+methodHeight,Xaxis+width,Yaxis+headerHeight+attributeHeight+methodHeight);

        g2D.setFont(headerFont);
        BufferedImage iconHeader = null;
        if (obj.getKeyWord().equals("class")) iconHeader = imageClass; else iconHeader = imageInterface;
        g2D.drawImage(iconHeader,Xaxis+(this.width-g2D.getFontMetrics().stringWidth(obj.getName()) - iconSize)/2,
                Yaxis+g2D.getFontMetrics().charWidth('A')+line_spacing-18,null);
        g2D.drawString(obj.getName(),Xaxis+(this.width-g2D.getFontMetrics().stringWidth(obj.getName()) - iconSize)/2+iconSize+3,
                Yaxis+g2D.getFontMetrics().charWidth('A')+line_spacing);

        g2D.setFont(otherFont);
        ArrayList<Element> listOfElement = obj.getListOfElement();
        int addingSpace = headerHeight + line_spacing;

        for (Element element : listOfElement) {

            if (element instanceof Method) continue;

            // Tao dau .... o duoi khi thu nho block
            if (addingSpace + line_spacing*3/2 > this.getHeight()) {
                String dotString = "";
                while (g2D.getFontMetrics().stringWidth(dotString) < this.getWidth() - line_indent*16) dotString += '.';
                g2D.drawString(dotString,Xaxis+line_indent*8,Yaxis+this.getHeight()-5);
                return;
            }

            String printedString = element.getName()+" : "+element.getType();
            printedString = minimizeString(printedString);
            addingSpace += g2D.getFontMetrics().charWidth('A');

            String accessLevel = element.getAccessLevel();
            BufferedImage icon = null;
            switch (accessLevel) {
                case "public":
                    icon = imagePublic;
                    break;
                case "private":
                    icon = imagePrivate;
                    break;
                case "protected":
                    icon = imageProtected;
                    break;
                case "no modifiers":
                    icon = imagePackage;
                    break;
            }
            g2D.drawImage(icon,Xaxis+line_indent,Yaxis+addingSpace-13,null);
            g2D.drawString(printedString,Xaxis+line_indent+iconSize,Yaxis+addingSpace);
            addingSpace += line_spacing;
        }

        addingSpace = headerHeight + attributeHeight + line_spacing;


        for (Element element : listOfElement) {

            if (element instanceof Attribute) continue;

            if (addingSpace + line_spacing*3/2 > this.getHeight()) {
                String dotString = "";
                while (g2D.getFontMetrics().stringWidth(dotString) < this.getWidth() - line_indent*16) dotString += '.';
                g2D.drawString(dotString,Xaxis+line_indent*8,Yaxis+this.getHeight()-5);
                return;
            }
            Method method = (Method) element;

            String printedString = element.getName()+" : "+element.getType();
            if (showParameter) {
                printedString =




                        element.getName()+method.getParameter()+" : "+element.getType();
            }
            printedString = minimizeString(printedString);
            addingSpace += g2D.getFontMetrics().charWidth('A');

            String accessLevel = element.getAccessLevel();
            //System.out.println(accessLevel);
            BufferedImage icon = null;
            switch (accessLevel) {
                case "public":
                    icon = imagePublic;
                    break;
                case "private":
                    icon = imagePrivate;
                    break;
                case "protected":
                    icon = imageProtected;
                    break;
                case "no modifiers":
                    icon = imagePackage;
                    break;
            }
            g2D.drawImage(icon,Xaxis+line_indent,Yaxis+addingSpace-13,null);
            g2D.drawString(printedString,Xaxis+line_indent+iconSize,Yaxis+addingSpace);
            addingSpace += line_spacing;
        }
    }

    private Point getIntersectionLinetoLine(Line2D.Double pLine1, Line2D.Double pLine2)
    {
        Point result = null;

        double s1_x = pLine1.x2 - pLine1.x1,
                s1_y = pLine1.y2 - pLine1.y1,
                s2_x = pLine2.x2 - pLine2.x1,
                s2_y = pLine2.y2 - pLine2.y1,
                s = (-s1_y * (pLine1.x1 - pLine2.x1) + s1_x * (pLine1.y1 - pLine2.y1)) / (-s2_x * s1_y + s1_x * s2_y),
                t = ( s2_x * (pLine1.y1 - pLine2.y1) - s2_y * (pLine1.x1 - pLine2.x1)) / (-s2_x * s1_y + s1_x * s2_y);

        if (s >= 0 && s <= 1 && t >= 0 && t <= 1)
        {
            result = new Point(
                    (int) (pLine1.x1 + (t * s1_x)),
                    (int) (pLine1.y1 + (t * s1_y)));
        }
        return result;
    }

    private Point getIntersectionLinetoRec(Line2D.Double line2D, Rectangle rectangle) {
        double x = rectangle.getX() , y = rectangle.getY() , width = rectangle.getWidth() , height = rectangle.getHeight();
        Line2D.Double recLine1 = new Line2D.Double(x,y,x+width,y);
        Line2D.Double recLine2 = new Line2D.Double(x+width,y,x+width,y+height);
        Line2D.Double recLine3 = new Line2D.Double(x,y,x,y+height);
        Line2D.Double recLine4 = new Line2D.Double(x,y+height,x+width,y+height);

        Point resPoint = null;
        resPoint = getIntersectionLinetoLine(line2D,recLine1);
        if (resPoint!=null) return resPoint;
        resPoint = getIntersectionLinetoLine(line2D,recLine2);
        if (resPoint!=null) return resPoint;
        resPoint = getIntersectionLinetoLine(line2D,recLine3);
        if (resPoint!=null) return resPoint;
        resPoint = getIntersectionLinetoLine(line2D,recLine4);
        return resPoint;
    }

    private void drawArrow(Layer layer1 , Layer layer2, String type) {
        //System.out.println(type+" "+layer1.obj.getName()+" "+layer2.obj.getName());
        Point centralPoint1 = new Point(layer1.getXaxis()+layer1.getWidth()/2,layer1.getYaxis()+layer1.getHeight()/2);
        Point centralPoint2 = new Point(layer2.getXaxis()+layer2.getWidth()/2,layer2.getYaxis()+layer2.getHeight()/2);

        Line2D.Double connectedLine = new Line2D.Double(centralPoint1,centralPoint2);
        Rectangle recLayer1 = new Rectangle(layer1.getXaxis(),layer1.getYaxis(),layer1.getWidth(),layer1.getHeight());
        Rectangle recLayer2 = new Rectangle(layer2.getXaxis(),layer2.getYaxis(),layer2.getWidth(),layer2.getHeight());
        Point startPoint = getIntersectionLinetoRec(connectedLine,recLayer1);
        Point endPoint = getIntersectionLinetoRec(connectedLine,recLayer2);

        if (recLayer1.intersects(recLayer2)) {
            startPoint = centralPoint1;
            endPoint = centralPoint2;
        }
        //System.out.println(recLayer2.getX()+" "+layer2.getYaxis()+" "+layer2.getWidth()+" "+layer2.getHeight());

        double x1 = startPoint.getX() , y1 = startPoint.getY() , x2 = endPoint.getX() , y2 = endPoint.getY();
        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        double arrowTail = 20;
        int len = (int) Math.sqrt(dx*dx + dy*dy);
        AffineTransform oldTransform = g2D.getTransform();
        Stroke oldStroke = g2D.getStroke();

        AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
        at.concatenate(AffineTransform.getRotateInstance(angle));
        g2D.transform(at);
        if (type == "implementation")
            g2D.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{8}, 0));
                else g2D.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));

        g2D.drawLine(0, 0, len, 0);
        g2D.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        if (type == "inner") {
            double xOval = (len-arrowTail/2) - arrowTail/2 , yOval = 0 - arrowTail/2;

            g2D.setColor(new Color(0xE2FFDA));
            g2D.fillOval((int)(xOval),(int)(yOval),(int)(arrowTail),(int)(arrowTail));
            g2D.setColor(Color.BLACK);
            g2D.drawOval((int)(xOval),(int)(yOval),(int)(arrowTail),(int)(arrowTail));
            g2D.drawLine((int)(len-arrowTail/2),(int)(-arrowTail/2),(int)(len-arrowTail/2),(int)(arrowTail/2));
            g2D.drawLine((int)(len-arrowTail),0,len,0);
        }
            else {
            if (type == "inheritance" || type == "implementation") {
                g2D.setColor(new Color(0xE2FFDA));
                g2D.fillPolygon(new int[]{len, (int) (len - arrowTail), (int) (len - arrowTail), len},
                        new int[]{0, (int) (-arrowTail / 2), (int) (arrowTail / 2), 0}, 4);
            }

            g2D.setColor(Color.BLACK);
            g2D.drawLine(len, 0, (int) (len - arrowTail), (int) (-arrowTail / 2));
            g2D.drawLine(len, 0, (int) (len - arrowTail), (int) (arrowTail / 2));

            if (type == "inheritance" || type == "implementation") {
                g2D.drawLine((int) (len - arrowTail), (int) (-arrowTail / 2), (int) (len - arrowTail), (int) (arrowTail / 2));
            }
        }

        g2D.setTransform(oldTransform);
        g2D.setStroke(oldStroke);
    };

    public void renderRelations() {
        ArrayList<ClassRelation> listOfRelation = obj.getListOfRelation();
        for (ClassRelation relation : listOfRelation) {
            Layer targetLayer = relation.getObj().getLayer();
            if (!targetLayer.isVisible()) continue;
            String typeOfRelation = relation.getTypeOfRelation();
            drawArrow(this,targetLayer,typeOfRelation);
        }
    }
}
