package net.alexnaomi.GangMod.entity.event;

import net.alexnaomi.GangMod.GangMod;
import net.alexnaomi.GangMod.entity.ModEntities;
import net.alexnaomi.GangMod.entity.client.FishyModel;
import net.alexnaomi.GangMod.entity.client.FishyRenderer;
import net.alexnaomi.GangMod.entity.client.ZZParticleProvider;
import net.alexnaomi.GangMod.entity.custom.FishyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = GangMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(FishyModel.LAYER_LOCATION, FishyModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event){
        event.put(ModEntities.FISHY.get(), FishyEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Entity renderers
        EntityRenderers.register(ModEntities.FISHY.get(), FishyRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        // Safe registration with null check
        SimpleParticleType particle = GangMod.ZZZ_PARTICLE.get();
        if (particle != null) {
            event.registerSpriteSet(particle, ZZParticleProvider::new);
        } else {
            GangMod.LOGGER.error("ZZZ Particle type not registered!");
        }
    }
}
