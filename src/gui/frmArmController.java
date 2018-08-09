package gui;

import kinematics.ForwardK;

import javax.swing.*;
import java.util.Arrays;

public class frmArmController extends Window{
    private JSlider slider1;
    private JSlider slider2;
    private JSlider slider3;
    private JLabel lblCoord;
    private JPanel panel1;
    private JTextField txtQ1;
    private JTextField txtQ2;
    private JTextField txtQ3;
    private JButton xButton1;
    private JTextField textField1;
    private JButton xButton;
    private JButton yButton;
    private JButton yButton1;
    private JTextField textField2;
    private JButton zButton;
    private JTextField textField3;
    private JButton zButton1;

    public static double angles[] = new double[]{0,0,0};

    public frmArmController(){


        coords = new double[]{0,0,0};


        slider1.addChangeListener(a ->{
            txtQ1.setText(slider1.getValue()+"");
            applyFK(0,slider1.getValue());
        });

        slider2.addChangeListener(a ->{
            txtQ2.setText(slider2.getValue()+"");
            applyFK(1,slider2.getValue());
        });

        slider3.addChangeListener(a ->{
            txtQ3.setText(slider3.getValue()+"");
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
            double c1 = (double)Math.round(coords[0] * 10000d) / 10000d;
            double c2 = (double)Math.round(coords[1] * 10000d) / 10000d;
            double c3 = (double)Math.round(coords[2] * 10000d) / 10000d;

            lblCoord.setText("[ "+c1+" , "+c2 + " , "+c3+" ]");
        }
    }
}
