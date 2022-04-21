package dev.thatsmybaby.shared.task;

import cn.nukkit.Server;
import cn.nukkit.scheduler.Task;

import java.util.HashMap;
import java.util.Map;

public abstract class TaskHandlerStorage {

    private final Map<String, Task> taskMap = new HashMap<>();
    private String lastTask = null;

    public void scheduleRepeating(Task task, int ticks) {
        this.taskMap.put(task.getClass().getSimpleName(), task);

        this.lastTask = task.getClass().getSimpleName();

        Server.getInstance().getScheduler().scheduleRepeatingTask(task, ticks);
    }

    public <T extends Task> T forceGetScheduler(Class<T> taskClass) {
        return (T) this.taskMap.get(taskClass.getSimpleName());
    }

    public Task getLastScheduler() {
        return this.taskMap.get(this.lastTask);
    }
}