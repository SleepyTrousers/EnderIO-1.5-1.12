package crazypants.enderio.material;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;

public class ItemMJReader extends Item {

  public static ItemMJReader create() {
    ItemMJReader result = new ItemMJReader();
    result.init();
    return result;
  }

  protected ItemMJReader() {
    super(ModObject.itemMJReader.id);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemMJReader.unlocalisedName);
    setMaxStackSize(64);
  }

  protected void init() {
    LanguageRegistry.addName(this, ModObject.itemMJReader.name);
    GameRegistry.registerItem(this, ModObject.itemMJReader.unlocalisedName);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IconRegister iconRegister) {
    itemIcon = iconRegister.registerIcon("enderio:mJReader");
  }

}
