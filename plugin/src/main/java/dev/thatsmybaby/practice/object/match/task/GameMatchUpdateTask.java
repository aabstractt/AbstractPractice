package dev.thatsmybaby.practice.object.match.task;

import cn.nukkit.scheduler.Task;
import dev.thatsmybaby.practice.object.GameMatch;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class GameMatchUpdateTask extends Task {

    private final GameMatch match;

    @Override
    public void onRun(int i) {

    }
}