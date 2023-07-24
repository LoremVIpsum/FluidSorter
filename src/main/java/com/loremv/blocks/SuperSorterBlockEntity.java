package com.loremv.blocks;

import com.loremv.FluidSorter;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import static net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants.BUCKET;

public class SuperSorterBlockEntity extends BlockEntity {
    NbtList paths = new NbtList();
    public SuperSorterBlockEntity(BlockPos pos, BlockState state) {
        super(FluidSorter.SUPER_SORTER_BLOCK_ENTITY, pos, state);
    }

    public void addPath(Direction from, Direction to, int fromFluidIndex)
    {
        NbtCompound path = new NbtCompound();
        path.putInt("from",from.getId());
        path.putInt("to",to.getId());
        paths.add(path);
        markDirty();
    }
    public void clearPaths()
    {
        paths=new NbtList();
        markDirty();
    }

    public NbtList getPaths() {
        return paths;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.put("paths",paths);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        paths= (NbtList) nbt.get("paths");
    }

    public static void tick(World world, BlockPos pos, BlockState state, SuperSorterBlockEntity be)
    {
        NbtList paths = be.getPaths();
        if(paths!=null)
        {
            for(NbtElement path: paths)
            {
                NbtCompound compound = (NbtCompound) path;
                Direction to = Direction.byId(compound.getInt("to"));
                Direction from = Direction.byId(compound.getInt("from"));
                int index = compound.getInt("fluidIndex");
                //System.out.println(from+" going to "+to+" with "+index);

                Storage<FluidVariant> fromTank = FluidStorage.SIDED.find(world,pos.offset(from), from.getOpposite());
                Storage<FluidVariant> toTank = FluidStorage.SIDED.find(world,pos.offset(to), to.getOpposite());

                List<FluidVariant> variants = new ArrayList<>();
                for (StorageView<FluidVariant> fluidVariant : fromTank) {
                    variants.add(fluidVariant.getResource());

                }

                if(fromTank!=null && toTank!=null)
                {
                    try (Transaction transaction = Transaction.openOuter()) {
                        if (fromTank.extract(variants.get(index), BUCKET, transaction) == BUCKET
                                && toTank.insert(variants.get(index), BUCKET, transaction) == BUCKET) {
                            transaction.commit();
                        }
                    }
                    catch (Exception ignored){}
                }
            }
        }


    }
}
