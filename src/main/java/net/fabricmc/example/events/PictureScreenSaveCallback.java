package net.fabricmc.example.events;

import java.util.ArrayList;

import net.fabricmc.example.blocks.PictureBlockEntity;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface PictureScreenSaveCallback {
  Event<PictureScreenSaveCallback> EVENT = EventFactory.createArrayBacked(PictureScreenSaveCallback.class,
      (listeners) -> (urls, pictureBlockEntity) -> {
        for (PictureScreenSaveCallback listener : listeners) {
          ActionResult result = listener.interact(urls, pictureBlockEntity);

          if (result != ActionResult.PASS) {
            return result;
          }
        }

        return ActionResult.PASS;
      });

  ActionResult interact(ArrayList<String> urls, PictureBlockEntity pictureBlockEntity);
}
