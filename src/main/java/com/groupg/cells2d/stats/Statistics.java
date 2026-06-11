package com.groupg.cells2d.stats;

import com.groupg.cells2d.model.board.Cell;
import com.groupg.cells2d.model.board.Grid;

/**
 * les données
 */

public class Statistics {
    private Statistics(){
        //
    }

    public static Snapshot compute(Grid grid, int step){
        int healthy = 0;
        int partial = 0;
        int infected = 0;
        int critical = 0;
        int total = 0;

        for (int row = 0; row < grid.getRows(); row++){
            for(int col =0; col < grid.getCols(); col ++ ){
                Cell cell = grid.getCell(row, col);
                
                if (cell == null){
                    continue;
                }
                total ++;

                switch (cell.getState()) {
                    case HEALTHY:
                        healthy++;
                        break;

                    case PARTIAL:
                        partial++;
                        break;

                    case INFECTED:
                        infected++;
                        break;

                    case CRITICAL:
                        critical++;
                        break;
                }
            }
        }

        return new Snapshot(step,total,healthy,partial,infected,critical);        
    }
}

