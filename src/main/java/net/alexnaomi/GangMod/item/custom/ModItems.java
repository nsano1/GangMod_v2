package net.alexnaomi.GangMod.item.custom;

import net.alexnaomi.GangMod.GangMod;
import net.alexnaomi.GangMod.entity.ModEntities;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

public class ModItems {


    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GangMod.MOD_ID);


    public static final RegistryObject<Item> FISHY_SPAWN_EGG = ITEMS.register("fishy_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.FISHY, 0xFFFFFF, 0xAAAAAA, new Item.Properties()));

    //Add more here, then add in GangMod.java
    //TODO : MAKE RAMEN ENTITY
    public static final RegistryObject<Item> RAMEN_SPAWN_EGG = ITEMS.register("ramen_spawn_egg",
            () -> new Item(new Item.Properties()));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
