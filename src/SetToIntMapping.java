import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class SetToIntMapping {
    private Map<TreeSet<Integer>, Integer> setMapping = new HashMap<>();

    public Integer getSetMapping(TreeSet<Integer> set) {
        return setMapping.get(set);
    }

    public void setSetMapping(TreeSet<Integer> set) {
        if (!setMapping.containsKey(set)) {
            setMapping.put(set, setMapping.size());
        }
    }
}
