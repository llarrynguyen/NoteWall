package com.larrynguyen.notewall.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import com.larrynguyen.notewall.R;


public class DrawableCompatEditText extends AppCompatEditText
{
	public DrawableCompatEditText(Context context)
	{
		super(context);
	}

	public DrawableCompatEditText(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initAttrs(context, attrs);
	}

	public DrawableCompatEditText(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		initAttrs(context, attrs);
	}

	void initAttrs(Context context, AttributeSet attrs)
	{
		if (attrs != null)
		{
			TypedArray attributeArray = context.obtainStyledAttributes(
					attrs,
					R.styleable.DrawableCompatTextView);

			Drawable drawableStart = null;
			Drawable drawableEnd = null;
			Drawable drawableBottom = null;
			Drawable drawableTop = null;

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
			{
				drawableStart = attributeArray.getDrawable(R.styleable.DrawableCompatTextView_drawableStart);
				drawableEnd = attributeArray.getDrawable(R.styleable.DrawableCompatTextView_drawableEnd);
				drawableBottom = attributeArray.getDrawable(R.styleable.DrawableCompatTextView_drawableBottom);
				drawableTop = attributeArray.getDrawable(R.styleable.DrawableCompatTextView_drawableTop);
			}
			else
			{
				final int drawableStartId = attributeArray.getResourceId(R.styleable.DrawableCompatTextView_drawableStart, -1);
				final int drawableEndId = attributeArray.getResourceId(R.styleable.DrawableCompatTextView_drawableEnd, -1);
				final int drawableBottomId = attributeArray.getResourceId(R.styleable.DrawableCompatTextView_drawableBottom, -1);
				final int drawableTopId = attributeArray.getResourceId(R.styleable.DrawableCompatTextView_drawableTop, -1);

				if (drawableStartId != -1)
					drawableStart = AppCompatResources.getDrawable(context, drawableStartId);
				if (drawableEndId != -1)
					drawableEnd = AppCompatResources.getDrawable(context, drawableEndId);
				if (drawableBottomId != -1)
					drawableBottom = AppCompatResources.getDrawable(context, drawableBottomId);
				if (drawableTopId != -1)
					drawableTop = AppCompatResources.getDrawable(context, drawableTopId);
			}

			setCompoundDrawablesCompat(drawableStart, drawableTop, drawableEnd, drawableBottom);

			attributeArray.recycle();
		}
	}

	public void setDrawableStart(Drawable drawable)
	{
		Drawable[] drawables = getCompoundDrawables();
		setCompoundDrawablesCompat(drawable, drawables[1], drawables[2], drawables[3]);
	}

	public void setDrawableTop(Drawable drawable)
	{
		Drawable[] drawables = getCompoundDrawables();
		setCompoundDrawablesCompat(drawables[0], drawable, drawables[2], drawables[3]);
	}

	public void setDrawableEnd(Drawable drawable)
	{
		Drawable[] drawables = getCompoundDrawables();
		setCompoundDrawablesCompat(drawables[0], drawables[1], drawable, drawables[3]);
	}

	public void setDrawableBottom(Drawable drawable)
	{
		Drawable[] drawables = getCompoundDrawables();
		setCompoundDrawablesCompat(drawables[0], drawables[1], drawables[2], drawable);
	}

	public void setCompoundDrawablesCompat(@Nullable Drawable drawableStart, @Nullable Drawable drawableTop,
										   @Nullable Drawable drawableEnd, @Nullable Drawable drawableBottom)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
			setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, drawableTop, drawableEnd, drawableBottom);
		else setCompoundDrawablesWithIntrinsicBounds(drawableStart, drawableTop, drawableEnd, drawableBottom);
	}
}
