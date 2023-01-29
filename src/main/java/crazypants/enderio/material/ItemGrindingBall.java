package crazypants.enderio.material;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.ModObject;

public class ItemGrindingBall extends ItemAlloy {

    public static ItemGrindingBall create() {
        ItemGrindingBall ball = new ItemGrindingBall();
        ball.init();
        return ball;
    }

    private ItemGrindingBall() {
        setUnlocalizedName(ModObject.itemGrindingBall.unlocalisedName);
    }

    private void init() {
        GameRegistry.registerItem(this, ModObject.itemGrindingBall.unlocalisedName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        damage = MathHelper.clamp_int(damage, 0, numItems - 1);
        return icons[damage];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister IIconRegister) {
        int numAlloys = Alloy.values().length;
        for (int i = 0; i < numAlloys; i++)
            icons[i] = IIconRegister.registerIcon(Alloy.values()[i].iconKey + "GrindingBall");
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, numItems - 1);
        return Alloy.values()[i].unlocalisedName + "_ball";
    }
}
