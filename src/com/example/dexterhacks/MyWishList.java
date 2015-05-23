package com.example.dexterhacks;

import java.util.ArrayList;
import java.util.HashMap;

import net.sourceforge.zbar.android.CameraTest.R;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MyWishList extends Activity {
	InboxAdapter listAdapter;
	ArrayList<NotificationItems>inboxfeeds=null;
	
	private final static String DB_NAME = "MyWishList";
	private final static String TABLE_NAME = "my_wishlist";
	
	SQLiteDatabase database = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_wishlist);
		
		inboxfeeds = new ArrayList<NotificationItems>();
		
		/*for(int i=0; i<5; i++) {
			NotificationItems item=new NotificationItems();
	        item.setTitle("title "+i);
	 		item.setDescription("desc "+i);
	 		inboxfeeds.add(item);
		}*/
		
		try {
			database = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
			
			String sqlstr="select val_type, value, scan_date from "+ TABLE_NAME +" order by id desc";
			Cursor cursorobj = database.rawQuery(sqlstr,null);
			
			if(cursorobj!=null)
			{
				cursorobj.moveToFirst();
				
			    for (int i = 0; i < cursorobj.getCount(); i++) 
			    {
			    	NotificationItems item=new NotificationItems();
			        item.setTitle(cursorobj.getString(0));
			 		item.setDescription(cursorobj.getString(1));
			 		item.setPubDate(cursorobj.getString(2));
					
			 		inboxfeeds.add(item);
					cursorobj.moveToNext();
			    }
				
				cursorobj.close();
				cursorobj=null;
			
			}
			database.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			database.close();
		}
		
		ListView  myInboxList = (ListView) findViewById( R.id.myInboxList);
		listAdapter =   new InboxAdapter(MyWishList.this, inboxfeeds);
	    myInboxList.setAdapter(listAdapter);
	}
	
	public class InboxAdapter extends BaseAdapter 
	{
		private LayoutInflater mInflater;
		private Context cont;
		int layout_xml;

		//int logoidname;
		ArrayList<NotificationItems> arr;
		private SparseBooleanArray mSelectedItemsIds;

		public InboxAdapter(Context context, ArrayList<NotificationItems> arr) 
		{
			this.cont = context;
			this.arr = arr;
			mInflater = LayoutInflater.from(context);
			//mSelectedItemsIds = new SparseBooleanArray();
		}
		public View getView(final int position, View convertView, ViewGroup parent) 
		{
			boolean b=false;
			if (convertView == null) {
				b=true;
				layout_xml = R.layout.row_inbox_listview;
				convertView = mInflater.inflate(layout_xml, null);
				
				ImageView ImgIcon = (ImageView) convertView.findViewById(R.id.ImgIcon);
				
				TextView title = (TextView) convertView.findViewById(R.id.title);
				String type ="";
				if(arr.get(position).getTitle().equals("64")) {
					type = "QR Code";
					ImgIcon.setImageResource(R.drawable.qr_code_icon);
				}
				else {
					ImgIcon.setImageResource(R.drawable.barcode_icon);
					type = "Barcode";
				}
				title.setText(type);
				
				TextView desc = (TextView) convertView.findViewById(R.id.desc);
				desc.setText(Html.fromHtml(arr.get(position).getDescription()));
				desc.setAutoLinkMask(Linkify.WEB_URLS);
				
				TextView scan_date = (TextView) convertView.findViewById(R.id.scan_date);
				scan_date.setText(arr.get(position).getPubDate());
				
			}
			
			return convertView;
		}
		
		public void toggleSelection(int position) {
			selectView(position, !mSelectedItemsIds.get(position));
			
		}
	 
		public void removeSelection() {
			mSelectedItemsIds = new SparseBooleanArray();
			notifyDataSetChanged();
		}
	 
		public void selectView(int position, boolean value) {
			if (value)
				mSelectedItemsIds.put(position, value);
			else
				mSelectedItemsIds.delete(position);
			notifyDataSetChanged();
		}
		/*public void remove(NotificationItems object) {
			inboxfeeds.remove(object);
			notifyDataSetChanged();
		}

		@Override
		public NotificationItems getItem(int position) 
		{
			return arr.get(position);
		}
		
		public SparseBooleanArray getSelectedIds() {
			return mSelectedItemsIds;
		}*/

		@Override
		public long getItemId(int position) 
		{
			//return position;
			return position;
		}

		@Override
		public int getCount() 
		{
			return arr.size();
			// return data.length;
			// return 1;
		}
		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		/*@Override
		public Object getItem(int position) 
		{
			return arr.get(position);
		}*/
	}

}
