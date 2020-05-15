package com.signakey.sktrack.skclient;

import android.util.Log;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

public class WebMarkAuthenticateResult extends BaseObject {
	public WebResult Common;
	public Boolean RecoverableFailure;
	public Integer TargetIndex;
	public ArrayList<DemoItem> ItemList;
	Logger logger;
	Date date = new Date() ;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;

	public Object getProperty(int index) {

		Log.e("","WebMarkAuthenticateResult: getProperty:  index "+index);

		switch (index) {
		case 0:
			return Common;
		case 1:
			return RecoverableFailure;
		case 2:
			return TargetIndex;
		case 3:
			return ItemList;
		}
		return null;
	}

	public int getPropertyCount() {
		return 4; // was 6
	}

	public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable properties,
			PropertyInfo info) {
		Log.e("","WebMarkAuthenticateResult: getPropertyInfo:  index "+index);

		Log.i("SKScanner", "WebAuthenticateResult: getPropertyInfo ");
		switch (index) {
		case 0:
			info.name = "Common";
			info.type = WebResult.class;
			break;
		case 1:
			info.name = "RecoverableFailure";
			info.type = Boolean.class;
			break;
		case 2:
			info.name = "TargetIndex";
			info.type = Integer.class;
			break;
		case 3:
			info.name = "ItemList";
			info.type = SoapObject.class;
			break;

		default:
			break;
		}
	}

	public void setProperty(int index, Object value) {

		Log.e("","WebMarkAuthenticateResult: setProperty:  index "+index);

		switch (index) {
		case 0:
			Common = (WebResult) value;
			Log.i("SKScanner",
					"WebMarkAuthenticateResult setProperty Common = " + Common);
			break;
		case 1:
			RecoverableFailure = (Boolean) value;
			Log.i("SKScanner",
					"WebMarkAuthenticateResult setProperty RecoverableFailure = "
							+ RecoverableFailure);
			break;
		case 2:
			TargetIndex = (Integer) value;
			Log.i("SKScanner",
					"WebMarkAuthenticateResult setProperty TargetIndex = "
							+ TargetIndex.toString());
			break;
		case 3:
			ItemList = GetItemLists(value);
			Log.i("SKScanner",
					"WebMarkAuthenticateResult setProperty ItemList = "
							+ ItemList.size());
			break;

		default:
			break;
		}
	}

	private String GetNullableString(SoapObject entry, String name) {

		Log.e("","WebMarkAuthenticateResult: GetNullableString:  name "+name);

		try {
			Object found = entry.getProperty(name);
			if (found == null) {
				return "";
			} else {
				return found.toString();
			}
		} catch (Exception ex) {

			return "";

		}

	}

	private ArrayList<DemoItem> GetItemLists(Object value) {
		// Log.i("SKScanner",
		// "WebMarkAuthenticateResult setProperty PublicFileList value = " +
		// value);
		SoapObject stuff = (SoapObject) value;
		ArrayList<DemoItem> pojos = null;
		int pcount = stuff.getPropertyCount(); // Check to see what is attached

		Log.i("SKScanner",
				"WebMarkAuthenticateResult setProperty  GetItemLists:  attachedFiles count: "+pcount);
		if (pcount > 0) {

			pojos = new ArrayList<DemoItem>();
			for (int pcount1 = 0; pcount1 < pcount; pcount1++) { // process the
																	// number of
																	// media
																	// files
				SoapObject p = (SoapObject) stuff.getProperty(pcount1);
				DemoItem m = new DemoItem();
				m.KeyText = GetNullableString(p, "KeyText");
				logger.addLog(dateFormat.format(date) + ": " +"SKScanner: " + "WebMarkAuthenticateSymbols responce,ItemList:  KeyText: " + m.KeyText);
				m.IsContainer = Boolean.parseBoolean(p.getProperty(
						"IsContainer").toString());
				logger.addLog("IsContainer: " + m.IsContainer);

				m.IsContained = Boolean.parseBoolean(p.getProperty(
						"IsContained").toString());
				logger.addLog("IsContained: " + m.IsContained);

				m.PublicText = GetNullableString(p, "PublicText");
				logger.addLog("PublicText: " + m.PublicText);

				m.ConsumerText = GetNullableString(p, "ConsumerText");
				logger.addLog("ConsumerText: " + m.ConsumerText);

				m.RestrictedText = GetNullableString(p, "RestrictedText");
				logger.addLog("RestrictedText: " + m.RestrictedText);

				m.PrivateText = GetNullableString(p, "PrivateText");
				logger.addLog("PrivateText: " + m.PrivateText);

				m.PublicFileList = GetMediaFiles(p, "PublicFileList");
				logger.addLog("PublicFileList: " + m.PublicFileList);

				m.ConsumerFileList = GetMediaFiles(p, "ConsumerFileList");
				logger.addLog("ConsumerFileList: " + m.ConsumerFileList);

				m.RestrictedFileList = GetMediaFiles(p, "RestrictedFileList");
				logger.addLog("RestrictedFileList: " + m.RestrictedFileList);

				m.PrivateFileList = GetMediaFiles(p, "PrivateFileList");
				logger.addLog("PrivateFileList: " + m.PrivateFileList);

				m.DecodeCount = Integer.parseInt(p.getProperty("DecodeCount")
						.toString());
				logger.addLog("DecodeCount: " + m.DecodeCount);

				m.DecodeOldest = GetNullableString(p, "DecodeOldest");
				logger.addLog("DecodeOldest: " + m.DecodeOldest);

				m.DecodeNewest = GetNullableString(p, "DecodeNewest");
				logger.addLog("DecodeNewest: " + m.DecodeNewest);

                Log.i("SKScanner",
                        "WebMarkAuthenticateResult setProperty " +
								"\nConsumerText = " + m.ConsumerText+
								"\nDecodeNewest = " + m.DecodeNewest+
								"\nDecodeOldest = " + m.DecodeOldest+
								"\nKeyText = " + m.KeyText+
								"\nPrivateText = " + m.PrivateText+
								"\nPublicText = " + m.PublicText+
								"\nRestrictedText = " + m.RestrictedText+
								"\nDecodeCount = " + m.DecodeCount+
								"\nIsContained = " + m.IsContained+
								"\nIsContainer = " + m.IsContainer+
								"\nConsumerFileList = " + m.ConsumerFileList+
								"\nPrivateFileList = " + m.PrivateFileList+
								"\nPublicFileList = " + m.PublicFileList+
								"\nRestrictedFileList = " + m.RestrictedFileList
				);
				pojos.add(m);

			}
		}
		return pojos;
	}

	private ArrayList<MediaFile> GetMediaFiles(SoapObject entry, String name) {
		// Log.i("SKScanner",
		// "WebMarkAuthenticateResult setProperty PublicFileList value = " +
		// value);
		// SoapObject stuff = (SoapObject) value;
		ArrayList<MediaFile> pojos = null;

		try {
			Object found = entry.getProperty(name);
			if (found != null) {
				SoapObject stuff = (SoapObject) found;
				int pcount = stuff.getPropertyCount(); // Check to see if media
														// is attached
				Log.e("","WebMarkAuthenticateResult: GetMediaFiles: Propertyname: "+name+"; mediaFiles count: "+pcount);

				if (pcount > 0) {

					pojos = new ArrayList<MediaFile>();
					for (int pcount1 = 0; pcount1 < pcount; pcount1++) { // process
																			// the
																			// number
																			// of
																			// media
																			// files
						SoapObject p = (SoapObject) stuff.getProperty(pcount1);
						MediaFile m = new MediaFile();
						m.Identifier = p.getProperty("Identifier").toString();

						logger.addLog("MediaFile:  Identifier: " + m.Identifier);
						m.URL = p.getProperty("URL").toString();
						logger.addLog("MediaFile:  URL: " + m.URL);
						m.Medium = Integer.parseInt(p.getProperty("Medium")
								.toString());
						logger.addLog("MediaFile:  Medium: " + m.Medium);
						m.Order = Integer.parseInt(p.getProperty("Order")
								.toString());
						logger.addLog("MediaFile:  Order: " + m.Order);
						m.Visibility = Integer.parseInt(p.getProperty(
								"Visibility").toString());
						logger.addLog("MediaFile:  Visibility: " + m.Visibility);
						pojos.add(m);

					}
				}

			}
		} catch (Exception ex) {
			Log.e("","WebMarkAuthenticateResult: GetMediaFiles: Propertyname: "+name+"; Exception: "+ex);
		}

		return pojos;
	}

	public void register(SoapSerializationEnvelope envelope) {
		envelope.addMapping(NAMESPACE, "WebDemoLookupSymbolsContainedResult",
				this.getClass());
		new WebResult().register(envelope);
		new ArrayOfDemoItem().register(envelope);
		new DemoItem().register(envelope);
		new ArrayOfMediaFile().register(envelope);
		new MediaFile().register(envelope);
	}

}
