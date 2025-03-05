package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.reflection;

import net.neoforged.fml.earlydisplay.RenderElement;
import net.neoforged.fml.earlydisplay.SimpleBufferBuilder;

/**
 * {@link RenderElement.DisplayContext} and {@link SimpleBufferBuilder} merged together
 */
public record CSB(RenderElement.DisplayContext ctx, SimpleBufferBuilder buffer) {
}
