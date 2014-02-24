package com.ronb.towerdefense;

import java.util.ArrayList;
import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.IPathModifierListener;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.ease.EaseLinear;

import android.os.Handler;

public class TowerTest extends SimpleBaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;

	// ===========================================================
	// Fields
	// ===========================================================
	private BuildableBitmapTextureAtlas mBitmapTextureAtlas;
	private ITextureRegion mTowerTextureRegion;
	private ITextureRegion mEnemyTextureRegion;
	private ITextureRegion mBulletTextureRegion;

	//private BitmapTextureAtlas fontTexture;
	//private Font font;
	static Scene scene;

	private Enemy Enemy;

	Path path;

	ArrayList<Enemy> arrayEn;
	ArrayList<Tower> arrayTower;
	Sprite getBullet;

	// ========================================
	// Others
	// ========================================
	// for touches
	float touchX;
	float touchY;

	Tower tw;
	Handler TIMER_ONE; // our thread

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BuildableBitmapTextureAtlas(this.getTextureManager(), 512, 512);
		this.mTowerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "tower.png");
		this.mBulletTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "bullet.png");
		this.mEnemyTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "enemy.png");

		//fontTexture = new BitmapTextureAtlas(this.getTextureManager(), 256,
		//256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		//font = FontFactory.create(this.getFontManager(), fontTexture,
		//Typeface.DEFAULT, 40, true, Color.RED);

		try {
			this.mBitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			this.mBitmapTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		scene = new Scene();
		scene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));

		arrayEn = new ArrayList<Enemy>();
		arrayTower = new ArrayList<Tower>();

		path = new Path(9).to(10, 10).to(10, CAMERA_HEIGHT/2 - 74).to(CAMERA_WIDTH/2 - 58, CAMERA_HEIGHT/2 - 74)
			.to(CAMERA_WIDTH/2 - 58, CAMERA_HEIGHT - 74).to(CAMERA_WIDTH - 58, CAMERA_HEIGHT - 74)
			.to(CAMERA_WIDTH/2 - 58, CAMERA_HEIGHT - 74).to(CAMERA_WIDTH/2 - 58, CAMERA_HEIGHT/2 - 74)
			.to(10, CAMERA_HEIGHT/2 - 74).to(10, 10);
		
		/*
		* Calculate the coordinates for the face, so its centered on the
		* camera.
	
		final float centerX = (CAMERA_WIDTH - this.mTowerTextureRegion
		.getWidth()) / 2;
		final float centerY = (CAMERA_HEIGHT - this.mTowerTextureRegion
		.getHeight()) / 2;
		*/
		scene.registerUpdateHandler(loop);
		scene.setTouchAreaBindingOnActionDownEnabled(true);
		// scene.setOnSceneTouchListener(pOnSceneTouchListener);
		scene.setOnSceneTouchListener(new IOnSceneTouchListener() {

			@Override
			public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
				if (pSceneTouchEvent.isActionDown()) {
					touchX = pSceneTouchEvent.getX();
					touchY = pSceneTouchEvent.getY();
					System.out.println("<<<<<<<<<<<<<<<<<<<<Touch screen activated!!!");
					tw = new Tower(touchX, touchY, 32, 32, mBulletTextureRegion, mTowerTextureRegion, getVertexBufferObjectManager()) {
				
						public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				
						tw.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
						return true;
						}
					};
				
					arrayTower.add(tw); // add to array
					scene.registerTouchArea(tw); // register touch area , so
					// this allows you to drag it
					scene.attachChild(tw); // add it to the scene
				}
		
				if (pSceneTouchEvent.isActionUp()) {
					return true;
				}
		
				if (pSceneTouchEvent.isActionMove()) {
					touchX = pSceneTouchEvent.getX();
					touchY = pSceneTouchEvent.getY();
			
					return true;
				}
				return true;
			}
		});

		add_enemy(); // timer add enemy every amount of defined secs
		//TIMER_ONE = new Handler(); //Thread Created
		TIMER_ONE_START(); // start our Thread 
		/*
		* final Sprite Tower = new Sprite(centerX, centerY,
		* this.mTowerTextureRegion, this.getVertexBufferObjectManager());
		* scene.registerTouchArea(Tower); scene.attachChild(Tower);
		* scene.setTouchAreaBindingOnActionDownEnabled(true);
		*
		* final Sprite Bullet = new ButtonSprite(centerX, centerY +
		* this.mTowerTextureRegion.getHeight(), this.mBulletTextureRegion,
		* this.getVertexBufferObjectManager(), this);
		* scene.registerTouchArea(Bullet); scene.attachChild(Bullet);
		* scene.setTouchAreaBindingOnActionDownEnabled(true);
		*
		* final Sprite Enemy = new ButtonSprite(centerX, centerY +
		* this.mTowerTextureRegion.getHeight()*2, this.mEnemyTextureRegion,
		* this.getVertexBufferObjectManager(), this);
		* scene.registerTouchArea(Enemy); scene.attachChild(Enemy);
		* scene.setTouchAreaBindingOnActionDownEnabled(true);
		*/

		return scene;
	}

	int allow_enemy;
	private float targetX;
	private float targetY;

	public void add_enemy() {
		final float delay = 2f;

		TimerHandler enemy_handler = new TimerHandler(delay, true, new ITimerCallback() {
	
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
		
				// =================Code must go here=======================
		
				if (allow_enemy >= 4) {
				
				} else {
					allow_enemy++;
					Random a = new Random();
					int b = a.nextInt(400) + 10;
		
					Enemy = new Enemy(b, b, mEnemyTextureRegion,getVertexBufferObjectManager());
			
					Enemy.registerEntityModifier(new LoopEntityModifier(new PathModifier(30, path, null, new IPathModifierListener() {
						@Override
						public void onPathStarted(final PathModifier pPathModifier, final IEntity pEntity) {
							Debug.d("onPathStarted");
						}
				
						@Override
						public void onPathWaypointStarted(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {
							Debug.d("onPathWaypointStarted: " + pWaypointIndex);
						}
				
						@Override
						public void onPathWaypointFinished(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {
							Debug.d("onPathWaypointFinished: " + pWaypointIndex);
						}
				
						@Override
						public void onPathFinished(final PathModifier pPathModifier, final IEntity pEntity) {
							Debug.d("onPathFinished");
						}
						}, EaseLinear.getInstance())));
						scene.attachChild(Enemy);
						arrayEn.add(Enemy);
				}
					// this above code adds enemy every 2s
				
					// ================= end of
					// code==========================
		
			}
		});
		getEngine().registerUpdateHandler(enemy_handler);

	}

	public void TIMER_ONE_START() {

		// == THREAD STARTS
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(5);

						TowerTest.this.runOnUpdateThread(new Runnable() {
							@Override
							public void run() {
								collision(); // run the <--collision every 5ms
							}
						});

					} catch (Exception e) {
					}
				}
			}
		}).start(); /* Re-start the thread */
		// == THREAD ENDS
	}

	public void checkBulletDie(Tower tower) {
		// check if bullet die on the ground at the end
		if (getBullet!=null && tower.bulletOfTowerDie == 1){
			getBullet = tower.getBulletSprite(); // gets bullets from
			tower.bulletOfTowerDie = 2;
			// Tower class where bullets are fired from
			scene.detachChild(getBullet); // no longer needed to be shown

			getBullet.dispose();
			getBullet=null;

			// you can remove shoot enemies here or create enemy class with
			// its own life here
		}
	}
	
	public boolean checkBulletHitOnTarget(Tower tower, Enemy enemy){
		getBullet = tower.getBulletSprite(); // gets bullets from
		if(getBullet != null && getBullet.collidesWith(enemy) && tower.bulletOfTowerDie == 0){
			tower.bulletOfTowerDie = 3;
			scene.detachChild(getBullet); // no longer needed to be shown

			getBullet.dispose();
			getBullet=null;

			enemy.blood--;
			System.out.println("<<<<<<<<<<<<enemy blood"+enemy.blood);
			if(enemy.blood<-1){

				System.out.println("<<<<<<<<<<<<enemy blood"+enemy.blood+enemy.blood+enemy.blood+enemy.blood+enemy.blood+enemy.blood);
				System.out.println("<<<<<<<<<<<<wow wow wow!!!!! Die is not enough?? Already dead"+enemy.blood);
			} else if(enemy.blood<0 && tower.bulletOfTowerDie!=4){
				System.out.println("<<<<<<<<<<<<enemy blood"+enemy.blood+enemy.blood+enemy.blood+enemy.blood+enemy.blood+enemy.blood);
				scene.detachChild(enemy); // no longer needed to be shown

				Rectangle blood = enemy.getbloodPool();
				scene.detachChild(blood);

				arrayEn.remove(enemy);
				enemy.dispose();
				enemy=null;

				blood.dispose();
				blood=null;

				allow_enemy--;
				//add_enemy();
			}

			return true;
		}
		return false;
	}
	
	public void collision() {
		// Lets Loop our array of enemies

		// for(Sprite enemy: arrayEn){
		for (int j = 0; j < arrayEn.size(); j++) {
			Enemy enemy = arrayEn.get(j);

			if(arrayEn.get(j).bloodstatus==1){
				arrayEn.get(j).bloodPool.setX(arrayEn.get(j).getX());
				arrayEn.get(j).bloodPool.setY(arrayEn.get(j).getY()-arrayEn.get(j).getHeight()/2-5);
				arrayEn.get(j).bloodPool.setWidth(arrayEn.get(j).getWidth()/10.0f*arrayEn.get(j).blood);
			}

			// enemy.setPosition(enemy.getX()+3/6f,enemy.getY()); you can use to
			// move enemy
			// Lets Loop our Towers
			// for(Tower tower: arrayTower){
			for (int k = 0; k < arrayTower.size(); k++) {
				Tower tower = (Tower) arrayTower.get(k);
				tower.speed--;

				float rangex = enemy.getX() - tower.getX();
				float rangey = enemy.getY() - tower.getY();
				float range = rangex*rangex + rangey*rangey;
				range = (float) Math.sqrt(range);
				// check if they collide
				if (range<100 && tower.speed <= 0) {

					System.out.println(">>>>>>>>>>fire!!!"+this.arrayEn.size()+":"+this.arrayTower.size());
					fire(tower, enemy);// call the fire and send pass the tower
					// and enemy to fire

					break; // take a break

				}

				if(checkBulletHitOnTarget(tower,enemy)){
					
				} else{
					checkBulletDie(tower);
				}

			}
		}
	}

	public void fire(Tower tower, Enemy enemy) {

		targetX = enemy.getX() + enemy.getWidth() / 2; // simple get the enemy
		// x,y and middle it and tell the bullet where to aim and fire
		targetY = enemy.getY() + enemy.getHeight() / 2;

		tower.fire(targetX, targetY, tower.getX() + tower.getWidth() / 2, tower.getY() + tower.getHeight() / 2); 
		// Asks the tower to open fire and places the bullet in middle of tower

		scene.attachChild(tower.getBulletSprite());

		if(Enemy.bloodstatus==0){
			Rectangle blood = Enemy.getbloodPool();
			scene.attachChild(blood);
			blood.setX(targetX);
			blood.setY(targetY-enemy.getHeight()/2-5);
			blood.setWidth(Enemy.getWidth()/10.0f*Enemy.blood);
			arrayEn.get(arrayEn.indexOf(Enemy)).bloodstatus=1;
		}
	}

	IUpdateHandler loop = new IUpdateHandler() {
		@Override
		public void reset() {
			
		}

		@Override
		public void onUpdate(float pSecondsElapsed) {
	
			// =================Code must go here=======================
		
			// this is free to do any other things you like!
		
			// code ends
	
		}
	};

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}