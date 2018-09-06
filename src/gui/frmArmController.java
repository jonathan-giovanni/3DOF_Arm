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
    private JTabbedPane tabbedPane1;
    private JButton btnMinusX;
    private JTextField txtQ1;
    private JTextField txtQ2;
    private JTextField txtQ3;
    private JButton btnPlusX;
    private JTextField txtX;
    private JTextField textField2;
    private JComboBox comboBox1;
    private JButton btnMinusY;
    private JButton btnMinusZ;
    private JButton btnPlusY;
    private JButton btnPlusZ;
    private JTextField txtY;
    private JTextField txtZ;
    private JLabel lblAngles;
    private JButton entrenarButton;
    private JProgressBar progressBar1;
    private JButton xButton;
    private JTextField textField1;
    private JButton xButton1;
    private JButton yButton;
    private JTextField textField3;
    private JButton yButton1;
    private JButton zButton;
    private JTextField textField4;
    private JButton zButton1;

    public static double angles[] = new double[]{0,0,0};

    public double step = 0.5;


    double init_coords[] = {0,110,28};

    public frmArmController(){




        coords = new double[]{0,110,28};


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


        // -- X
        btnPlusX.addActionListener(a-> {
            double v =  decimals( Double.parseDouble(txtX.getText()) + step,1);
            applyIK(0,v);
            txtX.setText( v+"" );
        });
        btnMinusX.addActionListener(a-> {
            double v =  decimals( Double.parseDouble(txtX.getText()) - step,1);
            applyIK(0,v);
            txtX.setText( v+"" );
        });

        txtX.addActionListener(a->{
            double v =  decimals( Double.parseDouble(txtX.getText()),1);
            applyIK(0,v);
        });

        // -- Y
        btnPlusY.addActionListener(a-> {
            double v =  decimals( Double.parseDouble(txtY.getText()) + step,1);
            applyIK(1,v);
            txtY.setText( v+"" );
        });
        btnMinusY.addActionListener(a-> {
            double v =  decimals( Double.parseDouble(txtY.getText()) - step,1);
            applyIK(1,v);
            txtY.setText( v+"" );
        });
        txtY.addActionListener(a->{
            double v =  decimals( Double.parseDouble(txtY.getText()),1);
            applyIK(1,v);
        });
        // -- Z
        btnPlusZ.addActionListener(a-> {
            double v =  decimals( Double.parseDouble(txtZ.getText()) + step,1);
            applyIK(2,v);
            txtZ.setText( v+"" );
        });
        btnMinusZ.addActionListener(a-> {
            double v =  decimals( Double.parseDouble(txtZ.getText()) - step,1);
            applyIK(2,v);
            txtZ.setText( v+"" );
        });
        txtZ.addActionListener(a->{
            double v =  decimals( Double.parseDouble(txtZ.getText()),1);
            applyIK(2,v);
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

        coords = init_coords;
    }

    @Override
    public void draw() {
        super.draw();





    }

    public void applyFK(int angleChanged,int value){
        angles[angleChanged] = Math.toRadians(value);
        System.out.println("angulos en degrees "+ Arrays.toString(angles));
        arm.setAngles(angles,true);
        if(fk!=null) {
            coords =  fk.getCartesian(angles, true);
            double c[] = decimals(coords,4);
            lblCoord.setText(Arrays.toString(c));
        }
    }




    public void applyIK(int coordChanged,double value){
        coords[coordChanged] = value;
        System.out.println("coordenadas "+ Arrays.toString(coords));
        if(ik!=null) {

            double anglesTemp[] =  ik.getAngles(coords);
            //double a[] = decimals(angles,4);
            String contenido = Arrays.toString( rad2degrees( anglesTemp) );

            lblAngles.setText(contenido);
            System.out.println(Arrays.toString(anglesTemp));

            if( !Double.isNaN( anglesTemp[0]) &&  !Double.isNaN( anglesTemp[1] ) && !Double.isNaN(anglesTemp[2]) ){
                angles = anglesTemp;
                arm.setAngles(angles,true);
            }else{
                contenido+= " Error se sobre pasa el espacio de trabajo";
                lblAngles.setText(contenido);
            }
        }
    }


    private double[] decimals(double inputs[],int limit){
        double result[] = new double[inputs.length];
        for (int i=0;i<inputs.length;i++)
            result[i] = decimals(inputs[i],limit);

        return result;
    }

    private double decimals(double input,int limit){
        return (double)Math.round(input * Math.pow(10,limit)) / Math.pow(10,limit);
    }

    private double[] rad2degrees(double inputs[]){
        double degree[] = new double[3];
        for(int i=0;i<inputs.length;i++){
            degree[i] = inputs[i]*180/Math.PI;
        }
        return degree;
    }
}
