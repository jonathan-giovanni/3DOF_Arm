package gui;

import arm.Arm;
import kinematics.ForwardK;
import kinematics.InverseK;
import processing.core.PApplet;
import processing.core.PVector;





/**
 *
 * Sistemas de coordenadas en Processing
 *
 *            -Y  -Z
 *             |  /
 *             | /
 *             |/
 *    -X ------/------- +X
 *            /|
 *           / |
 *          /  |
 *        +Z  +Y
 *
 * coordenadas con escala negativa
 *
 *            +Y  -Z
 *             |  /
 *             | /
 *             |/
 *    +X ------/------- -X
 *            /|
 *           / |
 *          /  |
 *        +Z  -Y
 *
 * Sistema de coordenadas en libreria
 *
 *            +Z  -Y
 *             |  /
 *             | /
 *             |/
 *    -X ------/------- +X
 *            /|
 *           / |
 *          /  |
 *        +Y  -Z
 *
 * cambios Lib a Pro : +X a -X
 *                     +Y a +Z
 *                     +Z a -Y
 *
 */


public class Window extends PApplet {

    PVector origin;
    static Arm arm;
    float rX,rY,zoom,size;
    protected static InverseK ik;
    protected static ForwardK fk;
    public static double coords[] = {110,0,28};

    @Override
    public void settings() {
        size(800, 800, P3D);
    }

    @Override
    public void setup() {
        smooth();
        textMode(SHAPE);
        origin  = new PVector(width / 2, height / 2,0);
        zoom    = 1.5f;
        rX      = -0.51f;
        rY      = -0.65f;
        size    = 2000;
        arm     = new Arm(this);
        ik      = new InverseK(arm.getL());
        fk      = new ForwardK(arm.getL());
        coords  = new double[]{0,0,0};
        //arm.setAngles(angles,false);

    }

    @Override
    public void draw() {

        background(255);
        lights();
        directionalLight(40, 90, 100, 1, 40, 40);
        translate(origin.x,origin.y);
        scale(zoom);

        userInput();
        drawAxes();

        scale(-1);// invierte

        arm.drawArm();
        //arm.setAngles(angles,false);
        //arm.setAngles(new double[]{90,0,0},false);

        pushMatrix();
        noStroke();
        fill(250,50,50);
        translate((float)coords[0],(float)coords[1],(float)coords[2]);
        sphere(10);
        popMatrix();


        pushMatrix();
        noStroke();
        fill(50,50,250);
        translate(40,40,40);
        sphere(10);
        popMatrix();


    }

    @Override
    public void translate(float x, float y, float z) {
        super.translate(-x, z, -y); // cambios en ejes -x , z,-y
    }

    private void userInput(){
        if(mousePressed){
            rX   -= (mouseY - pmouseY) * 0.002f;//map(mouseY,0,height,-PI,PI);
            rY   -= (mouseX - pmouseX) * 0.002f;// map(mouseX,0,width,PI,-PI);
        }
        rotateX(rX);
        rotateY(rY);

        if(keyPressed){
            if(keyCode == UP){
                zoom += 0.01f;
            }
            if(keyCode == DOWN){
                zoom -= 0.01f;
            }
        }
    }


    private void drawAxes() {
        float margin = 90;
        //X rojo
        text("+X",margin,-2,0);
        text("-X",-margin,-2,0);
        stroke(210, 0, 0);
        line(-size,0,0,size,0,0);

        //Y verde
        text("-Z",2,margin,0);
        text("+Z",2,-margin,0);
        stroke(0, 210, 0);
        line(0,-size,0,0,size,0);

        //Z azul
        text("+Y",5,0,margin);
        text("-Y",5,0,-margin);
        stroke(0, 0, 210);
        line(0, 0, -size,0,0, size);
    }


}
