package commands;

import gui.GuiChatWindow;
import core.Buddy;

/**
 * Run commands specified
 * @author tbenjis
 *
 */
public class list_of_commands {

	public static boolean in_command(Buddy buddy, String s, GuiChatWindow w) {
		String command = s.split(" ")[0];
		if (command.equals("/log")) {
			in_log.command(buddy, s, w);
			return false;
		} else if (command.equals("/help")) {
			in_help.command(buddy, s, w);
			return false;
		} else if (command.equals("/me")) {
			in_me.command(buddy, s, w);
			return false;
		} else if (command.equals("/otr") || s.startsWith("?OTR")) {
			in_otr.command(buddy, s, w, w.getUs(), w.getOTRCall(), w.getOTRContext());
			return false;
		}
		
		return true;
	}

	public static boolean out_command(Buddy buddy, String s, GuiChatWindow w,
			boolean with_delay) {
		String command = s.split(" ")[0];
		if (command.equals("/me")) {
			out_me.command(buddy, s, w, with_delay);
			return false;
		}
		/** Use this to detect an OTR message coming in **/
		if (command.equals("/otr") || s.startsWith("?OTR")) {
			out_otr.command(buddy, s, w, with_delay, w.getUs(), w.getOTRCall());
			return false;
		}
		
		// When the command is not exist then it cannot be Anonchat because you
		// can not send commands yourself
		return true;
	}
	

}
