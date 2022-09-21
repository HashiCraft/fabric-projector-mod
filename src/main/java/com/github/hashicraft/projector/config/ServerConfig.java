package com.github.hashicraft.projector.config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.github.hashicraft.projector.ProjectorMod;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.PacketByteBuf;

public class ServerConfig {

  private static HashMap<String, String> projectorEnv;

  // Register the server side config, environment variables
  // with the given prefix will be synced to the client collection
  // Prefix must be a non empty string
  public static void Register(String prefix) {
    ProjectorMod.LOGGER.info("Register server config");

    if (prefix.isEmpty()) {
      ProjectorMod.LOGGER.info("Empty prefix, exiting");
      return;
    }

    projectorEnv = new HashMap<String, String>();

    Map<String, String> env = System.getenv();

    // copy the environment variables to the server
    env.forEach((k, v) -> {
      if (k.startsWith(prefix)) {
        ProjectorMod.LOGGER.info("Adding variable: " + k);
        projectorEnv.put(k.replace(prefix, ""), v);
      }
    });

    // Register for a server notification, when a new player joins the server
    // send them the config
    ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
      ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
      ObjectOutputStream out;

      try {
        out = new ObjectOutputStream(byteOut);
        out.writeObject(projectorEnv);

        PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
        passedData.writeByteArray(byteOut.toByteArray());

        sender.sendPacket(Messages.CONFIGUPDATE, passedData);
      } catch (IOException e) {
        e.printStackTrace();
      }

    });
  }
}
