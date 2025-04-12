package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ElementPositionTest {
    private ElementPosition position;

    @BeforeEach
    void beforeEach() {
        position = new ElementPosition();
    }

    @Test
    void getSafeWidthReturnsWidthWhenSet() {
        final float width = 5f;
        position.setWidth(width);

        assertEquals(width, position.getSafeWidth(3, 2));
    }

    @Test
    void getSafeWidthCalculatesWidthWhenNotSet() {
        final float height = 4f;
        final int elementWidth = 3;
        final int elementHeight = 2;
        position.setHeight(height);

        // the width is doubled since the desired height is twice the element height
        assertEquals(elementWidth * 2, position.getSafeWidth(elementWidth, elementHeight));
    }

    @Test
    void getSafeHeightReturnsHeightWhenSet() {
        final float height = 6f;
        position.setHeight(height);

        assertEquals(height, position.getSafeHeight(3, 2));
    }

    @Test
    void getSafeHeightCalculatesHeightWhenNotSet() {
        final float width = 1.5f;
        final int elementWidth = 3;
        final int elementHeight = 2;
        position.setWidth(width);

        // the height is halved since the desired width is half the element width
        assertEquals(elementHeight / 2, position.getSafeHeight(elementWidth, elementHeight));
    }

    @Test
    void getRelativeWidthReturnsPercentageWidthWhenWidthIsSet() {
        
    }


    @Test
    void getRelativeWidth() {
    }

    @Test
    void getRelativeHeight() {
    }

    @Test
    void resolveBounds() {
    }
}