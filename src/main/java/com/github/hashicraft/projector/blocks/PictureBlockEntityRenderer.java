package com.github.hashicraft.projector.blocks;

import com.github.hashicraft.projector.downloader.FileDownloader;
import com.github.hashicraft.projector.downloader.FileDownloader.PictureData;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

public class PictureBlockEntityRenderer<T extends PictureBlockEntity> implements BlockEntityRenderer<T> {

  private class PictureBlockDimensions {
    public float width = 0.0F;
    public float height = 0.0F;
    public boolean mainBlock = false;

    public PictureBlockDimensions(float width, float height, boolean mainBlock) {
      this.width = width;
      this.height = height;
      this.mainBlock = mainBlock;
    }
  }

  public PictureBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
  }

  @Override
  public void render(PictureBlockEntity blockEntity, float tickDelta, MatrixStack matrices,
      VertexConsumerProvider vertexConsumers, int light, int overlay) {

    PictureBlockDimensions dimensions = detectNearbyBlocks(blockEntity);

    if (!dimensions.mainBlock) {
      return;
    }

    // load the picture
    String url = blockEntity.getCurrentPicture();

    // no picture ignore
    if (url == null || url.isEmpty()) {
      return;
    }

    // get the identity
    PictureData data = FileDownloader.getInstance().getPictureDataForURL(url, true);
    if (data == null || data.identifier == null) {
      return;
    }

    RenderSystem.enableDepthTest();

    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    // RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
    RenderSystem.setShaderTexture(0, data.identifier);
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    RenderSystem.depthMask(false);

    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();

    matrices.push();

    Direction direction = blockEntity.getCachedState().get(Properties.HORIZONTAL_FACING);

    float xOffset = 0.0F;
    float zOffset = 0.0F;

    float zTranslate = 0.0F;
    float xTranslate = 0.0F;

    Quaternion yRotation = Vec3f.POSITIVE_Y.getDegreesQuaternion(0.0F);

    switch (direction) {
      case NORTH:
        zTranslate = -0.001F;
        zOffset = 1.0F;
        xOffset = 1.0F;
        yRotation = Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F);
        break;
      case SOUTH:
        zTranslate = 0.001F;
        break;
      case EAST:
        xTranslate = 0.001F;
        zOffset = 1.0F;
        yRotation = Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0F);
        break;
      case WEST:
        yRotation = Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F);
        xTranslate = -0.001F;
        xOffset = 1.0F;
        break;
    }

    matrices.translate(xTranslate + xOffset, 0.00F, zTranslate + zOffset);

    // set the rotation
    matrices.multiply(yRotation);

    Matrix4f matrix4f = matrices.peek().getModel();
    bufferBuilder.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

    bufferBuilder.vertex(matrix4f, dimensions.width, 0.0F, 1.0F).texture(1.0F, 1.0F).color(255, 255, 255, 255).next(); // A
    bufferBuilder.vertex(matrix4f, dimensions.width, dimensions.height, 1.0F).texture(1.0F, 0.0F)
        .color(255, 255, 255, 255).next(); // B
    bufferBuilder.vertex(matrix4f, 0.0F, dimensions.height, 1.0F).texture(0.0F, 0.0F).color(255, 255, 255, 255).next(); // C
    bufferBuilder.vertex(matrix4f, 0.0F, 0.0F, 1.0F).texture(0.0F, 1.0F).color(255, 255, 255, 255).next(); // D

    tessellator.draw();
    matrices.pop();
    RenderSystem.disableDepthTest();
    RenderSystem.depthMask(true);
    RenderSystem.disableBlend();

    // Cull stops the image from being visible from the back
    // disabling Cull means the texture is visible from two sides
    // RenderSystem.disableCull();
  }

  // detectNearbyBlocks determins if this block is part of a collection such as a
  // wall
  private PictureBlockDimensions detectNearbyBlocks(PictureBlockEntity blockEntity) {
    World world = blockEntity.getWorld();

    Boolean isMainBlock = true;
    float width = 1.0F;
    float height = 1.0F;
    Direction startBlockDirection = Direction.WEST;
    Direction widthBlockDirection = Direction.EAST;

    Direction facing = blockEntity.getCachedState().get(Properties.HORIZONTAL_FACING);
    switch (facing) {
      case NORTH:
        startBlockDirection = Direction.EAST;
        widthBlockDirection = Direction.WEST;
        break;
      case WEST:
        startBlockDirection = Direction.NORTH;
        widthBlockDirection = Direction.SOUTH;
        break;
      case EAST:
        startBlockDirection = Direction.SOUTH;
        widthBlockDirection = Direction.NORTH;
        break;
    }

    // first check if I am the start block, there should be nothing to the right
    BlockPos currentPos = blockEntity.getPos();
    BlockPos checkPos = currentPos.offset(startBlockDirection);
    BlockEntity foundBlock = world.getBlockEntity(checkPos);

    // if there is a block to the west return
    if (foundBlock != null && foundBlock.getType() == blockEntity.getType()) {
      isMainBlock = false;
    }

    checkPos = currentPos.offset(Direction.DOWN);
    foundBlock = world.getBlockEntity(checkPos);

    // if there is a block to the right return
    if (foundBlock != null && foundBlock.getType() == blockEntity.getType()) {
      isMainBlock = false;
    }

    if (!isMainBlock) {
      return new PictureBlockDimensions(0, 0, false);
    }

    // check the east faces for connected block
    checkPos = currentPos.offset(widthBlockDirection);
    foundBlock = world.getBlockEntity(checkPos);

    while (foundBlock != null && foundBlock.getType() == blockEntity.getType()) {
      width++;

      // check the next block
      currentPos = foundBlock.getPos();
      checkPos = currentPos.offset(widthBlockDirection);
      foundBlock = world.getBlockEntity(checkPos);
    }

    // check the top faces for connected blocks
    currentPos = blockEntity.getPos();
    checkPos = currentPos.offset(Direction.UP);
    foundBlock = world.getBlockEntity(checkPos);

    while (foundBlock != null && foundBlock.getType() == blockEntity.getType()) {
      height++;

      // check the next block
      currentPos = foundBlock.getPos();
      checkPos = currentPos.offset(Direction.UP);
      foundBlock = world.getBlockEntity(checkPos);
    }

    return new PictureBlockDimensions(width, height, true);
  }
}