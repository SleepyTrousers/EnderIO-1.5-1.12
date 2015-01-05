package crazypants.enderio.machine.weather;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractPowerConsumerEntity;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.network.PacketHandler;

public class TileWeatherObelisk extends AbstractPowerConsumerEntity {

  enum Task {
    // TODO placeholder power vals
    CLEAR(1000, new ItemStack(Items.cake)) {
      @Override
      void complete(TileEntity te) {
        rain(te, false);
        thunder(te, false);
      }
    },
    RAIN(1000, new ItemStack(Items.water_bucket)) {
      @Override
      void complete(TileEntity te) {
        rain(te, true);
        thunder(te, false);
      }
    },
    STORM(1000, new ItemStack(Items.lava_bucket)) {
      @Override
      void complete(TileEntity te) {
        rain(te, true);
        thunder(te, true);
      }
    };

    final int power;
    final ItemStack requiredItem;

    Task(int power, ItemStack requiredItem) {
      this.power = power;
      this.requiredItem = requiredItem;
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
  }

  private int powerUsed = 0;
  private Task activeTask = null;

  private boolean canBeActive = true;

  public TileWeatherObelisk() {
    super(new SlotDefinition(1, 0, 1));
  }
  
  public void updateEntity() {
    super.updateEntity();
    if (worldObj.isRemote) {
      if (activeParticleTicks > 0) {
        spawnParticle();
      }
    }
  }
  
  @SideOnly(Side.CLIENT)
  private void spawnParticle() {
    EntitySmokeFX fx = new EntitySmokeFX(getWorldObj(), xCoord + 0.5, yCoord + 0.3, zCoord + 0.5, 0, 0.2, 0);
    fx.setRBGColorF(0.95f, 0.50f, 1f);
    Minecraft.getMinecraft().effectRenderer.addEffect(fx);
    activeParticleTicks--;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockWeatherObelisk.unlocalisedName;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    for (Task task : Task.values()) {
      if (task.isValid(itemstack)) {
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
    return isActive() ? (float) powerUsed / (float) activeTask.power : 0;
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
          powerUsed = 0;
          activeTask = null;
          res = true;
        }
      }
    }

    return true;
  }

  public void startTask(int taskid) {
    powerUsed = 0;
    Task task = Task.values()[taskid];
    if (task.isValid(inventory[slotDefinition.minInputSlot])) {
      activeTask = task;
      decrStackSize(slotDefinition.minInputSlot, 1);
    }
  }

  private int activeParticleTicks = 0;
  public void activateClientParticles() {
    activeParticleTicks = 10;
  }
}
