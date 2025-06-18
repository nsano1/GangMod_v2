package net.alexnaomi.GangMod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.alexnaomi.GangMod.GangMod;
import net.alexnaomi.GangMod.entity.custom.FishyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Pose;

public class FishyRenderer extends MobRenderer<FishyEntity, FishyModel<FishyEntity>> {
    private final RandomSource random = RandomSource.create();

    public FishyRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new FishyModel<>(pContext.bakeLayer(FishyModel.LAYER_LOCATION)), 0.25f);
    }

    @Override
    public ResourceLocation getTextureLocation(FishyEntity pEntity) {
        return ResourceLocation.fromNamespaceAndPath(GangMod.MOD_ID, "textures/entity/fishy/fishy_texture.png");
    }

    @Override
    public void render(FishyEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

        if (entity.getPose() == Pose.SLEEPING && Minecraft.getInstance().level != null) {
            // Only spawn particles on client side
            if (entity.tickCount % 5 == 0) { // Every 5 ticks (4 times per second)
                spawnSleepParticles(entity);
            }
        }
    }

    private void spawnSleepParticles(FishyEntity entity) {
        // Create 3 Z's in a vertical column
        for (int i = 0; i < 3; i++) {
            double x = entity.getX() + (random.nextDouble() - 0.5) * 0.3;
            double y = entity.getY() + 0.5 + i * 0.15;
            double z = entity.getZ() + (random.nextDouble() - 0.5) * 0.3;

            Minecraft.getInstance().level.addParticle(
                    GangMod.ZZZ_PARTICLE.get(), // Use your custom particle
                    x, y, z,
                    0, 0.05, 0  // Slight upward motion
            );
        }
    }
}
