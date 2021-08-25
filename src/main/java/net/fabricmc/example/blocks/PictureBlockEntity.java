package net.fabricmc.example.blocks;

import net.fabricmc.example.ExampleMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Identifier;
import java.util.ArrayList;

public class PictureBlockEntity extends BlockEntity {
  public class Picture {
    int height = 0;
    int width = 0;
    String location = "";

    public Picture(int width, int height, String location) {
      this.height = height;
      this.width = width;
      this.location = location;
    }
  }

  private ArrayList<Picture> pictures = new ArrayList<Picture>();

  public PictureBlockEntity(BlockPos pos, BlockState state) {
    super(ExampleMod.EXAMPLE_BLOCK_ENTITY, pos, state);
    pictures.add(new Picture(1197, 676, "projector:textures/block/slide_1.png"));
    pictures.add(new Picture(1206, 679, "projector:textures/block/slide_2.png"));
    pictures.add(new Picture(1197, 672, "projector:textures/block/slide_3.png"));
  }

  public void nextPicture() {
    BlockState state = this.getCachedState();
    int currentPicture = state.get(PictureBlock.SLIDE).intValue();
    currentPicture++;

    if (currentPicture >= pictures.size()) {
      currentPicture = 0;
    }

    this.setCachedState(state.with(PictureBlock.SLIDE, currentPicture));
    this.getWorld().setBlockState(this.pos, this.getCachedState());
  }

  public void previousPicture() {
    BlockState state = this.getCachedState();
    int currentPicture = state.get(PictureBlock.SLIDE).intValue();
    currentPicture--;

    if (currentPicture < 0) {
      currentPicture = pictures.size() - 1;
    }

    this.setCachedState(state.with(PictureBlock.SLIDE, currentPicture));
    this.getWorld().setBlockState(this.pos, this.getCachedState());
  }

  public Picture getCurrentPicture() {
    int currentPicture = this.getCachedState().get(PictureBlock.SLIDE).intValue();
    return pictures.get(currentPicture);
  }
}
