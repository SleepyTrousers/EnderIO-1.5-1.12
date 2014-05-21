package crazypants.enderio.item;

import java.util.List;

import cofh.api.energy.ItemEnergyContainer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.Config;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.ConduitDisplayMode;
import crazypants.enderio.gui.IResourceTooltipProvider;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.machine.power.PowerDisplayUtil.PowerType;
import crazypants.enderio.material.Material;
import crazypants.enderio.teleport.TravelController;
import crazypants.util.ItemUtil;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemMagnet extends ItemEnergyContainer implements IResourceTooltipProvider {
  
  private static final String ACTIVE_KEY = "magnetActive";
  
  public static void setActive(ItemStack item, boolean active) {
    if(item == null) {
      return;
    }
    NBTTagCompound nbt = ItemUtil.getOrCreateNBT(item);
    nbt.setBoolean(ACTIVE_KEY, active);
  }
  
  public static boolean isActive(ItemStack item) {
    if(item == null) {
      return false;
    }
    if(item.stackTagCompound == null) {
      return false;
    }
    if(!item.stackTagCompound.hasKey(ACTIVE_KEY)) {
      return false;
    }
    return item.stackTagCompound.getBoolean(ACTIVE_KEY);    
  }
  
  public static boolean hasPower(ItemStack itemStack) {    
    return EnderIO.itemMagnet.getEnergyStored(itemStack) > 0;
  }

  public static void drainPerTickPower(ItemStack itemStack) {
    EnderIO.itemMagnet.extractEnergy(itemStack, Config.magnetPowerUsePerTickRF, false);    
  }

  public static ItemMagnet create() {    
    ItemMagnet result = new ItemMagnet();
    result.init();    
    FMLCommonHandler.instance().bus().register(new MagnetController());    
    return result;
  }

  
  protected ItemMagnet() {
    super(Config.magnetPowerCapacityRF, Config.magnetPowerCapacityRF/100);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemMagnet.unlocalisedName);    
    setMaxDamage(16);
    setMaxStackSize(1);
    setHasSubtypes(true);
  }

  protected void init() {
    GameRegistry.registerItem(this, ModObject.itemMagnet.unlocalisedName);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IIconRegister IIconRegister) {
    itemIcon = IIconRegister.registerIcon("enderio:magnet");
  }
  
  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List par3List) {
    ItemStack is = new ItemStack(this);
    setFull(is);
    par3List.add(is);

    is = new ItemStack(this);
    setEnergy(is, 0);
    par3List.add(is);
  }
  
  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
    super.addInformation(itemStack, par2EntityPlayer, list, par4);
    String str = PowerDisplayUtil.formatPower(PowerType.RF, getEnergyStored(itemStack)) + "/"
        + PowerDisplayUtil.formatPower(PowerType.RF, getMaxEnergyStored(itemStack)) + " " + PowerDisplayUtil.abrevation();
    list.add(str);
  }
  
  @Override
  public boolean hasEffect(ItemStack item, int pass) {    
    return isActive(item);
  }
  
  @Override
  public void onCreated(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
    setEnergy(itemStack, 0);
  }
  
  @Override
  public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
    int res = super.receiveEnergy(container, maxReceive, simulate);
    if(res != 0 && !simulate) {
      updateDamage(container);
    }
    return res;
  }

  @Override
  public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
    int res = super.extractEnergy(container, maxExtract, simulate);
    if(res != 0 && !simulate) {
      updateDamage(container);
    }
    return res;
  }

  void extractInternal(ItemStack item, int powerUse) {
    int res = Math.max(0, getEnergyStored(item) - powerUse);
    setEnergy(item, res);
  }

  void setEnergy(ItemStack container, int energy) {
    if(container.stackTagCompound == null) {
      container.stackTagCompound = new NBTTagCompound();
    }
    container.stackTagCompound.setInteger("Energy", energy);
    updateDamage(container);
  }

  void setFull(ItemStack container) {
    setEnergy(container, Config.magnetPowerCapacityRF);
  }

  private void updateDamage(ItemStack stack) {
    float r = (float) getEnergyStored(stack) / getMaxEnergyStored(stack);
    int res = 16 - (int) (r * 16);
    stack.setItemDamage(res);
  }


  @Override
  public ItemStack onItemRightClick(ItemStack equipped, World world, EntityPlayer player) {           
    if(player.isSneaking()) {      
      setActive(equipped, !isActive(equipped));
    } 
    return equipped;
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    return getUnlocalizedName();
  }

}
