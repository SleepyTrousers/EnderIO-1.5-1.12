package crazypants.enderio.block;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.util.ClientUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class BlockDarkSteelLadder extends BlockLadder implements IResourceTooltipProvider, IHaveRenderers {

  public static BlockDarkSteelLadder create() {
    BlockDarkSteelLadder res = new BlockDarkSteelLadder();
    res.init();
    return res;
  }

  protected BlockDarkSteelLadder() {
    super();
    setUnlocalizedName(ModObject.blockDarkSteelLadder.getUnlocalisedName());
    setRegistryName(ModObject.blockDarkSteelLadder.getUnlocalisedName());
    setSoundType(SoundType.METAL);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setHardness(0.4F);
  }

  protected void init() {
    GameRegistry.register(this);
    ItemBlock ib = new ItemBlock(this);
    ib.setRegistryName(ModObject.blockDarkSteelLadder.getUnlocalisedName());
    GameRegistry.register(ib);
  }

  @Override
  public Material getMaterial(IBlockState state) {
    return Material.IRON;
  }

  @Override
  public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {    
    if (entity.onGround || entity.isCollidedVertically) {
      return;
    }

    if (entity.motionY >= 0.1) {
      entity.setPosition(entity.posX, entity.posY + Config.darkSteelLadderSpeedBoost, entity.posZ);
    } else if (entity.motionY <= -0.1) {
      Block blockUnder = entity.world
          .getBlockState(new BlockPos(MathHelper.floor(entity.posX), MathHelper.floor(entity.posY) - 3, MathHelper.floor(entity.posZ)))
          .getBlock();
      if (blockUnder == Blocks.AIR || blockUnder == this) { // prevent clipping into block
        entity.setPosition(entity.posX, entity.posY - Config.darkSteelLadderSpeedBoost, entity.posZ);
      }
    }
  }

  @Override
  public String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    ClientUtil.registerDefaultItemRenderer(ModObject.blockDarkSteelLadder);
  }

}
