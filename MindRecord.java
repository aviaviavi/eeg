import com.sun.jna.ptr.IntByReference;
import java.util.Hashtable;
import java.lang.StringBuilder;
import java.util.Date;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class MindRecord {

	public static void recordStart() {
		Pointer eEvent			= Edk.INSTANCE.EE_EmoEngineEventCreate();
    	Pointer eState			= Edk.INSTANCE.EE_EmoStateCreate();
    	// IntByReference userID 	= null;
    	//3008 = control panel connect, connecting directly to device seems to mean you have to do profile management manaully. screw that.
    	short composerPort		= 3008;
    	int state  				= 0;
    	Date startTime 			= new Date();
    	Date now				= new Date();
    	String delimeter		= " ";
    	StringBuilder timeStamp;

    	if (Edk.INSTANCE.EE_EngineRemoteConnect("127.0.0.1", composerPort, "Emotiv Systems-5") != EdkErrorCode.EDK_OK.ToInt()) {
				System.out.println("Cannot connect to EmoComposer on [127.0.0.1]");
				return;
		}
		System.out.println("Connected to ControlPanel on [127.0.0.1]");

			while (now.getMinutes() - startTime.getMinutes() < 3) {
				timeStamp = new StringBuilder();
				state = Edk.INSTANCE.EE_EngineGetNextEvent(eEvent);

				// New event needs to be handled
				if (state == EdkErrorCode.EDK_OK.ToInt()) {

					int eventType = Edk.INSTANCE.EE_EmoEngineEventGetType(eEvent);
					// Edk.INSTANCE.EE_EmoEngineEventGetUserId(eEvent, userID);

					// Log the EmoState if it has been updated
					if (eventType == Edk.EE_Event_t.EE_EmoStateUpdated.ToInt()) {

						Edk.INSTANCE.EE_EmoEngineEventGetEmoState(eEvent, eState);
						
						// System.out.print("WirelessSignalStatus: ");
						// System.out.println(EmoState.INSTANCE.ES_GetWirelessSignalStatus(eState));
						
						// if (EmoState.INSTANCE.ES_ExpressivIsBlink(eState) == 1)
						// 	System.out.println("Blink");
						// if (EmoState.INSTANCE.ES_ExpressivIsLeftWink(eState) == 1)
						// 	System.out.println("LeftWink");
						// if (EmoState.INSTANCE.ES_ExpressivIsRightWink(eState) == 1)
						// 	System.out.println("RightWink");
						// if (EmoState.INSTANCE.ES_ExpressivIsLookingLeft(eState) == 1)
						// 	System.out.println("LookingLeft");
						// if (EmoState.INSTANCE.ES_ExpressivIsLookingRight(eState) == 1)
						// 	System.out.println("LookingRight");
						
						//for printing line by line, just pipe to some file	
						timeStamp.append(EmoState.INSTANCE.ES_AffectivGetExcitementShortTermScore(eState) + delimeter);
						timeStamp.append(EmoState.INSTANCE.ES_AffectivGetExcitementLongTermScore(eState) + delimeter);
						timeStamp.append(EmoState.INSTANCE.ES_AffectivGetEngagementBoredomScore(eState) + delimeter);			
						timeStamp.append(EmoState.INSTANCE.ES_AffectivGetMeditationScore(eState) + delimeter);
						timeStamp.append(EmoState.INSTANCE.ES_AffectivGetFrustrationScore(eState) + delimeter);

						now = new Date();
						timeStamp.append(now);

						System.out.println(timeStamp);

						//for exporting to json


					}
				} else if (state != EdkErrorCode.EDK_NO_EVENT.ToInt()) {
						System.out.println("Internal error in Emotiv Engine!");
						break;
				}
			}
	    	
	    	Edk.INSTANCE.EE_EngineDisconnect();
	    	System.out.println("Disconnected!");
	  
    }

	public static void main(String[] args) {
		recordStart();
	}

}