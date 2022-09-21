package com.github.hashicraft.projector.items;

import java.util.List;

import com.github.hashicraft.projector.ProjectorMod;
import com.github.hashicraft.projector.blocks.DisplayEntity;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.NetworkSyncedItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Remote extends NetworkSyncedItem {
  public Remote(Settings settings) {
    super(settings);
  }

  public void onCraft(ItemStack stack, World world, PlayerEntity player) {
    ProjectorMod.LOGGER.info("Crafting a remote");
  }

  @Override
  public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
    if (!stack.hasNbt()) {
      tooltip.add(Text.literal("Not linked").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
      return;
    }

    NbtCompound position = stack.getOrCreateNbt();
    int x = position.getInt("x");
    int y = position.getInt("y");
    int z = position.getInt("z");

    BlockPos pos = new BlockPos(x, y, z);

    if (pos != null) {
      tooltip
          .add(Text.literal("Linked to " + pos.toShortString()).setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
    }
  }

  public void link(ItemStack stack, BlockPos pos) {
    NbtCompound position = stack.getOrCreateNbt();
    position.putDouble("x", (double) pos.getX());
    position.putDouble("y", (double) pos.getY());
    position.putDouble("z", (double) pos.getZ());
    stack.setNbt(position);
  }

  public void click(ItemStack stack, World world) {
    NbtCompound position = stack.getOrCreateNbt();
    int x = position.getInt("x");
    int y = position.getInt("y");
    int z = position.getInt("z");

    BlockPos pos = new BlockPos(x, y, z);

    DisplayEntity entity = (DisplayEntity) world.getBlockEntity(pos);
    if (entity != null) {
      entity.nextPicture();
      entity.markForUpdate();
    }

  }

  @Override
  public ActionResult useOnBlock(ItemUsageContext context) {
    return ActionResult.PASS;
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
    ItemStack itemStack = playerEntity.getStackInHand(hand);
    click(itemStack, world);
    playerEntity.playSound(SoundEvents.UI_BUTTON_CLICK, 0.2F, 1.0F);
    return TypedActionResult.success(itemStack);
  }
}