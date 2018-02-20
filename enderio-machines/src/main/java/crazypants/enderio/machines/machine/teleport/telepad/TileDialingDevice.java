package crazypants.enderio.machines.machine.teleport.telepad;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.BlockEnder;
import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.InventorySlot;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.blockiterators.CubicBlockIterator;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.capacitor.DefaultCapacitorData;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.item.coordselector.TelepadTarget;
import crazypants.enderio.base.item.coordselector.TelepadTarget.TelepadTargetArrayListHandler;
import crazypants.enderio.base.machine.base.te.AbstractCapabilityPoweredMachineEntity;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.render.ranged.IRanged;
import crazypants.enderio.base.render.ranged.RangeParticle;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketTargetList;
import crazypants.enderio.machines.network.PacketHandler;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Storable
public class TileDialingDevice extends AbstractCapabilityPoweredMachineEntity implements IRanged {

  @Store
  private DialerFacing dialerFacing;

  @Store(handler = TelepadTargetArrayListHandler.class)
  private final ArrayList<TelepadTarget> targets = new ArrayList<TelepadTarget>();

  public TileDialingDevice() {
    super(CapacitorKey.DIALING_DEVICE_POWER_INTAKE, CapacitorKey.DIALING_DEVICE_POWER_BUFFER, CapacitorKey.DIALING_DEVICE_POWER_USE);

    getInventory().add(Type.INPUT, "INPUT", new InventorySlot(TileTelePad.LOCATION_PRINTOUTS, 1));
    getInventory().add(Type.OUTPUT, "OUTPUT", new InventorySlot(1));
    getInventory().getSlot(CAPSLOT).set(new ItemStack(ModObject.itemBasicCapacitor.getItemNN(), 1, DefaultCapacitorData.ENDER_CAPACITOR.ordinal()));
  }

  @Override
  public boolean processTasks(boolean redstoneCheck) {
    getEnergy().useEnergy();

    if (!getInventory().getSlot("INPUT").isEmpty() && getInventory().getSlot("OUTPUT").isEmpty()
        && getEnergy().useEnergy(CapacitorKey.DIALING_DEVICE_POWER_USE_PAPER)) {
      ItemStack stack = getInventory().getSlot("INPUT").get();
      TelepadTarget newTarg = TelepadTarget.readFromNBT(stack);
      if (newTarg != null && !targets.contains(newTarg)) {
        addTarget(newTarg);
        PacketHandler.sendToAllAround(new PacketTargetList(this, newTarg, true), this);
      }
      getInventory().getSlot("INPUT").clear();
      getInventory().getSlot("OUTPUT").set(stack);
    }

    return false;
  }

  public void addTarget(TelepadTarget newTarg) {
    if (newTarg == null) {
      return;
    }
    targets.add(newTarg);
    markDirty();
  }

  public void removeTarget(TelepadTarget target) {
    if (target == null) {
      return;
    }
    targets.remove(target);
    markDirty();
  }

  public ArrayList<TelepadTarget> getTargets() {
    return targets;
  }

  public void setTargets(Collection<TelepadTarget> t) {
    targets.clear();
    if (t != null) {
      targets.addAll(t);
    }
  }

  @Override
  public @Nonnull BlockPos getLocation() {
    return getPos();
  }

  public @Nonnull DialerFacing getDialerFacing() {
    return dialerFacing != null ? dialerFacing : DialerFacing.DOWN_TONORTH;
  }

  public void setDialerFacing(DialerFacing facing) {
    this.dialerFacing = facing;
    markDirty();
  }

  public @Nullable TileTelePad findTelepad() {
    for (BlockPos check : new CubicBlockIterator(getBounds())) {
      TileTelePad result = BlockEnder.getAnyTileEntitySafe(getWorld(), NullHelper.first(check, pos), TileTelePad.class);
      if (result != null) {
        return result.getMaster();
      }
    }
    return null;
  }

  // RANGE

  private boolean showingRange;
  public static final @Nonnull Vector4f RANGE_COLOR = new Vector4f(0x22 / 255f, 0x75 / 255f, 0x81 / 255f, 0.4f);

  @SideOnly(Side.CLIENT)
  public void setShowRange(boolean showRange) {
    if (showingRange == showRange) {
      return;
    }
    showingRange = showRange;
    if (showingRange) {
      Minecraft.getMinecraft().effectRenderer.addEffect(new RangeParticle<>(this, RANGE_COLOR));
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean isShowingRange() {
    return showingRange;
  }

  @Override
  @Nonnull
  public BoundingBox getBounds() {
    EnumFacing forward = getDialerFacing().getInputSide();
    EnumFacing up;
    EnumFacing side;
    if (forward.getFrontOffsetY() == 0) {
      up = EnumFacing.UP;
      side = forward.rotateY();
    } else { // look along y
      up = EnumFacing.NORTH;
      side = EnumFacing.EAST;
    }

    int range = 4;
    BlockPos checkMin = pos.offset(forward, 0 + 1).offset(side, 0 - range).offset(up, 0 - range);
    BlockPos checkMax = pos.offset(forward, (range * 2 - 1) + 1).offset(side, (range * 2 - 1) - range).offset(up, (range * 2 - 1) - range);

    return new BoundingBox(checkMin, checkMax);
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  public boolean supportsMode(@Nullable EnumFacing faceHit, @Nullable IoMode mode) {
    return mode == IoMode.NONE;
  }

  @Override
  protected void onAfterNbtRead() {
    getInventory().getSlot(CAPSLOT).set(new ItemStack(ModObject.itemBasicCapacitor.getItemNN(), 1, DefaultCapacitorData.ENDER_CAPACITOR.ordinal()));
    super.onAfterNbtRead();
  }

}
