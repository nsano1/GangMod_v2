package net.alexnaomi.GangMod.entity.client;

import net.alexnaomi.GangMod.GangMod;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class ZZParticleProvider implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet sprites;

    public ZZParticleProvider(SpriteSet spriteSet) {
        // Validate during construction
        if (spriteSet == null) {
            throw new IllegalStateException("Null SpriteSet in provider constructor");
        }
        this.sprites = spriteSet;
    }

    @Override
    public @Nullable Particle createParticle(SimpleParticleType type, ClientLevel level,
                                             double x, double y, double z,
                                             double xSpeed, double ySpeed, double zSpeed) {
        try {
            return new ZZParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        } catch (Exception e) {
            GangMod.LOGGER.error("Failed to create particle", e);
            return null;
        }
    }
}