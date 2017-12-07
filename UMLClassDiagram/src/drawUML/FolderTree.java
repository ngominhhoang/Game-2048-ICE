package drawUML;

/**
 * Created by VAIO on 05-Dec-17.
 */
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.*;

public class FolderTree extends JScrollPane implements Runnable {

    private DefaultMutableTreeNode root;
    private DefaultTreeModel treeModel;

    private String projectPath ;
    private File folder ;
    private int fontSize = 17;
    private JTree tree;
    private Diagram dS;
    ArrayList<Layer> listOfLayer = new ArrayList<Layer>();

    ImageIcon javaIcon = new ImageIcon(new ImageIcon(FolderTree.class.getResource("TreeIcon\\java.png"))
            .getImage().getScaledInstance(fontSize, fontSize, Image.SCALE_DEFAULT));
    ImageIcon picIcon = new ImageIcon(new ImageIcon(FolderTree.class.getResource("TreeIcon\\pictures.png"))
            .getImage().getScaledInstance(fontSize, fontSize, Image.SCALE_DEFAULT));
    ImageIcon documentIcon = new ImageIcon(new ImageIcon(FolderTree.class.getResource("TreeIcon\\general_docs.png"))
            .getImage().getScaledInstance(fontSize, fontSize, Image.SCALE_DEFAULT));
    ImageIcon xmlIcon = new ImageIcon(new ImageIcon(FolderTree.class.getResource("TreeIcon\\xml.png"))
            .getImage().getScaledInstance(fontSize, fontSize, Image.SCALE_DEFAULT));
    ImageIcon imlIcon = new ImageIcon(new ImageIcon(FolderTree.class.getResource("TreeIcon\\iml.png"))
            .getImage().getScaledInstance(fontSize, fontSize, Image.SCALE_DEFAULT));
    ImageIcon zipIcon = new ImageIcon(new ImageIcon(FolderTree.class.getResource("TreeIcon\\zip.png"))
            .getImage().getScaledInstance(fontSize, fontSize, Image.SCALE_DEFAULT));
    ImageIcon htmlIcon = new ImageIcon(new ImageIcon(FolderTree.class.getResource("TreeIcon\\html.png"))
            .getImage().getScaledInstance(fontSize, fontSize, Image.SCALE_DEFAULT));
    ImageIcon otherIcon = new ImageIcon(new ImageIcon(FolderTree.class.getResource("TreeIcon\\other.png"))
            .getImage().getScaledInstance(fontSize, fontSize, Image.SCALE_DEFAULT));

    // folders
    ImageIcon folderIcon = new ImageIcon(new ImageIcon(FolderTree.class.getResource("TreeIcon\\directory.png"))
            .getImage().getScaledInstance(fontSize, fontSize, Image.SCALE_DEFAULT));

    JMenuItem showItem = new JMenuItem("Add");
    JPopupMenu popupMenu = new JPopupMenu();

    FolderTree(String projectPath) {
        this.projectPath = projectPath;
        folder = new File(projectPath);
    }

    public void setListOfLayer(ArrayList<Layer> listOfLayer) {
        this.listOfLayer = listOfLayer;
    }

    public void setdS(Diagram dS) {
        this.dS = dS;
    }

    public void run() {
        popupMenu.add(showItem);
        showItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] paths = tree.getSelectionPath().getPath();
                String nodePath = "";
                for (int i=0; i<paths.length; i++) {
                    nodePath += paths[i].toString();
                    if (i!=paths.length-1) nodePath+="\\";
                }

                if (!nodePath.endsWith(".java")) return;

