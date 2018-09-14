package crazypants.enderio.endergy.conduit;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.IconUtil;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.conduits.conduit.ItemConduitSubtype;
import crazypants.enderio.conduits.conduit.power.IPowerConduit;
import crazypants.enderio.conduits.conduit.power.IPowerConduitData;
import crazypants.enderio.endergy.config.EndergyConfig;
import crazypants.enderio.endergy.init.EndergyObject;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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

  static final String[] POSTFIX = new String[] { "_cobble", "_iron", "_alu", "_gold", "_copper", "_silver", "_electrum", "_energetic_silver", "_crystalline",
      "_pink_slime", "_melodic", "_stellar" };

  static {
    for (int i = 0; i < POSTFIX.length; i++) {
      IPowerConduitData.Registry.register(new EndergyPowerConduitData(i, TextureRegistry.registerTexture(IPowerConduit.ICON_KEY + POSTFIX[i]),
          TextureRegistry.registerTexture(IPowerConduit.ICON_CORE_KEY + POSTFIX[i])));
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
  private final @Nonnull TextureSupplier icon, core;

  public EndergyPowerConduitData(int id, @Nonnull TextureSupplier icon, @Nonnull TextureSupplier core) {
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
  public TextureAtlasSprite getTextureForState(@Nonnull CollidableComponent component) {
    if (component.dir == null) {
      return core.get(TextureAtlasSprite.class);
    }
    if (IPowerConduit.COLOR_CONTROLLER_ID.equals(component.data)) {
      return IconUtil.instance.whiteTexture;
    }
    return icon.get(TextureAtlasSprite.class);
  }

  private int getIndex() {
    return getID() - OFFSET;
  }

}