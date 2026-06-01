package com.groupg.cells2d.engine;

import com.groupg.cells2d.model.board.Cell;

import java.util.ArrayList;
import java.util.List;

/**
 * obtenir le voisinage de la cellule
 */

public class CellNeighborhood{
    private Cell[][] cells;

    public CellNeighborhood(Cell[][] cells){
        this.cells=cells;
    }

    public Cell[][] getCells(){
        return cells;
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
        for(int d=0;d<directions.length;d++) {
            int neighbori = cell.getRow() + directions[d][0];
            int neighborj = cell.getCol() + directions[d][1];
            if (neighbori >= 0 && neighbori < cells.length && neighborj >= 0 && neighborj < cells[0].length) {
                neighbors.add(cells[neighbori][neighborj]);
            }
        }
    return neighbors;
    }
}