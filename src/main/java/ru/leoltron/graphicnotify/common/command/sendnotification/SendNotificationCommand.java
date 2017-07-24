package ru.leoltron.graphicnotify.common.command.sendnotification;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class SendNotificationCommand implements ICommand {

    private static final String COMMAND_NAME = "sendNotification";

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender) {
        return '/' + COMMAND_NAME + " [private <nickname>] [link <url>] [itemstack <(item/block)_id> <damage>] [time <time(sec)>] <message>";
    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList(COMMAND_NAME, "sn");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {


        try {
            SNArgs commandArgs = parseArgs(sender, args);
            commandArgs.formAndSendMessage();
        } catch (SNException e) {
            sendErrorMessage(sender, e.getMessage());
        }

    }

    private SNArgs parseArgs(ICommandSender sender, String[] args) throws SNException {
        SNArgs commandArgs = new SNArgs();

        if (args.length < 1) {
            throw new SNException(getCommandUsage(sender));
        }

        int pointer = 0;
        boolean isPublic;
        if (args[pointer].equalsIgnoreCase("private"))
            isPublic = false;
        else {
            isPublic = true;
            pointer--;
        }

        if (!isPublic)
            commandArgs.setTargetPlayer(args[pointer + 1]);

        pointer += isPublic ? 1 : 2;

        for (; ; pointer++) {
            if (pointer >= args.length) {
                throw new SNException(getCommandUsage(sender));
            }

            if (args[pointer].equalsIgnoreCase("link")) {
                pointer++;
                if (pointer >= args.length) {
                    throw new SNException("There're must be a link after \"" + args[pointer - 1] + "\"!");
                }
                try {
                    new URI(args[pointer]);
                } catch (URISyntaxException e) {
                    throw new SNException("Can't resolve link " + args[pointer], e);
                }
                commandArgs.url = args[pointer];
            } else if (args[pointer].equalsIgnoreCase("itemstack")) {
                String errorMessage = "Describe item stack through \"" + args[pointer] + " <item_id> <stack_damage>\"";

                if (pointer + 3 >= args.length) {
                    throw new SNException(errorMessage);
                }

                pointer++;
                int id;
                try {
                    id = Integer.valueOf(args[pointer]);
                } catch (NumberFormatException e) {
                    throw new SNException("Cannot resolve item id " + args[pointer], e);
                }
                if (id < 0) {
                    throw new SNException("Item id must be positive!");
                }

                pointer++;
                int stackDamage;
                try {
                    stackDamage = Integer.valueOf(args[pointer]);
                } catch (NumberFormatException e) {
                    throw new SNException("Cannot resolve stack damage " + args[pointer], e);
                }
                if (stackDamage < 0) {
                    throw new SNException("Stack damage must be positive!");
                }
                commandArgs.itemStack = new ItemStack(Item.getItemById(id), 1, stackDamage);

            } else if (args[pointer].equalsIgnoreCase("time")) {
                pointer++;
                if (pointer >= args.length) {
                    throw new SNException("There're must be a time value after \"" + args[pointer - 1] + "\"!");
                }
                try {
                    commandArgs.displayTime = Integer.valueOf(args[pointer]);
                } catch (NumberFormatException e) {
                    throw new SNException("Cannot resolve display time " + args[pointer], e);
                }
                if (commandArgs.displayTime <= 0) {
                    throw new SNException("Time must not be negative!");
                }
            } else break;
        }

        StringBuilder messageBuilder = new StringBuilder(args[pointer]);
        for (pointer++; pointer < args.length; pointer++)
            messageBuilder.append(' ').append(args[pointer]);
        commandArgs.message = messageBuilder.toString();

        if (commandArgs.itemStack == null && sender instanceof EntityPlayer) {
            IChatComponent noISFoundMessage = new ChatComponentText(
                    "Have not found item description in command arguments, using itemstack from your hand.");
            noISFoundMessage.getChatStyle().setColor(EnumChatFormatting.YELLOW);
            sender.addChatMessage(noISFoundMessage);

            commandArgs.itemStack = ((EntityPlayer) sender).getCurrentEquippedItem();
            if (commandArgs.itemStack != null)
                commandArgs.itemStack = commandArgs.itemStack.copy();
        }

        return commandArgs;
    }

    private void sendErrorMessage(ICommandSender sender, String message) {
        IChatComponent chatComponent = new ChatComponentText(message);
        chatComponent.getChatStyle().setColor(EnumChatFormatting.GOLD);
        sender.addChatMessage(chatComponent);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length > 1 && args[args.length - 2].equalsIgnoreCase("private") ?
                CommandBase.getListOfStringsMatchingLastWord(args, getPlayers()) : null;
    }

    private String[] getPlayers() {
        return MinecraftServer.getServer().getAllUsernames();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index > 0 && args[index - 1].equalsIgnoreCase("private");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(Object o) {
        return -1;
    }
}




