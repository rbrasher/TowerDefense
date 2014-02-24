package com.ronb.towerdefense;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;


public class Enemy extends Sprite {

	int blood;
	Rectangle bloodPool;
	int bloodstatus;

	public Enemy(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		// TODO Auto-generated constructor stub
		blood = 10;
		bloodPool = new Rectangle(this.getX(), this.getY()-this.getHeight()/2-5, this.getWidth(), 5, this.getVertexBufferObjectManager());
		bloodPool.setColor(0,1,0);
		bloodstatus=0;
	}
	
	public Rectangle getbloodPool() {
		return bloodPool; // our main class uses this to attach to the scene
	}

}