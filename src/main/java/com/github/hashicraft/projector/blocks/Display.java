package com.github.hashicraft.projector.blocks;

import com.github.hashicraft.projector.ProjectorMod;
import com.github.hashicraft.projector.events.DisplayClicked;
import com.github.hashicraft.projector.items.Remote;
import com.github.hashicraft.stateful.blocks.StatefulBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class Display extends StatefulBlock {

  public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
  public static final BooleanProperty TOP = BooleanProperty.of("top");
  public static final BooleanProperty BOTTOM = BooleanProperty.of("bottom");
  public static final BooleanProperty LEFT = BooleanProperty.of("left");
  public static final BooleanProperty RIGHT = BooleanProperty.of("right");

  public Display(Settings settings) {
    super(settings);
    setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(TOP, false)
        .with(BOTTOM, false).with(LEFT, false).with(RIGHT, false));
  }

  public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
      WorldAccess world, BlockPos pos, BlockPos neighborPos) {
    Direction facing = state.get(FACING);
    Direction opposite = facing.getOpposite();

    Direction left = facing.rotateClockwise(Axis.Y);
    Direction right = facing.rotateCounterclockwise(Axis.Y);

    if (direction != opposite && neighborState.getBlock() == ProjectorMod.DISPLAY) {
      if (direction == right) {
        state = state.with(Display.RIGHT, true);
      } else if (direction == left) {
        state = state.with(Display.LEFT, true);
      } else if (direction == Direction.UP) {
        state = state.with(Display.TOP, true);
      } else if (direction == Direction.DOWN) {
        state = state.with(Display.BOTTOM, true);
      }
    } else {
      if (direction == right) {
        state = state.with(Display.RIGHT, false);
      } else if (direction == left) {
        state = state.with(Display.LEFT, false);
      } else if (direction == Direction.UP) {
        state = state.with(Display.TOP, false);
      } else if (direction == Direction.DOWN) {
        state = state.with(Display.BOTTOM, false);
      }
    }

    return state;
  }

  @Override
  public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
    Direction facing = state.get(FACING);
    switch (facing) {
      case NORTH:
        return VoxelShapes.cuboid(0f, 0f, 0.5f, 1f, 1f, 1f);
      case SOUTH:
        return VoxelShapes.cuboid(0f, 0f, 0f, 1f, 1f, 0.5f);
      case EAST:
        return VoxelShapes.cuboid(0f, 0f, 0f, 0.5f, 1f, 1f);
      case WEST:
        return VoxelShapes.cuboid(0.5f, 0f, 0f, 1f, 1f, 1f);
      default:
        return VoxelShapes.cuboid(0f, 0f, 0f, 1f, 0.5f, 1f);
    }
  }

  @Override
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
      BlockHitResult hit) {

    DisplayEntity blockEntity = (DisplayEntity) world.getBlockEntity(pos);
    if (world.isClient()) {
      if (player.getMainHandStack().isOf(ProjectorMod.REMOTE)) {
        // only show the menu for the main picture block
        if (!blockEntity.detectNearbyBlocks().mainBlock) {
          return ActionResult.SUCCESS;
        }
        // call the event that is handled in the client mod
        DisplayClicked.EVENT.invoker().interact(blockEntity, () -> {
          blockEntity.markForUpdate();
        });

        Remote remote = (Remote) player.getMainHandStack().getItem();
        remote.link(player.getMainHandStack(), pos);
        player.sendMessage(Text.literal("Linked the remote to " + pos.toShortString()), false);

        return ActionResult.SUCCESS;
      }

      return ActionResult.SUCCESS;
    }

    return ActionResult.PASS;
  }

  @Override
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }

  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new DisplayEntity(pos, state, this);
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
    stateManager.add(FACING);
    stateManager.add(TOP);
    stateManager.add(BOTTOM);
    stateManager.add(LEFT);
    stateManager.add(RIGHT);
  }

  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
  }
}