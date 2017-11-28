package crazypants.enderio.machine.alloy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.GuiID;
import crazypants.enderio.init.IModObject;
import crazypants.enderio.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.registry.TextureRegistry;
import crazypants.enderio.render.registry.TextureRegistry.TextureSupplier;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAlloySmelter extends AbstractMachineBlock<TileAlloySmelter> implements IPaintable.ISolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint {

  public static BlockAlloySmelter create(@Nonnull IModObject modObject) {
    BlockAlloySmelter res = new BlockAlloySmelter(modObject);
    res.init();
    return res;
  }

  public static final TextureSupplier vanillaSmeltingOn = TextureRegistry.registerTexture("blocks/furnace_smelting_on");
  public static final TextureSupplier vanillaSmeltingOff = TextureRegistry.registerTexture("blocks/furnace_smelting_off");
  public static final TextureSupplier vanillaSmeltingOnly = TextureRegistry.registerTexture("blocks/furnace_smelting_only");

  private BlockAlloySmelter(@Nonnull IModObject modObject) {
    super(modObject, TileAlloySmelter.class);
  }

  protected BlockAlloySmelter(@Nonnull IModObject mo, @Nullable Class<TileAlloySmelter> teClass, @Nonnull Material mat) {
    super(mo, teClass, mat);
  }

  protected BlockAlloySmelter(@Nonnull IModObject mo, @Nullable Class<TileAlloySmelter> teClass) {
    super(mo, teClass);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if (te instanceof TileAlloySmelter) {
      return new ContainerAlloySmelter(player.inventory, (TileAlloySmelter) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if (te instanceof TileAlloySmelter) {
      return new GuiAlloySmelter(player.inventory, (TileAlloySmelter) te);
    }
    return null;
  }

  @Override
  protected @Nonnull GuiID getGuiId() {
    return GuiID.GUI_ID_ALLOY_SMELTER;
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileAlloySmelter tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing()).addCacheKey(tileEntity.isActive());
  }

}
