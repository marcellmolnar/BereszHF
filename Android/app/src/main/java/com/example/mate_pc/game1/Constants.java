package com.example.mate_pc.game1;

public abstract class Constants {

    final static String CONTROL_SETTINGS_KEY = "controlSettings_isJoystick";
    final static String SHOW_JOYSTICK_SETTINGS_KEY = "showJoystickSettings_isJoystick";
    public final static String SOUND_SETTINGS_KEY = "soundSettings_isSound";
    final static String BACKGROUND_SETTINGS_KEY = "backgroundSettings_chosenBackground";
    public final static String MY_SETTINGS = "RicardoSettings";

    public final static int START_SETTINGS_CODE = 121;
    final static int START_CONNECTOR_CODE = 314;
    final static int START_GAME_MENU_CODE = 318;
    public final static int RESULT_CODE_SETTINGS_MAY_CHANGED = 0x01;
    public final static int RESULT_CODE_CONNECTOR_RETURN = 0x01;

    final static String SELECTED_BACKGROUND_INTENT_EXTRA = "selectedBackground";

    public final static String CONNECTOR_IP_KEY = "server_ip_address";
    public final static String WON_THE_MATCH_KEY = "won_the_match";

}
