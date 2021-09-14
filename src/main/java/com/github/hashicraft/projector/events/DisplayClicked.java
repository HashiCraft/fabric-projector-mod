package com.github.hashicraft.projector.events;

import com.github.hashicraft.projector.blocks.DisplayEntity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface DisplayClicked {

  Event<DisplayClicked> EVENT = EventFactory.createArrayBacked(DisplayClicked.class,
      (listeners) -> (block, callback) -> {
        for (DisplayClicked listener : listeners) {
          ActionResult result = listener.interact(block, callback);

          if (result != ActionResult.PASS) {
            return result;
          }
        }

        return ActionResult.PASS;
      });

  ActionResult interact(DisplayEntity block, DisplayGuiCallback callback);
}