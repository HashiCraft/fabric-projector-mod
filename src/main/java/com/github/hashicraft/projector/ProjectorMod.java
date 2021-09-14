package com.github.hashicraft.projector;

import com.github.hashicraft.projector.blocks.Display;
import com.github.hashicraft.projector.blocks.DisplayEntity;
import com.github.hashicraft.projector.items.Remote;
import com.github.hashicraft.stateful.blocks.EntityServerState;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ProjectorMod implements ModInitializer {
  public static final String MODID = "projector";

  public static final Identifier REMOTE_ID = identifier("remote");
	public static Item REMOTE;

  public static final Identifier DISPLAY_ID = identifier("display");
  public static final Identifier PLACEHOLDER_TEXTURE = identifier("textures/block/display_placeholder.png");
  public static final Block DISPLAY = new Display(FabricBlockSettings.of(Material.METAL).strength(4.0f).nonOpaque());
  public static BlockEntityType<DisplayEntity> DISPLAY_ENTITY;

  public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(identifier("general"), () -> new ItemStack(DISPLAY));

  @Override
  public void onInitialize() {
    REMOTE = Registry.register(Registry.ITEM, REMOTE_ID, new Remote(new Item.Settings().group(ITEM_GROUP)));

    Registry.register(Registry.BLOCK, DISPLAY_ID, DISPLAY);
    Registry.register(Registry.ITEM, DISPLAY_ID, new BlockItem(DISPLAY, new FabricItemSettings().group(ITEM_GROUP)));
    DISPLAY_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, DISPLAY_ID, FabricBlockEntityTypeBuilder.create(DisplayEntity::new, DISPLAY).build(null));

    // register for sever events
    EntityServerState.RegisterStateUpdates();
  }

  public static Identifier identifier(String id) {
    return new Identifier(MODID, id);
  }
}