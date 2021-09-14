package com.github.hashicraft.projector;

import com.github.hashicraft.projector.blocks.DisplayEntityRenderer;
import com.github.hashicraft.projector.events.DisplayClicked;
import com.github.hashicraft.projector.ui.DisplayGui;
import com.github.hashicraft.projector.ui.DisplayScreen;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;

public class ProjectorModClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    
    BlockEntityRendererRegistry.INSTANCE.register(ProjectorMod.DISPLAY_ENTITY, DisplayEntityRenderer::new);

    // DisplayClicked.EVENT.register((block, callback) -> {
    //   DisplayGui gui = new DisplayGui();
    //   DisplayScreen screen = new DisplayScreen(gui);
    //   MinecraftClient.getInstance().setScreen(screen);

    //   // set the default state
    //   gui.setup(block, callback);

    //   return ActionResult.PASS;
    // });
  }
}