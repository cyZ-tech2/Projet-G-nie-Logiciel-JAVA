package com.groupg.cells2d.model.board;
import java.util.ArrayList;
import java.util.List;

public class Grid {
    private Cell[][] map;
    private int rows;
    private int cols;


    public Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.map = new Cell[rows][cols];
    }

    /* Functions */

    public Grid clone(){
        Grid newOne = new Grid(this.rows, this.cols);
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                newOne.setCell(i,j,map[i][j]);
            }
        }
        return newOne;
    }


    /* I put the exception here for the moment*/

    private boolean isInBounds(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    /* Override */

    @Override
    public String toString() {
        return "Grid : rows = " + rows + ", cols = " + cols + " ";
    }

    /* Getters and setters */

    public Cell[][] getMap()  {
        return map;
    }

    public Cell getCell(int row, int col) {
        if (isInBounds(row, col)) {
            return map[row][col];
        }
        return null;
    }

    public void setCell(int row, int col, Cell cell) {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            map[row][col] = cell;
        }
    }

    public void setMap(Cell[][] map) {
        this.map = map;
    }

    public int getRows() {
        return rows;
    }
    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }
    public void setCols(int cols) {
        this.cols = cols;
    }






}