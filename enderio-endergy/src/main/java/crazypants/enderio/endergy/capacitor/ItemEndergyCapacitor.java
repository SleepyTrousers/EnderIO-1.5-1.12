package crazypants.enderio.endergy.capacitor;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.CompoundCapabilityProvider;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.capacitor.CapabilityCapacitorData;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.capacitor.ICapacitorData;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.util.NbtValue;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemEndergyCapacitor extends Item {

  private ICapacitorData data;

  public static ItemEndergyCapacitor create(@Nonnull IModObject modObject) {
    return new ItemEndergyCapacitor(modObject, EndergyCapacitorData.GRAINY_CAPACITOR);
  }

  protected ItemEndergyCapacitor(@Nonnull IModObject modObject, @Nonnull ICapacitorData data) {
    setCreativeTab(EnderIOTab.tabEnderIOMaterials);
    modObject.apply(this);
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);
    this.data = data;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);
    tooltip.add(Lang.MACHINE_UPGRADE.get());
    if (SpecialTooltipHandler.showAdvancedTooltips()) {
      SpecialTooltipHandler.addDetailedTooltipFromResources(tooltip, Lang.MACHINE_UPGRADE.getKey());
    } else {
      SpecialTooltipHandler.addShowDetailsTooltip(tooltip);
    }
    if (NbtValue.GLINT.hasTag(stack)) {
      tooltip.add(EnderIO.lang.localize("loot.capacitor.entry." + NbtValue.CAPNO.getInt(stack), NbtValue.CAPNAME.getString(stack, "(!%$&�*&%*???")));
    }
  }

  @Override
  @Nullable
  public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt) {
    ICapabilityProvider capProvider = new ICapabilityProvider() {

      @Override
      public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityCapacitorData.getCapNN();
      }

      @Override
      @Nullable
      public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityCapacitorData.getCapNN()) {
          return CapabilityCapacitorData.getCapNN().cast(NullHelper.notnullJ(data, "Enum.values() has a null"));
        }
        return null;
      }
    };
    return new CompoundCapabilityProvider(super.initCapabilities(stack, nbt), capProvider);
  }

  @Override
  public boolean hasEffect(@Nonnull ItemStack stack) {
    return super.hasEffect(stack) || NbtValue.GLINT.hasTag(stack);
  }

  @Override
  public @Nonnull EnumActionResult onItemUseFirst(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side,
      float hitX, float hitY, float hitZ, @Nonnull EnumHand hand) {

    if (world.isRemote || System.getProperty("INDEV") == null || !player.isCreative()) {
      return EnumActionResult.PASS;
    }

    TileEntity te = world.getTileEntity(pos);
    if (te instanceof TileEntityChest) {
      TileEntityChest chest = (TileEntityChest) te;
      chest.clear();

      LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer) world);
      lootcontext$builder.withLuck(player.getLuck());

      LootTable loottable = world.getLootTableManager().getLootTableFromLocation(LootTableList.CHESTS_SIMPLE_DUNGEON);
      // LootTable loottable = world.getLootTableManager().getLootTableFromLocation(LootTableList.CHESTS_IGLOO_CHEST);
      loottable.fillInventory(chest, world.rand, lootcontext$builder.build());
      return EnumActionResult.PASS;
    }

    return EnumActionResult.PASS;
  }

}
