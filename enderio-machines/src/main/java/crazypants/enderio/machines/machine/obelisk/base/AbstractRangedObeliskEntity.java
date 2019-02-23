package crazypants.enderio.machines.machine.obelisk.base;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.BoundingBox;

import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.machine.baselegacy.AbstractPowerConsumerEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.render.ranged.IRanged;
import crazypants.enderio.base.render.ranged.RangeParticle;
import crazypants.enderio.machines.machine.obelisk.inhibitor.BlockInhibitorObelisk;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Storable
public abstract class AbstractRangedObeliskEntity extends AbstractPowerConsumerEntity implements IRanged {

  protected boolean showingRange;

  public AbstractRangedObeliskEntity(@Nonnull SlotDefinition slotDefinition, @Nonnull ICapacitorKey maxEnergyRecieved, @Nonnull ICapacitorKey maxEnergyStored,
      @Nonnull ICapacitorKey maxEnergyUsed) {
    super(slotDefinition, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean isShowingRange() {
    return showingRange;
  }

  @SideOnly(Side.CLIENT)
  public void setShowRange(boolean showRange) {
    if (showingRange == showRange) {
      return;
    }
    showingRange = showRange;
    if (showingRange) {
      Minecraft.getMinecraft().effectRenderer.addEffect(new RangeParticle<AbstractRangedObeliskEntity>(this));
    }
  }

  @Override
  public @Nonnull BoundingBox getBounds() {
    return new BoundingBox(getPos()).expand(getRange() / 2d);
  }

  abstract public float getRange();

  /**
   * Checks if this obelisk can work or is missing something. If it returns false, the {@link GuiRangedObelisk} will complain about missing Soul Vials and the
   * obelisk should not consume power.
   * <p>
   * Note: This actually belong on {@link AbstractMobObeliskEntity}, but because the {@link BlockInhibitorObelisk} re-uses the same GUI it is here.
   * 
   * @return <code>true</code> is the obelisk can do stuff.
   */
  abstract public boolean canWork();

  abstract protected @Nonnull String getDocumentationPage();
}
