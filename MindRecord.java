import com.sun.jna.ptr.IntByReference;
import java.util.list;
import java.util.Hashtable;

public class MindRecord {

	private Hashtable<String, List> allRecords;

	public MindRecord() {
		allRecords = new HashTable<String, List>();
	}

	//
	public boolean controlPanelConnect(){

	}

	//
	public void recordStart() {
		Pointer eEvent			= Edk.INSTANCE.EE_EmoEngineEventCreate();
    	Pointer eState			= Edk.INSTANCE.EE_EmoStateCreate();
    	IntByReference userID 	= null;
    	//control panel by default, we wont connect directly
    	short composerPort		= 3008;
    	int state  				= 0;

    	if (Edk.INSTANCE.EE_EngineRemoteConnect("127.0.0.1", composerPort, "Emotiv Systems-5") != EdkErrorCode.EDK_OK.ToInt()) {
				System.out.println("Cannot connect to EmoComposer on [127.0.0.1]");
				return;
			}
		System.out.println("Connected to ControlPanel on [127.0.0.1]");

				while (true) 
		{
			state = Edk.INSTANCE.EE_EngineGetNextEvent(eEvent);

			// New event needs to be handled
			if (state == EdkErrorCode.EDK_OK.ToInt()) {

				int eventType = Edk.INSTANCE.EE_EmoEngineEventGetType(eEvent);
				Edk.INSTANCE.EE_EmoEngineEventGetUserId(eEvent, userID);

				// Log the EmoState if it has been updated
				if (eventType == Edk.EE_Event_t.EE_EmoStateUpdated.ToInt()) {

					Edk.INSTANCE.EE_EmoEngineEventGetEmoState(eEvent, eState);
					float timestamp = EmoState.INSTANCE.ES_GetTimeFromStart(eState);
					System.out.println(timestamp + " : New EmoState from user " + userID.getValue());
					
					System.out.print("WirelessSignalStatus: ");
					System.out.println(EmoState.INSTANCE.ES_GetWirelessSignalStatus(eState));
					
					if (EmoState.INSTANCE.ES_ExpressivIsBlink(eState) == 1)
						System.out.println("Blink");
					if (EmoState.INSTANCE.ES_ExpressivIsLeftWink(eState) == 1)
						System.out.println("LeftWink");
					if (EmoState.INSTANCE.ES_ExpressivIsRightWink(eState) == 1)
						System.out.println("RightWink");
					if (EmoState.INSTANCE.ES_ExpressivIsLookingLeft(eState) == 1)
						System.out.println("LookingLeft");
					if (EmoState.INSTANCE.ES_ExpressivIsLookingRight(eState) == 1)
						System.out.println("LookingRight");
					
					System.out.print("ExcitementShortTerm: ");
					System.out.println(EmoState.INSTANCE.ES_AffectivGetExcitementShortTermScore(eState));
					System.out.print("ExcitementLongTerm: ");
					System.out.println(EmoState.INSTANCE.ES_AffectivGetExcitementLongTermScore(eState));
					System.out.print("EngagementBoredom: ");
					System.out.println(EmoState.INSTANCE.ES_AffectivGetEngagementBoredomScore(eState));
					
					System.out.print("CognitivGetCurrentAction: ");
					System.out.println(EmoState.INSTANCE.ES_CognitivGetCurrentAction(eState));
					System.out.print("CurrentActionPower: ");
					System.out.println(EmoState.INSTANCE.ES_CognitivGetCurrentActionPower(eState));
				}
			}
			else if (state != EdkErrorCode.EDK_NO_EVENT.ToInt()) {
				System.out.println("Internal error in Emotiv Engine!");
				break;
			}
		}
    	
    	Edk.INSTANCE.EE_EngineDisconnect();
    	System.out.println("Disconnected!");
    }
	}

	//
	public void recordStop() {

	}

	
}