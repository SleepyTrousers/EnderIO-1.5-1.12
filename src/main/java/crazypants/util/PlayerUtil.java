package crazypants.util;


import net.minecraftforge.common.UsernameCache;

import java.util.Map;
import java.util.UUID;


public class PlayerUtil {



    public static UUID getPlayerUUID(String username)
    {
        /*
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            GameProfile profile = MinecraftServer.getServer().func_152358_ax().func_152655_a(username);
            UUID uuid = null;
            if (profile == null)
                uuid = EntityPlayer.func_146094_a(new GameProfile(null, username));
            else
                uuid = profile.getId();
            return uuid;
        }
        else
            return PacketPlayerUUID.playerUUID.get(username);
            */
        for (Map.Entry<UUID,String> entry : UsernameCache.getMap().entrySet())
        {

            if (entry.getValue().equalsIgnoreCase(username))
            {
                return entry.getKey();
            }
        }
        return null;
    }


    public static UUID getPlayerUIDUnstable(String possibleUUID)
    {
        if(possibleUUID==null || possibleUUID.isEmpty())
            return null;
        UUID uuid=null;
        try
        {
            uuid=UUID.fromString(possibleUUID);
        }
        catch(IllegalArgumentException e)
        {
            uuid=getPlayerUUID(possibleUUID);
        }
        return uuid;
    }
}
