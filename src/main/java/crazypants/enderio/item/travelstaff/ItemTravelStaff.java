package crazypants.enderio.item.travelstaff;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.api.teleport.IItemOfTravel;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.config.Config;
import crazypants.enderio.handler.darksteel.DarkSteelRecipeManager;
import crazypants.enderio.handler.darksteel.IDarkSteelItem;
import crazypants.enderio.init.IModObject;
import crazypants.enderio.item.darksteel.upgrade.energy.EnergyUpgrade;
import crazypants.enderio.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.render.util.PowerBarOverlayRenderHelper;
import crazypants.enderio.teleport.TravelController;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTravelStaff extends Item implements IItemOfTravel, IAdvancedTooltipProvider, IOverlayRenderAware, IDarkSteelItem {

  private long lastBlickTick = 0;

  public static ItemTravelStaff create(@Nonnull IModObject modObject) {
    ItemTravelStaff result = new ItemTravelStaff(modObject);
    result.init();
    return result;
  }

  protected ItemTravelStaff(@Nonnull IModObject modObject) {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    setMaxStackSize(1);
    setHasSubtypes(true);
  }

  protected void init() {
    GameRegistry.register(this);
  }

  @Override
  public void onCreated(@Nonnull ItemStack itemStack, @Nonnull World world, @Nonnull EntityPlayer entityPlayer) {
    EnergyUpgrade.EMPOWERED.writeToItem(itemStack);
  }

  @Override
  public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
    ItemStack equipped = player.getHeldItem(hand);
    if (player.isSneaking()) {
      long ticksSinceBlink = EnderIO.proxy.getTickCount() - lastBlickTick;
      if (ticksSinceBlink < 0) {
        lastBlickTick = -1;
      }
      if (Config.travelStaffBlinkEnabled && world.isRemote && ticksSinceBlink >= Config.travelStaffBlinkPauseTicks) {
        if (TravelController.instance.doBlink(equipped, hand, player)) {
          player.swingArm(hand);
          lastBlickTick = EnderIO.proxy.getTickCount();
        }
      }
      return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
    }

    if (world.isRemote) {
      TravelController.instance.activateTravelAccessable(equipped, hand, world, player, TravelSource.STAFF);
    }
    player.swingArm(hand);
    return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
  }

  @Override
  public void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    DarkSteelRecipeManager.instance.addCommonTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addBasicEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    DarkSteelRecipeManager.instance.addBasicTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addDetailedEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    list.add(EnergyUpgradeManager.getStoredEnergyString(itemstack));
    DarkSteelRecipeManager.instance.addAdvancedTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void extractInternal(@Nonnull ItemStack item, int powerUse) {
    EnergyUpgradeManager.extractEnergy(item, powerUse, false);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(@Nonnull Item item, @Nullable CreativeTabs par2CreativeTabs, @Nonnull NonNullList<ItemStack> par3List) {
    ItemStack is = new ItemStack(this);
    par3List.add(is);

    is = new ItemStack(this);
    EnergyUpgrade.EMPOWERED_FOUR.writeToItem(is);
    EnergyUpgradeManager.setPowerFull(is);
    par3List.add(is);
  }

  @Override
  public boolean isActive(@Nonnull EntityPlayer ep, @Nonnull ItemStack equipped) {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean isFull3D() {
    return true;
  }

  @Override
  public void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
    PowerBarOverlayRenderHelper.instance.render(stack, xPosition, yPosition);
  }

  @Override
  public int getIngotsRequiredForFullRepair() {
    return 0;
  }

  @Override
  public boolean isItemForRepair(@Nonnull ItemStack right) {
    // not damageable, no repair
    return false;
  }

  @Override
  public int getEnergyStored(@Nonnull ItemStack item) {
    return EnergyUpgradeManager.getEnergyStored(item);
  }

}
