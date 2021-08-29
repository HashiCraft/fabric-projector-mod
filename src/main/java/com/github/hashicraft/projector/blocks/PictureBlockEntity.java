package com.github.hashicraft.projector.blocks;

import java.util.ArrayList;

import com.github.hashicraft.projector.ProjectorMod;
import com.github.hashicraft.projector.downloader.FileDownloader;
import com.github.hashicraft.projector.state.EntityStateData;
import com.github.hashicraft.projector.state.StatefulBlockEntity;
import com.github.hashicraft.projector.state.Syncable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PictureBlockEntity extends StatefulBlockEntity {

  @Syncable
  public int currentPicture;

  @Syncable
  public ArrayList<String> pictures;

  public class PictureBlockDimensions {
    public float width = 0.0F;
    public float height = 0.0F;
    public boolean mainBlock = false;
    public boolean isStronglyPowered = false;
    public boolean isWeaklyPowered = false;

    public PictureBlockDimensions(float width, float height, boolean mainBlock, boolean isStronglyPowered,
        boolean isWeaklyPowered) {

      this.width = width;
      this.height = height;
      this.mainBlock = mainBlock;
      this.isStronglyPowered = isStronglyPowered;
      this.isWeaklyPowered = isWeaklyPowered;
    }
  }

  public PictureBlockEntity(BlockPos pos, BlockState state) {
    super(ProjectorMod.PICTURE_BLOCK_ENTITY, pos, state);
    pictures = new ArrayList<String>();
  }

  @Override
  public void serverStateUpdated(EntityStateData data) {
    super.serverStateUpdated(data);

    // Pre cache all images
    for (String picture : pictures) {
      FileDownloader.getInstance().download(picture);
    }
  }

  public void clearPictures() {
    pictures.clear();
    this.markForUpdate();
  }

  public void addPicture(String location) {
    pictures.add(location);
    this.markForUpdate();
  }

  public void nextPicture() {
    currentPicture++;

    if (currentPicture >= pictures.size()) {
      currentPicture = 0;
    }

    this.markForUpdate();
  }

  public void previousPicture() {
    currentPicture--;

    if (currentPicture < 0) {
      currentPicture = pictures.size() - 1;
    }

    this.markForUpdate();
  }

  public String getCurrentPicture() {
    if (this.pictures == null || this.pictures.size() == 0) {
      return null;
    }

    return pictures.get(currentPicture);
  }

  public ArrayList<String> getPictures() {
    return pictures;
  }

  public void setCurrentPicture(int currentPicture) {
    this.currentPicture = currentPicture;
  }

  // detectNearbyBlocks determines if this block is part of a collection such as a
  // wall. It returns the height and width of the continous block group.
  // Also if this block is the main block, the main block is the bottom left
  // corner.
  public PictureBlockDimensions detectNearbyBlocks() {
    World world = this.getWorld();

    Boolean isMainBlock = true;
    Boolean isStronglyPowered = false;
    Boolean isWeaklyPowered = false;
    float width = 1.0F;
    float height = 1.0F;
    Direction startBlockDirection = Direction.WEST;
    Direction widthBlockDirection = Direction.EAST;

    Direction facing = this.getCachedState().get(Properties.HORIZONTAL_FACING);
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

    // Check if the block is redstone powered
    int power = world.getReceivedRedstonePower(pos);
    if (power > 6) {
      isStronglyPowered = true;
      isWeaklyPowered = false;
    } else if (power > 0) {
      isWeaklyPowered = true;
    }

    // Check if I am the start block, there should be nothing to the right
    BlockPos currentPos = this.getPos();
    BlockPos checkPos = currentPos.offset(startBlockDirection);
    BlockEntity foundBlock = world.getBlockEntity(checkPos);

    // if there is a block to the west return
    if (foundBlock != null && foundBlock.getType() == this.getType()) {
      isMainBlock = false;
    }

    checkPos = currentPos.offset(Direction.DOWN);
    foundBlock = world.getBlockEntity(checkPos);

    // if there is a block to the right return
    if (foundBlock != null && foundBlock.getType() == this.getType()) {
      isMainBlock = false;
    }

    if (!isMainBlock) {
      return new PictureBlockDimensions(0, 0, false, false, false);
    }

    // check the east faces for connected block
    checkPos = currentPos.offset(widthBlockDirection);
    foundBlock = world.getBlockEntity(checkPos);
    power = world.getReceivedRedstonePower(checkPos);

    while (foundBlock != null && foundBlock.getType() == this.getType()) {
      width++;

      if (power > 6) {
        isStronglyPowered = true;
        isWeaklyPowered = false;
      } else if (power > 0 && !isStronglyPowered) {
        isWeaklyPowered = true;
      }

      // check the next block
      currentPos = foundBlock.getPos();
      checkPos = currentPos.offset(widthBlockDirection);
      foundBlock = world.getBlockEntity(checkPos);
      power = world.getReceivedRedstonePower(checkPos);
    }

    // check the top faces for connected blocks
    currentPos = this.getPos();
    checkPos = currentPos.offset(Direction.UP);
    foundBlock = world.getBlockEntity(checkPos);

    while (foundBlock != null && foundBlock.getType() == this.getType()) {
      height++;

      // check the next block
      currentPos = foundBlock.getPos();
      checkPos = currentPos.offset(Direction.UP);
      foundBlock = world.getBlockEntity(checkPos);
    }

    return new PictureBlockDimensions(width, height, true, isStronglyPowered, isWeaklyPowered);
  }
}