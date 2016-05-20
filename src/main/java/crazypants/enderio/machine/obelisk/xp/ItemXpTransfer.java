package crazypants.enderio.machine.obelisk.xp;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Vector3d;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.xp.XpUtil;

public class ItemXpTransfer extends Item implements IResourceTooltipProvider {

  public static ItemXpTransfer create() {
    PacketHandler.INSTANCE.registerMessage(PacketXpTransferEffects.class, PacketXpTransferEffects.class, PacketHandler.nextID(), Side.CLIENT);

    ItemXpTransfer result = new ItemXpTransfer();
    result.init();
    return result;
  }

  protected ItemXpTransfer() {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemXpTransfer.getUnlocalisedName());
    setRegistryName(ModObject.itemXpTransfer.getUnlocalisedName());
    setMaxStackSize(1);
    setHasSubtypes(true);
  }

  @Override
  public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {    
    return onActivated(player, world, pos, side);
  }

  public static EnumActionResult onActivated(EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
    if (world.isRemote) {
      return EnumActionResult.FAIL;
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
    TileEntity te = world.getTileEntity(pos);
    if (!(te instanceof IFluidHandler)) {
      return false;
    }
    IFluidHandler fh = (IFluidHandler) te;
    if (!fh.canDrain(side, Fluids.fluidXpJuice)) {
      return false;
    }
    int currentXP = XpUtil.getPlayerXP(player);
    int nextLevelXP = XpUtil.getExperienceForLevel(player.experienceLevel + 1);
    int requiredXP = nextLevelXP - currentXP;

    int fluidVolume = XpUtil.experienceToLiquid(requiredXP);
    FluidStack fs = new FluidStack(Fluids.fluidXpJuice, fluidVolume);
    FluidStack res = fh.drain(side, fs, true);
    if (res == null || res.amount <= 0) {
      return false;
    }

    int xpToGive = XpUtil.liquidToExperience(res.amount);
    player.addExperience(xpToGive);

    return true;
  }

  public static boolean tranferFromPlayerToBlock(EntityPlayer player, World world, BlockPos pos, EnumFacing side) {

    if (player.experienceTotal <= 0) {
      return false;
    }
    TileEntity te = world.getTileEntity(pos);
    if (!(te instanceof IFluidHandler)) {
      return false;
    }
    IFluidHandler fh = (IFluidHandler) te;

    if (!fh.canFill(side, Fluids.fluidXpJuice)) {
      return false;
    }

    int fluidVolume = XpUtil.experienceToLiquid(XpUtil.getPlayerXP(player));
    FluidStack fs = new FluidStack(Fluids.fluidXpJuice, fluidVolume);
    int takenVolume = fh.fill(side, fs, true);
    if (takenVolume <= 0) {
      return false;
    }
    int xpToTake = XpUtil.liquidToExperience(takenVolume);
    XpUtil.addPlayerXP(player, -xpToTake);
    return true;
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
