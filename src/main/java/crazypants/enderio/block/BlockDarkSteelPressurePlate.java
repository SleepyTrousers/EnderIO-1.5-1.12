package crazypants.enderio.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.gui.IResourceTooltipProvider;

public class BlockDarkSteelPressurePlate extends BlockPressurePlate implements IResourceTooltipProvider {

  public static BlockDarkSteelPressurePlate create() {
    BlockDarkSteelPressurePlate res = new BlockDarkSteelPressurePlate();
    res.init();
    return res;
  }
  
  public BlockDarkSteelPressurePlate() {
    super(ModObject.blockDarkSteelPressurePlate.unlocalisedName, Material.iron, Sensitivity.players);
    setBlockName(ModObject.blockDarkSteelPressurePlate.unlocalisedName);
    setStepSound(Block.soundTypeMetal);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setHardness(2.0f);
  }
  
  protected void init() {
    GameRegistry.registerBlock(this, ModObject.blockDarkSteelPressurePlate.unlocalisedName);
  }
  
  @Override
  public void registerBlockIcons(IIconRegister iIconRegister) {
    blockIcon = iIconRegister.registerIcon("enderio:" + ModObject.blockDarkSteelPressurePlate.unlocalisedName);
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }

}
