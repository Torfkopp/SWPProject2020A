package de.uol.swp.common.devmenu.response;

import de.uol.swp.common.message.AbstractResponseMessage;

import java.util.List;
import java.util.Map;

/**
 * Response used by the CommandService to communicate the list of classes and
 * their respective constructors the client is allowed to use in the
 * Developer Menu
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @since 2021-02-22
 */
public class DevMenuClassesResponse extends AbstractResponseMessage {

    /**
     * Map of class names to a List of their constructors, each represented
     * by a Map of the parameter name to its type
     * <p>
     * In short:
     * {@code <classname, constructor<<argumentname, argumenttype>>>}
     */
    private final Map<String, List<Map<String, Class<?>>>> classesMap;

    /**
     * Constructor
     *
     * @param classesMap The Map of classes the client is allowed to use
     */
    public DevMenuClassesResponse(Map<String, List<Map<String, Class<?>>>> classesMap) {
        this.classesMap = classesMap;
    }

    /**
     * Gets the classes Map
     *
     * @return Map of class names to List of constructors, each as a Map of
     * parameter name to parameter type
     */
    public Map<String, List<Map<String, Class<?>>>> getClassesMap() {
        return classesMap;
    }
}
