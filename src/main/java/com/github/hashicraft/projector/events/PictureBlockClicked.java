package com.github.hashicraft.projector.events;

import com.github.hashicraft.projector.blocks.PictureBlockEntity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface PictureBlockClicked {

  Event<PictureBlockClicked> EVENT = EventFactory.createArrayBacked(PictureBlockClicked.class,
      (listeners) -> (block) -> {
        for (PictureBlockClicked listener : listeners) {
          ActionResult result = listener.interact(block);

          if (result != ActionResult.PASS) {
            return result;
          }
        }

        return ActionResult.PASS;
      });

  ActionResult interact(PictureBlockEntity block);
}