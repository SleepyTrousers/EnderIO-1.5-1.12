package crazypants.enderio.conduit;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.util.BlockCoord;
import crazypants.util.Util;

public abstract class AbstractItemConduit extends Item implements IConduitItem {

  protected ModObject modObj;

  protected ItemConduitSubtype[] subtypes;

  protected IIcon[] icons;

  protected AbstractItemConduit(ModObject modObj) {
    this.modObj = modObj;
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(modObj.unlocalisedName);
    setMaxStackSize(64);
    setHasSubtypes(true);
  }

  protected void init(ItemConduitSubtype[] subtypes) {
    this.subtypes = subtypes;
    icons = new IIcon[subtypes.length];
    GameRegistry.registerItem(this, modObj.unlocalisedName);
  }

  @Override
  public void registerIcons(IIconRegister IIconRegister) {
    int index = 0;
    for (ItemConduitSubtype subtype : subtypes) {
      icons[index] = IIconRegister.registerIcon(subtype.iconKey);
      index++;
    }
  }

  @Override
  public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

    BlockCoord placeAt = Util.canPlaceItem(stack, EnderIO.blockConduitBundle, player, world, x, y, z, side);
    if(placeAt != null) {
      if(!world.isRemote) {
        if(world.setBlock(placeAt.x, placeAt.y, placeAt.z, EnderIO.blockConduitBundle, 0, 1)) {
          TileEntity te = world.getTileEntity(placeAt.x, placeAt.y, placeAt.z);
          if(te instanceof IConduitBundle) {
            IConduitBundle bundle = (IConduitBundle) te;
            bundle.addConduit(createConduit(stack, player));
            Block b = EnderIO.blockConduitBundle;
            world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, b.stepSound.getStepResourcePath(),
                (b.stepSound.getVolume() + 1.0F) / 2.0F, b.stepSound.getPitch() * 0.8F);
          }
        }
      }
      if(!player.capabilities.isCreativeMode) {
        stack.stackSize--;
      }
      return true;

    } else {

      ForgeDirection dir = ForgeDirection.values()[side];
      int placeX = x + dir.offsetX;
      int placeY = y + dir.offsetY;
      int placeZ = z + dir.offsetZ;

      if(world.getBlock(placeX, placeY, placeZ) == EnderIO.blockConduitBundle) {

        IConduitBundle bundle = (TileConduitBundle) world.getTileEntity(placeX, placeY, placeZ);
        if(bundle == null) {
          System.out.println("AbstractItemConduit.onItemUse: Bundle null");
          return false;
        }
        IConduit con = createConduit(stack, player);
        if(con == null) {
          System.out.println("AbstractItemConduit.onItemUse: Conduit null.");
          return false;
        }
        if(bundle.getConduit(con.getBaseConduitType()) == null) {
          if(!world.isRemote) {
            bundle.addConduit(con);
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
  public IIcon getIconFromDamage(int damage) {
    damage = MathHelper.clamp_int(damage, 0, subtypes.length - 1);
    return icons[damage];
  }

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, subtypes.length - 1);
    return subtypes[i].unlocalisedName;

  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
    for (int j = 0; j < subtypes.length; ++j) {
      par3List.add(new ItemStack(this, 1, j));
    }
  }

}
