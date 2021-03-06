package core.otr;

import java.io.IOException;

import javax.swing.JOptionPane;
import commands.list_of_commands;
import util.ChatWindow;
import util.Util;
import gui.Gui;
import gui.GuiChatWindow;
import core.Buddy;
import core.Logger;
import ca.uwaterloo.crysp.otr.iface.OTRCallbacks;
import ca.uwaterloo.crysp.otr.iface.OTRContext;
import ca.uwaterloo.crysp.otr.iface.OTRInterface;
import ca.uwaterloo.crysp.otr.iface.Policy;

public class LocalCallback implements OTRCallbacks{
	
	Buddy bud;
	GuiChatWindow w;
	
	public LocalCallback(Buddy b) {
		bud=b;
		//get chat window
		w = Gui.getChatWindow(bud, true, true);	
	}

	public void injectMessage(String accName, String prot, String rec, String msg){
		if(msg==null)return;
		//return a log of injected message
		Logger.log(Logger.INFO, this.getClass().getName(), "Injecting message to the recipient:"
				+msg.length()+": "+msg.toString());
		
		
		//add otr to the message
		try {
			bud.sendRaw("message "+msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getOtrPolicy(OTRContext conn) {
		return Policy.DEFAULT;
	}

	public int isLoggedIn(String accountname, String protocol,
			String recipient) {
		if(bud.isFullyConnected())
		{
			return 1;
		}else{
			return 0;
		}
	}

	public int maxMessageSize(OTRContext context) {
		return 3000;
	}

	public void newFingerprint(OTRInterface us,
			String accountname, String protocol, String username,
			byte[] fingerprint) {
		
		Logger.log(Logger.INFO, this.getClass().getName(),"New fingerprint is created."+Util.bytesToHex(fingerprint));
		
		w.setStatusText("New fingerprint is created: ",1);
		// show encrypted and set the buddys fingerprint
		w.setBuddyFingerprint(Util.fineFingerprint(Util.bytesToHex(fingerprint)));
		w.setFullEncryption();
	}

	public void stillSecure(OTRContext context, int is_reply) {
		Logger.log(Logger.INFO, this.getClass().getName(),"Still secure.");
		w.setStatusText("Still secure",1);
	}

	public void updateContextList() {
		Logger.log(Logger.INFO, this.getClass().getName(),"Updating context list.");
		w.setStatusText("Updating context list.",1);
	}

	public void writeFingerprints() {
		Logger.log(Logger.INFO, this.getClass().getName(),"Writing fingerprints.");
		w.setStatusText("Writing fingerprints.",1);
	}

	public String errorMessage(OTRContext context, int err_code) {
		if(err_code==OTRCallbacks.OTRL_ERRCODE_MSG_NOT_IN_PRIVATE){
			String str= "You sent an encrypted message, but we finished " +
					"the private conversation.";		
			return str;
		}
		w.setOTRoff();
		return null;
	}

	public void handleMsgEvent(int msg_event,
			OTRContext context, String message) {
		String str = "The private connection has already ended.";
		if(msg_event==OTRCallbacks.OTRL_MSGEVENT_CONNECTION_ENDED){
			Logger.log(Logger.INFO, this.getClass().getName(),str);
			w.setOTRoff();
			ChatWindow.update_window(0, w, str, "", "", false);
		}else if(msg_event==OTRCallbacks.OTRL_MSGEVENT_RCVDMSG_NOT_IN_PRIVATE){
			str = "We received an encrypted message, but we are not in " +
					"encryption state.";
			Logger.log(Logger.INFO, this.getClass().getName(),str);
			w.setOTRoff();
			ChatWindow.update_window(0, w, str, "", "", false);
		}
	}

	public void handleSmpEvent(int smpEvent,
			OTRContext context, int progress_percent, String question) {
		String str;
		if(smpEvent == OTRCallbacks.OTRL_SMPEVENT_ASK_FOR_SECRET){
			Logger.log(Logger.INFO, this.getClass().getName(),"The other side has initialized SMP." +
					" Please respond with /rs.");
			
			str = JOptionPane.showInputDialog("The other side has initialized SMP. Enter the secret");
		
				list_of_commands.in_command(bud, "/otr /rs"+str, w);	
			
		}else if(smpEvent == OTRCallbacks.OTRL_SMPEVENT_ASK_FOR_ANSWER){
			Logger.log(Logger.INFO, this.getClass().getName(),"The other side has initialized SMP, with question:" +
					question + ", "+
			" Please respond with /rs.");
			str = JOptionPane.showInputDialog("The other side has initialized SMP: "+question+"? Enter the secret");
			
				list_of_commands.in_command(bud, "/otr /rs"+str, w);
			
		}else if(smpEvent == OTRCallbacks.OTRL_SMPEVENT_SUCCESS){
			Logger.log(Logger.INFO, this.getClass().getName(),"SMP succeeded (User trusted).");
			w.setStatusText("Encrypted (SMP succeeded)",3);
			w.setMITMoff();
		}else if(smpEvent == OTRCallbacks.OTRL_SMPEVENT_FAILURE){
			Logger.log(Logger.INFO, this.getClass().getName(),"SMP failed (User not trusted).");
			w.setStatusText("SMP failed.",2);
		}
				
	}

	@Override
	public void goneSecure(OTRContext context, byte[] fingerPrint) {
		Logger.log(Logger.INFO, this.getClass().getName(),"AKE succeeded");
		w.setStatusText("AKE succeeded",1);
		//get fingerprint and set it
		w.setFingerprint(Util.fineFingerprint(Util.bytesToHex(fingerPrint)));
		//list_of_commands.in_command(bud, "/otr Receiving fingerprint", w);
		
	}
	
}