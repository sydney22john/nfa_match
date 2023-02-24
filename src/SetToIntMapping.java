import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SetToIntMapping {
    private Map<Set<Integer>, Integer> setMapping = new HashMap<>();

    public Integer getSetMapping(Set<Integer> set) {
        if (set.isEmpty()) return -1;
        setSetMapping(set);
        return setMapping.get(set);
    }

    public void setSetMapping(Set<Integer> set) {
        if (!setMapping.containsKey(set)) {
            setMapping.put(set, setMapping.size());
        }
    }
}
