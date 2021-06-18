package de.uol.swp.common.devmenu.response;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DevMenuClassesResponseTest {

    @Test
    void getClassesMap() {
        Map<String, Class<?>> constructorArgMap = new HashMap<>();
        List<Map<String, Class<?>>> constructorList = new ArrayList<>();
        Map<String, List<Map<String, Class<?>>>> classesMap = new HashMap<>();

        constructorList.add(constructorArgMap);
        classesMap.put("first", constructorList);
        DevMenuClassesResponse response = new DevMenuClassesResponse(classesMap);

        assertEquals(classesMap, response.getClassesMap());
    }
}