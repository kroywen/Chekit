package ca.chekit.android.model;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.text.TextUtils;
import ca.chekit.android.R;

public class WorkStatus {
	
	public static final String NEW = "New";
	public static final String ON_ROUTE = "On-Route";
	public static final String ON_SITE = "On-Site";
	public static final String VERIFY_SITE = "Verify Site";
	public static final String BEGINNING_TASK = "Beginning Task";
	public static final String EXPECTED_PROGRESS = "Expected Progress";
	public static final String DELIVERED_OR_SIGNED = "Delivered / Signed";
	public static final String PAYMENT_RECEIVED = "Payment Received";
	public static final String TASK_COMPLETED = "Task Completed";
	public static final String LEAVE_SITE = "Leave Site";
	public static final String SLOW_PROGRESS = "Slow Progress";
	public static final String OFFSITE_REQUIRED = "Offsite Required";
	public static final String SAFETY_ISSUE = "Safety Issue";
	public static final String TECHNICAL_OR_PRODUCT_ISSUES = "Technical / Product Issues";
	public static final String REASSESS_REQUIREMENTS = "Reassess Requirements";
	public static final String TEMPORARY_WORK_STOP = "Temporary Work Stop";
	public static final String STOP_PROGRESS = "Stop Progress";
	public static final String DANGEROUS_SITUATION = "Dangerous Situation";
	public static final String SITE_ABANDONED = "Site Abandoned";
	public static final String REASSIGN_TASK = "ReAssign Task";
	public static final String DELIVERY_INCOMPLETE = "Delivery Incomplete";
	public static final String TASK_ISSUES_IRRESOLVABLE = "Task Issues Irresolvable";
	
	public static final String GREEN = "Tasks Good";
	public static final String YELLOW = "Tasks Delayed";
	public static final String RED = "Tasks Stopped";
	public static final String NO_STATUS = "No status";
	
	private String name;
	private String postName;
	private int icon;
	private int iconWithBackground;
	private int background;
	private int order;
	private String colorName;
	private int pinBackground;
	
	private static List<WorkStatus> statuses;
	
