package aaa.promise_me;

import java.util.ArrayList;
import aaa.promise_me.customWidgets.CustomCalendar;
import aaa.promise_me.helpers.MonthAndDays;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentContainer {
	
	static class PromiseMeFragment extends Fragment {
		
		MainActivity parentActivity;
		
		public PromiseMeFragment() {
		}
		
		@Override
		public void onAttach(Activity activity) {
			
			super.onAttach(activity);
			parentActivity = (MainActivity)activity;
		}
	}
	
	public static class QuestionFragment extends PromiseMeFragment {

		public QuestionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			
			return rootView;
		}
		
		@Override
		public void onResume() {
			
			super.onResume();
			refresh();
		}
		
		public void refresh() {
			
			String response = (String)parentActivity.getInfo(FragmentID.Question);
			if(response == null) {
				response = parentActivity.getString(R.string.noPromises);
			}
			
			((TextView) getView().findViewById(R.id.promiseQuestion)).setText(response);
		}
	}
	
	public static class NewPromiseFragment extends PromiseMeFragment {
		
		public NewPromiseFragment() {
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			
			View rootView = inflater.inflate(R.layout.fragment_new_promise, container, false);
			return rootView;
		}
		
		@Override
		public void onResume() {
			
			super.onResume();
			((EditText) getView().findViewById(R.id.promiseText)).setText("");
		}
		
		public void createPromise() {
			
			parentActivity.sendInfo(FragmentID.NewPromise, ((EditText) getView().findViewById(R.id.promiseText)).getText().toString());
		}
	}
	
	public static class PromiseListFragment extends PromiseMeFragment {
		
		public PromiseListFragment() {
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			
			View rootView = inflater.inflate(R.layout.fragment_promise_list, container, false);
			((ListView)rootView.findViewById(R.id.promiseList)).setOnItemClickListener(new PromiseListItemClickListener());
			return rootView;
		}
		
		@Override
		public void onResume() {
			
			super.onResume();
			
			Cursor allPromises = (Cursor)parentActivity.getInfo(FragmentID.PromiseList);
			ListView promiseList = (ListView) getActivity().findViewById(R.id.promiseList);
			promiseList.setAdapter(new PromiseListAdapter(promiseList, parentActivity.getBaseContext(), allPromises, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
			
			if(allPromises.getCount() == 0) {
				Toast.makeText(parentActivity.getBaseContext(), parentActivity.getString(R.string.noPromises), Toast.LENGTH_LONG).show();
			}
		}
		
		class PromiseListItemClickListener implements OnItemClickListener {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				parentActivity.sendInfo(FragmentID.PromiseList, ((TextView)arg1.findViewById(R.id.promiseRow)).getText().toString());
			}			
		}
		
		class PromiseListAdapter extends CursorAdapter {

			ListView currentView;
			
			public PromiseListAdapter(ListView currentView, Context context, Cursor c, int flags) {

				super(context, c, flags);
				this.currentView = currentView;
			}

			@Override
			public void bindView(View arg0, Context arg1, Cursor arg2) {
				
				TextView promiseRow = (TextView) arg0.findViewById(R.id.promiseRow);
				promiseRow.setText(arg2.getString(arg2.getColumnIndex(PromiseContract.PromiseLookup.COLUMN_NAME)));
			}

			@Override
			public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
				
				LayoutInflater inflater = LayoutInflater.from(currentView.getContext());
				return inflater.inflate(R.layout.single_row_item, currentView, false);
			}			
		}
	}
	
	public static class PromiseInfoFragment extends PromiseMeFragment {
		
		private int monthIndex;
		private MonthAndDays currentSelection;
		
		public PromiseInfoFragment() { monthIndex = -1; }
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			
			View rootView = inflater.inflate(R.layout.fragment_promise_info, container, false);
			((CustomCalendar)rootView.findViewById(R.id.promiseCalendar)).sendReference(this);
			return rootView;
		}
		
		@SuppressWarnings("unchecked")
		public ArrayList<MonthAndDays> getPromiseInfo() {
			
			return (ArrayList<MonthAndDays>) parentActivity.getInfo(FragmentID.PromiseInfo);
		}

		public void drawCalendar(ArrayList<MonthAndDays> promiseInfo) {
			
			currentSelection = promiseInfo.get(monthIndex);
			
			CustomCalendar customCalendar = ((CustomCalendar)getView().findViewById(R.id.promiseCalendar));
			customCalendar.getData();
			customCalendar.invalidate();
		}
		
		public MonthAndDays getData() {
			
			return currentSelection;
		}
		
		@Override
		public void onResume() {
			
			super.onStart();
			
			ArrayList<MonthAndDays> promiseInfo = getPromiseInfo();
			monthIndex = -1;
			
			if(promiseInfo.get(0).getProimseText() == null) {
				((TextView)getView().findViewById(R.id.promiseName)).setText(parentActivity.getString(R.string.selectPromise));
			}
			else {
				((TextView)getView().findViewById(R.id.promiseName)).setText(promiseInfo.get(0).getProimseText());
			}
			
			if(promiseInfo.size() > 1) {
				setPromiseInfoText(promiseInfo);
				
				monthIndex = promiseInfo.size() - 1;
				drawCalendar(promiseInfo);
			} 
			else {
				((TextView)getView().findViewById(R.id.promiseInfo)).setText(parentActivity.getString(R.string.noResponses));
			}
		}
		
		public void setPromiseInfoText(ArrayList<MonthAndDays> promiseInfo) {
			
			TextView promiseInfoText = ((TextView)getView().findViewById(R.id.promiseInfo));
			
			int totalDays = 0, numYesDays = 0, numNoDays = 0;
			SparseIntArray yesAnswerCountList = new SparseIntArray();
			SparseIntArray noAnswerCountList = new SparseIntArray();
			
			for(int i = 1; i < promiseInfo.size(); i++) {
				totalDays += updateCountList(yesAnswerCountList, promiseInfo.get(i).getYesDays());
				totalDays += updateCountList(noAnswerCountList, promiseInfo.get(i).getNoDays());
			}
			
			numYesDays = getDayCount(yesAnswerCountList);
			numNoDays = getDayCount(noAnswerCountList);
			
			promiseInfoText.setText("Days Answered: " + totalDays 
									+ "\nYes Days: " + numYesDays + "(" + ((int)numYesDays * 100/totalDays) + "%)"
									+ "\nNo Days: " + numNoDays + "(" + ((int)numNoDays * 100/totalDays) + "%)");
		}
		
		public int updateCountList(SparseIntArray dayCountList, ArrayList<Integer> dayList) {
			
			int daysCounted = 0;
			for(int i : dayList) {
				daysCounted++;
				
				if(dayCountList.get(i, -1) == -1) {
					dayCountList.put(i, 1);
				}
				else {
					dayCountList.put(i, dayCountList.get(i) + 1);
				}
			}
			
			return daysCounted;
		}
		
		public int getDayCount(SparseIntArray dayList) {
			
			int totalDays = 0;
			for(int i = 0; i < dayList.size(); i++) {
				totalDays += dayList.get(dayList.keyAt(i));
			}
			
			return totalDays;
		}

		public void changeMonth(String direction) {
			
			if(monthIndex != -1) {
				ArrayList<MonthAndDays> promiseInfo = getPromiseInfo();
				
				if(MainActivity.NEXT.equals(direction)) {
					if(monthIndex < promiseInfo.size() - 1) {
						monthIndex++;
					}
				}
				else {
					if(monthIndex > 1) {
						monthIndex--;
					}
				}
				
				drawCalendar(promiseInfo);
			}
		}
		
		public void deletePromise() {

			((TextView)getView().findViewById(R.id.promiseInfo)).setText("");
			((CustomCalendar)getView().findViewById(R.id.promiseCalendar)).clearData();
			parentActivity.sendInfo(FragmentID.PromiseInfo, null);
		}
	}
	
	public interface CommInterface {
		
		public Object getInfo(FragmentID requestingFragment);
		public void sendInfo(FragmentID sendingFragment, String info);
	}
	
	public interface CC_Interface {
		
		public void sendReference(PromiseInfoFragment parentFragment);
	}
	
	public enum FragmentID
	{
		Question, 
		NewPromise, 
		PromiseList, 
		PromiseInfo
	}
}