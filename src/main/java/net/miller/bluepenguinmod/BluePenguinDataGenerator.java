package net.miller.bluepenguinmod;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.miller.bluepenguinmod.datagen.ModBlockTagProvider;
import net.miller.bluepenguinmod.datagen.ModItemTagProvider;

public class BluePenguinDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
 		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		 pack.addProvider(ModBlockTagProvider::new);
	}
}
