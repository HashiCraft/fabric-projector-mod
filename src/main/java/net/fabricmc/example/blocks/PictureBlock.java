package net.fabricmc.example.blocks;

import net.fabricmc.example.ui.PictureBlockScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.example.ui.PictureBlockGui;
import net.fabricmc.example.events.PictureDownloadedCallback;
import net.fabricmc.example.events.PictureScreenSaveCallback;
import net.fabricmc.example.networking.Channels;
import net.fabricmc.example.networking.PictureData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.network.PacketByteBuf;
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

    PictureBlockEntity blockEntity = (PictureBlockEntity) world.getBlockEntity(pos);

    if (world.isClient()) {
      registerEvents(player);

      PictureBlockGui gui = new PictureBlockGui();
      PictureBlockScreen screen = new PictureBlockScreen(gui);
      MinecraftClient.getInstance().setScreen(screen);

      // set the default state
      gui.setURLs(blockEntity);

      // FileDownloader dl = FileDownloader.getInstance();
      // dl.Download("https://www.datocms-assets.com/2885/1571764238-me.jpg");

      // PictureBlockEntity blockEntity = (PictureBlockEntity)
      // world.getBlockEntity(pos);

      // if (player.isInSneakingPose()) {
      // blockEntity.previousPicture();
      // player.sendMessage(new LiteralText("Previous!"), false);
      // return ActionResult.SUCCESS;
      // }

      // blockEntity.nextPicture();
      // player.sendMessage(new LiteralText("Next!"), false);
    }
    return ActionResult.SUCCESS;
  }

  private Boolean eventsRegistered = false;

  private void registerEvents(PlayerEntity player) {
    if (eventsRegistered) {
      return;
    }

    eventsRegistered = true;

    PictureDownloadedCallback.EVENT.register((url, identifier) -> {
      System.out.println("Download callback " + identifier.toString());

      // update the state
      // blockEntity.setIdentifier(url, identifier);

      return ActionResult.PASS;
    });

    PictureScreenSaveCallback.EVENT.register((urls, blockEntity) -> {
      System.out.println("URLs saved");

      // clear any existing pictures
      blockEntity.clearPictures();

      // add the new URLs
      for (String url : urls) {
        if (!url.isEmpty()) {
          blockEntity.addPicture(0, 0, url);
        }
      }

      // send the data to the sever so that it can be written to other players
      PictureData data = new PictureData(blockEntity.getPictures(), blockEntity.getPos());
      PacketByteBuf buf = PacketByteBufs.create();
      buf.writeByteArray(data.toBytes());

      ClientPlayNetworking.send(Channels.UPDATE_PICTURES, buf);

      return ActionResult.PASS;
    });
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
    stateManager.add(SLIDE);
  }

  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    return getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
  }
}