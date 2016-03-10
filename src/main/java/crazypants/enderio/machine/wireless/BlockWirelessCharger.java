package crazypants.enderio.machine.wireless;

import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.BlockEio;
import crazypants.enderio.ModObject;
import crazypants.enderio.network.PacketHandler;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

public class BlockWirelessCharger extends BlockEio<TileWirelessCharger> implements IResourceTooltipProvider /* IGuiHandler */{

  public static BlockWirelessCharger create() {

    PacketHandler.INSTANCE.registerMessage(PacketStoredEnergy.class, PacketStoredEnergy.class, PacketHandler.nextID(), Side.CLIENT);

    BlockWirelessCharger res = new BlockWirelessCharger();
    res.init();
    return res;
  }

  public static final PropertyBool RENDER_ACTIVE = PropertyBool.create("active");
  
  protected BlockWirelessCharger() {
    super(ModObject.blockWirelessCharger.unlocalisedName, TileWirelessCharger.class);
    setLightOpacity(1);
    setDefaultState(blockState.getBaseState().withProperty(RENDER_ACTIVE, false)); 
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public BlockState createBlockState() {
      return new BlockState(this,  RENDER_ACTIVE); 
  }

  @Override
  public int getMetaFromState(IBlockState state) {
      return 0;
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
      return getDefaultState();
  }

  @Override
  public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
    TileWirelessCharger te = getTileEntity(world, pos);    
    return state.withProperty(RENDER_ACTIVE, te != null && te.isActive());
  } 
  
  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }
  
  @Override
  public boolean doNormalDrops(IBlockAccess world, BlockPos pos) {  
    return false;
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
  
    super.onBlockPlacedBy(world, pos, state, player, stack);

    if(stack.getTagCompound()!= null) {
      TileEntity te = world.getTileEntity(pos);
      if(te instanceof TileWirelessCharger) {
        ((TileWirelessCharger) te).readCustomNBT(stack.getTagCompound());
      }
    }
  }

  @Override
  protected void processDrop(IBlockAccess world, BlockPos pos, @Nullable TileWirelessCharger te, ItemStack drop) {
    drop.setTagCompound(new NBTTagCompound());    
    te.writeCustomNBT(drop.getTagCompound());    
  }

}
