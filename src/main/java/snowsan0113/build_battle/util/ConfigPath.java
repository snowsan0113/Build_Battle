package snowsan0113.build_battle.util;

import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.NotNull;

public enum ConfigPath {

    BUILD_TIME("game.build_time"),
    COUNT_TIME("game.count_time"),
    WORLD_NAME("location.world_name"),
    LOBBY("location.lobby"),
    LOBBY_X("location.lobby.x"),
    LOBBY_Y("location.lobby.y"),
    LOBBY_Z("location.lobby.z"),
    BUILD_SPAWN("location.build_spawn"),
    BUILD_SPAWN_X("location.build_spawn.x"),
    BUILD_SPAWN_Y("location.build_spawn.y"),
    BUILD_SPAWN_Z("location.build_spawn.z"),
    ARENA_START("location.arena_start"),
    ARENA_START_X("location.arena_start.x"),
    ARENA_START_Y("location.arena_start.y"),
    ARENA_START_Z("location.arena_start.z"),
    ARENA_END("location.arena_end"),
    ARENA_END_X("location.arena_end.x"),
    ARENA_END_Y("location.arena_end.y"),
    ARENA_END_Z("location.arena_end.z");

    private final String path;

    ConfigPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public boolean isLocationPath() {
        return path.contains(".x") || path.contains(".y") || path.contains(".z");
    }

    public static String getBackPath(String path) {
        String[] paths = path.split("\\.");
        if (paths.length >= 2) {
            StringBuilder builder = new StringBuilder();
            for (int n = 0; n < paths.length - 1; n++) {
                builder.append(paths[n]);
                if (n < paths.length - 2) {
                    builder.append(".");
                }
            }
            return builder.toString();
        }
        else {
            throw new IllegalStateException("これ以上前のパスに戻ることはできません。");
        }
    }
}
