package de.uol.swp.common.devmenu.request;

import de.uol.swp.common.devmenu.CommandParser;
import de.uol.swp.common.message.AbstractRequestMessage;

import java.util.List;

public class DevMenuCommandRequest extends AbstractRequestMessage {

    private final String classname;
    private final List<CommandParser.ASTToken> args;

    public DevMenuCommandRequest(String classname, List<CommandParser.ASTToken> args) {
        this.classname = classname;
        this.args = args;
    }

    public List<CommandParser.ASTToken> getArgs() {
        return args;
    }

    public String getClassname() {
        return classname;
    }
}
