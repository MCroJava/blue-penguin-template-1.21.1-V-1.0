package net.miller.bluepenguinmod.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.miller.bluepenguinmod.BluePenguin;
import net.miller.bluepenguinmod.entity.ModEntities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item BLUE_PENGUIN_SPAWN_EGG = registerItem("blue_penguin_spawn_egg",
            new SpawnEggItem(ModEntities.BLUE_PENGUIN, 0x547cab, 0xf0f0f0, new Item.Settings()));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(BluePenguin.MOD_ID, name), item);
    }

    public static void registerModItems() {
        BluePenguin.LOGGER.info("Registering Mod Items for " + BluePenguin.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
            entries.add(BLUE_PENGUIN_SPAWN_EGG);});
    }
}
