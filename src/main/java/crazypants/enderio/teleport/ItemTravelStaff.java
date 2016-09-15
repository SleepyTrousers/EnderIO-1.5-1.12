package crazypants.enderio.teleport;

import java.util.List;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.teleport.IItemOfTravel;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.PowerBarOverlayRenderHelper;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.power.PowerHandlerItemStack;
import crazypants.util.NbtValue;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTravelStaff extends Item implements IItemOfTravel, IResourceTooltipProvider, IOverlayRenderAware {

  private long lastBlickTick = 0;

  public static ItemTravelStaff create() {
    ItemTravelStaff result = new ItemTravelStaff();
    result.init();
    return result;
  }

  protected ItemTravelStaff() {
    //super(Config.darkSteelPowerStorageLevelTwo, Config.darkSteelPowerStorageLevelTwo / 100, 0);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemTravelStaff.getUnlocalisedName());
    setRegistryName(ModObject.itemTravelStaff.getUnlocalisedName());
    setMaxStackSize(1);
    setHasSubtypes(true);
  }

  protected void init() {
    GameRegistry.register(this);
  }

  @Override
  public void onCreated(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
    setEnergy(itemStack, 0);
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(ItemStack equipped, World world, EntityPlayer player, EnumHand hand) {
    if(player.isSneaking()) {
      long ticksSinceBlink = EnderIO.proxy.getTickCount() - lastBlickTick;
      if(ticksSinceBlink < 0) {
        lastBlickTick = -1;
      }
      if(Config.travelStaffBlinkEnabled && world.isRemote && ticksSinceBlink >= Config.travelStaffBlinkPauseTicks) {
        if (TravelController.instance.doBlink(equipped, hand, player)) {
          player.swingArm(hand);
          lastBlickTick = EnderIO.proxy.getTickCount();
        }
      }
      return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
    }

    if(world.isRemote) {
      TravelController.instance.activateTravelAccessable(equipped, hand, world, player, TravelSource.STAFF);
    }
    player.swingArm(hand);
    return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List<String> list, boolean par4) {
    super.addInformation(itemStack, par2EntityPlayer, list, par4);
    String str = PowerDisplayUtil.formatPower(getEnergyStored(itemStack)) + "/"
        + PowerDisplayUtil.formatPower(getMaxEnergyStored(itemStack)) + " " + PowerDisplayUtil.abrevation();
    list.add(str);
  }

  @Override
  public void extractInternal(ItemStack item, int powerUse) {
    int res = Math.max(0, getEnergyStored(item) - powerUse);
    setEnergy(item, res);
  }

  @Override
  public int getEnergyStored(ItemStack item) {
    return NbtValue.ENERGY.getInt(item);
  }
  
  private int getMaxEnergyStored(ItemStack itemStack) {
    return Config.darkSteelPowerStorageLevelTwo;
  }

  void setEnergy(ItemStack container, int energy) {
    if(container.getTagCompound() == null) {
      container.setTagCompound(new NBTTagCompound());
    }
    container.getTagCompound().setInteger("Energy", energy);
  }

  public void setFull(ItemStack container) {
    setEnergy(container, getMaxEnergyStored(container));
  }
  
  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
    return new PowerHandlerItemStack(stack, Config.darkSteelPowerStorageLevelTwo, Config.darkSteelPowerStorageLevelTwo / 100, 0);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
    ItemStack is = new ItemStack(this);
    par3List.add(is);

    is = new ItemStack(this);
    setFull(is);
    par3List.add(is);
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    return getUnlocalizedName();
  }

  @Override
  public boolean isActive(EntityPlayer ep, ItemStack equipped) {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean isFull3D() {
    return true;
  }

  @Override
  public void renderItemOverlayIntoGUI(ItemStack stack, int xPosition, int yPosition) {
    PowerBarOverlayRenderHelper.instance.render(stack, xPosition, yPosition);
  }

}
