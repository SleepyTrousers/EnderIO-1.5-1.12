package crazypants.enderio.teleport.telepad;

import java.util.List;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Vector3d;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemCoordSelector extends Item implements IResourceTooltipProvider {

  public static ItemCoordSelector create() {
    ItemCoordSelector ret = new ItemCoordSelector();
    GameRegistry.register(ret);
    return ret;
  }

  private ItemCoordSelector() {
    setCreativeTab(EnderIOTab.tabEnderIO);    
    setRegistryName(ModObject.itemCoordSelector.name());
    setUnlocalizedName(ModObject.itemCoordSelector.getUnlocalisedName());
    setMaxStackSize(1);
  }
  
  @Override
  public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems) {
    ItemStack stack = new ItemStack(item);
    subItems.add(stack);
  }
  
  @Override
  public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {    
    if (printCoords(stack, world, player)) {
      player.swingArm(hand);
    }
    return super.onItemRightClick(stack, world, player, hand);
  }


  private boolean printCoords(ItemStack stack, World world, EntityPlayer player) {
    
    Vector3d headVec = Util.getEyePositionEio(player);
    Vec3d start = headVec.getVec3();
    Vec3d lookVec = player.getLook(1.0F);
    double reach = 500;
    headVec.add(lookVec.xCoord * reach, lookVec.yCoord * reach, lookVec.zCoord * reach);
    RayTraceResult mop = world.rayTraceBlocks(start, headVec.getVec3());
    if (mop == null) {
      return false;
    }
    
    BlockCoord bc = new BlockCoord(mop);
    if(!player.isSneaking()) {
      EnumFacing dir = mop.sideHit;
      bc = bc.getLocation(dir);
    }
    
    player.openGui(EnderIO.instance, GuiHandler.GUI_ID_LOCATION_PRINTOUT_CREATE, world, bc.x, bc.y, bc.z);

    return true;
  }
  
  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }
}
