package snowsan0113.build_battle.manager;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RuleManager {

    private static final RuleManager instance = new RuleManager();
    private Map<GameRule, Object> has_rule_map = new HashMap<>();

    private RuleManager() {}

    public static RuleManager getInstance() {
        return instance;
    }

    public void setRule(GameRule rule, Object object) {
        has_rule_map.put(rule, object);
    }

    public Object getRule(GameRule rule) {
        return has_rule_map.get(rule);
    }

    public enum GameRule {
        CAN_PLACE_BLOCK_SIZE(Integer.class),
        CAN_NOT_PLACE_BLOCK_TYPE(BlockData.class);

        private final Class<?> value_class;

        GameRule(Class<?> value_class) {
            this.value_class = value_class;
        }

        public Class<?> getValueClass() {
            return value_class;
        }
    }
}
