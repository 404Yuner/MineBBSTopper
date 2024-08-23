package com.mythmc.api.event;

import com.mythmc.impl.cache.target.PlayerInfo;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class PlayerAccumulatedRewardClaimEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final PlayerInfo playerInfo;
    private final List<String> rewardCommand;
    private final Integer rewardIndex;
    private boolean isCancelled;


    public PlayerAccumulatedRewardClaimEvent(PlayerInfo playerInfo, List<String> rewardCommand, int rewardIndex) {
        this.playerInfo = playerInfo;
        this.rewardCommand = rewardCommand;
        this.rewardIndex = rewardIndex;
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public List<String> getRewardCommand() {
        return rewardCommand;
    }

    public long getRewardIndex() {
        return rewardIndex;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }
}
