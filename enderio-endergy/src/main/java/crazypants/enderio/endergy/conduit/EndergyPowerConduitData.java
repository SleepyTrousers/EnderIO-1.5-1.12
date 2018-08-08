package crazypants.enderio.endergy.conduit;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.IconUtil;

import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;
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

  static final String[] POSTFIX = new String[] { "_cobble", "_iron", "_alu", "_gold", "_copper", "_silver", "_electrum", "_crystalline", "_melodic",
      "_stellar" };

  static final Map<String, TextureSupplier> ICONS = new HashMap<>();

  static {
    for (int i = 0; i < POSTFIX.length; i++) {
      IPowerConduitData.Registry.register(new EndergyPowerConduitData(i));
      ICONS.put(IPowerConduit.ICON_KEY + POSTFIX[i], TextureRegistry.registerTexture(IPowerConduit.ICON_KEY + POSTFIX[i]));
      ICONS.put(IPowerConduit.ICON_KEY_INPUT + POSTFIX[i], TextureRegistry.registerTexture(IPowerConduit.ICON_KEY_INPUT + POSTFIX[i]));
      ICONS.put(IPowerConduit.ICON_KEY_OUTPUT + POSTFIX[i], TextureRegistry.registerTexture(IPowerConduit.ICON_KEY_OUTPUT + POSTFIX[i]));
      ICONS.put(IPowerConduit.ICON_CORE_KEY + POSTFIX[i], TextureRegistry.registerTexture(IPowerConduit.ICON_CORE_KEY + POSTFIX[i]));
    }
  }

  static int damage2id(int dmg) {
    return dmg + OFFSET;
  }

  private final int id;

  public EndergyPowerConduitData(int id) {
    this.id = OFFSET + id;
  }

  @Override
  public int getID() {
    return id;
  }

  @Override
  public @Nonnull ItemStack createItemStackForSubtype() {
    return new ItemStack(EndergyObject.itemEndergyConduit.getItemNN(), 1, getID() - OFFSET);
  }

  @Override
  public int getMaxEnergyIO() {
    return EndergyConfig.maxIO.get(getID() - OFFSET).get();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public TextureAtlasSprite getTextureForState(@Nonnull CollidableComponent component) {
    if (component.dir == null) {
      return ICONS.get(IPowerConduit.ICON_CORE_KEY + POSTFIX[getID() - OFFSET]).get(TextureAtlasSprite.class);
    }
    if (IPowerConduit.COLOR_CONTROLLER_ID.equals(component.data)) {
      return IconUtil.instance.whiteTexture;
    }
    return ICONS.get(IPowerConduit.ICON_KEY + POSTFIX[getID() - OFFSET]).get(TextureAtlasSprite.class);
  }

  @Override
  public TextureAtlasSprite getTextureForInputMode() {
    return ICONS.get(IPowerConduit.ICON_KEY_INPUT + POSTFIX[getID() - OFFSET]).get(TextureAtlasSprite.class);
  }

  @Override
  public TextureAtlasSprite getTextureForOutputMode() {
    return ICONS.get(IPowerConduit.ICON_KEY_OUTPUT + POSTFIX[getID() - OFFSET]).get(TextureAtlasSprite.class);
  }

}