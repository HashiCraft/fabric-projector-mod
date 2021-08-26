package com.github.hashicraft.projector;

import com.github.hashicraft.projector.blocks.PictureBlockEntityRenderer;
import com.github.hashicraft.projector.events.PictureBlockClicked;
import com.github.hashicraft.projector.ui.PictureBlockGui;
import com.github.hashicraft.projector.ui.PictureBlockScreen;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;

public class ProjectorModClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    // Here we will put client-only registration code
    BlockEntityRendererRegistry.INSTANCE.register(ProjectorMod.PICTURE_BLOCK_ENTITY, PictureBlockEntityRenderer::new);

    PictureBlockClicked.EVENT.register((block) -> {
      PictureBlockGui gui = new PictureBlockGui();
      PictureBlockScreen screen = new PictureBlockScreen(gui);
      MinecraftClient.getInstance().setScreen(screen);

      // set the default state
      gui.setURLs(block);

      return ActionResult.PASS;
    });
  }
}