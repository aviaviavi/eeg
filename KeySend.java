import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.*;

public class KeySend {
	private Robot keyboard;

	public KeySend() {
		keyboard = new Robot();
	}

	/*
	given an action and a power, push appropriate key on keyboard. used for lego mindstorm car (for now)
	*/
	public void takeInput(int action, float power) {
		switch (action) {
			case (EmoState.EE_CognitivAction_t.COG_PUSH.ToInt()):
				keyboard.keyPress(KeyEvent.VK_UP);
				keyboard.delay(500);
				keyboard.keyRelease(KeyEvent.VK_UP);
				break;
			case (EmoState.EE_CognitivAction_t.COG_PULL.ToInt()):
				break;
			case (EmoState.EE_CognitivAction_t.COG_LEFT.ToInt()):
				break;
			case (EmoState.EE_CognitivAction_t.COG_RIGHT.ToInt()):
				break;
			default:
				System.out.println("Invalid Action");
		}
	}


}