package com.ravelsoftware.ravtech.dk.ui.options;

public interface SettingsComponent {

    /** retrieves the setting */
    void load ();

    /** applies the setting */
    void save ();
}
