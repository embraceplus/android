package com.embraceplus.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.embraceplus.app.R;
import com.embraceplus.model.EffectVO;
import com.embraceplus.utils.EColor;

public class EmulatorView extends View {

	private static final String TAG = "EmulatorView";
	
	private static final int STATE_IDLE = 0;
	private static final int STATE_FADE_IN = 1;
	private static final int STATE_HOLD = 2;
	private static final int STATE_FADE_OUT = 3;
	private static final int STATE_PAUSE = 4;
	
	private int currentState = STATE_IDLE;
	
	private int currentColorLeft = 0;
	private int currentColorRight = 0;
	
	private int targetColorLeft = 0;
	private int targetColorRight = 0;
	
	private int baseColorLeft = 0;
	private int baseColorRight = 0;
	
	private boolean isPlaying = false;
	
	private long lastRenderTime = 0;
	
	private long currentTime = 0;
	private long loopDuration = 0;
	
	private Drawable mLeftDrawable;
	private Drawable mRightDrawable;
	
	private EffectVO effect;
	
	private boolean once = false;
	
	public void play(EffectVO effect) {
		
		this.effect = effect;
		
		loopDuration = effect.getLoopDuration();
		
		if (effect.random) {
			
			baseColorLeft = EColor.getRandomColor();
			baseColorRight = EColor.getRandomColor();
			
			targetColorLeft = EColor.getRandomColor();
			targetColorRight = EColor.getRandomColor();
			
		} /*else if (effect.revert)
		{
			baseColorLeft = effect.colorL1;
			//baseColorRight = effect.colorR1;
			baseColorRight = Color.TRANSPARENT;
			
			targetColorLeft = Color.TRANSPARENT;
			targetColorRight = effect.colorL1;
		}*/else {
			
			baseColorLeft = effect.colorL1;
			baseColorRight = effect.colorR1;
			
			targetColorLeft = effect.colorL2;
			targetColorRight = effect.colorR2;
		}
		
		adjustTransparentColors();
		
		lastRenderTime = System.currentTimeMillis();
		currentTime = 0;
		
		isPlaying = true;
		
		currentState = STATE_IDLE;
			
		post(postRender);
		
	}
	
	public void playOnce(EffectVO effect) {
		once = true;
		play(effect);
	}
	
	public void pause() {
		if (isPlaying) {
			isPlaying = false;
		}
	}
	
	private void render() {
		long now = System.currentTimeMillis();
		long deltaTime = (now - lastRenderTime);
		
		currentTime += deltaTime;
		
		if (currentTime >= loopDuration) {
			if (once) {
				once = false;
				removeCallbacks(postRender);
				return;
			}
			currentTime -= loopDuration;
		}
		
		float fraction = 0;
		
		if (currentTime >= 0 && currentTime < effect.fadeInTime) {
			
			if (currentState != STATE_FADE_IN) {
				currentState = STATE_FADE_IN;
				
				if (effect.random) {
					targetColorLeft = EColor.getRandomColor();
					targetColorRight = EColor.getRandomColor();
					
					adjustTransparentColors();
				}
			}
			
			fraction = currentTime / (float) effect.fadeInTime;
			
			currentColorLeft = EColor.getInterpolatedColor(fraction, baseColorLeft, targetColorLeft);
			currentColorRight = EColor.getInterpolatedColor(fraction, baseColorRight, targetColorRight);
			
		} else if (currentTime >= effect.fadeInTime && currentTime < effect.fadeInTime + effect.holdTime) {
			
			if (currentState != STATE_HOLD) {
				currentState = STATE_HOLD;
			}
			
			currentColorLeft = targetColorLeft;
			currentColorRight = targetColorRight;
			
		} else if (currentTime >= effect.fadeInTime + effect.holdTime &&
				currentTime < effect.fadeInTime + effect.holdTime + effect.fadeOutTime) {
			
			if (currentState != STATE_FADE_OUT) {
				currentState = STATE_FADE_OUT;
				
				if (effect.random) {
					
					baseColorLeft = EColor.getRandomColor();
					baseColorRight = EColor.getRandomColor();
					
					adjustTransparentColors();
				}
			}
			
			fraction = 1 - (float) (currentTime - effect.fadeInTime - effect.holdTime) / (float) (effect.fadeOutTime);
			
			currentColorLeft = EColor.getInterpolatedColor(fraction, baseColorLeft, targetColorLeft);
			currentColorRight = EColor.getInterpolatedColor(fraction, baseColorRight, targetColorRight);
			
		} else if (currentTime >= effect.fadeInTime + effect.holdTime + effect.fadeOutTime &&
				currentTime < loopDuration) {
			
			
			
			if (currentState != STATE_PAUSE) {
				currentState = STATE_PAUSE;
			}
			
			currentColorLeft = effect.blackoutOnPause ? Color.TRANSPARENT : baseColorLeft;
			currentColorRight = effect.blackoutOnPause ? Color.TRANSPARENT : baseColorRight;
		}
		
		lastRenderTime = now;
		
		if (isPlaying) {
			postInvalidate();
			post(postRender);
		} else {
			removeCallbacks(postRender);
		}
	}
	
	private void adjustTransparentColors() {
		if (Color.alpha(baseColorLeft) == 0) {
			baseColorLeft = Color.argb(0, Color.red(targetColorLeft), Color.green(targetColorLeft), Color.blue(targetColorLeft));
		} else if (Color.alpha(targetColorLeft) == 0) {
			targetColorLeft = Color.argb(0, Color.red(baseColorLeft), Color.green(baseColorLeft), Color.blue(baseColorLeft));
		}
		
		if (Color.alpha(baseColorRight) == 0) {
			baseColorRight = Color.argb(0, Color.red(targetColorRight), Color.green(targetColorRight), Color.blue(targetColorRight));
		} else if (Color.alpha(targetColorRight) == 0) {
			targetColorRight = Color.argb(0, Color.red(baseColorRight), Color.green(baseColorRight), Color.blue(baseColorRight));
		}
	}
	
	private final Runnable postRender = new Runnable() {
		@Override
		public void run() {
			render();
		}
	};
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if (!isInEditMode()) {

			mLeftDrawable.setColorFilter(currentColorLeft, Mode.MULTIPLY);
			mRightDrawable.setColorFilter(currentColorRight, Mode.MULTIPLY);
		}
	}

	private void init() {
		LayerDrawable background = (LayerDrawable) getBackground();
		
		mLeftDrawable = background.findDrawableByLayerId(R.id.bracelet_left);
		mRightDrawable = background.findDrawableByLayerId(R.id.bracelet_rigth);
	}

	public EmulatorView(Context context) {
		super(context);
		init();
	}
	
	public EmulatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

}
