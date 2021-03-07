package de.uol.swp.common.devmenu.request;

import de.uol.swp.common.message.AbstractRequestMessage;

import java.util.List;

/**
 * Request used by the client to request instantiation of a class by the
 * CommandService.
 * <p>
 * Only posted by the Developer Menu
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @since 2021-02-22
 */
public class DevMenuCommandRequest extends AbstractRequestMessage {

    private final String classname;
    private final List<String> args;

    /**
     * Constructor
     *
     * @param classname The simple name of the class to instantiate
     * @param args      The List of arguments to pass to the constructor
     */
    public DevMenuCommandRequest(String classname, List<String> args) {
        this.classname = classname;
        this.args = args;
    }

    /**
     * Gets the list of arguments to be used when instantiating the requested
     * class
     *
     * @return List of Strings representing the arguments
     */
    public List<String> getArgs() {
        return args;
    }

    /**
     * Gets the simple name of the class to instantiate
     *
     * @return String representing the simple class name
     */
    public String getClassname() {
        return classname;
    }
}
