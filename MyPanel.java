package DLA;

import acm.util.*;
import java.util.*;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics; 
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseMotionAdapter;

class MyPanel extends JPanel { 

  private static final int PANEL_WIDTH = 1000;
  private static final int PANEL_HEIGHT= 1000;
  private List<Node> nodeList;
  private Node diffuser;
  public void setDiffuser(Node diffuser) {
    this.diffuser = diffuser;
  }
  public void updateNodeList(List<Node> nodeList) {
    this.nodeList = nodeList;
  }
  public Dimension getPreferredSize() {
    return new Dimension(PANEL_WIDTH,PANEL_HEIGHT);
  } 
  public void moveNode(double oldX, double oldY, double newX, double newY){
    // clipping todo.
    repaint();
  }
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Color color = Color.BLACK;
    for (Node node: nodeList) { 
      node.paintNode(g, color, PANEL_WIDTH/2, PANEL_WIDTH/2);
    }
    color = Color.RED;
    diffuser.paintNode(g,color, PANEL_WIDTH/2, PANEL_WIDTH/2);
  }
      
}
