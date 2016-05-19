package crazypants.enderio.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;

public class BlockDarkSteelLadder extends BlockLadder implements IResourceTooltipProvider {
  
  public static BlockDarkSteelLadder create() {
    BlockDarkSteelLadder res = new BlockDarkSteelLadder();
    res.init();
    return res;
  }

  protected BlockDarkSteelLadder() {       
    setUnlocalizedName(ModObject.blockDarkSteelLadder.getUnlocalisedName());
    setStepSound(Block.soundTypeMetal);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setHardness(0.4F);
  }
  
  protected void init() {
    GameRegistry.registerBlock(this, ModObject.blockDarkSteelLadder.getUnlocalisedName());    
  }
  
  @Override
  public Material getMaterial() {
    return Material.iron;
  }
  
  @Override
  public void onEntityCollidedWithBlock(World world, BlockPos pos, Entity entity) {
    if (entity.onGround || entity.isCollidedVertically) {
      return;
    }
    
    if(entity.motionY >= 0.1) {
      entity.setPosition(entity.posX, entity.posY + Config.darkSteelLadderSpeedBoost, entity.posZ);
    } else if(entity.motionY <= -0.1) {
      Block blockUnder = entity.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY) - 3, MathHelper.floor_double(entity.posZ))).getBlock();
      if (blockUnder == null || blockUnder == this) { // prevent clipping into block
        entity.setPosition(entity.posX, entity.posY - Config.darkSteelLadderSpeedBoost, entity.posZ);
      }
    }
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }

}
