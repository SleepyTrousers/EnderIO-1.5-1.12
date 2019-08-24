package crazypants.enderio.base.item.darksteel;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShieldRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber(modid = EnderIO.MODID)
public class DarkShieldRecipes extends ShieldRecipes.Decoration {

  private static final @Nonnull String BASE = "Base";
  private static final @Nonnull String BLOCK_ENTITY_TAG = "BlockEntityTag";

  @SubscribeEvent
  public static void register(@Nonnull RegistryEvent.Register<IRecipe> event) {
    final IForgeRegistry<IRecipe> registry = event.getRegistry();
    registry.register(new DarkShieldRecipes().setRegistryName(EnderIO.DOMAIN, "dark_shield_recipes"));
  }

  @Override
  public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {
    ItemStack theShield = ItemStack.EMPTY;
    ItemStack theBanner = ItemStack.EMPTY;

    for (int i = 0; i < inv.getSizeInventory(); ++i) {
      ItemStack itemstack = inv.getStackInSlot(i);

      if (!itemstack.isEmpty()) {
        if (itemstack.getItem() == Items.BANNER) {
          if (!theBanner.isEmpty()) {
            return false;
          }

          theBanner = itemstack;
        } else {
          if (!(itemstack.getItem() instanceof ItemDarkSteelShield)) {
            return false;
          }

          if (!theShield.isEmpty()) {
            return false;
          }

          if (itemstack.getSubCompound(BLOCK_ENTITY_TAG) != null) {
            return false;
          }

          theShield = itemstack;
        }
      }
    }

    if (!theShield.isEmpty() && !theBanner.isEmpty()) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public @Nonnull ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
    ItemStack theBanner = ItemStack.EMPTY;
    ItemStack theShield = ItemStack.EMPTY;

    for (int i = 0; i < inv.getSizeInventory(); ++i) {
      ItemStack itemstack = inv.getStackInSlot(i);

      if (!itemstack.isEmpty()) {
        if (itemstack.getItem() == Items.BANNER) {
          theBanner = itemstack;
        } else if (itemstack.getItem() instanceof ItemDarkSteelShield) {
          theShield = itemstack.copy();
        }
      }
    }

    if (theShield.isEmpty() || theBanner.isEmpty()) {
      return theShield;
    } else {
      NBTTagCompound nbttagcompound = theBanner.getSubCompound(BLOCK_ENTITY_TAG);
      NBTTagCompound nbttagcompound1 = nbttagcompound == null ? new NBTTagCompound() : nbttagcompound.copy();
      nbttagcompound1.setInteger(BASE, theBanner.getMetadata() & 15);
      theShield.setTagInfo(BLOCK_ENTITY_TAG, nbttagcompound1);
      return theShield;
    }
  }

  @Override
  public @Nonnull ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }

  @Override
  public @Nonnull NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inv) {
    NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack> withSize(inv.getSizeInventory(), ItemStack.EMPTY);

    for (int i = 0; i < nonnulllist.size(); ++i) {
      ItemStack itemstack = inv.getStackInSlot(i);

      if (itemstack.getItem().hasContainerItem(itemstack)) {
        nonnulllist.set(i, itemstack.getItem().getContainerItem(itemstack));
      }
    }

    return nonnulllist;
  }

  @Override
  public boolean isDynamic() {
    return true;
  }

  @Override
  public boolean canFit(int width, int height) {
    return width * height >= 2;
  }
}
