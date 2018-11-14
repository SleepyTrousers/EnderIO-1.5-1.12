package crazypants.enderio.endergy.conduit;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.IconUtil;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.conduit.IConduitTexture;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.conduits.conduit.ItemConduitSubtype;
import crazypants.enderio.conduits.conduit.power.IPowerConduit;
import crazypants.enderio.conduits.conduit.power.IPowerConduitData;
import crazypants.enderio.conduits.render.ConduitTexture;
import crazypants.enderio.conduits.render.ConduitTextureWrapper;
import crazypants.enderio.endergy.config.EndergyConfig;
import crazypants.enderio.endergy.init.EndergyObject;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class EndergyPowerConduitData implements IPowerConduitData {

  private static final int OFFSET = 10;

  /**
   * To add more conduit types:
   * 
   * <ol>
   * <li>Add a new postfix to POSTFIX
   * <li>Add a new config key to {@link EndergyConfig#maxIO}
   * <li>Add new lang key
   * <li>Add new textures
   * <li>Add new item model
   * </ol>
   * 
   */

  private static class Data {
    final @Nonnull String postfix;
    final int file, idx;

    Data(@Nonnull String postfix, int file, int idx) {
      this.postfix = postfix;
      this.file = file;
      this.idx = idx;
    };

    @Nonnull
    ConduitTexture corefile(@Nonnull String key) {
      // return new ConduitTexture(TextureRegistry.registerTexture(key + postfix), ConduitTexture.CORE);
      return new ConduitTexture(TextureRegistry.registerTexture(key + "_endergy_" + file), coreidx());
    }

    private @Nonnull Vector4f coreidx() {
      switch (idx) {
      case 0:
        return ConduitTexture.CORE0;
      case 1:
        return ConduitTexture.CORE1;
      case 2:
        return ConduitTexture.CORE2;
      case 3:
        return ConduitTexture.CORE3;
      default:
        return ConduitTexture.CORE;
      }
    }

    @Nonnull
    ConduitTexture armfile(@Nonnull String key) {
      return new ConduitTexture(TextureRegistry.registerTexture(key + "_endergy_" + file), idx);
    }

    @Override
    public String toString() {
      return postfix;
    }
  }

  static final Data[] POSTFIX = new Data[] { //
      new Data("_cobble", 0, 0), new Data("_iron", 0, 1), new Data("_alu", 0, 2), new Data("_gold", 0, 3), //
      new Data("_copper", 1, 0), new Data("_silver", 1, 1), new Data("_electrum", 1, 2), new Data("_energetic_silver", 1, 3), //
      new Data("_crystalline", 2, 0), new Data("_pink_slime", 2, 1), new Data("_melodic", 2, 2), new Data("_stellar", 2, 3) };

  static {
    for (int i = 0; i < POSTFIX.length; i++) {
      IPowerConduitData.Registry
          .register(new EndergyPowerConduitData(i, POSTFIX[i].armfile(IPowerConduit.ICON_KEY), POSTFIX[i].corefile(IPowerConduit.ICON_CORE_KEY)));
    }
  }

  static int damage2id(int dmg) {
    return dmg + OFFSET;
  }

  static ItemConduitSubtype[] createSubTypes(IModObject modObject) {
    ItemConduitSubtype[] types = new ItemConduitSubtype[POSTFIX.length];
    for (int i = 0; i < POSTFIX.length; i++) {
      types[i] = new ItemConduitSubtype(modObject.getUnlocalisedName() + POSTFIX[i], modObject.getRegistryName().toString() + POSTFIX[i]);
    }
    return types;
  }

  private final int id;
  private final @Nonnull IConduitTexture icon, core;

  public EndergyPowerConduitData(int id, @Nonnull IConduitTexture icon, @Nonnull IConduitTexture core) {
    this.id = OFFSET + id;
    this.icon = icon;
    this.core = core;
  }

  @Override
  public int getID() {
    return id;
  }

  @Override
  public @Nonnull ItemStack createItemStackForSubtype() {
    return new ItemStack(EndergyObject.itemEndergyConduit.getItemNN(), 1, getIndex());
  }

  @Override
  public int getMaxEnergyIO() {
    return EndergyConfig.maxIO.get(getIndex()).get();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IConduitTexture getTextureForState(@Nonnull CollidableComponent component) {
    if (component.isCore()) {
      return core;
    }
    if (IPowerConduit.COLOR_CONTROLLER_ID.equals(component.data)) {
      return new ConduitTextureWrapper(IconUtil.instance.whiteTexture);
    }
    return icon;
  }

  private int getIndex() {
    return getID() - OFFSET;
  }

}