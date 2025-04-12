package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.element_anchor;

public enum ElementAnchor implements AnchorFunction {
    /**
     * Anchor is in the top left corner of the screen.
     * <pre><code>
     * +-------------+
     * |#            |
     * |             |
     * |             |
     * +-------------+
     * </code></pre>
     */
    TOP_LEFT(ElementAnchorFunctionHelper::topLeft),
    /**
     * Anchor is in the top right corner of the screen.
     * <pre><code>
     * +-------------+
     * |            #|
     * |             |
     * |             |
     * +-------------+
     * </code></pre>
     */
    TOP_RIGHT(ElementAnchorFunctionHelper::topRight),
    /**
     * Anchor is in the bottom left corner of the screen.
     * <pre><code>
     * +-------------+
     * |             |
     * |             |
     * |#            |
     * +-------------+
     * </code></pre>
     */
    BOTTOM_LEFT(ElementAnchorFunctionHelper::bottomLeft),
    /**
     * Anchor is in the bottom right corner of the screen.
     * <pre><code>
     * +-------------+
     * |             |
     * |             |
     * |            #|
     * +-------------+
     * </code></pre>
     */
    BOTTOM_RIGHT(ElementAnchorFunctionHelper::bottomRight),
    /**
     * Anchor is in the center of the screen.
     * <pre><code>
     * +-------------+
     * |             |
     * |      #      |
     * |             |
     * +-------------+
     * </code></pre>
     */
    CENTER(ElementAnchorFunctionHelper::center),
    /**
     * Anchor is in the top center of the screen.
     * <pre><code>
     * +-------------+
     * |      #      |
     * |             |
     * |             |
     * +-------------+
     * </code></pre>
     */
    TOP_CENTER(ElementAnchorFunctionHelper::topCenter),
    /**
     * Anchor is in the bottom center of the screen.
     * <pre><code>
     * +-------------+
     * |             |
     * |             |
     * |      #      |
     * +-------------+
     * </code></pre>
     */
    BOTTOM_CENTER(ElementAnchorFunctionHelper::bottomCenter),
    /**
     * Anchor is in the left center of the screen.
     * <pre><code>
     * +-------------+
     * |             |
     * |#            |
     * |             |
     * +-------------+
     * </code></pre>
     */
    LEFT_CENTER(ElementAnchorFunctionHelper::leftCenter),
    /**
     * Anchor is in the right center of the screen.
     * <pre><code>
     * +-------------+
     * |             |
     * |            #|
     * |             |
     * +-------------+
     * </code></pre>
     */
    RIGHT_CENTER(ElementAnchorFunctionHelper::rightCenter);

    private final AnchorFunction anchorFunction;

    ElementAnchor(AnchorFunction anchorFunction) {
        this.anchorFunction = anchorFunction;
    }


    @Override
    public int[] apply(int x, int y, int width, int height) {
        return anchorFunction.apply(x, y, width, height);
    }
}
