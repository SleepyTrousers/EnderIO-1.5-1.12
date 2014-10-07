package crazypants.enderio.entity;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

//Code taken from Natura. Tanks!
public class ItemSpawnEgg extends Item {

  public static ItemSpawnEgg create() {
    ItemSpawnEgg res = new ItemSpawnEgg();
    res.init();
    return res;
  }

  int[] primaryColor = { 0x27624D};
  int[] secondaryColor = { 0x212121};
  String[] mobNames = { "EnderIO." + EntityEnderminy.NAME };
  
  private ItemSpawnEgg() {
    setUnlocalizedName("itemSpawnEggEnderIO");
    setCreativeTab(EnderIOTab.tabEnderIO);
    setHasSubtypes(true);
  }
  
  private void init() {
    GameRegistry.registerItem(this, "itemSpawnEggEnderIO");
  }

  @Override
  public void registerIcons(IIconRegister iconRegister) {
  }

  @Override
  public boolean requiresMultipleRenderPasses() {
    return true;
  }

  @Override
  public IIcon getIconFromDamageForRenderPass(int par1, int par2) {
    return Items.spawn_egg.getIconFromDamageForRenderPass(par1, par2);
  }

  @Override
  public String getItemStackDisplayName(ItemStack par1ItemStack) {
    String s = ("" + StatCollector.translateToLocal(getUnlocalizedName() + ".name")).trim();
    String s1 = mobNames[par1ItemStack.getItemDamage()];
    if(s1 != null) {
      s = s + " " + StatCollector.translateToLocal("entity." + s1 + ".name");
    }
    return s;
  }

  @Override
  public void getSubItems(Item id, CreativeTabs tab, List list) {
    for (int i = 0; i < mobNames.length; i++) {
      list.add(new ItemStack(id, 1, i));
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getColorFromItemStack(ItemStack stack, int pass) {
    int damage = stack.getItemDamage();
    return pass == 0 ? primaryColor[damage] : secondaryColor[damage];
  }

  @Override
  public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int posX, int posY, int posZ, int par7, float par8, float par9, float par10) {
    if(!world.isRemote) {
      activateSpawnEgg(stack, world, posX, posY, posZ, par7);
      if(!player.capabilities.isCreativeMode) {
        --stack.stackSize;
      }
    }
    return true;
  }

  public static EntityLiving activateSpawnEgg(ItemStack stack, World world, double posX, double posY, double posZ, int par7) {
    Block i1 = world.getBlock((int) posX, (int) posY, (int) posZ);
    posX += Facing.offsetsXForSide[par7];
    posY += Facing.offsetsYForSide[par7];
    posZ += Facing.offsetsZForSide[par7];
    double d0 = 0.0D;
    if(par7 == 1 && i1 != null && i1.getRenderType() == 11) {
      d0 = 0.5D;
    }

    int damage = stack.getItemDamage();
    EntityLiving entity = null;
    switch (damage) {
    case 0:
      entity = new EntityEnderminy(world);
      spawnEntity(posX, posY, posZ, entity, world);
      break;
    }
    return entity;
  }

  public static void spawnEntity(double x, double y, double z, EntityLiving entity, World world) {
    if(!world.isRemote) {
      entity.setPosition(x, y, z);
      world.spawnEntityInWorld(entity);
    }
  }
}
