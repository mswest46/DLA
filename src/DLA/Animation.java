package DLA;

import acm.util.*;
import java.util.*;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics; 

class Animation extends JPanel { 
  private static final int PANEL_WIDTH = 1000;
  private static final int PANEL_HEIGHT= 1000;
  private List<Particle> particleList;
  private Particle diffuser;
  private Particle nearestParticle;
  private AnimationType type;
  private boolean animateDiffuser;
  private boolean animateAttacher;
  private boolean animateFinal;
  private int pause;

  public Animation (AnimationOptions animationOptions) {
    super();
    this.type = animationOptions.type;
    this.animateDiffuser = (type == AnimationType.DIFFUSE_FAST 
        || type == AnimationType.DIFFUSE_SLOW);
    this.animateAttacher = (type == AnimationType.DIFFUSE_FAST 
        || type == AnimationType.DIFFUSE_SLOW 
        || type == AnimationType.ATTACH);
    this.animateFinal = (type == AnimationType.DIFFUSE_FAST
        || type == AnimationType.DIFFUSE_SLOW
        || type == AnimationType.ATTACH 
        || type == AnimationType.FINAL);
    this.pause = animationOptions.pause;
  }

  public void updateDiffuser(Particle diffuser) {
    if (animateDiffuser) { 
      this.diffuser = diffuser;
    }
  }

  public void updateNearestParticle(Particle nearestParticle) {
    if (animateDiffuser) {
      this.nearestParticle = nearestParticle;
    }
  }

  public void updateParticleList(List<Particle> particleList) {
    this.particleList = particleList;
  }

  public Dimension getPreferredSize() {
    return new Dimension(PANEL_WIDTH,PANEL_HEIGHT);
  } 

  public void finalPaint(){
    repaint();
  }

  public void paintAggregate() { 
    repaint();
    pause(10);
  }

  public void paintDiffusion() { 
    if (animateDiffuser) { 
      repaint();
      pause(10);
    }
  }

  public void pause(int pauseTime) { 
    try { 
      Thread.sleep(pauseTime);
    } catch (Exception e) {
      throw new RuntimeException();
    }
  }

  /*
   * @ override
   */
  public void paintComponent(Graphics g) {
    try { 
      super.paintComponent(g);
      Color color = Color.BLACK;
       for (Particle particle: particleList) { 
         particle.paint(g, color, PANEL_WIDTH/2, PANEL_HEIGHT/2); }

      if (animateDiffuser) {
        diffuser.paint(g,Color.RED, PANEL_WIDTH/2, PANEL_HEIGHT/2);
        if (!(nearestParticle==null)) {
          nearestParticle.paint(g,Color.BLUE, PANEL_WIDTH/2, PANEL_WIDTH/2);
        }
      }
    } catch (ConcurrentModificationException e) { 
    }

  }
      
}
