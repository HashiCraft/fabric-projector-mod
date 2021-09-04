package com.github.hashicraft.projector.ui;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.text.LiteralText;
import java.util.ArrayList;

import com.github.hashicraft.projector.blocks.PictureBlockEntity;
import com.github.hashicraft.projector.events.PictureBlockGuiCallback;

public class PictureBlockGui extends LightweightGuiDescription {
  private ArrayList<WTextField> urlFields = new ArrayList<WTextField>();

  private PictureBlockEntity currentEntity;
  private PictureBlockGuiCallback callback;

  public PictureBlockGui() {
    WGridPanel root = new WGridPanel();
    setRootPanel(root);
    root.setInsets(Insets.ROOT_PANEL);

    WLabel label = new WLabel(new LiteralText("Image URL"));
    root.add(label, 0, 0, 4, 1);

    WTextField urlField;
    urlField = new WTextField(new LiteralText("Image Url to load"));
    root.add(urlField, 0, 1, 16, 2);
    urlField.setMaxLength(255);
    urlFields.add(urlField);

    urlField = new WTextField(new LiteralText("Image Url to load"));
    root.add(urlField, 0, 2, 16, 2);
    urlField.setMaxLength(255);
    urlFields.add(urlField);

    urlField = new WTextField(new LiteralText("Image Url to load"));
    root.add(urlField, 0, 3, 16, 2);
    urlField.setMaxLength(255);
    urlFields.add(urlField);

    urlField = new WTextField(new LiteralText("Image Url to load"));
    root.add(urlField, 0, 4, 16, 2);
    urlField.setMaxLength(255);
    urlFields.add(urlField);

    urlField = new WTextField(new LiteralText("Image Url to load"));
    root.add(urlField, 0, 5, 16, 2);
    urlField.setMaxLength(255);
    urlFields.add(urlField);

    WButton button = new WButton(new LiteralText("Save"));
    button.setOnClick(() -> {
      System.out.println("URLs saved");
      currentEntity.clearPictures();

      for (WTextField url : urlFields) {
        String text = url.getText();
        if (!text.isEmpty()) {
          currentEntity.addPicture(text);
        }
      }

      // notify the opener that the dialog has completed
      this.callback.onSave();
    });

    root.add(button, 0, 7, 16, 1);

    root.validate(this);
  }

  public void setup(PictureBlockEntity pictureBlockEntity, PictureBlockGuiCallback callback) {
    this.currentEntity = pictureBlockEntity;
    this.callback = callback;

    ArrayList<String> urls = pictureBlockEntity.getPictures();

    int n = 0;
    for (String url : urls) {
      urlFields.get(n).setText(url);
      n++;
    }
  }
}
