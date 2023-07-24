package com.loremv.screens;

import com.loremv.FluidSorter;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.impl.client.item.group.FabricCreativeGuiComponents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import java.awt.*;
import java.awt.image.renderable.RenderContext;
import java.util.HashMap;
import java.util.List;

public class SuperSorterScreen extends Screen {
    private NbtCompound found;
    private BlockPos loc;
    public SuperSorterScreen(Text title, NbtCompound foundIn,BlockPos pos) {
        super(title);
        found=foundIn;
        loc=pos;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void init() {
        super.init();
        addDrawableChild(new ButtonWidget((width/2)-120,166,100,20,Text.of("Save"),a->
        {
            PacketByteBuf packet = PacketByteBufs.create();
            packet.writeNbt(orders);
            packet.writeBlockPos(loc);
            ClientPlayNetworking.send(FluidSorter.TANK_RETURN_PACKET,packet);

        }));
        if(found.contains("down"))
        {
            NbtList s = (NbtList) found.get("down");
            for (int i = 0; i < s.size(); i++) {
                int finalI = i;
                addDrawableChild(new ButtonWidget((width/2)-82+(i*20),46,20,20,Text.empty(),a->clickButton("down", finalI)));
            }
        }
        if(found.contains("up"))
        {
            NbtList s = (NbtList) found.get("up");
            for (int i = 0; i < s.size(); i++) {
                int finalI = i;
                addDrawableChild(new ButtonWidget((width/2)-82+(i*20),66,20,20,Text.empty(),a->clickButton("up", finalI)));
            }
        }
        if(found.contains("east"))
        {
            NbtList s = (NbtList) found.get("east");
            for (int i = 0; i < s.size(); i++) {
                int finalI = i;
                addDrawableChild(new ButtonWidget((width/2)-82+(i*20),86,20,20,Text.empty(),a->clickButton("east", finalI)));
            }
        }
        if(found.contains("west"))
        {
            NbtList s = (NbtList) found.get("west");
            for (int i = 0; i < s.size(); i++) {
                int finalI = i;
                addDrawableChild(new ButtonWidget((width/2)-82+(i*20),106,20,20,Text.empty(),a->clickButton("west", finalI)));
            }
        }
        if(found.contains("north"))
        {
            NbtList s = (NbtList) found.get("north");
            for (int i = 0; i < s.size(); i++) {
                int finalI = i;
                addDrawableChild(new ButtonWidget((width/2)-82+(i*20),126,20,20,Text.empty(),a->clickButton("north", finalI)));
            }
        }
        if(found.contains("south"))
        {
            NbtList s = (NbtList) found.get("south");
            for (int i = 0; i < s.size(); i++) {
                int finalI = i;
                addDrawableChild(new ButtonWidget((width/2)-82+(i*20),146,20,20,Text.empty(), a->clickButton("south", finalI)));
            }
        }
    }
    private NbtCompound orders = new NbtCompound();
    private void clickButton(String direction, int i)
    {
        String mash = direction+","+i;
        if(orders.contains(mash))
        {
            orders.putInt(mash,orders.getInt(mash)+1);

        }
        if(!orders.contains(mash) || orders.getInt(mash)>5)
        {
            orders.putInt(mash,0);
        }
    }
    private void renderDirectionUsingOrder(MatrixStack stack, String direction, int i)
    {
        String mash = direction+","+i;

        int yOffset = 0;
        Formatting formatting = Formatting.BLUE;
        switch (direction)
        {
            case "down" -> yOffset = 46;
            case "up" -> yOffset = 66;
            case "east" -> yOffset = 86;
            case "west" -> yOffset = 106;
            case "north" -> yOffset = 126;
            case "south" -> yOffset = 146;
        }


        if(orders.contains(mash))
        {
            switch (orders.getInt(mash))
            {
                case 0 -> formatting=Formatting.GOLD;
                case 1 -> formatting=Formatting.RED;
                case 2 -> formatting=Formatting.GREEN;
                case 3 -> formatting=Formatting.LIGHT_PURPLE;
                case 4 -> formatting=Formatting.DARK_RED;
                case 5 -> formatting=Formatting.BLUE;
            }
            textRenderer.draw(stack,Text.empty().append("#").formatted(formatting),(width/2)-82+(i*20),yOffset, Formatting.WHITE.getColorValue());
        }
    }
    private static final Identifier bg = new Identifier("minecraft","textures/gui/demo_background.png");

    public void renderBackground(MatrixStack matrices) {
        super.renderBackground(matrices);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, bg);
        int i = (this.width - 248) / 2;
        int j = (this.height - 166) / 2;
        this.drawTexture(matrices, i, j, 0, 0, 248, 166);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);

