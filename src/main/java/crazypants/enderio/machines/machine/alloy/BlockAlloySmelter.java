package crazypants.enderio.machines.machine.alloy;

import javax.annotation.Nonnull;

import crazypants.enderio.base.GuiID;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAlloySmelter<T extends TileAlloySmelter> extends AbstractMachineBlock<T>
    implements IPaintable.ISolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint {

  public static BlockAlloySmelter<TileAlloySmelter> create(@Nonnull IModObject modObject) {
    BlockAlloySmelter<TileAlloySmelter> res = new BlockAlloySmelter<TileAlloySmelter>(modObject, TileAlloySmelter.class, GuiID.GUI_ID_ALLOY_SMELTER);
    res.init();
    return res;
  }

  public static BlockAlloySmelter<TileAlloySmelter.Simple> create_simple(@Nonnull IModObject modObject) {
    BlockAlloySmelter<TileAlloySmelter.Simple> res = new BlockAlloySmelter<TileAlloySmelter.Simple>(modObject, TileAlloySmelter.Simple.class,
        GuiID.GUI_ID_SIMPLE_ALLOY_SMELTER);
    res.init();
    return res;
  }

  private final @Nonnull GuiID guiID;

  public static final TextureSupplier vanillaSmeltingOn = TextureRegistry.registerTexture("blocks/furnace_smelting_on");
  public static final TextureSupplier vanillaSmeltingOff = TextureRegistry.registerTexture("blocks/furnace_smelting_off");
  public static final TextureSupplier vanillaSmeltingOnly = TextureRegistry.registerTexture("blocks/furnace_smelting_only");

  protected BlockAlloySmelter(@Nonnull IModObject modObject, @Nonnull Class<T> te, @Nonnull GuiID guiID) {
    super(modObject, te);
    this.guiID = guiID;
  }

  @Override
  public Object getServerGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    T te = getTileEntity(world, pos);
    if (te != null) {
      return ContainerAlloySmelter.create(player.inventory, te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    T te = getTileEntity(world, pos);
    if (te != null) {
      return new GuiAlloySmelter<T>(player.inventory, te);
    }
    return null;
  }

  @Override
  protected @Nonnull GuiID getGuiId() {
    return guiID;
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileAlloySmelter tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing()).addCacheKey(tileEntity.isActive());
  }

}
