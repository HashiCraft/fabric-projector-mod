package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.example.blocks.PictureBlock;
import net.fabricmc.example.blocks.PictureBlockEntity;
import net.fabricmc.example.networking.Channels;
import net.fabricmc.example.networking.PictureData;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayChannelHandler;
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

import net.minecraft.block.entity.BlockEntity;

public class ExampleMod implements ModInitializer {

  public static final Block EXAMPLE_BLOCK = new PictureBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
  public static BlockEntityType<PictureBlockEntity> EXAMPLE_BLOCK_ENTITY;

  @Override
  public void onInitialize() {
    // This code runs as soon as Minecraft is in a mod-load-ready state.
    // However, some things (like resources) may still be uninitialized.
    // Proceed with mild caution.

    Registry.register(Registry.BLOCK, new Identifier("projector", "example_block"), EXAMPLE_BLOCK);
    Registry.register(Registry.ITEM, new Identifier("projector", "example_block"),
        new BlockItem(EXAMPLE_BLOCK, new FabricItemSettings().group(ItemGroup.MISC)));
    EXAMPLE_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "projector:demo_block_entity",
        FabricBlockEntityTypeBuilder.create(PictureBlockEntity::new, EXAMPLE_BLOCK).build(null));

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

            be.markDirty();
            be.sync();
          });

          // update the picture entity
        });

    System.out.println("Hello Fabric world!");
  }
}
