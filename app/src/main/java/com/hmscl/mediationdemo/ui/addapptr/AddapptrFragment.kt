package com.hmscl.mediationdemo.ui.addapptr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.hmscl.mediationdemo.R
import com.hmscl.mediationdemo.Utils
import com.huawei.hms.ads.nativead.NativeView
import com.intentsoftware.addapptr.*
import com.intentsoftware.addapptr.ad.VASTAdData
import kotlinx.android.synthetic.main.fragment_addapptr.*


class AddapptrFragment : Fragment(), AATKit.Delegate {
    private lateinit var configuration: AATKitConfiguration
    private var stickyBannerId = -1
    private var multisizeBannerId = -1
    private var fullscreenId = -1
    private var rewardedId = -1
    private lateinit var inFeedBannerPlacement: BannerPlacement
    private var nativeId = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_addapptr, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configuration = AATKitConfiguration(requireActivity().application)
        configuration.setDelegate(this)
//        configuration.setTestModeAccountId(2426)
        AATKit.init(configuration)

        btn_showFullscreenAd.setOnClickListener {
            showFullscreenAds()
        }

        btn_showRewardedAd.setOnClickListener {
            showRewardedAds()
        }
    }

    private fun showRewardedAds() {
        AATKit.showPlacement(rewardedId)
    }

    private fun showFullscreenAds() {
        AATKit.showPlacement(fullscreenId)
    }

    private fun loadAds() {
        loadStickyBanner()
        loadMultisizeBanner()
//        loadInFeedBanners()
        loadFullscreenAds()
        loadRewardedAds()
        loadNativeAds()
    }

    private fun loadNativeAds() {
        nativeId = AATKit.createNativeAdPlacement("Native",true)
        AATKit.reloadPlacement(nativeId)

        val nativeAd = AATKit.getNativeAd(nativeId)
        if (nativeAd != null && AATKit.isNativeAdExpired(nativeAd) && AATKit.isNativeAdReady(nativeAd)) {
            AATKit.reportAdSpaceForPlacement(nativeId)
            val network = AATKit.getNativeAdNetwork(nativeAd)
            val networkExtraView = AATKit.getNativeAdBrandingLogo(nativeAd)
            val type = AATKit.getNativeAdType(nativeAd)

            var nativeBannerView: ViewGroup? = null
            var mainImageView: View? = null
            var iconView: View? = null

            when (network) {
                AdNetwork.HUAWEI -> {
                    nativeBannerView = native_video_view as NativeView
                    nativeBannerView.titleView = ad_title
                    nativeBannerView.mediaView = ad_media
                    nativeBannerView.adSourceView = ad_source
                    nativeBannerView.callToActionView = ad_call_to_action
                }
            }

            AATKit.attachNativeAdToLayout(nativeAd,nativeBannerView,mainImageView,iconView)
        }
    }

    private fun loadRewardedAds() {
        rewardedId = AATKit.createRewardedVideoPlacement("RewardedVideo")
        AATKit.startPlacementAutoReload(rewardedId)
    }

    private fun loadFullscreenAds() {
        fullscreenId = AATKit.createPlacement("Fullscreen",PlacementSize.Fullscreen)
        AATKit.startPlacementAutoReload(fullscreenId)
    }

    private fun loadInFeedBanners() {
        val config = BannerConfiguration()
        inFeedBannerPlacement = AATKit.createBannerPlacement("TestInFeedBanner",config)
        val request = BannerRequest(null)
        val listener = BannerRequestCompletionListener { layout, error ->

        }
        inFeedBannerPlacement.requestAd(request,listener)
    }

    private fun loadStickyBanner() {
        stickyBannerId = AATKit.createPlacement("TestBanner", PlacementSize.Banner320x53)
        val mainLayout = aat_stickybanner as FrameLayout
        val placementView = AATKit.getPlacementView(stickyBannerId)
        mainLayout.addView(placementView)
        AATKit.startPlacementAutoReload(stickyBannerId)
    }

    private fun loadMultisizeBanner() {
        multisizeBannerId = AATKit.createPlacement("TestMultisizeBanner", PlacementSize.MultiSizeBanner)
        val mainLayout = aat_multisizebanner as FrameLayout
        AATKit.startPlacementAutoReload(multisizeBannerId);
    }

    private fun unloadBanners() {
        AATKit.stopPlacementAutoReload(stickyBannerId)
        val stickyBannerView = AATKit.getPlacementView(stickyBannerId)
        if (stickyBannerView.parent != null) {
            val parent = stickyBannerView.parent as ViewGroup
            parent.removeView(stickyBannerView)
        }

        AATKit.stopPlacementAutoReload(multisizeBannerId)
        val multisizeBannerView = AATKit.getPlacementView(multisizeBannerId)
        if (multisizeBannerView.parent != null) {
            val parent = multisizeBannerView.parent as ViewGroup
            parent.removeView(multisizeBannerView)
        }

        AATKit.stopPlacementAutoReload(fullscreenId)
    }

    override fun onResume() {
        super.onResume()
        AATKit.onActivityResume(activity)
        loadAds()
    }

    override fun onPause() {
        super.onPause()
        unloadBanners()
        AATKit.onActivityPause(activity)
    }

    override fun aatkitHaveAd(placementId: Int) {
        Utils.showToast(requireContext(), "Have ad")
    }

    override fun aatkitNoAd(placementId: Int) {
        Utils.showToast(requireContext(), "No ad")
    }

    override fun aatkitPauseForAd(placementId: Int) {
        Utils.showToast(requireContext(), "Pause for ad")
    }

    override fun aatkitResumeAfterAd(placementId: Int) {
        Utils.showToast(requireContext(), "Resume after ad")
    }

    override fun aatkitShowingEmpty(placementId: Int) {
        Utils.showToast(requireContext(), "Showing empty")
    }

    override fun aatkitUserEarnedIncentive(placementId: Int) {
        if (placementId == rewardedId) {
            Utils.showToast(requireContext(), "User earned")
        }
    }

    override fun aatkitObtainedAdRules(fromTheServer: Boolean) {
//        Utils.showToast(requireContext(), "Obtained Ad Rules - $fromTheServer")
    }

    override fun aatkitUnknownBundleId() {
//        TODO("Not yet implemented")
    }

    override fun aatkitHaveAdForPlacementWithBannerView(
        placementId: Int,
        bannerView: BannerPlacementLayout?
    ) {
        val mainLayout = aat_multisizebanner as FrameLayout
        mainLayout.removeAllViews()
        mainLayout.addView(bannerView)
    }

    override fun aatkitHaveVASTAd(placementId: Int, data: VASTAdData?) {
//        TODO("Not yet implemented")
    }
}