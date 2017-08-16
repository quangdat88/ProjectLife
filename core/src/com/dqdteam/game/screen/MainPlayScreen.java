package com.dqdteam.game.screen;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Sine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.dqdteam.game.MonsterPong;
import com.dqdteam.game.accessor.CameraAccessor;
import com.dqdteam.game.accessor.PaddleAccessor;
import com.dqdteam.game.effect.ParticleEmitter;
import com.dqdteam.game.input.InputProcessorExt;
import com.dqdteam.game.objects.Ball;
import com.dqdteam.game.objects.Controller;
import com.dqdteam.game.objects.Paddle;

/**
 * @author DQDAT Class tao ra Screen choi game chinh - xu ly cac dau vao va cac
 *         dich chuyen
 */
public class MainPlayScreen implements Screen {
	public static final int VELOCITY_ME = 400;

	final MonsterPong game;
	private int height;
	private int width;
	Paddle paddle1;
	Paddle paddle2;
	Controller leftCtr;
	Controller rightCtr;
	Controller middleCtr;
	private Ball ball;
	private OrthographicCamera camera;
	private Array<Paddle> paddleList;
	private Array<Rectangle> net;
	private Texture netTexture;
	private int player1Score;
	private int player2Score;
	private Sound paddleCollisionSound;
	private ParticleEmitter particleEmitter;
	private int paddleHits;
	private boolean allowScreenShake;
	BitmapFont scoreFont;
	InputProcessorExt input;

	public MainPlayScreen(final MonsterPong gam) {
		this.game = gam;
		width = gam.width;
		height = gam.height;
		scoreFont = game.scoreFont;

		// am thanh va cham
		paddleCollisionSound = game.assetManager.get("ping.wav", Sound.class);
		// tao ra 2 paddle
		setupPaddles();
		// tao ra vach phan cach giua
		// setupNet();
		// tao ra cac button dk
		setupController();
		// camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, width, height);

		// hieu ung cho ball
		particleEmitter = new ParticleEmitter(game);

		Controller[] aCtrl = new Controller[3];
		aCtrl[0] = leftCtr;
		aCtrl[1] = middleCtr;
		aCtrl[2] = rightCtr;
		Paddle[] aPad = new Paddle[2];
		aPad[0] = paddle1;
		aPad[1] = paddle2;
		input = new InputProcessorExt(aCtrl, aPad, gam, camera);
	}

