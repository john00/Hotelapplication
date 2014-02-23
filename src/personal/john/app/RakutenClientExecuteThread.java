package personal.john.app;

import java.io.IOException;

import org.xml.sax.SAXException;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

public class RakutenClientExecuteThread extends AsyncTask<RakutenClient, Void, Void> {
	private MainActivity mActivity ;
    RakutenClient mRc;

    public RakutenClientExecuteThread(MainActivity activity) {
        // �Ăяo�����̃A�N�e�B�r�e�B
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
			.setTitle("�������ʂȂ�")
			.setMessage("�ߏ�Ƀz�e��������܂���B�����͈͂��L���邩�A�ړ����čēx�������s���Ă��������B")
			.setNegativeButton("�L�����Z��", new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int id) {
			        dialog.cancel();
			   }
			})
			.show();
		}
	}
}
