package crazypants.enderio.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import crazypants.enderio.BlockEio;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.gui.IResourceTooltipProvider;
import crazypants.enderio.machine.painter.TileEntityPaintedBlock;
import crazypants.enderio.material.BlockFusedQuartz;

public class BlockReinforcedObsidian extends BlockEio implements IResourceTooltipProvider {

  public static BlockReinforcedObsidian create() {
    BlockReinforcedObsidian result = new BlockReinforcedObsidian();
    result.init();
    return result;
  }

  private BlockReinforcedObsidian() {
    super(ModObject.blockReinforcedObsidian.unlocalisedName, null, Material.rock);    
    setHardness(50.0F);
    setResistance(2000.0F);
    setStepSound(soundTypePiston);
    
    if(!Config.reinforcedObsidianEnabled) {
      setCreativeTab(null);
    }
  }
  
  public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
    return false;
  }
  
  @Override
  public void onBlockExploded(World world, int x, int y, int z, Explosion explosion) {
    ;
  }
  
  @Override
  public boolean canDropFromExplosion(Explosion p_149659_1_) {
    return false;
  }
  
  public MapColor getMapColor(int p_149728_1_) {
    return MapColor.obsidianColor;
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }
}
