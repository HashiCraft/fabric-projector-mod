package net.fabricmc.example.ui;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.fabricmc.example.blocks.PictureBlockEntity;
import net.fabricmc.example.events.PictureScreenSaveCallback;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.text.LiteralText;

public class PictureBlockGui extends LightweightGuiDescription {
  private WTextField url1;
  private WTextField url2;
  private WTextField url3;
  private WTextField url4;

  private PictureBlockEntity currentEntity;

  public PictureBlockGui() {
    WGridPanel root = new WGridPanel();
    setRootPanel(root);
    root.setInsets(Insets.ROOT_PANEL);

    WLabel label = new WLabel(new LiteralText("Image URL"));
    root.add(label, 0, 0, 4, 1);

    url1 = new WTextField(new LiteralText("Image Url to load"));
    root.add(url1, 0, 1, 16, 2);
    url1.setMaxLength(255);

    url2 = new WTextField(new LiteralText("Image Url to load"));
    root.add(url2, 0, 2, 16, 2);
    url1.setMaxLength(255);

    url3 = new WTextField(new LiteralText("Image Url to load"));
    root.add(url3, 0, 3, 16, 2);
    url1.setMaxLength(255);

    url4 = new WTextField(new LiteralText("Image Url to load"));
    root.add(url4, 0, 4, 16, 2);
    url1.setMaxLength(255);

    WButton button = new WButton(new LiteralText("Save"));
    button.setOnClick(() -> {
      // This code runs on the client when you click the button.
      // Broadcast the download event
      PictureScreenSaveCallback.EVENT.invoker().interact(getURLs(), currentEntity);
    });

    root.add(button, 0, 7, 16, 1);

    root.validate(this);
  }

  public String[] getURLs() {
    return new String[] { url1.getText(), url2.getText(), url3.getText(), url4.getText() };
  }

  public void setURLs(PictureBlockEntity pictureBlockEntity) {
    this.currentEntity = pictureBlockEntity;
    String[] urls = pictureBlockEntity.getPictureURLs();

    if (urls.length > 0) {
      url1.setText(urls[0]);
    }

    if (urls.length > 1) {
      url2.setText(urls[1]);
    }

    if (urls.length > 2) {
      url3.setText(urls[2]);
    }

    if (urls.length > 3) {
      url4.setText(urls[3]);
    }
  }
}
