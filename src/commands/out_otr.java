package commands;

import ca.uwaterloo.crysp.otr.iface.OTRCallbacks;
import ca.uwaterloo.crysp.otr.iface.OTRInterface;
import ca.uwaterloo.crysp.otr.iface.StringTLV;
import gui.GuiChatWindow;
import util.ChatWindow;
import core.Buddy;
import core.Config;
import core.Logger;
/**
 * Display a messeng that was sent by a contact
 * @author tbenjis
 *
 */
public class out_otr {
	public static void command(Buddy buddy, String s, GuiChatWindow w,
			boolean with_delay, OTRInterface us, OTRCallbacks callback) {
		//remove the otr tag
		s = s.substring(5);
		Logger.log(Logger.INFO, "OUT_OTR","From network:"+s.length()+": "+s);
		StringTLV stlv;
		try {
			stlv = us.messageReceiving(Config.us, buddy.getClient(), buddy.getAddress(), s, callback);
			if(stlv!=null){
				s=stlv.msg;
				Logger.log(Logger.INFO, "OUT_OTR","From OTR:"+s.length()+": "+s);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ChatWindow.update_window(6, w, s, "", "", with_delay);
	}
}