package snowsan0113.build_battle.manager;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import snowsan0113.build_battle.BuildBattle;
import snowsan0113.build_battle.util.ConfigPath;

public class LocationManager {

    private static final BuildBattle instance;
    private static final FileConfiguration config;

    static {
        instance = BuildBattle.getPlugin(BuildBattle.class);
        config = instance.getConfig();
    }

    public static Location getLocation(World world, ConfigPath path) {
        int x = config.getInt(path.getPath() + ".x");
        int y = config.getInt(path.getPath() + ".y");
        int z = config.getInt(path.getPath() + ".z");
        return new Location(world, x, y, z);
    }

    public static void fill(BlockData data, Location start, Location end) {
        if (!start.getWorld().equals(end.getWorld())) {
            throw new IllegalArgumentException("開始地点と終了地点のワールドは同じ必要があります。");
        }

        int min_x = Math.min(start.getBlockX(), end.getBlockX());
        int max_x = Math.max(start.getBlockX(), end.getBlockX());

        int min_y = Math.min(start.getBlockY(), end.getBlockY());
        int max_y = Math.max(start.getBlockY(), end.getBlockY());

        int min_z = Math.min(start.getBlockZ(), end.getBlockZ());
        int max_z = Math.max(start.getBlockZ(), end.getBlockZ());

        for (int x = min_x; x <= max_x; x++) {
            for (int y = min_y; y <= max_y; y++) {
                for (int z = min_z; z <= max_z; z++) {
                    Location fill_loc = new Location(start.getWorld(), x, y, z);
                    fill_loc.getBlock().setBlockData(data);
                }
            }
        }
    }

}
