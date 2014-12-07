package crazypants.enderio.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.gui.IResourceTooltipProvider;

public class BlockDarkSteelLadder extends BlockLadder implements IResourceTooltipProvider {
  
  public static BlockDarkSteelLadder create() {
    BlockDarkSteelLadder res = new BlockDarkSteelLadder();
    res.init();
    return res;
  }

  protected BlockDarkSteelLadder() {
    super();
    
    setBlockName(ModObject.blockDarkSteelLadder.unlocalisedName);
    setBlockTextureName(EnderIO.MODID + ":" + ModObject.blockDarkSteelLadder.unlocalisedName);
    setStepSound(Block.soundTypeMetal);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setHardness(0.4F);
  }
  
  protected void init() {
    GameRegistry.registerBlock(this, ModObject.blockDarkSteelLadder.unlocalisedName);    
  }
  
  @Override
  public Material getMaterial() {
    return Material.iron;
  }

  @Override
  public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
    if(entity.motionY >= 0.1) {
      entity.setPosition(entity.posX, entity.posY + Config.darkSteelLadderSpeedBoost, entity.posZ);
    } else if (entity.motionY <= 0.1) {
      entity.setPosition(entity.posX, entity.posY - Config.darkSteelLadderSpeedBoost, entity.posZ);
    }
  }
  
  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }
}
