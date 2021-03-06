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
		String rec = s;
		Logger.log(Logger.INFO, "OUT_OTR","From network:"+s.length()+": "+s);
		StringTLV stlv;
		//enable otr
		if(!w.isOTREnabled())
		{
			w.setOTRon();
			//set partial enabled
			w.setPartialEncryption();
			//generate keys for otr
			w.generateOTRkeys();
		}
		try {
			stlv = us.messageReceiving(Config.us, buddy.getClient(), buddy.getAddress(), rec, callback);
			if(stlv!=null){
				rec=stlv.msg;
				Logger.log(Logger.INFO, "OUT_OTR","From OTR (converted):"+rec.length()+": "+rec);
				//message is encrypted you can enable the menus
				if(!w.isOTREnabled())
				{
					w.setOTRon();
				}
			}
			//only display messages for otr
			if(rec.startsWith("/otr"))
			{
				ChatWindow.update_window(6, w, rec.substring(5), "", "", with_delay);			
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}