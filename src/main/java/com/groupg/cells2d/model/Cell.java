package com.groupg.cells2d.model;

/**
 * pas sûre si elle est nécessaire ou si elle est dans Grid mais je la met pour continuer avec
 * GridNeighborhood qu'il faudrait appeler CellNeighborhood d'ailleurs
 */

public class Cell {
    int col;
    int row;
    Cell cell;

    public int getRow(){
        return cell.row;
    }

    public int getCol(){
        return cell.col;
    }

}