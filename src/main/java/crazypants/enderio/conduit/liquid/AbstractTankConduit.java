package crazypants.enderio.conduit.liquid;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.ChatUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.RaytraceResult;
import crazypants.enderio.tool.ToolUtil;

public abstract class AbstractTankConduit extends AbstractLiquidConduit {

  protected ConduitTank tank = new ConduitTank(0);
  protected boolean stateDirty = false;
  protected long lastEmptyTick = 0;
  protected int numEmptyEvents = 0;
  protected boolean fluidTypeLocked = false;
  private int lastLightValue;

  @Override
  public boolean onBlockActivated(EntityPlayer player, RaytraceResult res, List<RaytraceResult> all) {
    if(player.getCurrentEquippedItem() == null) {
      return false;
    }
    AbstractTankConduitNetwork<? extends AbstractTankConduit> network = getTankNetwork();
    if(ToolUtil.isToolEquipped(player)) {

      if(!getBundle().getEntity().getWorldObj().isRemote) {

        if(res != null && res.component != null) {

          ForgeDirection connDir = res.component.dir;
          ForgeDirection faceHit = ForgeDirection.getOrientation(res.movingObjectPosition.sideHit);

          if(connDir == ForgeDirection.UNKNOWN || connDir == faceHit) {

            if(getConnectionMode(faceHit) == ConnectionMode.DISABLED) {
              setConnectionMode(faceHit, getNextConnectionMode(faceHit));
              return true;
            }

            BlockCoord loc = getLocation().getLocation(faceHit);
            ILiquidConduit n = ConduitUtil.getConduit(getBundle().getEntity().getWorldObj(), loc.x, loc.y, loc.z, ILiquidConduit.class);
            if(n == null) {
              return false;
            }
            if(!canJoinNeighbour(n)) {
              return false;
            }
            if(!(n instanceof AbstractTankConduit)) {
              return false;
            }
            AbstractTankConduit neighbour = (AbstractTankConduit) n;
            if(neighbour.getFluidType() == null || getFluidType() == null) {
              FluidStack type = getFluidType();
              type = type != null ? type : neighbour.getFluidType();
              neighbour.setFluidTypeOnNetwork(neighbour, type);
              setFluidTypeOnNetwork(this, type);
            }
            return ConduitUtil.joinConduits(this, faceHit);
          } else if(containsExternalConnection(connDir)) {
            // Toggle extraction mode
            setConnectionMode(connDir, getNextConnectionMode(connDir));
          } else if(containsConduitConnection(connDir)) {
            FluidStack curFluidType = null;
            if(getTankNetwork() != null) {
              curFluidType = getTankNetwork().getFluidType();
            }
            ConduitUtil.disconectConduits(this, connDir);
            setFluidType(curFluidType);

          }
        }
      }
      return true;

    } else if(player.getCurrentEquippedItem().getItem() == Items.bucket) {

      if(!getBundle().getEntity().getWorldObj().isRemote) {
        long curTick = getBundle().getEntity().getWorldObj().getTotalWorldTime();
        if(curTick - lastEmptyTick < 20) {
          numEmptyEvents++;
        } else {
          numEmptyEvents = 1;
        }
        lastEmptyTick = curTick;

        if(numEmptyEvents < 2) {
          if(network.fluidTypeLocked) {
            network.setFluidTypeLocked(false);
            numEmptyEvents = 0;
            ChatUtil.sendNoSpamUnloc(player, EnderIO.lang, "itemLiquidConduit.unlockedType");
          }
        } else if(network != null) {
          network.setFluidType(null);
          numEmptyEvents = 0;
        }
      }

      return true;
    } else {

      FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(player.getCurrentEquippedItem());
      if (fluid != null) {
        if (!getBundle().getEntity().getWorldObj().isRemote) {
          if (network != null
              && (network.getFluidType() == null || network.getTotalVolume() < 500 || LiquidConduitNetwork.areFluidsCompatable(getFluidType(), fluid))) {
            network.setFluidType(fluid);
            network.setFluidTypeLocked(true);
            ChatUtil.sendNoSpamUnloc(player, EnderIO.lang, "itemLiquidConduit.lockedType", " " + FluidRegistry.getFluidName(fluid));
          }
        }
        return true;
      }
    }

    return false;
  }

  void setFluidTypeLocked(boolean fluidTypeLocked) {
    if(fluidTypeLocked == this.fluidTypeLocked) {
      return;
    }

    this.fluidTypeLocked = fluidTypeLocked;
    stateDirty = true;

    //    BlockCoord l = getLocation();
    //    getBundle().getEntity().worldObj.markTileEntityChunkModified(l.x, l.y, l.z, getBundle().getEntity());
  }

  private void setFluidTypeOnNetwork(AbstractTankConduit con, FluidStack type) {
    AbstractConduitNetwork<?, ?> n = con.getNetwork();
    if(n != null) {
      AbstractTankConduitNetwork<?> network = (AbstractTankConduitNetwork<?>) n;
      network.setFluidType(type);
    }

  }

  protected abstract boolean canJoinNeighbour(ILiquidConduit n);

  public abstract AbstractTankConduitNetwork<? extends AbstractTankConduit> getTankNetwork();
  
  public void setFluidType(FluidStack liquidType) {
    if(tank.getFluid() != null && tank.getFluid().isFluidEqual(liquidType)) {
      return;
    }
    if(liquidType != null) {
      liquidType = liquidType.copy();
    } else if(tank.getFluid() == null) {
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
    if(getTankNetwork() != null) {
      result = getTankNetwork().getFluidType();
    }
    if(result == null) {
      result = tank.getFluid();
    }
    return result;
  }

  public boolean isFluidTypeLocked() {
    return fluidTypeLocked;
  }

  @Override
  public void updateEntity(World world) {
    int lightValue = getLightValue();
    if(lastLightValue != lightValue) {
      BlockCoord bc = getLocation();
      getBundle().getWorld().updateLightByType(EnumSkyBlock.Block, bc.x, bc.y, bc.z);
      lastLightValue = lightValue;
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
  public void readFromNBT(NBTTagCompound nbtRoot, short nbtVersion) {
    super.readFromNBT(nbtRoot, nbtVersion);
    updateTank();
    if(nbtRoot.hasKey("tank")) {
      FluidStack liquid = FluidStack.loadFluidStackFromNBT(nbtRoot.getCompoundTag("tank"));
      tank.setLiquid(liquid);
    } else {
      tank.setLiquid(null);
    }
    fluidTypeLocked = nbtRoot.getBoolean("fluidLocked");
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);
    FluidStack ft = getFluidType();
    if(ConduitUtil.isFluidValid(ft)) {
      updateTank();
      ft = ft.copy();
      ft.amount = tank.getFluidAmount();
      nbtRoot.setTag("tank", ft.writeToNBT(new NBTTagCompound()));
    }
    nbtRoot.setBoolean("fluidLocked", fluidTypeLocked);
  }

}
