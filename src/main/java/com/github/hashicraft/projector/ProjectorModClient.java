package com.github.hashicraft.projector;

import com.github.hashicraft.projector.blocks.PictureBlockEntityRenderer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;

public class ProjectorModClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    // Here we will put client-only registration code
    BlockEntityRendererRegistry.INSTANCE.register(ProjectorMod.PICTURE_BLOCK_ENTITY, PictureBlockEntityRenderer::new);
  }
}