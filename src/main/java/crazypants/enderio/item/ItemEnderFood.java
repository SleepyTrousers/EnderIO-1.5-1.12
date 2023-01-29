package crazypants.enderio.item;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;

public class ItemEnderFood extends ItemFood implements IResourceTooltipProvider {

    @SideOnly(Side.CLIENT)
    public class SpecialFont extends FontRenderer {

        private FontRenderer wrapped;
        private ItemStack stack = EnderFood.ENDERIOS.getStack();
        private int inARow = 0;

        public SpecialFont(FontRenderer wrapped) {
            super(
                    Minecraft.getMinecraft().gameSettings,
                    new ResourceLocation("textures/font/ascii.png"),
                    Minecraft.getMinecraft().renderEngine,
                    false);
            this.wrapped = wrapped;
        }

        @Override
        public int drawString(String string, int x, int y, int color, boolean p_85187_5_) {
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
            int ret = wrapped.drawString(string, x, y, color, p_85187_5_);
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
            return lines.contains(EnumChatFormatting.getTextWithoutFormattingCodes(string));
        }

        @Override
        public int getCharWidth(char p_78263_1_) {
            return wrapped.getCharWidth(p_78263_1_);
        }
    }

    public enum EnderFood {

        ENDERIOS("itemEnderios", 10, 0.8f);

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
            return new ItemStack(EnderIO.itemEnderFood, size, ordinal());
        }

        public static EnderFood get(ItemStack stack) {
            return VALUES[stack.getItemDamage() % VALUES.length];
        }
    }

    public static ItemEnderFood create() {
        ItemEnderFood ret = new ItemEnderFood();
        GameRegistry.registerItem(ret, ModObject.itemEnderFood.unlocalisedName);
        return ret;
    }

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    @SideOnly(Side.CLIENT)
    private SpecialFont fr;

    public ItemEnderFood() {
        super(0, false);
        setCreativeTab(EnderIOTab.tabEnderIO);
        setMaxStackSize(1);
        setHasSubtypes(true);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (EnderFood f : EnderFood.VALUES) {
            list.add(f.getStack());
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack p_77667_1_) {
        return "enderio." + EnderFood.get(p_77667_1_).unlocalisedName;
    }

    @Override
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[EnderFood.VALUES.length];
        for (EnderFood f : EnderFood.VALUES) {
            icons[f.ordinal()] = register.registerIcon("enderio:" + f.unlocalisedName);
        }
    }

    @Override
    public IIcon getIconFromDamage(int damage) {
        return icons[damage % icons.length];
    }

    @Override
    public int func_150905_g(ItemStack p_150905_1_) {
        return EnderFood.get(p_150905_1_).hunger;
    }

    @Override
    public float func_150906_h(ItemStack p_150906_1_) {
        return EnderFood.get(p_150906_1_).saturation;
    }

    @Override
    public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
        return getUnlocalizedName(itemStack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public FontRenderer getFontRenderer(ItemStack stack) {
        if (fr == null) {
            fr = new SpecialFont(Minecraft.getMinecraft().fontRenderer);
        }
        return fr;
    }
}
