import processing.core.*;

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
import javax.swing.JPanel;
import javax.swing.JOptionPane;

public class DisplayFrame extends javax.swing.JFrame {

    private CircleSketch sketch;

    public DisplayFrame(){
        this.setSize(600, 600); //The window Dimensions
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setBounds(20, 20, 600, 600);
        this.sketch = new CircleSketch();
        panel.add(sketch);
        this.add(panel);
        sketch.init(); //this is the function used to start the execution of the sketch
        this.setVisible(true);
    }

    public void increase() {
        this.sketch.increase();
    }

    public void decrease() {
        this.sketch.decrease();
    }
}
