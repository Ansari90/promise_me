package aaa.promise_me.helpers;

import java.util.ArrayList;

public class MonthAndDays {
	
	private String PromiseText;
	private int Month;
	private int Year;
	private ArrayList<Integer> YesDays = null;
	private ArrayList<Integer> NoDays = null;
	
	public MonthAndDays(String promiseText) {
		
		PromiseText = promiseText;
	}

	public MonthAndDays(int month, int year) {
		
		PromiseText = null;
		Month = month;
		Year = year;
	}
	
	public String getProimseText() { return PromiseText; }
	
	public int getMonth() { return Month; }
	
	public int getYear() { return Year; }
	
	public String getIdCombo() { return Month + "-" + Year; }
	
	private void initialize() {
		
		if(YesDays == null) {
			YesDays = new ArrayList<Integer>();
		}
		if(NoDays == null) {
			NoDays = new ArrayList<Integer>();
		}
	}
	
	public void addYesDay(int dayNum) { 
		
		initialize();
		YesDays.add(dayNum); 
	}
	
	public ArrayList<Integer> getYesDays() { return YesDays; }
	
	public void addNoDay(int dayNum) {
		
		initialize();
		NoDays.add(dayNum); 
	}
	
	public ArrayList<Integer> getNoDays() { return NoDays; }
}
