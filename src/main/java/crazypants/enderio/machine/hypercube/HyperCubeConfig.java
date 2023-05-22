package crazypants.enderio.machine.hypercube;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

import com.enderio.core.common.util.PlayerUtil;

import crazypants.enderio.Log;

public class HyperCubeConfig {

    private static final String KEY_PUBLIC_CHANNELS = "public.chanels";

    private static final String DELIM = "~";
    private static final String DELIM_ESC = "/:/";

    private static final String KEY_USERS = "users";

    private static final String KEY_USER_CHANNEL = ".channels";

    private final Properties props = new Properties();

    private final List<Channel> publicChannels = new ArrayList<Channel>();

    private final Map<UUID, List<Channel>> userChannels = new HashMap<UUID, List<Channel>>();

    private final File file;

    public HyperCubeConfig(File file) {
        this.file = file;
        if (file.exists()) {
            load(file);
        }
    }

    public List<Channel> getPublicChannels() {
        return publicChannels;
    }

    public void setPublicChannels(Collection<Channel> chans) {
        publicChannels.clear();
        publicChannels.addAll(chans);
    }

    public Map<UUID, List<Channel>> getUserChannels() {
        return userChannels;
    }

    public void setUserChannels(Map<UUID, List<Channel>> channels) {
        userChannels.clear();
        userChannels.putAll(channels);
    }

    public void save() {
        props.clear();

        setChannelListProperty(KEY_PUBLIC_CHANNELS, publicChannels);

        StringBuilder userListStr = new StringBuilder();
        Iterator<Entry<UUID, List<Channel>>> itr = userChannels.entrySet().iterator();
        while (itr.hasNext()) {
            Entry<UUID, List<Channel>> entry = itr.next();
            UUID user = entry.getKey();

            List<Channel> channels = entry.getValue();
            if (user != null && channels != null && !channels.isEmpty()) {
                userListStr.append(user.toString());
                setChannelListProperty(user + KEY_USER_CHANNEL, channels);
            }
            if (itr.hasNext()) {
                userListStr.append(DELIM);
            }
        }

        if (userListStr.length() > 0) {
            props.setProperty(KEY_USERS, userListStr.toString());
        }

        FileOutputStream fos = null;
        try {
            file.getParentFile().mkdirs();
            fos = new FileOutputStream(file);
            props.store(fos, null);
        } catch (IOException ex) {
            Log.warn("HyperCubeConfig: could not save hypercube config:" + ex);
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    private void setChannelListProperty(String key, List<Channel> channels) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < channels.size(); i++) {
            // DELIM_ESC;
            String name = channels.get(i).name;
            if (name != null) {
                name = name.trim();
                name = name.replaceAll(DELIM, DELIM_ESC);
                if (name.length() > 0) {
                    sb.append(name);
                }
            }
            if (i != channels.size() - 1) {
                sb.append(DELIM);
            }
        }
        props.setProperty(key, sb.toString());
    }

    private void load(File file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            props.load(fis);
        } catch (Exception e) {
            Log.error("HyperCubeConfig: Could not load config file: " + e);
            return;
        } finally {
            IOUtils.closeQuietly(fis);
        }
        publicChannels.clear();
        loadChannelList(KEY_PUBLIC_CHANNELS, null, publicChannels);

        userChannels.clear();
        List<String> users = new ArrayList<String>();
        String usersStr = props.getProperty(KEY_USERS, "");
        String[] usersSplit = usersStr.split(DELIM);
        for (String user : usersSplit) {
            if (user != null) {
                users.add(user);
            }
        }

        for (String user : users) {
            List<Channel> channels = new ArrayList<Channel>();
            UUID uuid = PlayerUtil.getPlayerUIDUnstable(user);
            loadChannelList(user + KEY_USER_CHANNEL, uuid, channels);
            if (!channels.isEmpty()) {
                userChannels.put(uuid, channels);
            }
        }
    }

    private void loadChannelList(String key, UUID user, List<Channel> channels) {
        String chans = props.getProperty(key, "");
        // chans = chans.replaceAll(DELIM_ESC, DELIM);
        String[] chanSplit = chans.split(DELIM);
        for (String chan : chanSplit) {
            if (chan != null) {
                chan = chan.trim();
                if (!chan.isEmpty()) {
                    channels.add(new Channel(chan.replaceAll(DELIM_ESC, DELIM), user));
                }
            }
        }
    }
}
