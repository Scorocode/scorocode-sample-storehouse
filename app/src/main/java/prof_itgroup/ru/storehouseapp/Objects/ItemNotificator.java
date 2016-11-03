package prof_itgroup.ru.storehouseapp.Objects;

import android.content.Context;

import prof_itgroup.ru.storehouseapp.Helpers.Helper;
import prof_itgroup.ru.storehouseapp.R;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackSendEmail;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackSendPush;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackSendSms;
import ru.profit_group.scorocode_sdk.Requests.messages.MessageEmail;
import ru.profit_group.scorocode_sdk.Requests.messages.MessagePush;
import ru.profit_group.scorocode_sdk.Requests.messages.MessageSms;
import ru.profit_group.scorocode_sdk.scorocode_objects.Message;
import ru.profit_group.scorocode_sdk.scorocode_objects.Query;

/**
 * Created by Peter Staranchuk on 10/31/16
 */

public class ItemNotificator {

    Context context;
    private String itemId;
    private String itemName;
    private final Message message;

    public ItemNotificator(Context context, String itemId, String itemName) {
        this.context = context;
        this.itemId = itemId;
        this.itemName = itemName;
        this.message = new Message();
    }

    public void notifyPersonalAboutItemSend() {
        sendPushToLoaderPerson();
        sendEmailInAccountingDepartment();
        sendSmsToDeliveryPerson();
    }

    private void sendSmsToDeliveryPerson() {
        Query query = new Query("roles");
        query.equalTo("name", "deliveryPerson");
        query.equalTo("isFree", true);
        query.setLimit(1);

        MessageSms messageSms = new MessageSms(context.getString(R.string.take_item) + getItemInfo());
        message.sendSms(messageSms, query, new CallbackSendSms() {
            @Override
            public void onSmsSended() {
                Helper.showToast(context, R.string.sms_was_sended);
            }

            @Override
            public void onSmsSendFailed(String errorCode, String errorMessage) {
                Helper.showToast(context, R.string.cant_send_sms);
            }
        });

    }

    private void sendEmailInAccountingDepartment() {
        Query query = new Query("roles");
        query.equalTo("name", "accountantPerson");

        MessageEmail messageEmail = new MessageEmail(context.getString(R.string.from), context.getString(R.string.device) + getItemInfo() , context.getString(R.string.device) + getItemInfo() + context.getString(R.string.sold));
        message.sendEmail(messageEmail, query, new CallbackSendEmail() {
            @Override
            public void onEmailSend() {
                Helper.showToast(context, R.string.email_was_sended);
            }

            @Override
            public void onEmailSendFailed(String errorCode, String errorMessage) {
                Helper.showToast(context, R.string.cant_send_email);
            }
        });
    }

    private void sendPushToLoaderPerson() {
        Query query = new Query("roles");
        query.equalTo("name", "loaderPerson");
        query.equalTo("isFree", true);
        query.setLimit(1);

        MessagePush messagePush = new MessagePush(context.getString(R.string.you_should_take) + getItemInfo() + context.getString(R.string.and_prepare), null);
        message.sendPush(messagePush, query, new CallbackSendPush() {
            @Override
            public void onPushSended() {
                Helper.showToast(context, R.string.push_sended);
            }

            @Override
            public void onPushSendFailed(String errorCode, String errorMessage) {
                Helper.showToast(context, R.string.cant_send_push);
            }
        });
    }

    private String getItemInfo() {
        return itemId +" (" + itemName + ")";
    }
}
