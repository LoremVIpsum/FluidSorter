package com.loremv;

import com.loremv.blocks.*;
import com.loremv.items.BlockItemWithLore;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class FluidSorter implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("fluidsorter");

	public static final ItemGroup TAB = FabricItemGroupBuilder.build(new Identifier("fluidsorter","tab"),()->new ItemStack(FluidSorter.FLUID_SORTER_BLOCK));


	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		registerBlocks();
		registerItems();
		registerFluidStorages();
		LOGGER.info("May your fluids be sorted and your console log blessed");


	}

	public static final FluidSorterBlock FLUID_SORTER_BLOCK = new FluidSorterBlock(AbstractBlock.Settings.of(Material.METAL));
	public static final InfiniteWaterTankBlock INFINITE_WATER_TANK_BLOCK = new InfiniteWaterTankBlock(AbstractBlock.Settings.of(Material.METAL));
	public static final GeneralTankBlock GENERAL_TANK_BLOCK = new GeneralTankBlock(AbstractBlock.Settings.of(Material.METAL));
	public void registerBlocks()
	{
		Registry.register(Registry.BLOCK,new Identifier("fluidsorter","cool_pipe_system"),FLUID_SORTER_BLOCK);
		Registry.register(Registry.BLOCK,new Identifier("fluidsorter","infinite_water_tank"),INFINITE_WATER_TANK_BLOCK);
		Registry.register(Registry.BLOCK,new Identifier("fluidsorter","general_tank"),GENERAL_TANK_BLOCK);
	}


	public void registerItems()
	{
		Registry.register(Registry.ITEM,new Identifier("fluidsorter","cool_pipe_system"),new BlockItem(FLUID_SORTER_BLOCK,new Item.Settings().group(TAB)));
		Registry.register(Registry.ITEM,new Identifier("fluidsorter","infinite_water_tank"),new BlockItem(INFINITE_WATER_TANK_BLOCK,new Item.Settings().group(TAB)));
		Registry.register(Registry.ITEM,new Identifier("fluidsorter","general_tank"),new BlockItemWithLore(GENERAL_TANK_BLOCK,new Item.Settings().group(TAB), Arrays.asList(Text.of("Holds 27 buckets of most fluids."))));

	}


	//block entities
	public static BlockEntityType<FluidSorterBlockEntity> FLUID_SORTER_BLOCK_ENTITY = Registry.register(
			Registry.BLOCK_ENTITY_TYPE,
			new Identifier("fluidsorter", "fluid_sorter_block_entity"),
			FabricBlockEntityTypeBuilder.create(FluidSorterBlockEntity::new, FLUID_SORTER_BLOCK).build()
	);
	public static BlockEntityType<InfiniteWaterTankBlockEntity> INFINITE_WATER_TANK_BLOCK_ENTITY = Registry.register(
			Registry.BLOCK_ENTITY_TYPE,
			new Identifier("fluidsorter", "infinite_water_tank_block_entity"),
			FabricBlockEntityTypeBuilder.create(InfiniteWaterTankBlockEntity::new, INFINITE_WATER_TANK_BLOCK).build()
	);

	public static BlockEntityType<GeneralTankBlockEntity> GENERAL_TANK_BLOCK_ENTITY = Registry.register(
			Registry.BLOCK_ENTITY_TYPE,
			new Identifier("fluidsorter", "general_tank_block_entity"),
			FabricBlockEntityTypeBuilder.create(GeneralTankBlockEntity::new, GENERAL_TANK_BLOCK).build()
	);

	public void registerFluidStorages()
	{
		FluidStorage.SIDED.registerForBlockEntity((myTank, direction) -> myTank.fluidStorage, INFINITE_WATER_TANK_BLOCK_ENTITY);
		FluidStorage.SIDED.registerForBlockEntity((myTank, direction) -> myTank.fluidStorage, GENERAL_TANK_BLOCK_ENTITY);
	}
}