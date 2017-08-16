package com.dqdteam.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.dqdteam.game.accessor.CameraAccessor;
import com.dqdteam.game.accessor.PaddleAccessor;
import com.dqdteam.game.accessor.TableAccessor;
import com.dqdteam.game.objects.Paddle;
import com.dqdteam.game.screen.LoadingScreen;
import com.dqdteam.game.screen.MainPlayScreen;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

//Oke
public class MonsterPong extends Game {
    public int width;
	public int height;
    public SpriteBatch batch;
    public Screen mainMenu;
    public MainPlayScreen pongBoard;
    public boolean musicOn = true;
    public String winningPlayer = "Player 1";
    public int player1Score = 0;
    public int player2Score = 0;
    public Music musicToPlay;
    public boolean musicCurrentlyPlaying = false;
    public TweenManager tweenManager;
    public AssetManager assetManager;
    public LabelStyle titleStyle;
    public Texture ballImage;
    public Texture leftButton;
    public Texture rightButton;
    public Texture perButton;
    public Texture paddleImage;
    public Texture smallParticleImage;
    public Texture mediumParticleImage;
    public Texture largeParticleImage;
    public Texture netImage;
    public BitmapFont scoreFont;

    public MonsterPong(int width,int height){
    	this.width  = width;
    	this.height = height;
    }
	@Override
	public void create () {
        setupTweenManager();
        assetManager = new AssetManager();

        batch = new SpriteBatch();
        this.setScreen(new LoadingScreen(this));
	}

	@Override
	public void render () {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    private void setupTweenManager() {
        tweenManager = new TweenManager();
        Tween.registerAccessor(Camera.class, new CameraAccessor());
        Tween.registerAccessor(Table.class, new TableAccessor());
        Tween.registerAccessor(Paddle.class, new PaddleAccessor());
    }

}
