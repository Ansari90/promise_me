package aaa.promise_me;

import aaa.promise_me.FragmentContainer.FragmentID;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

public class MainActivity extends ActionBarActivity implements FragmentContainer.CommInterface {

	public static String NEXT;
	public static String PREVIOUS;
	
	public static String YES;
	public static String NO;
	
	FragmentManager fragmentMananager;
	FragmentContainer.QuestionFragment questionFragment;
	FragmentContainer.NewPromiseFragment newPromiseFragment;
	FragmentContainer.PromiseListFragment promiseListFragment;
	FragmentContainer.PromiseInfoFragment promiseInfoFragment;

	InputMethodManager inputMethodManager;
	PromiseDbHelper dbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		NEXT = getString(R.string.next);
		PREVIOUS = getString(R.string.previous);
		YES = getString(R.string.yes);
		NO = getString(R.string.no);
		
		fragmentMananager = getSupportFragmentManager();
		questionFragment = new FragmentContainer.QuestionFragment();
		newPromiseFragment = new FragmentContainer.NewPromiseFragment();
		promiseListFragment = new FragmentContainer.PromiseListFragment();
		promiseInfoFragment = new FragmentContainer.PromiseInfoFragment();
		
		inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
		dbHelper = new PromiseDbHelper(getBaseContext(), getString(R.string.dbName), Integer.parseInt(getString(R.string.version)));
		dbHelper.initialise();
		dbHelper.setPromiseId();
		
		if(dbHelper.getLookupCursor().getCount() == 0) {
			addFragment(newPromiseFragment);
		}
		else {
			if(dbHelper.getPromiseText() == null) {
				addFragment(promiseListFragment);
			}
			else {
				addFragment(questionFragment);
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int id = item.getItemId();		
		switch(id)
		{
		case R.id.listPromises:
			replaceFragment(promiseListFragment);
			break;
		case R.id.makeNewPromise:
			replaceFragment(newPromiseFragment);
			break;
		case R.id.answerPromise:
			dbHelper.setPromiseId();
			replaceFragment(questionFragment);
			break;
		}	
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onDestroy() {
		
		dbHelper.close();		
		super.onDestroy();
	}
	
	public void addFragment(Fragment newFragment) { 
		
		fragmentMananager.beginTransaction().add(R.id.container, newFragment).commit();
	}
	
	public void replaceFragment(Fragment newFragment) {
		
		fragmentMananager.beginTransaction().replace(R.id.container, newFragment).addToBackStack(null).commit();
	}
	
	public void changeInfoMonth(View v) {
		
		if(((Button)v).equals((Button)promiseInfoFragment.getView().findViewById(R.id.nextMonthButton))) {
			promiseInfoFragment.changeMonth(NEXT);
		}
		else {
			promiseInfoFragment.changeMonth(PREVIOUS);
		}
	}
	
	public void answerQuestion(View v) {
		
		if(((Button)v).equals((Button)questionFragment.getView().findViewById(R.id.yesButton))) {
			dbHelper.insertPromiseAnswer(YES);
		}
		else {
			dbHelper.insertPromiseAnswer(NO);
		}
		
		togglePromise((Button)questionFragment.getView().findViewById(R.id.nextPromiseButton));
	}
	
	public void togglePromise(View v) {
		
		String toggleDirection = null;
		
		if(((Button)v).equals((Button)questionFragment.getView().findViewById(R.id.nextPromiseButton))) {
			toggleDirection = NEXT;
		}
		else {
			toggleDirection = PREVIOUS;
		}
		
		if(dbHelper.changePromise(toggleDirection) != true) {
			dbHelper.setPromiseId();
		}
		
		questionFragment.refresh();
	}
	
	public void createPromise(View v) {	newPromiseFragment.createPromise(); }
	
	public void deletePromise(View v) { promiseInfoFragment.deletePromise(); }
	
	@Override
	public Object getInfo(FragmentID requestingFragment) {
		
		Object response = null;		
		switch(requestingFragment)
		{
		case PromiseInfo:
			response = dbHelper.getPromiseData();
			break;
		case PromiseList:
			response = dbHelper.getLookupCursor();
			break;
		case Question:
			response = dbHelper.getPromiseText();		
			break;
		default:
			break;
		}
		
		return response;
	}

	@Override
	public void sendInfo(FragmentID sendingFragment, String info) {
		
		switch(sendingFragment)
		{
		case NewPromise:
			dbHelper.createPromise(info);
			replaceFragment(questionFragment);
			inputMethodManager.hideSoftInputFromWindow(newPromiseFragment.getView().findViewById(R.id.promiseText).getWindowToken(), 0);			
			break;
		case PromiseInfo:
			dbHelper.deletePromise();
			replaceFragment(promiseListFragment);
			break;
		case PromiseList:
			dbHelper.setPromiseId(info);
			replaceFragment(promiseInfoFragment);
			break;
		default:
			break;
		}
	}
}
