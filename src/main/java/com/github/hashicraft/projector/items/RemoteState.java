package com.github.hashicraft.projector.items;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

public class RemoteState extends PersistentState {

  public int x;
  public int y;
  public int z;
  public RegistryKey<World> dimension;

  private RemoteState(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public static void updatePosition(ItemStack stack, BlockPos pos) {
    NbtList state;
    if (stack.hasNbt() && stack.getNbt().contains("position", 9)) {
      state = stack.getNbt().getList("position", 10);
    } else {
      state = new NbtList();
      stack.setSubNbt("position", state);
    }

		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putDouble("x", (double)pos.getX());
    nbtCompound.putDouble("y", (double)pos.getY());
		nbtCompound.putDouble("z", (double)pos.getZ());
		state.add(nbtCompound);
	}

  public static RemoteState fromNbt(NbtCompound nbt) {
    int x = nbt.getInt("x");
		int y = nbt.getInt("y");
    int z = nbt.getInt("z");

    return new RemoteState(x, y, z);
  }

  @Override
  public NbtCompound writeNbt(NbtCompound nbt) {
    nbt.putInt("x", this.x);
		nbt.putInt("y", this.y);
    nbt.putInt("z", this.z);
    return nbt;
  }
  
}
