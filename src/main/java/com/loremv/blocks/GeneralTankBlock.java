package com.loremv.blocks;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GeneralTankBlock extends BlockWithEntity {
    public GeneralTankBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GeneralTankBlockEntity(pos,state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        GeneralTankBlockEntity generalTankBlockEntity = (GeneralTankBlockEntity) world.getBlockEntity(pos);
        if(generalTankBlockEntity.addFluid(player.getStackInHand(hand)))
        {
            player.setStackInHand(hand,new ItemStack(Items.BUCKET));
            player.playSound(SoundEvents.ITEM_BUCKET_FILL,1,1);
            return ActionResult.PASS;
        }
        else
        {
            ItemStack ret = generalTankBlockEntity.removeFluid(player.getStackInHand(hand));
            if(ret!=null)
            {
                player.setStackInHand(hand,ret);
                player.playSound(SoundEvents.ITEM_BUCKET_EMPTY,1,1);
                return ActionResult.CONSUME;
            }
            else
            {
                if(hand==Hand.MAIN_HAND && !world.isClient)
                {
                    player.sendMessage(Text.empty().append(Text.of((generalTankBlockEntity.fluidStorage.amount/ FluidConstants.BUCKET)+" ")).append(Text.translatable(generalTankBlockEntity.fluidStorage.getResource().getFluid().getBucketItem().getTranslationKey())));
                }
            }
        }


        return ActionResult.CONSUME;
    }
}
