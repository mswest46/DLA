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
  private List<Particle> particleList;
  private Particle diffuser;
  private Particle nearestNode;
  private AnimationType animationType;

  public MyPanel (AnimationType type) {
    super();
    this.animationType = type;
  }

  public String toString() { 
    return "my panel";
  }

  public void setDiffuser(Particle diffuser) {
    this.diffuser = diffuser;
  }

  public void setNearestNode(Particle nearestNode) {
    this.nearestNode = nearestNode;
  }

  public void updateParticleList(List<Particle > particleList) {
    this.particleList = particleList;
  }
  public Dimension getPreferredSize() {
    return new Dimension(PANEL_WIDTH,PANEL_HEIGHT);
  } 
  public void moveParticle(double oldX, double oldY, double newX, double newY, double radius){
    repaint();
    // paintwhereParticleUsedToBe();
    // paintwhereParticleIsNow();
    // not working
    //repaint((int)(oldX - 4 * radius + PANEL_WIDTH/2), 
    //    (int) (oldY + 4 * radius + PANEL_HEIGHT/2),
    //    (int) (8 * radius), 
    //    (int) (8 * radius));
    //repaint((int)(newX - 4 * radius + PANEL_WIDTH/2), 
    //    (int) (newY + 4 * radius + PANEL_HEIGHT/2), 
    //    (int) (8 * radius), 
    //    (int) (8 * radius));
  }

  public void finalPaint(){
    repaint();
  }

  /*
   * @override
   */
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Color color = Color.BLACK;
     for (Particle node: particleList) { 
       node.paintNode(g, color, PANEL_WIDTH/2, PANEL_HEIGHT/2); }

    boolean animateDiffuser = (animationType == AnimationType.DIFFUSE_SLOW 
        || animationType == AnimationType.DIFFUSE_FAST);

    if (animateDiffuser) {
      diffuser.paintNode(g,Color.RED, PANEL_WIDTH/2, PANEL_HEIGHT/2);
      if (!(nearestNode==null)) {
        nearestNode.paintNode(g,Color.BLUE, PANEL_WIDTH/2, PANEL_WIDTH/2);
      }
    }
  }
      
}
