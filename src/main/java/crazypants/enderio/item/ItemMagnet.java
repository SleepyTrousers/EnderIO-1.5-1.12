package crazypants.enderio.item;

import java.util.List;

import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;
import com.enderio.core.common.util.ItemUtil;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.item.darksteel.upgrade.IRenderUpgrade;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.power.forge.PowerHandlerItemStack;
import crazypants.util.BaublesUtil;
import crazypants.util.NbtValue;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.config.Config.magnetAllowInMainInventory;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles|API")
public class ItemMagnet extends Item implements IResourceTooltipProvider, IBauble, IOverlayRenderAware, IHasPlayerRenderer {

  private static final String ACTIVE_KEY = "magnetActive";

  public static void setActive(ItemStack item, boolean active) {
    if (item == null) {
      return;
    }
    NBTTagCompound nbt = ItemUtil.getOrCreateNBT(item);
    nbt.setBoolean(ACTIVE_KEY, active);
  }

  public static boolean isMagnet(ItemStack item) {
    return item != null && item.getItem() instanceof ItemMagnet;
  }

  public static boolean isActive(ItemStack item) {
    if (!isMagnet(item)) {
      return false;
    }
    if (item.getTagCompound() == null) {
      return false;
    }
    if (!item.getTagCompound().hasKey(ACTIVE_KEY)) {
      return false;
    }
    return item.getTagCompound().getBoolean(ACTIVE_KEY);
  }

  public static boolean hasPower(ItemStack itemStack) {
    int energyStored = NbtValue.ENERGY.getInt(itemStack);
    return energyStored > 0 && energyStored >= Config.magnetPowerUsePerSecondRF;
  }

  public static void drainPerSecondPower(ItemStack itemStack) {
    DarkSteelItems.itemMagnet.extractEnergyInternal(itemStack, Config.magnetPowerUsePerSecondRF);
  }

  static MagnetController controller = new MagnetController();

  public static ItemMagnet create() {
    ItemMagnet result = new ItemMagnet();
    result.init();
    MinecraftForge.EVENT_BUS.register(controller);
    return result;
  }

  protected ItemMagnet() {
    //super(Config.magnetPowerCapacityRF, Config.magnetPowerCapacityRF / 100);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemMagnet.getUnlocalisedName());
    setRegistryName(ModObject.itemMagnet.getUnlocalisedName());
    setMaxStackSize(1);
    setHasSubtypes(true);
  }

  protected void init() {
    GameRegistry.register(this);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
    ItemStack is = new ItemStack(this);
    par3List.add(is);

    is = new ItemStack(this);
    NbtValue.ENERGY.setInt(is, Config.magnetPowerCapacityRF);
    par3List.add(is);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List<String> list, boolean par4) {
    super.addInformation(itemStack, par2EntityPlayer, list, par4);
    String str = PowerDisplayUtil.formatPower(NbtValue.ENERGY.getInt(itemStack)) + "/" + PowerDisplayUtil.formatPower(Config.magnetPowerCapacityRF) + " "
        + PowerDisplayUtil.abrevation();
    list.add(str);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean hasEffect(ItemStack item) {
    return isActive(item);
  }

  @Override
  public void onCreated(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
    NbtValue.ENERGY.setInt(itemStack, 0);
  }

  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
    return new PowerHandlerItemStack(stack, Config.magnetPowerCapacityRF, Config.magnetPowerCapacityRF / 100, 0);
  }
  
  private void extractEnergyInternal(ItemStack itemStack, int extract) {
    if(extract <= 0) {
      return;
    }
    int energy = NbtValue.ENERGY.getInt(itemStack);
    energy = Math.max(0, energy - extract);
    NbtValue.ENERGY.setInt(itemStack, energy);
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(ItemStack equipped, World world, EntityPlayer player, EnumHand hand) {
    if (player.isSneaking()) {
      setActive(equipped, !isActive(equipped));
      return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
    }
    return new ActionResult<ItemStack>(EnumActionResult.PASS, equipped);
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    return getUnlocalizedName() + (magnetAllowInMainInventory ? ".everywhere" : "");
  }

  @Override
  @Method(modid = "Baubles|API")
  public BaubleType getBaubleType(ItemStack itemstack) {
    BaubleType t = null;
    try {
      t = BaubleType.valueOf(Config.magnetBaublesType);
    } catch (Exception e) {
      // NOP
    }
    return t != null ? t : BaubleType.AMULET;
  }

  @Override
  public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
    if (player instanceof EntityPlayer && isActive(itemstack) && hasPower(itemstack) && ((EntityPlayer) player).getHealth() > 0f) {
      controller.doHoover((EntityPlayer) player);
      if (!player.worldObj.isRemote && player.worldObj.getTotalWorldTime() % 20 == 0) {
        ItemMagnet.drainPerSecondPower(itemstack);
        IInventory baubles = BaublesUtil.instance().getBaubles((EntityPlayer) player);
        if (baubles != null) {
          for (int i = 0; i < baubles.getSizeInventory(); i++) {
            if (baubles.getStackInSlot(i) == itemstack) {
              baubles.setInventorySlotContents(i, itemstack);
            }
          }
        }
      }
    }
  }

  @Override
  public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
  }

  @Override
  public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
  }

  @Override
  public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
    return Config.magnetAllowInBaublesSlot && (Config.magnetAllowDeactivatedInBaublesSlot || isActive(itemstack));
  }

  @Override
  public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
    return true;
  }

  @Override
  public void renderItemOverlayIntoGUI(ItemStack stack, int xPosition, int yPosition) {
    PowerBarOverlayRenderHelper.instance.render(stack, xPosition, yPosition);
  }

  @Override
  @Nullable
  @SideOnly(Side.CLIENT)
  public IRenderUpgrade getRender() {
    return MagnetLayer.instance;
  }

  @Override
  public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
    return slotChanged ? super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged)
        : (oldStack == null || newStack == null || oldStack.getItem() != newStack.getItem() || isActive(oldStack) != isActive(newStack));
  }

}
