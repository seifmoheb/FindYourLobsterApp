package com.app.findyourlobster.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.app.findyourlobster.R;
import com.app.findyourlobster.data.Constants;
import com.app.findyourlobster.data.SquaredRecycleAdapter;
import com.app.findyourlobster.data.squares;

import java.util.ArrayList;
import java.util.List;

public class GoPremiumActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    Window window;
    RecyclerView recycler;
    SquaredRecycleAdapter squaredRecycleAdapter;
    Button subscribe, cancel;
    List skuList = new ArrayList();
    private ArrayList<squares> list = new ArrayList<>();
    private BillingClient billingClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_premium);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        recycler = findViewById(R.id.recyclerView);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        squares squares1 = new squares("Ads Free", getDrawable(R.drawable.adblock));
        list.add(squares1);
        squares squares2 = new squares("6 daily free super likes", getDrawable(R.drawable.swipeicon));
        list.add(squares2);
        squares squares3 = new squares("Free Rewind option to cards", getDrawable(R.drawable.reverseicon));
        list.add(squares3);
        squares squares4 = new squares("Hide your age", getDrawable(R.drawable.newspaper));
        list.add(squares4);
        squares squares5 = new squares("Hide Location", getDrawable(R.drawable.hidelocation));
        list.add(squares5);
        squares squares6 = new squares("Control who sees you", getDrawable(R.drawable.control));
        list.add(squares6);
        squares squares7 = new squares("Be able to watch all profile pictures, favourite and locked", getDrawable(R.drawable.picture));
        list.add(squares7);
        squaredRecycleAdapter = new SquaredRecycleAdapter(list);
        recycler.setAdapter(squaredRecycleAdapter);
        subscribe = findViewById(R.id.subscribe);
        cancel = findViewById(R.id.cancel);
        skuList.add(Constants.Monthly);
        subscribe.setEnabled(false);
        setupBillingClient();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void setupBillingClient() {
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    load();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });
    }

    private void load() {
        if (billingClient.isReady()) {
            final SkuDetailsParams params = SkuDetailsParams.newBuilder()
                    .setSkusList(skuList)
                    .setType(BillingClient.SkuType.SUBS)
                    .build();
            Log.i("1",params.toString());
            billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(@NonNull final BillingResult billingResult, @Nullable List<SkuDetails> list) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        Log.i("2","true");

                        for (Object skuDetailsObject : list) {
                            final SkuDetails skuDetails = (SkuDetails) skuDetailsObject;
                            if (skuDetails.getSku().equals(Constants.Monthly)) {
                                Log.i("3",skuDetails.getSku());
                                subscribe.setEnabled(true);
                                subscribe.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        BillingFlowParams billingFlowParams = BillingFlowParams
                                                .newBuilder()
                                                .setSkuDetails(skuDetails)
                                                .build();
                                        Log.i("4",billingFlowParams.getSkuDetails().toString());

                                        billingClient.launchBillingFlow(GoPremiumActivity.this, billingFlowParams);

                                    }
                                });
                            }
                        }
                    }
                }
            });
        }
    }


    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

        int responseCode = billingResult.getResponseCode();
        Log.i("Response code ",String.valueOf(responseCode));
        Log.i("Response code ",String.valueOf(BillingClient.BillingResponseCode.OK));

        Log.i("LIST", list.toString());
        if (responseCode == BillingClient.BillingResponseCode.OK) {

                    Log.i("Purchase token 6: ",list.get(0).getAccountIdentifiers().getObfuscatedAccountId().toString());
                    Log.i("Purchase token 7: ",list.get(0).getAccountIdentifiers().getObfuscatedProfileId().toString());
            /*AcknowledgePurchaseParams acknowledgePurchaseParams
                    = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(list.get(0).getPurchaseToken())
                    .build();
            billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult1 -> {
                if(billingResult1.getResponseCode()== BillingClient.BillingResponseCode.OK){
                    //Toast.makeText(RemoveAdsActivity.this, "Purchase Acknowledged", Toast.LENGTH_SHORT).show();
                }
            });*/


                    //purchase.getAccountIdentifiers().


        } else if (responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {

        } else if (responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {

        }
        else{
            Log.i("Purchase token: ","none");

        }
    }
}