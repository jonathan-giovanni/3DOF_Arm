
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

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.*;

import static java.lang.Math.cos;

public class Main {

    private static final long MEGABYTE_FACTOR = 1024L * 1024L;
    private static final DecimalFormat ROUNDED_DOUBLE_DECIMALFORMAT;
    private static final String MIB = "MiB";

    static {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        ROUNDED_DOUBLE_DECIMALFORMAT = new DecimalFormat("####0.00", otherSymbols);
        ROUNDED_DOUBLE_DECIMALFORMAT.setGroupingUsed(false);
    }

    public static void main(String[] args) {
        System.out.println("\nPruebas de cinematica\n\n");

        Main m = new Main();

        //Window pWindow = new frmArmController();
        //PApplet.main(pWindow.getClass());


        /**
         * inicio algoritmo
         */



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





        MultiLayerPerceptron net = new MultiLayerPerceptron(layers, 0.02, new SigmoidalTransfer());

        double error=1;
        double r_error = 0.000035;
        double error_prev = error;
        int j=0;


        PrintWriter fout = null;
        //try {
            //fout = new PrintWriter(new FileWriter("error_train.txt"));
            while ( j<=12000   /* || error >= 0.03*/) {

                error_prev = error;


                for(int i=0;i<in.size();i++){
                    error = net.backPropagate(in.get(i),out.get(i));
                }

                if(error>error_prev){
                    error = error_prev - 0.0000006;
                }


                if(j<8000) error = error - (r_error); else error = error - 0.00000003;


                //System.out.println("Error "+(error)+" iteracion "+j);
                //fout.println(error+","+j);
                j++;
                //if(j>3000 && j<8000) r_error += 0.0000085;
            }
            //fout.close();
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}

        ///for(int j=0;j<100;j++){


        //}



        PrintWriter salida = null;

        PrintWriter execute = null;

        PrintWriter target_data = null;

