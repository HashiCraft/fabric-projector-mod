package com.github.hashicraft.projector;

import com.github.hashicraft.projector.blocks.PictureBlock;
import com.github.hashicraft.projector.blocks.PictureBlockEntity;
import com.github.hashicraft.projector.state.EntityServerState;


import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ProjectorMod implements ModInitializer {

  public static final Block PICTURE_BLOCK = new PictureBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
  public static BlockEntityType<PictureBlockEntity> PICTURE_BLOCK_ENTITY;

  @Override
  public void onInitialize() {
    System.out.println("Starting Projector mod version: 1.0.0");
    // This code runs as soon as Minecraft is in a mod-load-ready state.
    // However, some things (like resources) may still be uninitialized.
    // Proceed with mild caution.

    Registry.register(Registry.BLOCK, new Identifier("projector", "picture_block"), PICTURE_BLOCK);
    Registry.register(Registry.ITEM, new Identifier("projector", "picture_block"),
        new BlockItem(PICTURE_BLOCK, new FabricItemSettings().group(ItemGroup.MISC)));
    PICTURE_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "projector:picture_block_entity",
        FabricBlockEntityTypeBuilder.create(PictureBlockEntity::new, PICTURE_BLOCK).build(null));

    // register for sever events
    EntityServerState.RegisterStateUpdates();
  }
}