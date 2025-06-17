package net.miller.bluepenguinmod.world.gen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.miller.bluepenguinmod.entity.ModEntities;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;

public class ModEntitySpawns {
    public static void addSpawns() {
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.PLAINS, BiomeKeys.BEACH, BiomeKeys.STONY_SHORE, BiomeKeys.SNOWY_BEACH),
                SpawnGroup.CREATURE, ModEntities.BLUE_PENGUIN, 30, 1, 4);

        SpawnRestriction.register(ModEntities.BLUE_PENGUIN, SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.WORLD_SURFACE, AnimalEntity::isValidNaturalSpawn);

    }
}
