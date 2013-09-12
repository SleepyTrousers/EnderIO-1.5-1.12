package crazypants.render;

import java.util.ArrayList;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

public class IconUtil {

  public static interface IIconProvider {

    public void registerIcons(IconRegister register);

    /** 0 = terrain.png, 1 = items.png */
    public int getTextureType();
  }

  static {
    MinecraftForge.EVENT_BUS.register(new IconUtil());
  }

  private static ArrayList<IIconProvider> iconProviders = new ArrayList<IIconProvider>();

  public static void addIconProvider(IIconProvider registrar) {
    iconProviders.add(registrar);
  }

  @ForgeSubscribe
  public void onIconLoad(TextureStitchEvent.Pre event) {
    for (IIconProvider reg : iconProviders) {
      if (reg.getTextureType() == event.map.textureType) {
        reg.registerIcons(event.map);
      }
    }
  }
  
  
  public static Icon getIconForItem(int itemId, int meta) {
    if(itemId < 0 || itemId >= Item.itemsList.length) {
      return null;
    }    
    Item item = Item.itemsList[itemId];
    if(item == null) {
      return null;
    }
    return item.getIconFromDamage(meta);
  }

}
