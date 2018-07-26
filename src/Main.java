import gui.Window;
import gui.frmArmController;
import processing.core.PApplet;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");


        Window pWindow = new frmArmController();
        PApplet.main(pWindow.getClass());

    }
}
