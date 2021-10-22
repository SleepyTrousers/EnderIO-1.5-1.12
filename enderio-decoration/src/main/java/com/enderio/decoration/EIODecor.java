package com.enderio.decoration;

import com.enderio.decoration.common.block.DecorBlocks;
import com.enderio.decoration.common.blockentity.DecorBlockEntities;
import com.enderio.decoration.common.entity.DecorEntities;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.NonNullLazyValue;
import net.minecraftforge.fml.common.Mod;

@Mod(EIODecor.MODID)
public class EIODecor {
    public static final String MODID = "enderiodecoration";
    public static final String DOMAIN = "enderio";

    private static final NonNullLazyValue<Registrate> REGISTRATE = new NonNullLazyValue<>(() -> Registrate.create(DOMAIN));

    public EIODecor() {
        DecorBlocks.register();
        DecorBlockEntities.register();
        DecorEntities.register();
    }

    public static Registrate registrate() {
        return REGISTRATE.get();
    }
}
