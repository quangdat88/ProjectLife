package com.dqdteam.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dqdteam.game.PongForAndroid;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = PongForAndroid.WIDTH;
        config.height = PongForAndroid.HEIGHT;
        new LwjglApplication(new PongForAndroid(), config);

    }
}
