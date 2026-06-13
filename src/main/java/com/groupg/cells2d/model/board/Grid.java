package com.groupg.cells2d.model.board;
import java.util.ArrayList;
import java.util.List;
import com.groupg.cells2d.model.exceptions.InvalidGridCoordinatesException;
import java.io.Serializable;
/**
 * Represents the simulation grid composed of a 2D array of cells.
 * It manages the spatial structure and relations between cells.
 */
public class Grid implements Serializable {
    private Cell[][] map; // 2D array indexed by [row][col]
    private int rows;
    private int cols;
    private static final long serialVersionUID = 1L;

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
     * Returns all valid neighbours of a cell (cardinal + diagonal directions).
     * @param cell the target cell
     * @return list of adjacent cells
     */
    public List<Cell> getNeighbours(Cell cell){
        List<Cell> neighbors=new ArrayList<>();
        int[][] directions={
                {-1,0},{1,0},{0,-1},{0,1},
                {-1,-1},{1,1},{-1,1},{1,-1}
        };

        // Iterate through all 8 relative directions
        for(int d=0;d<directions.length;d++) {
            int neighbori = cell.getRow() + directions[d][0];
            int neighborj = cell.getCol() + directions[d][1];
            // Boundary check
            if (neighbori >= 0 && neighbori < map.length && neighborj >= 0 && neighborj < map[0].length) {
                neighbors.add(map[neighbori][neighborj]);
            }
        }
        return neighbors;
    }


    // -------------------------------------------------------------------------
    // Grid operations
    // -------------------------------------------------------------------------

    /**
     * Returns a shallow clone of this grid (same dimensions, empty cell map).
     * For a deep copy including cell data use
     * {@link com.groupg.cells2d.engine.SimulationEngine}'s internal deep-copy logic.
     */
    public Grid clone(){
        Grid newOne = new Grid(this.rows, this.cols);

        return newOne;
    }


    // Bounds check used by getCell to guard against invalid coordinates

    private boolean isInBounds(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Grid : rows = " + rows + ", cols = " + cols + " ";
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    /** Returns the raw 2D cell array. */
    public Cell[][] getMap()  {
        return map;
    }

    /**
     * Returns the cell at the given coordinates.
     * @param row row index
     * @param col column index
     * @return the cell, or null if that position is empty
     * @throws com.groupg.cells2d.model.exceptions.InvalidGridCoordinatesException if out of bounds
     */
    public Cell getCell(int row, int col) {
        if (!isInBounds(row, col)) {
            throw new InvalidGridCoordinatesException(row, col);
        }

        return map[row][col];
    }

    /**
     * Places a cell at the given position.
     * Silently ignores out-of-bounds coordinates.
     * @param row  row index
     * @param col  column index
     * @param cell the cell to place
     */
    public void setCell(int row, int col, Cell cell) {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            map[row][col] = cell;
        }
    }

    /** Replaces the entire cell map. */
    public void setMap(Cell[][] map) {
        this.map = map;
    }

    /** Returns the number of rows in this grid. */
    public int getRows() {
        return rows;
    }
    /** Sets the number of rows (does not resize the map array). */
    public void setRows(int rows) {
        this.rows = rows;
    }

    /** Returns the number of columns in this grid. */
    public int getCols() {
        return cols;
    }
    /** Sets the number of columns (does not resize the map array). */
    public void setCols(int cols) {
        this.cols = cols;
    }






}