package net.miller.bluepenguinmod;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.miller.bluepenguinmod.entity.ModEntities;
import net.miller.bluepenguinmod.entity.custom.BluePenguinEntity;
import net.miller.bluepenguinmod.world.gen.ModEntitySpawns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BluePenguin implements ModInitializer {
	public static final String MOD_ID = "bluepenguinmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModEntities.registerModEntities();
		FabricDefaultAttributeRegistry.register(ModEntities.BLUE_PENGUIN, BluePenguinEntity.createAttributes());
		ModEntitySpawns.addSpawns();
	}
}