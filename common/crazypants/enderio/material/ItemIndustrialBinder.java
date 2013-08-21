package crazypants.enderio.material;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;

public class ItemIndustrialBinder extends Item {

  public static ItemIndustrialBinder create() {
    ItemIndustrialBinder result = new ItemIndustrialBinder();
    result.init();
    return result;
  }

  protected ItemIndustrialBinder() {
    super(ModObject.itemIndustrialBinder.id);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemIndustrialBinder.unlocalisedName);
    setMaxStackSize(64);
  }

  protected void init() {
    LanguageRegistry.addName(this, ModObject.itemIndustrialBinder.name);
    GameRegistry.registerItem(this, ModObject.itemIndustrialBinder.unlocalisedName);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IconRegister iconRegister) {
    itemIcon = iconRegister.registerIcon("enderio:industrialBinder");
  }

}
