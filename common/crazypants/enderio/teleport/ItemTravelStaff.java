package crazypants.enderio.teleport;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;

public class ItemTravelStaff extends Item {

  public static ItemTravelStaff create() {
    ItemTravelStaff result = new ItemTravelStaff();
    result.init();
    return result;
  }

  protected ItemTravelStaff() {
    super(ModObject.itemTravelStaff.id);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName("enderio." + ModObject.itemTravelStaff.name());
    setMaxDamage(50000);
    setMaxStackSize(1);
  }

  protected void init() {
    GameRegistry.registerItem(this, ModObject.itemTravelStaff.unlocalisedName);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IconRegister iconRegister) {
    itemIcon = iconRegister.registerIcon("enderio:itemTravelStaff");
  }

  @Override
  public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
    int blockId = world.getBlockId(x, y, z);
    Block block = Block.blocksList[blockId];
    if(block != null && block.rotateBlock(world, x, y, z, ForgeDirection.getOrientation(side))) {
      player.swingItem();
      return !world.isRemote;
    }
    return false;
  }

  @Override
  public ItemStack onItemRightClick(ItemStack equipped, World world, EntityPlayer player) {

    if(world.isRemote) {
      TravelPlatformController.instance.travelToSelectedTarget(player);
    }
    player.swingItem();

    return equipped;
  }

}
