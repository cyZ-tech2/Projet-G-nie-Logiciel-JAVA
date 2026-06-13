package com.groupg.cells2d.model.board;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds the simulation grid directly from the simplified Paris image
 * paris-districts.png.
 *
 * The image is used as the visual background. The grid is generated over the
 * same 265x190 coordinate system, so the cells are aligned with the map.
 * District membership is assigned once in the backend: one cell = one district.
 */
public final class ParisGridFactory {

    public static final int    DEFAULT_ROWS = 38;
    public static final int    DEFAULT_COLS = 53;
    public static final double MAP_WIDTH    = 265.0;
    public static final double MAP_HEIGHT   = 190.0;
    public static final String MAP_RESOURCE = "/img/paris-districts.png";

    private static final List<DistrictSeed> DISTRICT_SEEDS = createDistrictSeeds();

    private ParisGridFactory() {} // utility class — not instantiable

    /**
     * Creates the default 38×53 Paris simulation grid.
     * @return ready-to-use grid
     */
    public static Grid createDefaultParisGrid() {
        return createParisGrid(DEFAULT_ROWS, DEFAULT_COLS);
    }

    /**
     * Builds a Paris simulation grid of the given dimensions.
     * Each cell is classified as inside/outside Paris using the district mask image,
     * and assigned to the nearest arrondissement centroid.
     * @param rows number of grid rows
     * @param cols number of grid columns
     * @return populated grid
     */
    public static Grid createParisGrid(int rows, int cols) {
        Grid grid = new Grid(rows, cols);
        BufferedImage image = loadMapImage();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double centerX = (col + 0.5) * MAP_WIDTH  / cols;
                double centerY = (row + 0.5) * MAP_HEIGHT / rows;
                boolean insideParis = isInsideParisImage(image, centerX, centerY);
                // Cells inside Paris get a default population of 1200; outside cells get 0
                Cell cell = new Cell("PARIS-" + row + "-" + col, insideParis ? 1200 : 0, row, col);
                cell.setInsideParis(insideParis);
                // Assign the nearest district to every Paris cell
                if (insideParis) {
                    DistrictSeed district = nearestDistrict(centerX, centerY);
                    cell.setDistrictId(district.id);
                    cell.setDistrictName("Arrondissement " + district.id);
                }
                grid.setCell(row, col, cell);
            }
        }
        return grid;
    }

    /**
     * Returns the arrondissement name for a cell at (row, col) in a grid of given size.
     * @param row  cell row
     * @param col  cell column
     * @param rows total grid rows
     * @param cols total grid columns
     * @return "Arrondissement N"
     */
    public static String getDistrictNameFor(int row, int col, int rows, int cols) {
        double centerX = (col + 0.5) * MAP_WIDTH  / cols;
        double centerY = (row + 0.5) * MAP_HEIGHT / rows;
        return "Arrondissement " + nearestDistrict(centerX, centerY).id;
    }

    /**
     * Loads the Paris district mask image from the classpath.
     * @return the buffered image
     * @throws IllegalStateException if the resource is missing or unreadable
     */
    private static BufferedImage loadMapImage() {
        try (InputStream input = ParisGridFactory.class.getResourceAsStream(MAP_RESOURCE)) {
            if (input == null) throw new IllegalStateException("Resource not found: " + MAP_RESOURCE);
            return ImageIO.read(input);
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Cannot load Paris map image", e);
        }
    }

    /**
     * Determines whether the point (x, y) in map coordinates falls inside Paris
     * by sampling a 5×5 pixel neighbourhood in the mask image.
     * A point is considered inside Paris if at least 2 of the sampled pixels are non-white.
     * @param image the Paris boundary mask
     * @param x     x coordinate in map units
     * @param y     y coordinate in map units
     * @return true if the point is inside Paris
     */
    private static boolean isInsideParisImage(BufferedImage image, double x, double y) {
        int cx = clamp((int) Math.round(x), 0, image.getWidth()  - 1);
        int cy = clamp((int) Math.round(y), 0, image.getHeight() - 1);
        int radius = 2, nonWhite = 0, samples = 0;
        for (int yy = cy - radius; yy <= cy + radius; yy++) {
            for (int xx = cx - radius; xx <= cx + radius; xx++) {
                if (xx < 0 || xx >= image.getWidth() || yy < 0 || yy >= image.getHeight()) continue;
                samples++;
                if (!isBackgroundWhite(image.getRGB(xx, yy))) nonWhite++;
            }
        }
        return samples > 0 && nonWhite >= 2;
    }

    /**
     * Returns true if the given ARGB pixel value represents a near-white background colour.
     * Used to distinguish the white background from Paris district colours in the mask.
     * @param rgb ARGB pixel value
     * @return true if white (R, G, B all > 245)
     */
    private static boolean isBackgroundWhite(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >>  8) & 0xff;
        int b =  rgb        & 0xff;
        return r > 245 && g > 245 && b > 245;
    }

    /**
     * Returns the district whose centroid is closest (Euclidean distance) to (x, y).
     * @param x x coordinate in map units
     * @param y y coordinate in map units
     * @return nearest district seed
     */
    private static DistrictSeed nearestDistrict(double x, double y) {
        DistrictSeed best = DISTRICT_SEEDS.get(0);
        double bestDistance = Double.MAX_VALUE;
        for (DistrictSeed seed : DISTRICT_SEEDS) {
            double dx = x - seed.x, dy = y - seed.y;
            double distance = dx * dx + dy * dy;
            if (distance < bestDistance) { bestDistance = distance; best = seed; }
        }
        return best;
    }

    /**
     * Clamps an integer value to the range [min, max].
     * @param value the value to clamp
     * @param min   lower bound (inclusive)
     * @param max   upper bound (inclusive)
     * @return clamped value
     */
    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Returns the list of arrondissement centroid seeds used for nearest-district assignment.
     * Coordinates are in the 265×190 map pixel space.
     * @return list of district seeds (arrondissements 1–20)
     */
    private static List<DistrictSeed> createDistrictSeeds() {
        List<DistrictSeed> seeds = new ArrayList<>();
        seeds.add(new DistrictSeed("1",  148, 96));
        seeds.add(new DistrictSeed("2",  146, 80));
        seeds.add(new DistrictSeed("3",  169, 93));
        seeds.add(new DistrictSeed("4",  156,105));
        seeds.add(new DistrictSeed("5",  151,123));
        seeds.add(new DistrictSeed("6",  132,108));
        seeds.add(new DistrictSeed("7",  109,110));
        seeds.add(new DistrictSeed("8",  108, 67));
        seeds.add(new DistrictSeed("9",  139, 61));
        seeds.add(new DistrictSeed("10", 171, 62));
        seeds.add(new DistrictSeed("11", 197, 91));
        seeds.add(new DistrictSeed("12", 208,127));
        seeds.add(new DistrictSeed("13", 171,157));
        seeds.add(new DistrictSeed("14", 130,157));
        seeds.add(new DistrictSeed("15",  84,128));
        seeds.add(new DistrictSeed("16",  70, 91));
        seeds.add(new DistrictSeed("17", 100, 40));
        seeds.add(new DistrictSeed("18", 158, 31));
        seeds.add(new DistrictSeed("19", 200, 39));
        seeds.add(new DistrictSeed("20", 227, 86));
        return seeds;
    }

    private static final class DistrictSeed {
        final String id;
        final double x, y;
        DistrictSeed(String id, double x, double y) { this.id = id; this.x = x; this.y = y; }
    }
}
