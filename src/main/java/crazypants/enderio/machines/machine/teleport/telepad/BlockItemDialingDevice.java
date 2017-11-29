package crazypants.enderio.machines.machine.teleport.telepad;

import com.enderio.core.common.util.Util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockItemDialingDevice extends ItemBlock {

  public BlockItemDialingDevice(Block b, String name) {
    super(b);
    setRegistryName(name);
  }

  @Override
  public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ,
      IBlockState newState) {

    boolean result = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
    TileEntity te = world.getTileEntity(pos);
    if (!(te instanceof TileDialingDevice)) {
      return result;
    }
    float dx = Math.abs(0.5f - hitX);
    float dy = Math.abs(0.5f - hitY);
    float dz = Math.abs(0.5f - hitZ);
    side = side.getOpposite();  
    DialerFacing facing = DialerFacing.DOWN_TONORTH;
    EnumFacing looking = Util.getFacingFromEntity(player);
    switch (side) {
    case DOWN:
      
      if (looking == EnumFacing.EAST) {
        facing = DialerFacing.DOWN_TOEAST;
      } else if (looking == EnumFacing.WEST) {
        facing = DialerFacing.DOWN_TOWEST;
      } else if (looking == EnumFacing.NORTH) {
        facing = DialerFacing.DOWN_TONORTH;
      } else if (looking == EnumFacing.SOUTH) {
        facing = DialerFacing.DOWN_TOSOUTH;
      }
      break;
    case UP:      
      if (looking == EnumFacing.EAST) {
        facing = DialerFacing.UP_TOEAST;
      } else if (looking == EnumFacing.WEST) {
        facing = DialerFacing.UP_TOWEST;
      } else if (looking == EnumFacing.NORTH) {
        facing = DialerFacing.UP_TONORTH;
      } else if (looking == EnumFacing.SOUTH) {
        facing = DialerFacing.UP_TOSOUTH;
      }
      break;
    case NORTH:
      if (dx < dy) {
        facing = hitY > 0.5 ? DialerFacing.NORTH_TOUP : DialerFacing.NORTH_TODOWN;
      } else {
        facing = hitX > 0.5 ? DialerFacing.NORTH_TOEAST : DialerFacing.NORTH_TOWEST;
      }
      break;
    case SOUTH:
      if (dx < dy) {
        facing = hitY > 0.5 ? DialerFacing.SOUTH_TOUP : DialerFacing.SOUTH_TODOWN;
      } else {
        facing = hitX > 0.5 ? DialerFacing.SOUTH_TOEAST : DialerFacing.SOUTH_TOWEST;
      }
      break;
    case WEST:
      if (dy < dz) {
        facing = hitZ > 0.5 ? DialerFacing.WEST_TOSOUTH : DialerFacing.WEST_TONORTH;
      } else {
        facing = hitY > 0.5 ? DialerFacing.WEST_TOUP : DialerFacing.WEST_TODOWN;
      }
      break;
    case EAST:
      if (dy < dz) {
        facing = hitZ > 0.5 ? DialerFacing.EAST_TOSOUTH : DialerFacing.EAST_TONORTH;
      } else {
        facing = hitY > 0.5 ? DialerFacing.EAST_TOUP : DialerFacing.EAST_TODOWN;
      }
      break;
    default:
      facing = DialerFacing.DOWN_TOWEST;
      break;
    }
    TileDialingDevice logicTileEntity = (TileDialingDevice) te;
    logicTileEntity.setFacing(facing);
    return result;

  }

}
