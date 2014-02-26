package crazypants.render;

import cpw.mods.fml.common.Mod;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;

public class IconUtil {

  public static interface IIconProvider {

    public void registerIcons(IIconRegister register);

    /** 0 = terrain.png, 1 = items.png */
    public int getTextureType();
  }

  private static ArrayList<IIconProvider> iconProviders = new ArrayList<IIconProvider>();

  public static IIcon whiteTexture;

  static {
    MinecraftForge.EVENT_BUS.register(new IconUtil());
    addIconProvider(new IIconProvider() {

      @Override
      public void registerIcons(IIconRegister register) {
        whiteTexture = register.registerIcon("enderio:white");
      }

      @Override
      public int getTextureType() {
        return 0;
      }
    });
  }

  public static void addIconProvider(IIconProvider registrar) {
    iconProviders.add(registrar);
  }

  @Mod.EventHandler
  public void onIconLoad(TextureStitchEvent.Pre event) {
    for (IIconProvider reg : iconProviders) {
      if(reg.getTextureType() == event.map.getTextureType()) {
        reg.registerIcons(event.map);
      }
    }
  }

  public static IIcon getIconForItem(int itemId, int meta) {
    Item item = Item.getItemById(itemId);
    if(item == null) {
      return null;
    }
    return item.getIconFromDamage(meta);
  }

}
