package com.test.contest.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 获取通讯录的工具类
 */
public class ContactUtil {

    /**
     * 得到本地联系人信息
     * @param context 上下文
     */
    public static void getLocalContactsInfos(Map<String, List<String>> clients, Context context) {
        ContentResolver cr = context.getContentResolver();
        String str[] = { ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_ID };
        Cursor cur = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, str, null,
                null, null);

        if (cur != null) {
            while (cur.moveToNext()) {
                String name = cur.getString(cur
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNum = cur.getString(cur
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)); // 得到手机号码
                if (!clients.containsKey(name)) {
                    clients.put(name, new ArrayList<String>());
                    clients.get(name).add(phoneNum);
                } else {
                    clients.get(name).add(phoneNum);
                }
            }
            cur.close();
        }
    }


    /**
     * 获取SIM卡的联系人
     * @param context 上下文
     */
    public static void getSIMContactsInfos(Map<String, List<String>> clients, Context context) {
        ContentResolver cr = context.getContentResolver();
        final String SIM_URI_ADN = "content://icc/adn"; // SIM卡
        Uri uri = Uri.parse(SIM_URI_ADN);
        Cursor cursor = cr.query(uri, null, null, null, null);
        if(null != cursor) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String phoneNum = cursor.getString(cursor.getColumnIndex("number"));
                if (!clients.containsKey(name)) {
                    clients.put(name, new ArrayList<String>());
                    clients.get(name).add(phoneNum);
                } else {
                    clients.get(name).add(phoneNum);
                }
            }
            cursor.close();
        }
    }

}
