package com.github.hashicraft.projector.blocks;

import com.github.hashicraft.projector.events.PictureBlockClicked;
import com.github.hashicraft.projector.ui.PictureBlockGui;
import com.github.hashicraft.projector.ui.PictureBlockScreen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PictureBlock extends BlockWithEntity {

  public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

  public PictureBlock(Settings settings) {
    super(settings);
    this.setDefaultState(this.stateManager.getDefaultState());
  }

  @Override
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
      BlockHitResult hit) {

    PictureBlockEntity blockEntity = (PictureBlockEntity) world.getBlockEntity(pos);

    if (world.isClient()) {
      if (player.isInSneakingPose()) {
        System.out.println("clicked");

        // call the event that is handled in the client mod
        PictureBlockClicked.EVENT.invoker().interact(blockEntity);
        return ActionResult.SUCCESS;
      }

      blockEntity.nextPicture();
    }

    return ActionResult.SUCCESS;
  }

  @Override
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }

  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    PictureBlockEntity blockEntity = new PictureBlockEntity(pos, state);

    return blockEntity;
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
    stateManager.add(FACING);
  }

  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    return getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
  }
}