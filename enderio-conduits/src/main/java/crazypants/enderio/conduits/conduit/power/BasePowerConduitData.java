package crazypants.enderio.conduits.conduit.power;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.IconUtil;

import crazypants.enderio.base.conduit.IConduitTexture;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.conduits.config.ConduitConfig;
import crazypants.enderio.conduits.render.ConduitTextureWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.conduits.init.ConduitObject.item_power_conduit;

public final class BasePowerConduitData implements IPowerConduitData {

  private final int id;

  public BasePowerConduitData(int id) {
    this.id = id;
  }

  @Override
  public int getID() {
    return id;
  }

  @Override
  public @Nonnull ItemStack createItemStackForSubtype() {
    return new ItemStack(item_power_conduit.getItemNN(), 1, getID());
  }

  @Override
  public int getMaxEnergyIO() {
    switch (getID()) {
    case 1:
      return ConduitConfig.tier2_maxIO.get();
    case 2:
      return ConduitConfig.tier3_maxIO.get();
    default:
      return ConduitConfig.tier1_maxIO.get();
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IConduitTexture getTextureForState(@Nonnull CollidableComponent component) {
    if (component.isCore()) {
      return PowerConduit.ICONS.get(PowerConduit.ICON_CORE_KEY + PowerConduit.POSTFIX[getID()]);
    }
    if (PowerConduit.COLOR_CONTROLLER_ID.equals(component.data)) {
      return new ConduitTextureWrapper(IconUtil.instance.whiteTexture);
    }
    return PowerConduit.ICONS.get(PowerConduit.ICON_KEY + PowerConduit.POSTFIX[getID()]);
  }

}