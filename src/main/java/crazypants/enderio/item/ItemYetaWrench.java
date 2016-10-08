package crazypants.enderio.item;

import java.util.ArrayList;
import java.util.List;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;

import buildcraft.api.tools.IToolWrench;
import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.tool.IConduitControl;
import crazypants.enderio.api.tool.ITool;
import crazypants.enderio.conduit.ConduitDisplayMode;
import crazypants.enderio.config.Config;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.IPaintable.IBlockPaintableBlock;
import crazypants.enderio.paint.PainterUtil2;
import crazypants.enderio.paint.YetaUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.ModObject.blockConduitBundle;

@Optional.InterfaceList({ @Interface(iface = "buildcraft.api.tools.IToolWrench", modid = "BuildCraftAPI|core") })
public class ItemYetaWrench extends Item implements ITool, IConduitControl, IAdvancedTooltipProvider, IToolWrench {

  public static ItemYetaWrench create() {
    if (Config.useSneakMouseWheelYetaWrench) {
      PacketHandler.INSTANCE.registerMessage(YetaWrenchPacketProcessor.class, YetaWrenchPacketProcessor.class, PacketHandler.nextID(), Side.SERVER);
    }
    ItemYetaWrench result = new ItemYetaWrench();
    GameRegistry.register(result);
    return result;
  }

  protected ItemYetaWrench() {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemYetaWrench.getUnlocalisedName());
    setRegistryName(ModObject.itemYetaWrench.getUnlocalisedName());
    setMaxStackSize(1);
  }

  @Override
  public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
   
    if (world.isRemote) {
      //If its client side we have to return pass so this method is called on server, where we need to perform the op
      return EnumActionResult.PASS;
    }
    
    final IBlockState blockState = world.getBlockState(pos);
    IBlockState bs = blockState;
    Block block = bs.getBlock();
    boolean ret = false;
    if (block != null) {
      RightClickBlock e = new RightClickBlock(player, hand, player.getHeldItem(hand), pos,side, new Vec3d(hitX, hitY, hitZ));
      if (MinecraftForge.EVENT_BUS.post(e) || e.getResult() == Result.DENY || e.getUseBlock() == Result.DENY || e.getUseItem() == Result.DENY) {
        return EnumActionResult.PASS;
      }
      if(block instanceof BlockDoor) {
        EnumDoorHalf half = bs.getValue(BlockDoor.HALF);
        if(half == EnumDoorHalf.UPPER) {
          pos = pos.down();
        }
      }
      if (!player.isSneaking() && block.rotateBlock(world, pos, side)) {
        ret = true;
      } else if (block instanceof IBlockPaintableBlock && !player.isSneaking() && !YetaUtil.shouldHeldItemHideFacades(player)) {
        IBlockState paintSource = ((IBlockPaintableBlock) block).getPaintSource(blockState, world, pos);
        if (paintSource != null) {
          final IBlockState rotatedPaintSource = PainterUtil2.rotate(paintSource);
          if (rotatedPaintSource != paintSource) {
            ((IBlockPaintableBlock) block).setPaintSource(blockState, world, pos, rotatedPaintSource);
          }
          ret = true;
        }
      }
    }
    
    //Need to catch 'shift-clicks' here and pass them on manually or an item in the off hand can eat the right click
    //so 'onBlockActivated' is never called
    if(!ret && player.isSneaking() && block instanceof BlockEio<?>) {
      BlockEio<?> beio = (BlockEio<?>)block;
      if(beio.shouldWrench(world, pos, player, side)) {
        beio.onBlockActivated(world, pos, bs, player, hand, player.getHeldItem(hand), side, hitX, hitY, hitZ);
        ret = true;
      }
    }
    if (ret) {
      player.swingArm(hand);
    }
    return ret ? EnumActionResult.SUCCESS: EnumActionResult.PASS;
  }
  
  @Override
  public ActionResult<ItemStack> onItemRightClick(ItemStack equipped, World world, EntityPlayer player, EnumHand hand) {
    if (!Config.useSneakRightClickYetaWrench) {
      return new ActionResult<ItemStack>(EnumActionResult.PASS, equipped);
    }
    if (!player.isSneaking()) {
      return new ActionResult<ItemStack>(EnumActionResult.PASS, equipped);
    }
    ConduitDisplayMode curMode = ConduitDisplayMode.getDisplayMode(equipped);
    if (curMode == null) {
      curMode = ConduitDisplayMode.ALL;
    }
    ConduitDisplayMode newMode = curMode.next();
    ConduitDisplayMode.setDisplayMode(equipped, newMode);
    return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
  }

  @Override
  public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
    IBlockState bs = player.worldObj.getBlockState(pos);
    Block block = bs.getBlock();
    if (player.isSneaking() && block == blockConduitBundle.getBlock() && player.capabilities.isCreativeMode) {
      block.onBlockClicked(player.worldObj, pos, player);
      return true;
    }
    return false;
  }

  @Override
  public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
    return !ItemStack.areItemsEqual(oldStack, newStack); // Ignore NBT
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean isFull3D() {
    return true;
  }

  @Override
  public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
    return false;
  }

  
  @Override
  public boolean canUse(ItemStack stack, EntityPlayer player, BlockPos pos) {
    return true;
  }

  @Override
  public void used(ItemStack stack, EntityPlayer player, BlockPos pos) {
  }

  @Override
  public boolean shouldHideFacades(ItemStack stack, EntityPlayer player) {
    ConduitDisplayMode curMode = ConduitDisplayMode.getDisplayMode(stack);
    return curMode != ConduitDisplayMode.NONE;
  }

  @Override
  public boolean showOverlay(ItemStack stack, EntityPlayer player) {
    return true;
  }

  /* IAdvancedTooltipProvider */

  @Override
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
  }

  @Override
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
  }

  @Override
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    ArrayList<String> tmp = new ArrayList<String>();
    SpecialTooltipHandler.addDetailedTooltipFromResources(tmp, getUnlocalizedName());
    String keyName = KeyTracker.instance.getYetaWrenchMode().getDisplayName();
    for (String line : tmp) {
      list.add(String.format(line, keyName));
    }
  }

  @Override
  @Optional.Method(modid = "BuildCraftAPI|core")
  public boolean canWrench(EntityPlayer arg0, BlockPos arg1) {
    return true;
  }

  @Override
  @Optional.Method(modid = "BuildCraftAPI|core")
  public boolean canWrench(EntityPlayer arg0, Entity arg1) {
    return false;
  }

  @Override
  @Optional.Method(modid = "BuildCraftAPI|core")
  public void wrenchUsed(EntityPlayer player, BlockPos pos) {
    used(player.getHeldItemMainhand(), player, pos);
  }

  @Override
  @Optional.Method(modid = "BuildCraftAPI|core")
  public void wrenchUsed(EntityPlayer player, Entity arg1) {
  }
}
