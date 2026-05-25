package com.groupg.cells2d.model.board;
import java.awt.Point;
import com.groupg.cells2d.model.enums.CellState;

public class Cell{
    private String cellId;
    private int population;
    private CellState state;
    private SEIRData seirData;
    private Point coordinates;

    public Cell(String cellId, int population, Point coordinates) {
        this.cellId = cellId;
        this.population = population;
        this.coordinates = coordinates;
        this.state = CellState.HEALTHY;
        this.seirData = new SEIRData();
    }

    public Cell(String cellId, int population, CellState state, SEIRData seirData, Point coordinates) {
        this.cellId = cellId;
        this.population = population;
        this.state = state;
        this.seirData = seirData;
        this.coordinates = coordinates;
    }

    public void evolve(TimeStep step) {

    }

    public void addCase(PatientCase c) {

    }

    public double getInfectionRate() {

    }

    /* Override */

    @Override
    public String toString() {
        return "Cell{" + "cellId='" + cellId + '\'' + ", population=" + population + ", state=" + state + ", coordinates=" + coordinates + '}';
    }

    /* Getters and setters */

    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public CellState getState() {
        return state;
    }

    public void setState(CellState state) {
        this.state = state;
    }

    public SEIRData getSeirData() {
        return seirData;
    }

    public void setSeirData(SEIRData seirData) {
        this.seirData = seirData;
    }

    public Point getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Point coordinates) {
        this.coordinates = coordinates;
    }



}

