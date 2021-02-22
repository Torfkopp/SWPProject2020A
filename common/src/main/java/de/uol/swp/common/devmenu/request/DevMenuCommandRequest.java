package de.uol.swp.common.devmenu.request;

import de.uol.swp.common.message.AbstractRequestMessage;

import java.util.List;

public class DevMenuCommandRequest extends AbstractRequestMessage {

    private final String classname;
    private final List<String> args;

    public DevMenuCommandRequest(String classname, List<String> args) {
        this.classname = classname;
        this.args = args;
    }

    public List<String> getArgs() {
        return args;
    }

    public String getClassname() {
        return classname;
    }
}
