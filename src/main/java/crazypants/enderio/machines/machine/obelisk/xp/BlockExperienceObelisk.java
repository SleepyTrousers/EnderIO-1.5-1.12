package crazypants.enderio.machines.machine.obelisk.xp;

import java.util.Random;

import javax.annotation.Nonnull;

import crazypants.enderio.base.GuiID;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.machines.machine.obelisk.AbstractBlockObelisk;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockExperienceObelisk extends AbstractBlockObelisk<TileExperienceObelisk> {

  public static BlockExperienceObelisk create(@Nonnull IModObject modObject) {
    BlockExperienceObelisk res = new BlockExperienceObelisk(modObject);
    res.init();
    return res;
  }

  private BlockExperienceObelisk(@Nonnull IModObject modObject) {
    super(modObject, TileExperienceObelisk.class);
  }

  @Override
  protected boolean isActive(@Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos) {
    return true;
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName();
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileExperienceObelisk te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new ContainerExperienceObelisk(te);
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
  protected @Nonnull GuiID getGuiId() {
    return GuiID.GUI_ID_XP_OBELISK;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void randomDisplayTick(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
    // Has no particles
  }

}
