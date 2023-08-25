package com.github.hashicraft.projector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hashicraft.projector.blocks.Display;
import com.github.hashicraft.projector.blocks.DisplayEntity;
import com.github.hashicraft.projector.config.ServerConfig;
import com.github.hashicraft.projector.items.Remote;
import com.github.hashicraft.stateful.blocks.EntityServerState;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ProjectorMod implements ModInitializer {
  public static final String MODID = "projector";
  public static final Logger LOGGER = LoggerFactory.getLogger("projector");

  public static final Identifier REMOTE_ID = identifier("remote");
  public static final Identifier DISPLAY_ID = identifier("display");
  public static final Identifier PLACEHOLDER_TEXTURE = identifier("textures/block/display_placeholder.png");

  public static final Block DISPLAY = new Display(FabricBlockSettings.create().strength(4.0f).nonOpaque().solid());

  public static final Item REMOTE_ITEM = new Remote(new Item.Settings());
  public static final Item DISPLAY_ITEM = new BlockItem(DISPLAY, new Item.Settings());

  public static final RegistryKey<ItemGroup> ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP,
      new Identifier(MODID, "general"));

  public static Item REMOTE;
  public static BlockEntityType<DisplayEntity> DISPLAY_ENTITY;

  @Override
  public void onInitialize() {
    Registry.register(Registries.BLOCK, DISPLAY_ID, DISPLAY);
    Registry.register(Registries.ITEM, DISPLAY_ID, DISPLAY_ITEM);
    Registry.register(Registries.ITEM_GROUP, ITEM_GROUP, FabricItemGroup.builder()
        .icon(() -> new ItemStack(DISPLAY))
        .displayName(Text.translatable("projector.display"))
        .build());

    REMOTE = Registry.register(Registries.ITEM, REMOTE_ID, REMOTE_ITEM);
    DISPLAY_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, DISPLAY_ID,
        FabricBlockEntityTypeBuilder.create(DisplayEntity::new, DISPLAY).build());

    ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(content -> {
      content.add(REMOTE_ITEM);
      content.add(DISPLAY_ITEM);
    });

    // register for sever events
    EntityServerState.RegisterStateUpdates();

    // register the config class
    ServerConfig.Register("PROJECTOR_");
  }

  public static Identifier identifier(String id) {
    return new Identifier(MODID, id);
  }
}