package gui;

import kinematics.ForwardK;

import javax.swing.*;
import java.util.Arrays;

public class frmArmController extends Window{
    private JSlider slider1;
    private JSlider slider2;
    private JSlider slider3;
    private JLabel lblQ1;
    private JLabel lblQ2;
    private JLabel lblQ3;
    private JLabel lblCoord;
    private JPanel panel1;

    public static double angles[] = new double[]{0,0,0};

    public frmArmController(){


        coords = new double[]{0,0,0};


        slider1.addChangeListener(a ->{
            lblQ1.setText(slider1.getValue()+"");
            applyFK(0,slider1.getValue());
        });

        slider2.addChangeListener(a ->{
            lblQ2.setText(slider2.getValue()+"");
            applyFK(1,slider2.getValue());
        });

        slider3.addChangeListener(a ->{
            lblQ3.setText(slider3.getValue()+"");
            applyFK(2,slider3.getValue());
        });

    }

    @Override
    public void settings() {
        super.settings();
        //ik2 = new InverseK(arm.getL());
        JFrame frame = new JFrame("frmArmController");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(new frmArmController().panel1);
        frame.pack();
        frame.setVisible(true);

    }

    @Override
    public void draw() {
        super.draw();





    }

    public void applyFK(int coordChanged,int value){
        angles[coordChanged] = Math.toRadians(value);
        System.out.println("angulos en degrees "+ Arrays.toString(angles));
        arm.setAngles(angles,true);
        if(fk!=null) {
            coords =  fk.getCartesian(angles, true);
            lblCoord.setText(Arrays.toString(coords));
        }
    }
}
