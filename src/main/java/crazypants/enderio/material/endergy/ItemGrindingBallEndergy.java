package crazypants.enderio.material.endergy;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.ModObject;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;

public class ItemGrindingBallEndergy extends ItemAlloyEndergy {

    public static ItemGrindingBallEndergy create() {
        ItemGrindingBallEndergy ball = new ItemGrindingBallEndergy();
        ball.init();
        return ball;
    }

    private ItemGrindingBallEndergy() {
        setUnlocalizedName(ModObject.itemGrindingBallEndergy.unlocalisedName);
    }

    private void init() {
        GameRegistry.registerItem(this, ModObject.itemGrindingBallEndergy.unlocalisedName);
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
        int numAlloys = AlloyEndergy.values().length;
        for (int i = 0; i < numAlloys; i++)
            icons[i] = IIconRegister.registerIcon(AlloyEndergy.values()[i].iconKey + "GrindingBall");
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, numItems - 1);
        return AlloyEndergy.values()[i].unlocalisedName + "GrindingBall";
    }
}
