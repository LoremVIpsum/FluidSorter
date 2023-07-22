package com.loremv.blocks;

import com.loremv.FluidSorter;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import static net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants.BUCKET;

public class FluidSorterBlockEntity extends BlockEntity {
    public FluidSorterBlockEntity(BlockPos pos, BlockState state) {
        super(FluidSorter.FLUID_SORTER_BLOCK_ENTITY, pos, state);
    }
    public static void tick(World world, BlockPos pos, BlockState state, FluidSorterBlockEntity be) {

        if(world.getTimeOfDay() % 10L == 0L)
        {
            Storage<FluidVariant> hatch = FluidStorage.SIDED.find(world,pos.down(), Direction.UP);
            Storage<FluidVariant> in = FluidStorage.SIDED.find(world,pos.up(), Direction.DOWN);
            Storage<FluidVariant> out1 = FluidStorage.SIDED.find(world,pos.east(), Direction.WEST);
            Storage<FluidVariant> out2 = FluidStorage.SIDED.find(world,pos.west(), Direction.EAST);

            List<StorageView<FluidVariant>> variants = new ArrayList<>();
            if(hatch!=null)
            {

                for (StorageView<FluidVariant> variant : hatch) {
                    if(!variant.isResourceBlank())
                    {
                        variants.add(variant);
                    }

                }
            }

            if(in!=null)
            {
                FluidVariant fin = in.iterator().next().getResource();
                if(fin!=null && !fin.isBlank())
                {
                    try (Transaction transaction = Transaction.openOuter()) {
                        if (in.extract(fin, BUCKET, transaction) == BUCKET && hatch.insert(fin, BUCKET, transaction) == BUCKET) {
                            transaction.commit();
                        }
                    }
                    catch (Exception ignored){}
                }

            }

            if(out1!=null && variants.size()>1 && !variants.get(1).isResourceBlank())
            {
                try (Transaction transaction = Transaction.openOuter()) {
                    if (hatch.extract(variants.get(1).getResource(), BUCKET, transaction) == BUCKET
                            && out1.insert(variants.get(1).getResource(), BUCKET, transaction) == BUCKET) {
                        transaction.commit();
                    }
                }
                catch (Exception ignored){}
            }

            if(out2!=null && variants.size()>2 && !variants.get(2).isResourceBlank())
            {
                try (Transaction transaction = Transaction.openOuter()) {
                    if (hatch.extract(variants.get(2).getResource(), BUCKET, transaction) == BUCKET
                            && out2.insert(variants.get(2).getResource(), BUCKET, transaction) == BUCKET) {
                        transaction.commit();
                    }
                }
                catch (Exception ignored){}
            }
        }
    }
}
