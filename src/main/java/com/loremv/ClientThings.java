package com.loremv;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.render.RenderLayer;

public class ClientThings implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(FluidSorter.INFINITE_WATER_TANK_BLOCK, RenderLayer.getTranslucent());
        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> 0x3495eb,FluidSorter.INFINITE_WATER_TANK_BLOCK);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> 0x3495eb,FluidSorter.INFINITE_WATER_TANK_BLOCK);

    }
}
