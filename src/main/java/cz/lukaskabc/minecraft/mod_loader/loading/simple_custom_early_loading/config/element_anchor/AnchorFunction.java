package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.element_anchor;

@FunctionalInterface
public interface AnchorFunction {
    /**
     * Transforms the given coordinates.
     *
     * @param x      the x coordinate on the screen
     * @param y      the y coordinate on the screen
     * @param width  the width of the element
     * @param height the height of the element
     * @return 4 element array containing the transformed coordinates {@code {x0, y0, x1, y1}}
     */
    int[] apply(int x, int y, int width, int height);
}