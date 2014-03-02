package personal.john.app;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyCustomListAdapter extends ArrayAdapter<MyCustomListData>{
	private LayoutInflater layoutInflater;
    private Context myContext;

    public MyCustomListAdapter (Context context, int viewResourceId, List<MyCustomListData> objects) {
        super(context, viewResourceId, objects);
        myContext = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //- 特定の行(position)のデータを得る
        MyCustomListData item = (MyCustomListData)getItem(position);

        //- リスト用のレイアウトを初回のみ作成
        if( convertView == null ) {
             convertView = layoutInflater.inflate(R.layout.result_listitem, null);
        }

        LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.listContainer);
        
        //- イメージ画像のセット
        ImageView imageView = (ImageView) convertView.findViewById(R.id.listImg);
        imageView.setImageBitmap(item.getBitmap());
        
        //- メッセージのセット
        TextView listMessageTextView = (TextView) convertView.findViewById(R.id.listMessage);
        listMessageTextView .setText(item.getMessage());
        
        return convertView;
    }
}
