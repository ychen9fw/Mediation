package com.hmscl.huawei_admob_mediation_adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.mediation.*
import com.google.android.gms.ads.mediation.customevent.CustomEventBanner
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitial
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.BannerAdSize
import com.huawei.hms.ads.InterstitialAd
import com.huawei.hms.ads.banner.BannerView
import java.lang.Exception

class HuaweiCustomEventAdapter : CustomEventBanner, CustomEventInterstitial {
    private val TAG = HuaweiCustomEventAdapter::class.java.simpleName

    private lateinit var huaweiBannerView: BannerView
    private var huaweiBannerAdId = "testw6vs28auh3"

    private lateinit var huaweiInterstitialView: InterstitialAd
    private var huaweiInterstitialAdId = "testb4znbuh3n2"


    override fun requestBannerAd(
        context: Context?,
        listener: CustomEventBannerListener,
        serverParameters: String?,
        size: AdSize,
        mediationAdRequest: MediationAdRequest,
        mediationExtras: Bundle?
    ) {
        try {
            huaweiBannerView= BannerView(context)
            var eventForwarder = HuaweiCustomEventBannerEventForwarder(listener, huaweiBannerView)
            huaweiBannerView.adListener = eventForwarder
            if (serverParameters != null) {
                huaweiBannerAdId = serverParameters
            }
            huaweiBannerView.adId = huaweiBannerAdId
            huaweiBannerView.bannerAdSize = BannerAdSize(size.width, size.height)
            huaweiBannerView.loadAd(configureAdRequest(mediationAdRequest))
        } catch (e: Exception) {
            Log.e(TAG, "Request Banner Ad Failed - ${e.message}")
            huaweiBannerView.adListener.onAdFailed(AdParam.ErrorCode.INNER)
        }
    }

    override fun requestInterstitialAd(
        context: Context?,
        listener: CustomEventInterstitialListener,
        serverParameters: String?,
        mediationAdRequest: MediationAdRequest,
        mediationExtras: Bundle?
    ) {
        try {
            huaweiInterstitialView = InterstitialAd(context)
            huaweiInterstitialView.adListener = HuaweiCustomEventInterstitialEventForwarder(listener,huaweiInterstitialView)

            if (serverParameters != null) {
                huaweiInterstitialAdId = serverParameters
            }
            huaweiInterstitialView.adId = huaweiInterstitialAdId
            huaweiInterstitialView.loadAd(configureAdRequest(mediationAdRequest))
        } catch (e: Exception) {
            Log.e(TAG, "Request Interstitial Ad Failed - ${e.message}")
            huaweiInterstitialView.adListener.onAdFailed(AdParam.ErrorCode.INNER)
        }

    }

    override fun showInterstitial() {
        if (huaweiInterstitialView.isLoaded) {
            huaweiInterstitialView.show()
        }
    }

    private fun configureAdRequest(bannerAdRequest: MediationAdRequest): AdParam {
        val adParam = AdParam.Builder()
        bannerAdRequest.keywords?.forEach { keyword ->
            adParam.addKeyword(keyword)
        }
        //COPPA
        adParam.setTagForChildProtection(bannerAdRequest.taggedForChildDirectedTreatment())
        //not everything is configured!!
        return adParam.build()
    }

    override fun onDestroy() {
//        Not sure if these are needed
//        huaweiBannerView.adListener = AdListener()
//        huaweiInterstitialView.adListener = AdListener()
    }

    override fun onPause() {
        TODO("Not yet implemented")
    }

    override fun onResume() {
        TODO("Not yet implemented")
    }
}