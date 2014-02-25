package personal.john.app;

import java.io.IOException;

import org.xml.sax.SAXException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

public class RakutenClientExecuteThread extends AsyncTask<RakutenClient, Void, Void> {
	private MainActivity mActivity ;
    RakutenClient mRc;

    public RakutenClientExecuteThread(MainActivity activity) {
        // 呼び出し元のアクティビティ
    	mActivity = activity;
    }

	@Override
	protected Void doInBackground(RakutenClient... params) {
		mRc = params[0];
		try {
			mRc.requestHotel();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		mActivity.updateMarker();
		if (0 == mRc.getRecordCount()) {
			new AlertDialog.Builder(mActivity)
			.setTitle("検索結果なし")
			.setMessage("近場にホテルがありません。検索範囲を広げるか、移動して再度検索を行ってください。")
			.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int id) {
			        dialog.cancel();
			   }
			})
			.show();
		}
	}
}
