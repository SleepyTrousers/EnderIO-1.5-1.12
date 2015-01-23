package crazypants.enderio.item;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
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
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.tool.IConduitControl;
import crazypants.enderio.api.tool.ITool;
import crazypants.enderio.conduit.ConduitDisplayMode;
import crazypants.enderio.config.Config;
import crazypants.enderio.gui.IAdvancedTooltipProvider;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.tool.ToolUtil;
import crazypants.util.Lang;

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
    Block block = world.getBlock(x, y, z);
    if(block != null && !player.isSneaking() && block.rotateBlock(world, x, y, z, ForgeDirection.getOrientation(side))) {
      player.swingItem();
      return !world.isRemote;
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
    return true;
  }
  
  @Override
  public boolean showOverlay(ItemStack stack, EntityPlayer player) {
    return true;
  }

  /* IAdvancedTooltipProvider */
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    int line = 1;
    String unloc = getUnlocalizedName() + ".tooltip.detailed.line", loc = null;
    while (!(loc = Lang.localize(unloc + line, false)).equals(unloc + line++)) {
      list.add(String.format(loc, Keyboard.getKeyName(KeyTracker.instance.getYetaWrenchMode().getKeyCode())));
    }
  }
  
  @SuppressWarnings("rawtypes")
  @Override
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    ; // none
  }
  
  @SuppressWarnings("rawtypes")
  @Override
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    ; // none
  }

  /* InvocationHandler */
  
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    System.out.println("ItemYetaWrench.invoke: method = " + method.getName());
    return null;
  }
}
