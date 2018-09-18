package crazypants.enderio.machines.machine.generator.stirling;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.IProgressTile;

import crazypants.enderio.api.capacitor.ICapacitorData;
import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.machine.baselegacy.AbstractGeneratorEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.power.PowerDistributor;
import crazypants.enderio.machines.capability.LegacyStirlingWrapper;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.network.PacketHandler;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import static crazypants.enderio.machines.capacitor.CapacitorKey.SIMPLE_STIRLING_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.SIMPLE_STIRLING_POWER_GEN;
import static crazypants.enderio.machines.capacitor.CapacitorKey.SIMPLE_STIRLING_POWER_LOSS;
import static crazypants.enderio.machines.capacitor.CapacitorKey.STIRLING_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.STIRLING_POWER_EFFICIENCY;
import static crazypants.enderio.machines.capacitor.CapacitorKey.STIRLING_POWER_GEN;
import static crazypants.enderio.machines.capacitor.CapacitorKey.STIRLING_POWER_LOSS;

@Storable
public class TileStirlingGenerator extends AbstractGeneratorEntity implements IProgressTile, IPaintable.IPaintableTileEntity {

  public static class Simple extends TileStirlingGenerator {

    public Simple() {
      super(new SlotDefinition(1, 0, 0), SIMPLE_STIRLING_POWER_BUFFER, SIMPLE_STIRLING_POWER_GEN);
      setEnergyLoss(SIMPLE_STIRLING_POWER_LOSS);
    }

    @Override
    public boolean isMachineItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
      return super.isMachineItemValidForSlot(i, itemstack) && Prep.isInvalid(itemstack.getItem().getContainerItem(itemstack));
    }

  }

  private static final @Nonnull ResourceLocation SOUND = new ResourceLocation(EnderIO.DOMAIN, "generator.stirling");

  /** How many ticks left until the item is burnt. */
  @Store
  public int burnTime = 0;
  @Store
  public int totalBurnTime;
  @Store
  public boolean isLavaFired;

  private PowerDistributor powerDis;

  public TileStirlingGenerator() {
    super(new SlotDefinition(1, 0, 1), STIRLING_POWER_BUFFER, STIRLING_POWER_GEN);
    setEnergyLoss(STIRLING_POWER_LOSS);
  }

  protected TileStirlingGenerator(@Nonnull SlotDefinition slotDefinition, @Nonnull ICapacitorKey maxEnergyStored, @Nonnull ICapacitorKey maxEnergyUsed) {
    super(slotDefinition, maxEnergyStored, maxEnergyUsed);
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineObject.block_stirling_generator.getUnlocalisedName();
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
    return TileEntityFurnace.isItemFuel(itemstack);
  }

  @Override
  public boolean isActive() {
    return burnTime > 0;
  }

  @Override
  public float getProgress() {
    if (burnTime <= 0) {
      return -1;
    }
    return (float) burnTime / (float) totalBurnTime;
  }

  @Override
  public void setProgress(float progress) {
    burnTime = (int) (totalBurnTime * progress);
  }

  @Override
  public @Nonnull TileEntity getTileEntity() {
    return this;
  }

  @Override
  public ResourceLocation getSound() {
    return SOUND;
  }

  @Override
  public void onNeighborBlockChange(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos posIn, @Nonnull Block blockIn,
      @Nonnull BlockPos fromPos) {
    super.onNeighborBlockChange(state, worldIn, posIn, blockIn, fromPos);
    if (powerDis != null) {
      powerDis.neighboursChanged();
    }
  }

  public static int getBurnTimeGeneric(@Nonnull ItemStack item) {
    return TileEntityFurnace.getItemBurnTime(item);
  }
  
  public static int getBurnTime(@Nonnull ItemStack item, @Nonnull ICapacitorKey maxUsage, @Nonnull ICapacitorData data) {
    float base = (getBurnTimeGeneric(item) / (maxUsage.get(data) / maxUsage.getDefaultFloat())) * getBurnEfficiency(data);
    if (item.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
      // Lava and other fluid buckets are nerfed, prefer combustion engine for those
      base /= 5;
    }
    // The vanilla burn time results in 24,000FE for a piece of coal at 15FE/t output.
    // So we hardcode 15 as a baseline to keep that density consistent
    return Math.round(base /= maxUsage.getDefaultFloat() / 15);
  }

  public int getBurnTime(@Nonnull ItemStack item) {
    return getBurnTime(item, maxEnergyUsed, getCapacitorData());
  }
  
  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    boolean needsUpdate = false;
    boolean sendBurnTimePacket = false;

    if (burnTime > 0) {
      if (getEnergyStored() < getMaxEnergyStored()) {
        setEnergyStored(getEnergyStored() + getPowerUsePerTick());
      }
      burnTime--;
      sendBurnTimePacket = shouldDoWorkThisTick(20, -1) || burnTime == 0;
    }

    transmitEnergy();

    if (redstoneCheck && burnTime <= 0 && getEnergyStored() < getMaxEnergyStored()) {
      final ItemStack fuelStack = inventory[0];
      if (fuelStack != null && Prep.isValid(fuelStack)) {
        burnTime = getBurnTime(fuelStack);
        if (burnTime > 0) {
          totalBurnTime = burnTime;
          isLavaFired = fuelStack.getItem() == Items.LAVA_BUCKET;
          ItemStack containedItem = fuelStack.getItem().getContainerItem(fuelStack.splitStack(1));
          if (Prep.isValid(containedItem)) {
            if (Prep.isInvalid(fuelStack)) {
              inventory[0] = containedItem;
            } else {
              world.spawnEntity(new EntityItem(world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, containedItem));
            }
          }
          markDirty();
          needsUpdate = true;
        }
      }
    }

    if (!needsUpdate && sendBurnTimePacket) {
      PacketHandler.sendToAllAround(new PacketBurnTime(this), this);
    }

    return needsUpdate;
  }

  public static float getEnergyMultiplier(@Nonnull ICapacitorData capacitorType) {
    return STIRLING_POWER_GEN.get(capacitorType) / STIRLING_POWER_GEN.getDefaultFloat();
  }

  public static float getBurnEfficiency(@Nullable ICapacitorData data) {
    return (data == null ? STIRLING_POWER_EFFICIENCY.getDefaultFloat() : STIRLING_POWER_EFFICIENCY.getFloat(data));
  }

  public float getBurnEfficiency() {
    return getBurnEfficiency(getCapacitorData());
  }

  private boolean transmitEnergy() {
    if (powerDis == null) {
      powerDis = new PowerDistributor(getPos());
    }
    int canTransmit = Math.min(getEnergyStored(), getPowerUsePerTick() * 2);
    if (canTransmit <= 0) {
      return false;
    }
    int transmitted = powerDis.transmitEnergy(world, canTransmit);
    setEnergyStored(getEnergyStored() - transmitted);
    return transmitted > 0;
  }

  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing1) {
    if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing1 != null) {
      return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new LegacyStirlingWrapper(this, facing1));
    }
    return super.getCapability(capability, facing1);
  }

  @Override
  protected boolean hasStuffToPush() {
    final ItemStack itemStack = inventory[0];
    return itemStack != null && Prep.isValid(itemStack) && !TileEntityFurnace.isItemFuel(itemStack);
  }

}
