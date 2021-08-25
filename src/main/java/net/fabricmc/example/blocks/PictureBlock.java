package net.fabricmc.example.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.item.ItemPlacementContext;

import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;

public class PictureBlock extends BlockWithEntity {

  public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
  public static IntProperty SLIDE = IntProperty.of("slide", 0, 2000);

  public PictureBlock(Settings settings) {
    super(settings);
    this.setDefaultState(this.stateManager.getDefaultState());
  }

  @Override
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
      BlockHitResult hit) {

    if (!world.isClient) {
      PictureBlockEntity blockEntity = (PictureBlockEntity) world.getBlockEntity(pos);

      if (player.isInSneakingPose()) {
        blockEntity.previousPicture();
        player.sendMessage(new LiteralText("Previous!"), false);
        return ActionResult.SUCCESS;
      }

      blockEntity.nextPicture();
      player.sendMessage(new LiteralText("Next!"), false);
    }
    return ActionResult.SUCCESS;
  }

  @Override
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }

  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    // TODO Auto-generated method stub
    return new PictureBlockEntity(pos, state);
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
    stateManager.add(FACING);
    stateManager.add(SLIDE);
  }

  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    return getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
  }
}