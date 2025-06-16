package net.miller.bluepenguinmod.entity.client;

import net.miller.bluepenguinmod.BluePenguin;
import net.miller.bluepenguinmod.entity.custom.BluePenguinEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class BluePenguinRenderer extends MobEntityRenderer<BluePenguinEntity, BluePenguinModel<BluePenguinEntity>> {
    public BluePenguinRenderer(EntityRendererFactory.Context context) {
        super(context, new BluePenguinModel<>(context.getPart(BluePenguinModel.BLUEPENGUIN)), 0.75f);
    }

    @Override
    public Identifier getTexture(BluePenguinEntity entity) {
        return Identifier.of(BluePenguin.MOD_ID, "textures/entity/bluepenguin/bluepenguin-texture.png");
    }
    @Override
    public void render(BluePenguinEntity livingEntity, float f, float g, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumerProvider, int i) {
        if(livingEntity.isBaby()) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            matrixStack.scale(1f, 1f, 1f);
        }

        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
