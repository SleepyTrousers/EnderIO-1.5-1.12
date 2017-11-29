package crazypants.enderio.machines.machine.crafter;
/*package crazypants.enderio.base.machines.machine.crafter;

import javax.annotation.Nonnull;

import crazypants.enderio.base.GuiID;
import crazypants.enderio.base.machines.machine.MachineObject;
import crazypants.enderio.base.machines.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.base.machines.machine.render.RenderMappers;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCrafter extends AbstractMachineBlock<TileCrafter> implements IPaintable.ISolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint {

  public static BlockCrafter create() {
    PacketHandler.INSTANCE.registerMessage(PacketCrafter.class, PacketCrafter.class, PacketHandler.nextID(), Side.SERVER);
    BlockCrafter res = new BlockCrafter();
    res.init();
    return res;
  }

  protected BlockCrafter() {
    super(MachineObject.blockCrafter, TileCrafter.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileCrafter te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new ContainerCrafter(player.inventory, te, null);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileCrafter te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new GuiCrafter(player.inventory, te);
    }
    return null;
  }

  @Override
  protected GuiID getGuiId() {
    return GuiID.GUI_ID_CRAFTER;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IItemRenderMapper getItemRenderMapper() {
    return RenderMappers.FRONT_MAPPER;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return RenderMappers.FRONT_MAPPER;
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileCrafter tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing());
  }

}
*/