package com.hct.calendar.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

public class DashedLineView extends View {

    public DashedLineView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#66717171"));
        paint.setStrokeWidth(8);
        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(0, 600);
        PathEffect effects = new DashPathEffect(new float[] { 24, 12, 24, 12 },
                2);
        paint.setPathEffect(effects);
        canvas.drawPath(path, paint);
    }
}