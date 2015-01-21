package ocs.com.ebys;

import android.os.AsyncTask;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EBYSController {
	private final String HOST_URL = "http://ebys.ege.edu.tr/";
	private final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.99 Safari/537.36";
	private final int DERS_KODU = 0;
	private final int DERS_ADI = 1;
	private final int OGRETIM_UYESI = 4;
	private final int DEVAM_DURUMU = 5;
	private final int ARA_SINAV = 7;
	private final int ODEV_1 = 8;
	private final int ODEV_2 = 9;
	private final int YID = 10;
	private final int YSSN = 11;
	private final int BN = 12;
	private final int BUT = 13;
	private final int HBN = 14;
	
	private static EBYSController instance;
    private static User user;
	private List<String> cookies;
	private HttpURLConnection connection;
    private OnLoginTaskCompleted loginTaskListener;
    private OnGetCoursesTaskCompleted getCoursesTaskListener;
	
	public static EBYSController getInstance() {
		if (instance == null) {
			instance = new EBYSController();
		}
		return instance;
	}

    public static User getUser() {
        return user;
    }

	public void getCourses(OnGetCoursesTaskCompleted listener) {
        getCoursesTaskListener = listener;
        new GetCourses().execute();
	}
	
	public void login(OnLoginTaskCompleted listener, String username, String password) {
        loginTaskListener = listener;
        new Login().execute(username, password);
	}

    private class Login extends AsyncTask<String, Void, ServerResult> {
        ServerResult result = new ServerResult();

        @Override
        protected ServerResult doInBackground(String... params) {
            String url = HOST_URL + "login.aspx";
            String ebys = HOST_URL + "dashboard.aspx";

            // Make sure cookies is turn on
            CookieHandler.setDefault(new CookieManager());
            try {
                // 1. Send a "GET" request, so that you can extract the form's data.
                String page = getPageContent(url);
                String postParams = getFormParams(page, params[0], params[1]);

                // 2. Construct above post's content and then send a POST request for
                // Authentication
                String html = sendPost(url, postParams);

                // 3. Check login status
                String message = checkLoginResult(html);
                if (message.equals("successful")) {
                    result.setSuccess(true);
                    result.setMessage("Oturum açıldı");
                } else {
                    result.setSuccess(false);
                    result.setMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
                result.setSuccess(false);
                result.setMessage("Tekrar deneyin");
            }
            return result;
        }

        @Override
        protected void onPostExecute(ServerResult result) {
            super.onPostExecute(result);
            loginTaskListener.onLoginTaskCompleted(result);
        }
    }

    private class GetCourses extends AsyncTask<String, Void, ServerResult> {
        ServerResult result = new ServerResult();

        @Override
        protected ServerResult doInBackground(String... params) {
            String url = HOST_URL + "Ogrenci/Ogr0201/Default.aspx?lang=tr-TR";
            List<Course> courses = new ArrayList<Course>();

            try {
                String html = getPageContent(url);
                Document doc = Jsoup.parse(html);

                if (user == null) {
                    String name = doc.select("span#lbl_ad_soyad2").first().text();
                    String number = doc.select("span#lbl_ogrno2").first().text();
                    user = new User(name, number);
                }

                Elements fieldsets = doc.select("fieldset");
                fieldsets.remove(fieldsets.size() - 1);
                Elements rows = fieldsets.select("tr");

                for (int i = 1; i < rows.size(); i += 3) {
                    Element header = rows.get(i - 1);
                    Element row = rows.get(i);
                    Elements grades = row.select("td");
                    Elements headers = header.select("td");

                    Course course = new Course();

                    try {
                        course.setCode(grades.get(DERS_KODU).text());
                        course.setName(grades.get(DERS_ADI).text());
                        course.setInstructor(grades.get(OGRETIM_UYESI).text());
                        course.setAttendance(grades.get(DEVAM_DURUMU).text());
                        course.addGrade(new Grade(headers.get(ARA_SINAV).text(), grades.get(ARA_SINAV).text()));

                        if (!headers.get(ODEV_1).text().isEmpty()) {
                            course.addGrade(new Grade(headers.get(ODEV_1).text(), grades.get(ODEV_1).text()));
                        }
                        if (!headers.get(ODEV_2).text().isEmpty()) {
                            course.addGrade(new Grade(headers.get(ODEV_2).text(), grades.get(ODEV_2).text()));
                        }

                        course.addGrade(new Grade(headers.get(YID).text(), grades.get(YID).text()));
                        course.addGrade(new Grade(headers.get(YSSN).text(), grades.get(YSSN).text()));
                        course.addGrade(new Grade(headers.get(BN).text(), grades.get(BN).text()));

                        if (!grades.get(BUT).text().contains("-")) {
                            course.addGrade(new Grade(headers.get(BUT).text(), grades.get(BUT).text()));
                        }

                        course.addGrade(new Grade(headers.get(HBN).text(), grades.get(HBN).text()));
                        course.setLetterGrade(grades.get(HBN).text().split("-")[0].trim());

                        courses.add(course);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                result.setData(courses);
                result.setSuccess(true);
                result.setMessage("Notlar yüklendi");

            } catch (Exception e) {
                e.printStackTrace();
                result.setSuccess(false);
                result.setMessage("Tekrar deneyin");
            }

            return result;
        }

        @Override
        protected void onPostExecute(ServerResult result) {
            super.onPostExecute(result);
            List<Course> reversed = (List<Course>) result.getData();

            try {
                Collections.reverse(reversed);
                result.setData(reversed);
            } catch (UnsupportedOperationException e) {
                e.printStackTrace();
            }
            getCoursesTaskListener.onGetCoursesTaskCompleted(result);
        }
    }

	private String sendPost(String url, String postParams) throws IOException {
		URL obj = new URL(url);
		connection = (HttpURLConnection) obj.openConnection();

        connection.setInstanceFollowRedirects(false);

		// Acts like a browser
		connection.setUseCaches(false);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Host", "ebys.ege.edu.tr");
		connection.setRequestProperty("Origin", "http://ebys.ege.edu.tr");
		connection.setRequestProperty("User-Agent", USER_AGENT);
		connection.setRequestProperty("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		connection.setRequestProperty("Accept-Language", "en,tr;q=0.8,en-US;q=0.6");
		connection.setRequestProperty("Cache-Control", "max-age=0");
		connection.setRequestProperty("Connection", "keep-alive");
		connection.setRequestProperty("Referer", "http://ebys.ege.edu.tr/login.aspx");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Length", Integer.toString(postParams.length()));
		connection.setRequestProperty("DNT", "1");
        if (cookies != null) {
            for (String cookie : this.cookies) {
                connection.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
            }
        }

		// Send post request
		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(postParams);
		wr.flush();
		wr.close();

		int responseCode = connection.getResponseCode();
		System.out.println("\nSending 'POST' request to URL: " + url);
		System.out.println("Post parameters: " + postParams);
		System.out.println("Response Code: " + responseCode);

		BufferedReader in = 
				new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

        return response.toString();
	}

	private String getPageContent(String url) throws IOException {
		URL obj = new URL(url);
		connection = (HttpURLConnection) obj.openConnection();

        connection.setInstanceFollowRedirects(false);

		// Default is GET
		connection.setRequestMethod("GET");

		connection.setUseCaches(false);

		// Acts like a browser
		connection.setRequestProperty("User-Agent", USER_AGENT);
		connection.setRequestProperty("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		connection.setRequestProperty("Accept-Language", "en,tr;q=0.8,en-US;q=0.6");
		connection.setRequestProperty("Cache-Control", "max-age=0");
		connection.setRequestProperty("DNT", "1");
		if (cookies != null) {
			for (String cookie : this.cookies) {
				connection.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
			}
		}
		int responseCode = connection.getResponseCode();
		System.out.println("\nSending 'GET' request to URL: " + url);
		System.out.println("Response Code: " + responseCode);

		BufferedReader in = 
				new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// Get the response cookies
		setCookies(connection.getHeaderFields().get("Set-Cookie"));

		return response.toString();
	}

	private String getFormParams(String html, String username, String password)
			throws UnsupportedEncodingException {

		System.out.println("Extracting form's data...");

		Document doc = Jsoup.parse(html);

		// Login form id
		Element loginForm = doc.getElementById("form1");
		Elements inputElements = loginForm.getElementsByTag("input");
		List<String> paramList = new ArrayList<String>();
		for (Element inputElement : inputElements) {
			String key = inputElement.attr("name");
			String value = inputElement.attr("value");

			if (key.equals("txtLogin")) {
				value = username;
			} else if (key.equals("txtPassword")) {				
				value = password;
			}
			paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
		}

		// Build parameters list
		StringBuilder result = new StringBuilder();
		for (String param : paramList) {
			if (result.length() == 0) {
				result.append(param);
			} else {
				result.append("&" + param);
			}
		}
		return result.toString();
	}

	public List<String> getCookies() {
		return cookies;
	}

	private void setCookies(List<String> cookies) {
		this.cookies = cookies;
	}

    private String checkLoginResult(String html) {
        String result = "successful";

        Document doc = Jsoup.parse(html);
        String error = doc.select("span#lblError").text();

        if (!error.isEmpty()) {
            result = error;
        }

        return result;
    }
}
