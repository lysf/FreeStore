package com.snailgame.cjg.friend;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.snailgame.cjg.R;
import com.snailgame.cjg.friend.adapter.FriendContactAdapter;
import com.snailgame.cjg.friend.base.FriendBaseActivity;
import com.snailgame.cjg.friend.model.ContactModel;
import com.snailgame.cjg.friend.model.FriendSearchModel;
import com.snailgame.cjg.network.FSRequestHelper;
import com.snailgame.cjg.util.ActionBarUtils;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.ExtendJsonUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.fastdev.network.IFDResponse;
import com.snailgame.fastdev.util.ListUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import third.com.zhy.base.loadandretry.LoadingAndRetryManager;
import third.com.zhy.base.loadandretry.OnLoadingAndRetryListener;

/**
 * 手机联系人
 * Created by TAJ_C on 2016/5/16.
 */
public class FriendContactActivity extends FriendBaseActivity {//implements LoaderManager.LoaderCallbacks<Cursor>
    private static final String TAG = FriendContactActivity.class.getSimpleName();


    @Bind(R.id.lv_friend_contact)
    ListView friendContactListView;


    public static final int FLAG_SEARCH_PHONE = 1;

    private List<ContactModel> contactList = new ArrayList<>();
    private List<ContactModel> friendList = new ArrayList<>();
    private FriendContactAdapter mAdatper;

    private StringBuffer contactPhones;


    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, FriendContactActivity.class);
        return intent;
    }

    @Override
    protected void handleIntent() {

    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        ActionBarUtils.makeCommonActionbar(this, getSupportActionBar(), getString(R.string.friend_contact_title));
        mAdatper = new FriendContactAdapter(this, friendList);
        friendContactListView.setAdapter(mAdatper);

        mLoadingAndRetryManager = LoadingAndRetryManager.generate(friendContactListView, new OnLoadingAndRetryListener() {
            @Override
            public void setRetryEvent(View retryView) {
                loadData();
            }
        });

    }

    @Override
    protected void loadData() {
        showLoading();
        loadData(FLAG_SEARCH_PHONE, getPhoneContacts());
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_friend_contact;
    }

    private void loadData(int flag, String content) {
        String url = JsonUrl.getJsonUrl().JSON_URL_FRIEND_SEARCH_LIST + "?searchFlag=" + flag + "&content=" + content;
        FSRequestHelper.newGetRequest(url, TAG, FriendSearchModel.class, new IFDResponse<FriendSearchModel>() {
            @Override
            public void onSuccess(FriendSearchModel friendSearchModel) {
                if (friendSearchModel != null) {
                    friendList = friendSearchModel.getRecommendList();
                    setEmptyMessage(getString(R.string.friend_contact_none));
                    if (!ListUtils.isEmpty(friendList) && !ListUtils.isEmpty(contactList)) {
                        showContent();
                        for (ContactModel friend : friendList) {
                            for (ContactModel contactModel : contactList) {
                                if (friend.getPhone().equals(contactModel.getPhone())) {
                                    String pinyin = ComUtil.getPingYin(contactModel.getContactName());
                                    String FPinYin = pinyin.substring(0, 1).toUpperCase();
                                    if (FPinYin.matches("[A-Z]")) {
                                        friend.setFirstPinYin(FPinYin);
                                    } else {
                                        friend.setFirstPinYin("#");
                                    }
                                    friend.setPinYin(pinyin);
                                    friend.setContactName(contactModel.getContactName());
                                }
                            }
                        }

                        Collections.sort(friendList, new LanguageComparator());
                        mAdatper.refreshData(friendList);
                    } else {
                        showEmpty();
                    }

                } else {
                    showEmpty();
                }
            }

            @Override
            public void onNetWorkError() {
                showError();
            }

            @Override
            public void onServerError() {
                showError();
            }
        }, false, true, new ExtendJsonUtil());
    }


    public class LanguageComparator implements Comparator<ContactModel> {


        @Override
        public int compare(ContactModel lhs, ContactModel rhs) {
            // 获取ascii值
            int lhs_ascii = lhs.getFirstPinYin().toUpperCase().charAt(0);
            int rhs_ascii = rhs.getFirstPinYin().toUpperCase().charAt(0);
            // 判断若不是字母，则排在字母之后
            if (lhs_ascii < 65 || lhs_ascii > 90)
                return 1;
            else if (rhs_ascii < 65 || rhs_ascii > 90)
                return -1;
            else
                return lhs.getPinYin().compareTo(rhs.getPinYin());
        }
    }

    private String getPhoneContacts() {
        contactPhones = new StringBuffer();
        ContentResolver resolver = getContentResolver();
        // 获取手机联系人
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                String phoneNumber = phoneCursor.getString(2);
                // 当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                // 得到联系人名称
                phoneNumber = phoneNumber.replace(" ", "");
                String username = phoneCursor.getString(1);

                ContactModel contactModel = new ContactModel();
                contactModel.setContactName(username);
                contactModel.setPhone(phoneNumber);
                contactPhones.append(phoneNumber).append(",");
                contactList.add(contactModel);
            }
            phoneCursor.close();
        }
        return contactPhones.toString();

    }

}
