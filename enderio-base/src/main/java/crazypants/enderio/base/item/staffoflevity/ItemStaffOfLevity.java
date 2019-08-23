package crazypants.enderio.base.item.staffoflevity;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.common.MappedCapabilityProvider;
import com.enderio.core.common.interfaces.IOverlayRenderAware;
import com.enderio.core.common.util.FluidUtil;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IEquipmentData;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.capacitor.CapacitorKey;
import crazypants.enderio.base.config.config.ItemConfig;
import crazypants.enderio.base.handler.darksteel.DarkSteelTooltipManager;
import crazypants.enderio.base.item.darksteel.attributes.EquipmentData;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.render.itemoverlay.PowerBarOverlayRenderHelper;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.util.NbtValue.FLUIDAMOUNT;

public class ItemStaffOfLevity extends Item implements IAdvancedTooltipProvider, IOverlayRenderAware, IDarkSteelItem {

  public static ItemStaffOfLevity create(@Nonnull IModObject modObject, @Nullable Block block) {
    return new ItemStaffOfLevity(modObject);
  }

  private long lastActivationTick = 0;
  private boolean isEffectActive = false;

  public ItemStaffOfLevity(@Nonnull IModObject modObject) {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    setMaxStackSize(1);
    setHasSubtypes(true);
  }

  @Override
  @Nonnull
  public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
    ItemStack stack = player.getHeldItem(hand);
    if (player.isSneaking()) {
      long ticksSinceActivation = EnderIO.proxy.getTickCount() - lastActivationTick;
      if (ticksSinceActivation < 0) {
        lastActivationTick = -1;
      }
      player.swingArm(hand);

      if (world.isRemote && (ticksSinceActivation == 0 || ticksSinceActivation >= ItemConfig.staffOfLevityTicksBetweenActivation.get())) {
        if (!isEffectActive && hasFluid(stack)) {
          useFluid(stack);
          player.addPotionEffect(new PotionEffect(MobEffects.LEVITATION));
        } else {
          player.removePotionEffect(MobEffects.LEVITATION);
        }
        lastActivationTick = EnderIO.proxy.getTickCount();
        isEffectActive = !isEffectActive;
      }
    }
    return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
  }

  @Override
  public boolean canDestroyBlockInCreative(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
    return false;
  }

  @Override
  public void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
    PowerBarOverlayRenderHelper.instance.render(stack, xPosition, yPosition, true);
    PowerBarOverlayRenderHelper.instance_fluid.render(stack, xPosition, yPosition, 1, true);
  }

  @Override
  public void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    DarkSteelTooltipManager.addCommonTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addBasicEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    DarkSteelTooltipManager.addBasicTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addDetailedEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    DarkSteelTooltipManager.addAdvancedTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    if (isInCreativeTab(tab)) {
      ItemStack is = new ItemStack(this);
      list.add(is);

      is = new ItemStack(this);
      EnergyUpgrade.UPGRADES.get(3).addToItem(is, this);
      EnergyUpgradeManager.setPowerFull(is, this);
      FLUIDAMOUNT.setInt(is, ItemConfig.staffOfLevityFluidStorage.get());
      list.add(is);
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean isFull3D() {
    return true;
  }

  @Override
  public boolean isForSlot(@Nonnull EntityEquipmentSlot slot) {
    return slot == EntityEquipmentSlot.MAINHAND;
  }

  // ------------------------------------------------------
  // FLUID HANDLING
  // ------------------------------------------------------

  private boolean hasFluid(@Nonnull ItemStack contianer) {
    int amount = FLUIDAMOUNT.getInt(contianer);
    if (ItemConfig.staffOfLevityFluidUsePerTeleport.get() > amount) {
      return false;
    }
    return true;
  }

  private boolean useFluid(@Nonnull ItemStack container) {
    int amount = FLUIDAMOUNT.getInt(container, 0);
    if (ItemConfig.staffOfLevityFluidUsePerTeleport.get() > amount) {
      FLUIDAMOUNT.setInt(container, 0);
      return false;
    } else {
      FLUIDAMOUNT.setInt(container, amount - ItemConfig.staffOfLevityFluidUsePerTeleport.get());
      return true;
    }
  }

  public FluidStack getFluid(@Nonnull ItemStack container) {
    int amount = FLUIDAMOUNT.getInt(container, 0);
    if (amount > 0) {
      return new FluidStack(ItemConfig.staffOfLevityFluidType.get(), amount);
    } else {
      return null;
    }
  }

  public int fill(@Nonnull ItemStack container, @Nonnull FluidStack resource, boolean doFill) {
    if (!(container.getItem() == this) || resource.amount <= 0 || resource.getFluid() == null
        || resource.getFluid() != ItemConfig.staffOfLevityFluidType.get()) {
      return 0;
    }
    int amount = FLUIDAMOUNT.getInt(container, 0);
    int capacity = ItemConfig.staffOfLevityFluidStorage.get();
    int free = capacity - amount;
    int toFill = Math.min(resource.amount, free);
    if (toFill > 0 && doFill) {
      FLUIDAMOUNT.setInt(container, amount + toFill);
    }
    return toFill;
  }

  @Override
  @Nonnull
  public MappedCapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt, @Nonnull MappedCapabilityProvider capProv) {
    return capProv.add(FluidUtil.getFluidItemCapability(), new FluidCapabilityProvider(stack));
  }

  private final class FluidCapabilityProvider implements IFluidHandlerItem {
    protected final @Nonnull ItemStack container;

    private FluidCapabilityProvider(@Nonnull ItemStack container) {
      this.container = container;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
      return new IFluidTankProperties[] { new IFluidTankProperties() {

        @Override
        @Nullable
        public FluidStack getContents() {
          return getFluid(container);
        }

        @Override
        public int getCapacity() {
          return ItemConfig.staffOfLevityFluidStorage.get();
        }

        @Override
        public boolean canFill() {
          return true;
        }

        @Override
        public boolean canDrain() {
          return false;
        }

        @Override
        public boolean canFillFluidType(FluidStack fluidStack) {
          return fluidStack != null && fluidStack.getFluid() == ItemConfig.staffOfLevityFluidType.get();
        }

        @Override
        public boolean canDrainFluidType(FluidStack fluidStack) {
          return false;
        }
      } };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
      return ItemStaffOfLevity.this.fill(container, NullHelper.notnull(resource, "Cannot use null as a fluid stack"), doFill);
    }

    @Override
    @Nullable
    public FluidStack drain(FluidStack resource, boolean doDrain) {
      return null;
    }

    @Override
    @Nullable
    public FluidStack drain(int maxDrain, boolean doDrain) {
      return null;
    }

    @Override
    @Nonnull
    public ItemStack getContainer() {
      return container;
    }
  }

  @Override
  public @Nonnull IEquipmentData getEquipmentData() {
    return EquipmentData.IRON;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyStorageKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_LEVITY_ENERGY_BUFFER;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyInputKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_LEVITY_ENERGY_INPUT;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyUseKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_LEVITY_ENERGY_USE;
  }

  @Override
  public @Nonnull ICapacitorKey getAbsorptionRatioKey(@Nonnull ItemStack stack) {
    return CapacitorKey.NO_POWER;
  }

}