                for (Layer layer : listOfLayer)
                    layer.setLine_stroke(1);
                for (Layer layer : listOfLayer) {
                    if (layer.getObj().getPath().contains(nodePath)) {
                        layer.setVisible(true);
                    }
                }
                dS.repaint();
            }
        });

        File fileRoot = new File(projectPath);
        root = new DefaultMutableTreeNode(new FileNode(fileRoot));
        treeModel = new DefaultTreeModel(root);

        tree = new JTree(treeModel);
        tree.setShowsRootHandles(true);
        tree.setRootVisible(true);
        tree.setFont(new Font("Arial", Font.PLAIN, fontSize));
        Border border = BorderFactory.createEmptyBorder ( 20, 0, 20, 0 );

        tree.setCellRenderer(new DefaultTreeCellRenderer()
        {
            // file extensions
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
                                                          boolean expanded, boolean isLeaf, int row,
                                                          boolean focused)
            {
                JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, isLeaf, row, focused);
                label.setBorder(border);

                Object nodeObj = ((DefaultMutableTreeNode) value).getUserObject();
                String str = nodeObj.toString();

                // check file extensions
                boolean isJava = str.endsWith(".java") || str.endsWith(".class") || str.endsWith(".jar")
                        || str.endsWith(".jad") || str.endsWith(".jsp");
                boolean isPicture = str.endsWith(".png") || str.endsWith(".jpg") || str.endsWith(".gif")
                        || str.endsWith(".tif") || str.endsWith(".bmp");
                boolean isDocument = str.endsWith(".txt") || str.endsWith(".doc") || str.endsWith(".docx")
                        || str.endsWith(".odt") || str.endsWith(".tex") || str.endsWith(".rtf")
                        || str.endsWith(".log");
                boolean isXML = str.endsWith(".xml"), isIML = str.endsWith(".iml"),
                        isZIP = str.endsWith(".zip"), isHTML = str.endsWith(".html");
                boolean isFolder = !str.contains(".");

                if (isFolder) setIcon(folderIcon);
                if (isJava) setIcon(javaIcon);
                if (isIML) setIcon(imlIcon);
                if (isPicture) setIcon(picIcon);
                if (isDocument) setIcon(documentIcon);
                if (isXML) setIcon(xmlIcon);
                if (isZIP) setIcon(zipIcon);
                if (isLeaf && !isJava && !isIML && !isPicture && !isDocument && !isXML
                        && !isZIP)
                {
                    setIcon(otherIcon);
                }

                return this;
            }
        });

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                if (tree.getSelectionPath() == null) return;
                Object[] paths = tree.getSelectionPath().getPath();
                if (paths == null) return;
                String nodePath = "";
                for (int i=0; i<paths.length; i++) {
                    nodePath += paths[i].toString();
                    if (i!=paths.length-1) nodePath+="\\";
                }

                if (!nodePath.endsWith(".java")) return;

                for (Layer layer : listOfLayer)
                    layer.setLine_stroke(1);
                for (Layer layer : listOfLayer) {
                    if (layer.isVisible()&&layer.getObj().getPath().contains(nodePath)) {
                        layer.setLine_stroke(3);
                    }
                }
                dS.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

                if (tree.getSelectionPath() == null) return;
                Object[] paths = tree.getSelectionPath().getPath();
                if (paths == null) return;
                if (!e.isPopupTrigger()) return;
                String nodePath = "";
                for (int i=0; i<paths.length; i++) {
                    nodePath += paths[i].toString();
                    if (i!=paths.length-1) nodePath+="\\";
                }

                if (!nodePath.endsWith(".java")) return;
                popupMenu.show(e.getComponent(),e.getX(),e.getY());
            }

        });

        //this.add(tree);
        this.setViewportView(tree);
        CreateChildNodes ccn = new CreateChildNodes(fileRoot, root);
        new Thread(ccn).start();
    }

    class CreateChildNodes implements Runnable {

        private DefaultMutableTreeNode root;

        private File fileRoot;

        public CreateChildNodes(File fileRoot, DefaultMutableTreeNode root) {
            this.fileRoot = fileRoot;
            this.root = root;
        }

        @Override
        public void run() {
            createChildren(fileRoot, root);
        }

        private void createChildren(File fileRoot, DefaultMutableTreeNode node) {
            File[] files = fileRoot.listFiles();
            if (files == null) return;

            for (File file : files) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new FileNode(file));
                node.add(childNode);
                if (file.isDirectory()) {
                    createChildren(file, childNode);
                }
            }
        }
    }

    public class FileNode {
        private File file;

        public FileNode(File file) {
            this.file = file;
        }

        @Override
        public String toString() {
            String name = file.getName();
            if (name.equals("")) {
                return file.getAbsolutePath();
            } else {
                return name;
            }
        }
    }
}
