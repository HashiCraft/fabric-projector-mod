package net.fabricmc.example.networking;

import java.io.Serializable;

public class Picture implements Serializable {
  public int height = 0;
  public int width = 0;
  public String location = "";
  public String identifier = "";

  public Picture(int width, int height, String location) {
    this.height = height;
    this.width = width;
    this.location = location;
  }

  public Picture(int width, int height, String location, String identifier) {
    this.height = height;
    this.width = width;
    this.location = location;
    this.identifier = identifier;
  }
}