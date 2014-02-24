package com.ronb.towerdefense;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.IModifier;

public class Tower extends Sprite {
	ITextureRegion bullet;
	VertexBufferObjectManager pVB;
	float x, y;
	Sprite SpriteBullet;
	int speed = 500;
	//ArrayList<Sprite> arrayBullets;
	int bulletOfTowerDie = 0;

	public Tower(float pX, float pY, float pWidth, float pHeight, ITextureRegion b, ITextureRegion t, VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pWidth, pHeight, t, pVertexBufferObjectManager);
		// TODO Auto-generated constructor stub
		bullet = b; // we need bullet TextureRegoin to make one
		pVB = pVertexBufferObjectManager;
		x = pX; // some x n y of the tower
		y = pY;
		//arrayBullets = new ArrayList<Sprite>(); // create a new ArrayList
	}

	public void fire(float targetX, float targetY, float tx, float ty) {

		SpriteBullet = new Sprite(tx, ty, 10, 10, bullet, pVB);
		bulletOfTowerDie = 0;

		float gY = targetY - SpriteBullet.getY(); // some calu about how far the
		// bullet can go, in this
		// case up to the enemy
		float gX = targetX - SpriteBullet.getX();

		//MoveByModifier movMByod = new MoveByModifier(0.5f, gX, gY);
		MoveByModifier movMByod = new MoveByModifier(0.2f, gX, gY, new IEntityModifierListener(){

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
			// TODO Auto-generated method stub
			
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				// TODO Auto-generated method stub
				if(bulletOfTowerDie==0)
				bulletOfTowerDie = 1;
			
			}

		});
		SpriteBullet.registerEntityModifier(movMByod);

		speed = 500; // speed
		//arrayBullets.add(SpriteBullet);
	}

	public Sprite getBulletSprite() {
		return SpriteBullet; // our main class uses this to attach to the scene
	}

	//public ArrayList<Sprite> getArrayList() {
		// return arrayBullets; // our main class uses this to check bullets etc
	//}

}

/*
public class Tower extends Sprite {
	TextureRegion bullet;
	float x, y;
	Sprite SpriteBullet;
	int speed = 500;
	ArrayList<Sprite> arrayBullets;
	
	public Tower(TextureRegion b, float pX, float pY, float pWidth, float pHeight, TextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pWidth, pHeight, pTextureRegion, pVertexBufferObjectManager);
		bullet = b;
		x = pX;
		y = pY;
		arrayBullets = new ArrayList<Sprite>();
	}
	
	public void fire(float targetX, float targetY, float tx, float ty) {
		SpriteBullet = new Sprite(tx, ty, 10, 10, bullet, getVertexBufferObjectManager());
		
		float gY = targetY - SpriteBullet.getY();
		float gX = targetX - SpriteBullet.getX();
		
		MoveByModifier moveMByod = new MoveByModifier(0.5f, gX, gY);
		SpriteBullet.registerEntityModifier(moveMByod);
		
		speed = 500;
		arrayBullets.add(SpriteBullet);
	}
	
	public Sprite getBulletSprite() {
		return SpriteBullet;	//our main class uses this to attach to the scene
	}
	
	public ArrayList<Sprite> getArrayList() {
		return arrayBullets;	//our main class uses this to check bullets, etc.
	}
}
*/
