package com.tommy.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import com.tommy.R;
public class ClearEditText extends EditText implements View.OnFocusChangeListener, TextWatcher {

	/** 删除按钮的引用 */
	private Drawable mClearDrawable;
	/** 控件是否有焦点 */
	private boolean hasFoucs;

	/**
	 * 使用代码new ClearEditText时，会调用这个方法。
	 * 
	 * @param context
	 */
	public ClearEditText(Context context) {
		this(context, null);
	}

	/**
	 * 当在layout使用ClearEditText时会自动调用这个方法
	 * 
	 * @param context
	 * @param attrs
	 */
	public ClearEditText(Context context, AttributeSet attrs) {
		// 这里构造方法也很重要，不加这个，很多属性不能在xml里面定义。
		this(context, attrs, android.R.attr.editTextStyle); // 使用系统自带的样式
	}

	/**
	 * 当在layout使用ClearEditText时会自动调用这个方法
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyleAttr
	 *            :样式
	 */
	public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		mClearDrawable = getCompoundDrawables()[2];
		if (mClearDrawable == null) {
			mClearDrawable = getResources().getDrawable(R.drawable.selector_ic_delete);
		}

		mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());

		// 默认设置隐藏图标
		setClearIconVisible(false);
		// 设置焦点改变的监听
		setOnFocusChangeListener(this);
		// 设置输入框里面的内容发生改变解析.
		addTextChangedListener(this);
	}

	/**
	 * 设置清除图标的显示与隐藏，调用setCompoundDrawable为EditText绘制上去
	 * 
	 * @param visible
	 */
	protected void setClearIconVisible(boolean visible) {
		Drawable right = visible ? mClearDrawable : null;
		setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
	}

	/**
	 * 当ClearEditText焦点发生变化的时候，判断里面字符串长度，设置清除图标的显示与隐藏。
	 * 
	 * @param v
	 * @param hasFocus
	 */
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		this.hasFoucs = hasFocus;
		if (hasFocus) {
			setClearIconVisible(getText().length() > 0);
		} else {
			setClearIconVisible(false);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		// 和onTextChanged()方法中的代码是一样的效果
		/*
		 * if(hasFoucs) { setClearIconVisible(s.toString().length() > 0) ; }
		 */
	}

	/**
	 * 当输入框里面内容发生变化的时候回调的方法
	 * 
	 * @param text
	 * @param start
	 * @param lengthBefore
	 * @param lengthAfter
	 */
	@Override
	public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
		if (hasFoucs) {
			setClearIconVisible(text.length() > 0);
		}
	}

	/**
	 * @param event
	 * @return
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (getCompoundDrawables()[2] != null) {
				// 是否点击在图标的坐标上
				boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight()) && (event.getX() < ((getWidth() - getPaddingRight())));
				if (touchable) {
					this.setText("");
				}
			}
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 设置晃动动画
	 */
	public void setShakeAnimation() {
		this.setAnimation(ShakeAnimation(5));
	}

	/**
	 * 晃动动画
	 * 
	 * @param counts
	 *            1秒钟晃动多少下
	 * @return
	 */
	public static Animation ShakeAnimation(int counts) {
		Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
		// setInterpolator：左右抖动, setInterpolator:上下幅度。
		translateAnimation.setInterpolator(new CycleInterpolator(counts));
		translateAnimation.setDuration(1000);
		return translateAnimation;
	}

}
