import com.sun.jna.ptr.IntByReference;
import java.util.Hashtable;
import java.lang.StringBuilder;
import java.util.Date;
import com.sun.jna.Pointer;
import java.util.ArrayList;
import Jama.*;
import com.sun.jna.ptr.IntByReference;

public class EmotivEpoc {

	private Pointer eEvent;
	private Pointer eState;
	//3008 = control panel connect, connecting directly to device seems to mean you have to do profile management manaully. screw that.
	private short composerPort		= 3008;
	private int state  				= 0;
    public int sliceVectorLength    = 7;
	public boolean connected;
    private Hashtable<String, ArrayList> recordings;

	public EmotivEpoc() {
		eEvent = Edk.INSTANCE.EE_EmoEngineEventCreate();
		eState = Edk.INSTANCE.EE_EmoStateCreate();
        recordings = new Hashtable<String, ArrayList>();
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

	public double getPower() {
		int[] out = getActionAndPower();
		return (double) out[1] / 100;
	}

	public double[] getEmoStates() {
		state = Edk.INSTANCE.EE_EngineGetNextEvent(eEvent);
		int eventType = Edk.INSTANCE.EE_EmoEngineEventGetType(eEvent);
		int cogType = Edk.INSTANCE.EE_CognitivEventGetType(eEvent);
		System.out.println(cogType);
		Edk.INSTANCE.EE_EmoEngineEventGetEmoState(eEvent, eState);	
		double shortTerm = EmoState.INSTANCE.ES_AffectivGetExcitementShortTermScore(eState);
		double longTerm = EmoState.INSTANCE.ES_AffectivGetExcitementLongTermScore(eState);
		double engagement = EmoState.INSTANCE.ES_AffectivGetEngagementBoredomScore(eState);
		double meditation = EmoState.INSTANCE.ES_AffectivGetMeditationScore(eState);
		double frustration = EmoState.INSTANCE.ES_AffectivGetFrustrationScore(eState);
		return new double[] {shortTerm, longTerm, engagement, meditation, frustration};
	}

	public double getShortTermEx() {
		double[] out = getEmoStates();
		return out[0];
	}

	public double getLongTermEx() {
		double[] out = getEmoStates();
		return out[1];
	}

	public double getEngagement() {
		double[] out = getEmoStates();
		return out[2];
	}

	public double getMeditation() {
		double[] out = getEmoStates();
		return out[3];
	}

	public double getFrustration() {
		double[] out = getEmoStates();
		return out[4];
	}

	public void printAll(int minutes) {
		Date start = new Date();
		Date now = new Date();
		int[] cognitiv;
		double[] affectiv;
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

    public double[] getTimeSliceVector() {
        double[] affectiv = getEmoStates();
        int[] cognitiv = getActionAndPower();
        double[] output = new double[sliceVectorLength];
        for (int i = 0; i < output.length; i++) {
            if (i < affectiv.length) {
                output[i] = affectiv[i];
            } else {
                output[i] = cognitiv[i - affectiv.length];
            }
        } return output;
    }

    public Matrix recordTimeMatrix(int seconds) {
       Date start = new Date();
       ArrayList<double[]> rows = new ArrayList<double[]>();
       Matrix recording;
       Date now = new Date();
       //do the recording
       for (int t = 0; now.getTime() - start.getTime() < seconds * 1000; t++) {
           now = new Date();
           rows.add(getTimeSliceVector());
       }
       //make the 2d array
       double[] oneRow = rows.get(0);
       int size = oneRow.length;
       double[][] data = new double[size][sliceVectorLength];
       for (int i = 0; i < size; i++) {
           data[i] = rows.get(i);
       } recording = new Matrix(data);

       return recording;
    }

    public void recordThought(String label, int seconds){
        Matrix record = recordTimeMatrix(seconds);
        ArrayList<Matrix> list;
        if (recordings.containsKey(label)) {
            list = recordings.get(label);
        } else {
            list = new ArrayList<Matrix>();
        }
        list.add(record);
        recordings.put(label, list);
    }

    //take in a thought matrix and classify what thought, if any.
    public String identify(Matrix input) {
        return input.toString();
    }
//	public static void main(String[] args) {
//		EmotivEpoc eeg = new EmotivEpoc();
//		if (eeg.connect()) {
//			System.out.println("here");
//			eeg.printAll(1);
//		}
//	}
}