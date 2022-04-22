package dev.thatsmybaby.shared.object;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor @Getter
public final class CrossServerGameMap {

    private final String mapName;
    private final List<String> kits;
    private final boolean partyAllowed;
}