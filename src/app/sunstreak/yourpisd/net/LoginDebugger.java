/**
 * This file is part of yourPISD.
 *
 *  yourPISD is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  yourPISD is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with yourPISD.  If not, see <http://www.gnu.org/licenses/>.
 */

package app.sunstreak.yourpisd.net;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;

import app.sunstreak.yourpisd.TermFinder;

public class LoginDebugger {

	static String mEmail;
	static String mPassword;

	static int CURRENT_TERM_INDEX;

	public static void main(String[] args) throws IOException, JSONException, ExecutionException, InterruptedException {
		
		
		if (args.length == 2) {
			mEmail = args[0];
			mPassword = args[1];
			System.out.println("|" + mEmail + "|" + mPassword + "|");
		} else {
			Scanner sc = new Scanner(System.in);
			System.out.print("Please enter username:\t");
			mEmail = sc.next();
			System.out.print("Please enter password:\t");
			mPassword = sc.next();
			sc.close();
		}

		YPSession session = new YPSession(mEmail, mPassword);

		int loginSuccess = session.login();
		if (loginSuccess == 1)
			System.out.println("Successful login!");
		else {
			System.out.println("Login failed.");
			System.exit(-1);
		}

		session.tryLoginGradebook();

		for (Student st : session.getStudents()) {
			st.loadGradeSummary();
			
			for (int i = 0; i < 8; i++)
				System.out.println(st.getClassesForTerm(i));
			
			for (int i = 0; i < st.getClassMatch().length; i++) {
				System.out.printf("%20s", st.getClassList().optJSONObject(st.getClassMatch()[i]).getString("title") );
				System.out.print("   ");
				JSONArray classGrade = st.getClassList().optJSONObject(st.getClassMatch()[i])
						.optJSONArray("terms");
				
				if (classGrade.optJSONObject(0).getString("description").equals("4th Six Weeks"))
					System.out.printf("%20s", "");
				
				for (int j = 0; j < classGrade.length() ; j++) {
					int score = classGrade.optJSONObject(j).optInt("average", -1);
					System.out.printf("%5s", displayScore(score));
				}
				
				if (classGrade.length() == 4 &&
						classGrade.optJSONObject(0).getString("description").equals("1st Six Weeks"))
					System.out.printf("%20s", "");

				System.out.printf("  S1:%5s", displayScore(st.getClassList().optJSONObject(st.getClassMatch()[i])
						.optInt("firstSemesterAverage", -1)));
				System.out.printf("  S2:%5s\n", displayScore(st.getClassList().optJSONObject(st.getClassMatch()[i])
						.optInt("secondSemesterAverage", -1)));
			}
			
			st.loadAttendanceSummary();
			String[] classNames = st.getAttendanceSummaryClassNames();
			int[][] attendanceSummary = st.getAttendanceSummary();
			for (int j = 0; j < Parser.AttendanceSummary.COLS.length; j++) {
				System.out.printf("%20s", "");
				System.out.printf("%5s", Parser.AttendanceSummary.COLS[j]);
				System.out.println();
			}
			for (int i = 0; i < classNames.length; i++) {
				System.out.printf("%20s", classNames[i]);
				for (int j = 0; j < attendanceSummary[i].length; j++) {
					System.out.printf("%5d", attendanceSummary[i][j]);
				}
				System.out.println();
			}
		}

	}
	
	public static String displayScore (int score) {
		switch (score) {
		case -1: return "XXX";
		case -2: return "NNN";
		default: return Integer.toString(score);
		}
	}

}
