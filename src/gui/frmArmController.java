package gui;

import neuralNet.ApplyMLP;
import neuralNet.Input;
import neuralNet.mlp.transferfunctions.SigmoidalTransfer;
import utils.Numerics;
import utils.PRECISION;

import javax.swing.*;
import java.util.ArrayList;
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
    private JTextField txtHidenLayers;
    private JButton btnMinusY;
    private JButton btnMinusZ;
    private JButton btnPlusY;
    private JButton btnPlusZ;
    private JTextField txtY;
    private JTextField txtZ;
    private JLabel lblAngles;
    private JProgressBar progressBarTraining;
    private JButton btnXMinusNet;
    private JTextField txtXNet;
    private JButton btnXPlusNet;
    private JButton btnYMinusNet;
    private JTextField txtYNet;
    private JButton btnYPlusNet;
    private JButton btnZMinusNet;
    private JTextField txtZNet;
    private JButton btnZPlusNet;
    private JButton btnSaveNet;
    private JButton btnTrainNet;
    private JComboBox cmbTrainOptions;
    private JTextField txtEpoch;
    private JLabel lblTrainError;
    private JTextField txtLearningRate;
    private JButton btnLoadNet;
    private JLabel lblStateNet;
    private JLabel lblAnglesNet;
    private JLabel lblAnglesIkNet;
    private JLabel lblErrorAnglesNet;

    private static boolean isTrained=false;

    public static double angles[] = new double[]{0,0,0};

    public double step = 0.5;
    double init_coords[] = {0,110,28};
    ApplyMLP applyMLP;

    /**
     * Constructor de los controles en Swing
     * Inicializa los eventos y las variables usadas en la GUI
     */
    public frmArmController(){
        coords = new double[]{0,110,28};

        /**tabForwardKinematics*/
        tabForwardKinematics();
        /**tabInverseKinematics*/
        tabInverseKinematics();
        /** configure ANN*/
        tabConfigureANN();
        /** execute ANN*/
        tabExecuteANN();

        tabbedPane1.addChangeListener(v-> { checkANNState(); });

    }

    /**
     * Controles para manipular la cinematica directa
     */
    private void tabForwardKinematics(){
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

    /**
     * Controles para manipular la cinematica inversa
     */
    private void tabInverseKinematics(){

        // -- X
        btnPlusX.addActionListener(a-> {
            double v =  Numerics.justNdecimals( Double.parseDouble(txtX.getText()) + step,1);
            applyIK(0,v);
            txtX.setText( v+"" );
        });
        btnMinusX.addActionListener(a-> {
            double v =  Numerics.justNdecimals( Double.parseDouble(txtX.getText()) - step,1);
            applyIK(0,v);
            txtX.setText( v+"" );
        });

        txtX.addActionListener(a->{
            double v =  Numerics.justNdecimals( Double.parseDouble(txtX.getText()),1);
            applyIK(0,v);
        });

        // -- Y
        btnPlusY.addActionListener(a-> {
            double v =  Numerics.justNdecimals( Double.parseDouble(txtY.getText()) + step,1);
            applyIK(1,v);
            txtY.setText( v+"" );
        });
        btnMinusY.addActionListener(a-> {
            double v =  Numerics.justNdecimals( Double.parseDouble(txtY.getText()) - step,1);
            applyIK(1,v);
            txtY.setText( v+"" );
        });
        txtY.addActionListener(a->{
            double v =  Numerics.justNdecimals( Double.parseDouble(txtY.getText()),1);
            applyIK(1,v);
        });
        // -- Z
        btnPlusZ.addActionListener(a-> {
            double v =  Numerics.justNdecimals( Double.parseDouble(txtZ.getText()) + step,1);
            applyIK(2,v);
            txtZ.setText( v+"" );
        });
        btnMinusZ.addActionListener(a-> {
            double v =  Numerics.justNdecimals( Double.parseDouble(txtZ.getText()) - step,1);
            applyIK(2,v);
            txtZ.setText( v+"" );
        });
        txtZ.addActionListener(a->{
            double v =  Numerics.justNdecimals( Double.parseDouble(txtZ.getText()),1);
            applyIK(2,v);
        });
    }

    /**
     * Controles para configurar la cinematica directa
     */
    private void tabConfigureANN(){

        cmbTrainOptions.addItem("400   - Bajo");
        cmbTrainOptions.addItem("900   - Medio");
        cmbTrainOptions.addItem("3600  - Alto");
        cmbTrainOptions.addItem("8100  - Muy alto");


        cmbTrainOptions.setSelectedIndex(1);

        btnSaveNet.setEnabled(isTrained);
        btnTrainNet.addActionListener(a-> trainNet());

        btnSaveNet.addActionListener(a->{
            if(isTrained){
                applyMLP.saveMLP("ANN.dat");
            }
        });
        btnLoadNet.addActionListener(a->{
            loadANN();
        });

    }

    private void loadANN(){
        ApplyMLP loadedMLP = ApplyMLP.load("ANN.dat");
        if(loadedMLP!=null){
            applyMLP = loadedMLP;
            txtHidenLayers.setText(  applyMLP.getLayers()[1]+"" );
            txtLearningRate.setText( applyMLP.getLearningRate()+"");
            txtEpoch.setText( applyMLP.getEpochs()+"");
            lblTrainError.setText( applyMLP.getErrorTrain()+"");

            PRECISION p = applyMLP.getPrecision();

            if(p == PRECISION.LOW){
                cmbTrainOptions.setSelectedIndex(0);
            }
            if(p == PRECISION.MEDIUM){
                cmbTrainOptions.setSelectedIndex(1);
            }
            if(p == PRECISION.HIGH){
                cmbTrainOptions.setSelectedIndex(2);
            }
            if(p == PRECISION.VERY_HIGH){
                cmbTrainOptions.setSelectedIndex(3);
            }

            isTrained = true;
            JOptionPane.showMessageDialog(frame,
                    "Red neuronal cargada correctamente",
                    "Red cargada correctamente",
                    JOptionPane.INFORMATION_MESSAGE);

        }else{
            JOptionPane.showMessageDialog(frame,
                    "No se ha encontrado red neuronal\nGuarde primero la red para su posterior uso",
                    "Error al cargar red",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Controles para ejecutar la red neuronal artificial
     */
    private void tabExecuteANN(){
        // X
        btnXPlusNet.addActionListener(n->{
            double v =  Numerics.justNdecimals( Double.parseDouble(txtXNet.getText()) + step,1);
            applyANN(0,v);
            txtXNet.setText(v+"");
        });
        btnXMinusNet.addActionListener(n->{
            double v =  Numerics.justNdecimals( Double.parseDouble(txtXNet.getText()) - step,1);
            applyANN(0,v);
            txtXNet.setText(v+"");
        });
        txtXNet.addActionListener(n->{
            double v =  Numerics.justNdecimals( Double.parseDouble(txtY.getText()),1);
            applyANN(0,v);
        });

        //Y
        btnYPlusNet.addActionListener(n->{
            double v =  Numerics.justNdecimals( Double.parseDouble(txtYNet.getText()) + step,1);
            applyANN(1,v);
            txtYNet.setText(v+"");
        });
        btnYMinusNet.addActionListener(n->{
            double v =  Numerics.justNdecimals( Double.parseDouble(txtYNet.getText()) - step,1);
            applyANN(1,v);
            txtYNet.setText(v+"");
        });
        txtXNet.addActionListener(n->{
            double v =  Numerics.justNdecimals( Double.parseDouble(txtY.getText()),1);
            applyANN(1,v);
        });


        //Z
        btnZPlusNet.addActionListener(n->{
            double v =  Numerics.justNdecimals( Double.parseDouble(txtZNet.getText()) + step,1);
            applyANN(2,v);
            txtZNet.setText(v+"");
        });
        btnZMinusNet.addActionListener(n->{
            double v =  Numerics.justNdecimals( Double.parseDouble(txtZNet.getText()) - step,1);
            applyANN(2,v);
            txtZNet.setText(v+"");
        });
        txtZNet.addActionListener(n->{
            double v =  Numerics.justNdecimals( Double.parseDouble(txtZ.getText()),1);
            applyANN(2,v);
        });


    }

    /**
     * Revisa el estado de la red neuronal
     * En caso de que este entrenda habilita los controles
     */
    private void checkANNState(){
        setEnableANNControls(isTrained);
        if(isTrained){
            lblStateNet.setText("Red cargada y entrenada");
        }else{
            lblStateNet.setText("Red no ha sido entrenada o cargada");
        }
    }

    private void setEnableANNControls(boolean isEnable){
        txtXNet.setEnabled(isEnable);
        txtYNet.setEnabled(isEnable);
        txtZNet.setEnabled(isEnable);

        btnXMinusNet.setEnabled(isEnable);
        btnXPlusNet.setEnabled(isEnable);

        btnYMinusNet.setEnabled(isEnable);
        btnYPlusNet.setEnabled(isEnable);

        btnZMinusNet.setEnabled(isEnable);
        btnZPlusNet.setEnabled(isEnable);
    }

    /**
     * Funcion para entrenar la red neuronal
     */
    private void trainNet(){

        boolean error=false;
        int layers  = 0;
        int epoch = 0;
        double learningRate=0;

        /**se evaluan los distintos escenarios donde se introduzca una variable no valida**/
        try{
            layers = Integer.parseInt( txtHidenLayers.getText());
            if(layers<=0) throw new Exception("Dato invalido");
        }catch (Exception ex){
            error=true;
            JOptionPane.showMessageDialog(frame,
                    "Error de conversión en campo: Capas ocultas\nIngrese unicamente números positivos",
                    "Error en conversion",
                    JOptionPane.ERROR_MESSAGE);
        }
        try{
            epoch = Integer.parseInt( txtEpoch.getText());
            if(epoch<=0) throw new Exception("Dato invalido");
        }catch (Exception ex){
            error=true;
            JOptionPane.showMessageDialog(frame,
                    "Error de conversión en campo: Epocas\nIngrese unicamente números positivos",
                    "Error en conversion",
                    JOptionPane.ERROR_MESSAGE);
        }

        try{
            learningRate = Double.parseDouble( txtLearningRate.getText());
            if(learningRate<=0) throw new Exception("Dato invalido");
        }catch (Exception ex){
            error=true;
            JOptionPane.showMessageDialog(frame,
                    "Error de conversión en campo: Factor de aprendizaje\nIngrese unicamente números positivos",
                    "Error en conversion",
                    JOptionPane.ERROR_MESSAGE);
        }

        //si no hay errores
        if(!error){

            PRECISION precision = PRECISION.MEDIUM;
            switch (cmbTrainOptions.getSelectedIndex()){
                case 0:
                    precision = PRECISION.LOW;
                    break;
                case 1:
                    precision = PRECISION.MEDIUM;
                    break;
                case 2:
                    precision = PRECISION.HIGH;
                    break;
                case 3:
                    precision = PRECISION.VERY_HIGH;
                    break;
            }
            //generar las entradas
            Input generateInput = new Input(arm.getL(), precision, 0,90,-90,0);
            //Guardar las entradas en un arreglo de listas
            ArrayList< double[] >[] IO_data  =  generateInput.getInputs();
            //crear la red neuronal
            applyMLP = new ApplyMLP(new int[]{2,layers,2},learningRate,new SigmoidalTransfer(),IO_data[0],IO_data[1]);
            applyMLP.setPrecision(precision);

            int finalEpoch = epoch;
            class TrainProcess extends SwingWorker<String, String> {
                double error_generado=0;
                protected String doInBackground() {
                    lblTrainError.setText("Entrenando...");
                    progressBarTraining.setIndeterminate(true);
                    error_generado = applyMLP.train(finalEpoch);
                    return "Done.";
                }

                protected void done() {
                    progressBarTraining.setIndeterminate(false);
                    lblTrainError.setText(error_generado+"");
                    btnSaveNet.setEnabled(true);
                    isTrained = true;
                }
            }
            //ejecutar el hilo
            new TrainProcess().execute();

        }

    }


    /**
     * Inicializa el formulario donde se cargaran los controles
     */
    @Override
    public void settings() {
        super.settings();
        JFrame frame = new JFrame("frmArmController");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(new frmArmController().panel1);
        frame.pack();
        frame.setVisible(true);

        coords = init_coords;
    }

    /**
     * Aplica la cinematica directa dado los angulos cambiados
     * @param angleChanged
     * @param value
     */
    private void applyFK(int angleChanged,int value){
        angles[angleChanged] = Math.toRadians(value);
        System.out.println("angulos en degrees "+ Arrays.toString(angles));
        arm.setAngles(angles,true);
        if(fk!=null) {
            coords =  fk.getCartesian(angles, true);
            double c[] = Numerics.justNdecimals(coords,4);
            lblCoord.setText(Arrays.toString(c));
        }
    }

    /**
     * Aplica la cinematica inversa dada la coordenada establecida
     * Evalua si el brazo sobrepasa el espacio de trabajo y muestra los angulos en degrees
     * @param coordChanged
     * @param value
     */
    private void applyIK(int coordChanged,double value){
        coords[coordChanged] = value;
        if(ik!=null) {
            double anglesTemp[] =  ik.getAngles(coords);

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

    /**
     * Ejecuta la red neuronal dado un vector de coordenadas XYZ
     * Devuelve los angulos y se establecen en las articulaciones del brazo
     * Se evalua el error de los angulos obtenidos por la red neuronal
     * @param coordChanged
     * @param value
     */
    public void applyANN(int coordChanged,double value){
        coords[coordChanged] = value;
        if(applyMLP!=null && ik!=null){

            double error[] = new double[3];

            String contenido = "[ 0.0, 0.0, 0.0 ]";

            double anglesANN[] = new double[3];
            double anglesIK[]  = new double[3];
            try{
                anglesANN =  Numerics.justNdecimals( applyMLP.execute(coords),4);
                anglesIK  =  Numerics.justNdecimals(rad2degrees(ik.getAngles(coords)),4);
                contenido = Arrays.toString(anglesIK);
            }catch (Exception ex){
                anglesIK[0] = Double.NaN;
            }

            if( !Double.isNaN( anglesIK[0]) &&  !Double.isNaN( anglesIK[1] ) && !Double.isNaN(anglesIK[2]) ){
                lblAnglesNet.setText(Arrays.toString(anglesANN));
                lblAnglesIkNet.setText(contenido);
                error[0] = (anglesIK[0] - anglesANN[0]);
                error[1] = (anglesIK[1] - anglesANN[1]);
                error[2] = (anglesIK[2] - anglesANN[2]);

                error = Numerics.justNdecimals(error,4);
                lblErrorAnglesNet.setText(Arrays.toString(error));
                angles = anglesANN;
                arm.setAngles(angles,false);
            }else{
                contenido = " Error se sobre pasa el espacio de trabajo";
                lblAnglesIkNet.setText(contenido);
                lblAnglesNet.setText(contenido);
            }

        }
    }

    /**
     * Convierte un vector de entradas en radianes a degrees
     * @param inputs
     * @return
     */
    public double[] rad2degrees(double inputs[]){
        double degree[] = new double[3];
        for(int i=0;i<inputs.length;i++){
            degree[i] = inputs[i]*180/Math.PI;
        }
        return degree;
    }
}
