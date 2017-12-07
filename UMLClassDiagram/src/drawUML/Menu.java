package drawUML;

import analyzingProject.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by VAIO on 08-Nov-17.
 */

public class Menu extends JPanel {

    private static ClassObjManager manager;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        JFrame frame=new JFrame("UML Class Diagram");

        JTextField linkField=new JTextField("Enter your project ...");
        Font font_linkField = new Font("Courier",Font.BOLD,14);
        linkField.setBounds(30,150, 450,30);
        linkField.setFont(font_linkField);
        linkField.setEditable(false);
        linkField.setBackground(Color.WHITE);
        linkField.setForeground(Color.GRAY);

        JLabel headerLabel = new JLabel(new ImageIcon("src\\analyzingProject\\Header.png"));
        headerLabel.setLocation(30,30);
        headerLabel.setBounds(5,-5,480,160);
        /*JPanel headerPanel = new JPanel();
        headerPanel.add(headerLabel);
        headerPanel.setPreferredSize(new Dimension(240,80));
        headerPanel.setLocation(30,30);*/

        JLabel successfulLabel=new JLabel("Compile Successfully");
        Font font_commentLabel = new Font("Courier",Font.BOLD,18);
        successfulLabel.setBounds(150,185, 200,40);
        successfulLabel.setFont(font_commentLabel);
        Color successfulColor = new Color(102,204,0);
        successfulLabel.setForeground(successfulColor);
        successfulLabel.setBorder(null);
        successfulLabel.setVisible(false);

        JLabel errorLabel=new JLabel("Compile Error");
        errorLabel.setBounds(180,185, 200,40);
        errorLabel.setFont(font_commentLabel);
        Color falseColor = new Color(255,0,0);
        errorLabel.setForeground(falseColor);
        errorLabel.setBorder(null);
        errorLabel.setVisible(false);

        JButton button_1 = new JButton("Choose Path");
        button_1.setBounds(30,230,120,30);
        JButton button_2 = new JButton("Compile");
        button_2.setBounds(180,230,120,30);
        button_2.setEnabled(false);
        JButton button_3 = new JButton("Draw");
        button_3.setBounds(330,230,120,30);
        button_3.setEnabled(false);

        button_1.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){

                JFileChooser fc = new JFileChooser("E:\\Informatics\\Java\\Code\\NormalCode\\code");
                fc.setFileSelectionMode(JFileChooser.CUSTOM_DIALOG);

                int i = fc.showOpenDialog(frame);
                if (i == JFileChooser.APPROVE_OPTION) {
                    File file=fc.getSelectedFile();
                    String filepath=file.getPath();
                    linkField.setText(filepath);
                    button_3.setEnabled(true);
                }
            }
        });

        button_2.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                File folder = new File(linkField.getText());
                String commandClear = "cmd.exe /c DEL /f /s /q E:\\bin";
                String command = "javac -d E:\\bin @srcfiles.txt";
                String command2 = "cmd.exe /c dir /s /b " +"\"" +linkField.getText() +"\"" + "\\*.java > srcfiles.txt";

                try {
                    //System.out.println(command2);
                    //Process pro = Runtime.getRuntime().exec("mkdir E:\\bin");

                    //runProcess(command);
                    //Process pro = Runtime.getRuntime().exec("dir /s /b "+linkField.getText() + " \\*.java > srcfiles.java");
                    Process proClear = Runtime.getRuntime().exec(commandClear);
                    Process pro2 = Runtime.getRuntime().exec(command2);
                    Process pro = Runtime.getRuntime().exec(command);
                    pro.waitFor();
                    pro2.waitFor();
                    proClear.waitFor();
                    if (pro.exitValue() == 0) {
                        successfulLabel.setVisible(true);
                        errorLabel.setVisible(false);
                        button_3.setEnabled(true);
                    } else {
                        successfulLabel.setVisible(false);
                        errorLabel.setVisible(true);
                    }
                    //runProcess("java E:\\Informatics\\Java\\Code\\NormalCode\\code\\Bai1\\src\\Cau1");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        button_3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //screen2();
                manager = new ClassObjManager(linkField.getText());
                new DrawShape(manager,linkField.getText());
                //System.out.println("haha");
                /*ArrayList<ClassObj> listOfClassObj = manager.getClassObjList();
                for (ClassObj obj : listOfClassObj) {

                    System.out.println(obj.getName());
                    //System.out.println(obj.getPath());

                    ArrayList<Element> listEle = obj.getListOfElement();
                    for (Element ele : listEle) {
                        if (ele instanceof Method) {
                            System.out.println("Method " + ele.getAccessLevel() + " " + ele.getType() + " " + ele.getName() + " " + ((Method) ele).getParameter());
                        } else {
                            System.out.println("Attribute " + ele.getAccessLevel() + " " + ele.getType() + " " + ele.getName());
                        }
                        System.out.println();
                    }
                    //frame.setVisible(false);
                    //frame1.setVisible(true);
                //new DrawShape();

                }*/
            }
        });
        frame.add(button_1);frame.add(linkField);
        frame.add(button_2);frame.add(successfulLabel);frame.add(errorLabel);
        frame.add(button_3);
        frame.add(headerLabel);
        frame.setSize(530,350);
        frame.setLayout(null);
        frame.setVisible(true);
        //frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}