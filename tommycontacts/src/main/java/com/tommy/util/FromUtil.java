package com.tommy.util;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;

public class FromUtil {

	public static void doImportFromSim(Context context, Account account) {
		// if (account != null) {
		// GoogleSource.createMyContactsIfNotExist(account, context);
		// }

		Intent importIntent = new Intent(Intent.ACTION_VIEW);
		importIntent.setType("vnd.android.cursor.item/sim-contact");
		if (account != null) {
			importIntent.putExtra("account_name", account.name);
			importIntent.putExtra("account_type", account.type);
		}
		importIntent.setClassName("com.android.phone",
				"com.android.phone.SimContacts");
		context.startActivity(importIntent);
	}
}
