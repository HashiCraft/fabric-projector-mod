package com.github.hashicraft.projector.ui;

import java.util.ArrayList;

import com.github.hashicraft.projector.ProjectorMod;
import com.github.hashicraft.projector.blocks.DisplayEntity;
import com.github.hashicraft.projector.events.DisplayGuiCallback;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.WToggleButton;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class DisplayGui extends LightweightGuiDescription {
  WPlainPanel root = new WPlainPanel();
  WScrollPanel scroll;
  WLabel title = new WLabel(Text.literal("§lConfigure Projector Images"));

  WLabel rotateLabel = new WLabel(Text.literal("Image rotation (seconds)"));
  WTextField rotateDuration = new WTextField(Text.literal("0"));
  WLabel cacheLabel = new WLabel(Text.literal("Cache duration (seconds)"));
  WTextField cacheDuration = new WTextField(Text.literal("0"));
  WToggleButton autoRotate = new WToggleButton(Text.literal("Auto rotate"));

  ArrayList<WTextField> imageFields = new ArrayList<WTextField>();

  private int imageCount = 0;

  private DisplayEntity display;
  private DisplayGuiCallback callback;

  public DisplayGui(DisplayEntity display, DisplayGuiCallback callback) {
    this.display = display;
    this.callback = callback;

    setRootPanel(root);
    root.setSize(300, 250);
    root.setInsets(Insets.ROOT_PANEL);

    ArrayList<String> urls = display.getPictures();
    imageCount = urls.size();

    drawPanel();

    populateValues();
  }

  private void drawPanel() {
    imageFields.clear();
    root.add(title, 0, 0, 4, 1);

    root.add(rotateDuration, 0, 20, 40, 18);
    root.add(rotateLabel, 50, 26, 16, 18);

    root.add(cacheDuration, 0, 44, 40, 18);
    root.add(cacheLabel, 50, 50, 16, 18);

    root.add(autoRotate, 0, 68, 20, 18);

    WLabel label = new WLabel(Text.literal("Images to load:"));
    root.add(label, 0, 90, 100, 18);

    WButton plus = new WButton(Text.literal("+"));
    root.add(plus, 260, 84, 18, 18);

    plus.setOnClick(() -> {
      setValues();

      ArrayList<String> urls = display.getPictures();
      imageCount = urls.size();
      imageCount++;

      drawPanel();
      populateValues();
    });

    if (imageCount > 1) {
      WButton minus = new WButton(Text.literal("-"));
      root.add(minus, 236, 84, 18, 18);

      minus.setOnClick(() -> {
        imageCount--;
        setValues();

        drawPanel();
        populateValues();
      });
    }

    if (scroll != null) {
      root.remove(scroll);
    }

    WPlainPanel panel = new WPlainPanel();

    scroll = new WScrollPanel(panel);
    scroll.setHost(root.getHost());
    root.add(scroll, 0, 110, 300, 100);

    // draw the images
    int currentRow = 0;
    for (int n = 0; n < imageCount; n++) {
      WLabel l = new WLabel(Text.literal(String.valueOf(n + 1) + "."));
      panel.add(l, 0, currentRow * 24 + 6, 20, 18);

      WTextField wtf = new WTextField(Text.literal("http://image.com/image.jpg"));
      wtf.setMaxLength(25500);
      panel.add(wtf, 24, currentRow * 24, 200, 18);

      WButton clear = new WButton(Text.literal("x"));
      panel.add(clear, 236, currentRow * 24, 18, 18);

      clear.setOnClick(() -> {
        wtf.setText("");
      });

      imageFields.add(wtf);

      currentRow++;
    }

    WButton save = new WButton(Text.literal("Save"));
    save.setOnClick(() -> {
      ProjectorMod.LOGGER.info("Button clicked!");

      setValues();

      // notify the opener that the dialog has completed
      callback.onSave();

      // close the dialog
      MinecraftClient client = MinecraftClient.getInstance();
      client.player.closeScreen();
      client.setScreen((Screen) null);
    });
    // root.add(save, 0, 200, 100, 18);
    root.add(save, 0, 220, 100, 18);

    root.validate(this);
  }

  private void populateValues() {
    ArrayList<String> urls = display.getPictures();
    int n = 0;
    for (String url : urls) {
      imageFields.get(n).setText(url);
      n++;
    }

    autoRotate.setToggle(display.getAutoRotate());
    rotateDuration.setText(String.valueOf(display.getRotateSeconds()));
    cacheDuration.setText(String.valueOf(display.getCacheSeconds()));
  }

  private void setValues() {
    display.clearPictures();
    for (int n = 0; n < imageCount; n++) {
      String text = imageFields.get(n).getText();
      if (!text.isEmpty()) {
        display.addPicture(text);
      }
    }

    display.setAutoRotate(autoRotate.getToggle());

    int rotateSeconds;
    try {
      rotateSeconds = Integer.parseInt(rotateDuration.getText());
    } catch (NumberFormatException e) {
      rotateSeconds = 0;
    }
    display.setRotateSeconds(rotateSeconds);

    int cacheSeconds;
    try {
      cacheSeconds = Integer.parseInt(cacheDuration.getText());
    } catch (NumberFormatException e) {
      cacheSeconds = 0;
    }
    display.setCacheSeconds(cacheSeconds);
  }
}
