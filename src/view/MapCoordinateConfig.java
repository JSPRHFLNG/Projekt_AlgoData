package view;

import model.quadtree.Quadtree;

public class MapCoordinateConfig {
    public static final double MAP_MIN_X = 258000;
    public static final double MAP_MAX_X = 930000;
    public static final double MAP_MIN_Y = 6120000;
    public static final double MAP_MAX_Y = 7700000;

    public static Quadtree.Rectangle getDefaultBoundary() {
        return new Quadtree.Rectangle(
                (MAP_MIN_X + MAP_MAX_X) / 2,
                (MAP_MIN_Y + MAP_MAX_Y) / 2,
                MAP_MAX_X - MAP_MIN_X,
                MAP_MAX_Y - MAP_MIN_Y
        );
    }
}