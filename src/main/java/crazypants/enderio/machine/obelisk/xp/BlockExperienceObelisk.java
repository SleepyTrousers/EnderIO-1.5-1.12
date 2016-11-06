package crazypants.enderio.machine.obelisk.xp;

import java.util.Random;

import javax.annotation.Nonnull;

import crazypants.enderio.GuiID;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.ContainerNoInv;
import crazypants.enderio.machine.obelisk.AbstractBlockObelisk;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockExperienceObelisk extends AbstractBlockObelisk<TileExperienceObelisk> {

  public static BlockExperienceObelisk create() {
    BlockExperienceObelisk res = new BlockExperienceObelisk();
    res.init();
    return res;
  }

  private BlockExperienceObelisk() {
    super(ModObject.blockExperienceObelisk, TileExperienceObelisk.class);
  }

  @Override
  protected boolean isActive(@Nonnull IBlockAccess blockAccess, int x, int y, int z) {
    return true; 
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileExperienceObelisk te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new ContainerNoInv(te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileExperienceObelisk te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new GuiExperienceObelisk(player.inventory, te);
    }
    return null;
  }

  @Override
  protected GuiID getGuiId() {
    return GuiID.GUI_ID_XP_OBELISK;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void randomDisplayTick(IBlockState bs, World world, BlockPos pos, Random rand) {
    // Has no particles
  }

}
