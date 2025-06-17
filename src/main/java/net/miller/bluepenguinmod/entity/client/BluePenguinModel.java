package net.miller.bluepenguinmod.entity.client;

import net.miller.bluepenguinmod.BluePenguin;
import net.miller.bluepenguinmod.entity.custom.BluePenguinEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class BluePenguinModel<T extends BluePenguinEntity> extends SinglePartEntityModel<T> {

    public static final EntityModelLayer BLUEPENGUIN = new EntityModelLayer(Identifier.of(net.miller.bluepenguinmod.BluePenguin.MOD_ID, "bluepenguin"), "main");

    private final ModelPart BluePenguin;
    private final ModelPart Body;
    private final ModelPart Head;
    private final ModelPart eyes;
    private final ModelPart Beak;
    private final ModelPart Top;
    private final ModelPart Bottom;
    private final ModelPart Flippers;
    private final ModelPart LeftFlipper;
    private final ModelPart RightFlipper;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final ModelPart Chest;
    private final ModelPart Feet;
    private final ModelPart Left;
    private final ModelPart right;
    public BluePenguinModel(ModelPart root) {
        this.BluePenguin = root.getChild("BluePenguin");
        this.Body = this.BluePenguin.getChild("Body");
        this.Head = this.Body.getChild("Head");
        this.eyes = this.Head.getChild("eyes");
        this.Beak = this.Head.getChild("Beak");
        this.Top = this.Beak.getChild("Top");
        this.Bottom = this.Beak.getChild("Bottom");
        this.Flippers = this.Body.getChild("Flippers");
        this.LeftFlipper = this.Flippers.getChild("LeftFlipper");
        this.RightFlipper = this.Flippers.getChild("RightFlipper");
        this.Torso = this.Body.getChild("Torso");
        this.Tail = this.Torso.getChild("Tail");
        this.Chest = this.Torso.getChild("Chest");
        this.Feet = this.Body.getChild("Feet");
        this.Left = this.Feet.getChild("Left");
        this.right = this.Feet.getChild("right");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData BluePenguin = modelPartData.addChild("BluePenguin", ModelPartBuilder.create(), ModelTransform.of(0.0F, 25.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        ModelPartData Body = BluePenguin.addChild("Body", ModelPartBuilder.create(), ModelTransform.pivot(-1.0F, -7.0F, 0.0F));

        ModelPartData Head = Body.addChild("Head", ModelPartBuilder.create().uv(30, 24).cuboid(-1.0F, -6.0F, -2.0F, 2.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(48, 9).cuboid(-2.0F, -5.0F, -3.0F, 4.0F, 1.0F, 6.0F, new Dilation(0.0F))
                .uv(28, 0).cuboid(-3.0F, -4.0F, -4.0F, 6.0F, 1.0F, 8.0F, new Dilation(0.0F))
                .uv(0, 17).cuboid(-3.0F, -3.0F, -4.0F, 7.0F, 4.0F, 8.0F, new Dilation(0.0F))
                .uv(36, 48).cuboid(-2.0F, -3.0F, -5.0F, 5.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(48, 48).cuboid(-2.0F, -3.0F, 4.0F, 5.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(36, 38).cuboid(-4.0F, -1.0F, -4.0F, 1.0F, 2.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, -4.0F, 0.0F));

        ModelPartData eyes = Head.addChild("eyes", ModelPartBuilder.create().uv(56, 0).cuboid(-0.3F, -1.0F, -3.8F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(56, 4).cuboid(-0.3F, -1.0F, 1.8F, 1.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.0F, -2.0F, 0.0F));

        ModelPartData Beak = Head.addChild("Beak", ModelPartBuilder.create(), ModelTransform.pivot(3.0F, 11.0F, 0.0F));

        ModelPartData Top = Beak.addChild("Top", ModelPartBuilder.create().uv(54, 37).cuboid(-3.0F, -1.0F, -1.0F, 3.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-6.0F, -12.0F, 0.0F));

        ModelPartData Bottom = Beak.addChild("Bottom", ModelPartBuilder.create().uv(54, 37).cuboid(-3.0F, 0.0F, -1.0F, 3.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-6.0F, -12.0F, 0.0F));

        ModelPartData Flippers = Body.addChild("Flippers", ModelPartBuilder.create(), ModelTransform.pivot(1.0F, 7.0F, 0.0F));

        ModelPartData LeftFlipper = Flippers.addChild("LeftFlipper", ModelPartBuilder.create().uv(32, 53).cuboid(-1.7F, 0.0F, -0.4F, 3.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, -11.0F, -5.0F));

        ModelPartData RightFlipper = Flippers.addChild("RightFlipper", ModelPartBuilder.create().uv(24, 52).cuboid(-1.7F, 0.0F, -0.6F, 3.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, -11.0F, 5.0F));

        ModelPartData Torso = Body.addChild("Torso", ModelPartBuilder.create().uv(18, 38).cuboid(-5.0F, -10.0F, -4.0F, 1.0F, 6.0F, 8.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-4.0F, -10.0F, -5.0F, 4.0F, 7.0F, 10.0F, new Dilation(0.0F))
                .uv(30, 9).cuboid(0.0F, -10.0F, -4.0F, 1.0F, 7.0F, 8.0F, new Dilation(0.0F))
                .uv(18, 29).cuboid(-3.0F, -3.0F, -4.0F, 4.0F, 1.0F, 8.0F, new Dilation(0.0F))
                .uv(0, 29).cuboid(1.0F, -12.0F, -4.0F, 1.0F, 10.0F, 8.0F, new Dilation(0.0F))
                .uv(42, 24).cuboid(2.0F, -9.0F, -3.0F, 1.0F, 7.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(1.0F, 7.0F, 0.0F));

        ModelPartData Tail = Torso.addChild("Tail", ModelPartBuilder.create().uv(48, 16).cuboid(0.0F, -2.0F, -2.0F, 1.0F, 4.0F, 4.0F, new Dilation(0.0F))
                .uv(14, 52).cuboid(1.0F, 0.0F, -2.0F, 1.0F, 2.0F, 4.0F, new Dilation(0.0F))
                .uv(56, 24).cuboid(2.0F, 1.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(3.0F, -4.0F, 0.0F));

        ModelPartData Chest = Torso.addChild("Chest", ModelPartBuilder.create().uv(0, 47).cuboid(-6.0F, -10.0F, -3.0F, 1.0F, 5.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData Feet = Body.addChild("Feet", ModelPartBuilder.create(), ModelTransform.pivot(1.0F, 7.0F, 0.0F));

        ModelPartData Left = Feet.addChild("Left", ModelPartBuilder.create().uv(54, 40).cuboid(-1.0F, -2.0F, -3.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, -0.6F));

        ModelPartData right = Feet.addChild("right", ModelPartBuilder.create().uv(54, 43).cuboid(-1.0F, -2.0F, 1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.6F));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void setAngles(BluePenguinEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        this.setHeadAngles(netHeadYaw, headPitch);
        if(entity.isTouchingWater()) {
            this.animateMovement(BluePenguinAnimations.ANIM_BLUEPENGUIN_SWIMMING, limbSwing, limbSwingAmount, 1.5f, 2.0f);
        } else {
            this.animateMovement(BluePenguinAnimations.ANIM_BLUEPENGUIN_WALKING, limbSwing, limbSwingAmount, 2f, 2.5f);
        }
        this.updateAnimation(entity.idleAnimationState, BluePenguinAnimations.ANIM_BLUEPENGUIN_IDLE, ageInTicks, 1f);
    }

    private void setHeadAngles(float headYaw, float headPitch) {
        headYaw = MathHelper.clamp(headYaw, -30.0F, 30.0F);
        headPitch = MathHelper.clamp(headPitch, -25.0F, 45.0F);

        this.Head.yaw = headYaw * 0.017453292F;
        this.Head.pitch = headPitch * 0.017453292F;
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        BluePenguin.render(matrices, vertexConsumer, light, overlay, color);
    }

    @Override
    public ModelPart getPart() {
        return BluePenguin;
    }
}
