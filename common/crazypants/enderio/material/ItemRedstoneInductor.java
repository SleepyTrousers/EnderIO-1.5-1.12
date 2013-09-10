package crazypants.enderio.material;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;

public class ItemRedstoneInductor extends Item {

  public static ItemRedstoneInductor create() {
    ItemRedstoneInductor result = new ItemRedstoneInductor();
    result.init();
    return result;
  }

  protected ItemRedstoneInductor() {
    super(ModObject.itemRedstoneInductor.id);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemRedstoneInductor.unlocalisedName);
    setMaxStackSize(64);
  }

  protected void init() {
    LanguageRegistry.addName(this, ModObject.itemRedstoneInductor.name);
    GameRegistry.registerItem(this, ModObject.itemRedstoneInductor.unlocalisedName);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IconRegister iconRegister) {
    itemIcon = iconRegister.registerIcon("enderio:redstoneInductor");
  }

}
