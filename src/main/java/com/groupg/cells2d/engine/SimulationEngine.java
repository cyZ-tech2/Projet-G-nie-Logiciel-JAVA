package com.groupg.cells2d.engine;

import com.groupg.cells2d.model.board.Cell;
import com.groupg.cells2d.model.board.Grid;
import com.groupg.cells2d.model.board.SEIRData;
import com.groupg.cells2d.model.enums.SimStatus;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.io.Serializable;

/**
 * Core simulation class. Drives the main loop that advances the epidemic state.
 * Manages play, pause, stop, and step-by-step execution.
 */

public class SimulationEngine implements Serializable {
    private Grid grid;
    private Propagation propagation;
    private transient CellNeighborhood neighborhood;
    private SimStatus status;
    private int stepCount;
    private int stepDuration;
    private static final long serialVersionUID = 1L;
    private static final int MAX_HISTORY = 100;
    private final Deque<Grid> history = new ArrayDeque<>();

    /**
     * Builds a new SimulationEngine.
     * @param grid       the full cell map to simulate
     * @param propagation propagation rules and SEIR parameters
     * @throws IllegalArgumentException if grid is null
     * @throws IllegalArgumentException if propagation is null
     */
    public SimulationEngine(Grid grid, Propagation propagation){
        if(grid == null) throw new IllegalArgumentException("grid cannot be null");
        if(propagation == null) throw new IllegalArgumentException("propagation cannot be null");


        this.grid=grid;
        this.propagation=propagation;
        this.neighborhood=new CellNeighborhood(grid.getMap());
        this.status= SimStatus.IDLE;
        this.stepCount=0;
        this.stepDuration=100;
    }

    /**
     * Returns the delay between two automatic simulation steps in milliseconds.
     * @return step duration in ms
     */
    public int getStepDuration() {return stepDuration;}

    /**
     * Sets the delay between two automatic simulation steps.
     * @param stepDuration duration in milliseconds (must be positive)
     * @throws IllegalArgumentException if stepDuration is zero or negative
     */
    public void setStepDuration(int stepDuration) {
        if(stepDuration <= 0) throw new IllegalArgumentException("stepDuration must be positive");
        this.stepDuration = stepDuration;
    }

    /**
     * Returns the current simulation status.
     * @return current status
     */
    public SimStatus getStatus(){return status;}

    /**
     * Returns the number of simulation steps completed.
     * @return step count
     */
    public int getStepCount(){return stepCount;}

    /**
     * Returns the simulation grid.
     * @return the grid
     */
    public Grid getGrid(){return grid;}

    /**
     * Advances the simulation by one step.
     * Updates every cell based on its neighbours and the propagation rules.
     * The previous grid state is pushed onto the history stack for rewinding.
     * @throws IllegalStateException if neighborhood is null call rebuildNeighborhood() first
     * @throws IllegalStateException if grid is null
     */
    public void step(){
        double totalDead = 0;
        double totalPop = 0;
        if(neighborhood == null) throw new IllegalStateException("neighborhood is null, call rebuildNeighborhood() first");
        if(grid == null) throw new IllegalStateException("grid is null");

        // Push a copy onto the history stack before mutating
        history.push(deepCopyGrid(grid));
        if (history.size() > MAX_HISTORY) history.pollLast();

        int rows=grid.getRows();
        int cols= grid.getCols();

        // Buffer for the next SEIR state of each cell
        SEIRData[][] newDataBuffer=new SEIRData[rows][cols];

        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                Cell cell=grid.getMap()[i][j];
                if(cell==null) continue;
                List<Cell> neighbors=neighborhood.getNeighbors(cell);
                newDataBuffer[i][j] = propagation.apply(cell, neighbors);
            }
        }
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                Cell cell = grid.getMap()[i][j];
                if(cell != null && newDataBuffer[i][j] != null){
                    totalDead += cell.getSeirData().getDead();
                    totalPop += cell.getPopulation();
                    cell.setSeirData(newDataBuffer[i][j]);
                    double infectionRate = cell.getPopulation() > 0 ?
                            newDataBuffer[i][j].getInfected() / cell.getPopulation() : 0;
                    cell.updateState(infectionRate);
                }
            }
        }
        if(totalDead/totalPop >= 0.5) {
            this.status = SimStatus.FINISHED;
        }
        stepCount++;
    }

    /**
     * Starts the simulation on a background daemon thread.
     * The thread advances one step every {@code stepDuration} ms
     * until the status is no longer RUNNING.
     * @throws IllegalStateException if simulation already running
     */
    public void play(){
        if(this.status == SimStatus.RUNNING) throw new IllegalStateException("simulation is already running");
        this.status= SimStatus.RUNNING;
        Thread thread=new Thread(()-> {
            while (this.status == SimStatus.RUNNING  ) {
                step();
                try {
                    Thread.sleep(this.stepDuration);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Pauses the simulation. Call {@link #play()} to resume.
     */
    public void pause(){
        this.status= SimStatus.PAUSED;
    }

    /**
     * Stops the simulation and resets the step counter to zero.
     */
    public void stop(){
        this.status= SimStatus.FINISHED;
        this.stepCount=0;
    }

    /**
     * Reverts the simulation to the previous step.
     * @return true if rewind was possible, false if no history
     */
    public boolean rewind() {
        if (history.isEmpty()) return false;
        this.grid = history.pop();
        this.neighborhood = new CellNeighborhood(grid.getMap());
        if (stepCount > 0) stepCount--;
        return true;
    }

    /**
     * Returns true if there is at least one previous state in the history stack.
     * @return true if rewinding is possible
     */
    public boolean canRewind() {
        return !history.isEmpty();
    }

    /**
     * Creates a deep copy of a grid including all cell states and SEIR data.
     * Used to push a snapshot onto the history stack before each step.
     * @param source the grid to copy
     * @return an independent copy
     */
    private Grid deepCopyGrid(Grid source) {
        int rows = source.getRows();
        int cols = source.getCols();
        Grid copy = new Grid(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                com.groupg.cells2d.model.board.Cell orig = source.getMap()[i][j];
                if (orig == null) continue;
                com.groupg.cells2d.model.board.SEIRData sd = orig.getSeirData();
                com.groupg.cells2d.model.board.SEIRData sdCopy = new com.groupg.cells2d.model.board.SEIRData(
                    sd.getSusceptible(), sd.getExposed(), sd.getInfected(), sd.getRecovered(), sd.getDead());
                com.groupg.cells2d.model.board.Cell cellCopy = new com.groupg.cells2d.model.board.Cell(
                    orig.getCellId(), orig.getPopulation(), orig.getState(), sdCopy, orig.getRow(), orig.getCol());
                cellCopy.setInsideParis(orig.isInsideParis());
                cellCopy.setDistrictId(orig.getDistrictId());
                cellCopy.setDistrictName(orig.getDistrictName());
                cellCopy.setAlive(orig.isAlive());
                copy.setCell(i, j, cellCopy);
            }
        }
        return copy;
    }

    /**
     * Rebuilds the cell neighbourhood index after deserialisation or a grid swap.
     * @throws IllegalStateException if grid is null, neighborhood cannot be rebuilded
     */
    public void rebuildNeighborhood(){
        if(grid == null) throw new IllegalStateException("grid is null, cannot rebuild neighborhood");
        this.neighborhood = new CellNeighborhood(grid.getMap());
    }
}