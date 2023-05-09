package crazypants.enderio.conduit.power.endergy;

import static crazypants.enderio.config.Config.powerConduitEndergyTiers;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.client.render.IconUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.power.PowerConduit;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.ICapacitor;

public class PowerConduitEndergy extends PowerConduit {

    static final Map<String, IIcon> ICONS = new HashMap<String, IIcon>();

    private static ICapacitor[] capacitors;

    static final String[] POSTFIX = new String[] { "Crude", "Iron", "Aluminum", "Gold", "Copper", "Silver", "Electrum",
            "EnergeticSilver", "Crystalline", "CrystallinePinkSlime", "Melodic", "Stellar" };

    static ICapacitor[] getCapacitors() {
        if (capacitors == null) {
            capacitors = new BasicCapacitor[] { new BasicCapacitor(powerConduitEndergyTiers[0]),
                    new BasicCapacitor(powerConduitEndergyTiers[1]), new BasicCapacitor(powerConduitEndergyTiers[2]),
                    new BasicCapacitor(powerConduitEndergyTiers[3]), new BasicCapacitor(powerConduitEndergyTiers[4]),
                    new BasicCapacitor(powerConduitEndergyTiers[5]), new BasicCapacitor(powerConduitEndergyTiers[6]),
                    new BasicCapacitor(powerConduitEndergyTiers[7]), new BasicCapacitor(powerConduitEndergyTiers[8]),
                    new BasicCapacitor(powerConduitEndergyTiers[9]), new BasicCapacitor(powerConduitEndergyTiers[10]),
                    new BasicCapacitor(powerConduitEndergyTiers[11]) };
        }
        return capacitors;
    }

    static ItemStack createItemStackForSubtype(int subtype) {
        ItemStack result = new ItemStack(EnderIO.itemPowerConduitEndergy, 1, subtype);
        return result;
    }

    public static void initIcons() {
        initIcons(POSTFIX, ICONS);
    }

    public PowerConduitEndergy() {
        super();
    }

    public PowerConduitEndergy(int meta) {
        super(meta);
    }

    @Override
    public ICapacitor getCapacitor() {
        return getCapacitors()[getSubtype()];
    }

    @Override
    public ItemStack createItem() {
        return createItemStackForSubtype(getSubtype());
    }

    // Rendering
    @Override
    public IIcon getTextureForState(CollidableComponent component) {
        if (component.dir == ForgeDirection.UNKNOWN) {
            return ICONS.get(ICON_CORE_KEY + POSTFIX[getSubtype()]);
        }
        if (COLOR_CONTROLLER_ID.equals(component.data)) {
            return IconUtil.whiteTexture;
        }
        return ICONS.get(ICON_KEY + POSTFIX[getSubtype()]);
    }

    @Override
    public IIcon getTextureForInputMode() {
        return ICONS.get(ICON_KEY_INPUT + POSTFIX[getSubtype()]);
    }

    @Override
    public IIcon getTextureForOutputMode() {
        return ICONS.get(ICON_KEY_OUTPUT + POSTFIX[getSubtype()]);
    }

    @Override
    public int getMaxEnergyStored() {
        return getCapacitors()[getSubtype()].getMaxEnergyStored();
    }
}
