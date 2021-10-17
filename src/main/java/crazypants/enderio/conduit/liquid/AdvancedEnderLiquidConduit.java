package crazypants.enderio.conduit.liquid;

import com.enderio.core.client.render.IconUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class AdvancedEnderLiquidConduit extends AbstractEnderLiquidConduit {

  public static final Type TYPE = Type.ADVANCED;
  public static final String ICON_KEY = "enderio:liquidConduitAdvancedEnder";
  public static final String ICON_CORE_KEY = "enderio:liquidConduitCoreAdvancedEnder";

  static IIcon iconKey;
  static IIcon iconCoreKey;

  @SideOnly(Side.CLIENT)
  public static void initIcons() {
    IconUtil.addIconProvider(new IconUtil.IIconProvider() {

      @Override
      public void registerIcons(IIconRegister register) {
        iconKey = register.registerIcon(ICON_KEY);
        iconCoreKey = register.registerIcon(ICON_CORE_KEY);
      }

      @Override
      public int getTextureType() {
        return 0;
      }

    });
  }

  @Override
  public Type getType() {
    return TYPE;
  }

  @Override
  protected IIcon getIconKey() {
    return iconKey;
  }

  @Override
  protected IIcon getIconCoreKey() {
    return iconCoreKey;
  }

  @Override
  public ItemStack createItem() {
    return new ItemStack(EnderIO.itemLiquidConduit, 1, 3);
  }
}