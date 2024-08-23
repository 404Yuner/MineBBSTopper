package com.mythmc.tools.utils.type;

import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;

public enum ActionType {
    CONSOLE("consol(e|es|e1|e1s)?\\b"),
    PLAYER("player|cmd|command(s)?"),
    TELL("tell(s)?"),
    SOUND("(play)?-?sounds?"),
    BROADCAST("broadcast|bc"),
    GIVE_MONEY("(give|add|deposit)-?(money|eco|coin)s?"),
    GIVE_POINTS("(give|add|deposit)-?points?"),
    CLOSE("close|shut"),
    OPEN("opens?|(open)?-?gui|menu"),
    TITLE("titl(e|es)?"),
    BOSSBAR("(send)?-?boss(bar)?s?"),
    CONNECT("bungee|server|connect"),
    OP("op(erator)?s?"),
    NULL("null");

    private final Pattern pattern;
    private static final Map<Pattern, ActionType> lookup = new HashMap<>();

    // 构造函数
    ActionType(String regex) {
        this.pattern = Pattern.compile("^" + regex + "$", Pattern.CASE_INSENSITIVE);
    }

    // 静态代码块初始化 lookup
    static {
        for (ActionType type : ActionType.values()) {
            lookup.put(type.pattern, type);
        }
    }

    public Pattern getPattern() {
        return pattern;
    }

    public static ActionType fromString(String input) {
        for (Map.Entry<Pattern, ActionType> entry : lookup.entrySet()) {
            if (entry.getKey().matcher(input).matches()) {
                return entry.getValue();
            }
        }
        return null;
    }
}