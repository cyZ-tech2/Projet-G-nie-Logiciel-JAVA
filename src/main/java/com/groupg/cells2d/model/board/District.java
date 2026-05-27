package com.groupg.cells2d.model.board;
import java.util.List;

public class District {

    private String name;
    private Grid grid;

    public District(String name, int rows, int cols) {
        this.name = name;
        this.grid = new Grid(rows, cols);
    }

    public District(String name, Grid grid) {
        this.name = name;
        this.grid = grid;
    }

    public List<Cell> getNeighbours(int row, int col) {
        return grid.getNeighbours(row, col);
    }

    public Grid clone() {
        return grid.clone();
    }

    public Cell getCell(int row, int col){
        return grid.getCell(row, col);
    }
    public void setCell(int row, int col, Cell c){
        grid.setCell(row, col, c);
    }

    /* Override */

    @Override
    public String toString() {
        return "District : name : " + name + ", rows : " + grid.getRows() + ", cols : " + grid.getCols() + "";
    }

    /* Getters and setters*/

    public String getName(){
        return name;
    }

    public void   setName(String name){
        this.name = name;
    }

    public Grid getGrid(){
        return grid;
    }

    public int getRows(){
        return grid.getRows();
    }

    public int getCols(){
        return grid.getCols();
    }

}


