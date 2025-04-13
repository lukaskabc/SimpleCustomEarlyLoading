package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.element_anchor;

import com.google.gson.annotations.SerializedName;

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
    @SerializedName(value = "TOP_LEFT", alternate = {"top_left", "TOP-LEFT", "top-left"})
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
    @SerializedName(value = "TOP_RIGHT", alternate = {"top_right", "TOP-RIGHT", "top-right"})
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
    @SerializedName(value = "BOTTOM_LEFT", alternate = {"bottom_left", "BOTTOM-LEFT", "bottom-left"})
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
    @SerializedName(value = "BOTTOM_RIGHT", alternate = {"bottom_right", "BOTTOM-RIGHT", "bottom-right"})
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
    @SerializedName(value = "CENTER", alternate = {"center"})
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
    @SerializedName(value = "TOP_CENTER", alternate = {"top_center", "TOP-CENTER", "top-center"})
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
    @SerializedName(value = "BOTTOM_CENTER", alternate = {"bottom_center", "BOTTOM-CENTER", "bottom-center"})
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
    @SerializedName(value = "LEFT_CENTER", alternate = {"left_center", "LEFT-CENTER", "left-center"})
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
    @SerializedName(value = "RIGHT_CENTER", alternate = {"right_center", "RIGHT-CENTER", "right-center"})
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
