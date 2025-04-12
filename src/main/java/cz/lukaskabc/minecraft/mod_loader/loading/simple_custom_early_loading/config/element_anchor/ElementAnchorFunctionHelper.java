package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.element_anchor;

/**
 * Provides implementation for position coords adjustments of {@link ElementAnchor}
 */
public class ElementAnchorFunctionHelper {
    private ElementAnchorFunctionHelper() {
        throw new AssertionError();
    }

    public static int[] topLeft(int x, int y, int width, int height) {
        return new int[]{x, y, x + width, y + height};
    }

    public static int[] topRight(int x, int y, int width, int height) {
        return new int[]{x - width, y, x, y + height};
    }

    public static int[] bottomLeft(int x, int y, int width, int height) {
        return new int[]{x, y - height, x + width, y};
    }

    public static int[] bottomRight(int x, int y, int width, int height) {
        return new int[]{x - width, y - height, x, y};
    }

    public static int[] center(int x, int y, int width, int height) {
        return new int[]{x - width / 2, y - height / 2, x + width / 2, y + height / 2};
    }

    public static int[] topCenter(int x, int y, int width, int height) {
        return new int[]{x - width / 2, y, x + width / 2, y + height};
    }

    public static int[] bottomCenter(int x, int y, int width, int height) {
        return new int[]{x - width / 2, y - height, x + width / 2, y};
    }

    public static int[] leftCenter(int x, int y, int width, int height) {
        return new int[]{x, y - height / 2, x + width, y + height / 2};
    }

    public static int[] rightCenter(int x, int y, int width, int height) {
        return new int[]{x - width, y - height / 2, x, y + height / 2};
    }
}
