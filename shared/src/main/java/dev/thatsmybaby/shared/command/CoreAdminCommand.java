package dev.thatsmybaby.shared.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class CoreAdminCommand extends Command {

    private final Map<String, Argument> arguments = new HashMap<>();

    public CoreAdminCommand(String name, String description, String usageMessage, String[] aliases) {
        super(name, description, usageMessage, aliases);
    }

    public void registerArgument(Argument... arguments) {
        for (Argument argument : arguments) {
            this.arguments.put(argument.getName(), argument);
        }
    }

    private Argument getArgument(String argumentName) {
        return this.arguments.values().stream().filter(argument -> argument.equals(argumentName)).findAny().orElse(null);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage(TextFormat.RED + "Usage: /" + s + " help");

            return false;
        }

        Argument argument = this.getArgument(args[0]);

        if (argument == null) {
            commandSender.sendMessage(TextFormat.RED + "Usage: /" + s + " help");

            return false;
        }

        if (argument.getPermission() != null && !commandSender.hasPermission(argument.getPermission())) {
            commandSender.sendMessage(TextFormat.RED + "You don't have permissions to use this command.");

            return false;
        }

        argument.execute(commandSender, s, args[0], Arrays.copyOfRange(args, 1, args.length));

        return false;
    }
}