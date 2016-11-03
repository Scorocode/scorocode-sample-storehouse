package prof_itgroup.ru.storehouseapp.Objects;

import android.content.Context;

import prof_itgroup.ru.storehouseapp.Helpers.Helper;
import prof_itgroup.ru.storehouseapp.R;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackSendScript;
import ru.profit_group.scorocode_sdk.scorocode_objects.Script;

/**
 * Created by Peter Staranchuk on 10/31/16
 */

public class BalanceNotificator {
    private Context context;

    public BalanceNotificator(Context context) {
        this.context = context;
    }

    public void refreshCompanyBalance() {
        Script script = new Script();
        script.runScript("5800ad9342d52f1ba275fbcd", new CallbackSendScript() {
            @Override
            public void onScriptSended() {
                Helper.showToast(context, R.id.balance_refreshed);
            }

            @Override
            public void onScriptSendFailed(String errorCode, String errorMessage) {
                Helper.showToast(context, R.id.can_refresh_balance);
            }
        });
    }
}
