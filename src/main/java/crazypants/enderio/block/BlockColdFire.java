package crazypants.enderio.block;

import java.util.Random;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import net.minecraft.block.BlockFire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockColdFire extends BlockFire implements IResourceTooltipProvider {

  public static BlockColdFire create() {
    BlockColdFire res = new BlockColdFire();
    res.initColdFire();
    return res;
  }

  private BlockColdFire() {
    setUnlocalizedName(ModObject.blockColdFire.getUnlocalisedName());
    setRegistryName(ModObject.blockColdFire.getUnlocalisedName());
    setCreativeTab(EnderIOTab.tabEnderIO);
    setTickRandomly(false);
    setHardness(0.0F);
    setLightLevel(1.0F);
  }

  protected void initColdFire() {// BlockFire already has a static init()
    GameRegistry.register(this);
  }

  @Override
  public void updateTick(World p_updateTick_1_, BlockPos p_updateTick_2_, IBlockState p_updateTick_3_, Random p_updateTick_4_) {
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return this.getUnlocalizedName();
  }

}