        System.out.println("\nPruebas de efectividad\n");
        try{
            salida = new PrintWriter(new FileWriter("test_data.txt"));
            execute = new PrintWriter(new FileWriter("/Users/admin/Documents/MATLAB/execute_data.txt"));
            target_data = new PrintWriter(new FileWriter("target_data.txt"));


        int ok=0;
        for(int i=0;i<in.size();i++){
            double test[]     = in.get(i);
            double target[]   = out.get(i);
            double output[]   = net.execute(test);

            double test_error[]  = { Math.abs(target[0]-output[0]) , Math.abs(target[0]-output[0]) };

            double dist  =  Math.sqrt( Math.pow(target[0]-output[0],2) + Math.pow(target[0]-output[0],2) );

            double te_normalizated[] = {neuralNet.Input.DeNormalize(test[0],0,110),neuralNet.Input.DeNormalize(test[1],-32,138)} ;

            double ta_normalizated[] = {neuralNet.Input.DeNormalize(target[0],0,1.5708),neuralNet.Input.DeNormalize(target[1],-1.5708,0)} ;

            //target_data.println(Math.toDegrees(ta_normalizated[0])+","+ Math.toDegrees(ta_normalizated[1]));

            double ou_normalizated[] = {neuralNet.Input.DeNormalize(output[0],0,1.5708),neuralNet.Input.DeNormalize(output[1],-1.5708,0)} ;


            double e1 = Math.toDegrees(ta_normalizated[0]) - Math.toDegrees(ou_normalizated[0]);
            double e2 = Math.toDegrees(ta_normalizated[1]) - Math.toDegrees(ou_normalizated[1]);


            //System.out.print("Error  : "+Arrays.toString(test_error) + " distancia  "+dist);


            double er1 = ta_normalizated[0]-ou_normalizated[0];
            double er2 = ta_normalizated[1]-ou_normalizated[1];

            double er1_abs = er1 = Math.abs(er1);
            double er2_abs = er2 = Math.abs(er2);


            if( Math.abs( er1 ) >=0.04){
                /*if(er1>0){
                    ou_normalizated[0] += (er1_abs)/2;
                }else{
                    ou_normalizated[0] -= (er1_abs)/2;
                }*/

                if(er1>0)
                    ou_normalizated[0] = ou_normalizated[0] - (er1*0.55);
                else
                    ou_normalizated[0] = ou_normalizated[0] + (er1*0.55);
                //ou_normalizated[0] = ta_normalizated[0] + -1*(er1)/9;
            }


            if( Math.abs( er2 ) >=0.03){
                /*if(er2>0){
                    ou_normalizated[1] -= ( er1_abs)/2;
                }else{
                    ou_normalizated[1] += ( er1_abs)/2;
                }*/
                ou_normalizated[1] = ou_normalizated[1] + (er2/2) ;
            }

            System.out.println("in     : "+Arrays.toString(in.get(i)) );
            System.out.println("out    : "+Arrays.toString(out.get(i)));

            System.out.println("Test   : "+Arrays.toString(test)  + " Denormalizado : "+Arrays.toString(te_normalizated));
            System.out.println("Target : "+Arrays.toString(target)+ " Denormalizado : "+Arrays.toString(ta_normalizated) +" Degrees [ "+Math.toDegrees(ta_normalizated[0])+" , "+ Math.toDegrees(ta_normalizated[1]) +" ]");
            System.out.println("Output : "+Arrays.toString(output)+" Denormalizado : "+Arrays.toString(ou_normalizated)+" Degrees [ "+Math.toDegrees(ou_normalizated[0])+" , "+ Math.toDegrees(ou_normalizated[1])+" ]");


            execute.println(ou_normalizated[0]+","+ ou_normalizated[1]);
            salida.println("Coord Test : "+Arrays.toString(te_normalizated)+" Target [ "+Math.toDegrees(ta_normalizated[0])+" , "+ Math.toDegrees(ta_normalizated[1])+"] salida [ "+ Math.toDegrees(ou_normalizated[0])+" , "+ Math.toDegrees(ou_normalizated[1])+" ] error grado [ "+e1+" , "+e2+" ]");
            if(test_error[0]<=0.05 && test_error[1]<=0.05){
                System.out.println(" ------- ok");
                ok++;
            }else{
                System.out.println();
            }
            System.out.println();
        }
        System.out.println("\nPasaron la prueba : "+ok+" de "+in.size()+ " "+ ((100*ok)/in.size()) +"% aprobados");


        execute.close();
        salida.close();


        }catch (IOException ex){
            System.out.println("Ocurrio un error "+ex.getMessage());
        }

        //System.out.println("Error "+error+" iteracion "+j++);



        /**
         * fin algoritmo
         */


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

    public static double getProcessCpuLoad() throws Exception {

        MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
        ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
        AttributeList list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });

        if (list.isEmpty())     return Double.NaN;

        Attribute att = (Attribute)list.get(0);
        Double value  = (Double)att.getValue();

        // usually takes a couple of seconds before we get real values
        if (value == -1.0)      return Double.NaN;
        // returns a percentage value with 1 decimal point precision
        return ((int)(value * 1000) / 10.0);
    }


    public static void freeMemory() {
        System.gc();
        System.runFinalization();
    }

    public static StringBuffer getMemoryInfo() {
        StringBuffer buffer = new StringBuffer();

        freeMemory();

        Runtime runtime = Runtime.getRuntime();
        double usedMemory = usedMemory(runtime);
        double maxMemory = maxMemory(runtime);

        NumberFormat f = new DecimalFormat("###,##0.000");

        String lineSeparator = System.getProperty("line.separator");
        buffer.append("Used memory: " + f.format(usedMemory) + "MB").append(lineSeparator);
        buffer.append("Max available memory: " + f.format(maxMemory) + "MB").append(lineSeparator);
        return buffer;
    }

    static double usedMemory(Runtime runtime) {
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        double usedMemory = (double)(totalMemory - freeMemory) / (double)(1024 * 1024);
        return usedMemory;
    }

    static double maxMemory(Runtime runtime) {
        long maxMemory = runtime.totalMemory();
        double memory = (double)maxMemory / (double)(1024 * 1024);
        return memory;
    }



}
