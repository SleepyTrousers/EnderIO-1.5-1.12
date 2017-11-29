package crazypants.enderio.machines.machine.obelisk;

import com.enderio.core.client.render.BoundingBox;

import crazypants.enderio.capacitor.ICapacitorKey;

import crazypants.enderio.init.ModObject;
import crazypants.enderio.machine.baselegacy.AbstractPowerConsumerEntity;
import crazypants.enderio.machine.baselegacy.SlotDefinition;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.render.ranged.IRanged;
import crazypants.enderio.render.ranged.RangeParticle;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Storable
public abstract class AbstractRangedTileEntity extends AbstractPowerConsumerEntity implements IRanged {
  
  private boolean showingRange;
  
  public AbstractRangedTileEntity(SlotDefinition slotDefinition, ICapacitorKey maxEnergyRecieved, ICapacitorKey maxEnergyStored, ICapacitorKey maxEnergyUsed) {
    super(slotDefinition, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
  }

  public AbstractRangedTileEntity(SlotDefinition slotDefinition, MachineObject modObject) {
    super(slotDefinition, modObject);
  }
  
  @Override
  @SideOnly(Side.CLIENT)
  public boolean isShowingRange() {
    return showingRange;
  }

  @SideOnly(Side.CLIENT)
  public void setShowRange(boolean showRange) {
    if(showingRange == showRange) {
      return;
    }
    showingRange = showRange;
    if(showingRange) {
      Minecraft.getMinecraft().effectRenderer.addEffect(new RangeParticle<AbstractRangedTileEntity>(this));
    }
  }
  
  @Override
  public BoundingBox getBounds() {
    return new BoundingBox(getPos()).expand(getRange() / 2d);
  }

  abstract protected float getRange();

}
