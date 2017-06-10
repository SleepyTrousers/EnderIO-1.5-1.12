package crazypants.enderio.capacitor;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.init.IModObject;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.util.ClientUtil;
import crazypants.util.NbtValue;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCapacitor extends Item implements ICapacitorDataItem, IHaveRenderers {

  public static ItemCapacitor create(@Nonnull IModObject modObject) {
    ItemCapacitor result = new ItemCapacitor(modObject);
    result.init();
    return result;
  }

  protected ItemCapacitor(@Nonnull IModObject modObject) {
    setCreativeTab(EnderIOTab.tabEnderIOMaterials);
    modObject.apply(this);
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);
  }

  protected void init() {
    GameRegistry.register(this);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject modObject) {
    final NNList<ResourceLocation> resourceLocations = DefaultCapacitorData.getResourceLocations();
    ModelBakery.registerItemVariants(this, resourceLocations.toArray(new ResourceLocation[0]));
    for (int i = 0; i < resourceLocations.size(); i++) {
      ClientUtil.regRenderer(this, i, resourceLocations.get(i));
    }
  }
  
  @Override
  public @Nonnull String getUnlocalizedName(@Nonnull ItemStack stack) {
    return getCapacitorData(stack).getUnlocalizedName();
  }

  @Override  
  @SideOnly(Side.CLIENT)
  public void getSubItems(@Nonnull Item par1, @Nullable CreativeTabs par2CreativeTabs, @Nonnull NonNullList<ItemStack> par3List) {
    for (int j = 0; j < DefaultCapacitorData.values().length - 1; ++j) {
      par3List.add(new ItemStack(par1, 1, j));
    }
  }

  @Override
  public int getMetadata(@Nonnull ItemStack stack) {
    return MathHelper.clamp(stack.getItemDamage(), 0, DefaultCapacitorData.values().length - 1);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(@Nonnull ItemStack stack, @Nonnull EntityPlayer par2EntityPlayer, @Nonnull List<String> par3List, boolean advanced) {
      par3List.add(EnderIO.lang.localize("machine.tooltip.upgrade"));
      if(SpecialTooltipHandler.showAdvancedTooltips()) {
        SpecialTooltipHandler.addDetailedTooltipFromResources(par3List, "enderio.machine.tooltip.upgrade");
      } else {
        SpecialTooltipHandler.addShowDetailsTooltip(par3List);
      }
    if (NbtValue.GLINT.hasTag(stack)) {
      par3List.add(EnderIO.lang.localize("loot.capacitor.entry." + NbtValue.CAPNO.getInt(stack), NbtValue.CAPNAME.getString(stack, "(!%$&§*&%*???")));
    }
  }

  @Override
  public @Nonnull ICapacitorData getCapacitorData(@Nonnull ItemStack stack) {
    return NullHelper.notnullJ(DefaultCapacitorData.values()[getMetadata(stack)], "Enum.values() has a null");
  }

  @Override
  public boolean hasEffect(@Nonnull ItemStack stack) {
    return super.hasEffect(stack) || NbtValue.GLINT.hasTag(stack);
  }

  @Override
  public @Nonnull EnumActionResult onItemUseFirst(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side,
      float hitX, float hitY, float hitZ, @Nonnull EnumHand hand) {

    if (world.isRemote || System.getProperty("INDEV") == null) {
      return EnumActionResult.PASS;
    }

    TileEntity te = world.getTileEntity(pos);
    if (te instanceof TileEntityChest) {
      TileEntityChest chest = (TileEntityChest) te;
      chest.clear();

      LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer) world);
      lootcontext$builder.withLuck(player.getLuck());

      // LootTable loottable = world.getLootTableManager().getLootTableFromLocation(LootTableList.CHESTS_SIMPLE_DUNGEON);
      LootTable loottable = world.getLootTableManager().getLootTableFromLocation(LootTableList.CHESTS_IGLOO_CHEST);
      loottable.fillInventory(chest, world.rand, lootcontext$builder.build());
      return EnumActionResult.PASS;
    }

    return EnumActionResult.PASS;
  }

}
