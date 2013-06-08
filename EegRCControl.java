import java.util.Hashtable;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class EegRCControl  {

	public static int[] cognitivActionList ={EmoState.EE_CognitivAction_t.COG_NEUTRAL.ToInt(),
			   EmoState.EE_CognitivAction_t.COG_PUSH.ToInt(),
			   EmoState.EE_CognitivAction_t.COG_PULL.ToInt(),
			   EmoState.EE_CognitivAction_t.COG_LIFT.ToInt(),
			   EmoState.EE_CognitivAction_t.COG_DROP.ToInt(),
			   EmoState.EE_CognitivAction_t.COG_LEFT.ToInt(),
			   EmoState.EE_CognitivAction_t.COG_RIGHT.ToInt(),
			   EmoState.EE_CognitivAction_t.COG_ROTATE_LEFT.ToInt(),
			   EmoState.EE_CognitivAction_t.COG_ROTATE_RIGHT.ToInt(),
			   EmoState.EE_CognitivAction_t.COG_ROTATE_CLOCKWISE.ToInt(),
               EmoState.EE_CognitivAction_t.COG_ROTATE_COUNTER_CLOCKWISE.ToInt(),
               EmoState.EE_CognitivAction_t.COG_ROTATE_FORWARDS.ToInt(),
               EmoState.EE_CognitivAction_t.COG_ROTATE_REVERSE.ToInt(),
               EmoState.EE_CognitivAction_t.COG_DISAPPEAR.ToInt()
				};
	public static Boolean[] cognitivActionsEnabled = new Boolean[cognitivActionList.length];

	public static Hashtable<Integer, String> actionNames = new Hashtable<Integer, String>();
    	

    public static void initialize() {
    	String[] actionStrings = {"Neutral" ,"Push" ,"Pull" ,"Lift" ,"Drop" ,"Left" ,"Right" ,"RotateLeft" ,"RotateRight" 
        ,"RotateClock"    ,"RotateC_Clock"    ,"RotateForward"    ,"RotateReverse"    ,"Disappear"};
        for (int i = 1; i < 15; i++) {
        	actionNames.put(i, actionStrings[i-1]);
        }
    }	


	
	/// <summary>
    /// Start traning cognitiv action
    /// </summary>
    /// <param name="cognitivAction">Cognitiv Action</param>
    public static void StartTrainingCognitiv(EmoState.EE_CognitivAction_t cognitivAction)
    {
        if (cognitivAction == EmoState.EE_CognitivAction_t.COG_NEUTRAL)
        {
        	Edk.INSTANCE.EE_CognitivSetTrainingAction(0,EmoState.EE_CognitivAction_t.COG_NEUTRAL.ToInt());
			Edk.INSTANCE.EE_CognitivSetTrainingControl(0, Edk.EE_CognitivTrainingControl_t.COG_START.getType());
        }
        else
            for (int i = 1; i < cognitivActionList.length; i++)
            {
                if (cognitivAction.ToInt() == cognitivActionList[i])
                {
                    
                    if (cognitivActionsEnabled[i])
                    {
                    	Edk.INSTANCE.EE_CognitivSetTrainingAction(0, cognitivAction.ToInt());
                    	Edk.INSTANCE.EE_CognitivSetTrainingControl(0, Edk.EE_CognitivTrainingControl_t.COG_START.getType());
                    }
                    
                }
            }

    }
    /// <summary>
    /// Enable cognitiv action in arraylist
    /// </summary>
    /// <param name="cognitivAction">Cognitiv Action</param>
    /// <param name="iBool">True = Enable/False = Disable</param>
    public static void EnableCognitivAction(EmoState.EE_CognitivAction_t cognitivAction, Boolean iBool)
    {
        for (int i = 1; i < cognitivActionList.length; i++)
        {
            if (cognitivAction.ToInt() == cognitivActionList[i])
            {
                cognitivActionsEnabled[i] = iBool;
                
            }
        }

    }
    
    public static void EnableCognitivActionsList()
    {
        long cognitivActions = 0x0000;
        for (int i = 1; i < cognitivActionList.length; i++)
        {
            if (cognitivActionsEnabled[i])
            {
                cognitivActions = cognitivActions | ((long)cognitivActionList[i]);
                
            }
        }
        Edk.INSTANCE.EE_CognitivSetActiveActions(0, cognitivActions);
        
    }
	public static void main(String args[]) throws InterruptedException {
		System.out.println(EmoState.EE_CognitivAction_t.COG_NEUTRAL.ToInt());
		initialize();
		Pointer eEvent			= Edk.INSTANCE.EE_EmoEngineEventCreate();
    	Pointer eState			= Edk.INSTANCE.EE_EmoStateCreate();
    	IntByReference userID 	= null;
    	short composerPort		= 3008;
    	int option 				= 2;
    	int state  				= 0;
    	KeySend keyboard 		= new KeySend();
	
    	userID = new IntByReference(0);
    	
    	switch (option) {
		case 1:
		{
			if (Edk.INSTANCE.EE_EngineConnect("Emotiv Systems-5") != EdkErrorCode.EDK_OK.ToInt()) {
				System.out.println("Emotiv Engine start up failed.");
				return;
			}
			break;
		}
		case 2:
		{
			System.out.println("Target IP of ControlPanel: [127.0.0.1] ");

			if (Edk.INSTANCE.EE_EngineRemoteConnect("127.0.0.1", composerPort, "Emotiv Systems-5") != EdkErrorCode.EDK_OK.ToInt()) {
				System.out.println("Cannot connect to EmoComposer on [127.0.0.1]");
				return;
			}
			System.out.println("Connected to ControlPanel on [127.0.0.1]");
			break;
		}
		default:
			System.out.println("Invalid option...");
			return;
    	}
    	
		while (true) {
			state = Edk.INSTANCE.EE_EngineGetNextEvent(eEvent);
			
			// New event needs to be handled
			if (state == EdkErrorCode.EDK_OK.ToInt()) {

				int eventType = Edk.INSTANCE.EE_EmoEngineEventGetType(eEvent);
				Edk.INSTANCE.EE_EmoEngineEventGetUserId(eEvent, userID);
				
				if(eventType == Edk.EE_Event_t.EE_EmoStateUpdated.ToInt()) {
					Edk.INSTANCE.EE_EmoEngineEventGetEmoState(eEvent, eState);
					
						int action = EmoState.INSTANCE.ES_CognitivGetCurrentAction(eState);
						double power = EmoState.INSTANCE.ES_CognitivGetCurrentActionPower(eState);
						if(power!=0){
						
							System.out.println("action: " + action + "\npower: "+ power);

							keyboard.takeInput(action, power);
							//Thread.sleep(2000);

						}
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
	// MainGuiDev() {
	// 	cognitivActionsEnabled[0] = true;
 //        for (int i = 1; i < cognitivActionList.length; i++)
 //        {
 //            cognitivActionsEnabled[i] = false;
 //        }
	// 	this.setSize(600, 300);
	// 	Container content = getContentPane();
	//     content.setBackground(Color.white);
	//     content.setLayout(new FlowLayout());
	    
	//     String [] options = {"Neutral","Push","Lift"};
	//     comboBox = new JComboBox(options);
	 
	//     add(comboBox);
	// 	trainBtt = new JButton("Train");
	// 	trainBtt.addActionListener(new ActionListener() {
			
	// 		@Override
	// 		public void actionPerformed(ActionEvent e) {
		
	// 			int index = comboBox.getSelectedIndex();
	// 			if(index == 0)
	// 			{
	// 				Edk.INSTANCE.EE_CognitivSetTrainingAction(0,EmoState.EE_CognitivAction_t.COG_NEUTRAL.ToInt());
	// 				Edk.INSTANCE.EE_CognitivSetTrainingControl(0, Edk.EE_CognitivTrainingControl_t.COG_START.getType());
	// 			}
	// 			if(index ==1)
	// 			{
	// 				try
	// 				{
	// 					EnableCognitivAction(EmoState.EE_CognitivAction_t.COG_PUSH, true);
	// 					EnableCognitivActionsList();
	// 					StartTrainingCognitiv(EmoState.EE_CognitivAction_t.COG_PUSH);
	// 				}
	// 				catch(Exception ex){
	// 					ex.printStackTrace();
	// 				}
	// 			}
	// 			if(index == 2)
	// 			{
	// 				try
	// 				{
	// 					EnableCognitivAction(EmoState.EE_CognitivAction_t.COG_LIFT, true);
	// 					EnableCognitivActionsList();
	// 					StartTrainingCognitiv(EmoState.EE_CognitivAction_t.COG_LIFT);
	// 				}
	// 				catch(Exception ex){
	// 					ex.printStackTrace();
	// 				}
	// 			}
	// 		}
	// 	});
		
	// 	add(trainBtt);
		
	// 	/// Save Profile handle
	// 	saveBtt = new JButton("Save Profile");
	// 	saveBtt.addActionListener(new ActionListener() {
			
	// 		@Override
	// 		public void actionPerformed(ActionEvent e) {
				
	// 			EmoProfileManagement.SaveCurrentProfile();
	// 			EmoProfileManagement.SaveProfilesToFile();
	// 		}
	// 	});
	// 	add(saveBtt);
		
	// 	/// Load Profile handle
	// 	loadBtt = new JButton("Load Profile");
	// 	loadBtt.addActionListener(new ActionListener() {
			
	// 		@Override
	// 		public void actionPerformed(ActionEvent e) {
	// 			EmoProfileManagement.LoadProfilesFromFile();
	// 			EmoProfileManagement.SetUserProfile("1");
	// 			String actionList = EmoProfileManagement.CheckCurrentProfile();
	// 			long cognitivActions = Long.valueOf(actionList);
	// 			Edk.INSTANCE.EE_CognitivSetActiveActions(0, cognitivActions);
	// 		}
	// 	});
	// 	add(loadBtt);
	// 	setVisible(true);
	// }
	