package ca.chekit.android.model;

import java.util.List;

import org.json.JSONObject;

import ca.chekit.android.util.Utilities;

public class Division {
	
	public static final String ID = "Id";
	public static final String NAME = "Name";
	public static final String IS_ACTIVE = "IsActive";
	public static final String ICON = "Icon";
	public static final String COLOR = "Color";
	public static final String COMPANY_ID = "CompanyId";
	
	private long id;
	private String name;
	private boolean isActive;
	private Object icon;
	private String color;
	private long companyId;
	
	private static List<Division> divisions;
	
	public Division() {}
	
	public Division(JSONObject obj) {
		this.id = obj.optLong(ID);
		this.name = obj.optString(NAME);
		this.isActive = obj.optBoolean(IS_ACTIVE);
		this.icon = obj.opt(ICON);
		this.color = obj.optString(COLOR);
		this.companyId = obj.optLong(COMPANY_ID);
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public Object getIcon() {
		return icon;
	}
	
	public void setIcon(Object icon) {
		this.icon = icon;
	}
	
	public String getColor() {
		return color;
	}
	
	public void setColor(String color) {
		this.color = color;
	}
	
	public long getCompanyId() {
		return companyId;
	}
	
	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}
	
	public static List<Division> getDivisionList() {
		return divisions;
	}
	
	public static void setDivisionList(List<Division> list) {
		divisions = list;
	}
	
	public static Division getDivisionById(long id) {
		if (Utilities.isEmpty(divisions)) {
			return null;
		}
		for (Division division : divisions) {
			if (division.getId() == id) {
				return division;
			}
		}
		return null;
	}

}
