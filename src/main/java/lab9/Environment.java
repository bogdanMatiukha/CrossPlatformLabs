package lab9;

import java.util.ArrayList;
import java.util.List;

public class Environment {
    private final List<Observer> organisms = new ArrayList<>();

    public void addOrganism(Observer organism) {
        organisms.add(organism);
    }

    public void changeCondition(String condition) {
        System.out.println("\n=== Зміна середовища: "
                + condition + " ===");

        for (Observer organism : organisms) {
            organism.update(condition);
        }
    }
}
