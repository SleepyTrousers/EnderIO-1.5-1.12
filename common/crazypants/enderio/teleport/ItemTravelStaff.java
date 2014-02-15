package crazypants.enderio.teleport;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.ItemEnergyContainer;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.Config;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.power.PowerDisplayUtil;

public class ItemTravelStaff extends ItemEnergyContainer implements IEnergyContainerItem {

  public static ItemTravelStaff create() {
    ItemTravelStaff result = new ItemTravelStaff();
    result.init();
    return result;
  }

  protected ItemTravelStaff() {
    super(ModObject.itemTravelStaff.id, Config.travelStaffMaxStoredPower * 10, Config.travelStaffMaxPowerIo * 10, 0);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName("enderio." + ModObject.itemTravelStaff.name());
    setMaxDamage(16);
    setMaxStackSize(1);
    setHasSubtypes(true);
  }

  protected void init() {
    GameRegistry.registerItem(this, ModObject.itemTravelStaff.unlocalisedName);
  }

  @Override
  public void onCreated(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
    setFull(itemStack);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IconRegister iconRegister) {
    itemIcon = iconRegister.registerIcon("enderio:itemTravelStaff");
  }

  @Override
  public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
    int blockId = world.getBlockId(x, y, z);
    Block block = Block.blocksList[blockId];
    if(block != null && block.rotateBlock(world, x, y, z, ForgeDirection.getOrientation(side))) {
      player.swingItem();
      return !world.isRemote;
    }
    return false;
  }

  @Override
  public ItemStack onItemRightClick(ItemStack equipped, World world, EntityPlayer player) {
    if(world.isRemote) {
      if(TravelPlatformController.instance.hasTarget()) {
        TravelPlatformController.instance.travelToSelectedTarget(player, TravelSource.STAFF);
      }
    }
    player.swingItem();
    return equipped;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
    super.addInformation(itemStack, par2EntityPlayer, list, par4);
    String str = PowerDisplayUtil.formatPower(getEnergyStored(itemStack)) + "/"
        + PowerDisplayUtil.formatPower(getMaxEnergyStored(itemStack)) + " " + PowerDisplayUtil.abrevation();
    list.add(str);
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
    setEnergy(container, getMaxEnergyStored(container));
  }

  private void updateDamage(ItemStack stack) {
    float r = (float) getEnergyStored(stack) / getMaxEnergyStored(stack);
    int res = 16 - (int) (r * 16);
    stack.setItemDamage(res);
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
    ItemStack is = new ItemStack(this);
    setFull(is);
    par3List.add(is);

    is = new ItemStack(this);
    setEnergy(is, 0);
    par3List.add(is);

  }

}
