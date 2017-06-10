package crazypants.enderio.material.alloy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.init.IModObject;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.util.ClientUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemAlloy extends Item implements IHaveRenderers {

  private final @Nonnull String suffix;

  public static ItemAlloy create(@Nonnull IModObject modObject) {
    return new ItemAlloy(modObject, "").init(modObject);
  }

  public static ItemAlloy createNuggets(@Nonnull IModObject modObject) {
    return new ItemAlloy(modObject, "_nugget").init(modObject);
  }

  private ItemAlloy(@Nonnull IModObject modObject, @Nonnull String suffix) {
    setHasSubtypes(true);
    setMaxDamage(0);
    setCreativeTab(EnderIOTab.tabEnderIOMaterials);
    modObject.apply(this);
    this.suffix = suffix;
  }

  private ItemAlloy init(@Nonnull IModObject modObject) {
    GameRegistry.register(this);
    return this;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject modObject) {
    NNList.of(Alloy.class).apply(new Callback<Alloy>() {
      @Override
      public void apply(@Nonnull Alloy alloy) {
        ClientUtil.regRenderer(ItemAlloy.this, Alloy.getMetaFromType(alloy), alloy.getBaseName() + suffix);
      }
    });
  }

  @Override
  public @Nonnull String getUnlocalizedName(@Nonnull ItemStack stack) {
    return Alloy.getTypeFromMeta(stack.getItemDamage()).getBaseName() + suffix;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(@Nonnull final Item item, @Nullable CreativeTabs par2CreativeTabs, @Nonnull final NonNullList<ItemStack> list) {
    NNList.of(Alloy.class).apply(new Callback<Alloy>() {
      @Override
      public void apply(@Nonnull Alloy alloy) {
        list.add(new ItemStack(item, 1, Alloy.getMetaFromType(alloy)));
      }
    });
  }

}
