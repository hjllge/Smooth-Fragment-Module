package com.example.hj.fragmenttest2;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.RelativeLayout;

/**
 * Created by paveld on 4/17/14.
 */
public class SlidingListFragment extends Fragment {
	private static final String TAG = "SlidingListFragment";
	public final static int DURATION_SHOW = 400;
	public final static int DURATION_HIDE = 350;

	private View view;
	public static float sFraction = 1f;
	public static boolean isAnimPlaying = false;
	private ValueAnimator animator = null;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.sliding_fragment_layout, container, false);
		final RelativeLayout rr = view.findViewById(R.id.slidingRelativeLayout);
		rr.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				rr.getViewTreeObserver().removeOnPreDrawListener(this);
				rr.setTranslationX(rr.getWidth());
				return true;
			}
		});
		View scrollView = view.findViewById(R.id.scrollView);
		MainActivity.sKeyboardSlider = scrollView;
		EditText editText1 = scrollView.findViewById(R.id.editText1_in_fragment);
		editText1.setShowSoftInputOnFocus(false);
		editText1.setOnFocusChangeListener(MainActivity.OFCL);
		editText1.setOnClickListener(MainActivity.OCL);
		return view;
	}

	@Override
	public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
		if(enter) {
			if(animator != null && animator.isRunning())
				animator.cancel();
			animator = ValueAnimator.ofFloat(sFraction, 0f);
			Interpolator interpolator = MainActivity.interpolator;
			final int duration = sFraction== 1f ? DURATION_SHOW : (int)(DURATION_SHOW * interpolator.getInterpolation(sFraction));
			animator.setDuration(duration);
			animator.setInterpolator(MainActivity.interpolator);
			animator.addUpdateListener(updateListener);
			animator.addListener(animListener);
		} else {
			if(animator != null && animator.isRunning())
				animator.cancel();
			animator = ValueAnimator.ofFloat(sFraction, 1f);
			Interpolator interpolator = MainActivity.interpolator;
			final int duration = sFraction== 0f ? DURATION_HIDE : (int)(DURATION_HIDE * (1f - interpolator.getInterpolation(sFraction)));
			animator.setDuration(duration);
			animator.setInterpolator(MainActivity.interpolator);
			animator.addUpdateListener(updateListener);
			animator.addListener(animListener);
		}
		return animator;
	}

	public static Animator.AnimatorListener animListener = new Animator.AnimatorListener() {
		@Override
		public void onAnimationStart(Animator animation) {
			isAnimPlaying = true;
			MainActivity.preventFocus = true;
			//preventFocus
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			isAnimPlaying = false;
			MainActivity.preventFocus = false;
		}

		@Override
		public void onAnimationCancel(Animator animation) {}

		@Override
		public void onAnimationRepeat(Animator animation) {}
	};

	public ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() {
		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			sFraction = (Float)animation.getAnimatedValue();
			float translationX = view.getWidth() * sFraction;
			view.setTranslationX(translationX);
//			Log.d(TAG, "sFraction = " + sFraction);
		}
	};


}