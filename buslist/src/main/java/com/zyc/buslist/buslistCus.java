package com.zyc.buslist;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class buslistCus extends View {

    public static final String TAG = "buslist";
    /**
     * text之间间距和minTextSize之比
     */
    public static final float MARGIN_ALPHA = 4.0f;
    /**
     * 自动回滚到中间的速度
     */
    public static final float SPEED = 5;

    private List<String> mDataList;
    /**
     * 选中的位置，这个位置是mDataList的中心位置
     */
    private int mCurrentSelected;

    private float temp;
    private Paint mPaint;

    private float mTextSize = 80;
    private float mMinTextSize = 40;
//    private float mTextSpacing = 20;

    private float mMaxTextAlpha = 255;
    private float mMinTextAlpha = 120;

    private int mColorText = 0xffa0a0a0;
    private int mSelectedColorText = 0xffa0a0a0;
    private int mSelectedBackgroundColor = 0xffB27F46;

    private int maxshow;

    private String textStart = "";
    private String textEnd = "";
    private int mViewHeight;
    private int mViewWidth;

    private float mLastDownX;
    /**
     * 滑动的距离
     */
    private float mMoveLen = 0;
    private boolean isInit = false;
    private onSelectListener mSelectListener;
    private Timer timer;


    public buslistCus(Context context) {
        this(context, null);
    }

    public buslistCus(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public buslistCus(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.PickerView);

        //获取自定义属性和默认值
        mColorText = mTypedArray.getColor(R.styleable.PickerView_textColor, 0xffa0a0a0);
        mSelectedColorText = mTypedArray.getColor(R.styleable.PickerView_SelectedtextColor, 0xffa0a0a0);
        mSelectedBackgroundColor = mTypedArray.getColor(R.styleable.PickerView_SelectedBackgroundColor, 0xffB27F46);
        mTextSize = mTypedArray.getDimension(R.styleable.PickerView_maxtextSize, 40);
        mMinTextSize = mTypedArray.getDimension(R.styleable.PickerView_mintextSize, 20);
//        mTextSpacing = mTypedArray.getDimension(R.styleable.PickerView_TextSpacing, 10);
        maxshow = mTypedArray.getInteger(R.styleable.PickerView_maxshow, 100);
        textStart = mTypedArray.getString(R.styleable.PickerView_textStart);
        if (textStart == null) textStart = "";
        textEnd = mTypedArray.getString(R.styleable.PickerView_textEnd);
        if (textEnd == null) textEnd = "";
//        textIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, true);
//        style = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);
//        setProgress(mTypedArray.getInt(R.styleable.RoundProgressBar_progress, 0));
        mTypedArray.recycle();
        init();
    }

    public void setOnSelectListener(onSelectListener listener) {
        mSelectListener = listener;
    }

    private void performSelect() {
        if (mSelectListener != null) {
            mSelectListener.onSelect(mDataList.get(mCurrentSelected), mCurrentSelected, textStart, textEnd);
        }
    }

    private void performItemClick(int y) {
        if (mSelectListener != null) {
            mSelectListener.onItemClick(mCurrentSelected, mDataList.get(mCurrentSelected), textStart, textEnd, y);
        }
    }


    private void moveHeadToTail() {
        if (mCurrentSelected < mDataList.size() - 1) mCurrentSelected++;

    }

    private void moveTailToHead() {
        if (mCurrentSelected > 0) mCurrentSelected--;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewHeight = getMeasuredHeight();
        mViewWidth = getMeasuredWidth();
        // 按照View的高度计算字体大小
//        mTextSize = mViewHeight / 4.0f;
//        mMinTextSize = mTextSize / 2f;
        isInit = true;
        invalidate();
    }

    private void init() {
        timer = new Timer();
        mDataList = new ArrayList<String>();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Style.FILL);
        mPaint.setTextAlign(Align.CENTER);
        mPaint.setColor(mColorText);
        mDataList.add("高新六路光谷一路");
        mDataList.add("高新六路流芳大道");
        mDataList.add("高新六路汪田村");
        mDataList.add("高新六路康一路");
        mDataList.add("高新六路佛祖岭一路");
        mDataList.add("高新六路光谷三路");
        mDataList.add("光谷三路高新四路");
        mDataList.add("光谷三路大吕村");
        mDataList.add("高新二路大吕路");
        mDataList.add("高新二路光谷四路");
        mDataList.add("高新二路驿山南路");
        mDataList.add("高新二路光谷六路");
        mDataList.add("高新二路高科园路");
        mDataList.add("高新二路高科园二路");
        mDataList.add("高新二路光谷七路");
        mDataList.add("高新二路生物园路");
        mDataList.add("光谷八路蔡吴村");
        mDataList.add("桥北路教师小区");
        mDataList.add("桥北路三眼桥");
        mDataList.add("豹澥公交停车场");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 根据index绘制view
        if (isInit)
            drawData(canvas);
    }

    private int mLineBackgroundColor = 0xfff2f2f2;
    private int mLineSelectedColor = 0xffffe7a1;
    private int mLineDotBackgroundColor = 0xffd6d6d6;
    private int mLineDotSelectedColor = 0xffffc573;
    private float mLineWidth = 15;
    private float mLineTop = 100; //注意线上方还要显示当前站公交数量
    private float mLineLeft = 100;
    private float mLineDotSpace = 180;
    private float mLineDotWidth = 23;
    private float mLineNumSize = 30;
    private float mLineTextSize = 40;
    private int mLineTextBackgroundColor = 0xffffffff;
    private int mLineTextSelectedColor = 0xffffffff;

    private void drawData(Canvas canvas) {

        mCurrentSelected = 2;

        if (mCurrentSelected >= mDataList.size()) mCurrentSelected = 0;
        float lineLength = mLineDotSpace * (mDataList.size() - 1);

        //region 画背景
        //region 背景线
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mLineBackgroundColor);
        mPaint.setStrokeWidth(mLineWidth);
        canvas.drawLine(mLineLeft - mMoveLen, mLineTop, lineLength + mLineLeft - mMoveLen, mLineTop, mPaint);
        //endregion

        //region 背景点
        mPaint.setColor(mLineDotBackgroundColor);
        for (int i = 0; i < mDataList.size(); i++) {
            canvas.drawCircle(mLineLeft - mMoveLen + i * mLineDotSpace, mLineTop, mLineDotWidth, mPaint);// 小圆
        }
        //endregion
        //endregion

        //region 画前景
        //region 线
        mPaint.setColor(mLineSelectedColor);
        mPaint.setStrokeWidth(mLineWidth);
        canvas.drawLine(mLineLeft - mMoveLen, mLineTop, mLineDotSpace * mCurrentSelected + mLineLeft - mMoveLen, mLineTop, mPaint);
        //endregion
        //region 点
        mPaint.setColor(mLineDotSelectedColor);
        for (int i = 0; i < mCurrentSelected; i++) {
            canvas.drawCircle(mLineLeft - mMoveLen + i * mLineDotSpace, mLineTop, mLineDotWidth, mPaint);// 小圆
        }
        //endregion

        //region 当前选择点
        if (mCurrentSelected >= 0) {
            canvas.drawCircle(mLineLeft - mMoveLen + mCurrentSelected * mLineDotSpace, mLineTop, mLineDotWidth + 4, mPaint);// 小圆
            mPaint.setColor(0xffffffff);
            canvas.drawCircle(mLineLeft - mMoveLen + mCurrentSelected * mLineDotSpace, mLineTop, 10, mPaint);// 小圆
        }
        //endregion

        //endregion
        //region 画点上的数字
        mPaint.setTextSize(mLineNumSize);
        mPaint.setColor(mLineTextSelectedColor);
        float baseline = getBaseline(mLineTop);
        for (int i = 0; i < mCurrentSelected; i++) {
            canvas.drawText(String.valueOf(mCurrentSelected - i), mLineLeft - mMoveLen + i * mLineDotSpace, baseline, mPaint);
        }
        mPaint.setColor(mLineTextBackgroundColor);
        for (int i = mCurrentSelected + 1; i < mDataList.size(); i++) {
            canvas.drawText(String.valueOf(i - mCurrentSelected), mLineLeft - mMoveLen + i * mLineDotSpace, baseline, mPaint);
        }
        //endregion

        //region 站名
        mPaint.setColor(0xff000000);//选中站点前的站点字体颜色
        for (int i = 0; i < mDataList.size(); i++) {
            if (i == mCurrentSelected) mPaint.setColor(0xfffe871f); //选中站点字体颜色

            String str = mDataList.get(i);

            //当高度不够时减小字体大小以保证显示完全的文字
            float temp=mLineTextSize+1; //后面首先执行--
            float h;
            do{
                temp--;
                mPaint.setTextSize(temp);
                h = getTextHeight();
            }while(mViewHeight- mLineTop - mLineDotWidth < str.length() * h );

            for (int j = 0; j < str.length(); j++)
                canvas.drawText(String.valueOf(str.charAt(j)), mLineLeft - mMoveLen + i * mLineDotSpace, mLineTop + mLineDotWidth + j * h + h, mPaint);

            if (i == mCurrentSelected) mPaint.setColor(0xff999999); //选中站点后的站点字体颜色
        }


