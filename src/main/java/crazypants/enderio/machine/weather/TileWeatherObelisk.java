package crazypants.enderio.machine.weather;

import java.awt.Color;

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
    CLEAR(1000, new ItemStack(Items.cake), Color.YELLOW) {
      @Override
      void complete(TileEntity te) {
        rain(te, false);
        thunder(te, false);
      }
    },
    RAIN(1000, new ItemStack(Items.water_bucket), new Color(120, 120, 255)) {
      @Override
      void complete(TileEntity te) {
        rain(te, true);
        thunder(te, false);
      }
    },
    STORM(1000, new ItemStack(Items.lava_bucket), Color.DARK_GRAY) {
      @Override
      void complete(TileEntity te) {
        rain(te, true);
        thunder(te, true);
      }
    };

    final int power;
    final ItemStack requiredItem;
    final Color color;

    Task(int power, ItemStack requiredItem, Color color) {
      this.power = power;
      this.requiredItem = requiredItem;
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
  }

  int powerUsed = 0;
  Task activeTask = null;

  private Color particleColor;
  private boolean canBeActive = true;

  public TileWeatherObelisk() {
    super(new SlotDefinition(1, 0, 0));
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
    for (Task task : Task.values()) {
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
      Task task = Task.values()[taskid];
      if(task.isValid(inventory[slotDefinition.minInputSlot])) {
        activeTask = task;
        decrStackSize(slotDefinition.minInputSlot, 1);
      }
    } else if(activeTask != null && taskid == -1) {
      activeTask = null;
      powerUsed = 0;
      PacketHandler.INSTANCE.sendToDimension(new PacketActivateWeather(this, null), worldObj.provider.dimensionId);
    }
  }

  private int activeParticleTicks = 0;

  public void activateClientParticles(Task task) {
    activeParticleTicks = 20;
    particleColor = task.color;
  }
}
