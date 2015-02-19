package crazypants.enderio.machine.weather;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.storage.WorldInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractPowerConsumerEntity;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.ICapacitor;

public class TileWeatherObelisk extends AbstractPowerConsumerEntity {

  public enum WeatherTask {
    CLEAR(Config.weatherObeliskClearPower, Color.YELLOW) {
      @Override
      void complete(TileEntity te) {
        rain(te, false);
        thunder(te, false);
      }
    },
    RAIN(Config.weatherObeliskRainPower, new Color(120, 120, 255)) {
      @Override
      void complete(TileEntity te) {
        rain(te, true);
        thunder(te, false);
      }
    },
    STORM(Config.weatherObeliskThunderPower, Color.DARK_GRAY) {
      @Override
      void complete(TileEntity te) {
        rain(te, true);
        thunder(te, true);
      }
    };

    final int power;
    private ItemStack requiredItem;
    final Color color;

    WeatherTask(int power, Color color) {
      this.power = power;
      this.color = color;
    }

    abstract void complete(TileEntity te);

    void rain(TileEntity te, boolean state) {
      te.getWorldObj().getWorldInfo().setRaining(state);
    }

    void thunder(TileEntity te, boolean state) {
      te.getWorldObj().getWorldInfo().setThundering(state);
    }

    boolean isValid(ItemStack item) {
      return item != null && ItemStack.areItemStacksEqual(item, this.requiredItem);
    }
    
    public ItemStack requiredItem() {
      return requiredItem;
    }
    
    public void setRequiredItem(ItemStack item) {
      this.requiredItem = item;
    }
    
    public static boolean worldIsState(WeatherTask task, WorldInfo world) {
      if(world.isRaining()) {
        return world.isThundering() ? task == STORM : task == RAIN;
      }
      return task == CLEAR;
    }
  }

  int powerUsed = 0;
  int progress = 0; // client only
  WeatherTask activeTask = null;

  private Color particleColor;
  private boolean canBeActive = true;
  
  private static int biggestPowerReq = Math.max(Math.max(Config.weatherObeliskClearPower, Config.weatherObeliskThunderPower), Config.weatherObeliskRainPower);
  private static final BasicCapacitor cap = new BasicCapacitor(biggestPowerReq / 200, biggestPowerReq);
  
  public TileWeatherObelisk() {
    super(new SlotDefinition(1, 0, 0));
    setCapacitor(Capacitors.ACTIVATED_CAPACITOR);
  }

  public void updateEntity() {
    super.updateEntity();
    if(worldObj.isRemote) {
      if(activeParticleTicks > 0) {
        spawnParticle();
      }
    }
  }

  @SideOnly(Side.CLIENT)
  private void spawnParticle() {
    EntitySmokeFX fx = new EntitySmokeFX(getWorldObj(), xCoord + 0.5, yCoord + 0.3, zCoord + 0.5, 0, 0, 0);
    fx.setRBGColorF((float) particleColor.getRed() / 255f, (float) particleColor.getGreen() / 255f, (float) particleColor.getBlue() / 255f);
    fx.setVelocity(worldObj.rand.nextDouble() * 0.1 - 0.05, 0.35, worldObj.rand.nextDouble() * 0.1 - 0.05);
    Minecraft.getMinecraft().effectRenderer.addEffect(fx);
    activeParticleTicks--;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockWeatherObelisk.unlocalisedName;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    for (WeatherTask task : WeatherTask.values()) {
      if(task.isValid(itemstack)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isActive() {
    return canBeActive && activeTask != null;
  }

  @Override
  public float getProgress() {
    return isActive() ? worldObj.isRemote ? progress : (float) powerUsed / (float) activeTask.power : 0;
  }

  @Override
  public ICapacitor getCapacitor() {
    return cap;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheckPassed) {
    boolean res = false;

    if(!redstoneCheckPassed) {
      if(canBeActive) {
        canBeActive = false;
        res = true;
      }
      return res;
    } else {
      canBeActive = true;

      if(isActive()) {
        if(getEnergyStored() > getPowerUsePerTick()) {
          int remaining = activeTask.power - powerUsed;
          int toUse = Math.min(remaining, getPowerUsePerTick());
          setEnergyStored(getEnergyStored() - toUse);
          powerUsed += toUse;
          res = true;
        }

        if(powerUsed >= activeTask.power) {
          activeTask.complete(this);
          PacketHandler.INSTANCE.sendToDimension(new PacketFinishWeather(this, activeTask), worldObj.provider.dimensionId);
          startTask(-1);
          res = true;
        }
      }
    }

    return res;
  }

  public void startTask(int taskid) {
    if(activeTask == null && taskid >= 0) {
      powerUsed = 0;
      WeatherTask task = WeatherTask.values()[taskid];
      if(task.isValid(inventory[slotDefinition.minInputSlot])) {
        activeTask = task;
        decrStackSize(slotDefinition.minInputSlot, 1);
      }
    } else if(activeTask != null && taskid == -1) {
      activeTask = null;
      powerUsed = 0;
      if(!worldObj.isRemote) {
        PacketHandler.INSTANCE.sendToDimension(new PacketActivateWeather(this, null), worldObj.provider.dimensionId);
      }
    }
  }

  private int activeParticleTicks = 0;

  public void activateClientParticles(WeatherTask task) {
    activeParticleTicks = 20;
    particleColor = task.color;
  }
}
