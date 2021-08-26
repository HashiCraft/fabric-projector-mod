package net.fabricmc.example.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

public interface PictureDownloadedCallback {
  Event<PictureDownloadedCallback> EVENT = EventFactory.createArrayBacked(PictureDownloadedCallback.class,
      (listeners) -> (url, identifier) -> {
        for (PictureDownloadedCallback listener : listeners) {
          ActionResult result = listener.interact(url, identifier);

          if (result != ActionResult.PASS) {
            return result;
          }
        }

        return ActionResult.PASS;
      });

  ActionResult interact(String url, Identifier identifier);
}
