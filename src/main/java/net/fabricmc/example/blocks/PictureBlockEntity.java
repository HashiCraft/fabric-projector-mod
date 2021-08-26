package net.fabricmc.example.blocks;

import net.fabricmc.example.ExampleMod;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class PictureBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

  private ArrayList<String> pictures = new ArrayList<String>();

  public PictureBlockEntity(BlockPos pos, BlockState state) {
    super(ExampleMod.EXAMPLE_BLOCK_ENTITY, pos, state);
  }

  // Serialize the BlockEntity
  @Override
  public NbtCompound writeNbt(NbtCompound tag) {
    super.writeNbt(tag);

    tag.putByteArray("pictures", serializePictures());

    System.out.println("Serialized server data");
    return tag;
  }

  // Deserialize the BlockEntity
  @Override
  public void readNbt(NbtCompound tag) {
    super.readNbt(tag);

    pictures = deserializePictures(tag.getByteArray("pictures"));

    System.out.println("Deserialized server data");
  }

  @Override
  public void fromClientTag(NbtCompound tag) {
    super.readNbt(tag);

    pictures = deserializePictures(tag.getByteArray("pictures"));

    System.out.println("Deserialized client data");
  }

  @Override
  public NbtCompound toClientTag(NbtCompound tag) {
    super.writeNbt(tag);

    tag.putByteArray("pictures", serializePictures());

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
    BlockState state = this.getCachedState();
    int currentPicture = state.get(PictureBlock.SLIDE).intValue();
    currentPicture++;

    if (currentPicture >= pictures.size()) {
      currentPicture = 0;
    }

    this.setCachedState(state.with(PictureBlock.SLIDE, currentPicture));
    this.getWorld().setBlockState(this.pos, this.getCachedState());
  }

  public void previousPicture() {
    BlockState state = this.getCachedState();
    int currentPicture = state.get(PictureBlock.SLIDE).intValue();
    currentPicture--;

    if (currentPicture < 0) {
      currentPicture = pictures.size() - 1;
    }

    this.setCachedState(state.with(PictureBlock.SLIDE, currentPicture));
    this.getWorld().setBlockState(this.pos, this.getCachedState());
  }

  public String getCurrentPicture() {
    if (this.pictures == null || this.pictures.size() == 0) {
      return null;
    }

    int currentPicture = this.getCachedState().get(PictureBlock.SLIDE).intValue();
    return pictures.get(currentPicture);
  }

  public ArrayList<String> getPictures() {
    return pictures;
  }
}