package crazypants.enderio.machine.tank;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.BlockEnder;

import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiID;
import crazypants.enderio.config.Config;
import crazypants.enderio.init.IModObject;
import crazypants.enderio.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IHaveTESR;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.render.property.EnumRenderMode;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTank extends AbstractMachineBlock<TileTank>
    implements IAdvancedTooltipProvider, IPaintable.INonSolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint, IHaveTESR {

  public static BlockTank create(@Nonnull IModObject modObject) {
    PacketHandler.INSTANCE.registerMessage(PacketTankFluid.class, PacketTankFluid.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketTankVoidMode.class, PacketTankVoidMode.class, PacketHandler.nextID(), Side.SERVER);
    BlockTank res = new BlockTank(modObject);
    res.init();
    return res;
  }

  protected BlockTank(@Nonnull IModObject modObject) {
    super(modObject, TileTank.class);
    setSoundType(SoundType.GLASS);
    setLightOpacity(0);
    setDefaultState(
        this.blockState.getBaseState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.AUTO).withProperty(EnumTankType.KIND, EnumTankType.NORMAL));
  }

  @Override
  public ItemBlock createBlockItem(IModObject modObject) {
    return modObject.apply(new BlockItemTank(this));
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { EnumRenderMode.RENDER, EnumTankType.KIND });
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(EnumTankType.KIND, EnumTankType.getType(meta));
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return EnumTankType.getMeta(state.getValue(EnumTankType.KIND));
  }

  @Override
  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    return state.withProperty(EnumRenderMode.RENDER, EnumRenderMode.AUTO);
  }

  @Override
  public int damageDropped(IBlockState st) {
    return getMetaFromState(st);
  }

  @Override
  public @Nonnull TileEntity createTileEntity(World world, IBlockState state) {
    return new TileTank(state.getValue(EnumTankType.KIND));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean shouldSideBeRendered(IBlockState bs, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
    return true;
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileTank te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new ContainerTank(player.inventory, te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileTank te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new GuiTank(player.inventory, te);
    }
    return null;
  }

  @Override
  public boolean isOpaqueCube(IBlockState bs) {
    return false;
  }

  @Override
  protected GuiID getGuiId() {
    return GuiID.GUI_ID_TANK;
  }

  @Override
  public int getLightValue(IBlockState bs, IBlockAccess world, BlockPos pos) {
    TileTank tank = BlockEnder.getAnyTileEntitySafe(world, pos, TileTank.class);
    if (tank != null) {
      FluidStack stack = tank.tank.getFluid();
      return stack == null || stack.amount <= 0 ? 0 : stack.getFluid().getLuminosity(stack);
    }
    return super.getLightValue(bs, world, pos);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
  }

  @Override
  public float getExplosionResistance(World world, BlockPos pos, Entity par1Entity, Explosion explosion) {
    IBlockState state = world.getBlockState(pos);
    if (state.getValue(EnumTankType.KIND).isExplosionResistant()) {
      return Config.EXPLOSION_RESISTANT;
    } else {
      return super.getExplosionResistance(par1Entity);
    }
  }

  @Override
  public boolean hasComparatorInputOverride(IBlockState bs) {
    return true;
  }

  @Override
  public int getComparatorInputOverride(IBlockState bs, World w, BlockPos pos) {
    TileTank te = getTileEntity(w, pos);
    if (te != null) {
      return te.getComparatorOutput();
    }
    return 0;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    SpecialTooltipHandler.addDetailedTooltipFromResources(list, itemstack);
    if (EnumTankType.getType(itemstack).isExplosionResistant()) {
      list.add(TextFormatting.ITALIC + EnderIO.lang.localize("blastResistant"));
    }
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    return stack.getUnlocalizedName();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IItemRenderMapper getItemRenderMapper() {
    return TankItemRenderMapper.instance;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return TankItemRenderMapper.instance;
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileTank tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing()).addCacheKey(blockStateWrapper.getValue(EnumTankType.KIND));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void bindTileEntitySpecialRenderer() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileTank.class, new TankFluidRenderer());
  }

}
