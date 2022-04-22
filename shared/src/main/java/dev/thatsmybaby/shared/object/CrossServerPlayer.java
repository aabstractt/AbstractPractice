package dev.thatsmybaby.shared.object;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor @Getter
public final class CrossServerPlayer {

    private final String kitName;
    private final String opponent;
    private final String lastServer;
    private final boolean ranked;
}