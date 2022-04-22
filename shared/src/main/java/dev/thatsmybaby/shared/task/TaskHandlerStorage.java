package dev.thatsmybaby.shared.task;

import cn.nukkit.Server;
import cn.nukkit.scheduler.Task;
import cn.nukkit.scheduler.TaskHandler;

import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("unchecked")
public abstract class TaskHandlerStorage {

    private final Map<String, Task> taskMap = new HashMap<>();
    private final Map<String, Integer> taskIds = new HashMap<>();

    private String lastTask = null;

    public void scheduleRepeating(Task task, int ticks) {
        this.lastTask = task.getClass().getSimpleName();

        TaskHandler taskHandler = Server.getInstance().getScheduler().scheduleRepeatingTask(task, ticks);

        this.taskMap.put(task.getClass().getSimpleName(), task);
        this.taskIds.put(task.getClass().getSimpleName(), taskHandler.getTaskId());
    }

    public <T extends Task> T forceGetScheduler(Class<T> taskClass) {
        return (T) this.taskMap.get(taskClass.getSimpleName());
    }

    public Task getLastScheduler() {
        return this.taskMap.get(this.lastTask);
    }

    final public void cancelTask(String taskName, boolean cancel) {
        int taskId = this.taskIds.getOrDefault(taskName, -2);

        if (taskId == -2) {
            return;
        }

        this.taskMap.remove(taskName);
        this.taskIds.remove(taskName);

        if (cancel) {
            Server.getInstance().getScheduler().cancelTask(taskId);
        }
    }

    final public void cancelTasks() {
        for (Task task : this.taskMap.values()) {
            this.cancelTask(task.getClass().getSimpleName(), true);
        }
    }
}