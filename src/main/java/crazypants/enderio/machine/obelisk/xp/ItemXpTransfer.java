package crazypants.enderio.machine.obelisk.xp;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.fluid.FluidWrapper;
import com.enderio.core.common.fluid.IFluidWrapper;
import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Vector3d;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.machine.MachineObject;
import crazypants.enderio.fluid.Fluids;
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
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemXpTransfer extends Item implements IResourceTooltipProvider {

  public static ItemXpTransfer create() {
    PacketHandler.INSTANCE.registerMessage(PacketXpTransferEffects.class, PacketXpTransferEffects.class, PacketHandler.nextID(), Side.CLIENT);

    ItemXpTransfer result = new ItemXpTransfer();
    result.init();
    return result;
  }

  protected ItemXpTransfer() {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    setUnlocalizedName(MachineObject.itemXpTransfer.getUnlocalisedName());
    setRegistryName(MachineObject.itemXpTransfer.getUnlocalisedName());
    setMaxStackSize(1);
    setHasSubtypes(true);
  }

  @Override
  public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {    
    return onActivated(player, world, pos, side);
  }

  public static EnumActionResult onActivated(EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
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

  public static void sendXPUpdate(EntityPlayer player, World world, BlockPos pos, boolean swing) {
    Vector3d look = Util.getLookVecEio(player);
    double xP = player.posX + look.x;
    double yP = player.posY + 1.5;
    double zP = player.posZ + look.z;
    PacketHandler.INSTANCE.sendTo(new PacketXpTransferEffects(swing, xP, yP, zP), (EntityPlayerMP) player);
    world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F,
        0.5F * ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.8F), false);
  }

  public static boolean tranferFromBlockToPlayer(EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
    IFluidWrapper wrapper = FluidWrapper.wrap(world, pos, side);
    if (wrapper != null) {
      FluidStack availableFluid = wrapper.getAvailableFluid();
      if (availableFluid != null && availableFluid.getFluid() == Fluids.fluidXpJuice && availableFluid.amount > 0) {
        int currentXP = XpUtil.getPlayerXP(player);
        int nextLevelXP = XpUtil.getExperienceForLevel(player.experienceLevel + 1);
        int requiredXP = nextLevelXP - currentXP;
        int fluidVolume = XpUtil.experienceToLiquid(requiredXP);
        FluidStack fs = new FluidStack(Fluids.fluidXpJuice, fluidVolume);
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

  public static boolean tranferFromPlayerToBlock(EntityPlayer player, World world, BlockPos pos, EnumFacing side) {

    if (player.experienceTotal <= 0) {
      return false;
    }

    IFluidWrapper wrapper = FluidWrapper.wrap(world, pos, side);
    if (wrapper != null) {
      int fluidVolume = XpUtil.experienceToLiquid(XpUtil.getPlayerXP(player));
      FluidStack fs = new FluidStack(Fluids.fluidXpJuice, fluidVolume);
      int takenVolume = wrapper.fill(fs);
      if (takenVolume > 0) {
        int xpToTake = XpUtil.liquidToExperience(takenVolume);
        XpUtil.addPlayerXP(player, -xpToTake);
        return true;
      }
    }
    return false;
  }

  protected void init() {
    GameRegistry.register(this);
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    return getUnlocalizedName();
  }

  
  
  @Override
  public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {   
    return false;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean isFull3D() {
    return true;
  }

}
