package de.uol.swp.common.devmenu;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * The CommandParser used by the CommandService on the server
 * <p>
 * The CommandParser is tasked with parsing Strings in order to determine if
 * the String contains something like a List, Map, or otherwise special
 * data structure that needs to be handled differently from just calling a
 * Java class's Wrapper.parse(String) methods.
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @since 2021-02-23
 */
public class CommandParser implements Serializable {

    //TODO: check for correct descriptions
    //TODO: check for correct descriptions
    //TODO: check for correct descriptions

    /**
     * Helper method to check if a String ends in one of the Strings in
     * an array
     *
     * @param string     The String whose ending to check
     * @param endStrings The Array of Strings to check for being the ending
     *
     * @return true if string ends with one of the Strings in the array, false otherwise
     */
    private static boolean endIn(String string, String[] endStrings) {
        for (String endString : endStrings) {
            if (string.endsWith(endString)) return true;
        }
        return false;
    }

    /**
     * Lexes the provided command String to determine Lists, Maps, etc. //TODO: is it called lexing?
     *
     * @param commandString The String to lex
     *
     * @return A List containing Token types to indicate e.g. list starts
     */
    public static List<Token> lex(String commandString) {
        List<Token> tokens = new LinkedList<>();
        int start = 0;
        String subString;
        boolean inQuotes = false;
        boolean inList = false;
        for (int i = 0; i <= commandString.length(); i++) {
            subString = commandString.substring(start, i);
            if ((endIn(subString, new String[]{"\"", "]", "}"}) || i == commandString.length()) && !inQuotes) {
                if (subString.strip().length() == 0) continue;
                tokens.add(new Token(Token.Type.ANY, subString.strip()));
                start = i;
            } else if (subString.endsWith("\"") && !subString.endsWith("\\\"")) {
                if (inQuotes) {
                    tokens.add(new Token(Token.Type.ANY, subString.replace("^\"", "").replace("\"$", "").strip()));
                    start = i;
                }
                inQuotes = !inQuotes;
            }
            if (subString.endsWith("[") && !inQuotes) {
                tokens.add(new Token(Token.Type.LIST_START, "["));
                start = i;
            } else if (subString.endsWith("]") && !inQuotes) {
                tokens.add(new Token(Token.Type.LIST_END, "]"));
                start = i;
            } else if (subString.endsWith("{") && !inQuotes) {
                tokens.add(new Token(Token.Type.MAP_START, "]"));
                start = i;
            } else if (subString.endsWith("}") && !inQuotes) {
                tokens.add(new Token(Token.Type.MAP_END, "]"));
                start = i;
            } else if (subString.endsWith(",") && !inQuotes) {
                tokens.add(new Token(Token.Type.COMMA, ","));
                start = i;
            }
        }
        return tokens;
    }

    /**
     * Parse a list of Token types to a list of ASTToken types //TODO: maybe more expressive?
     *
     * @param tokens The List of Token types to parse
     *
     * @return A List of ASTToken according to the list input
     */
    public static List<ASTToken> parse(List<Token> tokens) {
        LinkedList<ASTToken> AST = new LinkedList<>();
        boolean inList = false, inMap = false;
        int start = 0;
        for (int i = 0; i < tokens.size(); i++) {
            switch (tokens.get(i).getType()) {
                case ANY:
                    if (inList) ;
                    else if (inMap) ;
                    else AST.add(new ASTToken(ASTToken.Type.UNTYPED, tokens.get(i).getContent()));
                    break;
                case LIST_START:
                    inList = true;
                    start = i + 1;
                    break;
                case LIST_END:
                    inList = false;
                    AST.add(new ASTToken(ASTToken.Type.LIST, parse(tokens.subList(start, i))));
                    break;
            }
        }
        return AST;
    }

    /**
     * Abstract Syntax Tree Tokens used to indicated data structures found while
     * parsing, like Lists or Maps or non-special types
     */
    public static class ASTToken implements Serializable {

        private final Type type;
        private final Union content;

        /**
         * Enum of LIST, MAP, and UNTYPED, indicating the type of data
         * structure found while parsing
         */
        public enum Type implements Serializable {
            LIST,
            MAP,
            UNTYPED
        }

        /**
         * Constructor
         *
         * @param type    The ASTToken representing the data structure
         * @param content The List of ASTToken representing the data
         */
        public ASTToken(Type type, List<ASTToken> content) {
            this.type = type;
            this.content = new Union(content);
        }

        /**
         * Constructor
         *
         * @param type    The ASTToken representing the data structure
         * @param content The String representing the data
         */
        public ASTToken(Type type, String content) {
            this.type = type;
            this.content = new Union(content);
        }

        /**
         * Constructor
         *
         * @param type    The ASTToken representing the data structure
         * @param content Union of {@code String} and {@code List<ASTToken>}
         */
        public ASTToken(Type type, Union content) {
            this.type = type;
            this.content = content;
        }

        /**
         * Gets the List of ASTToken
         *
         * @return List of ASTToken
         */
        public List<ASTToken> getAstTokens() {
            if (hasCollection()) return content.getAstTokens();
            else return null;
        }

        /**
         * Gets the String representing the data
         *
         * @return String representing the data
         */
        public String getString() {
            if (!hasCollection()) return content.getString();
            else return null;
        }

        /**
         * Check if this ASTToken is a List of ASTToken
         *
         * @return true if it is a List of ASTToken, false otherwise
         */
        public boolean hasCollection() {
            return content.isAstTokensList();
        }

        /**
         * A Union type able to hold either a String or a List of ASTToken
         */
        public static class Union implements Serializable {

            private final boolean isAstTokensList;
            private final String string;
            private final List<ASTToken> astTokens;

            /**
             * Constructor
             *
             * @param string The String representing the data
             */
            public Union(String string) {
                this.isAstTokensList = false;
                this.astTokens = null;
                this.string = string;
            }

            /**
             * Constructor
             *
             * @param astTokens The List of ASTToken representing the data
             */
            public Union(List<ASTToken> astTokens) {
                this.isAstTokensList = true;
                this.astTokens = astTokens;
                this.string = null;
            }

            /**
             * Gets the List of ASTTokens
             *
             * @return List of ASTToken if this Union is an ASTToken list, null if not
             */
            public List<ASTToken> getAstTokens() {
                if (isAstTokensList) return astTokens;
                else return null;
            }

            /**
             * Gets the String contained in the Union
             *
             * @return String contained in the Union if this Union is not a
             * List of ASTToken, null if it is
             */
            public String getString() {
                if (isAstTokensList) return null;
                else return string;
            }

            /**
             * Check if this Union is a List of ASTToken
             *
             * @return true if this Union is a List of ASTToken, false if not
             */
            public boolean isAstTokensList() {
                return isAstTokensList;
            }
        }
    }

    /**
     * Token used to represent the beginning and end of special data
     * structures, as well as Commas and non-special data types
     */
    public static class Token implements Serializable {

        private final Type type;
        private final String content;

        /**
         * Enum used as the Token type indicator
         */
        public enum Type implements Serializable {
            LIST_START,
            LIST_END,
            MAP_START,
            MAP_END,
            COMMA,
            ANY
        }

        /**
         * Constructor
         *
         * @param type    The Type representing a position or type of element
         * @param content The String representing the content
         */
        public Token(Type type, String content) {
            this.type = type;
            this.content = content;
        }

        /**
         * Gets the content stored in this Token
         *
         * @return String representing the content
         */
        public String getContent() {
            return content;
        }

        /**
         * Gets the type of Token this is
         *
         * @return Token.Type representing the type
         */
        public Type getType() {
            return type;
        }
    }
}