        textRenderer.drawWithShadow(matrices,"DOWN",(width/2)-120,52, Formatting.GOLD.getColorValue());
        textRenderer.drawWithShadow(matrices,"UP",(width/2)-120,72,Formatting.RED.getColorValue());
        textRenderer.drawWithShadow(matrices,"EAST",(width/2)-120,92,Formatting.GREEN.getColorValue());
        textRenderer.drawWithShadow(matrices,"WEST",(width/2)-120,112,Formatting.LIGHT_PURPLE.getColorValue());
        textRenderer.drawWithShadow(matrices,"NORTH",(width/2)-120,132,Formatting.DARK_RED.getColorValue());
        textRenderer.drawWithShadow(matrices,"SOUTH",(width/2)-120,152,Formatting.BLUE.getColorValue());


        int c = 0;
        if(found.contains("down"))
        {
            NbtList nbtFluids = (NbtList) found.get("down");
            for(NbtElement element: nbtFluids)
            {
                FluidVariant fluidVariant = FluidVariant.fromNbt((NbtCompound) element);
                itemRenderer.renderGuiItemIcon(fluidVariant.getFluid().getBucketItem().getDefaultStack(),(width/2)-80+(c*20),48);
                renderDirectionUsingOrder(matrices,"down",c);
                c++;
            }

        }
        if(found.contains("up"))
        {
            c=0;
            NbtList nbtFluids = (NbtList) found.get("up");
            for(NbtElement element: nbtFluids)
            {
                FluidVariant fluidVariant = FluidVariant.fromNbt((NbtCompound) element);
                itemRenderer.renderGuiItemIcon(fluidVariant.getFluid().getBucketItem().getDefaultStack(),(width/2)-80+(c*20),68);
                renderDirectionUsingOrder(matrices,"up",c);
                c++;
            }
        }
        if(found.contains("east"))
        {
            c=0;
            NbtList nbtFluids = (NbtList) found.get("east");
            for(NbtElement element: nbtFluids)
            {
                FluidVariant fluidVariant = FluidVariant.fromNbt((NbtCompound) element);
                itemRenderer.renderGuiItemIcon(fluidVariant.getFluid().getBucketItem().getDefaultStack(),(width/2)-80+(c*20),88);
                renderDirectionUsingOrder(matrices,"east",c);
                c++;
            }
        }
        if(found.contains("west"))
        {
            c=0;
            NbtList nbtFluids = (NbtList) found.get("west");
            for(NbtElement element: nbtFluids)
            {
                FluidVariant fluidVariant = FluidVariant.fromNbt((NbtCompound) element);
                itemRenderer.renderGuiItemIcon(fluidVariant.getFluid().getBucketItem().getDefaultStack(),(width/2)-80+(c*20),108);
                renderDirectionUsingOrder(matrices,"west",c);
                c++;
            }
        }
        if(found.contains("north"))
        {
            c=0;
            NbtList nbtFluids = (NbtList) found.get("north");
            for(NbtElement element: nbtFluids)
            {
                FluidVariant fluidVariant = FluidVariant.fromNbt((NbtCompound) element);
                itemRenderer.renderGuiItemIcon(fluidVariant.getFluid().getBucketItem().getDefaultStack(),(width/2)-80+(c*20),128);
                renderDirectionUsingOrder(matrices,"north",c);
                c++;
            }
        }
        if(found.contains("south"))
        {
            c=0;
            NbtList nbtFluids = (NbtList) found.get("south");
            for(NbtElement element: nbtFluids)
            {
                FluidVariant fluidVariant = FluidVariant.fromNbt((NbtCompound) element);
                itemRenderer.renderGuiItemIcon(fluidVariant.getFluid().getBucketItem().getDefaultStack(),(width/2)-80+(c*20),148);
                renderDirectionUsingOrder(matrices,"south",c);
                c++;
            }
        }

    }
}
