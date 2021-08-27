package com.github.hashicraft.projector.blocks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.github.hashicraft.projector.ProjectorMod;
import com.github.hashicraft.projector.downloader.FileDownloader;
import com.github.hashicraft.projector.networking.Channels;
import com.github.hashicraft.projector.networking.PictureData;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PictureBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

  private ArrayList<String> pictures = new ArrayList<String>();
  private int currentPicture = 0;

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
  }

  // Serialize the BlockEntity
  @Override
  public NbtCompound writeNbt(NbtCompound tag) {
    super.writeNbt(tag);

    tag.putByteArray("pictures", serializePictures());
    tag.putInt("current", currentPicture);

    System.out.println("Serialized server data");
    return tag;
  }

  // Deserialize the BlockEntity
  @Override
  public void readNbt(NbtCompound tag) {
    super.readNbt(tag);

    pictures = deserializePictures(tag.getByteArray("pictures"));
    currentPicture = tag.getInt("current");

    System.out.println("Deserialized server data");
  }

  @Override
  public void fromClientTag(NbtCompound tag) {
    super.readNbt(tag);

    pictures = deserializePictures(tag.getByteArray("pictures"));
    currentPicture = tag.getInt("current");

    // Call the downloader to ensure all the images are cached
    for (String url : pictures) {
      FileDownloader.getInstance().download(url);
    }

    System.out.println("Deserialized client data");
  }

  @Override
  public NbtCompound toClientTag(NbtCompound tag) {
    super.writeNbt(tag);

    tag.putByteArray("pictures", serializePictures());
    tag.putInt("current", currentPicture);

    System.out.println("Serialized client data");

    return tag;
  }

  private ArrayList<String> deserializePictures(byte[] data) {
    try {
      ByteArrayInputStream bis = new ByteArrayInputStream(data);
      ObjectInputStream ois;
      ois = new ObjectInputStream(bis);

      return (ArrayList<String>) ois.readObject();

    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    return null;
  }

  private byte[] serializePictures() {
    try {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream out;
      out = new ObjectOutputStream(bos);
      out.writeObject(pictures);
      out.close();

      return bos.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  public void clearPictures() {
    pictures = new ArrayList<String>();
  }

  public void addPicture(String location) {
    if (pictures == null) {
      pictures = new ArrayList<String>();
    }

    pictures.add(location);
  }

  public void nextPicture() {
    currentPicture++;

    if (currentPicture >= pictures.size()) {
      currentPicture = 0;
    }

    this.updateState();
  }

  public void previousPicture() {
    currentPicture--;

    if (currentPicture < 0) {
      currentPicture = pictures.size() - 1;
    }

    this.updateState();
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

  // updateState tells the server that the local client state has changed
  public void updateState() {
    // send the data to the sever so that it can be written to other players
    PictureData data = new PictureData(this.getPictures(), this.currentPicture, this.getPos());
    PacketByteBuf buf = PacketByteBufs.create();
    buf.writeByteArray(data.toBytes());

    ClientPlayNetworking.send(Channels.UPDATE_PICTURES, buf);
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