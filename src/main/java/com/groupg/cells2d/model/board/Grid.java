package com.groupg.cells2d.model.board;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents the simulation grid composed of a 2D array of cells.
 * It manages the spatial structure and relations between cells.
 */
public class Grid {
    private Cell[][] map; //Map <coordi, Cell>
    private int rows;
    private int cols;

    /**
     * Constructs a new Grid with the specified dimensions.
     * * @param rows the number of rows in the grid
     * @param cols the number of columns in the grid
     */
    public Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.map = new Cell[rows][cols]; 
    }

    /**
     * Function returning the List of neighbors of our target cell, all around it (up, down, right, left, and diogonals)
     * @param cell our target cell
     * @return list of the target cell neighbors
     */
    public List<Cell> getNeighbors(Cell cell){
        List<Cell> neighbors=new ArrayList<>();
        int[][] directions={
                {-1,0},{1,0},{0,-1},{0,1},
                {-1,-1},{1,1},{-1,1},{1,-1}
        };
        // Get the current position of target cell
        int currentI = cell.getRow();
        int currentJ = cell.getCol();

        //Iterate through all 8 relative directions
        for(int d=0;d<directions.length;d++) {
            int neighbori = cell.getRow() + directions[d][0];
            int neighborj = cell.getCol() + directions[d][1];
            //Boundary check 
            if (neighbori >= 0 && neighbori < cells.length && neighborj >= 0 && neighborj < cells[0].length) {
                neighbors.add(cells[neighbori][neighborj]);
            }
        }
        return neighbors;
    }
    

    /* Functions */

    public Grid clone(){
        Grid newOne = new Grid(this.rows, this.cols);

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