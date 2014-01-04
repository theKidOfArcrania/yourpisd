package app.sunstreak.yourpisd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import app.sunstreak.yourpisd.net.YPSession;
import app.sunstreak.yourpisd.net.DateHandler;


@SuppressLint("ValidFragment")
public class ClassSwipeActivity extends FragmentActivity implements ActionBar.TabListener{
	static List<Fragment> mFragments;
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	static ViewPager mViewPager;

	static int studentIndex;
	static int receivedClassIndex;
	static int classCount;
	static int classesMade = 0;
	static int termIndex;
	static boolean doneMakingClasses;
	static Typeface robotoNew;
	static YPSession session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_class_swipe);
		robotoNew = Typeface.createFromAsset(this.getAssets(),"Roboto-Light.ttf");


		receivedClassIndex = getIntent().getExtras().getInt("classIndex");
		classCount = getIntent().getExtras().getInt("classCount");
		termIndex = getIntent().getExtras().getInt("termIndex");
		studentIndex = getIntent().getExtras().getInt("studentIndex");

		setTitle(TermFinder.Term.values()[termIndex].name);

		session = ((YPApplication) getApplication() ).session;

		session.studentIndex = studentIndex;

		mFragments = new ArrayList<Fragment>();
		for (int i = 0; i < classCount; i++) {
			Bundle args = new Bundle();
			args.putInt(DescriptionFragment.ARG_SECTION_NUMBER, i);
			Fragment fragment = new DescriptionFragment();
			fragment.setArguments(args);
			mFragments.add(fragment);
		}



		//		System.out.println("mFragments size = " + mFragments.size() + " and classCount = " + classCount);


		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager(), mFragments);


		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		for(int i = 0; i< classCount; i++)
		{
			actionBar.addTab(actionBar.newTab().setText(session.getCurrentStudent().getClassName(session.getCurrentStudent().getClassMatch()[i]))
                    .setTabListener(this));
		}
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        	 
	            @Override
	            public void onPageSelected(int position) {
	                // on changing the page
	                // make respected tab selected
	                actionBar.setSelectedNavigationItem(position);
	            }
	         
	            @Override
	            public void onPageScrolled(int arg0, float arg1, int arg2) {
	            }
	         
	            @Override
	            public void onPageScrollStateChanged(int arg0) {
	            }
	        });
		
		
		
		
		
		mViewPager.setCurrentItem(receivedClassIndex);
		mViewPager.setOffscreenPageLimit(5);


	}
	//fixed tab listener implemented
		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			mViewPager.setCurrentItem(tab.getPosition());
			
		}
		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}
	@Override
	protected void onPause() {
		super.onPause();

	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.class_swipe_actions, menu);


		if (termIndex == 0)
			menu.findItem(R.id.previous_six_weeks).setEnabled(false);
		else if (termIndex == 3)
			menu.findItem(R.id.next_six_weeks).setEnabled(false);

		// Create list of students in Menu.
		if (session.MULTIPLE_STUDENTS) {
			for (int i = 0; i < session.getStudents().size(); i++) {
				String name = session.getStudents().get(i).name;
				MenuItem item = menu.add(name);

				// Set the currently enabled student un-clickable.
				if (i == studentIndex)
					item.setEnabled(false);

				item.setOnMenuItemClickListener(new StudentSelectListener(i));
				item.setVisible(true);
			}
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent;
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.log_out:
			SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
			editor.putBoolean("auto_login", false);
			editor.commit();
			intent = new Intent(this, LoginActivity.class);
			// Clear all activities between this and LoginActivity
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
			/*
		case R.id.refresh:
			Intent refreshIntent = new Intent(this, LoginActivity.class);
			refreshIntent.putExtra("Refresh", true);
			startActivity(refreshIntent);
			finish();
			return true;
			 */
		case R.id.previous_six_weeks:
			intent = new Intent(this, ClassSwipeActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("studentIndex", studentIndex);
			intent.putExtra("classCount", classCount);
			intent.putExtra("classIndex", mViewPager.getCurrentItem());
			// Don't go into the negatives!
			intent.putExtra("termIndex", termIndex - 1 >= 0 ? termIndex - 1 : 0);
			startActivity(intent);

			//			overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);

			return true;
		case R.id.next_six_weeks:
			intent = new Intent(this, ClassSwipeActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("studentIndex", studentIndex);
			intent.putExtra("classCount", classCount);
			intent.putExtra("classIndex", mViewPager.getCurrentItem());
			// Don't go too positive!
			intent.putExtra("termIndex", termIndex + 1 <= 7 ? termIndex + 1 : 7);
			startActivity(intent);
			//			overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		List<Fragment> fragmentList;

		public SectionsPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
			super(fm);
			fragmentList = fragments;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = fragmentList.get(position);        
			return fragment;
		}

		@Override
		public int getCount() {
			return fragmentList.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return session.getCurrentStudent().getClassName(session.getCurrentStudent().getClassMatch()[position]);
		}
	}


	/**
	 * A fragment that displays grades for one class.
	 */
	public static class DescriptionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";
		public static final int ASSIGNMENT_NAME_ID = 2222;
		
		private ClassGradeTask mClassGradeTask;
		private int position;
		private int classIndex;
		private JSONObject mClassGrade;
		private View rootView;

		@Override
		public void onPause () {
			if (mClassGradeTask != null)
				mClassGradeTask.cancel(true);
			super.onPause();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState)  {
			if (session.getStudents().size() == 0)
			{
				session = null;
				SharedPreferences.Editor editor = getActivity().getSharedPreferences("LoginActivity", Context.MODE_PRIVATE).edit();
				editor.putBoolean("auto_login", false);
				editor.commit();

				Intent intent = new Intent(getActivity(), LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("show", true);
				startActivity(intent);
			}
			position = getArguments().getInt(ARG_SECTION_NUMBER);
			classIndex = session.getCurrentStudent().getClassMatch()[position];

			rootView = inflater.inflate(R.layout.class_description, container, false);
			getActivity().setProgressBarIndeterminateVisibility(true);
			if ( session.getCurrentStudent().hasClassGrade(classIndex, termIndex) ) {
				mClassGrade = session.getCurrentStudent().getClassGrade(classIndex, termIndex);
				setUiElements();
			} else {
				mClassGradeTask = new ClassGradeTask();
				mClassGradeTask.execute(classIndex, termIndex);
			}


			return rootView;
		}



		@SuppressLint("ResourceAsColor")
		public class ClassGradeTask extends AsyncTask<Integer, Void, JSONObject> {

			@Override
			protected void onPreExecute () {
				System.out.println("ClassGradeTask starting");
			}

			@Override
			protected void onPostExecute (final JSONObject result) {
				mClassGrade = result;
				System.out.println("ClassGradeTask finished");

				setUiElements();
			}

			@Override
			protected JSONObject doInBackground(Integer... integers) {
				try {
					return session.getCurrentStudent().getClassGrade(integers[0], integers[1]);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}

		public void setUiElements () {
			getActivity().setProgressBarIndeterminateVisibility(false);
			
			RelativeLayout layout = (RelativeLayout) rootView.findViewById(R.id.info);
			
			int lastIdAdded = R.id.teacher_name;
			TextView teacher = (TextView) layout.findViewById(R.id.teacher_name);
			TextView sixWeeksAverage = (TextView) layout.findViewById(R.id.six_weeks_average);
			teacher.setVisibility(View.VISIBLE);
			sixWeeksAverage.setVisibility(View.VISIBLE);

			class AssignmentDetailListener implements OnClickListener {

				int classIndex;
				int termIndex;
				int assignmentId;

				AssignmentDetailListener (int classIndex, int termIndex, int assignmentId) {
					this.classIndex = classIndex;
					this.termIndex = termIndex;
					this.assignmentId = assignmentId;
				}

				@Override
				public void onClick(View arg0) {
					new GradeDetailsTask().execute(classIndex, termIndex, assignmentId);
				}
			}

			try {
				// The following line prevents force close. Idk why.
				// Maybe the extra print time somehow fixes it...
				System.out.println(mClassGrade);

				teacher.setText(session.getCurrentStudent().getClassList().getJSONObject(classIndex).optString("teacher"));

				int avg = mClassGrade.optInt("average", -1);
				if (avg != -1) {
					String average = Integer.toString(avg);
					sixWeeksAverage.setText(average);
				} else
					sixWeeksAverage.setVisibility(View.INVISIBLE);

				// Add current student's name
				if (session.MULTIPLE_STUDENTS) {
					LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					LinearLayout studentName = (LinearLayout) inflater.inflate(R.layout.main_student_name_if_multiple_students, layout, false);
					((TextView) studentName.findViewById(R.id.name)).setText(session.getCurrentStudent().name);
					
					RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT);
					lp.addRule(RelativeLayout.BELOW, lastIdAdded);
					lastIdAdded = R.id.name;
					
					layout.addView(studentName, lp);
				}
				
				LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				
				
				for (int category = 0;
						category < mClassGrade.getJSONArray("categoryGrades").length();
						category++)
				{
					LinearLayout card = new LinearLayout(getActivity());
					card.setOrientation(LinearLayout.VERTICAL);
					card.setBackgroundResource(R.drawable.card_custom);

					// Name of the category ("Daily Work", etc)
					String categoryName = mClassGrade.getJSONArray("categoryGrades").getJSONObject(category).getString("Category");
					
					// for every grade in this term [any category]
					for(int i = 0; i< mClassGrade.getJSONArray("grades").length(); i++)
					{
						// only if this grade is in the category which we're looking for
						if (mClassGrade.getJSONArray("grades").getJSONObject(i).getString("Category")
								.equals(categoryName))
						{
							LinearLayout innerLayout = (LinearLayout) inflater.inflate(R.layout.class_swipe_grade_view, card, false);
							innerLayout.setId(mClassGrade.getJSONArray("grades").getJSONObject(i)
									.getInt("assignmentId"));

							TextView descriptionView = (TextView) innerLayout.findViewById(R.id.description);
							String description = "" + mClassGrade.getJSONArray("grades")
									.getJSONObject(i).getString("Description");
							descriptionView.setText(description);
							descriptionView.setId(ASSIGNMENT_NAME_ID);
							
							TextView grade = (TextView) innerLayout.findViewById(R.id.grade);
							String gradeValue = mClassGrade.getJSONArray("grades")
									.getJSONObject(i).optString("Grade");
							grade.setText(gradeValue);

							innerLayout.setOnClickListener(new AssignmentDetailListener(classIndex, termIndex, innerLayout.getId()));
							
							card.addView(innerLayout);
						}

					}
					// Create a category summary view
					LinearLayout categoryLayout = (LinearLayout) inflater.inflate(R.layout.class_swipe_category_card, card, false);

					TextView categoryNameView = (TextView) categoryLayout.findViewById(R.id.category_name);
					categoryNameView.setText(categoryName);

					TextView scoreView = (TextView) categoryLayout.findViewById(R.id.category_score);
					String categoryScore = mClassGrade.getJSONArray("categoryGrades")
							.getJSONObject(category).optString("Letter");
					scoreView.setText(categoryScore);

					// Add the view to the card
					card.addView(categoryLayout);

					Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_from_down_rotate);
					animation.setStartOffset(0);
					
					card.setId(lastIdAdded + 1);
					RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT);
					lp.addRule(RelativeLayout.BELOW, lastIdAdded);
					layout.addView(card, lp);
					lastIdAdded = card.getId();
					
					card.startAnimation(animation);

				}


				//				TextView lastUpdatedView = new TextView(getActivity());
				//				lastUpdatedView.setText(DateHandler.timeSince(mClassGrade.getLong("lastUpdated")));
				//				lastUpdatedView.setPadding(10, 0, 0, 0);
				//				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				//						RelativeLayout.LayoutParams.WRAP_CONTENT,
				//						RelativeLayout.LayoutParams.WRAP_CONTENT);
				//				lp.addRule(RelativeLayout.BELOW, lastIdAdded);
				//				desc.addView(lastUpdatedView, lp);


			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
		
		class GradeDetailsTask extends AsyncTask<Integer, Integer, String> {
			
			CharSequence title;
			
			ProgressDialog dialog;
			
			@Override
			protected String doInBackground(Integer... params) {
				title = ((TextView)rootView.findViewById(params[2]).findViewById(ASSIGNMENT_NAME_ID)).getText();
				try {
					String[] array = session.getCurrentStudent().getAssignmentDetails(params[0], params[1], params[2]);
					return "Due date: "+ array[0] + DateHandler.daysRelative(array[1]) +"\nWeight: " + array[2];
				} catch (Exception e) {
					cancel(true);
					return null;
				}
			}

			@Override
			protected void onPreExecute () {
				dialog = ProgressDialog.show(getActivity(), "", 
						"Loading Grade Details", true);
				dialog.show();
			}

			@Override
			protected void onPostExecute (final String result) {
				dialog.dismiss();

				// Display the information.
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(title);

				try {
					builder.setMessage(result);
				} catch(Exception e) {
					return;
				}

				builder.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			}

		}
	}

	

	class StudentSelectListener implements MenuItem.OnMenuItemClickListener {

		int menuStudentIndex;

		public StudentSelectListener (int menuStudentIndex) {
			this.menuStudentIndex = menuStudentIndex;
		}

		@Override
		public boolean onMenuItemClick(MenuItem arg0) {

			session.studentIndex = menuStudentIndex;

			Intent intent = new Intent(ClassSwipeActivity.this, MainActivity.class);
			intent.putExtra("mainActivitySection", 1);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			startActivity(intent);

			return true;

		}

	}
	
}