//        for (int i = 0; i < mCurrentSelected; i++) {
//            String str = mDataList.get(i);
//            for (int j = 0; j < str.length(); j++)
//                canvas.drawText(String.valueOf(str.charAt(j)), mLineLeft - mMoveLen + i * mLineDotSpace, mLineTop + mLineDotWidth + j * h + h, mPaint);
//        }
//
//        mPaint.setColor(0xfffe871f);
//        for (int j = 0; j < mDataList.get(mCurrentSelected).length(); j++)
//            canvas.drawText(String.valueOf(mDataList.get(mCurrentSelected).charAt(j)), mLineLeft - mMoveLen + mCurrentSelected * mLineDotSpace, mLineTop + mLineDotWidth + j * h + h, mPaint);
//
//
//        mPaint.setColor(0xff999999);
//        for (int i = mCurrentSelected+1; i < mDataList.size(); i++) {
//            String str = mDataList.get(i);
//            for (int j = 0; j < str.length(); j++)
//                canvas.drawText(String.valueOf(str.charAt(j)), mLineLeft - mMoveLen + i * mLineDotSpace, mLineTop + mLineDotWidth + j * h + h, mPaint);
//        }
        //endregion

    }

    /**
     * 抛物线
     *
     * @param zero 零点坐标
     * @param x    偏移量
     * @return scale
     */
    private float parabola(float zero, float x) {
        float f = (float) (1 - Math.pow(x / zero, 2));
        return f < 0 ? 0 : f;
    }

    //根据y坐标获取字体的baseline
    private float getBaseline(float y) {
        if (mPaint == null) return -1.0f;
        // text居中绘制，注意baseline的计算才能达到居中，y值是text中心坐标
        FontMetricsInt fmi = mPaint.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
        return baseline;
    }

    //根据y坐标获取字体的baseline
    private float getTextHeight() {
        if (mPaint == null) return -1.0f;
        // text居中绘制，注意baseline的计算才能达到居中，y值是text中心坐标
        FontMetricsInt fmi = mPaint.getFontMetricsInt();
        float baseline = (float) ((fmi.bottom - fmi.top));
        return baseline;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                doDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                doMove(event);
                break;
            case MotionEvent.ACTION_UP:
                doUp(event);
                break;
        }
        return true;
    }

    //region 触摸事件
    private void doDown(MotionEvent event) {
        mLastDownX = event.getX();
    }

    private void doMove(MotionEvent event) {

        mMoveLen += (mLastDownX - event.getX());


        if (mMoveLen > mLineLeft * 2 + mLineDotSpace * (mDataList.size() - 1) - mViewWidth) {
            mMoveLen = mLineLeft * 2 + mLineDotSpace * (mDataList.size() - 1) - mViewWidth;
        } else if (mMoveLen < 0) mMoveLen = 0;

//        if (mMoveLen > MARGIN_ALPHA * mMinTextSize / 2) {
//            // 往下滑超过离开距离
//            if (mCurrentSelected == 0)//到顶滑动距离不能超过离开距离
//            {
//                moveHeadToTail();
//                mMoveLen = MARGIN_ALPHA * mMinTextSize / 2;
//            } else {
//                moveTailToHead();
//                mMoveLen = mMoveLen - MARGIN_ALPHA * mMinTextSize;
//            }
//        } else if (mMoveLen < -MARGIN_ALPHA * mMinTextSize / 2) {
//            // 往上滑超过离开距离
//
//            if (mCurrentSelected == mDataList.size() - 1) {
//                moveTailToHead();
//                mCurrentSelected = mDataList.size() - 1;
//                mMoveLen = -MARGIN_ALPHA * mMinTextSize / 2;
//            } else {
//                moveHeadToTail();
//                mMoveLen = mMoveLen + MARGIN_ALPHA * mMinTextSize;
//
//            }
//        }

        mLastDownX = event.getX();
        invalidate();
    }

    private void doUp(MotionEvent event) {
//
        // 抬起手后mCurrentSelected的位置由当前位置move到中间选中位置
        if (Math.abs(mMoveLen) < 1) {
            mMoveLen = 0;
            performItemClick(0);
            return;
        }

    }
    //endregion


    public interface onSelectListener {
        void onSelect(String text, int position, String start, String end);

        void onItemClick(int x, String text, String start, String end, int y);
    }

}

