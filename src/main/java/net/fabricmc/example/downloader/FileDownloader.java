package net.fabricmc.example.downloader;

import static java.util.concurrent.Executors.newFixedThreadPool;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;

import javax.imageio.ImageIO;

import net.fabricmc.example.events.PictureDownloadedCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.NativeImage.Format;
import net.minecraft.util.Identifier;

public class FileDownloader {

  static FileDownloader downloader = new FileDownloader();

  public static FileDownloader getInstance() {
    return downloader;
  }

  ExecutorService service = newFixedThreadPool(4);

  public void Download(String url) {
    this.downloadFile(url);
  }

  // downloads the file as a background process
  private void downloadFile(String url) {
    System.out.println("Starting download");

    service.submit(() -> {
      try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
          FileOutputStream fileOutputStream = new FileOutputStream("./test.jpg")) {
        byte dataBuffer[] = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
          fileOutputStream.write(dataBuffer, 0, bytesRead);
        }

        // convert to a png
        Identifier id = convertToPng("./test.jpg");

        // Broadcast the download event
        PictureDownloadedCallback.EVENT.invoker().interact(url, id);
      } catch (IOException e) {
        System.out.println("Unable to download file" + e);
      }

      System.out.println("Downloaded");
    });
  }

  private Identifier convertToPng(String inputFile) {
    // read a jpeg from a inputFile
    BufferedImage bufferedImage;
    try {
      bufferedImage = ImageIO.read(new File(inputFile));

      ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
      ImageIO.write(bufferedImage, "png", byteArrayOut);

      // byte[] resultingBytes = byteArrayOut.toByteArray();
      // ImageIO.write(bufferedImage, "png", new File(outputFile));
      InputStream targetStream = new ByteArrayInputStream(byteArrayOut.toByteArray());

      NativeImage nativeImage = NativeImage.read(targetStream);
      NativeImageBackedTexture nativeTexture = new NativeImageBackedTexture(nativeImage);

      Identifier id = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("image/erik",
          nativeTexture);

      System.out.println("id " + id.toString());

      return id;

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return null;

    // ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
    // ImageIO.write(bufferedImage, "png", byteArrayOut);
    // byte[] resultingBytes = byteArrayOut.toByteArray();
  }
}