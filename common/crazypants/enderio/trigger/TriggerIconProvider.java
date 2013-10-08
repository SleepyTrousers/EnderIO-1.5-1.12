package crazypants.enderio.trigger;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import buildcraft.api.core.IIconProvider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.render.IconUtil;

public class TriggerIconProvider implements IIconProvider {

  public static Icon[] ICONS = new Icon[3];

  public static TriggerIconProvider instance = new TriggerIconProvider();

  @SideOnly(Side.CLIENT)
  public static void initIcons() {
    IconUtil.addIconProvider(new IconUtil.IIconProvider() {

      @Override
      public void registerIcons(IconRegister register) {
        instance.registerIcons(register);
      }

      @Override
      public int getTextureType() {
        return 0;
      }

    });
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Icon getIcon(int index) {
    return ICONS[index];
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IconRegister iconRegistry) {
    ICONS[0] = iconRegistry.registerIcon("enderio:triggers/noEnergy");
    ICONS[1] = iconRegistry.registerIcon("enderio:triggers/hasEnergy");
    ICONS[2] = iconRegistry.registerIcon("enderio:triggers/fullEnergy");
  }

}
