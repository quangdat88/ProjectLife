package com.dqdteam.game.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.dqdteam.game.MonsterPong;

public class Paddle extends Rectangle {
	public Texture paddleImage;
    public String name;
    public Boolean tweening = false;
    private MonsterPong game;

    public Boolean getTweening() {
        return tweening;
    }

    public void setTweening(Boolean tweening) {
        this.tweening = tweening;
    }

    public Paddle(MonsterPong game,String name, int y) {
    	this.game = game;
        this.name = name;
        Pixmap paddlePixmap = new Pixmap(100, 10, Pixmap.Format.RGBA8888);
        paddlePixmap.setColor(Color.WHITE);
        paddlePixmap.fill();
        this.paddleImage = game.paddleImage;
        this.width = paddleImage.getWidth();
        this.height = paddleImage.getHeight();
        this.x = (game.width / 2) - (this.width / 2);
        this.y = y;
    }

    public void dispose() {
        paddleImage.dispose();
    }

    public void setCenterX(float posX) {
        this.x = posX - (this.width / 2);
    }

    public float getRight() {
        return this.x + this.width;
    }

    public void setRight(float posX) {
        this.x = posX - this.width;
    }

    public float getCenterX() {
        return x + (width / 2);
    }

    //cai dat phuong thuc move - chung IMove
    public void move() {}

}