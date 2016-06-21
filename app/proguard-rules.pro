#从sdk-tools/proguard/proguard-android-optimize.txt拷贝
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5

#允许访问并修改类和类的成员的权限，将某些内联函数设置为public的权限。在对外提供的library中不应配置该flag
-allowaccessmodification
#不预校验
-dontpreverify

#混淆时不会混淆类名
-dontusemixedcaseclassnames

#不忽略非公共的库类
#-dontskipnonpubliclibraryclasses

#输出生成信息
-verbose

#不管warnings信息一直替换。禁止使用
#-ignorewarnings

-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable,*Annotation*

-printmapping map.txt
-printseeds seed.txt

-keepattributes *Annotation*
-keepattributes Signature
-keep public class * extends android.app.Activity
-keep public class * extends android.support.v7.app.ActionBarActivity
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends java.lang.Exception
-dontnote com.android.vending.licensing.ILicensingService

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface
    <methods>;
}

-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.android.app.pay.IAlixPay{*;}
-keep class com.alipay.android.app.pay.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.pay.IAlixPayCallback{*;}
-keep class com.alipay.android.app.pay.IAlixPayCallback$Stub{*;}
-keep class com.alipay.android.app.script.**{*;}
-keep class com.alipay.android.app.pay.PayTask{*;}
-keep class com.alipay.android.app.pay.PayTask$OnPayListener{*;}
-keep class com.alipay.android.app.pay.CheckAccountTask{*;}
-keep class com.alipay.android.app.pay.CheckAccountTask$OnCheckListener{*;}
-keep class com.alipay.android.app.encrypt.**{*;}

-keep class com.alipay.mobile.command.*
-keep class android.webkit.*
-keep class com.alipay.mobilesecuritysdk.*
-keep class com.alipay.android.app.*
-keep class com.alipay.android.lib.*
-keep class com.alipay.android.mini.*
-keep class com.alipay.html.*
-keep class org.ccil.cowan.tagsoup.*
-keep class com.squareup.**
-dontwarn com.squareup.**

-keep class com.ut.*
-keep class com.alipay.test.ui.core.*
-keep class com.alipay.trobot.external.*
-keep class org.rome.android.ipp.*

-keep class com.shangdian.**
-dontwarn com.shangdian.**

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**

#引入的jar包
#umeng
-keep public class com.umeng.analytics.** {
    public protected *;
}
-dontwarn com.umeng.analytics.**

-keep public class u.aly.** {
    public protected *;
}
-dontwarn u.aly.**

#snail
-keep public class com.snail.** {
    public protected *;
}
-dontwarn com.snail.**

#snailbilling
-keep public class com.snailbilling.** {
    public protected *;
}
-dontwarn com.snailbilling.**

#weibo
-keep public class com.sina.** {
    public protected *;
}
-dontwarn com.sina.**

#nineoldandroids
-keep public class com.nineoldandroids.** {
    public protected *;
}
-dontwarn com.nineoldandroid.**

#getui
-keep public class com.igexin.** {
    public protected *;
}
-dontwarn com.igexin.**

#fastjson
-keep public class com.alibaba.** {
    public protected *;
}
-dontwarn com.alibaba.**

#snailstore-sdk
-keep public class com.snailgame.mobilesdk.** {
    public protected *;
}
-dontwarn com.snailgame.mobilesdk.**
-keep public class com.snailgame.sdkcore.** {
    public protected *;
}
-dontwarn com.snailgame.sdkcore.**

#android-async-http
-keep public class com.loopj.** {
    public protected *;
}
-dontwarn com.loopj.**

#umpay
-keep public class com.umpay.** {
    public protected *;
}
-dontwarn com.umpay.**

#unionpay
-keep public class com.unionpay.** {
    public protected *;
}
-dontwarn com.unionpay.**

#unipay
-keep public class com.unipay.** {
}
-dontwarn com.unipay.**

#tencent
-keep public class com.tencent.mm.sdk.** {
}
-dontwarn com.tencent.mm.sdk.**


#保证NineOldAnimation可以调用
-keep class com.snailgame.cjg.MainActivity$ViewWrapper{*;}
-keep class com.snailgame.cjg.common.model.SystemConfModel$ModelItem{*;}

