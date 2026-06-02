package com.groupg.cells2d.engine;

import com.groupg.cells2d.model.board.Cell;

import java.util.ArrayList;
import java.util.List;

/**
 * class to get the neighbors of our target cell,
 * not in Cell cause any cell doesn't need to know it's neighbors and all th deed needs to be done in engine
 */

public class CellNeighborhood{
    private Cell[][] cells;

    /**
     * constructor of our class CellNeighborhood
     * @param cells List of cells we give to our method
     */
    public CellNeighborhood(Cell[][] cells){
        this.cells=cells;
    }

    /**
     * getter to enable to get the values of cells
     * @return cells the List of our cells
     */
    public Cell[][] getCells(){
        return cells;
    }

    /**
     * Function returning the List of neighbors of our target cell, all around it (up, down, right, left, and diagonals)
     * @param cell our target cell
     * @return list of the target cell's neighbors
     */
    public List<Cell> getNeighbors(Cell cell){
        List<Cell> neighbors=new ArrayList<>();
        int[][] directions={
                {-1,0},{1,0},{0,-1},{0,1},       // where target cell [i,j] and all the 8 around it are [i,j] : top left -1,-1 top middle -1,0 top right -1,1
                {-1,-1},{1,1},{-1,1},{1,-1}      // middle left 0,-1 middle right 0,1
        };                                       // bottom left 1,-1 bottom middle 1,0 bottom right 1,1
        for(int d=0;d<directions.length;d++){      //loop to get the neighbor cells using getRow and getCol of cell
            int rowMove = directions[d][0];     // gets the couple at d and saves it's first value, the row ex: [d]: 0,1 and [d][0]: 0 and [d][1]: 1
            int colMove = directions[d][1];
            int neighbori = cell.getRow() + rowMove;
            int neighborj = cell.getCol() + colMove;

            if (neighbori >= 0 && neighbori < cells.length && neighborj >= 0 && neighborj < cells[0].length){
                neighbors.add(cells[neighbori][neighborj]);
            }
        }
    return neighbors;
    }
}