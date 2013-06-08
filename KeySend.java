import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.*;

public class KeySend {

	private Robot keyboard;
	private int push;
	private int pull;
	private int left;
	private int right;

	public KeySend() {
		try {
			keyboard = new Robot();
			push = EmoState.EE_CognitivAction_t.COG_PUSH.ToInt();
			pull = EmoState.EE_CognitivAction_t.COG_PULL.ToInt();
			left = EmoState.EE_CognitivAction_t.COG_LEFT.ToInt();
			right = EmoState.EE_CognitivAction_t.COG_RIGHT.ToInt();
		} catch (AWTException e) {
			e.printStackTrace(); 
		}
	}

	/*
	given an action and a power, push appropriate key on keyboard. used for lego mindstorm car (for now)
	*/
	public void takeInput(int action, double power) {
		if (action == push) {
			giveCommand(KeyEvent.VK_UP);
		} else if (action == pull) {
			giveCommand(KeyEvent.VK_DOWN);
		} else if (action == left) {
			giveCommand(KeyEvent.VK_LEFT);
		} else if (action == right) {
			giveCommand(KeyEvent.VK_RIGHT);
		} else {
			System.out.println("Invalid Action");
		}	
	}

	//simulates an arrow key press given a direction
	private void giveCommand(int direction) {
		keyboard.keyPress(direction);
		keyboard.delay(100);
		keyboard.keyRelease(direction);
		System.out.println("commanded");
	} 
}