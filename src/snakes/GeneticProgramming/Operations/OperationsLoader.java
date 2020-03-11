package snakes.GeneticProgramming.Operations;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class OperationsLoader {
    /**
     * Fetches the class given the name of the class and the package,
     * the class would be taken from the classpath and could be dynamically
     * added after the game is compiled.
     *
     * @return ArrayList of instances of the Subfunction class
     */
    public ArrayList<Operation> getAllOperations() {
        try {
            ArrayList<Operation> result = new ArrayList<>();

            File dir = new File("./src/snakes/GeneticProgramming/Operations");
            File[] filesList = dir.listFiles();
            for (File file : filesList) {
                if (file.isFile()) {
                    String subfunctionName = file.getName().substring(0, file.getName().lastIndexOf('.'));
                    if (subfunctionName.equals("Operation") || subfunctionName.equals("OperationsLoader"))
                        continue;

                    // Create a new JavaClassLoader
                    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

                    // Load the target class using its binary name
                    Class<?> loadedMyClass = classLoader.loadClass("snakes.GeneticProgramming.Operations." + subfunctionName);
                    System.out.println("Loaded class name: " + loadedMyClass.getName());

                    Class<? extends Operation> operationClass = loadedMyClass.asSubclass(Operation.class);
                    if (Modifier.isAbstract(operationClass.getModifiers())) { // if class is abstract - do not load it
                        continue;
                    }
                    Constructor<? extends Operation> operationClassCtor = operationClass.getConstructor();
                    result.add(operationClassCtor.newInstance());
                }
            }
            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
