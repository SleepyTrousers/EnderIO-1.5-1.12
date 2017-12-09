package crazypants.enderio.base.conduit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.IConduitBundle.FacadeRenderState;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.paint.YetaUtil;
import crazypants.enderio.base.sound.IModSound;
import crazypants.enderio.base.sound.SoundHelper;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.base.init.ModObject.itemConduitProbe;

public class ConduitUtil {

  public static final Random RANDOM = new Random();

  public static boolean forceSkylightRecalculation(@Nonnull World world, int xCoord, int yCoord, int zCoord) {
    return forceSkylightRecalculation(world, new BlockPos(xCoord, yCoord, zCoord));
  }

  public static boolean forceSkylightRecalculation(@Nonnull World world, @Nonnull BlockPos pos) {
    int height = world.getHeight(pos).getY();
    if (height <= pos.getY()) {
      for (int i = 1; i < 12; i++) {
        final BlockPos offset = pos.offset(EnumFacing.UP, i);
        if (world.isAirBlock(offset)) {
          // We need to force the re-lighting of the column due to a change
          // in the light reaching below the block from the sky. To avoid
          // modifying core classes to expose this functionality I am just
          // placing then breaking
          // a block above this one to force the check

          world.setBlockState(offset, Blocks.STONE.getDefaultState(), 3);
          world.setBlockToAir(offset);

          return true;
        }
      }
    }
    return false;
  }

  @SideOnly(Side.CLIENT)
  public static FacadeRenderState getRequiredFacadeRenderState(@Nonnull IConduitBundle bundle, @Nonnull EntityPlayer player) {
    if (!bundle.hasFacade()) {
      return FacadeRenderState.NONE;
    }
    if (YetaUtil.isFacadeHidden(bundle, player)) {
      return FacadeRenderState.WIRE_FRAME;
    }
    return FacadeRenderState.FULL;
  }

  public static boolean isConduitEquipped(@Nullable EntityPlayer player) {
    return isConduitEquipped(player, EnumHand.MAIN_HAND);
  }

  public static boolean isConduitEquipped(@Nullable EntityPlayer player, @Nonnull EnumHand hand) {
    player = player == null ? EnderIO.proxy.getClientPlayer() : player;
    if (player == null) {
      return false;
    }
    ItemStack equipped = player.getHeldItem(hand);
    return equipped.getItem() instanceof IConduitItem;
  }

  public static boolean isProbeEquipped(@Nullable EntityPlayer player, @Nonnull EnumHand hand) {
    player = player == null ? EnderIO.proxy.getClientPlayer() : player;
    if (player == null) {
      return false;
    }
    ItemStack equipped = player.getHeldItem(hand);
    return equipped.getItem() == itemConduitProbe.getItem();
  }

  public static <T extends IConduit> T getConduit(@Nonnull World world, int x, int y, int z, @Nonnull Class<T> type) {
    return getConduit(world, new BlockPos(x, y, z), type);
  }

