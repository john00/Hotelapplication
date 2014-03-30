
package personal.john.app;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyCustomListAdapter extends ArrayAdapter<MyCustomListData> {
    private LayoutInflater layoutInflater;

    public MyCustomListAdapter(Context context, int viewResourceId, List<MyCustomListData> objects) {
        super(context, viewResourceId, objects);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cv = convertView;
        
        // 特定の行(position)のデータを得る
        MyCustomListData item = (MyCustomListData) getItem(position);

        // リスト用のレイアウトを初回のみ作成
        if (cv == null) {
            cv = layoutInflater.inflate(R.layout.result_listitem, null);
        }

        // イメージ画像のセット
        ImageView imageView = (ImageView) cv.findViewById(R.id.listImg);
        imageView.setImageBitmap(item.getHotelImage());

        // ホテル名のセット
        TextView listNameTextView = (TextView) cv.findViewById(R.id.listHotelName);
        listNameTextView.setText(item.getHotelName());

        // ホテル情報のセット
        TextView listInfoTextView = (TextView) cv.findViewById(R.id.listHotelInfo);
        listInfoTextView.setText(item.getHotelInfo());
        
        // 現在地からホテルまでの距離
        TextView listDistanceTextView = (TextView) cv.findViewById(R.id.listHotelDistance);
        listDistanceTextView.setText(item.getHotelDistance());
        
        return cv;
    }
}
