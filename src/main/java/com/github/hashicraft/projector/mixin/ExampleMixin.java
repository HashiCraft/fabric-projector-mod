package com.github.hashicraft.projector.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.hashicraft.projector.ProjectorMod;

import net.minecraft.client.gui.screen.TitleScreen;

@Mixin(TitleScreen.class)
public class ExampleMixin {
  @Inject(at = @At("HEAD"), method = "init()V")
  private void init(CallbackInfo info) {
    ProjectorMod.LOGGER.info("This line is printed by an example mod mixin!");
  }
}
