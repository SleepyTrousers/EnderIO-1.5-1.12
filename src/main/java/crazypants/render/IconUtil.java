package crazypants.render;

import java.util.ArrayList;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class IconUtil {

  public static interface IIconProvider {

    public void registerIcons(IIconRegister register);

    /** 0 = terrain.png, 1 = items.png */
    public int getTextureType();
  }

  private static ArrayList<IIconProvider> iconProviders = new ArrayList<IIconProvider>();

  public static IIcon whiteTexture;
  public static IIcon blankTexture;
  public static IIcon errorTexture;

  static {
    MinecraftForge.EVENT_BUS.register(new IconUtil());
    addIconProvider(new IIconProvider() {

      @Override
      public void registerIcons(IIconRegister register) {
        whiteTexture = register.registerIcon("enderio:white");
        errorTexture = register.registerIcon("enderio:error");
        blankTexture = register.registerIcon("enderio:blank");
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
  @SubscribeEvent
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
