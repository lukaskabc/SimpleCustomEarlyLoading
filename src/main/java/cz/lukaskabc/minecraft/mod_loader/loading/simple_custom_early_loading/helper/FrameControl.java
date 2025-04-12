package cz.lukaskabc.minecraft.mod_loader.loading.simple_custom_early_loading.helper;

import net.ellerton.japng.chunks.PngFrameControl;

public record FrameControl(int width, int height, int xOffset, int yOffset) {
    public FrameControl(PngFrameControl pngFrameControl) {
        this(pngFrameControl.width,
                pngFrameControl.height,
                pngFrameControl.xOffset,
                pngFrameControl.yOffset);
    }
}
