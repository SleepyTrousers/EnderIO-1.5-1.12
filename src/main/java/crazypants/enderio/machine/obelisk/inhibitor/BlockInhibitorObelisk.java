package crazypants.enderio.machine.obelisk.inhibitor;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import com.enderio.core.common.util.BlockCoord;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.teleport.TeleportEntityEvent;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.obelisk.BlockObeliskAbstract;

public class BlockInhibitorObelisk extends BlockObeliskAbstract<TileInhibitorObelisk> {

    public static BlockInhibitorObelisk instance;

    public static BlockInhibitorObelisk create() {
        BlockInhibitorObelisk res = new BlockInhibitorObelisk();
        res.init();
        MinecraftForge.EVENT_BUS.register(res);
        return instance = res;
    }

    protected BlockInhibitorObelisk() {
        super(ModObject.blockInhibitorObelisk, TileInhibitorObelisk.class);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return ID == getGuiId()
                ? new ContainerInhibitorObelisk(player.inventory, (AbstractMachineEntity) world.getTileEntity(x, y, z))
                : null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == getGuiId()) {
            TileInhibitorObelisk te = (TileInhibitorObelisk) world.getTileEntity(x, y, z);
            if (te != null) {
                return new GuiInhibitorObelisk(te, new ContainerInhibitorObelisk(player.inventory, te));
            }
        }
        return null;
    }

    @Override
    protected int getGuiId() {
        return GuiHandler.GUI_ID_INHIBITOR;
    }

    public Map<BlockCoord, Float> activeInhibitors = Maps.newHashMap();

    @SubscribeEvent
    public void onTeleport(TeleportEntityEvent event) {
        for (Entry<BlockCoord, Float> e : activeInhibitors.entrySet()) {
            BlockCoord bc = e.getKey();
            int dist = bc.getDist(new BlockCoord(event.targetX, event.targetY, event.targetZ));
            if (dist < e.getValue()) {
                TileEntity te = bc.getTileEntity(event.entity.worldObj);
                if (te instanceof TileInhibitorObelisk && ((TileInhibitorObelisk) te).isActive()) {
                    event.setCanceled(true);
                }
            }
        }
    }
}
