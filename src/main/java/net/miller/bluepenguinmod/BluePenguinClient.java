package net.miller.bluepenguinmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.miller.bluepenguinmod.entity.ModEntities;
import net.miller.bluepenguinmod.entity.client.BluePenguinModel;
import net.miller.bluepenguinmod.entity.client.BluePenguinRenderer;
import net.miller.bluepenguinmod.item.ModItems;
import net.minecraft.client.render.entity.model.EntityModelLayer;

public class BluePenguinClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModItems.registerModItems();
        EntityModelLayerRegistry.registerModelLayer(BluePenguinModel.BLUEPENGUIN, BluePenguinModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.BLUE_PENGUIN, BluePenguinRenderer::new);
    }
}
