package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.elements;

import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.config.condition.DisplayCondition;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.CSB;
import cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection.RefRenderElement;
import net.neoforged.fml.earlydisplay.RenderElement;

import java.util.List;
import java.util.function.Supplier;

/**
 * Supplier providing new instance of RenderElement.
 */
public abstract class ElementSupplier implements Supplier<RenderElement> {
    private final List<DisplayCondition> displayConditions;

    public ElementSupplier(List<DisplayCondition> displayConditions) {
        this.displayConditions = displayConditions;
    }

    /**
     * Called on each frame to render the element.
     *
     * @param csb   FrameBuffer with context
     * @param frame frame number
     */
    public abstract void render(CSB csb, int frame);

    /**
     * Called before constructing the RenderElement.
     */
    public void initialize() {

    }

    protected void renderer(CSB csb, int frame) {
        if (shouldRender()) {
            render(csb, frame);
        }
    }

    protected boolean shouldRender() {
        return displayConditions.isEmpty() || displayConditions.stream().anyMatch(DisplayCondition::shouldDisplay);
    }

    /**
     * Returns a RenderElement rendering using {@link #render(CSB, int)}
     * <p>
     * Calls {@link #initialize()}.
     *
     * @return a RenderElement for rendering the element
     */
    @Override
    public RenderElement get() {
        initialize();
        return RefRenderElement.constructor(this::renderer);
    }
}
