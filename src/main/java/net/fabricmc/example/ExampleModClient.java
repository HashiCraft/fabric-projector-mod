package net.fabricmc.example;

import net.fabricmc.example.blocks.PictureBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;

public class ExampleModClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    // Here we will put client-only registration code
    BlockEntityRendererRegistry.INSTANCE.register(ExampleMod.EXAMPLE_BLOCK_ENTITY, PictureBlockEntityRenderer::new);
  }
}