package crazypants.enderio.base.block.darksteel.ladder;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.render.IDefaultRenderers;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDarkSteelLadder extends BlockLadder implements IResourceTooltipProvider, IDefaultRenderers, IModObject.WithBlockItem {

  public static BlockDarkSteelLadder create(@Nonnull IModObject modObject) {
    return new BlockDarkSteelLadder(modObject);
  }

  protected BlockDarkSteelLadder(@Nonnull IModObject modObject) {
    modObject.apply(this);
    setSoundType(SoundType.METAL);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setHardness(0.4F);
  }

  @Override
  public @Nonnull Material getMaterial(@Nonnull IBlockState state) {
    return Material.IRON;
  }

  private static class Data {
    boolean isMoving = false;
  }

  private static ThreadLocal<Data> DATA = new ThreadLocal<Data>() {
    @Override
    protected Data initialValue() {
      return new Data();
    }
  };

  @Override
  public void onEntityCollidedWithBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entity) {
    if (!(entity instanceof EntityPlayer) || entity.onGround || entity.collidedVertically || DATA.get().isMoving) {
      return;
    }

    try {
      DATA.get().isMoving = true;
      if (entity.motionY >= 0.1) {
        entity.move(MoverType.SELF, 0, Config.darkSteelLadderSpeedBoost, 0);
      } else if (entity.motionY <= -0.1) {
        entity.move(MoverType.SELF, 0, -Config.darkSteelLadderSpeedBoost, 0);
      }
    } finally {
      DATA.get().isMoving = false;
    }
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName();
  }

}
