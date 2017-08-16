package com.dqdteam.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.dqdteam.game.MonsterPong;

public class Controller extends Rectangle {

	public Texture button;
	public String name;
	public boolean isPress = false;

	public Controller(Texture button, String name, float posX, float posY) {
		this.button = button;
		this.name = name;
		this.x = posX;
		this.y = posY;
		this.width = button.getWidth();
        this.height = button.getHeight();
	}

	public void pressController(){
		this.isPress = true;
	}
	
	public void upController(){
		this.isPress = false;
	}
	
	public void dispose() {
		button.dispose();
	}
}
