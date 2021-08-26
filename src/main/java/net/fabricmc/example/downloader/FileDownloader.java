package net.fabricmc.example.downloader;

import static java.util.concurrent.Executors.newFixedThreadPool;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;

import javax.imageio.ImageIO;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public class FileDownloader {

  public class PictureData {
    public int height = 0;
    public int width = 0;
    public String location = "";
    public Identifier identifier = null;

    public PictureData(String location) {
      this.location = location;
    }
  }

  static FileDownloader downloader = new FileDownloader();

  public static FileDownloader getInstance() {
    return downloader;
  }

  // Ensure that no more than 4 download threads operate at once
  private ExecutorService service = newFixedThreadPool(4);

  // stores a cache of pictures
  private Hashtable<String, PictureData> cache = new Hashtable<String, PictureData>();

  private Object mutex = new Object();

  // Asyncronously download an image from the given URL
  public void download(String url) {
    synchronized (mutex) {
      PictureData data = this.cache.get(url);
      if (data == null) {
        this.downloadFile(url);
      }
    }
  }

  // Gets the picture data for the given URL, if a picture for the URL
  // does not exist in the cache and download is true getPictureDataForURL
  // will add the given URL to the download queue.
  //
  // It is safe to call this method in a loop as URL is only ever downloaded
  // once.
  public PictureData getPictureDataForURL(String url, Boolean download) {
    synchronized (mutex) {
      // attempt to get the url from the cache
      PictureData data = this.cache.get(url);
      if (data == null && download) {
        // add to the queue
        this.downloadFile(url);
        return null;
      }

      // if no identifier exists then the image has not completed downloading
      if (data.identifier == null) {
        return null;
      }

      return data;
    }
  }

  // downloads the file as a background process
  private void downloadFile(String url) {
    System.out.println("Starting download for url:" + url);

    // add the url to the cache, we can update this once download is complete
    this.cache.put(url, new PictureData(url));

    service.submit(() -> {
      try {
        BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
        File tempFile = File.createTempFile("image", "");
        tempFile.deleteOnExit();

        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tempFile));

        byte dataBuffer[] = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
          out.write(dataBuffer, 0, bytesRead);
        }

        out.close();

        // convert to a png
        BufferedImage bufferedImage = ImageIO.read(tempFile);

        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOut);

        // byte[] resultingBytes = byteArrayOut.toByteArray();
        // ImageIO.write(bufferedImage, "png", new File(outputFile));
        InputStream targetStream = new ByteArrayInputStream(byteArrayOut.toByteArray());

        NativeImage nativeImage = NativeImage.read(targetStream);
        NativeImageBackedTexture nativeTexture = new NativeImageBackedTexture(nativeImage);

        Identifier id = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("image/pictures",
            nativeTexture);

        // update the cache
        synchronized (mutex) {
          PictureData data = this.cache.get(url);
          data.identifier = id;
          data.height = bufferedImage.getHeight();
          data.width = bufferedImage.getWidth();
        }

        System.out.println("Downloaded url:" + url);

      } catch (IOException e) {
        System.out.println("Unable to download file" + e);
      }
    });
  }
}