package crazypants.enderio.machine.invpanel.remote;

import crazypants.enderio.Log;
import crazypants.enderio.config.Config;
import crazypants.enderio.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public enum ItemRemoteInvAccessType {
  BASIC(0, "basic", 5 * 16, false, true),
  ADVANCED(1, "advanced", -1, false, true),
  ENDER(2, "ender", -1, true, true);

  private final String nameSuffix;
  private final int range;
  private final boolean interdimensional;
  private final int fluidCapacity;
  private final int mbPerOpen;
  private final int rfCapacity;
  private final int rfPerTick;
  private final boolean visible;
  private final Fluid fluidType;

  private ItemRemoteInvAccessType(int index, String nameSuffix, int range, boolean interdimensional, boolean visible) {
    this.nameSuffix = nameSuffix;
    this.range = range;
    this.interdimensional = interdimensional;
    this.fluidCapacity = Config.remoteInventoryMBCapacity[index];
    this.rfCapacity = Config.remoteInventoryRFCapacity[index];
    this.mbPerOpen = Config.remoteInventoryMBPerOpen[index];
    this.rfPerTick = Config.remoteInventoryRFPerTick[index];
    this.visible = visible;
    
    Fluid fluid = null;
    String fluidName = Config.remoteInventoryFluidTypes[index];
    if(fluidName != null) {
      fluid = FluidRegistry.getFluid(fluidName);
      if(fluid == null) {
        Log.warn("ItemRemoteInvAccessType: Could not find fluid '" + fluidName + "' using default fluid " + Fluids.fluidNutrientDistillation);
      }
    }
    if(fluid == null) {
      fluid = Fluids.fluidNutrientDistillation;
    }
    fluidType = fluid;
    
  }

  public int toMetadata() {
    return ordinal();
  }

  public static ItemRemoteInvAccessType fromMetadata(int meta) {
    return values()[meta >= 0 && meta < values().length ? meta : 0];
  }

  public static ItemRemoteInvAccessType fromStack(ItemStack stack) {
    return fromMetadata(stack != null ? stack.getMetadata() : 0);
  }

  public boolean inRange(int dim0, int x0, int y0, int z0, int dim1, int x1, int y1, int z1) {
    if (!interdimensional && dim0 != dim1) {
      return false;
    }
    if (range < 0) {
      return true;
    }
    if (Math.abs(x0 - x1) > range || Math.abs(y0 - y1) > range || Math.abs(z0 - z1) > range) {
      return false;
    }
    return true;
  }

  public String getNameSuffix() {
    return nameSuffix;
  }

  public String getUnlocalizedName(String basename) {
    return basename + getNameSuffix();
  }

  public int getRange() {
    return range;
  }

  public boolean isInterdimensional() {
    return interdimensional;
  }

  public int getRfCapacity() {
    return rfCapacity;
  }
  
  public int getFluidCapacity() {
    return fluidCapacity;
  }

  public Fluid getFluidType() {
    return fluidType;
  }
  
  public boolean isVisible() {
    return visible;
  }

  public int getMbPerOpen() {
    return mbPerOpen;
  }

  public int getRfPerTick() {
    return rfPerTick;
  }

}