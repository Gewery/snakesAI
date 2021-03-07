package snakes.GeneticProgramming.Subfunctions;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class SubfunctionsLoader {
    /**
     * Fetches the class given the name of the class and the package,
     * the class would be taken from the classpath and could be dynamically
     * added after the game is compiled.
     *
     * @return ArrayList of instances of the Subfunction class
     */
    public ArrayList<Subfunction> getAllSubfunctions() {
        try {

            ArrayList<Subfunction> result = new ArrayList<>();

            File dir = new File("./src/snakes/GeneticProgramming/Subfunctions");
            File[] filesList = dir.listFiles();
            for (File file : filesList) {
                if (file.isFile()) {
                    String subfunctionName = file.getName().substring(0, file.getName().lastIndexOf('.'));
                    if (subfunctionName.equals("Subfunction") || subfunctionName.equals("SubfunctionsLoader"))
                        continue;

                    // Create a new JavaClassLoader
                    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

                    // Load the target class using its binary name
                    Class<?> loadedMyClass = classLoader.loadClass("snakes.GeneticProgramming.Subfunctions." + subfunctionName);
                    if (!Subfunction.class.isAssignableFrom(loadedMyClass)) {
                        //System.out.println("Class " + loadedMyClass.getName() + " doesn't implement " + Subfunction.class.getName() + " interface. Class skipped");
                        continue;
                    }

                    //System.out.println("Loaded class name: " + loadedMyClass.getName());

                    Class<? extends Subfunction> subfunctionClass = loadedMyClass.asSubclass(Subfunction.class);
                    if (Modifier.isAbstract(subfunctionClass.getModifiers())) { // if class is abstract - do not load it
                        continue;
                    }
                    Constructor<? extends Subfunction> subfunctionClassCtor = subfunctionClass.getConstructor();
                    result.add(subfunctionClassCtor.newInstance());
                }
            }
            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
