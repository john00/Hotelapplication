package personal.john.GeoSearcher;

import java.util.ArrayList;

public interface RakutenClientReceiver {
	public void receiveHotel(ArrayList<HotelInfo> infoList);
	public void receiveError(int id);
}
