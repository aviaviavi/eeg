import processing.core.*;
import java.awt.Component;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
 
public class CircleSketch extends PApplet {
 
  public float rad = 200;

  public void setup() {
    size(400, 400);
    background(0);
  }
  public void draw() {
    background(0);
    fill(200);
    ellipseMode(CENTER);
    if (rad >= 20) {
      rad = rad / 1.05f;
    }
    ellipse(mouseX,mouseY,rad,rad);
  }
  public void increase() {
    rad = rad * 1.2f;
  }
  public void decrease() {
    if (rad >= 20) {
     rad = rad / 1.2f;
    }
  }
}
