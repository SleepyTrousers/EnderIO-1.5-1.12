package crazypants.enderio.material;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;

public class BlockMachineChassi extends Block {

	  public static BlockMachineChassi create() {
		  BlockMachineChassi result = new BlockMachineChassi();
		    result.init();
		    return result;
  }

	  private BlockMachineChassi() {
		    super(ModObject.blockMachineChassi.id, Material.iron);
		    setHardness(0.5F);
		    setStepSound(Block.soundStoneFootstep);
		    setUnlocalizedName(ModObject.blockMachineChassi.unlocalisedName);
		    setCreativeTab(EnderIOTab.tabEnderIO);
  }

	  
  private void init() {
	LanguageRegistry.addName(this, ModObject.blockMachineChassi.name);
	GameRegistry.registerBlock(this, ModObject.blockMachineChassi.unlocalisedName);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IconRegister iconRegister) {
  blockIcon = iconRegister.registerIcon("enderio:machineChassi");
  }
}
