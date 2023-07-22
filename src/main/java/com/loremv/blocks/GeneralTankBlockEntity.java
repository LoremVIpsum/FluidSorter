package com.loremv.blocks;

import com.loremv.FluidSorter;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.Optional;

public class GeneralTankBlockEntity extends BlockEntity {
    public GeneralTankBlockEntity(BlockPos pos, BlockState state) {
        super(FluidSorter.GENERAL_TANK_BLOCK_ENTITY, pos, state);
    }

    public final SingleVariantStorage<FluidVariant> fluidStorage = new SingleVariantStorage<>() {
        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant variant) {
            return FluidConstants.BUCKET * 27;
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
        nbt.put("fluidVariant", fluidStorage.variant.toNbt());
        nbt.putLong("amount", fluidStorage.amount);
        super.writeNbt(nbt);
    }

    public boolean addFluid(ItemStack stack)
    {
        Identifier sid = Registry.ITEM.getId(stack.getItem());
        Identifier fluidGuess = new Identifier(sid.getNamespace(),sid.getPath().replace("_bucket",""));
        Optional<Fluid> fluidMaybe = Registry.FLUID.getOrEmpty(fluidGuess);
        if(stack.getCount()==1 && fluidMaybe.isPresent() && (fluidStorage.isResourceBlank() || fluidStorage.getResource().isOf(fluidMaybe.get())))
        {
            fluidStorage.variant=FluidVariant.of(fluidMaybe.get());
            fluidStorage.amount+=FluidConstants.BUCKET;
            markDirty();
            return true;
        }
        return false;
    }
    public ItemStack removeFluid(ItemStack stack)
    {
        ItemStack out = null;
        if(fluidStorage.amount>=FluidConstants.BUCKET && stack.isOf(Items.BUCKET) && stack.getCount()==1)
        {
            fluidStorage.amount-=FluidConstants.BUCKET;
            out = new ItemStack(fluidStorage.variant.getFluid().getBucketItem());
            if(fluidStorage.amount==0)
            {
                fluidStorage.variant=FluidVariant.blank();
            }

        }
        return out;
    }
}
