package crazypants.enderio.zoo.item;

import crazypants.enderio.zoo.EnderZoo;
import crazypants.enderio.zoo.EnderZooTab;
import crazypants.enderio.zoo.RegistryHandler;
import crazypants.enderio.zoo.config.Config;
import crazypants.enderio.zoo.entity.EntityOwlEgg;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ItemOwlEgg extends Item {
  private static final float VELOCITY_DEFAULT = 1.5F;
  private static final float INACCURACY_DEFAULT = 1.0F;
  private static final float PITCHOFFSET = 0.0F;
  public static final String NAME = "owlegg";

  public static ItemOwlEgg create() {
    
    EntityRegistry.registerModEntity(new ResourceLocation(EnderZoo.MODID,"EntityOwlEgg"),
        EntityOwlEgg.class, "EntityOwlEgg", Config.entityOwlEggId, EnderZoo.instance, 64, 10, true);
    
    ItemOwlEgg res = new ItemOwlEgg();
    res.init();
    return res;
  }

  private ItemOwlEgg() {
    setUnlocalizedName(NAME);
    setRegistryName(NAME);
    setCreativeTab(EnderZooTab.tabEnderZoo);
    setHasSubtypes(false);
  }

  private void init() {
	RegistryHandler.ITEMS.add(this);
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
    ItemStack itemStackIn = playerIn.getHeldItem(hand);
    if (!playerIn.capabilities.isCreativeMode) {
      itemStackIn.shrink(1);
    }
    worldIn.playSound(playerIn, playerIn.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.BLOCKS, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
    if (!worldIn.isRemote) {    
      EntityOwlEgg entityEgg = new EntityOwlEgg(worldIn, playerIn);
      //without setHeading the egg just falls to the players feet
      entityEgg.setHeadingFromThrower(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, PITCHOFFSET, VELOCITY_DEFAULT, INACCURACY_DEFAULT);
      worldIn.spawnEntity(entityEgg);
    }
    return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
  }

}