package crazypants.enderio.material;

import java.util.List;

import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.capacitor.DefaultCapacitorData;
import crazypants.enderio.capacitor.ICapacitorData;
import crazypants.enderio.capacitor.ICapacitorDataItem;
import crazypants.enderio.enderface.PacketOpenServerGUI;
import crazypants.enderio.machine.invpanel.TileInventoryPanel;
import crazypants.enderio.network.PacketHandler;
import crazypants.util.ClientUtil;
import crazypants.util.NbtValue;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.util.NbtValue.REMOTE_D;
import static crazypants.util.NbtValue.REMOTE_X;
import static crazypants.util.NbtValue.REMOTE_Y;
import static crazypants.util.NbtValue.REMOTE_Z;

public class ItemCapacitor extends Item implements ICapacitorDataItem {

  public static ItemCapacitor create() {
    ItemCapacitor result = new ItemCapacitor();
    result.init();
    return result;
  }

  protected ItemCapacitor() {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemBasicCapacitor.getUnlocalisedName());
    setRegistryName(ModObject.itemBasicCapacitor.getUnlocalisedName());
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);
  }

  protected void init() {
    GameRegistry.register(this);
  }

  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    final ResourceLocation[] resourceLocations = DefaultCapacitorData.getResourceLocations();
    ModelBakery.registerItemVariants(this, resourceLocations);
    for (int i = 0; i < resourceLocations.length; i++) {
      ClientUtil.regRenderer(this, i, resourceLocations[i]);
    }
  }
  
  @Override
  public String getUnlocalizedName(ItemStack stack) {
    return getCapacitorData(stack).getUnlocalizedName();
  }

  @Override  
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
    for (int j = 0; j < DefaultCapacitorData.values().length - 1; ++j) {
      par3List.add(new ItemStack(par1, 1, j));
    }
  }

  @Override
  public int getMetadata(ItemStack stack) {
    return MathHelper.clamp_int(stack != null ? stack.getItemDamage() : 0, 0, DefaultCapacitorData.values().length - 1);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List<String> par3List, boolean par4) {
    if (getMetadata(stack) > 0) {
      par3List.add(EnderIO.lang.localize("machine.tooltip.upgrade"));
      if(SpecialTooltipHandler.showAdvancedTooltips()) {
        SpecialTooltipHandler.addDetailedTooltipFromResources(par3List, "enderio.machine.tooltip.upgrade");
      } else {
        SpecialTooltipHandler.addShowDetailsTooltip(par3List);
      }
    }
    if (NbtValue.GLINT.hasTag(stack)) {
      par3List.add(EnderIO.lang.localize("loot.capacitor.entry." + NbtValue.CAPNO.getInt(stack), NbtValue.CAPNAME.getString(stack, "(!%$&§*&%*???")));
    }

  }

  @Override
  public ICapacitorData getCapacitorData(ItemStack stack) {
    return DefaultCapacitorData.values()[getMetadata(stack)];
  }

  @Override
  public boolean hasEffect(ItemStack stack) {
    return super.hasEffect(stack) || NbtValue.GLINT.hasTag(stack);
  }

  @Override
  public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ,
      EnumHand hand) {

    if (world.isRemote || System.getProperty("INDEV") == null) {
      return EnumActionResult.PASS;
    }
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof TileEntityChest) {
      TileEntityChest chest = (TileEntityChest) te;
      chest.clear();

      LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer) world);
      if (player != null) {
        lootcontext$builder.withLuck(player.getLuck());
      }

      LootTable loottable = world.getLootTableManager().getLootTableFromLocation(LootTableList.CHESTS_SIMPLE_DUNGEON);
      loottable.fillInventory(chest, world.rand, lootcontext$builder.build());
      return EnumActionResult.PASS;
    }
    if (player.isSneaking() && te instanceof TileInventoryPanel) {
      REMOTE_X.setInt(stack, te.getPos().getX());
      REMOTE_Y.setInt(stack, te.getPos().getY());
      REMOTE_Z.setInt(stack, te.getPos().getZ());
      REMOTE_D.setInt(stack, te.getWorld().provider.getDimension());
      player.addChatMessage(new TextComponentString(EnderIO.lang.localize("foobar")));
      return EnumActionResult.SUCCESS;
    }
    return EnumActionResult.PASS;
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(ItemStack equipped, World world, EntityPlayer player, EnumHand hand) {
    if (!player.isSneaking() && REMOTE_X.hasTag(equipped)) {
      int x = REMOTE_X.getInt(equipped);
      int y = REMOTE_Y.getInt(equipped);
      int z = REMOTE_Z.getInt(equipped);
      int d = REMOTE_D.getInt(equipped);

      Vec3d relativeHit = new Vec3d(0, 0, 0);
      PacketOpenServerGUI p = new PacketOpenServerGUI(x, y, z, EnumFacing.UP, relativeHit);
      PacketHandler.INSTANCE.sendToServer(p);
      player.addChatMessage(new TextComponentString(EnderIO.lang.localize("barfoo")));

      player.swingArm(hand);
      return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
    }
    return super.onItemRightClick(equipped, world, player, hand);
  }

}
