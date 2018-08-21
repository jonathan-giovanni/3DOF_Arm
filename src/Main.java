
import arm.Arm;
import com.sun.tools.internal.ws.wsdl.document.Input;
import gui.Window;
import gui.frmArmController;
import kinematics.ForwardK;
import kinematics.InverseK;
import neuralNet.ApplyMLP;
import neuralNet.mlp.transferfunctions.SigmoidalTransfer;
import processing.core.PApplet;
import utils.PRECISION;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        System.out.println("\nPruebas de cinematica\n\n");

        Main m = new Main();

        //Window pWindow = new frmArmController();
        //PApplet.main(pWindow.getClass());


        double L[] = new double[]{28,50,60};
        double coords[] = {110,0,28};
        double angles[] = {0,0,0};

        ForwardK fk = new ForwardK(L);
        InverseK ik = new InverseK(L);

        System.out.println("Longitudes de piezas    : "+Arrays.toString(L));
        System.out.println("Pruebas de cinematica directa");
        System.out.println("Angulos de entrada      : "+Arrays.toString(angles));
        System.out.println("Coordenadas de salida   : "+Arrays.toString(fk.getCartesian(angles,false)));
        System.out.println("Pruebas de cinematica inversa");
        System.out.println("Coordenadas de entrada  : "+Arrays.toString(coords));
        System.out.println("Angulos de salida       : "+Arrays.toString(ik.getAngles(coords)));




        //neuralNet.Input input = new neuralNet.Input(L, PRECISION.LOW,false,0,90,-90,90);
        //input.getInputs();
        //input.saveInputs(input.getInputs(),"pruebas2018.txt");
        //input

        //ArrayList<double[][]> inputs = input.normalizeInputs(input.getInputs());
        //input.saveInputs(inputs,"pruebas2018.txt");
        //ApplyMLP applyMLP = new ApplyMLP(new int[]{2,10,2},0.6,new SigmoidalTransfer());

        //applyMLP.train(8000,0.6,inputs);

        //1622699


        /*
        ForwardK fk = new ForwardK(L);
        int cont=0;
        for(double i=0;i<=90;i+=0.1){
            for(double j=-90;j<=90;j+=0.1){
                double a[] = new double[]{i,j};
                double c[] = fk.getCartesian(a,false);
                //hacer fk para dos datos y para tres
                //hacer clases utils donde existe limite de decimales

                System.out.println((cont++) + " Q1 " +i+", Q2 "+j+" "+ Arrays.toString(c));
            }
        }
        */


    }

    private double[] decimals(double inputs[],int limit){
        double result[] = new double[]{0,0,0};
        for (int i=0;i<inputs.length;i++)
            result[i] = decimals(inputs[i],limit);

        return result;
    }

    private double decimals(double input,int limit){
        return (double)Math.round(input * Math.pow(10,limit)) / Math.pow(10,limit);
    }
}
