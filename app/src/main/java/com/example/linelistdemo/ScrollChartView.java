package com.example.linelistdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import com.example.linelistdemo.util.TextLayoutUtil;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * describe：
 */
public class ScrollChartView extends View {

    private int mBeginRange = 0;

    private int mEndRange;

    private Paint mSelectBgPaint, mSelectBigbgPaint;

    private TextPaint textPaint;

    /**
     * 画笔 指示标
     */
    private Paint mIndicatePaint;

    /**
     * 指示标 左右间隔
     */
    private int mIndicatePadding, mSelectedheight;

    /**
     * 指示器 间隔底部的距离
     */
    private int mIndicateBottomPadding;

    /**
     * 画笔 文字
     */
    private Paint mTextPaint;

    /**
     * 文字 默认颜色
     */
    private int mTextColor;

    /**
     * 文字 默认大小
     */
    private float mTextSize;

    /**
     * 文字 选中大小
     */
    private float mTextSelectedSize, mTextUnSelectedSize;

    /**
     * 文字 间隔底部的距离
     */
    private int mTextBottomPadding;

    /**
     * 画笔 线
     */
    private Paint mLinePaint;
    /**
     * 画底部的笔线
     */
    private Paint mLineBottomPaint;

    /**
     * 线 结束颜色
     */
    private int mLineEndColor;


    /**
     * 线 宽度
     */
    private int mLineWidth;

    /**
     * 阴影 画笔
     */
    private Paint mShadowPaint;

    /**
     * 阴影 渐变开始颜色
     */
    private int mShadowStartColor;

    /**
     * 阴影 渐变结束颜色
     */
    private int mShadowEndColor;

    /**
     * 底部横线颜色
     */
    private int mBottomEndColor;

    /**
     * 网格线 画笔
     */
    private Paint mGridPaint;

    /**
     * 网格线 颜色
     */
    private int mGirdColor;
    /**
     * 选中后的背景色
     */
    private int mSelectedColor, mSelectedBigColor;

    /**
     * 网格线 宽度
     */
    private int mGridWith;

    /**
     * 底部文字和指示标的高度
     */
    private int mShadowMarginHeight;
    public int mSelectedposition = -1, nowposition = 0, insideposition, mysuggestTime;

    private int mGravity = Gravity.TOP;
    private Rect mIndicateLoc;
    private Bitmap selectCompele, unselectCompele;

    /**
     * X轴坐标值
     */
    private CopyOnWriteArrayList<String> timeList;
    /**
     * Y轴坐标值
     */
    private CopyOnWriteArrayList<Double> dataList;
    /**
     * 坐标集合
     */
    private CopyOnWriteArrayList<Point> mList = new CopyOnWriteArrayList<>();
    /**
     * Y轴坐标最大的数据
     */
    private double maxData;

    public ScrollChartView(Context context) {
        this(context, null);
    }

    public ScrollChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //初始化一些参数
        mIndicatePadding = TextLayoutUtil.dp2px(context, 32);
        mSelectedheight = TextLayoutUtil.dp2px(context, 42);
        mLineWidth = TextLayoutUtil.dp2px(context, 2);
        mTextSize = TextLayoutUtil.dp2px(context, 9);
        mTextSelectedSize = TextLayoutUtil.dp2px(context, 13);
        mTextUnSelectedSize = TextLayoutUtil.dp2px(context, 9);
        mTextBottomPadding = TextLayoutUtil.dp2px(getContext(), 1);
        mGridWith = TextLayoutUtil.dp2px(context, 1);
        mShadowMarginHeight = TextLayoutUtil.dp2px(getContext(), 30);
        mIndicateBottomPadding = TextLayoutUtil.dp2px(getContext(), 15);

        mTextColor = ContextCompat.getColor(context, R.color.colorText);
        mShadowStartColor = ContextCompat.getColor(getContext(), R.color.colorShadowStart);
        mShadowEndColor = ContextCompat.getColor(getContext(), R.color.colorShadowEnd);
        mBottomEndColor = ContextCompat.getColor(getContext(), R.color.colorBottomLine);
        mGirdColor = ContextCompat.getColor(context, R.color.colorGrid);
        mSelectedColor = ContextCompat.getColor(context, R.color.colorBgSelect);
        mSelectedBigColor = ContextCompat.getColor(context, R.color.color_F3FDFB);
        mLineEndColor = ContextCompat.getColor(context, R.color.colorLineEnd);

