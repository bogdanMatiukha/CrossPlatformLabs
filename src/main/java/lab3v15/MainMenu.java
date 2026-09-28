package lab3v15;

import lab3v15.tasks.Array1DTask;
import lab3v15.tasks.Array2DTask;
import lab3v15.tasks.JaggedArrayTask;
import lab3v15.tasks.ArrayObjectTask;

public class MainMenu {

    public static void main(String[] args) {
        System.out.println("LAB 3 (Arrays) | Variant 15");

        Array1DTask.run();
        Array2DTask.run();
        JaggedArrayTask.run();
        ArrayObjectTask.run();

        System.out.println("\nDone.");
    }
}