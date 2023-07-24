package com.loremv;

import com.loremv.blocks.*;
import com.loremv.items.BlockItemWithLore;
import com.loremv.screens.SuperSorterScreen;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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


	public static final Identifier TANK_PACKET = new Identifier("fluidsorter","tank_packet");
	public static final Identifier TANK_RETURN_PACKET = new Identifier("fluidsorter","tank_return_packet");
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		registerBlocks();
		registerItems();
		registerFluidStorages();
		LOGGER.info("May your fluids be sorted and your console log blessed");

		ClientPlayNetworking.registerGlobalReceiver(TANK_PACKET,((client, handler, buf, responseSender) ->
		{
			NbtCompound compound = buf.readNbt();
			BlockPos pos = buf.readBlockPos();
			client.execute(()->
			{
				client.setScreen(new SuperSorterScreen(Text.of("Super Sorter"),compound,pos));
			});
		}));
		ServerPlayNetworking.registerGlobalReceiver(TANK_RETURN_PACKET,((server, player, handler, buf, responseSender) ->
		{
			NbtCompound compound = buf.readNbt();
			BlockPos pos = buf.readBlockPos();
			server.execute(()->
			{
				SuperSorterBlockEntity superSorterBlockEntity = (SuperSorterBlockEntity) player.world.getBlockEntity(pos);
				superSorterBlockEntity.clearPaths();

				for(String key: compound.getKeys())
				{
					//System.out.println(key +"->"+compound.getInt(key));
					String[] split = key.split(",");
					Direction direction = Direction.byName(split[0]);
					int index = Integer.parseInt(split[1]);
					Direction toDir = Direction.UP;

					switch (compound.getInt(key))
					{
						case 0 -> toDir=Direction.DOWN;
						case 1 -> toDir=Direction.UP;
						case 2 -> toDir=Direction.EAST;
						case 3 -> toDir=Direction.WEST;
						case 4 -> toDir=Direction.NORTH;
						case 5 -> toDir=Direction.SOUTH;
					}
					superSorterBlockEntity.addPath(direction,toDir,index);
				}

			});
		}));
	}

	public static final FluidSorterBlock FLUID_SORTER_BLOCK = new FluidSorterBlock(AbstractBlock.Settings.of(Material.METAL));
	public static final InfiniteWaterTankBlock INFINITE_WATER_TANK_BLOCK = new InfiniteWaterTankBlock(AbstractBlock.Settings.of(Material.METAL));
	public static final GeneralTankBlock GENERAL_TANK_BLOCK = new GeneralTankBlock(AbstractBlock.Settings.of(Material.METAL));
	public static final SuperSorterBlock SUPER_SORTER_BLOCK = new SuperSorterBlock(AbstractBlock.Settings.of(Material.WOOD));
	public void registerBlocks()
	{
		Registry.register(Registry.BLOCK,new Identifier("fluidsorter","cool_pipe_system"),FLUID_SORTER_BLOCK);
		Registry.register(Registry.BLOCK,new Identifier("fluidsorter","infinite_water_tank"),INFINITE_WATER_TANK_BLOCK);
		Registry.register(Registry.BLOCK,new Identifier("fluidsorter","general_tank"),GENERAL_TANK_BLOCK);
		Registry.register(Registry.BLOCK,new Identifier("fluidsorter","super_sorter"),SUPER_SORTER_BLOCK);
	}


	public void registerItems()
	{
		Registry.register(Registry.ITEM,new Identifier("fluidsorter","cool_pipe_system"),new BlockItem(FLUID_SORTER_BLOCK,new Item.Settings().group(TAB)));
		Registry.register(Registry.ITEM,new Identifier("fluidsorter","infinite_water_tank"),new BlockItem(INFINITE_WATER_TANK_BLOCK,new Item.Settings().group(TAB)));
		Registry.register(Registry.ITEM,new Identifier("fluidsorter","general_tank"),new BlockItemWithLore(GENERAL_TANK_BLOCK,new Item.Settings().group(TAB), Arrays.asList(Text.of("Holds 27 buckets of most fluids."))));
		Registry.register(Registry.ITEM,new Identifier("fluidsorter","super_sorter"),new BlockItemWithLore(SUPER_SORTER_BLOCK,new Item.Settings().group(TAB), Arrays.asList(Text.of("Can move any fluid in any direction"))));

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

	public static BlockEntityType<SuperSorterBlockEntity> SUPER_SORTER_BLOCK_ENTITY = Registry.register(
			Registry.BLOCK_ENTITY_TYPE,
			new Identifier("fluidsorter", "super_sorter_block_entity"),
			FabricBlockEntityTypeBuilder.create(SuperSorterBlockEntity::new, SUPER_SORTER_BLOCK).build()
	);

	public void registerFluidStorages()
	{
		FluidStorage.SIDED.registerForBlockEntity((myTank, direction) -> myTank.fluidStorage, INFINITE_WATER_TANK_BLOCK_ENTITY);
		FluidStorage.SIDED.registerForBlockEntity((myTank, direction) -> myTank.fluidStorage, GENERAL_TANK_BLOCK_ENTITY);
	}
}