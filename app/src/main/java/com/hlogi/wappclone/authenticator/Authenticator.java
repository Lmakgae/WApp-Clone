package com.hlogi.wappclone.authenticator;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class Authenticator extends AbstractAccountAuthenticator {

//    private final Context mContext;

    public Authenticator(Context context) {
        super(context);
//        mContext = context;

    }
    // Editing properties is not supported
    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        //return null;
        throw new UnsupportedOperationException();
    }
    // Don't add additional accounts
    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        return null;
    }

//    @Override
//    public Bundle addAccount(AccountAuthenticatorResponse response,
//                             String accountType, String authTokenType, String[] requiredFeatures,
//                             Bundle options) throws NetworkErrorException {
//        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
//        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
//
//        final Bundle bundle = new Bundle();
//        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
//        return bundle;
//    }

    // Ignore attempts to confirm credentials
    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }
    // Getting an authentication token is not supported
    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        //return null;
        throw new UnsupportedOperationException();
    }
//    @Override
//    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
//                               String authTokenType, Bundle options) throws NetworkErrorException {
//        final Bundle result = new Bundle();
//        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
//        result.putString(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
//        return result;
//    }
    // Getting a label for the auth token is not supported
    @Override
    public String getAuthTokenLabel(String authTokenType) {
        //return null;
        throw new UnsupportedOperationException();
    }
    // Updating user credentials is not supported
    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        //return null;
        throw new UnsupportedOperationException();
    }
    // Checking features for the account is not supported
    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        //return null;
        throw new UnsupportedOperationException();
    }
}
