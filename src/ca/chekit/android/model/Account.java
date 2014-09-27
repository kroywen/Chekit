package ca.chekit.android.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import ca.chekit.android.util.Utilities;

public class Account implements Serializable {
	
	private static final long serialVersionUID = 3204177452962643465L;
	public static final String FILENAME = Account.class.getSimpleName();
	
	public static final String ID = "Id";
	public static final String FIRST_NAME = "FirstName";
	public static final String LAST_NAME = "LastName";
	public static final String DESCRIPTION = "Description";
	public static final String ADDRESS1 = "Address1";
	public static final String ADDRESS2 = "Address2";
	public static final String CITY = "City";
	public static final String ZIP_CODE = "ZipCode";
	public static final String IS_MOBILE = "IsMobile";
	public static final String COMPANY_ID = "CompanyId";
	public static final String STATE_PROVINCE_ID = "StateProvinceId";
	
	public static final String ADMIN = "Admin";
	public static final String SCHEDULER = "Scheduler";
	public static final String ASSIGNEE = "Assignee";
	public static final String MANAGER = "Manager";
	
	private long id;
	private String firstName;
	private String lastName;
	private String description;
	private String address1;
	private String address2;
	private String city;
	private String zipCode;
	private boolean isMobile;
	private long companyId;
	private long stateProvinceId;
	
	private static Account current;
	private static List<Account> contacts;
	
	public Account() {}
	
	public Account(JSONObject obj) {
		this.id = obj.optLong(ID);
		this.firstName = obj.optString(FIRST_NAME);
		this.lastName = obj.optString(LAST_NAME);
		this.description = obj.optString(DESCRIPTION);
		this.address1 = obj.optString(ADDRESS1);
		this.address2 = obj.optString(ADDRESS2);
		this.city = obj.optString(CITY);
		this.zipCode = obj.optString(ZIP_CODE);
		this.isMobile = obj.optBoolean(IS_MOBILE);
		this.companyId = obj.optLong(COMPANY_ID);
		this.stateProvinceId = obj.optLong(STATE_PROVINCE_ID);
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getAddress1() {
		return address1;
	}
	
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	
	public String getAddress2() {
		return address2;
	}
	
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getZipCode() {
		return zipCode;
	}
	
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	
	public boolean isMobile() {
		return isMobile;
	}
	
	public void setMobile(boolean isMobile) {
		this.isMobile = isMobile;
	}
	
	public long getCompanyId() {
		return companyId;
	}
	
	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}
	
	public long getStateProvinceId() {
		return stateProvinceId;
	}
	
	public void setStateProvinceId(long stateProvinceId) {
		this.stateProvinceId = stateProvinceId;
	}
	
	public String getFullName() {
		return firstName + " " + lastName;
	}
	
	public static Account getCurrent(Context context) {
		if (current == null) {
			loadCurrent(context);
		}
		return current;
	}
	
	public static void setCurrent(Context context, Account account) {
		current = account;
		saveCurrent(context);
	}
	
	public static void loadCurrent(Context context) {
		try {
			FileInputStream fis = context.openFileInput(FILENAME);
			ObjectInputStream ois = new ObjectInputStream(fis);
			current = (Account) ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void saveCurrent(Context context) {
		try {
			FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(current);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static List<Account> getContactList() {
		return contacts;
	}
	
	public static void setContactList(List<Account> list) {
		contacts = list;
	}
	
	public static Account getContactById(long id) {
		if (Utilities.isEmpty(contacts)) {
			return null;
		}
		for (Account contact : contacts) {
			if (contact.getId() == id) {
				return contact;
			}
		}
		return null;
	}
	
	public static Bitmap loadContactIcon(Context context, long contactId) {
		try {
			String filename = "contact_" + contactId + "_icon";
			FileInputStream fis = context.openFileInput(filename);			
			Bitmap bitmap = BitmapFactory.decodeStream(fis, null, null);
			fis.close();
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void saveContactIcon(Context context, long contactId, Bitmap bitmap) {
		try {
			String filename = "contact_" + contactId + "_icon";
			FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			bitmap.compress(CompressFormat.PNG, 100, fos);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
