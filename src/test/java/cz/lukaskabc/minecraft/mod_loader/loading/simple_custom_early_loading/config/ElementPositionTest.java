package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config;

import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.element_anchor.ElementAnchor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ElementPositionTest {
    private static final int imageTextureWidth = 654;
    private static final int imageTextureHeight = 152;
    private static final int windowWidth = 1920;
    private static final int windowHeight = 1080;
    private static final float widthPercentage = 50;
    private static final float heightPercentage = 11.62079f;
    private static final float expectedHeight = windowHeight / 100f * heightPercentage;
    private static final float expectedWidth = windowWidth / 100f * widthPercentage;

    private ElementPosition position;

    /*
    An example
    The image size is 654x152 pixels.
    Assuming the window size is FullHD 1920x1080,
    */

    private static void assertFloatTolerance(float expected, float actual) {
        assertEquals(expected, actual, 0.6f); // yes, this is a lot of tolerance TODO accuracy
    }

    @BeforeEach
    void beforeEach() {
        position = new ElementPosition();
    }

    @Test
    void getSafeWidthReturnsWidthWhenSet() {
        final float width = 33;
        position.setWidth(width);

        assertFloatTolerance(width, position.getSafeWidth(imageTextureWidth, imageTextureHeight));
    }

    @Test
    void getSafeWidthCalculatesWidthWhenNotSet() {
        position.setHeight(imageTextureHeight);
        assertFloatTolerance(imageTextureWidth, position.getSafeWidth(imageTextureWidth, imageTextureHeight));
    }

    @Test
    void getSafeHeightReturnsHeightWhenSet() {
        final float height = 6f;
        position.setHeight(height);

        assertEquals(height, position.getSafeHeight(imageTextureWidth, imageTextureHeight));
    }

    @Test
    void getSafeHeightCalculatesHeightWhenNotSet() {
        position.setWidth(imageTextureWidth);
        assertFloatTolerance(imageTextureHeight, position.getSafeHeight(imageTextureWidth, imageTextureHeight));
    }

    @Test
    void getRelativeWidthReturnsRelativeWidthWhenSet() {
        position.setWidth(widthPercentage);
        assertFloatTolerance(expectedWidth, position.getRelativeWidth(imageTextureWidth, imageTextureHeight, windowWidth, windowHeight));
    }

    @Test
    void getRelativeWidthCalculatesRelativeWidthWhenNotSet() {
        // Only height (as percentage) is set; width is not set.
        position.setHeight(heightPercentage);

        // Convert the height percentage to pixels.
        float pixelHeight = (heightPercentage * windowHeight / 100f);
        // Derive the pixel width from the texture aspect ratio.
        float expectedDerivedWidth = pixelHeight * ((float) imageTextureWidth / imageTextureHeight);

        int computedWidth = position.getRelativeWidth(imageTextureWidth, imageTextureHeight, windowWidth, windowHeight);

        assertFloatTolerance(expectedDerivedWidth, computedWidth);
    }

    @Test
    void getRelativeHeightReturnsRelativeHeightWhenSet() {
        position.setHeight(heightPercentage);
        assertFloatTolerance(expectedHeight, position.getRelativeHeight(imageTextureWidth, imageTextureHeight, windowWidth, windowHeight));
    }

    @Test
    void getRelativeHeightCalculatesRelativeHeightWhenNotSet() {
        // Only width (as percentage) is set; height is not set.
        position.setWidth(widthPercentage);

        // Convert the width percentage to pixels.
        float pixelWidth = (widthPercentage * windowWidth / 100f);
        // Derive the pixel height from the texture aspect ratio.
        float expectedDerivedHeight = pixelWidth * ((float) imageTextureHeight / imageTextureWidth);

        int computedHeight = position.getRelativeHeight(imageTextureWidth, imageTextureHeight, windowWidth, windowHeight);

        assertFloatTolerance(expectedDerivedHeight, computedHeight);
    }

    @Test
    void resolveBoundsReturnsCorrectBoundsForTopLeftAnchor() {
        position.setPositionAnchor(ElementAnchor.TOP_LEFT);
        position.setX(0);
        position.setY(0);
        position.setSizeUnit(ElementPosition.Unit.PERCENTAGE);
        position.setWidth(widthPercentage);
        position.setHeight(heightPercentage);

        final int[] bounds = position.resolveBounds(imageTextureWidth, imageTextureHeight, windowWidth, windowHeight);

        assertFloatTolerance(0, bounds[0]);
        assertFloatTolerance(0, bounds[1]);
        assertFloatTolerance(expectedWidth, bounds[2]);
        assertFloatTolerance(expectedHeight, bounds[3]);
    }

    @Test
    void resolveBoundsThrowsWhenNeitherSizeIsSet() {
        assertThrows(ConfigurationException.class, () -> position.resolveBounds(imageTextureWidth, imageTextureHeight, windowWidth, windowHeight));
    }

    @Test
    void resolveBoundsDoesNotThrowWhenOnlyWidthIsSet() {
        position.setWidth(widthPercentage);
        assertDoesNotThrow(() -> position.resolveBounds(imageTextureWidth, imageTextureHeight, windowWidth, windowHeight));
    }

    @Test
    void resolveBoundsDoesNotThrowWhenOnlyHeightIsSet() {
        position.setHeight(heightPercentage);
        assertDoesNotThrow(() -> position.resolveBounds(imageTextureWidth, imageTextureHeight, windowWidth, windowHeight));
    }

    @Test
    void resolveBoundsDoesNotThrowWHenBothWidthAndHeightAreSet() {
        position.setWidth(widthPercentage);
        position.setHeight(heightPercentage);
        assertDoesNotThrow(() -> position.resolveBounds(imageTextureWidth, imageTextureHeight, windowWidth, windowHeight));
    }
}