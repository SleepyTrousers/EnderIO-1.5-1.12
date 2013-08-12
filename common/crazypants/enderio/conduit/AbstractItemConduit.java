package crazypants.enderio.conduit;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.registry.*;
import crazypants.enderio.*;
import crazypants.enderio.material.Alloy;

public abstract class AbstractItemConduit extends Item implements IConduitItem {

  protected ModObject modObj;
  
  protected ItemConduitSubtype[] subtypes;
  
  protected Icon[] icons;
  
  protected AbstractItemConduit(ModObject modObj) {
    super(modObj.id);
    this.modObj = modObj;
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(modObj.unlocalisedName);
    setMaxStackSize(64);
    setHasSubtypes(true);
  }

  protected void init(ItemConduitSubtype[] subtypes) {
    this.subtypes = subtypes;
    icons = new Icon[subtypes.length];
    
    LanguageRegistry.addName(this, modObj.name);
    GameRegistry.registerItem(this, modObj.unlocalisedName);
    for(ItemConduitSubtype subtype : subtypes) {
      LanguageRegistry.instance().addStringLocalization(getUnlocalizedName() + "." + subtype.unlocalisedName + ".name", subtype.uiName);
    }
    
  }
  
  @Override
  public void registerIcons(IconRegister iconRegister) {
    int index = 0;
    for(ItemConduitSubtype subtype : subtypes) {
      icons[index] = iconRegister.registerIcon(subtype.iconKey);
      index++;
    }
  }

  @Override
  public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

    ForgeDirection dir = ForgeDirection.values()[side];
    int placeX = x + dir.offsetX;
    int placeY = y + dir.offsetY;
    int placeZ = z + dir.offsetZ;

    if (world.isAirBlock(placeX, placeY, placeZ)) {
      if(!world.isRemote) {
        if(world.setBlock(placeX, placeY, placeZ, ModObject.blockConduitBundle.actualId,0,1)) {
          IConduitBundle bundle = (IConduitBundle) world.getBlockTileEntity(placeX, placeY, placeZ);
          bundle.addConduit(createConduit(stack));
        }
      }
      if (!player.capabilities.isCreativeMode) {
        stack.stackSize--;
      }      
      return true;
    } else if(world.getBlockId(placeX, placeY, placeZ) == ModObject.blockConduitBundle.actualId) {
      
      IConduitBundle bundle = (TileConduitBundle)world.getBlockTileEntity(placeX, placeY, placeZ);
      if(bundle == null) {
        System.out.println("AbstractItemConduit.onItemUse: Bunle null");
        return false;
      }
      IConduit con = createConduit(stack);
      if(con == null) {
        System.out.println("AbstractItemConduit.onItemUse: Conduit null.");
        return false;
      }
      if(bundle.getConduit(con.getBaseConduitType()) == null) {
        if(!world.isRemote) {
          bundle.addConduit(con);
        }
        return true;        
      }
    }

    return false;
  }
  
  
  @Override
  public Icon getIconFromDamage(int damage) {
    damage = MathHelper.clamp_int(damage, 0, subtypes.length);
    return icons[damage];
  }


  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, subtypes.length);    
    return super.getUnlocalizedName() + "." + subtypes[i].unlocalisedName;
    
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {    
    for (int j = 0; j < subtypes.length; ++j) {
      par3List.add(new ItemStack(par1, 1, j));
    }    
  }
 
}
