package com.github.hashicraft.projector.config;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.hashicraft.projector.ProjectorMod;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientConfig {

  private static HashMap<String, String> projectorEnv;

  public static void Register() {
    ClientPlayNetworking.registerGlobalReceiver(Messages.CONFIGUPDATE, (client, handler, buf, responseSender) -> {
      ProjectorMod.LOGGER.info("Received config from the server");

      try {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(buf.readByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        projectorEnv = (HashMap<String, String>) in.readObject();

        ProjectorMod.LOGGER.info(projectorEnv.toString());

        // String out =
        // ClientConfig.ReplaceInString("${{env.PROCESSOR_LEVEL}}/${{env.NUMBER_OF_PROCESSORS}}");
        // ProjectorMod.LOGGER.info("out " + out);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  // Interpolates the given string replacing any placeholders ${{env.name}} with
  // projectorEnv values
  public static String ReplaceInString(String in) {
    Pattern pattern = Pattern.compile("(\\$\\{\\{env\\.(.+?)\\}\\})");
    Matcher matcher = pattern.matcher(in);

    String out = in;
    // check all occurance
    while (matcher.find()) {
      // expr is the full match ${{env.[name]}}
      // and the text to be replaced with the env var
      String expr = matcher.group(1);

      // name is the name part of ${{env.[name]}}
      // and should match an item in projectorEnv
      String name = matcher.group(2);

      try {
        String replacement = projectorEnv.get(name);
        if (replacement != null && !replacement.isEmpty()) {
          out = out.replace(expr, replacement);
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }

    return out;
  }
}
