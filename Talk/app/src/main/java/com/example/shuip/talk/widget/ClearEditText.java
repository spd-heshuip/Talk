package com.example.shuip.talk.widget;

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

import com.example.shuip.talk.R;

/**
 * Created by Administrator on 15-9-7.
 */
public class ClearEditText extends EditText implements View.OnFocusChangeListener,TextWatcher{

    private Drawable mClearDrawable;

    private boolean hasFoucs;
    public ClearEditText(Context context) {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
     * 当我们按下的位置 在  EditText的宽度 - 图标到控件右边的间距 - 图标的宽度  和
     * EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP){
            if (getCompoundDrawables()[2] != null){
                boolean touchable = (event.getX() > (getWidth() - getTotalPaddingRight())) &&
                        (event.getX() < ((getWidth() - getPaddingRight())));

                if (touchable){
                    this.setText("");
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private void init(){
        mClearDrawable = getCompoundDrawables()[2];
        if(mClearDrawable == null){
            mClearDrawable = getResources().getDrawable(R.drawable.selector_ic_delete);
        }
        //getIntrinsicWidth()取得的是Drawable在手机上的宽度，所以不同分辨率下获取到的值是不同的，关键所在处
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());

        setClearIconVisible(false);

        setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }

    private void setClearIconVisible(boolean visible){
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFoucs = hasFocus;
        if (hasFoucs){
            setClearIconVisible(getText().length() > 0);
        }else{
            setClearIconVisible(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (hasFoucs){
            setClearIconVisible(text.length() > 0);
        }
    }

    public void setShakeAnimation(){
        this.startAnimation(ShakeAnimation(5));
    }

    public static Animation ShakeAnimation(int count){
        TranslateAnimation translateAnimation = new TranslateAnimation(0,10,0,0);
        translateAnimation.setInterpolator(new CycleInterpolator(count));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }

}
