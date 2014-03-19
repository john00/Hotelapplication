
package personal.john.app;

import android.graphics.Bitmap;

public class MyCustomListData {
    private Bitmap bitmap;

    private String message;

    public void setBitmap(Bitmap img) {
        bitmap = img;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setMessage(String msg) {
        message = msg;
    }

    public String getMessage() {
        return message;
    }
}
