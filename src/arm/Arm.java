package arm;

import processing.core.PApplet;
import processing.core.PShape;

/**
 *
 * * Sistema de coordenadas en libreria
 *
 *            +Z  -X
 *             |  /
 *             | /
 *             |/
 *    -Y ------/------- +Y
 *            /|
 *           / |
 *          /  |
 *        +X  -Z
 *
 */

public class Arm {

    PApplet context;
    PShape base, shoulder, upArm, loArm, end;
    double L[];// longitud del brazo
    double Q[];// angulos en radianes del brazo

    /**
     * Constructor de Arm se cargan los .obj
     * y se establece la longitud del brazo
     * @param pApplet
     */
    public Arm(PApplet pApplet){
        context     = pApplet;

        L = new double[]{28,50,60};//longitud del brazo
        Q = new double[]{0,0,0};//angulos

        base        = context.loadShape("r5.obj");
        shoulder    = context.loadShape("r1.obj");
        upArm       = context.loadShape("r2.obj");
        loArm       = context.loadShape("r3.obj");
        end         = context.loadShape("r4.obj");

        base.disableStyle();
        shoulder.disableStyle();
        upArm.disableStyle();
        loArm.disableStyle();
        end.disableStyle();

    }

    /**
     * Establece los angulos en radianes al brazo
     * @param q
     */
    public void setAngles(double q[],boolean isRadian){
        Q = q;
        if(!isRadian){
            Q[0] = Math.toRadians(q[0]);
            Q[1] = Math.toRadians(q[1]);
            Q[2] = Math.toRadians(q[2]);
        }
    }


    /**
     * Dibuja el brazo con cada uno de sus elementos
     */
    public void drawArm(){
        context.pushMatrix();
        /**     base no rotatoria   **/
        context.fill(120, 120, 120,100);
        context.translate(0,0,0);
        context.shape(base);

        /**     base rotatoria      **/
        context.fill(255, 200, 75,100);
        context.translate(0, 0, 4);//para que se posicione arriba
        float anguloBase = (float) (-Q[0]  + Math.PI/2);
        context.rotateY(anguloBase);//gamma
        context.shape(shoulder);
        /**     antebrazo           **/
        context.fill(60, 200, 130,100);
        context.translate(0, 0, 25);
        context.rotateY(context.PI);
        float anguloAntebrazo = (float) (-Q[1]);
        context.rotateX(anguloAntebrazo);//alpha
        context.shape(upArm);
        /**      brazo               **/
        context.fill(60, 130, 200,100);
        context.translate(0, -50, 0);
        context.rotateY(context.PI);
        float anguloBrazo= (float) (Q[2]  );
        context.rotateX(anguloBrazo);//beta
        context.shape(loArm);
        /**     orientacion         **/
        context.fill(250, 100, 100,100);
        context.translate(0, 50, 0);
        context.rotateY(context.PI);
        context.shape(end);
        context.popMatrix();
    }

    /**
     * Retorna la longitud del brazo
     * @return L
     */
    public double[] getL(){return L;}
}
