package crazypants.enderio.machine.hypercube;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraftforge.common.DimensionManager;

import crazypants.enderio.Log;

public class HyperCubeRegister {

    private static final String CATEGORY_PUBLIC_CHANNELS = "PublicChannels";
    private static final String CATEGORY_PRIVATE_CHANNELS = "PrivateChannels";
    private static final String KEY_PUBLIC_CHANNELS = "names";

    public static HyperCubeRegister instance;

    private final Map<Channel, List<TileHyperCube>> channelMapping = new HashMap<Channel, List<TileHyperCube>>();

    private final List<Channel> publicChannels = new ArrayList<Channel>();
    private final List<Channel> publicChannelsRO = Collections.unmodifiableList(publicChannels);

    private final Map<UUID, List<Channel>> userChannels = new HashMap<UUID, List<Channel>>();

    // private Configuration config;
    private HyperCubeConfig conf;

    public static void load() {
        instance = new HyperCubeRegister();
        instance.innerLoad();
    }

    public static void unload() {
        instance = new HyperCubeRegister();
    }

    private void innerLoad() {
        File f = DimensionManager.getCurrentSaveRootDirectory();
        conf = new HyperCubeConfig(
                new File(DimensionManager.getCurrentSaveRootDirectory(), "enderio/dimensionalTransceiver.cfg"));
        publicChannels.addAll(conf.getPublicChannels());
        userChannels.putAll(conf.getUserChannels());
    }

    public synchronized List<Channel> getChannelsForUser(UUID user) {
        List<Channel> result = userChannels.get(user);
        if (result == null) {
            result = new ArrayList<Channel>();
            userChannels.put(user, result);
        }
        return result;
    }

    public synchronized void register(TileHyperCube cube) {
        List<TileHyperCube> cubes = innerGetCubesForChannel(cube.getChannel());
        if (cubes != null && !cubes.contains(cube)) {
            cubes.add(cube);
        }
    }

    public synchronized void deregister(TileHyperCube cube, Channel channel) {
        List<TileHyperCube> cubes = innerGetCubesForChannel(channel);
        if (cubes != null) {
            cubes.remove(cube);
        }
    }

    public synchronized void deregister(TileHyperCube cube) {
        List<TileHyperCube> cubes = innerGetCubesForChannel(cube.getChannel());
        if (cubes != null) {
            cubes.remove(cube);
        }
    }

    public synchronized List<TileHyperCube> getCubesForChannel(Channel channel) {
        List<TileHyperCube> chans = innerGetCubesForChannel(channel);
        if (chans == null) {
            return Collections.emptyList();
        }
        return new ArrayList<TileHyperCube>(chans);
    }

    public synchronized void addChannel(Channel channel) {
        if (channel == null || channel.name == null) {
            return;
        }
        if (channel.user == null) {
            if (!publicChannels.contains(channel)) {
                publicChannels.add(channel);
                updateConfig();
            }
        } else {
            List<Channel> channels = getChannelsForUser(channel.user);
            if (!channels.contains(channel)) {
                channels.add(channel);
                updateConfig();
            }
        }
    }

    public void removeChannel(Channel channel) {
        if (channel == null || channel.name == null) {
            return;
        }
        if (channel.user == null) {
            if (publicChannels.contains(channel)) {
                publicChannels.remove(channel);
                updateConfig();
            }
        } else {
            List<Channel> channels = getChannelsForUser(channel.user);
            if (channels.contains(channel)) {
                channels.remove(channel);
                updateConfig();
            }
        }
    }

    private void updateConfig() {

        if (conf == null) {
            Log.warn("HyperCubeRegister.updateConfig: Config was null.");
            return;
        }

        conf.setPublicChannels(publicChannels);
        conf.setUserChannels(userChannels);
        conf.save();
    }

    private List<TileHyperCube> innerGetCubesForChannel(Channel channel) {
        if (channel == null) {
            return null;
        }
        List<TileHyperCube> result = channelMapping.get(channel);
        if (result == null) {
            result = new ArrayList<TileHyperCube>();
            channelMapping.put(channel, result);
        }
        return result;
    }

    public List<Channel> getPublicChannels() {
        return publicChannelsRO;
    }
}
