package crazypants.enderio.machine.alloy;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.registry.TextureRegistry;
import crazypants.enderio.render.registry.TextureRegistry.TextureSupplier;

public class BlockAlloySmelter extends AbstractMachineBlock<TileAlloySmelter> implements IPaintable.ISolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint {

  public static BlockAlloySmelter create() {

    PacketHandler.INSTANCE.registerMessage(PacketClientState.class, PacketClientState.class, PacketHandler.nextID(), Side.SERVER);

    BlockAlloySmelter res = new BlockAlloySmelter();
    res.init();
    return res;
  }

  public String name() {
    return name;
  }

  public static final TextureSupplier vanillaSmeltingOn = TextureRegistry.registerTexture("blocks/furnaceSmeltingOn");
  public static final TextureSupplier vanillaSmeltingOff = TextureRegistry.registerTexture("blocks/furnaceSmeltingOff");
  public static final TextureSupplier vanillaSmeltingOnly = TextureRegistry.registerTexture("blocks/furnaceSmeltingOnly");

  private BlockAlloySmelter() {
    super(ModObject.blockAlloySmelter, TileAlloySmelter.class);
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
  protected int getGuiId() {
    return GuiHandler.GUI_ID_ALLOY_SMELTER;
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileAlloySmelter tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing()).addCacheKey(tileEntity.isActive());
  }

}
