package com.groupg.cells2d.engine;

import com.groupg.cells2d.model.board.Cell;
import com.groupg.cells2d.model.board.Grid;
import com.groupg.cells2d.model.board.SEIRData;
import com.groupg.cells2d.model.enums.SimStatus;

import java.util.List;

/**
 * the heart class, principal loop that changes the state of our simulation
 * manages play, pause, stop and step by step simulation
 */

public class SimulationEngine{
    private Grid grid;
    private Propagation propagation;
    private CellNeighborhood neighborhood;
    private SimStatus status;
    private int stepCount;

    /**
     * constructor of SimulationEngine
     * @param grid the entire map of our cells
     * @param propagation parameters for our propagation and state changements
     */
    public SimulationEngine(Grid grid, Propagation propagation){
        this.grid=grid;
        this.propagation=propagation;
        this.neighborhood=new CellNeighborhood(grid.getMap());
        this.status=SimStatus.IDLE;
        this.stepCount=0;
    }

    /**
     * getter for status
     * @return status
     */
    public SimStatus getStatus(){return status;}

    /**
     * getter for stepCount
     * @return stepCount
     */
    public int getStepCount(){return stepCount;}

    /**
     * getter for our grid
     * @return the grid
     */
    public Grid getGrid(){return grid;}

    /**
     * moves our simulation step by step for determined period of time
     * executes one simulation step : updates all cells in the grid
     * based on their neighbors and the propagation rules
     */
    public void step(){
        int rows=grid.getRows();
        int cols= grid.getCols();

        //place to keep new states
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
                    cell.setSeirData(newDataBuffer[i][j]);
                    double infectionRate = cell.getPopulation() > 0 ?
                            newDataBuffer[i][j].getInfected() / cell.getPopulation() : 0;
                    cell.updateState(infectionRate);
                }
            }
        }
        stepCount++;
    }

    /**
     * begins our simulation changes the state to running
     * starts the simulation in a separate thread
     * updates the grid every 500ms until paused or stopped
     * @throws InterruptedException if the thread is interrupted
     */
    public void play(){
        this.status=SimStatus.RUNNING;
        Thread thread=new Thread(()-> {
            while (this.status == SimStatus.RUNNING) {
                step();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * pauses the simulation
     * can be resumed with play
     */
    public void pause(){
        this.status=SimStatus.PAUSED;
    }

    /**
     * stops the simulation definitively and resets the step counter to 0
     */
    public void stop(){
        this.status=SimStatus.FINISHED;
        this.stepCount=0;
    }
}
