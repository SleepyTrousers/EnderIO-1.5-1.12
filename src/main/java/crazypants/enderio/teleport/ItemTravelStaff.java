package crazypants.enderio.teleport;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.ItemEnergyContainer;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.Config;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.machine.power.PowerDisplayUtil.PowerType;
import crazypants.enderio.teleport.packet.PacketOpenAuthGui;
import crazypants.util.BlockCoord;
import crazypants.util.Util;
import crazypants.vecmath.Vector3d;

public class ItemTravelStaff extends ItemEnergyContainer implements IEnergyContainerItem {

  public static boolean isEquipped(EntityPlayer ep) {
    if(ep == null || ep.getCurrentEquippedItem() == null) {
      return false;
    }
    return ep.getCurrentEquippedItem().getItem() == EnderIO.itemTravelStaff;
  }

  private long lastBlickTick = 0;

  public static ItemTravelStaff create() {
    ItemTravelStaff result = new ItemTravelStaff();
    result.init();
    return result;
  }

  protected ItemTravelStaff() {
    super(Config.travelStaffMaxStoredPowerRF, Config.travelStaffMaxPowerIoRF, 0);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemTravelStaff.name());
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
  public void registerIcons(IIconRegister IIconRegister) {
    itemIcon = IIconRegister.registerIcon("enderio:itemTravelStaff");
  }

  @Override
  public ItemStack onItemRightClick(ItemStack equipped, World world, EntityPlayer player) {
    if(player.isSneaking()) {
      if(Config.travelStaffBlinkEnabled && world.isRemote && player.worldObj.getTotalWorldTime() - lastBlickTick >= Config.travelStaffBlinkPauseTicks) {
        Vector3d eye = Util.getEyePositionEio(player);
        Vector3d look = Util.getLookVecEio(player);


        Vector3d sample = new Vector3d(look);
        sample.scale(Config.travelStaffMaxBlinkDistance);
        sample.add(eye);
        Vec3 eye3 = Vec3.createVectorHelper(eye.x,eye.y,eye.z);
        Vec3 end = Vec3.createVectorHelper(sample.x,sample.y,sample.z);

        double playerHeight = player.yOffset;
        //if you looking at you feet, and your player height to the max distance, or part there of
        double lookComp = -look.y * playerHeight;
        double maxDistance = Config.travelStaffMaxBlinkDistance + lookComp;

        MovingObjectPosition p = player.worldObj.rayTraceBlocks(eye3, end, !Config.travelStaffBlinkThroughClearBlocksEnabled);
        if(p == null) {

          //go as far as possible
          for (double i = maxDistance; i > 1; i--) {

            sample.set(look);
            sample.scale(i);
            sample.add(eye);
            //we test against our feets location
            sample.y -= playerHeight;

            //if(doBlink(player, eye, look, sample, i)) {
            if(doBlinkAround(player, sample)) {
              return equipped;
            }
          }
        } else {

          eye3 = Vec3.createVectorHelper(eye.x,eye.y,eye.z);

          Vector3d targetBc = new Vector3d(p.blockX, p.blockY, p.blockZ);
          double sampleDistance = 1.5;
          double teleDistance = p.hitVec.distanceTo(eye3) + sampleDistance;
          while(teleDistance < maxDistance) {
            sample.set(look);
            sample.scale(sampleDistance);
            sample.add(targetBc);
            //we test against our feets location
            sample.y -= playerHeight;

            if(doBlinkAround(player, sample)) {
              return equipped;
            }
            teleDistance++;
            sampleDistance++;
          }
          sampleDistance = -0.5;
          teleDistance = p.hitVec.distanceTo(eye3) + sampleDistance;
          while(teleDistance > 1) {
            sample.set(look);
            sample.scale(sampleDistance);
            sample.add(targetBc);
            //we test against our feets location
            sample.y -= playerHeight;

            if(doBlinkAround(player, sample)) {
              return equipped;
            }
            sampleDistance--;
            teleDistance--;
          }
        }
      }
      return equipped;
    }

    if(world.isRemote) {
      if(TravelController.instance.hasTarget()) {
        BlockCoord target = TravelController.instance.selectedCoord;
        TileEntity te = world.getTileEntity(target.x, target.y, target.z);
        if(te instanceof ITravelAccessable) {
          ITravelAccessable ta = (ITravelAccessable) te;
          if(ta.getRequiresPassword(player)) {
            PacketOpenAuthGui p = new PacketOpenAuthGui(target.x, target.y, target.z);
            EnderIO.packetPipeline.sendToServer(p);
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

  private boolean doBlinkAround(EntityPlayer player, Vector3d sample) {
    if(doBlink(player, new BlockCoord((int)Math.round(sample.x),(int)Math.round(sample.y) - 1,(int)Math.round(sample.z)))) {
      return true;
    }
    if(doBlink(player, new BlockCoord((int)Math.round(sample.x),(int)Math.round(sample.y),(int)Math.round(sample.z)))) {
      return true;
    }
    if(doBlink(player, new BlockCoord((int)Math.round(sample.x),(int)Math.round(sample.y) + 1,(int)Math.round(sample.z)))) {
      return true;
    }
    return false;
  }

  private boolean doBlink(EntityPlayer player, BlockCoord coord) {

    if(TravelController.instance.travelToLocation(player, TravelSource.STAFF_BLINK, coord)) {
      player.swingItem();
      lastBlickTick = player.worldObj.getTotalWorldTime();
      return true;
    }

    return false;
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

  public void extractInternal(ItemStack item, int powerUse) {
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
    setEnergy(container, Config.travelStaffMaxStoredPowerRF);
  }

  private void updateDamage(ItemStack stack) {
    float r = (float) getEnergyStored(stack) / getMaxEnergyStored(stack);
    int res = 16 - (int) (r * 16);
    stack.setItemDamage(res);
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

}
