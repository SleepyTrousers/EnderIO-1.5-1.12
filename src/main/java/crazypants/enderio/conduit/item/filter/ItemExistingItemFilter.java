package crazypants.enderio.conduit.item.filter;

import java.util.List;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.util.ChatUtil;
import com.enderio.core.common.util.ItemUtil;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.ModObject;
import crazypants.enderio.conduit.item.FilterRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemExistingItemFilter extends Item implements IItemFilterUpgrade, IResourceTooltipProvider {

  public static ItemExistingItemFilter create() {
    ItemExistingItemFilter result = new ItemExistingItemFilter();
    result.init();
    return result;
  }

  protected ItemExistingItemFilter() {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    setUnlocalizedName(ModObject.itemExistingItemFilter.getUnlocalisedName());
    setRegistryName(ModObject.itemExistingItemFilter.getUnlocalisedName());
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);
  }

  protected void init() {
    GameRegistry.register(this);
  }

  @Override
  public IItemFilter createFilterFromStack(ItemStack stack) {
    IItemFilter filter = new ExistingItemFilter();
    if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("filter")) {
      filter.readFromNBT(stack.getTagCompound().getCompoundTag("filter"));
    }
    return filter;
  }

  
  @Override
  public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
    if(world.isRemote) {
      return EnumActionResult.SUCCESS;
    }    

    if(player.isSneaking()) {
      TileEntity te = world.getTileEntity(pos);
      if (te instanceof IInventory) {
        IInventory inv = ItemUtil.getInventory((IInventory) te);
        ExistingItemFilter filter = (ExistingItemFilter) createFilterFromStack(stack);
        String unloc = "item.itemExistingItemFilter." + (filter.mergeSnapshot(inv) ? "filterUpdated" : "filterNotUpdated");
        ChatUtil.sendNoSpamUnloc(player, EnderIO.lang, unloc);
        FilterRegister.writeFilterToStack(filter, stack);
        return EnumActionResult.SUCCESS;
      }
    }

    return EnumActionResult.PASS;
  }

//  @Override
//  @SideOnly(Side.CLIENT)
//  public void registerIcons(IIconRegister IIconRegister) {
//    itemIcon = IIconRegister.registerIcon("enderio:existingItemFilter");
//  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    return getUnlocalizedName();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List<String> par3List, boolean par4) {
    if(FilterRegister.isFilterSet(par1ItemStack)) {
      if(SpecialTooltipHandler.showAdvancedTooltips()) {
        par3List.add(TextFormatting.ITALIC + EnderIO.lang.localize("itemConduitFilterUpgrade.configured"));
        par3List.add(TextFormatting.ITALIC + EnderIO.lang.localize("itemConduitFilterUpgrade.clearConfigMethod"));
      }
    }
  }

}
