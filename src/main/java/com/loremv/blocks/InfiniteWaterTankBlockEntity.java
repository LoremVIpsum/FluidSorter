package com.loremv.blocks;

import com.loremv.FluidSorter;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class InfiniteWaterTankBlockEntity extends BlockEntity {
    public InfiniteWaterTankBlockEntity(BlockPos pos, BlockState state) {
        super(FluidSorter.INFINITE_WATER_TANK_BLOCK_ENTITY, pos, state);
    }
    public final SingleVariantStorage<FluidVariant> fluidStorage = new SingleVariantStorage<>() {
        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant variant) {
            if(variant.isOf(Fluids.WATER))
            {
                return FluidConstants.BUCKET;
            }
            return FluidConstants.BUCKET;
        }

        @Override
        public long getAmount() {
            return FluidConstants.BUCKET*100;
        }

        @Override
        public FluidVariant getResource() {
            return FluidVariant.of(Fluids.WATER);
        }

        @Override
        public long getCapacity() {
            return FluidConstants.BUCKET*100;
        }

        @Override
        protected void onFinalCommit() {

            markDirty();
        }
    };



    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        fluidStorage.variant = FluidVariant.fromNbt(tag.getCompound("fluidVariant"));
        fluidStorage.amount = tag.getLong("amount");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.put("fluidVariant", FluidVariant.of(Fluids.WATER).toNbt());
        nbt.putLong("amount", FluidConstants.BUCKET*100);
        super.writeNbt(nbt);
    }
}
