package crazypants.enderio.conduit.liquid;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.ChatUtil;
import com.enderio.core.common.util.FluidUtil;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduitNetwork;
import crazypants.enderio.base.conduit.RaytraceResult;
import crazypants.enderio.base.tool.ToolUtil;
import crazypants.enderio.conduit.config.ConduitConfig;
import crazypants.enderio.conduit.render.BlockStateWrapperConduitBundle.ConduitCacheKey;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public abstract class AbstractTankConduit extends AbstractLiquidConduit {

  protected ConduitTank tank = new ConduitTank(0);
  protected boolean stateDirty = false;
  protected long lastEmptyTick = 0;
  protected int numEmptyEvents = 0;
  protected boolean fluidTypeLocked = false;
  private int lastLightValue;

  @Override
  public boolean onBlockActivated(@Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull RaytraceResult res, @Nonnull List<RaytraceResult> all) {
    ItemStack heldItem = player.getHeldItem(hand);
    if (heldItem.isEmpty()) {
      return false;
    }
    AbstractTankConduitNetwork<? extends AbstractTankConduit> network = getTankNetwork();
    if (ToolUtil.isToolEquipped(player, hand)) {

      if (!getBundle().getEntity().getWorld().isRemote) {

        if (res != null && res.component != null) {

          EnumFacing connDir = res.component.dir;
          EnumFacing faceHit = res.movingObjectPosition.sideHit;

          if (connDir == null || connDir == faceHit) {

            if (getConnectionMode(faceHit) == ConnectionMode.DISABLED) {
              setConnectionMode(faceHit, getNextConnectionMode(faceHit));
              return true;
            }

            BlockPos pos = getBundle().getLocation().offset(faceHit);
            ILiquidConduit liquidConduit = ConduitUtil.getConduit(getBundle().getEntity().getWorld(), pos.getX(), pos.getY(), pos.getZ(), ILiquidConduit.class);
            if (liquidConduit == null) {
              return false;
            }
            if (!canJoinNeighbour(liquidConduit)) {
              return false;
            }
            if (!(liquidConduit instanceof AbstractTankConduit)) {
              return false;
            }
            AbstractTankConduit neighbour = (AbstractTankConduit) liquidConduit;
            if (neighbour.getFluidType() == null || getFluidType() == null) {
              FluidStack type = getFluidType();
              type = type != null ? type : neighbour.getFluidType();
              neighbour.setFluidTypeOnNetwork(neighbour, type);
              setFluidTypeOnNetwork(this, type);
            }
            return ConduitUtil.connectConduits(this, faceHit);
          } else if (containsExternalConnection(connDir)) {
            // Toggle extraction mode
            setConnectionMode(connDir, getNextConnectionMode(connDir));
          } else if (containsConduitConnection(connDir)) {
            FluidStack curFluidType = null;
            if (getTankNetwork() != null) {
              curFluidType = getTankNetwork().getFluidType();
            }
            ConduitUtil.disconnectConduits(this, connDir);
            setFluidType(curFluidType);

          }
        }
      }
      return true;

    } else if (heldItem.getItem() == Items.BUCKET) {

      if (!getBundle().getEntity().getWorld().isRemote) {
        long curTick = getBundle().getEntity().getWorld().getTotalWorldTime();
        if (curTick - lastEmptyTick < 20) {
          numEmptyEvents++;
        } else {
          numEmptyEvents = 1;
        }
        lastEmptyTick = curTick;

        if (numEmptyEvents < 2) {
          if (network.fluidTypeLocked) {
            network.setFluidTypeLocked(false);
            numEmptyEvents = 0;
            // TODO Lang
            ChatUtil.sendNoSpamUnloc(player, EnderIO.lang, "itemLiquidConduit.unlockedType");
          }
        } else if (network != null) {
          network.setFluidType(null);
          numEmptyEvents = 0;
        }
      }

      return true;
    } else {

      FluidStack fluid = FluidUtil.getFluidTypeFromItem(heldItem);
      if (fluid != null) {
        if (!getBundle().getEntity().getWorld().isRemote) {
          if (network != null
              && (network.getFluidType() == null || network.getTotalVolume() < 500 || LiquidConduitNetwork.areFluidsCompatable(getFluidType(), fluid))) {
            network.setFluidType(fluid);
            network.setFluidTypeLocked(true);

            player.sendStatusMessage(new TextComponentTranslation(EnderIO.lang.addPrefix("itemLiquidConduit.lockedType"), fluid.getLocalizedName()), true);
          }
        }
        return true;
      }
    }

    return false;
  }

  void setFluidTypeLocked(boolean fluidTypeLocked) {
    if (fluidTypeLocked == this.fluidTypeLocked) {
      return;
    }
    this.fluidTypeLocked = fluidTypeLocked;
    stateDirty = true;
  }

  private void setFluidTypeOnNetwork(AbstractTankConduit con, FluidStack type) {
    IConduitNetwork<?, ?> n = con.getNetwork();
    if (n != null) {
      AbstractTankConduitNetwork<?> network = (AbstractTankConduitNetwork<?>) n;
      network.setFluidType(type);
    }

  }

  protected abstract boolean canJoinNeighbour(ILiquidConduit n);

  public abstract AbstractTankConduitNetwork<? extends AbstractTankConduit> getTankNetwork();

  public void setFluidType(FluidStack liquidType) {
    if (tank.getFluid() != null && tank.getFluid().isFluidEqual(liquidType)) {
      return;
    }
    if (liquidType != null) {
      liquidType = liquidType.copy();
    } else if (tank.getFluid() == null) {
      return;
    }
    tank.setLiquid(liquidType);
    stateDirty = true;
  }

  public ConduitTank getTank() {
    return tank;
  }

  public FluidStack getFluidType() {
    FluidStack result = null;
    if (getTankNetwork() != null) {
      result = getTankNetwork().getFluidType();
    }
    if (result == null) {
      result = tank.getFluid();
    }
    return result;
  }

  public boolean isFluidTypeLocked() {
    return fluidTypeLocked;
  }

  @Override
  public void updateEntity(@Nonnull World world) {
    if (ConduitConfig.dynamicLighting.get()) {
      int lightValue = getLightValue();
      if (lastLightValue != lightValue) {
        BlockPos pos = getBundle().getLocation();
        getBundle().getBundleworld().checkLightFor(EnumSkyBlock.BLOCK, pos);
        lastLightValue = lightValue;
      }
    }
    super.updateEntity(world);
  }

  @Override
  public int getLightValue() {
    FluidStack stack = getFluidType();
    return stack == null || stack.amount <= 0 ? 0 : stack.getFluid().getLuminosity(stack);
  }

  protected abstract void updateTank();

  @Override
  public void readFromNBT(@Nonnull NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);
    updateTank();
    if (nbtRoot.hasKey("tank")) {
      FluidStack liquid = FluidStack.loadFluidStackFromNBT(nbtRoot.getCompoundTag("tank"));
      tank.setLiquid(liquid);
    } else {
      tank.setLiquid(null);
    }
    fluidTypeLocked = nbtRoot.getBoolean("fluidLocked");
  }

  @Override
  public void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);
    FluidStack ft = getFluidType();
    if (ConduitUtil.isFluidValid(ft)) {
      updateTank();
      ft = ft.copy();
      ft.amount = tank.getFluidAmount();
      nbtRoot.setTag("tank", ft.writeToNBT(new NBTTagCompound()));
    }
    nbtRoot.setBoolean("fluidLocked", fluidTypeLocked);
  }

  @Override
  public void hashCodeForModelCaching(ConduitCacheKey hashCodes) {
    super.hashCodeForModelCaching(hashCodes);
    if (fluidTypeLocked) {
      hashCodes.add(1);
    }
  }

  public boolean canFill(EnumFacing side, FluidStack fluid) {
    if (getNetwork() == null || !getConnectionMode(side).acceptsInput()) {
      return false;
    }
    return canExtractFromDir(side) && LiquidConduitNetwork.areFluidsCompatable(getFluidType(), fluid);
  }

  public boolean canDrain(EnumFacing side, FluidStack fluid) {
    if (getNetwork() == null || !getConnectionMode(side).acceptsOutput()) {
      return false;
    }
    return canInputToDir(side) && LiquidConduitNetwork.areFluidsCompatable(getFluidType(), fluid);
  }

}
