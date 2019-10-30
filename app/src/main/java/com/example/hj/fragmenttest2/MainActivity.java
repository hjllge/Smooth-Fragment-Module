package com.example.hj.fragmenttest2;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements KeyboardHeightObserver {
	private static final String TAG = "MainActivity";
	private static final String LIST_FRAGMENT_TAG = "list_fragment";
	private final static int KEYBOARD_DURATION = 330;
	private static final double POWER = 4.0;
	//public static Interpolator interpolator = new AccelerateDecelerateInterpolator();
	private static long time;
	public static Interpolator interpolator = new Interpolator() {

		@Override
		public float getInterpolation(float input) {
			//FPS
			long cur = System.currentTimeMillis();
			int del = (int)(cur - time);
			if(del != 0) {
				int fps = 1000 / del;
				Log.d(TAG, "fps = " + fps);
				time = cur;
			}

			return 1 - (float)Math.pow(1 - input, POWER);
		}
	};
	static boolean preventFocus = false;
	static View.OnFocusChangeListener OFCL = new View.OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			String name = getNameFromId(v.getId());
			Log.d(TAG, name + " is hasFocus = " + hasFocus);
			if(preventFocus) {
				Log.d(TAG, "FocusChangeListener is prevented");
				return;
			}

			//TODO
			if(hasFocus) {
				int bottomSpace = sKeyboardSlider.getHeight() - v.getBottom();
				int shift = keyboardImageHeight - bottomSpace;
				Log.d(TAG, "shift = " + shift);
				if(shift > 0) {
					slideUp(shift);
					//textView1.append("\nshift up by keyboard = " + shift);
				} else {
					isShowKeyboard = true;
					keyboardUp();
				}
			} else {
				//slideDown();
			}
		}
	};

	static View.OnClickListener OCL = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			OFCL.onFocusChange(v, true);
		}
	};


	static View sKeyboardSlider = null;
	static View keyboardView = null;
	private View scrollView;
	private ValueAnimator animator = null;

	private TextView textView1;

	private EditText editText1;
	private EditText editText2;
	private EditText editText3;
	private EditText editText4;
	private EditText editText5;
	private EditText editText6;


	private static boolean isShowKeyboard = false;
	private static float keyboardTransY = 0f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if(scrollView== null)
			scrollView = findViewById(R.id.scrollView);
		sKeyboardSlider = scrollView;
		keyboardView = findViewById(R.id.keyboardView);

		textView1 = findViewById(R.id.textView1);
		editText1 = findViewById(R.id.editText1);
		editText2 = findViewById(R.id.editText2);
		editText3 = findViewById(R.id.editText3);
		editText4 = findViewById(R.id.editText4);
		editText5 = findViewById(R.id.editText5);
		editText6 = findViewById(R.id.editText6);
		editText1.setShowSoftInputOnFocus(false);
		editText2.setShowSoftInputOnFocus(false);
		editText3.setShowSoftInputOnFocus(false);
		editText4.setShowSoftInputOnFocus(false);
		editText5.setShowSoftInputOnFocus(false);
		editText6.setShowSoftInputOnFocus(false);
		editText1.setOnFocusChangeListener(OFCL);
		editText2.setOnFocusChangeListener(OFCL);
		editText3.setOnFocusChangeListener(OFCL);
		editText4.setOnFocusChangeListener(OFCL);
		editText5.setOnFocusChangeListener(OFCL);
		editText6.setOnFocusChangeListener(OFCL);
		editText1.setOnClickListener(OCL);
		editText2.setOnClickListener(OCL);
		editText3.setOnClickListener(OCL);
		editText4.setOnClickListener(OCL);
		editText5.setOnClickListener(OCL);
		editText6.setOnClickListener(OCL);
		setKeyboardObserver();

		keyboardView.post(new Runnable() {
			@Override
			public void run() {
				keyboardImageHeight = keyboardView.getHeight();
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)keyboardView.getLayoutParams();
				params.bottomMargin = -keyboardImageHeight;
				keyboardView.setLayoutParams(params);
			}
		});
	}

	private static ValueAnimator vAnimView;
	private static ValueAnimator.AnimatorUpdateListener scrollViewUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			keyboardTransY = (Integer)animation.getAnimatedValue();
			sKeyboardSlider.setTranslationY(keyboardTransY);
			//Log.d(TAG, "translationY = " + translationY);
		}
	};
	private static void slideUp(int slideUp) {
		if(slideUp == 0)
			return;
		Log.d(TAG, "slide up!");
		isShowKeyboard = true;
		if(vAnimView != null && vAnimView.isRunning()) {
			vAnimView.cancel();
		}
		vAnimView = ValueAnimator.ofInt((int)keyboardTransY, -slideUp);
		vAnimView.setDuration(KEYBOARD_DURATION);
		vAnimView.addUpdateListener(scrollViewUpdateListener);
		vAnimView.setInterpolator(MainActivity.interpolator);
		vAnimView.start();
		keyboardUp();
	}
	private static void slideDown() {
		if(!isShowKeyboard)
			return;
		Log.d(TAG, "slideDown!");
		isShowKeyboard = false;
		if(vAnimView != null && vAnimView.isRunning()) {
			vAnimView.cancel();
		}
		float currentTransY = (int)sKeyboardSlider.getTranslationY();
		vAnimView = ValueAnimator.ofInt(((int)currentTransY), 0);
		vAnimView.setDuration(KEYBOARD_DURATION);
		vAnimView.addUpdateListener(scrollViewUpdateListener);
		vAnimView.setInterpolator(MainActivity.interpolator);
		vAnimView.start();
		keyboardDown();
	}

	private static ValueAnimator vAnimKey;
	private static ValueAnimator.AnimatorUpdateListener keyboardViewUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			keyboardView.setTranslationY((Float)animation.getAnimatedValue());
			//Log.d(TAG, "translationY = " + translationY);
		}
	};
	private static void keyboardUp() {
		if(vAnimKey != null && vAnimKey.isRunning()) {
			vAnimKey.cancel();
		}
		vAnimKey = ValueAnimator.ofFloat((int)keyboardView.getTranslationY(), -keyboardImageHeight);
		vAnimKey.setDuration(KEYBOARD_DURATION);
		vAnimKey.addUpdateListener(keyboardViewUpdateListener);
		vAnimKey.setInterpolator(MainActivity.interpolator);
		vAnimKey.start();
	}
	private static void keyboardDown() {
		if(vAnimKey != null && vAnimKey.isRunning()) {
			vAnimKey.cancel();
		}
		float currentTransY = (int)keyboardView.getTranslationY();
		vAnimKey = ValueAnimator.ofFloat(((int)currentTransY), 0);
		vAnimKey.setDuration(KEYBOARD_DURATION);
		vAnimKey.addUpdateListener(keyboardViewUpdateListener);
		vAnimKey.setInterpolator(MainActivity.interpolator);
		vAnimKey.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch(id) {
			case R.id.action_show_state :
				Log.d(TAG, "current tranX = " + scrollView.getTranslationX());
				Log.d(TAG, "sFraction = " + SlidingListFragment.sFraction);
				Log.d(TAG, "keyboardHeight = " + keyboardHeight);
				Log.d(TAG, "isShowKeyboard = " + isShowKeyboard);
				return true;
			case R.id.action_show_list :
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editText1.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(editText2.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(editText3.getWindowToken(), 0);
				toggleFragment();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if(isShowKeyboard) {
			slideDown();
			return;
		}
		if(!isFragmentShow) {
			toggleFragment();
			return;
		}
		super.onBackPressed();
	}
	private boolean isFragmentShow = true;
	private void toggleFragment() {
		Fragment f = getSupportFragmentManager().findFragmentByTag(LIST_FRAGMENT_TAG);
		if (isFragmentShow) {	//show target fragment
			getSupportFragmentManager().beginTransaction()
					//.setCustomAnimations(R.animator.slide_up, R.animator.slide_down, R.animator.slide_up, R.animator.slide_down)	//original version
					.add(R.id.list_fragment_container,
							Fragment.instantiate(this, SlidingListFragment.class.getName()),
							LIST_FRAGMENT_TAG)
					//.addToBackStack(null)		//original version
					.commit();
			if(animator != null && animator.isRunning())
				animator.cancel();
			animator = ValueAnimator.ofFloat(SlidingListFragment.sFraction, 0f);
			final float fraction = interpolator.getInterpolation(SlidingListFragment.sFraction);
			final int duration = SlidingListFragment.sFraction== 1f ?
					SlidingListFragment.DURATION_SHOW
					: (int)(SlidingListFragment.DURATION_SHOW * fraction);
			animator.setDuration(duration);
			animator.setInterpolator(interpolator);
			animator.addUpdateListener(updateListener);
			animator.addListener(SlidingListFragment.animListener);
			animator.start();
			isFragmentShow = false;
			slideDown();
		} else {				//hide target fragment
			//improved version
			getSupportFragmentManager().beginTransaction()
					.remove(f)
					.commit();
			//original version
			//getSupportFragmentManager().popBackStack();
			if(animator != null && animator.isRunning())
				animator.cancel();
			animator = ValueAnimator.ofFloat(SlidingListFragment.sFraction, 1f);
			final float fraction = interpolator.getInterpolation(1f - SlidingListFragment.sFraction);
			final int duration = SlidingListFragment.sFraction == 0f ?
					SlidingListFragment.DURATION_HIDE
					: (int)(SlidingListFragment.DURATION_HIDE * fraction);
			animator.setDuration(duration);
			animator.setInterpolator(interpolator);
			animator.addUpdateListener(updateListener);
			animator.addListener(SlidingListFragment.animListener);
			animator.start();
			isFragmentShow = true;

			sKeyboardSlider = scrollView;
		}
	}

	public ValueAnimator.AnimatorUpdateListener updateListener =  new ValueAnimator.AnimatorUpdateListener() {
		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			float translationX = scrollView.getWidth() * ((Float)animation.getAnimatedValue() - 1);
			scrollView.setTranslationX(translationX);
		}
	};


	private void setKeyboardObserver() {
		keyboardHeightProvider = new KeyboardHeightProvider(this);
		View view = findViewById(R.id.rootLayout);
		view.post(new Runnable() {
			public void run() {
				keyboardHeightProvider.start();
			}
		});
	}

	/** Keyboard height 측정용 코드 **/
	private KeyboardHeightProvider keyboardHeightProvider;
	private int keyboardHeight = 0;
	private static int keyboardImageHeight = 0;
	@Override
	public void onKeyboardHeightChanged(int height, int orientation) {
		if(height > 0)
			keyboardHeight = height;
		String ori = orientation == Configuration.ORIENTATION_PORTRAIT ? "portrait" : "landscape";
		Log.d(TAG, "keyboardHeight in pixels: " + keyboardHeight + " " + ori);

		textView1.setText("MainActivity\n" +
				"keyboard height = " + Integer.toString(height) + "\n" +
				"screen orientation = " + ori);

		View focus = getCurrentFocus();
		if(focus != null && focus instanceof EditText) {
			// OnFocusChangeListener로 이동
			//if(height > 0) {
			//	int bottomSpace = sKeyboardSlider.getHeight() - focus.getBottom();
			//	int shift = height - bottomSpace;
			//	if(shift > 0) {
			//		slideUp(shift);
			//		textView1.append("\nshift up by keyboard = " + shift);
			//	}
			//} else {
			//	slideDown();
			//}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		keyboardHeightProvider.setKeyboardHeightObserver(null);
	}
	@Override
	public void onResume() {
		super.onResume();
		keyboardHeightProvider.setKeyboardHeightObserver(this);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		keyboardHeightProvider.close();
	}


	private static String getNameFromId(int id) {
		switch(id) {
			case R.id.editText1 :
				return "editText1";
			case R.id.editText2 :
				return "editText2";
			case R.id.editText3 :
				return "editText3";
			case R.id.editText1_in_fragment :
				return "editText1_in_fragment";
		}
		return "NO_ID";
	}
}
