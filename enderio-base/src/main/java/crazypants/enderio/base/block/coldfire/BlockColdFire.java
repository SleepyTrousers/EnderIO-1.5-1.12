package crazypants.enderio.base.block.coldfire;

import java.util.Random;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.render.IDefaultRenderers;
import crazypants.enderio.util.Prep;
import net.minecraft.block.BlockFire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class BlockColdFire extends BlockFire implements IDefaultRenderers {

  public static BlockColdFire create(@Nonnull IModObject modObject) {
    return new BlockColdFire(modObject);
  }

  private BlockColdFire(@Nonnull IModObject modObject) {
    modObject.apply(this);
    setTickRandomly(false);
    setHardness(0.0F);
    setLightLevel(1.0F);
  }

  @Override
  public void updateTick(@Nonnull World p_updateTick_1_, @Nonnull BlockPos p_updateTick_2_, @Nonnull IBlockState p_updateTick_3_,
      @Nonnull Random p_updateTick_4_) {
  }

  @SubscribeEvent(priority = EventPriority.LOWEST) // allow others to cancel this
  public static void onClick(@Nonnull PlayerInteractEvent.LeftClickBlock event) {
    final World world = event.getWorld();
    if (world != null && !world.isRemote && event.getUseBlock() != Result.DENY) {
      final EntityPlayer player = event.getEntityPlayer();
      if (player != null && !player.isSpectator() && !world.isAirBlock(event.getPos())) {
        final EnumFacing face = NullHelper.first(event.getFace(), EnumFacing.DOWN);
        final BlockPos pos = event.getPos().offset(face);
        if (world.getBlockState(pos).getBlock() instanceof BlockColdFire && player.canPlayerEdit(pos, face, Prep.getEmpty())) {
          world.playEvent(null, 1009, pos, 0);
          world.setBlockToAir(pos);
        }
      }
    }
  }

}
