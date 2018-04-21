package View;

import android.view.View;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.liu.asus.xiaoview.R;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by 地地 on 2018/4/21.
 * 邮箱：461211527@qq.com.
 */

public class WuziqiView extends View {

    private static final String TAG = "WuZiQiPanel";
    //棋盘的宽度和高度，为了正方形
    private int mPanelWidth;
    //每一行的高度
    private float mLineHeight;
    //设置棋盘为10*10的网格
    private int MAX_LINE = 10;
    //五子棋，所以当达到5时说明有一方已经胜利
    private int MAX_COUNT_IN_LINE = 5;

    //画笔
    private Paint mPaint = new Paint();

    //白棋子和黑棋子的图片
    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;

    //棋子只占网格宽度的3/4
    private float ratioPieceOfLineHeight = 3 * 1.0f /4;

    //白旗先手，当前轮到白旗
    private boolean mIsWhite = true;
    //黑棋子集合和白棋子的集合
    private ArrayList<Point> mWhiteArray = new ArrayList<>();
    private ArrayList<Point> mBlackArray = new ArrayList<>();

    //是否结束
    private boolean mIsGameOver = false;
    //是否白旗为赢家
    private boolean mIsWhiteWinner = false;



    public WuziqiView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //初始化画笔，获取棋子的图片等
        init();
    }

    private void init() {
        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mWhitePiece = BitmapFactory.
                decodeResource(getResources(), R.drawable.stone_w2);
        mBlackPiece = BitmapFactory.
                decodeResource(getResources(),R.drawable.stone_b1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //因为要绘制正方形，所以取宽和高的最小值
        int width = Math.min(widthSize,heightSize);
        //heightMode
        if (widthMode == MeasureSpec.UNSPECIFIED){
            width = heightSize;
        }else if (heightMode == MeasureSpec.UNSPECIFIED){
            width = widthSize;
        }
        setMeasuredDimension(width,width);
    }


    //当宽高确定后赋值
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;
        //总高度除以行数为每一行的高度
        mLineHeight = mPanelWidth * 1.0f /MAX_LINE;
        //对旗子进行一个缩放，为每一个网格宽度的3/4
        int pieceWidth = (int) (mLineHeight * ratioPieceOfLineHeight);
        //对旗子的大小做限制
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece,pieceWidth,pieceWidth,false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece,pieceWidth,pieceWidth,false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //如果五子棋结束则不能再落字
        if (mIsGameOver){
            return false;
        }
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP){
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point p = getValidPoint(x,y);
            //判断是否这个地方已经落过子
            if (mWhiteArray.contains(p) || mBlackArray.contains(p)){
                return false;
            }
            //每次都把所下的棋子加进集合，绘制时检查是否结束
            if (mIsWhite){
                mWhiteArray.add(p);
            } else {
                mBlackArray.add(p);
            }
            //每次都重新绘制
            invalidate();
            //白棋子和黑棋子轮流绘制
            mIsWhite = !mIsWhite;
        }
        //表明处理了touch事件
        return true;
    }

    private Point getValidPoint(int x, int y) {
        //用计算后的int值更容易判断一个点的位置是否已经下过了，防止重复
        return new Point((int)(x / mLineHeight), (int)(y / mLineHeight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制棋盘
        drawBoard(canvas);
        //绘制棋子
        drawPieces(canvas);
        //检查是否结束
        checkGameOver();
    }

    private void checkGameOver() {
        //每次绘制都检测白棋子和黑棋子是否结束
        boolean whiteWin = checkFiveInLine(mWhiteArray);
        boolean blackWin = checkFiveInLine(mBlackArray);
        if (whiteWin || blackWin){
            mIsGameOver = true;
            mIsWhiteWinner = whiteWin;
            String text = mIsWhiteWinner ? "白旗胜利":"黑旗胜利";
            Toast.makeText(getContext(),text,Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkFiveInLine(List<Point> points) {
        //横向检测、纵向检测、左上方、左下方、右上方、右下方分别检测
        for (Point p : points){
            int x = p.x;
            int y = p.y;
            boolean win = checkHorizontal(x,y, points);
            if (win){
                return true;
            }
            win = checkVertical(x,y, points);
            if (win){
                return true;
            }
            win = checkLeftDiagonal(x,y, points);
            if (win){
                return true;
            }
            win = checkRightDiagonal(x,y, points);
            if (win){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断x，y位置的旗子，是否横向有相邻的五个一致。
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkHorizontal(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i <MAX_COUNT_IN_LINE ; i++) {
            if (points.contains(new Point(x-i,y))){
                count++;
            }else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE){
            return true;
        }
        for (int i = 1; i <MAX_COUNT_IN_LINE ; i++) {
            if (points.contains(new Point(x+i,y))){
                count++;
            }else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE){
            return true;
        }
        return  false;
    }
    private boolean checkVertical(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i <MAX_COUNT_IN_LINE ; i++) {
            if (points.contains(new Point(x,y-i))){
                count++;
            }else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE){
            return true;
        }
        for (int i = 1; i <MAX_COUNT_IN_LINE ; i++) {
            if (points.contains(new Point(x,y+i))){
                count++;
            }else {
                break;
            }
        }

        if (count == MAX_COUNT_IN_LINE){
            return true;
        }
        return  false;
    }
    private boolean checkLeftDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i <MAX_COUNT_IN_LINE ; i++) {
            if (points.contains(new Point(x-i,y+i))){
                count++;
            }else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE){
            return true;
        }
        for (int i = 1; i <MAX_COUNT_IN_LINE ; i++) {
            if (points.contains(new Point(x+i,y-i))){
                count++;
            }else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE){
            return true;
        }
        return  false;
    }
    private boolean checkRightDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i <MAX_COUNT_IN_LINE ; i++) {
            if (points.contains(new Point(x-i,y-i))){
                count++;
            }else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE){
            return true;
        }
        for (int i = 1; i <MAX_COUNT_IN_LINE ; i++) {
            if (points.contains(new Point(x+i,y+i))){
                count++;
            }else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE){
            return true;
        }
        return  false;
    }

    //以下两个完全是按照数学方法绘制，重点是每个线的x,y坐标，每个棋子的x，y坐标，大家看图绝对能看懂
    //不懂可以加我qq635912159
    private void drawPieces(Canvas canvas) {
        for (int i = 0 ,n = mWhiteArray.size(); i < n; i++) {
            Point whitePoint = mWhiteArray.get(i);
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x +(1-ratioPieceOfLineHeight)/2 )*mLineHeight,
                    (whitePoint.y +(1-ratioPieceOfLineHeight)/2 )*mLineHeight,null);
        }
        for (int i = 0 ,n = mBlackArray.size(); i < n; i++) {
            Point whitePoint = mBlackArray.get(i);
            canvas.drawBitmap(mBlackPiece,
                    (whitePoint.x +(1-ratioPieceOfLineHeight)/2 )*mLineHeight,
                    (whitePoint.y +(1-ratioPieceOfLineHeight)/2 )*mLineHeight,null);
        }
    }

    private void drawBoard(Canvas canvas) {
        int w = mPanelWidth;
        float lineHeight = mLineHeight;
        for (int i = 0; i < MAX_LINE ; i++) {
            int startX = (int) (lineHeight /2);
            int endX = (int) (w - lineHeight /2);
            int y = (int) ((0.5 +i) * lineHeight);
            canvas.drawLine(startX,y,endX,y,mPaint);
            canvas.drawLine(y,startX,y,endX,mPaint);
        }
    }
    //重来一局，清除数据
    public void  start(){
        mWhiteArray.clear();
        mBlackArray.clear();
        mIsGameOver = false;
        mIsWhiteWinner =false;
        invalidate();
    }

    //数据保存恢复机制，防止屏幕旋转等销毁当前数据，所以每次保存当前数据
    //重新创建时再绘制上次销毁时的局面
    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE,super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER,mIsGameOver);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY,mBlackArray);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY,mWhiteArray);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof  Bundle){
            Bundle bundle = (Bundle) state;
            mIsGameOver = bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteArray =bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackArray =bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
        }
    }
}