  public static <T extends IConduit> T getConduit(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Class<T> type) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof IConduitBundle) {
      IConduitBundle con = (IConduitBundle) te;
      return con.getConduit(type);
    }
    return null;
  }

  public static <T extends IConduit> T getConduit(@Nonnull World world, @Nonnull TileEntity te, @Nonnull EnumFacing dir, @Nonnull Class<T> type) {
    return ConduitUtil.getConduit(world, te.getPos().getX() + dir.getFrontOffsetX(), te.getPos().getY() + dir.getFrontOffsetY(),
        te.getPos().getZ() + dir.getFrontOffsetZ(), type);
  }

  public static <T extends IConduit> Collection<T> getConnectedConduits(@Nonnull World world, int x, int y, int z, @Nonnull Class<T> type) {
    return getConnectedConduits(world, new BlockPos(x, y, z), type);
  }

  public static <T extends IConduit> Collection<T> getConnectedConduits(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Class<T> type) {
    TileEntity te = world.getTileEntity(pos);
    if (!(te instanceof IConduitBundle)) {
      return Collections.emptyList();
    }
    List<T> result = new ArrayList<T>();
    IConduitBundle root = (IConduitBundle) te;
    T con = root.getConduit(type);
    if (con != null) {
      for (EnumFacing dir : con.getConduitConnections()) {
        T connected = dir == null ? null : getConduit(world, root.getEntity(), dir, type);
        if (connected != null) {
          result.add(connected);
        }

      }
    }
    return result;
  }

  public static void writeToNBT(IConduit conduit, @Nonnull NBTTagCompound conduitRoot) {
    if (conduit == null) {
      conduitRoot.setString("UUID", UUID.nameUUIDFromBytes("null".getBytes()).toString());
    } else {
      conduitRoot.setString("UUID", ConduitRegistry.getInstanceUUID(conduit).toString());
      conduit.writeToNBT(conduitRoot);
    }
  }

  public static IConduit readConduitFromNBT(@Nonnull NBTTagCompound conduitRoot) {
    if (conduitRoot.hasKey("UUID")) {
      String UUIDString = conduitRoot.getString("UUID");
      IConduit result = ConduitRegistry.getInstance(UUID.fromString(UUIDString));
      if (result != null) {
        result.readFromNBT(conduitRoot);
      }
      return result;
    }

    // legacy NBT
    String typeName = conduitRoot.getString("conduitType");
    NBTTagCompound conduitBody = conduitRoot.getCompoundTag("conduit");
    if (typeName.isEmpty() || conduitBody.hasNoTags()) {
      return null;
    }
    IConduit result;
    try {
      result = (IConduit) Class.forName(typeName).newInstance();
    } catch (Exception e) {
      throw new RuntimeException("Could not create an instance of the conduit with name: " + typeName, e);
    }
    result.readFromNBT(conduitBody);
    return result;

  }

  public static boolean isRedstoneControlModeMet(@Nonnull IConduit conduit, @Nonnull RedstoneControlMode mode, @Nonnull DyeColor col) {

    if (mode == RedstoneControlMode.IGNORE) {
      return true;
    } else if (mode == RedstoneControlMode.NEVER) {
      return false;
    }

    int signalStrength = conduit.getBundle().getInternalRedstoneSignalForColor(col);
    if (signalStrength < RedstoneControlMode.MIN_ON_LEVEL && DyeColor.RED == col) {
      signalStrength = Math.max(signalStrength, conduit.getExternalRedstoneLevel());
    }
    return RedstoneControlMode.isConditionMet(mode, signalStrength);
  }

  public static int isBlockIndirectlyGettingPoweredIfLoaded(@Nonnull World world, @Nonnull BlockPos pos) {
    int i = 0;

    NNIterator<EnumFacing> iterator = NNList.FACING.iterator();
    while (iterator.hasNext()) {
      EnumFacing enumfacing = iterator.next();
      final BlockPos offset = pos.offset(enumfacing);
      if (world.isBlockLoaded(offset)) {
        int j = world.getRedstonePower(offset, enumfacing);

        if (j >= 15) {
          return 15;
        }

        if (j > i) {
          i = j;
        }
      }
    }

    return i;
  }

  public static boolean isFluidValid(FluidStack fluidStack) {
    if (fluidStack != null) {
      String name = FluidRegistry.getFluidName(fluidStack);
      if (name != null && !name.trim().isEmpty()) {
        return true;
      }
    }
    return false;
  }

  public static void openConduitGui(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) {
    openConduitGui(world, pos.getX(), pos.getY(), pos.getZ(), player);
  }

  public static void openConduitGui(@Nonnull World world, int x, int y, int z, @Nonnull EntityPlayer player) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if (!(te instanceof IConduitBundle)) {
      return;
    }
    IConduitBundle cb = (IConduitBundle) te;
    Set<EnumFacing> cons = new HashSet<EnumFacing>();
    boolean hasInsulated = false;
    for (IConduit con : cb.getConduits()) {
      cons.addAll(con.getExternalConnections());
      if (ConduitRegistry.get(con).canConnectToAnything()) {
        hasInsulated = true;
      }
    }
    if (cons.isEmpty() && !hasInsulated) {
      return;
    }
    if (cons.size() == 1) {
      EnumFacing facing = cons.iterator().next();
      if (facing != null) {
        ConduitRegistry.getConduitModObjectNN().openGui(world, new BlockPos(x, y, z), player, facing, 0);
      }
      return;
    }
    ConduitRegistry.getConduitModObjectNN().openClientGui(world, new BlockPos(x, y, z), player, null, 0);
  }

  public static void playBreakSound(@Nonnull SoundType snd, @Nonnull World world, int x, int y, int z) {
    SoundHelper.playSound(world, new BlockPos(x, y, z), new Sound(snd.getBreakSound()), (snd.getVolume() + 1.0F) / 2.0F, snd.getPitch() * 0.8F);
  }

  public static void playHitSound(@Nonnull SoundType snd, @Nonnull World world, int x, int y, int z) {
    SoundHelper.playSound(world, new BlockPos(x, y, z), new Sound(snd.getHitSound()), (snd.getVolume() + 1.0F) / 2.0F, snd.getPitch() * 0.8F);
  }

  public static void playStepSound(@Nonnull SoundType snd, @Nonnull World world, int x, int y, int z) {
    SoundHelper.playSound(world, new BlockPos(x, y, z), new Sound(snd.getStepSound()), (snd.getVolume() + 1.0F) / 2.0F, snd.getPitch() * 0.8F);
  }

  public static void playPlaceSound(@Nonnull SoundType snd, @Nonnull World world, int x, int y, int z) {
    SoundHelper.playSound(world, new BlockPos(x, y, z), new Sound(snd.getPlaceSound()), (snd.getVolume() + 1.0F) / 2.0F, snd.getPitch() * 0.8F);
  }

  private static class Sound implements IModSound {

    private final @Nonnull SoundEvent event;

    public Sound(@Nonnull SoundEvent event) {
      this.event = event;
    }

    @Override
    public boolean isValid() {
      return true;
    }

    @Override
    public @Nonnull SoundEvent getSoundEvent() {
      return event;
    }

    @Override
    public @Nonnull SoundCategory getSoundCategory() {
      return SoundCategory.BLOCKS;
    }

  }
}
