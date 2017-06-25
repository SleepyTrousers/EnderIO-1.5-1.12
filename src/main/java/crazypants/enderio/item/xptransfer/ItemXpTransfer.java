package crazypants.enderio.item.xptransfer;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.fluid.FluidWrapper;
import com.enderio.core.common.fluid.IFluidWrapper;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.init.IModObject;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.xp.XpUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemXpTransfer extends Item implements IResourceTooltipProvider {

  public static ItemXpTransfer create(@Nonnull IModObject modObject) {
    return new ItemXpTransfer(modObject);
  }

  protected ItemXpTransfer(@Nonnull IModObject modObject) {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    setMaxStackSize(1);
    setHasSubtypes(true);
  }

  @Override
  public @Nonnull EnumActionResult onItemUseFirst(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side,
      float hitX, float hitY, float hitZ, @Nonnull EnumHand hand) {
    if (world.isRemote) {
      return EnumActionResult.PASS;
    }
    boolean res;
    boolean swing = false;
    if (player.isSneaking()) {
      res = tranferFromPlayerToBlock(player, world, pos, side);
      swing = res;
    } else {
      res = tranferFromBlockToPlayer(player, world, pos, side);
    }
    if (res) {
      sendXPUpdate(player, world, pos, swing);
      return EnumActionResult.SUCCESS;
    }
    return EnumActionResult.PASS;
  }

  public static void sendXPUpdate(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, boolean swing) {
    PacketHandler.INSTANCE.sendTo(new PacketXpTransferEffects(swing, pos), (EntityPlayerMP) player);
    world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F,
        0.5F * ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.8F), false);
  }

  public static boolean tranferFromBlockToPlayer(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
    IFluidWrapper wrapper = FluidWrapper.wrap(world, pos, side);
    if (wrapper != null) {
      FluidStack availableFluid = wrapper.getAvailableFluid();
      if (availableFluid != null && availableFluid.getFluid() == Fluids.XP_JUICE.getFluid() && availableFluid.amount > 0) {
        int currentXP = XpUtil.getPlayerXP(player);
        int nextLevelXP = XpUtil.getExperienceForLevel(player.experienceLevel + 1);
        int requiredXP = nextLevelXP - currentXP;
        int fluidVolume = XpUtil.experienceToLiquid(requiredXP);
        FluidStack fs = new FluidStack(Fluids.XP_JUICE.getFluid(), fluidVolume);
        FluidStack res = wrapper.drain(fs);
        if (res != null && res.amount > 0) {
          int xpToGive = XpUtil.liquidToExperience(res.amount);
          player.addExperience(xpToGive);
          return true;
        }
      }
    }
    return false;
  }

  public static boolean tranferFromPlayerToBlock(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {

    if (player.experienceTotal <= 0) {
      return false;
    }

    IFluidWrapper wrapper = FluidWrapper.wrap(world, pos, side);
    if (wrapper != null) {
      int fluidVolume = XpUtil.experienceToLiquid(XpUtil.getPlayerXP(player));
      FluidStack fs = new FluidStack(Fluids.XP_JUICE.getFluid(), fluidVolume);
      int takenVolume = wrapper.fill(fs);
      if (takenVolume > 0) {
        int xpToTake = XpUtil.liquidToExperience(takenVolume);
        XpUtil.addPlayerXP(player, -xpToTake);
        return true;
      }
    }
    return false;
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack stack) {
    return getUnlocalizedName();
  }

  @Override
  public boolean doesSneakBypassUse(@Nonnull ItemStack stack, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) {
    return false;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean isFull3D() {
    return true;
  }

}