	public WorkStatus(String name, String postName, 
		int iconWithBackground, int icon, int background, 
		int order, String colorName, int pinBackground)
	{
		this.name = name;
		this.postName = postName;
		this.icon = icon;
		this.iconWithBackground = iconWithBackground;
		this.background = background;
		this.order = order;
		this.colorName = colorName;
		this.pinBackground = pinBackground;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPostName() {
		return postName;
	}
	
	public void setPostName(String postName) {
		this.postName = postName;
	}
	
	public int getIcon() {
		return icon;
	}
	
	public void setIcon(int icon) {
		this.icon = icon;
	}
	
	public int getIconWithBackground() {
		return iconWithBackground;
	}
	
	public void setIconWithBackground(int iconWithBackground) {
		this.iconWithBackground = iconWithBackground;
	}
	
	public int getBackground() {
		return background;
	}
	
	public void setBackground(int background) {
		this.background = background;
	}
	
	public int getOrder() {
		return order;
	}
	
	public void setOrder(int order) {
		this.order = order;
	}
	
	public String getColorName() {
		return colorName;
	}
	
	public void setColorName(String colorName) {
		this.colorName = colorName;
	}
	
	public int getPinBackground() {
		return pinBackground;
	}
	
	public void setPinBackground(int pinBackground) {
		this.pinBackground = pinBackground;
	}
	
	static {
		statuses = new ArrayList<WorkStatus>();
		statuses.add(WorkStatus.forName(NEW));
		statuses.add(WorkStatus.forName(ON_ROUTE));
		statuses.add(WorkStatus.forName(ON_SITE));
		statuses.add(WorkStatus.forName(VERIFY_SITE));
		statuses.add(WorkStatus.forName(BEGINNING_TASK));
		statuses.add(WorkStatus.forName(EXPECTED_PROGRESS));
		statuses.add(WorkStatus.forName(DELIVERED_OR_SIGNED));
		statuses.add(WorkStatus.forName(PAYMENT_RECEIVED));
		statuses.add(WorkStatus.forName(TASK_COMPLETED));
		statuses.add(WorkStatus.forName(LEAVE_SITE));
		statuses.add(WorkStatus.forName(SLOW_PROGRESS));
		statuses.add(WorkStatus.forName(OFFSITE_REQUIRED));
		statuses.add(WorkStatus.forName(SAFETY_ISSUE));
		statuses.add(WorkStatus.forName(TECHNICAL_OR_PRODUCT_ISSUES));
		statuses.add(WorkStatus.forName(REASSESS_REQUIREMENTS));
		statuses.add(WorkStatus.forName(TEMPORARY_WORK_STOP));
		statuses.add(WorkStatus.forName(STOP_PROGRESS));
		statuses.add(WorkStatus.forName(DANGEROUS_SITUATION));
		statuses.add(WorkStatus.forName(SITE_ABANDONED));
		statuses.add(WorkStatus.forName(REASSIGN_TASK));
		statuses.add(WorkStatus.forName(DELIVERY_INCOMPLETE));
		statuses.add(WorkStatus.forName(TASK_ISSUES_IRRESOLVABLE));
	}
	
	public static WorkStatus forName(String name) {
		if (TextUtils.isEmpty(name)) {
			return null;
		}
		if (name.equalsIgnoreCase(NEW)) {
			return new WorkStatus(NEW, NEW, 0, 0, 0, -1, NO_STATUS, 0);
		} else if (name.equalsIgnoreCase(ON_ROUTE)) {
			return new WorkStatus(ON_ROUTE, "OnRoute", R.drawable.status_on_route, R.drawable.status_on_route_icon, 
				R.drawable.info_window_background_green, 0, GREEN, R.drawable.pin_green_background);
		} else if (name.equalsIgnoreCase(ON_SITE)) {
			return new WorkStatus(ON_SITE, "OnSite", R.drawable.status_on_site, R.drawable.status_on_site_icon, 
				R.drawable.info_window_background_green, 1, GREEN, R.drawable.pin_green_background);
		} else if (name.equalsIgnoreCase(VERIFY_SITE)) {
			return new WorkStatus(VERIFY_SITE, "VerifySite", R.drawable.status_verify_site, 
				R.drawable.status_verify_site_icon, R.drawable.info_window_background_green, 2, GREEN,
				R.drawable.pin_green_background);
		} else if (name.equalsIgnoreCase(BEGINNING_TASK)) {
			return new WorkStatus(BEGINNING_TASK, "BeginningTask", R.drawable.status_beginning_task, 
				R.drawable.status_beginning_task_icon, R.drawable.info_window_background_green, 3, GREEN,
				R.drawable.pin_green_background);
		} else if (name.equalsIgnoreCase(EXPECTED_PROGRESS)) {
			return new WorkStatus(EXPECTED_PROGRESS, "ExpectedProgress", R.drawable.status_expected_progress, 
				R.drawable.status_expected_progress_icon, R.drawable.info_window_background_green, 4, GREEN,
				R.drawable.pin_green_background);
		} else if (name.equalsIgnoreCase(DELIVERED_OR_SIGNED)) {
			return new WorkStatus(DELIVERED_OR_SIGNED, "DeliveredOrSigned", R.drawable.status_delivered_or_signed, 
				R.drawable.status_delivered_or_signed_icon, R.drawable.info_window_background_green, 5, GREEN,
				R.drawable.pin_green_background);
		} else if (name.equalsIgnoreCase(PAYMENT_RECEIVED)) {
			return new WorkStatus(PAYMENT_RECEIVED, "PaymentReceived", R.drawable.status_payment_received, 
				R.drawable.status_payment_received_icon, R.drawable.info_window_background_green, 6, GREEN,
				R.drawable.pin_green_background);
		} else if (name.equalsIgnoreCase(TASK_COMPLETED)) {
			return new WorkStatus(TASK_COMPLETED, "TaskCompleted", R.drawable.status_task_completed, 
				R.drawable.status_task_completed_icon, R.drawable.info_window_background_green, 7, GREEN,
				R.drawable.pin_green_background);
		} else if (name.equalsIgnoreCase(LEAVE_SITE)) {
			return new WorkStatus(LEAVE_SITE, "LeaveSite", R.drawable.status_leave_site, 
				R.drawable.status_leave_site_icon, R.drawable.info_window_background_green, 8, GREEN,
				R.drawable.pin_green_background);
		} else if (name.equalsIgnoreCase(SLOW_PROGRESS)) {
			return new WorkStatus(SLOW_PROGRESS, "SlowProgress", R.drawable.status_slow_progress, 
				R.drawable.status_slow_progress_icon, R.drawable.info_window_background_yellow, 9, YELLOW,
				R.drawable.pin_yellow_background);
		} else if (name.equalsIgnoreCase(OFFSITE_REQUIRED)) {
			return new WorkStatus(OFFSITE_REQUIRED, "OffsiteRequired", R.drawable.status_offsite_required, 
				R.drawable.status_offsite_required_icon, R.drawable.info_window_background_yellow, 10, YELLOW,
				R.drawable.pin_yellow_background);
		} else if (name.equalsIgnoreCase(SAFETY_ISSUE)) {
			return new WorkStatus(SAFETY_ISSUE, "SafetyIssue", R.drawable.status_safety_issue, 
				R.drawable.status_safety_issue_icon, R.drawable.info_window_background_yellow, 11, YELLOW,
				R.drawable.pin_yellow_background);
		} else if (name.equalsIgnoreCase(TECHNICAL_OR_PRODUCT_ISSUES)) {
			return new WorkStatus(TECHNICAL_OR_PRODUCT_ISSUES, "TechnicalOrProductIssues", 
				R.drawable.status_technical_or_product_issues, R.drawable.status_technical_or_product_issues_icon, 
				R.drawable.info_window_background_yellow, 12, YELLOW, R.drawable.pin_yellow_background);
		} else if (name.equalsIgnoreCase(REASSESS_REQUIREMENTS)) {
			return new WorkStatus(REASSESS_REQUIREMENTS, "ReassessRequirements", R.drawable.status_reassess_requirements, 
				R.drawable.status_reassess_requirements_icon, R.drawable.info_window_background_yellow, 13, YELLOW,
				R.drawable.pin_yellow_background);
		} else if (name.equalsIgnoreCase(TEMPORARY_WORK_STOP)) {
			return new WorkStatus(TEMPORARY_WORK_STOP, "TemporaryWorkStop", R.drawable.status_temporary_work_stop, 
				R.drawable.status_temporary_work_stop_icon, R.drawable.info_window_background_yellow, 14, YELLOW,
				R.drawable.pin_yellow_background);
		} else if (name.equalsIgnoreCase(STOP_PROGRESS)) {
			return new WorkStatus(STOP_PROGRESS, "StopProgress", R.drawable.status_stop_progress, 
				R.drawable.status_stop_progress_icon, R.drawable.info_window_background_red, 15, RED,
				R.drawable.pin_red_background);
		} else if (name.equalsIgnoreCase(DANGEROUS_SITUATION)) {
			return new WorkStatus(DANGEROUS_SITUATION, "DangerousSituation", R.drawable.status_dangerous_situation, 
				R.drawable.status_dangerous_situation_icon, R.drawable.info_window_background_red, 16, RED,
				R.drawable.pin_red_background);
		} else if (name.equalsIgnoreCase(SITE_ABANDONED)) {
			return new WorkStatus(SITE_ABANDONED, "SiteAbandoned", R.drawable.status_site_abandoned, 
				R.drawable.status_site_abandoned_icon, R.drawable.info_window_background_red, 17, RED,
				R.drawable.pin_red_background);
		} else if (name.equalsIgnoreCase(REASSIGN_TASK)) {
			return new WorkStatus(REASSIGN_TASK, "ReAssignTask", R.drawable.status_reassign_task, 
				R.drawable.status_reassign_task_icon, R.drawable.info_window_background_red, 18, RED,
				R.drawable.pin_red_background);
		} else if (name.equalsIgnoreCase(DELIVERY_INCOMPLETE)) {
			return new WorkStatus(DELIVERY_INCOMPLETE, "DeliveryIncomplete", R.drawable.status_delivery_incomplete, 
				R.drawable.status_delivery_incomplete_icon, R.drawable.info_window_background_red, 19, RED,
				R.drawable.pin_red_background);
		} else if (name.equalsIgnoreCase(TASK_ISSUES_IRRESOLVABLE)) {
			return new WorkStatus(TASK_ISSUES_IRRESOLVABLE, "TaskIssuesIrresolvable", 
				R.drawable.status_task_issues_irresolvable, R.drawable.status_task_issues_irresolvable_icon, 
				R.drawable.info_window_background_red, 20, RED, R.drawable.pin_red_background);
		} else {
			return null;
		}
	}
	
	public static List<WorkStatus> getStatusList() {
		return statuses;
	}
	
	public static int getColorForName(String name) {
		int red = GREEN.equals(name) ? 35 : YELLOW.equals(name) ? 253 : RED.equals(name) ? 255 : 255;
		int green = GREEN.equals(name) ? 185 : YELLOW.equals(name) ? 185 : RED.equals(name) ? 54 : 255;
		int blue = GREEN.equals(name) ? 19 : YELLOW.equals(name) ? 19 : RED.equals(name) ? 0 : 255;
		return Color.rgb(red, green, blue);
	}

}
