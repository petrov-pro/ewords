/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package word.words;

import android.os.AsyncTask;
import android.app.ProgressDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import org.xmlpull.v1.XmlPullParser;
import android.widget.Toast;
import android.content.ContentValues;
import android.util.Log;

/**
 *
 * @author petroff
 */
class MyTask extends AsyncTask<Void, Integer, Void> {

	private static SQLiteDatabase db;
	Context context;
	ProgressDialog mProgressDialog;
	static final int IDD__HORIZONTAL_PROGRESS = 0;
	static final int IDD_WHEEL_PROGRESS = 1;
	private static final String TAG = "MyTask";
	private int dMax = 10000;

	MyTask(Context context, SQLiteDatabase db) {
		super();
		this.context = context;
		this.db = db;
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case IDD__HORIZONTAL_PROGRESS:
				mProgressDialog = new ProgressDialog(
						context);
				mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // устанавливаем стиль
				mProgressDialog.setMessage("Загружаю. Подождите...");  // задаем текст
				return mProgressDialog;

			case IDD_WHEEL_PROGRESS:
				mProgressDialog = new ProgressDialog(
						context);
				mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				mProgressDialog.setMessage("Загружаю. Подождите...");
				return mProgressDialog;

			default:
				return null;
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		onCreateDialog(0);
		mProgressDialog.setMax(dMax);
		mProgressDialog.show();
		mProgressDialog.setProgress(0);
	}

	protected Void doInBackground(Void... params) {

		try {
			XmlPullParser parser = context.getResources().getXml(R.xml.dictionary);
			ContentValues word = new ContentValues();
			ContentValues link = new ContentValues();
			int i = 0;
			while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
				if (parser.getEventType() == XmlPullParser.START_TAG
						&& parser.getName().equals("word")) {
					word.put("_id", parser.getAttributeValue(3));
					word.put("word", parser.getAttributeValue(2));
					word.put("type", parser.getAttributeValue(0));
					db.insert("words", null, word);
				} else if (parser.getEventType() == XmlPullParser.START_TAG
						&& parser.getName().equals("link")) {
					link.put("w1id", parser.getAttributeValue(0));
					link.put("w2id", parser.getAttributeValue(1));
					db.insert("links", null, link);
					link.put("w1id", parser.getAttributeValue(1));
					link.put("w2id", parser.getAttributeValue(0));
					db.insert("links", null, link);
				}

				i++;
				publishProgress(i);

				parser.next();
			}
		} catch (Throwable t) {
			Toast.makeText(context,
					"Ошибка при загрузке XML-документа: " + t.toString(), 4000).show();
		}




		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		mProgressDialog.setProgress(dMax);
		mProgressDialog.cancel();

	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		super.onProgressUpdate();
		mProgressDialog.setProgress(progress[0]);

	}
}