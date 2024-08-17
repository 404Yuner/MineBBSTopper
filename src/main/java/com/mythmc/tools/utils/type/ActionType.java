package com.mythmc.tools.utils.type;

public enum ActionType {
    CONSOLE("console"),
    PLAYER("player"),
    TELL("tell"),
    SOUND("sound"),
    BROADCAST("broadcast"),
    GIVE_MONEY("give-money"),
    GIVE_POINTS("give-points"),
    CLOSE("close"),
    OPEN("open"),
    TITLE("title"),
    BOSSBAR("bossbar"),
    CONNECT("connect"),
    OP("op"),
    NULL("null");

    private final String type;

    ActionType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static ActionType fromString(String type) {
        for (ActionType actionType : ActionType.values()) {
            if (actionType.getType().equals(type)) {
                return actionType;
            }
        }
        return null;
    }
}