package com.github.hashicraft.projector.blocks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.github.hashicraft.projector.ProjectorMod;
import com.github.hashicraft.projector.networking.Channels;
import com.github.hashicraft.projector.networking.PictureData;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class PictureBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

  private ArrayList<String> pictures = new ArrayList<String>();
  private int currentPicture = 0;

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
}