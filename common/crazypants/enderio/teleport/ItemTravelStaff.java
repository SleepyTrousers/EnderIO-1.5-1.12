package crazypants.enderio.teleport;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.ItemEnergyContainer;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.Config;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.util.BlockCoord;
import crazypants.util.Util;
import crazypants.vecmath.Vector3d;

public class ItemTravelStaff extends ItemEnergyContainer implements IEnergyContainerItem {

  public static boolean isEquipped(EntityPlayer ep) {
    if(ep == null || ep.getCurrentEquippedItem() == null) {
      return false;
    }
    return ep.getCurrentEquippedItem().itemID == ModObject.itemTravelStaff.actualId;
  }

  private long lastBlickTick = 0;

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
    setEnergy(itemStack, 0);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IconRegister iconRegister) {
    itemIcon = iconRegister.registerIcon("enderio:itemTravelStaff");
  }

  @Override
  public ItemStack onItemRightClick(ItemStack equipped, World world, EntityPlayer player) {
    if(player.isSneaking()) {
      long ticksSinceBlink = player.worldObj.getTotalWorldTime() - lastBlickTick;
      if(ticksSinceBlink < 0) {
        lastBlickTick = -1;
      }
      if(Config.travelStaffBlinkEnabled && world.isRemote && ticksSinceBlink >= Config.travelStaffBlinkPauseTicks) {
        Vector3d eye = Util.getEyePositionEio(player);
        Vector3d look = Util.getLookVecEio(player);

        Vector3d sample = new Vector3d();

        for (int i = (int) Math.round(Config.travelStaffMaxBlinkDistance); i > 0; i--) {
          sample.set(look);
          sample.scale(i);
          sample.add(eye);
          BlockCoord coord = new BlockCoord((int) sample.x, (int) sample.y, (int) sample.z);
          if(TravelController.instance.travelToLocation(player, TravelSource.STAFF_BLINK, coord)) {
            player.swingItem();
            lastBlickTick = player.worldObj.getTotalWorldTime();
            return equipped;
          }
        }
      }
      return equipped;
    }

    if(world.isRemote) {
      if(TravelController.instance.hasTarget()) {
        BlockCoord target = TravelController.instance.selectedCoord;
        TileEntity te = world.getBlockTileEntity(target.x, target.y, target.z);
        if(te instanceof ITravelAccessable) {
          ITravelAccessable ta = (ITravelAccessable) te;
          if(ta.getRequiresPassword(player.username)) {
            Packet packet = TravelPacketHandler.createOpenAuthGuiPacket(target.x, target.y, target.z);
            PacketDispatcher.sendPacketToServer(packet);
            return equipped;
          }
        }

        if(TravelController.instance.isTargetEnderIO()) {
          TravelController.instance.openEnderIO(equipped, world, player);
        } else if(Config.travelAnchorEnabled) {
          TravelController.instance.travelToSelectedTarget(player, TravelSource.STAFF);
        }
      }
    }
    player.swingItem();
    return equipped;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
    super.addInformation(itemStack, par2EntityPlayer, list, par4);
    String str = PowerDisplayUtil.formatPower(getEnergyStored(itemStack) / 10) + "/"
        + PowerDisplayUtil.formatPower(getMaxEnergyStored(itemStack) / 10) + " " + PowerDisplayUtil.abrevation();
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

  public void setFull(ItemStack container) {
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
