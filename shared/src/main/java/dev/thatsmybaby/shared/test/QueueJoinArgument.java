package dev.thatsmybaby.shared.test;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.shared.factory.QueueFactory;
import dev.thatsmybaby.shared.object.GameQueue;
import dev.thatsmybaby.shared.Placeholders;
import dev.thatsmybaby.shared.command.PlayerArgument;

public final class QueueJoinArgument extends PlayerArgument {

    public QueueJoinArgument(String name, String description, String permission) {
        super(name, description, permission);
    }

    @Override
    public void execute(Player sender, String commandLabel, String argumentLabel, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(TextFormat.RED + "Usage: /" + commandLabel + " queuejoin <kit> <?map>");

            return;
        }

        if (QueueFactory.getInstance().getPlayerQueue(sender) != null) {
            sender.sendMessage(Placeholders.replacePlaceholders("YOU_ALREADY_IN_QUEUE"));

            return;
        }

        GameQueue queue = QueueFactory.getInstance().getQueueType(args[0], false);

        if (queue == null) {
            sender.sendMessage(TextFormat.RED + "Queue for kit " + args[0] + " not found");

            return;
        }

        queue.joinAsPlayer(sender);
    }
}