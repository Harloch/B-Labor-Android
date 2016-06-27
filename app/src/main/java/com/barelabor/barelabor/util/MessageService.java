package com.barelabor.barelabor.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import com.barelabor.barelabor.R;
import com.barelabor.barelabor.base.BaseActivity;
import com.barelabor.barelabor.data.DataError;
import com.barelabor.barelabor.debug.Log;

public class MessageService {
	private ProgressDialog progressDialog;
	private AlertDialog alertDialog;

	public static enum DialogType {
		Error, Warning, Info
	}

	public void showDialog(Activity activity, DialogType type, String message, DialogInterface.OnClickListener okListener, final DialogInterface.OnClickListener cancelListener) {
		AlertDialog.Builder builder;

		hideProgressDialog();
		hideAlertDialog();

		Log.e(this, "Show dialog %1$s: %2$s", type, message);

		builder = new AlertDialog.Builder(activity);

		builder.setTitle(getTitle(activity, type));
		builder.setMessage(message);
		builder.setIconAttribute(android.R.attr.alertDialogIcon);
		if (type == DialogType.Warning) {
			builder.setNegativeButton(R.string.button_cancel, cancelListener);
		}
		builder.setPositiveButton(R.string.button_ok, okListener);

		this.alertDialog = builder.create();
		this.alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialogInterface) {
				if (cancelListener != null) {
					cancelListener.onClick(alertDialog, 0);
				}
			}
		});
		this.alertDialog.show();
	}

	public void showError(Activity activity, DataError error, boolean finish) {
		ActivityFinishListener listener = null;
		String message;

		switch (error.getType()) {
		case NoNetwork:
			message = Support.getText(activity, R.string.error_no_network);
			break;
		case ServiceError:
		case NetworkError:
			message = error.getMessage();
			break;
		default:
			message = Support.getText(activity, R.string.error_server);
			break;
		}

		if (finish) {
			listener = new ActivityFinishListener(activity);
		}
		showDialog(activity, DialogType.Error, message, listener, listener);
	}

	public void showError(Activity activity, String message, DialogInterface.OnClickListener listener) {
		showDialog(activity, DialogType.Error, message, listener, listener);
	}

	public void showError(Activity activity, String message, boolean finish) {
		ActivityFinishListener listener = null;

		if (finish) {
			listener = new ActivityFinishListener(activity);
		}

		showDialog(activity, DialogType.Error, message, listener, listener);
	}

	public void showWarning(Activity activity, String message, DialogInterface.OnClickListener onOkListener) {
		showDialog(activity, DialogType.Warning, message, onOkListener, null);
	}

	public void showWarning(Activity activity, String message, DialogInterface.OnClickListener onOkListener, DialogInterface.OnClickListener onCancelListener) {
		showDialog(activity, DialogType.Warning, message, onOkListener, onCancelListener);
	}

	public void showWarning(Activity activity, String message, boolean finish) {
		ActivityFinishListener listener = null;

		if (finish) {
			listener = new ActivityFinishListener(activity);
		}

		showDialog(activity, DialogType.Warning, message, listener, null);
	}

	public void showInfo(Activity activity, String message, boolean finish) {
		ActivityFinishListener listener = null;

		if (finish) {
			listener = new ActivityFinishListener(activity);
		}
		showDialog(activity, DialogType.Info, message, listener, listener);
	}

	private String getTitle(Activity activity, DialogType dialogType) {
		String title;

		switch (dialogType) {
		case Error:
			title = Support.getText(activity, R.string.dialog_error);
			break;
		case Warning:
			title = Support.getText(activity, R.string.dialog_warning);
			break;
		case Info:
			title = Support.getText(activity, R.string.dialog_info);
			break;

		default:
			throw new NotImplementedException();
		}

		return title;
	}

	public void showProgressDialog(final BaseActivity<?> activity, boolean isLoading) {
		if (this.progressDialog == null) {
			Log.i(this, "Show progress: %1$s", isLoading);

			hideAlertDialog();

			this.progressDialog = new ProgressDialog(activity);
			this.progressDialog.setTitle(Support.getText(activity, isLoading ? R.string.dialog_load : R.string.dialog_send));
			this.progressDialog.setMessage(Support.getText(activity, R.string.dialog_wait));
			if (isLoading) {
				this.progressDialog.setCancelable(true);
				this.progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
					public void onCancel(DialogInterface dialogInterface) {
						activity.onBackPressed();
					}
				});
			} else {
				this.progressDialog.setCancelable(false);
			}
			this.progressDialog.show();
		} else {
			Log.i(this, "Progress dialog already shown", new Object[0]);
		}
	}

	public void showProgressDialog(final BaseActivity<?> activity, boolean isLoading, DialogInterface.OnCancelListener onCancellListener) {
		if (this.progressDialog == null) {
			Log.i(this, "Show progress: %1$s", isLoading);

			hideAlertDialog();

			this.progressDialog = new ProgressDialog(activity);
			this.progressDialog.setTitle(Support.getText(activity, isLoading ? R.string.dialog_load : R.string.dialog_send));
			this.progressDialog.setMessage(Support.getText(activity, R.string.dialog_wait));
			if (isLoading) {
				this.progressDialog.setCancelable(true);
				this.progressDialog.setOnCancelListener(onCancellListener);
			} else {
				this.progressDialog.setCancelable(false);
			}
			this.progressDialog.show();
		} else {
			Log.i(this, "Progress dialog already shown", new Object[0]);
		}
	}

	public void hideAllDialogs() {
		hideProgressDialog();
		hideAlertDialog();
	}

	public void hideProgressDialog() {
		if (this.progressDialog != null && this.progressDialog.isShowing()) {
			this.progressDialog.dismiss();
		}

		this.progressDialog = null;
	}

	public void hideAlertDialog() {
		if (this.alertDialog != null && this.alertDialog.isShowing()) {
			this.alertDialog.dismiss();
		}
		this.alertDialog = null;
	}

	private class ActivityFinishListener implements DialogInterface.OnClickListener {
		private Activity activity;

		public ActivityFinishListener(Activity activity) {
			this.activity = activity;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			this.activity.finish();
		}
	}

}
