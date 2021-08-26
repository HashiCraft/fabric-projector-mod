package net.fabricmc.example.ui;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.fabricmc.example.blocks.PictureBlockEntity;
import net.fabricmc.example.events.PictureScreenSaveCallback;
import net.minecraft.text.LiteralText;
import java.util.ArrayList;

public class PictureBlockGui extends LightweightGuiDescription {
  private ArrayList<WTextField> urlFields = new ArrayList<WTextField>();

  private PictureBlockEntity currentEntity;

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
      // This code runs on the client when you click the button.
      // Broadcast the download event
      PictureScreenSaveCallback.EVENT.invoker().interact(getURLs(), currentEntity);
    });

    root.add(button, 0, 7, 16, 1);

    root.validate(this);
  }

  public ArrayList<String> getURLs() {
    ArrayList<String> urls = new ArrayList<String>();
    for (WTextField urlField : urlFields) {
      urls.add(urlField.getText());
    }

    return urls;
  }

  public void setURLs(PictureBlockEntity pictureBlockEntity) {
    this.currentEntity = pictureBlockEntity;
    ArrayList<String> urls = pictureBlockEntity.getPictures();

    int n = 0;
    for (String url : urls) {
      urlFields.get(n).setText(url);
      n++;
    }
  }
}
