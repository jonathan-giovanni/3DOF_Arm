
import arm.Arm;
import com.sun.tools.internal.ws.wsdl.document.Input;
import gui.Window;
import gui.frmArmController;
import kinematics.ForwardK;
import kinematics.InverseK;
import neuralNet.ApplyMLP;
import neuralNet.mlp.MultiLayerPerceptron;
import neuralNet.mlp.transferfunctions.HyperbolicTransfer;
import neuralNet.mlp.transferfunctions.SigmoidalTransfer;
import processing.core.PApplet;
import utils.PRECISION;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import static java.lang.Math.cos;

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



        /*

        neuralNet.Input input = new neuralNet.Input(L, PRECISION.HIGH,false,0,90,-90,90);
        System.out.println("Entradas ");
        //input.getInputs();

        ArrayList<double[][]>[] inputsArray = input.getInputs();

        System.out.println("\nEquacion 1");
        ArrayList<double[][]> eq1_input_normalized =  input.normalizeInputs(inputsArray[0],0,1);

        input.saveInputs(eq1_input_normalized,"eq1.txt");

        */


        ArrayList<double[]> in  = neuralNet.Input.loadFile("input_normalized.txt");

        ArrayList<double[]> out = neuralNet.Input.loadFile("output_normalized.txt");

        int[] layers = new int[]{ 2, 10, 2 };

        MultiLayerPerceptron net = new MultiLayerPerceptron(layers, 0.05, new SigmoidalTransfer());

        double error=1;
        for(int j=0;j<10000;j++){
            for(int i=0;i<in.size();i++){
                error = net.backPropagate(in.get(i),out.get(i));
            }
            System.out.println("Error "+error+" iteracion "+j);
        }


        System.out.println("\nPruebas de efectividad\n");
        int ok=0;
        for(int i=0;i<in.size();i++){
            double test[]     = in.get(i);
            double target[]   = out.get(i);
            double output[]   = net.execute(test);

            double test_error[]  = { Math.abs(target[0]-output[0]) , Math.abs(target[0]-output[0]) };

            double te_normalizated[] = {neuralNet.Input.DeNormalize(test[0],0,110),neuralNet.Input.DeNormalize(test[1],-32,138)} ;


            double ta_normalizated[] = {neuralNet.Input.DeNormalize(target[0],0,1.5708),neuralNet.Input.DeNormalize(target[1],-1.5708,0)} ;

            double ou_normalizated[] = {neuralNet.Input.DeNormalize(output[0],0,1.5708),neuralNet.Input.DeNormalize(output[1],-1.5708,0)} ;



            System.out.println("Test   : "+Arrays.toString(test)  + " Denormalizado : "+Arrays.toString(te_normalizated));
            System.out.println("Target : "+Arrays.toString(target)+ " Denormalizado : "+Arrays.toString(ta_normalizated) +" Degrees [ "+Math.toDegrees(ta_normalizated[0])+" , "+ Math.toDegrees(ta_normalizated[1]) +" ]");
            System.out.println("Output : "+Arrays.toString(output)+" Denormalizado : "+Arrays.toString(ou_normalizated)+" Degrees [ "+Math.toDegrees(ou_normalizated[0])+" , "+ Math.toDegrees(ou_normalizated[1])+" ]");
            System.out.print("Error  : "+Arrays.toString(test_error));
            if(test_error[0]<=0.05 && test_error[1]<=0.05){
                System.out.println(" ------- ok");
                ok++;
            }else{
                System.out.println();
            }
            System.out.println();

        }
        System.out.println("\nPasaron la prueba : "+ok+" de "+in.size()+ " "+ ((100*ok)/in.size()) +"% aprobados");



        //92% aprobados con 100k iteraciones  factor de aprendizaje 0.05
        //86% aprobados con 10k iteraciones factor de aprendizaje 0.05


        /*
        ApplyMLP net = new ApplyMLP(layers,0.2,new SigmoidalTransfer());
        net.train(0,4000,0.6,eq1_input_normalized);
        */


        //System.out.println("\2Equacion 2");
        //ArrayList<double[][]> eq2_input_normalized =  input.normalizeInputs(inputsArray[1],0,1);


        /*

        double x1=-Math.PI;
        double x2=Math.PI;
        double n=200;
        double step = (x2-x1)/(n-1);
        double in[][]  = new double[200][];
        double out[][] = new double[200][];
        int c=0;
        double Xmin=1000,Xmax=-1000;
        for(double i=x1;i<x2;i+=step){
            in[c]  = new double[]{L[2] * cos(i) + L[1] * cos(i)} ;
            out[c] = new double[]{i};

            Xmin = (in[c][0]<Xmin)?in[c][0]:Xmin;
            Xmax = (in[c][0]>Xmax)?in[c][0]:Xmax;

            c++;
        }

        //double in_normalized[] = neuralNet.Input.Normalize(in,Xmin,Xmax,0,1);
        System.out.println("Input         : "+Arrays.toString(in));
        //System.out.println("Normalized in : "+Arrays.toString(in_normalized));
        System.out.println("Output        : "+Arrays.toString(out));



        */





        /*
        double NET_error = 0;
        for(int i = 0; i < 40000; i++)
        {
            int pos = new Random().nextInt(199 + 1);
            double[] inputs = new double[]{pos};
            double[] output = new double[]{L[2] * cos(inputs[0]) + L[1] * cos(inputs[0])};

            //System.out.println("sin("+inputs[0]+") = "+output[0]);

            for(int j=0;j<eq1_input_normalized.size();j++){
                NET_error = net.backPropagate(eq1_input_normalized.get(j)[1], eq1_input_normalized.get(j)[0]);
            }

            System.out.println("Error at step "+i+" is "+NET_error);
        }








        */


        /*
        for(int i=0;i<eq1_input_normalized.size();i++){
            System.out.println(Arrays.toString(eq1_input_normalized.get(i)[0])+" , "+Arrays.toString(eq1_input_normalized.get(i)[1]));
        }

        */


        /*
        input.saveInputs(inputs,"pruebas2018.txt");

        //input.saveInputs(inputs,"pruebas2018.txt");

        System.out.println("Error "+applyMLP.train(4000,0.6,inputs));





        */

        //System.out.println("Angles denormalized "+Arrays.toString(denormalized_out));

        //System.out.println("Angles cord "+Math.toDegrees(denormalized_out[0])+" , "+Math.toDegrees(denormalized_out[1]));


        //10 capa oculta - factor aprendizaje 0.05 40k epocas
        //Coords Normalized [0.5555555555555555, 0.14141414141414116]
        //Angles normalized [0.792430901338932, 4.523411081198438E-5]
        //Angles cord 52.637562241007764 , -89.99185786005384

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
