package com.example.mate_pc.game1;

public abstract class Constants {

    final static String controlSettingsKey = "controlSettings_isJoystick";
    final static String soundSettingsKey = "soundSettings_isSound";
    final static String backgroundSettingsKey = "backgroundSettings_chosenBackground";
    final static String MY_SETTINGS = "RicardoSettings";

    public final static int START_SETTINGS_CODE = 121;
    final static int START_CONNECTOR_CODE = 314;
    public final static int RESULT_CODE_SETTINGS_MAY_CHANGED = 0x01;
    public final static int RESULT_CODE_CONNECTOR_RETURN = 0x01;

    final static String SELECTED_BACKGROUND_INTENT_EXTRA = "selectedBackground";

    public final static String CONNECTOR_IP_CODE = "server_ip_address";

}
