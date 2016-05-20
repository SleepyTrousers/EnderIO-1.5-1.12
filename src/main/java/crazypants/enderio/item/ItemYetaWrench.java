package crazypants.enderio.item;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.EnderIO;
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
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
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
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemYetaWrench extends Item implements ITool, IConduitControl, IAdvancedTooltipProvider, InvocationHandler {

  public static ItemYetaWrench create() {
    if (Config.useSneakMouseWheelYetaWrench) {
      PacketHandler.INSTANCE.registerMessage(YetaWrenchPacketProcessor.class, YetaWrenchPacketProcessor.class, PacketHandler.nextID(), Side.SERVER);
    }
    ItemYetaWrench result = new ItemYetaWrench();
    //TODO: 1.9 Mod integration
    //result = ToolUtil.addInterfaces(result);

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
    final IBlockState blockState = world.getBlockState(pos);
    IBlockState bs = blockState;
    Block block = bs.getBlock();
    boolean ret = false;
    if (block != null) {
      RightClickBlock e = new RightClickBlock(player, hand, player.getHeldItem(hand), pos,side, new Vec3d(hitX, hitY, hitZ));
//      EntityInteractSpecific e = new EntityInteractSpecific(player, hand, pos, side, world, new Vec3d(hitX, hitY, hitZ));
      if (MinecraftForge.EVENT_BUS.post(e) || e.getResult() == Result.DENY || e.getUseBlock() == Result.DENY || e.getUseItem() == Result.DENY) {
        return EnumActionResult.FAIL;
      }
      if (!player.isSneaking() && block.rotateBlock(world, pos, side)) {
        if (block == Blocks.CHEST) {
          // This works around a forge bug where you can rotate double chests to invalid directions
          TileEntityChest te = (TileEntityChest) world.getTileEntity(pos);
          if (te.adjacentChestXNeg != null || te.adjacentChestXPos != null || te.adjacentChestZNeg != null || te.adjacentChestZPos != null) {
            // Render master is always the chest to the negative direction
            TileEntityChest masterChest = te.adjacentChestXNeg == null && te.adjacentChestZNeg == null ? te : te.adjacentChestXNeg == null ? te.adjacentChestZNeg: te.adjacentChestXNeg;
            if (masterChest != te) {
              //TODO: 1.8
//              int meta = world.getBlockMetadata(masterChest.xCoord, masterChest.yCoord, masterChest.zCoord);
//              world.setBlockMetadataWithNotify(masterChest.xCoord, masterChest.yCoord, masterChest.zCoord, meta ^ 1, 3);
            } else {
              // If this is the master chest, we can just rotate twice
              block.rotateBlock(world,pos, side);
            }
          }
        }
        ret = true;
      } else if (block instanceof IBlockPaintableBlock && !player.isSneaking() && !YetaUtil.shouldHeldItemHideFacades()) {
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
    if (ret) {
      player.swingArm(hand);
    }
    return (ret && !world.isRemote) ? EnumActionResult.PASS : EnumActionResult.FAIL;
  }

  
  
  @Override
  public ActionResult<ItemStack> onItemRightClick(ItemStack equipped, World world, EntityPlayer player, EnumHand hand) {
    if (!Config.useSneakRightClickYetaWrench) {
      return new ActionResult<ItemStack>(EnumActionResult.FAIL, equipped);
    }
    if (!player.isSneaking()) {
      new ActionResult<ItemStack>(EnumActionResult.FAIL, equipped);
    }
    ConduitDisplayMode curMode = ConduitDisplayMode.getDisplayMode(equipped);
    if (curMode == null) {
      curMode = ConduitDisplayMode.ALL;
    }
    ConduitDisplayMode newMode = curMode.next();
    ConduitDisplayMode.setDisplayMode(equipped, newMode);
    return new ActionResult<ItemStack>(EnumActionResult.PASS, equipped);
  }

  @Override
  public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
    IBlockState bs = player.worldObj.getBlockState(pos);
    Block block = bs.getBlock();
    if (player.isSneaking() && block == EnderIO.blockConduitBundle && player.capabilities.isCreativeMode) {
      block.onBlockClicked(player.worldObj, pos, player);
      return true;
    }
    return false;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean isFull3D() {   
    return true;
  }

  @Override
  public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
    return true;
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
    String keyName = Keyboard.getKeyName(KeyTracker.instance.getYetaWrenchMode().getKeyCode());
    for (String line : tmp) {
      list.add(String.format(line, keyName));
    }
  }

  /* InvocationHandler */

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    System.out.println("ItemYetaWrench.invoke: method = " + method.getName());
    return null;
  }
}
