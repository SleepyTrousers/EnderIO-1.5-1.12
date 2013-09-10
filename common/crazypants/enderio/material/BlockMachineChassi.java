package crazypants.enderio.material;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemBlock;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;

public class BlockMachineChassi extends ItemBlock {

  public static BlockMachineChassi create() {
    BlockMachineChassi result = new BlockMachineChassi();
    result.init();
    return result;
  }

  protected BlockMachineChassi() {
    super(ModObject.blockMachineChassi.id);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.blockMachineChassi.unlocalisedName);
    setMaxStackSize(64);
  }

  protected void init() {
    LanguageRegistry.addName(this, ModObject.blockMachineChassi.name);
    GameRegistry.registerItem(this, ModObject.blockMachineChassi.unlocalisedName);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IconRegister iconRegister) {
    itemIcon = iconRegister.registerIcon("enderio:machineChassi");
  }

}
