package com.groupg.cells2d.data;

import com.groupg.cells2d.engine.SimulationEngine;

import java.io.*;

/**
 * Used to save and load a simulation.
 * The simulation state is stored in a binary file using
 * java Serialisation
 */
public class SaveManager {

    /**
     * Save the current simulation state into a binary file.
     * 
     * @param engine simulation to save
     * @param filePath destination file path
     * @throws IOException if an error occurs during writing
     */
    public static void save(SimulationEngine engine, String filePath) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(engine);
        }
    }

    /**
     * Loads a previuosly saved simulation from a binary file.
     * 
     * @param filePath source file path
     * @return restored simulation 
     * @throws IOException if the file cannot be read
     * @throws ClassNotFoundException if a serialised class cannot be found 
     */
    public static SimulationEngine load(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            SimulationEngine engine = (SimulationEngine) in.readObject();
            engine.rebuildNeighborhood();
            return engine;
        }
    }
}