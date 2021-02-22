package de.uol.swp.common.devmenu.response;

import de.uol.swp.common.message.AbstractResponseMessage;

import java.util.List;
import java.util.Map;

public class DevMenuClassesResponse extends AbstractResponseMessage {

    private final Map<String, List<Map<String, Class<?>>>> classesMap;

    public DevMenuClassesResponse(Map<String, List<Map<String, Class<?>>>> classesMap) {
        this.classesMap = classesMap;
    }

    public Map<String, List<Map<String, Class<?>>>> getClassesMap() {
        return classesMap;
    }
}