#保证Model对象不被混淆，json反序列化的时候需要使用
-keep class com.snailgame.cjg.common.model.**{*;}
-keep class com.snailgame.cjg.communication.model.**{*;}
-keep class com.snailgame.cjg.desktop.model.**{*;}
-keep class com.snailgame.cjg.detail.model.**{*;}
-keep class com.snailgame.cjg.download.model.**{*;}
-keep class com.snailgame.cjg.downloadmanager.model.**{*;}
-keep class com.snailgame.cjg.message.model.**{*;}
-keep class com.snailgame.cjg.personal.model.**{*;}
-keep class com.snailgame.cjg.member.model.**{*;}
-keep class com.snailgame.cjg.scorewall.model.**{*;}
-keep class com.snailgame.cjg.search.model.**{*;}
-keep class com.snailgame.cjg.seekgame.recommend.model.**{*;}
-keep class com.snailgame.cjg.seekgame.collection.model.**{*;}
-keep class com.snailgame.cjg.seekgame.category.model.**{*;}
-keep class com.snailgame.cjg.seekgame.rank.model.**{*;}
-keep class com.snailgame.cjg.spree.model.**{*;}
-keep class com.snailgame.cjg.strategy.model.**{*;}
-keep class com.snailgame.cjg.update.model.**{*;}
-keep class com.snailgame.cjg.home.model.**{*;}
-keep class com.snailgame.cjg.manage.model.**{*;}
-keep class com.snailgame.cjg.skin.model.**{*;}
-keep class com.snailgame.cjg.util.json.**{*;}
-keep class com.snailgame.cjg.util.model.**{*;}
-keep class com.snailgame.cjg.receiver.model.**{*;}
-keep class com.snailgame.cjg.common.inter.WebViewInterface{*;}
-keep class com.snailgame.cjg.common.inter.ImageUploadInterface{*;}
-keep class com.snailgame.cjg.common.db.dao.**{*;}
-keep class com.snailgame.cjg.store.model.**{*;}
-keep class com.snailgame.cjg.news.model.**{*;}
-keep class com.snailgame.cjg.friend.model.**{*;}
-keep class android.support.**{*;}
-keep public class * extends com.snailgame.cjg.common.inter.WebViewInterface{*;}
-keep public class * extends com.snailgame.cjg.common.inter.ImageUploadInterface{*;}
-keep class com.snailgame.cjg.common.model.AuthoModel$Content{*;}

##-----------------流量统计相关模块混淆设置 BEGIN---------------------
# OrmLite uses reflection
-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }

-keep class third.com.snail.trafficmonitor.engine.data.provider.**
-keepclassmembers class third.com.snail.trafficmonitor.engine.data.provider.** { *; }

# Table model
-keep class third.com.snail.trafficmonitor.engine.data.table.**
-keepclassmembers class third.com.snail.trafficmonitor.engine.data.table.** { *; }

-keepclasseswithmembers class third.com.snail.trafficmonitor.engine.data.TrafficDataHelper {
    public <init>(android.content.Context);
}

-keepclasseswithmembers class third.com.snail.trafficmonitor.engine.data.dao.TrafficDaoImpl {
    public <init>(com.j256.ormlite.support.ConnectionSource, com.j256.ormlite.table.DatabaseTableConfig);
}

-keep class com.tojc.ormlite.android.**
-keepclassmembers class com.tojc.ormlite.android.** { *; }
-keep enum com.tojc.ormlite.android.**
-keepclassmembers enum com.tojc.ormlite.android.** { *; }
-keep interface com.tojc.ormlite.android.**
-keepclassmembers interface com.tojc.ormlite.android.** { *; }
-dontwarn com.tojc.ormlite.**

# android-log
-keep class org.apache.commons.**
-keepclassmembers class org.apache.commons.** { *; }

# RecyclerView
-keep class android.support.v7.widget.**
-keepclassmembers class android.support.v7.widget.** { *; }

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

##---------------End: proguard configuration for Gson  ----------
##-----------------流量统计相关模块混淆设置 END---------------------

# ButterKnife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# 云测崩溃大师
-dontwarn com.testin.agent.**
-keep class com.testin.agent.** {*;}

# otto
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

#pinyin4j
-dontwarn demo.**
-keep class demo.** { *;}
-keep class net.sourceforge.pinyin4j.** {*;}

# MUCH掌机安装
-keep class android.content.pm.IPackageInstallObserver { *; }

#jsoup
-keep class org.jsoup.** {*;}