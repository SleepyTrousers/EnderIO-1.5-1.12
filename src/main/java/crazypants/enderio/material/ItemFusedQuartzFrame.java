package crazypants.enderio.material;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.painter.blocks.TileEntityPaintedBlock;
import crazypants.enderio.paint.PainterUtil2;

public class ItemFusedQuartzFrame extends Item {

  public static ItemFusedQuartzFrame create() {
    ItemFusedQuartzFrame result = new ItemFusedQuartzFrame();
    result.init();
    return result;
  }

  protected ItemFusedQuartzFrame() {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName("enderio." + ModObject.itemFusedQuartzFrame.name());
    setMaxStackSize(64);
  }

  protected void init() {
    GameRegistry.registerItem(this, ModObject.itemFusedQuartzFrame.unlocalisedName);
    // MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.unlocalisedName, new FramePainterRecipe(this));
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item item, CreativeTabs p_150895_2_, List list) {
    // TODO list.add(PainterUtil.applyDefaultPaintedState(new ItemStack(item)));
  }
  
  @Override
  public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {    

    if(world.getBlockState(pos).getBlock() == EnderIO.blockFusedQuartz) {
      TileEntityPaintedBlock tecb = (TileEntityPaintedBlock) world.getTileEntity(pos);
      if(tecb == null) {
        return false;
      }
      // TODO tecb.setSourceBlock(PainterUtil2.getSourceBlock(itemStack));
      world.markBlockForUpdate(pos);      
      if(!world.isRemote) {
        if(!player.capabilities.isCreativeMode) {
          itemStack.stackSize--;
        }
      }
      return true;
    } else {
      return false;
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean isFull3D() {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack item, EntityPlayer par2EntityPlayer, List<String> list, boolean par4) {
    super.addInformation(item, par2EntityPlayer, list, par4);
    list.add(PainterUtil2.getTooltTipText(item));
  }

  // public final class FramePainterRecipe extends BasicPainterTemplate {
  //
  // private ItemFusedQuartzFrame i;
  //
  // public FramePainterRecipe(ItemFusedQuartzFrame itemFusedQuartzFrame) {
  // i = itemFusedQuartzFrame;
  // }
  //
  // @Override
  // public boolean isValidTarget(ItemStack target) {
  // return target != null && target.getItem() == i;
  // }
  //
  // }
}
