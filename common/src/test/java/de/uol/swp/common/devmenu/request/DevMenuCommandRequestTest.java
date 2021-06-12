package de.uol.swp.common.devmenu.request;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DevMenuCommandRequestTest {

    @Test
    void testDevMenuCommandRequest() {
        String classname = "some.class.name";
        List<String> argList = new ArrayList<>();
        argList.add("arg0");
        argList.add("arg1");
        DevMenuCommandRequest request = new DevMenuCommandRequest(classname, argList);

        assertEquals(classname, request.getClassname());
        assertEquals(argList, request.getArgs());
    }
}