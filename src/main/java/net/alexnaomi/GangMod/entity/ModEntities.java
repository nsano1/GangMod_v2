package net.alexnaomi.GangMod.entity;

import net.alexnaomi.GangMod.GangMod;
import net.alexnaomi.GangMod.entity.custom.FishyEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GangMod.MOD_ID);

    public static final RegistryObject<EntityType<FishyEntity>> FISHY =
            ENTITY_TYPES.register("fishy", () -> EntityType.Builder.of(FishyEntity::new, MobCategory.CREATURE)
                    .sized(1.5f, 1.5f).build("fishy"));

    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }
}
