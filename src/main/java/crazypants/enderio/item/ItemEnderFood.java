package crazypants.enderio.item;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.google.common.collect.Lists;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.util.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.ModObject.itemEnderFood;

public class ItemEnderFood extends ItemFood implements IResourceTooltipProvider, IHaveRenderers {

  @SideOnly(Side.CLIENT)
  public class SpecialFont extends FontRenderer {

    private FontRenderer wrapped;
    private ItemStack stack = EnderFood.ENDERIOS.getStack();
    private int inARow = 0;

    public SpecialFont(FontRenderer wrapped) {
      super(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"), Minecraft.getMinecraft().renderEngine, false);
      this.wrapped = wrapped;
    }

    @Override
    public int drawString(String string, float x, float y, int color, boolean dropShadow) {
      boolean pop = false;
      if (isSmallText(string)) {
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        GL11.glTranslated(x, y, 0);
        GL11.glTranslated(0, -this.FONT_HEIGHT * inARow + FONT_HEIGHT, 0);
        inARow++;
        pop = true;
      } else {
        inARow = 0;
      }
      int ret = wrapped.drawString(string, x, y, color, dropShadow);
      if (pop) {
        GL11.glPopMatrix();
      }
      return ret;
    }

    @Override
    public int getStringWidth(String p_78256_1_) {
      int ret = wrapped.getStringWidth(p_78256_1_);
      if (isSmallText(p_78256_1_)) {
        ret /= 2;
      }
      return ret;
    }

    private boolean isSmallText(String string) {
      List<String> lines = Lists.newArrayList();
      SpecialTooltipHandler.addDetailedTooltipFromResources(lines, getUnlocalizedNameForTooltip(stack));
      return lines.contains(TextFormatting.getTextWithoutFormattingCodes(string));
    }

    @Override
    public int getCharWidth(char p_78263_1_) {
      return wrapped.getCharWidth(p_78263_1_);
    }
  }

  public enum EnderFood {
    ENDERIOS("itemEnderios", 10, 0.8f);

    public static List<ResourceLocation> resources() {
      List<ResourceLocation> res = new ArrayList<ResourceLocation>(values().length);
      for(EnderFood c : values()) {
        res.add(new ResourceLocation(EnderIO.MODID, c.unlocalisedName));
      }
      return res;
    }
        
    public final String unlocalisedName;
    public final int hunger;
    public final float saturation;

    public static final EnderFood[] VALUES = values();

    private EnderFood(String name, int hunger, float saturation) {    
      this.unlocalisedName = name;
      this.hunger = hunger;
      this.saturation = saturation;
    }

    public ItemStack getStack() {
      return getStack(1);
    }

    public ItemStack getStack(int size) {
      return new ItemStack(itemEnderFood.getItem(), size, ordinal());
    }

    public static EnderFood get(ItemStack stack) {
      return VALUES[stack.getItemDamage() % VALUES.length];
    }
  }

  public static ItemEnderFood create() {
    ItemEnderFood ret = new ItemEnderFood();
    GameRegistry.register(ret);
    return ret;
  }

  @SideOnly(Side.CLIENT)
  private SpecialFont fr;

  public ItemEnderFood() {
    super(0, false);
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    setMaxStackSize(1);
    setHasSubtypes(true);
    setRegistryName(ModObject.itemEnderFood.getUnlocalisedName());
  }
  
  @Override
  public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
    for (EnderFood f : EnderFood.VALUES) {
      list.add(f.getStack());
    }
  }

  @Override
  public String getUnlocalizedName(ItemStack itemStack) {
    return "enderio." + EnderFood.get(itemStack).unlocalisedName;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    List<ResourceLocation> names = EnderFood.resources();    
    ModelBakery.registerItemVariants(this, names.toArray(new ResourceLocation[names.size()]));    
    for (EnderFood c : EnderFood.values()) {
      ClientUtil.regRenderer(this, c.ordinal(), c.unlocalisedName);
    }     
  }

  @Override
  public int getHealAmount(ItemStack stack) {
    return EnderFood.get(stack).hunger;
  }

  @Override
  public float getSaturationModifier(ItemStack stack) {
    return EnderFood.get(stack).saturation;
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName(itemStack);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public FontRenderer getFontRenderer(ItemStack stack) {
    if (fr == null) {
      fr = new SpecialFont(Minecraft.getMinecraft().fontRendererObj);
    }
    return fr;
  }
}
