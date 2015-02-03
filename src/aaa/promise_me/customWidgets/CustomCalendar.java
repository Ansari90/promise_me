package aaa.promise_me.customWidgets;

import java.util.ArrayList;
import java.util.Calendar;

import aaa.promise_me.FragmentContainer.CC_Interface;
import aaa.promise_me.FragmentContainer.PromiseInfoFragment;
import aaa.promise_me.helpers.MonthAndDays;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class CustomCalendar extends View implements CC_Interface {
	
	private PromiseInfoFragment parentFragment;
	
	private static final int WIDTH = 750;
	private static final int HEIGHT = 500;
	
	private static final int HEADER_X = 225;
	private static final int HEADER_Y = 50;
	
	private static final int textSize = 45;
	private static final int DAY_GAP = 50;
	private static final int Y_OFFSET = 5;
	private static final int YORN_EDGE = 50;
	
	private static final int DAY_HEADER_X = 25;
	private static final int DAY_HEADER_Y = 100;
	
	private Paint textPaint;
	private Paint yesPaint;
	private Paint noPaint;
	
	private Rect yesRect;
	private Rect noRect;
	
	private int year;
	private int month;
	private ArrayList<Integer> yesDays;
	private ArrayList<Integer> noDays;
	private boolean dataSet;
	
	public CustomCalendar(Context context, AttributeSet attrs) {
		
		super(context, attrs);
		
		textPaint = new Paint();
		textPaint.setTextSize(textSize);
		textPaint.setColor(Color.BLACK);
		
		yesPaint = new Paint();
		yesPaint.setColor(Color.GREEN);
		yesRect = new Rect();
		
		noPaint = new Paint();
		noPaint.setColor(Color.RED);
		noRect = new Rect();
		
		month = -1;
		dataSet = false;
	}
	
	public void getData() {
		
		MonthAndDays current = parentFragment.getData();
		
		year = current.getYear();
		month = current.getMonth();
		yesDays = current.getYesDays();
		noDays = current.getNoDays();
		
		dataSet = true;
	}
	
	public void clearData() { dataSet = false; }
	
	public void onMeasure(int width, int height) {
		
		setMeasuredDimension(WIDTH, HEIGHT);
	}
	
	public void onDraw(Canvas canvas) {
		
		if(dataSet) {			
			int numOfDays = getNumOfDays();
			int currentX = DAY_HEADER_X, currentY = DAY_HEADER_Y, modifiedX = -1;
			String dayString = "";
			Calendar tempCalendar = Calendar.getInstance();
			
			canvas.drawText(getMonthString(month) + ", " + year , HEADER_X, HEADER_Y, textPaint);
			for(int i = 0; i < 7; i++) {
				dayString = getDayString(i);
				canvas.drawText(dayString, currentX, currentY, textPaint);
				
				currentX += DAY_GAP * 2;
			}
			
			currentX = DAY_HEADER_X;
			currentY += DAY_GAP;
			for(int i = 1; i <= numOfDays; i++) {
				tempCalendar.clear();
				tempCalendar.set(year, month, i);
				
				modifiedX = currentX + getDayOffset(tempCalendar.get(Calendar.DAY_OF_WEEK));
				for(int yesDay : yesDays) {
					if(yesDay == i) {
						yesRect.set(modifiedX, currentY - YORN_EDGE + Y_OFFSET, modifiedX + YORN_EDGE, currentY + Y_OFFSET);
						canvas.drawRect(yesRect, yesPaint);
					}
				}
				for(int noDay : noDays) {
					if(noDay == i) {
						noRect.set(modifiedX, currentY - YORN_EDGE + Y_OFFSET, modifiedX + YORN_EDGE, currentY + Y_OFFSET);
						canvas.drawRect(noRect, noPaint);
					}
				}
				canvas.drawText(String.valueOf(i), modifiedX, currentY, textPaint);
				
				if(tempCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
					currentY += DAY_GAP;
				}
			}
		}
	}

	public int getNumOfDays() {
		
		int numOfDays = 0;
		switch(month) {
		case Calendar.JANUARY:
		case Calendar.MARCH:
		case Calendar.MAY:
		case Calendar.JULY:
		case Calendar.AUGUST:
		case Calendar.OCTOBER:
		case Calendar.DECEMBER:
			numOfDays = 31;
			break;
		case Calendar.APRIL:
		case Calendar.JUNE:
		case Calendar.SEPTEMBER:
		case Calendar.NOVEMBER:
			numOfDays = 30;
			break;
		case Calendar.FEBRUARY:
			numOfDays = 28;
			
			if(year%4 == 0) {
				if(year%100 == 0) {
					if(year%400 == 0) {
						numOfDays = 29;
					}
				}
			}
			break;
		}
		
		return numOfDays;
	}
	
	public String getDayString(int index) {
		
		String dayString = "";
		switch(index) {
		case 0:
			dayString = "Mon";
			break;
		case 1:
			dayString = "Tue";
			break;
		case 2:
			dayString = "Wed";
			break;
		case 3:
			dayString = "Thu";
			break;
		case 4:
			dayString = "Fri";
			break;
		case 5:
			dayString = "Sat";
			break;
		case 6:
			dayString = "Sun";
			break;
		}
		
		return dayString;
	}
	
	public int getDayOffset(int calendarDay) {
		
		int dayOffset = -1;
		switch(calendarDay) {
		case Calendar.MONDAY:
			dayOffset = 0;
			break;
		case Calendar.TUESDAY:
			dayOffset = 1;
			break;
		case Calendar.WEDNESDAY:
			dayOffset = 2;
			break;
		case Calendar.THURSDAY:
			dayOffset = 3;
			break;
		case Calendar.FRIDAY:
			dayOffset = 4;
			break;
		case Calendar.SATURDAY:
			dayOffset = 5;
			break;
		case Calendar.SUNDAY:
			dayOffset = 6;
			break;
		}
		
		return dayOffset * DAY_GAP * 2;
	}
	
	public  String getMonthString(int monthNum) {
		
		String currentMonth = "Unknown Month";
		
		switch(monthNum) {
		case Calendar.JANUARY:
			currentMonth = "January";
			break;
		case Calendar.FEBRUARY:
			currentMonth = "February";
			break;
		case Calendar.MARCH:
			currentMonth = "March";
			break;
		case Calendar.APRIL:
			currentMonth = "April";
			break;
		case Calendar.MAY:
			currentMonth = "May";
			break;
		case Calendar.JUNE:
			currentMonth = "June";
			break;
		case Calendar.JULY:
			currentMonth = "July";
			break;
		case Calendar.AUGUST:
			currentMonth = "August";
			break;
		case Calendar.SEPTEMBER:
			currentMonth = "September";
			break;
		case Calendar.OCTOBER:
			currentMonth = "October";
			break;
		case Calendar.NOVEMBER:
			currentMonth = "November";
			break;
		case Calendar.DECEMBER:
			currentMonth = "December";
			break;
		case Calendar.UNDECIMBER:
			currentMonth = "13th Lunar Month";
			break;
		}
		
		return currentMonth;
	}
	
	public static String getDayName(int dayWeek) {
		
		String currentDay = "Unknown Day";
		
		switch(dayWeek) {
		case Calendar.MONDAY:
			currentDay = "Monday";
			break;
		case Calendar.TUESDAY:
			currentDay = "Tuesday";
			break;
		case Calendar.WEDNESDAY:
			currentDay = "Wednesday";
			break;
		case Calendar.THURSDAY:
			currentDay = "Thursday";
			break;
		case Calendar.FRIDAY:
			currentDay = "Friday";
			break;
		case Calendar.SATURDAY:
			currentDay = "Saturday";
			break;
		case Calendar.SUNDAY:
			currentDay = "Sunday";
			break;
		}
		
		return currentDay;
	}

	@Override
	public void sendReference(PromiseInfoFragment parentFragment) {
		
		this.parentFragment = parentFragment;
	}
}
