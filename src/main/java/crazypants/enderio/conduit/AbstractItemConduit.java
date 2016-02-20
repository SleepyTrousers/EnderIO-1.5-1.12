package crazypants.enderio.conduit;

import java.util.List;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.Util;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractItemConduit extends Item implements IConduitItem {

  protected ModObject modObj;

  protected ItemConduitSubtype[] subtypes;

//  protected IIcon[] icons;

  protected AbstractItemConduit(ModObject modObj, ItemConduitSubtype... subtypes) {
    this.modObj = modObj;
    this.subtypes = subtypes;
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(modObj.unlocalisedName);
    setMaxStackSize(64);
    setHasSubtypes(true);
  }

  protected void init() {
    GameRegistry.registerItem(this, modObj.unlocalisedName);
  }

//  @Override
//  @SideOnly(Side.CLIENT)
//  public void registerIcons(IIconRegister IIconRegister) {
//    icons = new IIcon[subtypes.length];
//    int index = 0;
//    for (ItemConduitSubtype subtype : subtypes) {
//      icons[index] = IIconRegister.registerIcon(subtype.iconKey);
//      index++;
//    }
//  }

  @Override
  public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
 
//    if (MicroblocksUtil.supportMicroblocks() && tryAddToMicroblocks(stack, player, world, pos, side)) {
//      return true;
//    }
    
    BlockCoord placeAt = Util.canPlaceItem(stack, EnderIO.blockConduitBundle, player, world, pos, side);
//    BlockCoord placeAt = new BlockCoord(pos.offset(side));
    if(placeAt != null) {
      if(!world.isRemote) {
        if(world.setBlockState(placeAt.getBlockPos(), EnderIO.blockConduitBundle.getDefaultState(), 1)) {
          TileEntity te = world.getTileEntity(placeAt.getBlockPos());
          if(te instanceof IConduitBundle) {
            IConduitBundle bundle = (IConduitBundle) te;
            bundle.addConduit(createConduit(stack, player));
            ConduitUtil.playBreakSound(Block.soundTypeMetal, world, placeAt.x, placeAt.y, placeAt.z);
          }
        }
      }
      if(!player.capabilities.isCreativeMode) {
        stack.stackSize--;
      }
      return true;

    } else {

      
      BlockPos place = pos.offset(side);

      if(world.getBlockState(place).getBlock() == EnderIO.blockConduitBundle) {

        IConduitBundle bundle = (IConduitBundle) world.getTileEntity(place);
        if(bundle == null) {
          System.out.println("AbstractItemConduit.onItemUse: Bundle null");
          return false;
        }
        if(!bundle.hasType(getBaseConduitType())) {
          if(!world.isRemote) {
            IConduit con = createConduit(stack, player);
            if(con == null) {
              System.out.println("AbstractItemConduit.onItemUse: Conduit null.");
              return false;
            }
            bundle.addConduit(con);
            ConduitUtil.playBreakSound(Block.soundTypeMetal, world, place.getX(), place.getY(), place.getZ());
            if(!player.capabilities.isCreativeMode) {
              stack.stackSize--;
            }
          }
          return true;
        }
      }
    }

    return false;
  }


  @Override
  public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {   
    // Conduit replacement
    if (player.isSneaking()) {
      return false;
    }
    TileEntity te = world.getTileEntity(pos);
    if (te == null || !(te instanceof IConduitBundle)) {
      return false;
    }
    IConduitBundle bundle = (IConduitBundle) te;
    IConduit existingConduit = bundle.getConduit(getBaseConduitType());
    if (existingConduit == null) {
      return false;
    }
    ItemStack existingConduitAsItemStack = existingConduit.createItem();
    if (!ItemUtil.areStacksEqual(existingConduitAsItemStack, stack)) {
      if (!world.isRemote) {
        IConduit newConduit = createConduit(stack, player);
        if (newConduit == null) {
          System.out.println("AbstractItemConduit.onItemUse: Conduit null.");
          return false;
        }
        bundle.removeConduit(existingConduit);
        bundle.addConduit(newConduit);
        if (!player.capabilities.isCreativeMode) {
          stack.stackSize--;
          for (ItemStack drop : existingConduit.getDrops()) {
            if (!player.inventory.addItemStackToInventory(drop)) {
              ItemUtil.spawnItemInWorldWithRandomMotion(world, drop,pos);
            }
          }
          player.inventoryContainer.detectAndSendChanges();
        }
        return true;
      } else {
        player.swingItem();
      }
    }
    return false;
  }
  
//  @Override
//  @SideOnly(Side.CLIENT)
//  public IIcon getIconFromDamage(int damage) {
//    damage = MathHelper.clamp_int(damage, 0, subtypes.length - 1);
//    return icons[damage];
//  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, subtypes.length - 1);
    return subtypes[i].unlocalisedName;

  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
    for (int j = 0; j < subtypes.length; ++j) {
      par3List.add(new ItemStack(this, 1, j));
    }
  }

}
