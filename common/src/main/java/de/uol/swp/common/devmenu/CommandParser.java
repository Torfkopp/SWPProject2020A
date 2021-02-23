package de.uol.swp.common.devmenu;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class CommandParser implements Serializable {

    public static List<Token> lex(String commandString) {
        List<Token> tokens = new LinkedList<>();
        int start = 0;
        String subString;
        boolean inQuotes = false;
        boolean inList = false;
        for (int i = 0; i <= commandString.length(); i++) {
            subString = commandString.substring(start, i);
            if ((subString.endsWith(" ") || i == commandString.length()) && !inQuotes) {
                if (subString.strip().length() == 0) continue;
                tokens.add(new Token(Token.Type.ANY, subString.strip()));
                start = i;
            } else if (subString.endsWith("\"") && !subString.endsWith("\\\"")) {
                if (inQuotes) {
                    tokens.add(new Token(Token.Type.ANY, subString.replace("^\"", "").replace("\"$", "").strip()));
                    start = i;
                }
                inQuotes = !inQuotes;
            } else if (subString.endsWith("[") && !inQuotes) {
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

    public static class ASTToken implements Serializable {

        private final Type type;
        private final Union content;

        public enum Type implements Serializable {
            LIST,
            MAP,
            UNTYPED
        }

        public ASTToken(Type type, List<ASTToken> content) {
            this.type = type;
            this.content = new Union(content);
        }

        public ASTToken(Type type, String content) {
            this.type = type;
            this.content = new Union(content);
        }

        public ASTToken(Type type, Union content) {
            this.type = type;
            this.content = content;
        }

        public List<ASTToken> getAstTokens() {
            if (hasCollection()) return content.getAstTokens();
            else return null;
        }

        public String getString() {
            if (!hasCollection()) return content.getString();
            else return null;
        }

        public boolean hasCollection() {
            return content.isAstTokensList();
        }

        public static class Union implements Serializable {

            private final boolean isAstTokensList;
            private final String string;
            private final List<ASTToken> astTokens;

            public Union(String string) {
                this.isAstTokensList = false;
                this.astTokens = null;
                this.string = string;
            }

            public Union(List<ASTToken> astTokens) {
                this.isAstTokensList = true;
                this.astTokens = astTokens;
                this.string = null;
            }

            public List<ASTToken> getAstTokens() {
                if (isAstTokensList) return astTokens;
                else return null;
            }

            public String getString() {
                if (isAstTokensList) return null;
                else return string;
            }

            public boolean isAstTokensList() {
                return isAstTokensList;
            }
        }
    }

    public static class Token implements Serializable {

        private final Type type;
        private final String content;

        public enum Type implements Serializable {
            LIST_START,
            LIST_END,
            MAP_START,
            MAP_END,
            COMMA,
            ANY
        }

        public Token(Type type, String content) {
            this.type = type;
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public Type getType() {
            return type;
        }
    }
}
