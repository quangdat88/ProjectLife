package com.dqdteam.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dqdteam.game.MonsterPong;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
        config.width = 320;
        config.height = 480;
        config.useGL30 = true;
        new LwjglApplication(new MonsterPong(config.width,config.height), config);
    }
}
