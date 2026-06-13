package com.groupg.cells2d.engine;

import com.groupg.cells2d.model.board.Cell;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Resolves the 8-directional neighbourhood of any cell in the grid.
 * Kept separate from {@link com.groupg.cells2d.model.board.Cell} so that spatial
 * logic stays in the engine layer.
 */

public class CellNeighborhood implements Serializable {
    private Cell[][] cells;
    private static final long serialVersionUID = 1L;

    /**
     * Builds the neighbourhood resolver for the given cell map.
     * @param cells 2D array of all cells in the grid
     */
    public CellNeighborhood(Cell[][] cells){
        this.cells=cells;
    }

    /**
     * Returns the raw cell map.
     * @return 2D array of all cells
     */
    public Cell[][] getCells(){
        return cells;
    }

    /**
     * Returns the up-to-8 neighbours of the given cell (cardinal + diagonal directions).
     * Out-of-bounds positions and cells outside Paris are excluded.
     * @param cell the cell whose neighbours are needed
     * @return list of valid neighbouring cells
     */
    public List<Cell> getNeighbors(Cell cell){
        List<Cell> neighbors=new ArrayList<>();
        int[][] directions={
                {-1,0},{1,0},{0,-1},{0,1},   // cardinal directions: top, bottom, left, right
                {-1,-1},{1,1},{-1,1},{1,-1}  // diagonal directions
        };
        for(int d=0;d<directions.length;d++){
            int rowMove = directions[d][0];
            int colMove = directions[d][1];
            int neighbori = cell.getRow() + rowMove;
            int neighborj = cell.getCol() + colMove;

            if (neighbori >= 0 && neighbori < cells.length && neighborj >= 0 && neighborj < cells[0].length){
                Cell neighbor = cells[neighbori][neighborj];
                if (neighbor !=null && neighbor.isInsideParis()) {
                    neighbors.add(neighbor);
                }
            }
        }
        return neighbors;
    }
}