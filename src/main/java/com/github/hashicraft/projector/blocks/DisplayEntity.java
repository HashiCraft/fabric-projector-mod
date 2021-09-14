package com.github.hashicraft.projector.blocks;

import java.util.ArrayList;

import com.github.hashicraft.projector.ProjectorMod;
import com.github.hashicraft.stateful.blocks.StatefulBlockEntity;
import com.github.hashicraft.stateful.blocks.Syncable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DisplayEntity extends StatefulBlockEntity {

  @Syncable
  public int currentPicture;

  @Syncable
  public ArrayList<String> pictures;

  public class DisplayDimensions {
    public float width = 0.0F;
    public float height = 0.0F;
    public boolean mainBlock = false;
    public boolean isPowered = false;

    public DisplayDimensions(float width, float height, boolean mainBlock, boolean isPowered) {
      this.width = width;
      this.height = height;
      this.mainBlock = mainBlock;
      this.isPowered = isPowered;
    }
  }

  public DisplayEntity(BlockPos pos, BlockState state) {
    super(ProjectorMod.DISPLAY_ENTITY, pos, state, null);
    pictures = new ArrayList<String>();
  }

  public DisplayEntity(BlockPos pos, BlockState state, Block parent) {
    super(ProjectorMod.DISPLAY_ENTITY, pos, state, parent);
    pictures = new ArrayList<String>();
  }

  public void clearPictures() {
    pictures.clear();
  }

  public void addPicture(String location) {
    pictures.add(location);
  }

  public void nextPicture() {
    currentPicture++;

    if (currentPicture >= pictures.size()) {
      currentPicture = 0;
    }
  }

  public void previousPicture() {
    currentPicture--;

    if (currentPicture < 0) {
      currentPicture = pictures.size() - 1;
    }
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
  public DisplayDimensions detectNearbyBlocks() {
    World world = this.getWorld();

    Boolean isMainBlock = true;
    Boolean isPowered = false;
    float width = 1.0F;
    float height = 1.0F;
    float curHeight = 1.0F;

    // default SOUTH
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
      case SOUTH:
        startBlockDirection = Direction.WEST;
        widthBlockDirection = Direction.EAST;
        break;
      default:
        break;
    }

    // Check if the block is redstone powered
    int power = world.getReceivedRedstonePower(pos);
    if (power > 0) {
      isPowered = true;
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
      return new DisplayDimensions(0, 0, false, false);
    }

    BlockPos xPos = currentPos;

    // check the x
    while (true) {

      // check the y
      curHeight = 1;
      while (true) {
        checkPos = currentPos.offset(Direction.UP);
        foundBlock = world.getBlockEntity(checkPos);

        if (foundBlock == null || foundBlock.getType() != this.getType()) {
          currentPos = xPos; // reset the x
          break;
        }

        curHeight++;
        if (curHeight >= height) {
          height = curHeight;
        }

        currentPos = foundBlock.getPos();

        power = world.getReceivedRedstonePower(checkPos);
        if (!isPowered && power > 0) {
          isPowered = true;
        }
      }

      // check the east faces for connected block
      checkPos = currentPos.offset(widthBlockDirection);
      foundBlock = world.getBlockEntity(checkPos);

      if (foundBlock == null || foundBlock.getType() != this.getType()) {
        break; // no more blocks
      }

      width++;
      currentPos = foundBlock.getPos();
      xPos = currentPos;

      power = world.getReceivedRedstonePower(checkPos);
      if (!isPowered && power > 0) {
        isPowered = true;
      }
    }

    return new DisplayDimensions(width, height, isMainBlock, isPowered);
  }
}