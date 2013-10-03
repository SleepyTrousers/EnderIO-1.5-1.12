package crazypants.enderio.material;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.crafting.IEnderIoRecipe;
import crazypants.enderio.crafting.impl.EnderIoRecipe;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.painter.BasicPainterTemplate;
import crazypants.enderio.machine.painter.PainterUtil;
import crazypants.enderio.machine.painter.TileEntityCustomBlock;

public class ItemFusedQuartzFrame extends Item {

  public static ItemFusedQuartzFrame create() {
    ItemFusedQuartzFrame result = new ItemFusedQuartzFrame();
    result.init();
    return result;
  }

  protected ItemFusedQuartzFrame() {
    super(ModObject.itemFusedQuartzFrame.id);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemFusedQuartzFrame.unlocalisedName);
    setMaxStackSize(64);
  }

  protected void init() {
    LanguageRegistry.addName(this, ModObject.itemFusedQuartzFrame.name);
    GameRegistry.registerItem(this, ModObject.itemFusedQuartzFrame.unlocalisedName);
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.unlocalisedName, new FramePainterRecipe());
  }

  @Override
  public void registerIcons(IconRegister iconRegister) {
    //itemIcon = iconRegister.registerIcon("enderio:fusedQuartzFrame");
  }

  @Override
  public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float par8,
      float par9, float par10) {

    if(world.getBlockId(x, y, z) == ModObject.blockFusedQuartz.actualId) {
      TileEntityCustomBlock tecb = (TileEntityCustomBlock) world.getBlockTileEntity(x, y, z);
      if(tecb == null) {
        return false;
      }
      // if(!world.isRemote) {
      tecb.setSourceBlockId(PainterUtil.getSourceBlockId(itemStack));
      tecb.setSourceBlockMetadata(PainterUtil.getSourceBlockMetadata(itemStack));
      world.markBlockForUpdate(x, y, z);
      world.markBlockForRenderUpdate(x, y, z);
      // }
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
  public void addInformation(ItemStack item, EntityPlayer par2EntityPlayer, List list, boolean par4) {
    super.addInformation(item, par2EntityPlayer, list, par4);
    list.add(PainterUtil.getTooltTipText(item));
  }

  public static final class FramePainterRecipe extends BasicPainterTemplate {

    public FramePainterRecipe() {
      super(ModObject.itemFusedQuartzFrame.actualId);
    }

    @Override
    public List<IEnderIoRecipe> getAllRecipes() {
      ItemStack is = new ItemStack(ModObject.itemFusedQuartzFrame.actualId, 1, 0);
      IEnderIoRecipe recipe = new EnderIoRecipe(IEnderIoRecipe.PAINTER_ID, DEFAULT_ENERGY_PER_TASK, is, is);
      return Collections.singletonList(recipe);
    }
  }
}
