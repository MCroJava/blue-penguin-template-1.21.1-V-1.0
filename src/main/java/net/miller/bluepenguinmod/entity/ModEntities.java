package net.miller.bluepenguinmod.entity;

import net.miller.bluepenguinmod.BluePenguin;
import net.miller.bluepenguinmod.entity.custom.BluePenguinEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.core.pattern.AbstractStyleNameConverter;

public class ModEntities {

    public static final EntityType<BluePenguinEntity> BLUE_PENGUIN = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(BluePenguin.MOD_ID, "bluepenguin"),
            EntityType.Builder.create(BluePenguinEntity::new, SpawnGroup.CREATURE)
                    .dimensions(1f, 2.5f).build());

    public static void registerModEntities() {
        BluePenguin.LOGGER.info("Registering Mod Entity for " + BluePenguin.MOD_ID);
    }
}
