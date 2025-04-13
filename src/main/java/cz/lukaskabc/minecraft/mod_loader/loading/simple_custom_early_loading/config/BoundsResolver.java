package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config;

/**
 * Provides bounds resolution for element position and size on the screen
 * using the original texture and available screen size.
 */
@FunctionalInterface
public interface BoundsResolver {
    /**
     * Resolves the bounds of the element on the screen.
     *
     * @param elementWidth  the width of the element texture
     * @param elementHeight the height of the element texture
     * @param screenWidth   the width of the available screen
     * @param screenHeight  the height of the available screen
     * @return array of 4 integers (x0, y0, x1, y1) representing the bounds of the element on the screen
     */
    int[] resolveBounds(int elementWidth, int elementHeight, int screenWidth, int screenHeight);
}
