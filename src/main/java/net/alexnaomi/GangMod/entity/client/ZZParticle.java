package net.alexnaomi.GangMod.entity.client;

import net.alexnaomi.GangMod.GangMod;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class ZZParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    // Changed to protected constructor
    protected ZZParticle(ClientLevel level, double x, double y, double z,
                         double xd, double yd, double zd, SpriteSet spriteSet) {
        super(level, x, y, z, xd, yd, zd);

        this.sprites = Objects.requireNonNull(spriteSet);
        this.lifetime = 60; // Longer lifetime
        this.quadSize = 0.5f; // Larger size
        this.gravity = -0.01f; // Stronger upward float
        this.setSpriteFromAge(this.sprites);

        // Debug logging
        GangMod.LOGGER.info("Creating ZZZ particle at {}, {}, {}", x, y, z);
    }

    @Override
    public void tick() {
        super.tick();
        // Safe sprite update
        if (!this.removed && this.sprites != null) {
            this.setSpriteFromAge(this.sprites);
        }
        this.yd += 0.01;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}