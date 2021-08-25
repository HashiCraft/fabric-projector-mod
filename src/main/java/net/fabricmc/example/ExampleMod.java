package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.example.blocks.PictureBlock;
import net.fabricmc.example.blocks.PictureBlockEntity;
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

    System.out.println("Hello Fabric world!");
  }
}
