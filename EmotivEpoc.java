import com.sun.jna.ptr.IntByReference;
import java.util.Hashtable;
import java.lang.StringBuilder;
import java.util.Date;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class EmotivEpoc {

	private Pointer eEvent;
	private Pointer eState;
	//3008 = control panel connect, connecting directly to device seems to mean you have to do profile management manaully. screw that.
	private short composerPort		= 3008;
	private int state  				= 0; 
	public boolean connected;

	public EmotivEpoc() {
		eEvent = Edk.INSTANCE.EE_EmoEngineEventCreate();
		eState = Edk.INSTANCE.EE_EmoStateCreate();
		connected = false;
	}

	public boolean connect() {
		if (connected) return true;
		if (Edk.INSTANCE.EE_EngineRemoteConnect("127.0.0.1", composerPort, "Emotiv Systems-5") != EdkErrorCode.EDK_OK.ToInt()) {
				System.out.println("Cannot connect to EmoComposer on [127.0.0.1]");
				return false;
		} connected = true;
		return true;
	}

	private int[] getActionAndPower() {
		state = Edk.INSTANCE.EE_EngineGetNextEvent(eEvent);
		int eventType = Edk.INSTANCE.EE_EmoEngineEventGetType(eEvent);
		int cogType = Edk.INSTANCE.EE_CognitivEventGetType(eEvent);
		Edk.INSTANCE.EE_EmoEngineEventGetEmoState(eEvent, eState);	
		int action = EmoState.INSTANCE.ES_CognitivGetCurrentAction(eState);
		double power = EmoState.INSTANCE.ES_CognitivGetCurrentActionPower(eState);
		return new int[] {action, (int)(power * 100)};
	}

	public int getAction() {
		int[] out = getActionAndPower();
		return out[0];
	}

	public float getPower() {
		int[] out = getActionAndPower();
		return (float) out[1] / 100;
	}

	public float[] getEmoStates() {
		state = Edk.INSTANCE.EE_EngineGetNextEvent(eEvent);
		int eventType = Edk.INSTANCE.EE_EmoEngineEventGetType(eEvent);
		int cogType = Edk.INSTANCE.EE_CognitivEventGetType(eEvent);
		System.out.println(cogType);
		Edk.INSTANCE.EE_EmoEngineEventGetEmoState(eEvent, eState);	
		float shortTerm = EmoState.INSTANCE.ES_AffectivGetExcitementShortTermScore(eState);
		float longTerm = EmoState.INSTANCE.ES_AffectivGetExcitementLongTermScore(eState);
		float engagement = EmoState.INSTANCE.ES_AffectivGetEngagementBoredomScore(eState);			
		float meditation = EmoState.INSTANCE.ES_AffectivGetMeditationScore(eState);
		float frustration = EmoState.INSTANCE.ES_AffectivGetFrustrationScore(eState);
		return new float[] {shortTerm, longTerm, engagement, meditation, frustration};
	}

	public float getShortTermEx() {
		float[] out = getEmoStates();
		return out[0];
	}

	public float getLongTermEx() {
		float[] out = getEmoStates();
		return out[1];
	}

	public float getEngagement() {
		float[] out = getEmoStates();
		return out[2];
	}

	public float getMeditation() {
		float[] out = getEmoStates();
		return out[3];
	}

	public float getFrustration() {
		float[] out = getEmoStates();
		return out[4];
	}

	public void printAll(int minutes) {
		Date start = new Date();
		Date now = new Date();
		int[] cognitiv;
		float[] affectiv;
		StringBuilder line = new StringBuilder();
		while (now.getMinutes() - start.getMinutes() < minutes) {
			cognitiv = getActionAndPower();
			line.append(cognitiv[0]);
			line.append(" ");
			line.append(cognitiv[1] / 100.0);
			line.append(" ");
			affectiv = getEmoStates();
			for (int i = 0; i < 5; i++) {
				line.append(affectiv[i]);
				line.append(" ");
			} line.append(now);
			System.out.println(line);
			now = new Date();
			line = new StringBuilder();
		}
	}

	public static void main(String[] args) {
		EmotivEpoc eeg = new EmotivEpoc();
		if (eeg.connect()) {
			System.out.println("here");
			eeg.printAll(1);
		}
	}
}