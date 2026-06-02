package com.groupg.cells2d.controller;

import com.groupg.cells2d.model.board.Grid;
import com.groupg.cells2d.model.board.Cell;
import com.groupg.cells2d.model.enums.CellState;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.util.Random;

public class SimulationController {
    @FXML private GridPane gridPane;
    @FXML private Label statsLabel;
    @FXML private Button createButton, startButton, pauseButton;

    private final int ROWS = 25, COLS = 25, CELL_SIZE = 18;
    private Grid grid;
    private Rectangle[][] nodes;
    private Timeline tl;
    private int t = 0;
    private final Random r = new Random();

    @FXML
    public void handleCreateSimulation(ActionEvent e) {
        if (tl != null) tl.stop();
        tl = null; t = 0;
        grid = new Grid(ROWS, COLS);
        nodes = new Rectangle[ROWS][COLS];
        gridPane.getChildren().clear();

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                Cell c = new Cell("C_" + i + "_" + j, 100, i, j);
                grid.setCell(i, j, c);
                Rectangle rect = new Rectangle(CELL_SIZE, CELL_SIZE, Color.GREEN);
                rect.setStroke(Color.web("#34495e"));
                rect.setStrokeWidth(0.5);
                nodes[i][j] = rect;
                gridPane.add(rect, j, i);
            }
        }
        for (int k = 0; k < 6; k++) {
            Cell c = grid.getCell(r.nextInt(ROWS), r.nextInt(COLS));
            if (c != null) {
                c.getSeirData().setSusceptible(70);
                c.getSeirData().setExposed(30);
                c.setState(CellState.PARTIAL);
                nodes[c.getRow()][c.getCol()].setFill(Color.YELLOW);
            }
        }
        statsLabel.setText("Simulation initialisée (Vert et Jaune). Prêt.");
        startButton.setDisable(false); pauseButton.setDisable(true);
    }

    @FXML
    public void handleStartSimulation(ActionEvent e) {
        if (tl == null) tl = new Timeline(new KeyFrame(Duration.millis(600), ev -> step()));
        tl.setCycleCount(Timeline.INDEFINITE);
        tl.play();
        startButton.setDisable(true); pauseButton.setDisable(false);
    }

    @FXML
    public void handlePauseSimulation(ActionEvent e) {
        if (tl != null) {
            if (tl.getStatus() == Animation.Status.RUNNING) { tl.pause(); statsLabel.setText(statsLabel.getText() + " (PAUSE)"); }
            else tl.play();
        }
    }

    private void step() {
        t++;
        double[][] pressure = new double[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                Cell c = grid.getCell(i, j);
                if (c == null) continue;
                for (Cell n : grid.getNeighbours(c)) {
                    if (n != null && n.getState() != CellState.HEALTHY) pressure[i][j] += n.getSeirData().getInfected() + 2.0;
                }
            }
        }

        int h = 0, p = 0, inf = 0, cr = 0;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                Cell c = grid.getCell(i, j);
                if (c == null) continue;

                double S = c.getSeirData().getSusceptible();
                if (pressure[i][j] > 0 && S > 0) {
                    double v = Math.min(S, (0.06 * S * pressure[i][j]) / c.getPopulation());
                    c.getSeirData().setSusceptible(S - v);
                    c.getSeirData().setExposed(c.getSeirData().getExposed() + v);
                }
                c.evolve(0.35, 0.2, 0.12, 0.03);

                CellState current = c.getState();
                if (current == CellState.HEALTHY && c.getSeirData().getExposed() > 1.0) c.setState(CellState.PARTIAL);
                else if (current == CellState.PARTIAL && c.getSeirData().getInfected() > 3.0) c.setState(CellState.INFECTED);
                else if (current == CellState.INFECTED && (r.nextDouble() < 0.08 || c.getSeirData().getSusceptible() < 5)) c.setState(CellState.CRITICAL);

                Color col = switch (c.getState()) {
                    case HEALTHY -> { h++; yield Color.GREEN; }
                    case PARTIAL -> { p++; yield Color.YELLOW; }
                    case INFECTED -> { inf++; yield Color.ORANGE; }
                    case CRITICAL -> { cr++; yield Color.RED; }
                };
                nodes[i][j].setFill(col);
            }
        }
        statsLabel.setText(String.format("Temps : %d | Healthy : %d | Partial : %d | Infected : %d | Critical : %d", t, h, p, inf, cr));
        if (cr == ROWS * COLS) { tl.stop(); startButton.setDisable(true); pauseButton.setDisable(true); statsLabel.setText("Simulation terminée au Temps : " + t); }
    }
}