package net.fabricmc.example.networking;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.util.math.BlockPos;

public class PictureData implements java.io.Serializable {
  public ArrayList<String> urls = new ArrayList<String>();
  public int x;
  public int y;
  public int z;

  public PictureData() {
  }

  public PictureData(ArrayList<String> urls, BlockPos pos) {
    this.urls = urls;
    this.x = pos.getX();
    this.y = pos.getY();
    this.z = pos.getZ();
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