	private void paddleMoveTween(final Paddle paddle, Vector3 pos) {

		TweenCallback tweenCallback = new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				paddle.setTweening(false);
			}
		};

		paddle.setTweening(true);
		Tween.to(paddle, PaddleAccessor.POSITION_X, .55f).ease(Sine.INOUT).target(pos.x).setCallback(tweenCallback)
				.start(game.tweenManager);
	}

	/**
	 * method tao ra cac paddles
	 */
	public void setupPaddles() {
		paddle1 = new Paddle(game, "paddle1", 80);
		paddle2 = new Paddle(game, "paddle2", height - 30);
		paddleList = new Array<Paddle>();
		paddleList.add(paddle1);
		paddleList.add(paddle2);
	}

	/**
	 * method tao ra cac button trai, phai, giua
	 */
	public void setupController() {
		leftCtr = new Controller(game.leftButton, "leftController", 0, 10);
		rightCtr = new Controller(game.rightButton, "rightController", width - 64, 10);
		middleCtr = new Controller(game.perButton, "middleControoler", (width - 64) / 2, 10);
	}

	public void setupNet() {
		net = new Array<Rectangle>();

		for (int i = 0; i < 6; i++) {
			int xPos = (width / 2);
			int yPos = 0;
			Pixmap netPixmap = new Pixmap(5, 5, Pixmap.Format.RGBA8888);
			netPixmap.setColor(Color.WHITE);
			netPixmap.fill();
			netTexture = new Texture(netPixmap);
			Rectangle newNetPiece = new Rectangle();

			newNetPiece.x = xPos;
			newNetPiece.y = yPos + (i * height / 6) + 35;
			newNetPiece.width = netTexture.getWidth();
			newNetPiece.height = netTexture.getHeight();

			net.add(newNetPiece);
			netPixmap.dispose();
		}
	}

	@Override
	public void render(float delta) {
		game.tweenManager.update(delta);
		camera.update();
		screenShake(delta);
		updateBallMovement(delta);
		Bossmove(delta);
		checkPaddleOutOfBounds();
		checkForGameOver();
		checkTotalPaddleHits();
		particleEmitter.update(ball, delta);
		Gdx.gl.glClearColor(0.075f, 0.059f, 0.188f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batchDraw();
	}

	private void Bossmove(float delta) {
		Vector3 posmove = new Vector3();
		if (this.ball != null) {
			posmove.x = this.ball.getRight();
			paddleMoveTween(paddle2, posmove);
		}
	}

	private void updateBallMovement(float deltaTime) {
		if (!(ball == null)) {
			ball.moveX(deltaTime);
			checkForPaddleCollision();
			checkForBallOutOfBounds();
			ball.moveY(deltaTime);
			checkForWallCollision();
		}
	}

	private void checkForPaddleCollision() {
		for (Paddle hitPaddle : paddleList) {
			if (Intersector.overlaps(hitPaddle, ball)) {
				paddleHits++;
				ball.yVel *= -1;
				if (ball.yVel > 0) {
					ball.yVel += 20;
					ball.xVel += 50;
				} else {
					ball.yVel -= 20;
				}

				paddleCollisionSound.play();
				startScreenShake();
				if (hitPaddle.name.equals("paddle1")) {
					ball.setPosition(ball.x, (hitPaddle.y + hitPaddle.height));
				} else if (hitPaddle.name.equals("paddle2")) {
					ball.setPosition(ball.x, (hitPaddle.y - ball.height));
				}
			}
		}
	}

	private void startScreenShake() {
		if (allowScreenShake) {
			Gdx.input.vibrate(150);

			Timeline.createSequence().push(Tween.set(camera, CameraAccessor.POSITION_XY).target(width / 2, height / 2))
					.push(Tween.to(camera, CameraAccessor.POSITION_XY, 0.035f).targetRelative(8, 0).ease(Quad.IN))
					.push(Tween.to(camera, CameraAccessor.POSITION_XY, 0.035f).targetRelative(-8, 0).ease(Quad.IN))
					.push(Tween.to(camera, CameraAccessor.POSITION_XY, 0.0175f).target(width / 2, height / 2)
							.ease(Quad.IN))
					.repeatYoyo(2, 0).start(game.tweenManager);
		}
	}

	private void screenShake(float delta) {
		if (game.tweenManager.containsTarget(camera)) {
			game.tweenManager.update(delta);
		} else {
			camera.position.set(width / 2, height / 2, 0);
		}
	}

	private void checkForBallOutOfBounds() {
		if (ball.y < 0) {
			ball.resetPosition();
			ball.reverseDirectionX();
			ball.reverseDirectionY();
			ball.resetVelocityY(-1);
			player2Score++;
			enterNormalState();
		} else if (ball.getTop() > height) {
			ball.resetPosition();
			ball.reverseDirectionX();
			ball.reverseDirectionY();
			ball.resetVelocityY(1);
			player1Score++;
			enterNormalState();
		}
	}

	private void enterNormalState() {
		paddleHits = 0;
		particleEmitter.setState("stop_emit");
		allowScreenShake = false;
	}

	private void checkForWallCollision() {
		if (ball.getRight() > width) {
			ball.reverseDirectionX();
			ball.setRight(width);
		} else if (ball.getX() < 0) {
			ball.reverseDirectionX();
			ball.setX(0f);
		}
	}

	private void checkPaddleOutOfBounds() {
		for (Paddle paddle : paddleList) {
			if (paddle.getRight() > width) {
				paddle.setRight(width);
			} else if (paddle.x < 0) {
				paddle.setX(0);
			}
		}
	}

	private void checkForGameOver() {
		if (player1Score >= 100 || player2Score >= 100) {
			if (!game.tweenManager.containsTarget(camera)) {
				ball = null;
				if (player1Score >= 5) {
					game.winningPlayer = "Player 1";
					game.player1Score++;
				} else {
					game.winningPlayer = "Player 2";
					game.player2Score++;
				}
				beginOutroTween();
			}
		}
	}

	private void checkTotalPaddleHits() {
		if (paddleHits >= 3) {
			particleEmitter.setState("emit");
			allowScreenShake = true;
		}
	}

	/**
	 * method ve ra giao dien - the hien cac doi tuong ra man hinh
	 */
	private void batchDraw() {
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		// cac paddles
		game.batch.draw(paddle1.paddleImage, paddle1.x, paddle1.y);
		game.batch.draw(paddle2.paddleImage, paddle2.x, paddle2.y);
		// cac controller
		game.batch.draw(leftCtr.button, leftCtr.x, leftCtr.y);
		game.batch.draw(rightCtr.button, rightCtr.x, rightCtr.y);
		game.batch.draw(middleCtr.button, middleCtr.x, middleCtr.y);
		/*
		 * for (Rectangle netPiece : net) { game.batch.draw(netTexture,
		 * netPiece.getX(), netPiece.getY()); }
		 */
		scoreFont.draw(game.batch, String.valueOf(player1Score), 10, 70);
		scoreFont.draw(game.batch, String.valueOf(player2Score), width - 30, 70);
		particleEmitter.drawParticles(game.batch);
		if (!(ball == null)) {
			game.batch.draw(ball.ballImage, ball.x, ball.y);
		}
		game.batch.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void resume() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void show() {
		player1Score = 0;
		player2Score = 0;
		paddleHits = 0;
		beginIntroTween();

	}

	@Override
	public void dispose() {
		paddle1.dispose();
		paddle2.dispose();
		// netTexture.dispose();
	}

	private void startMusic() {
		game.musicToPlay.stop();
		// game.musicToPlay = game.assetManager.get("recall_of_the_shadows.mp3",
		// Music.class);
		if (game.musicOn) {
			game.musicToPlay.play();
			game.musicToPlay.setLooping(true);
		}
	}

	private void beginIntroTween() {
		TweenCallback callBack = new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				ball = new Ball(game);
				// set input 9/2/2016
				// Gdx.input.setInputProcessor(new MainInputProcessor());
				Gdx.input.setInputProcessor(input);
				startMusic();

			}
		};
		camera.position.x += 800;
		Tween.to(camera, CameraAccessor.POSITION_X, 2f).targetRelative(-800).ease(Back.OUT).setCallback(callBack)
				.start(game.tweenManager);
	}

	private void beginOutroTween() {
		TweenCallback callBack = new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				game.setScreen(new WinScreen(game));
				dispose();
			}
		};
		Timeline.createSequence().pushPause(1.0f)
				.push(Tween.to(camera, CameraAccessor.POSITION_X, 2f).targetRelative(-800).ease(Back.IN))
				.setCallback(callBack).start(game.tweenManager);
	}
}