package personal.john.GeoSearcher;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class RakutenClient {
	private static final String LOG_TAG = "RakutenClient";
	private static final String LOG_Hotel = "RakutenClientHotel";

	private static final String API_URI = "http://api.rakuten.co.jp/rws/3.0/rest?";
	private static final String DEV_ID = "developerId=939efa54bb0986afd2f2ca7cfc89ad41";
	private static final String OPERATION = "&operation=SimpleHotelSearch";
	private static final String VERSION = "&version=2009-10-20";
	private static final String LATITUDE = "&latitude=";
	private static final String LONGITUDE = "&longitude=";
	private static final String SEARCHRADIUS = "&searchRadius=";
	private static final String DATUMTYPE = "&datumType=1";
	private static final String HITS = "&hits=30";

	// private static String mAccessKey = null;
	private SAXParser mParser = null;
	private RakutenClientReceiver mRakutenClientReceiver = null;
	private HotelHandler mHotelHandler = null;
	private String mRecordCount = "0";

	public static final int ERROR_GENERAL = 1;
	public static final int ERROR_FATAL = 2;
	public static final int RANGE_300 = 1;
	public static final int RANGE_500 = 2;
	public static final int RANGE_1000 = 3;
	public static final int RANGE_2000 = 4;
	public static final int RANGE_3000 = 5;
	public static final int CATEGORY_LARGE = 0;
	public static final int CATEGORY_SMALL = 1;

	public RakutenClient(RakutenClientReceiver receiver)
			throws ParserConfigurationException, SAXException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		mParser = factory.newSAXParser();
		mRakutenClientReceiver = receiver;
		mHotelHandler = new HotelHandler();
	}

	public void requestHotel(double latitude, double longitude, double range)
			throws SAXException, IOException {
		Log.v(LOG_TAG, "request Hotels.");
		String request = new String(API_URI + DEV_ID + OPERATION + VERSION
				+ LATITUDE + latitude + LONGITUDE + longitude + SEARCHRADIUS
				+ range + DATUMTYPE);
		mParser.parse(request, mHotelHandler);
	}

	public void setRecordCount(String recordcount) {
		mRecordCount = recordcount;
	}

	public int getRecordCount() {
		return Integer.parseInt(mRecordCount);
	}

	class HotelHandler extends DefaultHandler {
		private ArrayList<HotelInfo> mInfoList = null;
		private HotelInfo mInfo = null;
		private String mText = null;
		private boolean mOnCatchText = false;

		public void startDocument() {
			Log.d(LOG_Hotel, "startDocument");
			mInfoList = new ArrayList<HotelInfo>();
		}

		public void endDocument() {
			Log.d(LOG_Hotel, "endDocument");
			mRakutenClientReceiver.receiveHotel(mInfoList);
			mInfoList = null;
		}

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			Log.d(LOG_Hotel, "startElment:uri=" + uri.toString() + ", "
					+ "localName=" + localName + ", qName=" + qName
					+ ",attributes length=" + attributes.getLength());
			if (localName.equals("hotel")) {
				mInfo = new HotelInfo();
			} else if (localName.equals("recordCount")) {
				mOnCatchText = true;
			} else if (localName.equals("hotelNo")) {
				mOnCatchText = true;
			} else if (localName.equals("hotelName")) {
				mOnCatchText = true;
			} else if (localName.equals("hotelKanaName")) {
				mOnCatchText = true;
			} else if (localName.equals("latitude")) {
				mOnCatchText = true;
			} else if (localName.equals("longitude")) {
				mOnCatchText = true;
			} else if (localName.equals("hotelInformationUrl")) {
				mOnCatchText = true;
			} else if (localName.equals("planListUrl")) {
				mOnCatchText = true;
			} else if (localName.equals("telephoneNo")) {
				mOnCatchText = true;
			} else if (localName.equals("hotelSpecial")) {
				mOnCatchText = true;
			} else if (localName.equals("address1")) {
				mOnCatchText = true;
			} else if (localName.equals("address2")) {
				mOnCatchText = true;
			}
		}

		public void endElement(String uri, String localName, String qName) {
			Log.d(LOG_Hotel, "endElement:uri=" + uri.toString()
					+ ", localName=" + localName + ", qName=" + qName);
			if (localName.equals("hotel")) {
				mInfoList.add(mInfo);
			} else if (localName.equals("recordCount")) {
				setRecordCount(mText);
				mOnCatchText = false;
			} else if (localName.equals("hotelNo")) {
				mInfo.setNo(mText);
				mOnCatchText = false;
			} else if (localName.equals("hotelName")) {
				mInfo.setName(mText);
				mOnCatchText = false;
			} else if (localName.equals("hotelKanaName")) {
				mInfo.setKanaName(mText);
				mOnCatchText = false;
			} else if (localName.equals("latitude")) {
				mInfo.setLatitude(mText);
				mOnCatchText = false;
			} else if (localName.equals("longitude")) {
				mInfo.setLongitude(mText);
				mOnCatchText = false;
			} else if (localName.equals("hotelInformationUrl")) {
				mInfo.setInfomationUrl(mText);
				mOnCatchText = false;
			} else if (localName.equals("planListUrl")) {
				mInfo.setPlanListUrl(mText);
				mOnCatchText = false;
			} else if (localName.equals("telephoneNo")) {
				mInfo.setTelephoneNo(mText);
				mOnCatchText = false;
			} else if (localName.equals("hotelSpecial")) {
				mInfo.setSpecial(mText);
				mOnCatchText = false;
			} else if (localName.equals("address1")) {
				mInfo.setAddress1(mText);
				mOnCatchText = false;
			} else if (localName.equals("address2")) {
				mInfo.setAddress2(mText);
				mOnCatchText = false;
			}
		}

		public void characters(char[] ch, int start, int length) {
			if (mOnCatchText) {
				mText = new String(ch, start, length);
				Log.d(LOG_Hotel, "characters:" + mText);
			}
		}

		public void error(SAXParseException e) {
			Log.d(LOG_Hotel, "error:" + e.toString());
			mText = null;
			mInfo = null;
			mInfoList.clear();
			mInfoList = null;
			mRakutenClientReceiver.receiveError(ERROR_GENERAL);
		}

		public void fatalError(SAXParseException e) {
			Log.d(LOG_Hotel, "fatalError:" + e.toString());
			mText = null;
			mInfo = null;
			mInfoList.clear();
			mInfoList = null;
			mRakutenClientReceiver.receiveError(ERROR_GENERAL);
		}

		public void warning(SAXParseException e) {
			Log.d(LOG_Hotel, "warning:" + e.toString());
		}

	}
}