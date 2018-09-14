package crazypants.enderio.base.item.magnet;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.upgrades.IHasPlayerRenderer;
import crazypants.enderio.api.upgrades.IRenderUpgrade;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.config.config.ItemConfig;
import crazypants.enderio.base.integration.baubles.BaublesUtil;
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.base.power.AbstractPoweredItem;
import crazypants.enderio.base.power.IInternalPoweredItem;
import crazypants.enderio.base.render.itemoverlay.PowerBarOverlayRenderHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.util.NbtValue.MAGNET_ACTIVE;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles|API")
public class ItemMagnet extends AbstractPoweredItem implements IResourceTooltipProvider, IBauble, IOverlayRenderAware, IHasPlayerRenderer {

  public static ItemMagnet create(@Nonnull IModObject modObject) {
    return new ItemMagnet(modObject);
  }

  protected ItemMagnet(@Nonnull IModObject modObject) {
    super(ItemConfig.magnetPowerCapacity.get(), ItemConfig.magnetPowerCapacity.get() / 100, 0);
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    setMaxStackSize(1);
    setHasSubtypes(true);
  }

  public static void setActive(@Nonnull ItemStack item, boolean active) {
    MAGNET_ACTIVE.setBoolean(item, active);
  }

  public static boolean isMagnet(ItemStack item) {
    return item != null && item.getItem() instanceof ItemMagnet;
  }

  public static boolean isActive(@Nonnull ItemStack item) {
    return isMagnet(item) && MAGNET_ACTIVE.getBoolean(item);
  }

  public static boolean hasPower(@Nonnull ItemStack itemStack) {
    int energyStored = itemStack.getItem() instanceof IInternalPoweredItem ? ((IInternalPoweredItem) itemStack.getItem()).getEnergyStored(itemStack) : 0;
    return energyStored > 0 && energyStored >= ItemConfig.magnetPowerUsePerSecond.get();
  }

  public void drainPerSecondPower(@Nonnull ItemStack itemStack) {
    extractEnergyInternal(itemStack, ItemConfig.magnetPowerUsePerSecond.get());
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    if (isInCreativeTab(tab)) {
      ItemStack is = new ItemStack(this);
      list.add(is);

      is = new ItemStack(this);
      setFull(is);
      list.add(is);
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);
    tooltip.add(LangPower.RF(getEnergyStored(stack), ItemConfig.magnetPowerCapacity.get()));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean hasEffect(@Nonnull ItemStack item) {
    return isActive(item);
  }

  @Override
  public void onCreated(@Nonnull ItemStack itemStack, @Nonnull World world, @Nonnull EntityPlayer entityPlayer) {
    setEnergyStored(itemStack, 0);
  }

  private void extractEnergyInternal(@Nonnull ItemStack itemStack, int extract) {
    if (extract <= 0) {
      return;
    }
    int energy = getEnergyStored(itemStack);
    energy = Math.max(0, energy - extract);
    setEnergyStored(itemStack, energy);
  }

  @Override
  public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
    if (player.isSneaking()) {
      ItemStack equipped = player.getHeldItem(hand);
      setActive(equipped, !isActive(equipped));
      return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
    }
    return super.onItemRightClick(world, player, hand);
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack stack) {
    return getUnlocalizedName() + (ItemConfig.magnetAllowInMainInventory.get() ? ".everywhere" : "");
  }

  @Override
  @Method(modid = "Baubles|API")
  public BaubleType getBaubleType(ItemStack itemstack) {
    BaubleType t = null;
    try {
      t = BaubleType.valueOf(ItemConfig.magnetBaublesType.get());
    } catch (Exception e) {
      // NOP
    }
    return t != null ? t : BaubleType.AMULET;
  }

  @Override
  @Method(modid = "Baubles|API")
  public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
    if (itemstack == null || player == null) {
      return;
    }
    if (player instanceof EntityPlayer && isActive(itemstack) && hasPower(itemstack) && ((EntityPlayer) player).getHealth() > 0f) {
      MagnetController.doHoover((EntityPlayer) player);
      if (!player.world.isRemote && player.world.getTotalWorldTime() % 20 == 0) {
        // mustn't change the item that is in the slot or Baubles will ignore the change
        ItemStack changedStack = itemstack.copy();
        drainPerSecondPower(changedStack);
        IInventory baubles = BaublesUtil.instance().getBaubles((EntityPlayer) player);
        if (baubles != null) {
          for (int i = 0; i < baubles.getSizeInventory(); i++) {
            if (baubles.getStackInSlot(i) == itemstack) {
              baubles.setInventorySlotContents(i, changedStack);
            }
          }
        }
      }
    }
  }

  @Override
  @Method(modid = "Baubles|API")
  public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
  }

  @Override
  @Method(modid = "Baubles|API")
  public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
  }

  @Override
  @Method(modid = "Baubles|API")
  public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
    if (itemstack == null || player == null) {
      return false;
    }
    return ItemConfig.magnetAllowInBaublesSlot.get() && (ItemConfig.magnetAllowDeactivatedInBaublesSlot.get() || isActive(itemstack));
  }

  @Override
  @Method(modid = "Baubles|API")
  public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
    return true;
  }

  @Override
  public void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
    PowerBarOverlayRenderHelper.instance.render(stack, xPosition, yPosition);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IRenderUpgrade getRender() {
    return MagnetLayer.instance;
  }

  @Override
  public boolean shouldCauseReequipAnimation(@Nonnull ItemStack oldStack, @Nonnull ItemStack newStack, boolean slotChanged) {
    return slotChanged ? super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged)
        : (oldStack.getItem() != newStack.getItem() || isActive(oldStack) != isActive(newStack));
  }

}
