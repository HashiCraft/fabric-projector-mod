package com.github.hashicraft.projector.blocks;

import com.github.hashicraft.projector.ProjectorMod;
import com.github.hashicraft.projector.blocks.DisplayEntity.DisplayDimensions;
import com.github.hashicraft.projector.downloader.FileDownloader;
import com.github.hashicraft.projector.downloader.FileDownloader.PictureData;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class DisplayEntityRenderer<T extends DisplayEntity> implements BlockEntityRenderer<T> {
  public DisplayEntityRenderer(BlockEntityRendererFactory.Context ctx) {
  }

  @Override
  public void render(DisplayEntity blockEntity, float tickDelta, MatrixStack matrices,
      VertexConsumerProvider vertexConsumers, int light, int overlay) {

    DisplayDimensions dimensions = blockEntity.detectNearbyBlocks();

    if (!dimensions.mainBlock) {
      return;
    }

    // load the picture
    String url = blockEntity.getCurrentPicture();

    // no picture ignore
    if (url == null || url.isEmpty()) {
      return;
    }

    
    // get the texture or fallback to placeholder
    Identifier texture = ProjectorMod.PLACEHOLDER_TEXTURE;
    PictureData data = FileDownloader.getInstance().getPictureDataForURL(url, true);
    if (data != null && data.identifier != null) {
      texture = data.identifier;
    }

    // only enable if the block is powered
    if (!dimensions.isPowered) {
      return;
    }

    RenderSystem.enableDepthTest();
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderTexture(0, texture);

    RenderSystem.enableDepthTest();
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

    float displayWidth = dimensions.width - 0.25F;
    float displayHeight = dimensions.height - 0.25F;

    Quaternion yRotation = Vec3f.POSITIVE_Y.getDegreesQuaternion(0.0F);

    switch (direction) {
      case NORTH:
        zTranslate = 0.49F;
        zOffset = 1.0F;
        xOffset = 1.0F;
        yRotation = Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F);
        break;
      case SOUTH:
        zTranslate = -0.49F;
        break;
      case EAST:
        xTranslate = -0.49F;
        zOffset = 1.0F;
        yRotation = Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0F);
        break;
      case WEST:
        yRotation = Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F);
        xTranslate = 0.49F;
        xOffset = 1.0F;
        break;
      default:
        break;
    }

    matrices.translate(xTranslate + xOffset, 0.00F, zTranslate + zOffset);

    // set the rotation
    matrices.multiply(yRotation);

    Matrix4f matrix4f = matrices.peek().getModel();
    bufferBuilder.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

    bufferBuilder.vertex(matrix4f, displayWidth + 0.125F, 0.125F, 1.0F).texture(1.0F, 1.0F).color(255, 255, 255, 255)
        .light(light).overlay(overlay).next(); // A

    bufferBuilder.vertex(matrix4f, displayWidth + 0.125F, displayHeight + 0.125F, 1.0F).texture(1.0F, 0.0F)
        .color(255, 255, 255, 255).light(light).overlay(overlay).next(); // B

    bufferBuilder.vertex(matrix4f, 0.125F, displayHeight + 0.125F, 1.0F).texture(0.0F, 0.0F).color(255, 255, 255, 255)
        .light(light).overlay(overlay).next(); // C

    bufferBuilder.vertex(matrix4f, 0.125F, 0.125F, 1.0F).texture(0.0F, 1.0F).color(255, 255, 255, 255).light(light)
        .overlay(overlay).next(); // D

    tessellator.draw();
    matrices.pop();
    RenderSystem.disableDepthTest();
    RenderSystem.depthMask(true);
    RenderSystem.disableBlend();

    // Cull stops the image from being visible from the back
    // disabling Cull means the texture is visible from two sides
    // RenderSystem.disableCull();
  }
}