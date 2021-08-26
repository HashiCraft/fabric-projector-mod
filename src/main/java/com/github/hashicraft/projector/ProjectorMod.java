package com.github.hashicraft.projector;

import com.github.hashicraft.projector.blocks.PictureBlock;
import com.github.hashicraft.projector.blocks.PictureBlockEntity;
import com.github.hashicraft.projector.networking.Channels;
import com.github.hashicraft.projector.networking.PictureData;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class ProjectorMod implements ModInitializer {

  public static final Block PICTURE_BLOCK = new PictureBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
  public static BlockEntityType<PictureBlockEntity> PICTURE_BLOCK_ENTITY;

  @Override
  public void onInitialize() {

    System.out.println("Hello Fabric world!");
    // This code runs as soon as Minecraft is in a mod-load-ready state.
    // However, some things (like resources) may still be uninitialized.
    // Proceed with mild caution.

    Registry.register(Registry.BLOCK, new Identifier("projector", "picture_block"), PICTURE_BLOCK);
    Registry.register(Registry.ITEM, new Identifier("projector", "picture_block"),
        new BlockItem(PICTURE_BLOCK, new FabricItemSettings().group(ItemGroup.MISC)));
    PICTURE_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "projector:picture_block_entity",
        FabricBlockEntityTypeBuilder.create(PictureBlockEntity::new, PICTURE_BLOCK).build(null));

    // register for sever events
    ServerPlayNetworking.registerGlobalReceiver(Channels.UPDATE_PICTURES,
        (MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf,
            PacketSender responseSender) -> {

          System.out.println("got message from server");
          PictureData pictureData = PictureData.fromBytes(buf.readByteArray());

          server.execute(() -> {
            System.out.println("Pos" + pictureData.x + " " + pictureData.y + " " + pictureData.z);

            BlockPos pos = new BlockPos(pictureData.x, pictureData.y, pictureData.z);

            PictureBlockEntity be = (PictureBlockEntity) server.getOverworld().getBlockEntity(pos);

            be.clearPictures();

            for (String pics : pictureData.urls) {
              be.addPicture(pics);
            }

            be.setCurrentPicture(pictureData.currentImage);

            be.markDirty();
            be.sync();
          });

          // update the picture entity
        });

  }
}