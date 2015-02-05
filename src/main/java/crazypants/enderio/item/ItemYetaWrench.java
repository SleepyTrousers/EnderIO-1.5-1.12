package crazypants.enderio.item;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.tool.IConduitControl;
import crazypants.enderio.api.tool.ITool;
import crazypants.enderio.conduit.BlockConduitBundle;
import crazypants.enderio.conduit.ConduitDisplayMode;
import crazypants.enderio.config.Config;
import crazypants.enderio.gui.IAdvancedTooltipProvider;
import crazypants.enderio.gui.TooltipAddera;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.tool.ToolUtil;

public class ItemYetaWrench extends Item implements ITool, IConduitControl, IAdvancedTooltipProvider, InvocationHandler {

  public static ItemYetaWrench create() {
    if(Config.useSneakMouseWheelYetaWrench) {
      PacketHandler.INSTANCE.registerMessage(YetaWrenchPacketProcessor.class, YetaWrenchPacketProcessor.class, PacketHandler.nextID(), Side.SERVER);
    }
    ItemYetaWrench result = new ItemYetaWrench();
    result = ToolUtil.addInterfaces(result);
    //result.init();
    GameRegistry.registerItem(result, ModObject.itemYetaWrench.unlocalisedName);

    return result;
  }

  protected ItemYetaWrench() {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemYetaWrench.unlocalisedName);
    setMaxStackSize(1);
  }

  //  protected void init() {
  //    //GameRegistry.registerItem(this, ModObject.itemYetaWrench.unlocalisedName);
  //  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IIconRegister IIconRegister) {
    itemIcon = IIconRegister.registerIcon("enderio:yetaWrench");
  }

  @Override
  public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
    if(world.isRemote) {
      return false;
    }
    Block block = world.getBlock(x, y, z);
    if(block instanceof IRotatableFacade && !player.isSneaking() &&
            (block != EnderIO.blockConduitBundle || ConduitDisplayMode.getDisplayMode(stack) == ConduitDisplayMode.NONE)) {
      if(((IRotatableFacade)block).tryRotateFacade(world, x, y, z, ForgeDirection.getOrientation(side))) {
        player.swingItem();
        return true;
      }
    }
    if(block != null && !player.isSneaking() && block.rotateBlock(world, x, y, z, ForgeDirection.getOrientation(side))) {
      player.swingItem();
      return true;
    }
    return false;
  }

  @Override
  public ItemStack onItemRightClick(ItemStack equipped, World world, EntityPlayer player) {
    if(!Config.useSneakRightClickYetaWrench) {
      return equipped;
    }
    if(!player.isSneaking()) {
      return equipped;
    }
    ConduitDisplayMode curMode = ConduitDisplayMode.getDisplayMode(equipped);
    if(curMode == null) {
      curMode = ConduitDisplayMode.ALL;
    }
    ConduitDisplayMode newMode = curMode.next();
    ConduitDisplayMode.setDisplayMode(equipped, newMode);
    return equipped;
  }

  @Override
  public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
    Block block = player.worldObj.getBlock(x, y, z);
    return block == EnderIO.blockConduitBundle;
  }

  @Override
  public boolean isFull3D() {
    return true;
  }

  @Override
  public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
    return true;
  }

  @Override
  public boolean canUse(ItemStack stack, EntityPlayer player, int x, int y, int z) {
    return true;
  }

  @Override
  public void used(ItemStack stack, EntityPlayer player, int x, int y, int z) {
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
  
  @SuppressWarnings("rawtypes")
  @Override
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
  }
  
  @SuppressWarnings("rawtypes")
  @Override
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    ArrayList<String> tmp = new ArrayList<String>();
    TooltipAddera.addDetailedTooltipFromResources(tmp, getUnlocalizedName());
    String keyName = Keyboard.getKeyName(KeyTracker.instance.getYetaWrenchMode().getKeyCode());
    for(String line : tmp) {
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
