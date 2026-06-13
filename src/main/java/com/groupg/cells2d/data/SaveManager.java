package com.groupg.cells2d.data;

import com.groupg.cells2d.engine.SimulationEngine;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Used to save and load a simulation.
 * The simulation state is stored in a binary file using java Serialisation.
 *
 * Also exposes an auto-save path in the user's home directory
 * (~/.cells2d/doctor_autosave.dat) for the doctor's persistent simulation.
 */
public class SaveManager {

    /** Directory for user data: ~/.cells2d/ */
    private static final String USER_DATA_DIR =
        System.getProperty("user.home") + File.separator + ".cells2d";

    /** Auto-save file used by the doctor view. */
    public static final String AUTO_SAVE_PATH =
        USER_DATA_DIR + File.separator + "doctor_autosave.dat";

    /**
     * Save the current simulation state into a binary file.
     *
     * @param engine   simulation to save
     * @param filePath destination file path
     * @throws IOException if an error occurs during writing
     */
    public static void save(SimulationEngine engine, String filePath) throws IOException {
        // Ensure parent directories exist
        Path parent = Paths.get(filePath).getParent();
        if (parent != null) Files.createDirectories(parent);

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(engine);
        }
    }

    /**
     * Loads a previously saved simulation from a binary file.
     *
     * @param filePath source file path
     * @return restored simulation
     * @throws IOException            if the file cannot be read
     * @throws ClassNotFoundException if a serialised class cannot be found
     */
    public static SimulationEngine load(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            SimulationEngine engine = (SimulationEngine) in.readObject();
            engine.rebuildNeighborhood();
            return engine;
        }
    }

    /**
     * Saves the engine to the auto-save path.
     * Silently ignores errors (best-effort).
     */
    public static void autoSave(SimulationEngine engine) {
        try {
            save(engine, AUTO_SAVE_PATH);
        } catch (IOException e) {
            System.err.println("[SaveManager] autoSave failed: " + e.getMessage());
        }
    }

    /**
     * Loads the engine from the auto-save path.
     *
     * @return the saved engine, or null if no save file exists or loading fails
     */
    public static SimulationEngine autoLoad() {
        File file = new File(AUTO_SAVE_PATH);
        if (!file.exists()) return null;
        try {
            return load(AUTO_SAVE_PATH);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[SaveManager] autoLoad failed: " + e.getMessage());
            return null;
        }
    }
}
