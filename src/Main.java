
import gui.Window;
import gui.frmArmController;
import kinematics.ForwardK;
import kinematics.InverseK;
import neuralNet.ApplyMLP;
import neuralNet.Input;
import neuralNet.mlp.TransferFunction;
import neuralNet.mlp.transferfunctions.HyperbolicTransfer;
import neuralNet.mlp.transferfunctions.SigmoidalTransfer;
import processing.core.PApplet;
import util.Numerics;
import utils.PRECISION;

import java.util.*;


public class Main {

    public static void main(String[] args) {
        System.out.println("\nPruebas de cinematica\n\n");

        Main m = new Main();

        Window pWindow = new frmArmController();
        PApplet.main(pWindow.getClass());

        /** Pruebas

        double L[] = {28,50,60};

        double Q1min= 0;
        double Q1max= 90;

        double Q2min= -90;
        double Q2max= 0;

        double n=30; // para 30 son 900 datos , para 50 son 2500 datos y para 90 datos son 8,100
        double step1 = (Q1max-Q1min)/(n-1);
        double step2 = (Q2max-Q2min)/(n-1);

        ForwardK fk = new ForwardK(L);
        InverseK ik = new InverseK(L);

        ArrayList<double[]> input  = new ArrayList<>();
        ArrayList<double[]> output = new ArrayList<>();

        double Ymin= 0 ,Ymax= 110,Zmin= -32,Zmax= 138;

        int decimals = 4;


        Input generateInput = new Input(L, PRECISION.MEDIUM, Q1min,Q1max,Q2min,Q2max);

        ArrayList< double[] >[] IO_data  =  generateInput.getInputs();

        ApplyMLP applyMLP = new ApplyMLP( new int[]{2,10,2} ,0.02, new SigmoidalTransfer() ,IO_data[0],IO_data[1] );

        double trainError = applyMLP.train( 12000);


        System.out.println("Layers " +Numerics.LAYER );
        System.out.println("Epocas "+Numerics.EPOCH);
        System.out.println("Precision "+Numerics.PRECISION);


        System.out.println("\nPruebas de efectividad \n");


        double errorPromQ1=0,errorPromQ2=0,aprobados=0;

        for(int i=0;i<IO_data[0].size();i++){

            double in_denor[]  = { 0, Input.DeNormalize( IO_data[0].get(i)[0], Ymin,Ymax)  , Input.DeNormalize(IO_data[0].get(i)[1],Zmin,Zmax) };
            double out_denor[] = {   Input.DeNormalize( IO_data[1].get(i)[0] , Q1min,Q1max ) , Input.DeNormalize( IO_data[1].get(i)[1] , Q2min,Q2max )   };


            double result[] = applyMLP.execute(in_denor);

            if(result==null){
                result= new double[]{0,0,0};
            }

            double error[] = {Math.abs( out_denor[0] - result[1]  ) , Math.abs( out_denor[1] - result[2]  )};

            errorPromQ1 += error[0];
            errorPromQ2 += error[1];

            System.out.println("Iteracion "+(i+1));
            System.out.println("in  denormalized "+Arrays.toString(in_denor));
            System.out.println("out denormalized "+Arrays.toString(out_denor));
            System.out.println("test result      "+Arrays.toString(result));
            System.out.println("result error     "+Arrays.toString(error));
            if(error[0]<=1.5 && error[1]<=1.0){
                System.out.println("Aprobado");
                aprobados++;
            }



            System.out.println();

        }


        System.out.println("\nError promedio Q1 "+( errorPromQ1/IO_data[0].size() ));
        System.out.println("Error promedio Q2 "+( errorPromQ2/IO_data[0].size() ));
        System.out.println("Aprobados "+aprobados +" de "+IO_data[0].size()+" es el "+( (aprobados*100)/IO_data[0].size())+"%");
        System.out.println("Error de entrenamiento "+trainError);

        /**Fin pruebas**/

    }

}
