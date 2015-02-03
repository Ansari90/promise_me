package aaa.promise_me.customWidgets;

import aaa.promise_me.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.Button;

class CustomButton extends Button {

	Bitmap bmap;
	Matrix mtx;
	Paint pnt;
	
	public CustomButton(Context context) {
		
		super(context);
	}
	
	public CustomButton(Context context, AttributeSet attrs) {
		
		super(context, attrs);
		mtx = new Matrix();
		pnt = new Paint();
		
		String buttonString = attrs.getAttributeValue(context.getString(R.string.nameSpace), context.getString(R.string.attr_buttonString));
		switch(Integer.parseInt(buttonString.substring(1))) {
		case R.string.yes:
			bmap = BitmapFactory.decodeResource(getResources(), R.drawable.yes);
			break;
		case R.string.no:
			bmap = BitmapFactory.decodeResource(getResources(), R.drawable.no);
			break;
		case R.string.next:
			bmap = BitmapFactory.decodeResource(getResources(), R.drawable.next);
			break;
		case R.string.previous:
			bmap = BitmapFactory.decodeResource(getResources(), R.drawable.previous);
			break;
		case R.string.deletePromise:
			bmap = BitmapFactory.decodeResource(getResources(), R.drawable.delete);
			break;
		case R.string.promiseMe:
			bmap = BitmapFactory.decodeResource(getResources(), R.drawable.promiseme);
			break;
		}
	}
	
	protected void onMeasure(int width, int height) {
		
		setMeasuredDimension(bmap.getWidth(), bmap.getHeight());
	}
	
	protected void onDraw(Canvas canvas) {
		
		canvas.drawBitmap(bmap, mtx, pnt);
	}
}