        selectCompele = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_select_txt);
        unselectCompele = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_unselectd_txt);

        initValue();
    }

    //startOrend   0是开始，1是中间  2是结束
    public void setData(CopyOnWriteArrayList<String> times, CopyOnWriteArrayList<Double> dataList, int startOrend, double starty,
                        double endy, int nowposition, int mymaxTime, int suggestTime) {
        this.nowposition = nowposition;
        this.dataList = dataList;
        //如果数据里面有负数 则需要将每个数据都减去最大的负数 以防止负数出现
        this.timeList = times;
        mysuggestTime = (suggestTime == 0 ? 35 : suggestTime);
        maxData = mymaxTime == 0 ? 70 : mymaxTime;
        mEndRange = times.size() - 1;

        initValue();
        getPointList(startOrend, starty, endy);
        setLayoutParams(new LinearLayout.LayoutParams(getIndicateWidth() * (null != timeList ? timeList.size() : 0), getMeasuredHeight()));
        invalidate();
    }

    private void initValue() {

        mIndicatePaint = new Paint();
        mIndicatePaint.setStyle(Paint.Style.FILL);
        mIndicatePaint.setAntiAlias(true);

        //画线
        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mLinePaint.setStrokeWidth(mLineWidth);
        mLinePaint.setColor(mLineEndColor);

        mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setAntiAlias(true);

        //阴影
        mShadowPaint = new Paint();
        mShadowPaint.setStyle(Paint.Style.FILL);
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setStrokeCap(Paint.Cap.ROUND);
        Shader shader = new LinearGradient(getWidth() / 2, getHeight(), getWidth() / 2, 0, mShadowEndColor, mShadowStartColor, Shader.TileMode.MIRROR);
        mShadowPaint.setShader(shader);

        mGridPaint = new Paint();
        mGridPaint.setStyle(Paint.Style.FILL);
        mGridPaint.setAntiAlias(true);
        mGridPaint.setColor(mGirdColor);
        mGridPaint.setStrokeWidth(mGridWith);
        //底部直线
        mLineBottomPaint = new Paint();
        mLineBottomPaint.setStyle(Paint.Style.STROKE);
        mLineBottomPaint.setAntiAlias(true);
        mLineBottomPaint.setColor(mBottomEndColor);
        mLineBottomPaint.setStrokeWidth(mLineWidth);

        mSelectBgPaint = new Paint();
        mSelectBgPaint.setStyle(Paint.Style.FILL);
        mSelectBgPaint.setAntiAlias(true);
        mSelectBgPaint.setColor(mSelectedColor);

        mSelectBigbgPaint = new Paint();
        mSelectBigbgPaint.setStyle(Paint.Style.FILL);
        mSelectBigbgPaint.setAntiAlias(true);
        mSelectBigbgPaint.setColor(mSelectedBigColor);

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(mTextUnSelectedSize);
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);

        mIndicateLoc = new Rect();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (timeList == null) {
            return;
        }
        int count = canvas.save();
        //画网格
        drawGridLine(canvas);
        //阴影
        drawShadow(canvas);
        drawbootomline(canvas);
        //画线
        drawScrollLine(canvas);
        for (int value = mBeginRange, position = 0; value <= mEndRange; value++, position++) {
            drawText(canvas, position, timeList.get(value));
        }
        canvas.restoreToCount(count);
    }

    /**
     * 绘制网格线
     *
     * @param canvas
     */
    private void drawGridLine(Canvas canvas) {

        for (int i = 0; i < mList.size() - 1; i++) {
            computeIndicateLoc(mIndicateLoc, i);

            int left = mIndicateLoc.left;
            int right = mIndicateLoc.left + getIndicateWidth();
            mGridPaint.setColor(mGirdColor);
            canvas.drawRect(right - mGridWith, 0, right, mIndicateLoc.bottom, mGridPaint);

            if (nowposition == mSelectedposition && i == insideposition) {
                canvas.drawRect(left, 0, right - mGridWith, mIndicateLoc.bottom, mSelectBigbgPaint);
                RectF rectFdata = new RectF(left, 0, right - mGridWith, mSelectedheight - 6);
                canvas.drawRoundRect(rectFdata, 20, 20, mSelectBgPaint);
                RectF rectpaint = new RectF(left, mSelectedheight / 2, right - mGridWith, mSelectedheight - 6);
                canvas.drawRect(rectpaint, mSelectBgPaint);
                String zhanshidata = "";
                String showDate = timeList.get(timeList.size() > insideposition ? insideposition : 0);
                if (!TextUtils.isEmpty(showDate) && showDate.length() > 3) {
                    int showyue = Integer.valueOf(showDate.substring(0, 2));
                    zhanshidata = showyue + "月" + showDate.substring(3, showDate.length()) + "日";
                }

                canvas.drawText(zhanshidata, left + mIndicatePadding, getIndicateWidth()/10*3 - 4, textPaint);
                String timeminite = TextLayoutUtil.canceltoPoint(dataList.get(dataList.size() > insideposition ? insideposition : 0) + "");
                float timeminiteWidth = textPaint.measureText(timeminite) + timeminite.length() * 4;
                float miniteWidth = textPaint.measureText("分钟");
                int leftshuju = (int) ((mIndicatePadding * 2 - 10 - timeminiteWidth - miniteWidth) / 2);
                canvas.drawText("分钟", left + (leftshuju + timeminiteWidth  + miniteWidth/2+10), getIndicateWidth()/10*5, textPaint);
                textPaint.setTextSize(mTextSelectedSize);
                canvas.drawText(timeminite, left + leftshuju + timeminiteWidth/2 , getIndicateWidth()/10*5+3, textPaint);
                textPaint.setTextSize(mTextUnSelectedSize);
            }
        }
    }

    public void setmSelectedposition(int selectposition) {
        this.mSelectedposition = selectposition;
        invalidateView();
    }

    /**
     * 绘制文字
     */
    private void drawText(Canvas canvas, int position, String text) {
        computeIndicateLoc(mIndicateLoc, position);

        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        int x = (mIndicateLoc.left + mIndicateLoc.right) / 2;
        int y = mIndicateLoc.bottom + mIndicateBottomPadding - mTextBottomPadding;

        if (!isAlignTop()) {
            y = mIndicateLoc.top;
            mTextPaint.getTextBounds(text, 0, text.length(), mIndicateLoc);
            //增加一些偏移
            y += mIndicateLoc.top / 2;
        }

        canvas.drawText(text, x, y, mTextPaint);
    }


    //画底部的横线处理
    private void drawbootomline(Canvas canvas) {
        Path path = new Path();
        path.moveTo(mList.get(0).x, mIndicateLoc.bottom);
        path.lineTo(mList.get(mList.size() - 1).x, mIndicateLoc.bottom);
        canvas.drawPath(path, mLineBottomPaint);
    }

    /**
     * 绘制曲线图
     */
    private void drawScrollLine(Canvas canvas) {
        Point pStart;
        Point pEnd;
        Path path = new Path();

        for (int i = 0; i < mList.size() - 1; i++) {
            pStart = mList.get(i);
            pEnd = mList.get(i + 1);
            Point point3 = new Point();
            Point point4 = new Point();
            float wd = (pStart.x + pEnd.x) / 2;
            point3.x = wd;
            point3.y = pStart.y;
            point4.x = wd;
            point4.y = pEnd.y;

            path.moveTo(pStart.x, pStart.y);
            path.lineTo(pEnd.x, pEnd.y);
            //画曲线
//            path.cubicTo(point3.x, point3.y, point4.x, point4.y, pEnd.x, pEnd.y);
            canvas.drawPath(path, mLinePaint);

        }

        Point pEnddata;
        for (int i = 0; i < mList.size() - 1; i++) {
            pEnddata = mList.get(i + 1);
            if (nowposition == mSelectedposition && i == insideposition) {
                canvas.drawBitmap(mysuggestTime > dataList.get(dataList.size() > insideposition ? insideposition : 0) ? unselectCompele : selectCompele, pEnddata.x - selectCompele.getWidth() / 2, pEnddata.y - selectCompele.getHeight() / 2, mLinePaint);
            }
        }

    }

    /**
     * 绘制阴影
     */
    private void drawShadow(Canvas canvas) {
        Point pStart;
        Point pEnd;
        Path path = new Path();
        for (int i = 0; i < mList.size() - 1; i++) {
            pStart = mList.get(i);
            pEnd = mList.get(i + 1);
            Point point3 = new Point();
            Point point4 = new Point();
            float wd = (pStart.x + pEnd.x) / 2;
            point3.x = wd;
            point3.y = pStart.y;
            point4.x = wd;
            point4.y = pEnd.y;
            path.moveTo(pStart.x, pStart.y);
            path.lineTo(pEnd.x, pEnd.y);
//            path.cubicTo(point3.x, point3.y, point4.x, point4.y, pEnd.x, pEnd.y);
            //减去文字和指示标的高度
            path.lineTo(pEnd.x, mIndicateLoc.bottom);
            path.lineTo(pStart.x, mIndicateLoc.bottom);
        }
        path.close();
        canvas.drawPath(path, mShadowPaint);
    }

    /**
     * 获取每个数据源的坐标
     */
    private void getPointList(int startOrend, double starty, double endy) {
        mList.clear();
        for (int i = 0; i < dataList.size(); i++) {
            computeIndicateLoc(mIndicateLoc, i);
            int left = mIndicateLoc.left + mIndicatePadding;
            float top = getPositionY(dataList.get(i));

            Point point = new Point();
            point.x = left;
            point.y = top;
            if (i == 0) {
                Point pointone = new Point();
                pointone.x = mIndicateLoc.left;
                switch (startOrend) {
                    case 0:
                        pointone.y = top;
                        break;
                    case 1:
                    case 2:
                        pointone.y = (top + getPositionY(starty)) / 2;
                        break;
                }
                mList.add(pointone);
            }
            mList.add(point);
            if (i == dataList.size() - 1) {
                Point pointend = new Point();
                pointend.x = mIndicateLoc.left + mIndicatePadding * 2;
                switch (startOrend) {
                    case 2:
                        pointend.y = top;
                        break;
                    case 0:
                    case 1:
                        pointend.y = (top + getPositionY(endy)) / 2;
                        break;
                }
                mList.add(pointend);
            }
        }
    }

    private float getPositionY(double datashow) {
        int height = getHeight() - mShadowMarginHeight - mLineWidth + TextLayoutUtil.dp2px(getContext(), 17) - mSelectedheight;
        int top;
        if (datashow > mysuggestTime) {
            top = (int) (height / 2 - (height / 2) * (datashow - mysuggestTime) / (maxData - mysuggestTime)) + mSelectedheight;
        } else {
            top = (int) (height - (height / 2) * datashow / mysuggestTime) + mSelectedheight;
        }
        return top;
    }

    /**
     * 计算indicate的位置
     *
     * @param outRect
     * @param position
     */
    private void computeIndicateLoc(Rect outRect, int position) {
        if (outRect == null) {
            return;
        }
        int height = getHeight();
        int indicate = getIndicateWidth();

        int left = (indicate * position);
        int right = left + indicate;
        int top = getPaddingTop();
        int bottom = height - getPaddingBottom();

        if (isAlignTop()) {
            bottom -= mIndicateBottomPadding;
        } else {
            top += mIndicateBottomPadding;
        }

        outRect.set(left, top, right, bottom);
    }

    private int mLastMotionX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = (int) event.getX();
                break;
            case MotionEvent.ACTION_UP:
                //小范围滑动是点击处理，防止和滑动冲突
                if (Math.abs(mLastMotionX - event.getX()) < 6) {
                    int widthwidth = (int) (event.getX());
                    insideposition = widthwidth / getIndicateWidth();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private int getIndicateWidth() {
        return mIndicatePadding * 2;
    }

    private boolean isAlignTop() {
        return (mGravity & Gravity.TOP) == Gravity.TOP;
    }

    public void invalidateView() {
        if (Build.VERSION.SDK_INT >= 16) {
            postInvalidateOnAnimation();
        } else {
            invalidate();
        }
    }

    public class Point {

        public float x;
        public float y;
        public Point() {
        }
    }

}
