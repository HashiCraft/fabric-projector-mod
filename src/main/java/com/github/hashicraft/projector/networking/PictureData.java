package com.github.hashicraft.projector.networking;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;

public class PictureData implements java.io.Serializable {
  public ArrayList<String> urls = new ArrayList<String>();
  public int currentImage = 0;
  public int x;
  public int y;
  public int z;
  public String world;

  public PictureData() {
  }

  public PictureData(ArrayList<String> urls, int currentImage, BlockPos pos, RegistryKey world) {
    this.urls = urls;
    this.currentImage = currentImage;
    this.x = pos.getX();
    this.y = pos.getY();
    this.z = pos.getZ();
    this.world = world.getValue().toString();
  }

  public byte[] toBytes() {
    try {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream out;
      out = new ObjectOutputStream(bos);
      out.writeObject(this);
      out.close();

      return bos.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  public static PictureData fromBytes(byte[] data) {
    try {
      ByteArrayInputStream bis = new ByteArrayInputStream(data);
      ObjectInputStream ois = new ObjectInputStream(bis);

      PictureData pictureData = (PictureData) ois.readObject();

      return pictureData;
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    return null;
  }
}