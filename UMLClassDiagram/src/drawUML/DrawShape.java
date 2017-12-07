package drawUML;

import analyzingProject.ClassObj;
import analyzingProject.ClassObjManager;


import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.sound.sampled.Line;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;

public class DrawShape {

    DrawShape(ClassObjManager manager,String path) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                /*try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();*/

                //Tao 1 ScrollPane
                Diagram dS = new Diagram(manager);
                JFrame frame = new JFrame();
                JScrollPane scrollPane = new JScrollPane(dS);

                FolderTree jt=new FolderTree(path);
                SwingUtilities.invokeLater(jt);
                jt.setListOfLayer(dS.getListOfLayer());
                jt.setdS(dS);

                JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,jt,scrollPane);
                splitPane.setResizeWeight(0.25);
                splitPane.setDividerLocation(200);


                JToolBar toolBar = new JToolBar();
                toolBar.setRollover(true);
                ImportButton importButton = new ImportButton(frame,dS);
                toolBar.add(importButton);

                frame.setName("Diagram");
                frame.getContentPane().add(toolBar,BorderLayout.NORTH);
                frame.getContentPane().add(splitPane);
                //frame.getContentPane().add(dS);
                //frame.add(scrollPane);
                frame.pack();
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setVisible(true);
                //Tao 1 tree
            }
        });
    }
}

class ImportButton extends JButton {
    JFrame frame;
    Diagram dS;
    ImportButton(JFrame frame,Diagram dS) {
        this.frame = frame;
        this.dS = dS;
        this.setIcon(new ImageIcon("src\\drawUML\\Icon\\ImportIcon.png"));
        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser("E:");
                fc.setFileSelectionMode(JFileChooser.CUSTOM_DIALOG);

                int i = fc.showSaveDialog(frame);
                if (i == JFileChooser.APPROVE_OPTION) {
                    File file=fc.getSelectedFile();
                    String filepath=file.getPath();

                    Dimension size = dS.getSize();
                    BufferedImage image = new BufferedImage(
                            size.width, size.height
                            , BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2 = image.createGraphics();
                    dS.paint(g2);
                    try
                    {
                        ImageIO.write(image, "png", new File(filepath));
                    }
                    catch(Exception ee)
                    {
                        ee.printStackTrace();
                    }
                }
            }
        });
    }
}