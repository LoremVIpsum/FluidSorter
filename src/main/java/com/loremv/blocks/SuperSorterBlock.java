package com.loremv.blocks;

import com.loremv.FluidSorter;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SuperSorterBlock extends BlockWithEntity {
    public SuperSorterBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(4,4,4,12,12,12);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SuperSorterBlockEntity(pos,state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(!world.isClient)
        {
            Storage<FluidVariant> down = FluidStorage.SIDED.find(world,pos.down(), Direction.UP);
            Storage<FluidVariant> up = FluidStorage.SIDED.find(world,pos.up(), Direction.DOWN);
            Storage<FluidVariant> east = FluidStorage.SIDED.find(world,pos.east(), Direction.WEST);
            Storage<FluidVariant> west = FluidStorage.SIDED.find(world,pos.west(), Direction.EAST);
            Storage<FluidVariant> north = FluidStorage.SIDED.find(world,pos.north(), Direction.SOUTH);
            Storage<FluidVariant> south = FluidStorage.SIDED.find(world,pos.south(), Direction.NORTH);
            NbtCompound found = new NbtCompound();

            if(down!=null)
            {
                NbtList list = new NbtList();
                for(StorageView<FluidVariant> variant: down)
                {
                    if(!variant.isResourceBlank())
                    {
                        list.add(variant.getResource().toNbt());
                    }
                }
                found.put("down",list);
            }
            if(up!=null)
            {
                NbtList list = new NbtList();
                for(StorageView<FluidVariant> variant: up)
                {
                    if(!variant.isResourceBlank())
                    {
                        list.add(variant.getResource().toNbt());
                    }
                }
                found.put("up",list);
            }
            if(east!=null)
            {
                NbtList list = new NbtList();
                for(StorageView<FluidVariant> variant: east)
                {
                    if(!variant.isResourceBlank())
                    {
                        list.add(variant.getResource().toNbt());
                    }
                }
                found.put("east",list);
            }
            if(west!=null)
            {
                NbtList list = new NbtList();
                for(StorageView<FluidVariant> variant: west)
                {
                    if(!variant.isResourceBlank())
                    {
                        list.add(variant.getResource().toNbt());
                    }
                }
                found.put("west",list);
            }
            if(north!=null)
            {
                NbtList list = new NbtList();
                for(StorageView<FluidVariant> variant: north)
                {
                    if(!variant.isResourceBlank())
                    {
                        list.add(variant.getResource().toNbt());
                    }
                }
                found.put("north",list);
            }
            if(south!=null)
            {
                NbtList list = new NbtList();
                for(StorageView<FluidVariant> variant: south)
                {
                    if(!variant.isResourceBlank())
                    {
                        list.add(variant.getResource().toNbt());
                    }
                }
                found.put("south",list);
            }
            PacketByteBuf packet = PacketByteBufs.create();
            packet.writeNbt(found);
            packet.writeBlockPos(pos);
            ServerPlayNetworking.send((ServerPlayerEntity) player,FluidSorter.TANK_PACKET,packet);
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type,FluidSorter.SUPER_SORTER_BLOCK_ENTITY,SuperSorterBlockEntity::tick);
    }
}
