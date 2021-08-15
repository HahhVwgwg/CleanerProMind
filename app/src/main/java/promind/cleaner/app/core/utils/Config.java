package promind.cleaner.app.core.utils;

import android.Manifest;

import java.io.Serializable;

import promind.cleaner.app.R;

public class Config {

    public static final String DATA_OPEN_RESULT = "data open result screen";
    public static final String DATA_OPEN_BOOST = "data open boost screen";
    public static final String DATA_OPEN_FUNCTION = "data open function";
    public static final String DATA_OPEN_RESULT_WITHOUT_INTERSTITIAL = "data open without interstitial";
    public static final String RESULT_DEEP_CLEAN_DATA = "result deep clean data";
    public static final long TIME_ALLOW_BOOOST = 5 * 60 * 1000;
    public static final long TIME_BATTERY_FULL = 5 * 60 * 1000;
    public static final int MY_PERMISSIONS_REQUEST_STORAGE = 111;
    public static final int PERMISSIONS_USAGE = 112;
    public static final int PERMISSIONS_DRAW_APPICATION = 113;
    public static final int PERMISSIONS_NOTIFICATION_LISTENER = 114;
    public static final int PERMISSIONS_WRITE_SETTINGS = 115;
    public static final int UNINSTALL_REQUEST_CODE = 116;
    public static final int UNINSTALL_REQUEST_CODE_ACTIVITY = 117;
    public static final int MY_PERMISSIONS_REQUEST_CLEAN_CACHE = 118;
    public static final String PKG_RECERVER_DATA = "package recerver data";
    public static final String ALARM_OPEN_FUNTION = "alarm open funtion";

    public enum TYPE_DISPLAY_ADAPTER {
        VERTICAL,
        HORIZOLTAL,
        SUGGEST
    }

    public enum FUNCTION implements Serializable {
        JUNK_FILES(1, R.drawable.ic_junk_file, R.string.junk_files, R.string.junk, R.string.junk_files_des, R.drawable.bg_func_junkfile, R.color.color_00b6c5, R.string.clean, "trash_result.json", R.string.clean_result, R.string.junk_files_btn, R.string.junk_files_dialog),
        CPU_COOLER(2, R.drawable.ic_cpu_cooler, R.string.cpu_cooler, R.string.cpu_cooler, R.string.cpu_cooler_des, R.drawable.bg_func_cpucooler, R.color.color_3f7af8, R.string.cooling, "cpu_result.json", R.string.result_cooler, R.string.cpu_cooler_btn, R.string.cpu_cooler_dialog),
        PHONE_BOOST(3, R.drawable.ic_game_booster1, R.string.phone_booster, R.string.phone_booster, R.string.phone_booster_des, R.drawable.bg_func_phoneboost, R.color.color_ff7674, R.string.boost, "boost_result.json", R.string.boost_result, R.string.phone_booster_btn, R.string.phone_booster_dialog),
        ANTIVIRUS(4, R.drawable.ic_antivirus, R.string.antivirus, R.string.antivirus, R.string.antivirus_des, R.drawable.bg_func_antivirus, R.color.color_ff79a2, R.string.protect_now, "antivirus_result.json", R.string.antivirus_result, R.string.antivirus_btn, R.string.antivirus_dialog),
        POWER_SAVING(5, R.drawable.ic_power_saving, R.string.power_saving, R.string.power_saving, R.string.power_saving_des, R.drawable.bg_func_power_saver, R.color.color_b365ff, R.string.save_now, "power_saving_result.json", R.string.sm_edge_device_optimized, R.string.power_saving_btn, R.string.power_saving_dialog),
        SMART_CHARGE(8, R.drawable.ic_power_saving, R.string.smart_charge, R.string.smart_charge, R.string.smart_charge_des, R.drawable.bg_func_smart_charge, R.color.color_00e3ad, R.string.try_it, "charge_result.json", R.string.fast_charger_boosted_result, R.string.smart_charge_btn, R.string.smart_charge_dialog),
        DEEP_CLEAN(9, R.drawable.ic_deep_clean, R.string.deep_clean, R.string.deep_clean, R.string.deep_clean_des, R.drawable.bg_func_deep_clean, R.color.color_ff7674, R.string.try_it, "heart.json", R.string.deep_clean_result, R.string.deep_clean_btn, R.string.deep_clean_dialog),
        DEEP_CLEAN_JUNK(19, R.drawable.ic_deep_clean_junk, R.string.deep_clean_text, R.string.deep_clean_text, R.string.junk_files_des, R.drawable.bg_func_junkfile, R.color.color_00b6c5, R.string.clean, "trash_result.json", R.string.deep_clean_result, R.string.deep_clean_btn, R.string.deep_clean_dialog),
        APP_UNINSTALL(10, R.drawable.ic_app_uninstall, R.string.app_uninstall, R.string.app_uninstall, R.string.app_uninstall_des, R.drawable.bg_func_appmanager, R.color.color_3f7af8, R.string.try_it, "restult_like.json", R.string.sm_edge_device_optimized, R.string.app_uninstall_btn, R.string.app_uninstall_dialog),
        NOTIFICATION_MANAGER(12, R.drawable.ic_notification_manager, R.string.notification_manager, R.string.notification_manager, R.string.notification_manager_des, R.drawable.bg_func_notification_manager, R.color.color_b365ff, R.string.try_it, "trash_result.json", R.string.notification_manager_result, R.string.notification_manager_btn, R.string.notification_manager_dialog),

        PREMIUM_SECURITY(13, R.drawable.ic_antivirus, R.string.premium_shield, R.string.premium_shield, R.string.premium_shield_des, R.drawable.bg_func_appmanager, R.color.color_3f7af8, R.string.try_it, "restult_like.json", R.string.sm_edge_device_optimized, 0, 0),
        ADDITIONAL_DEEP_CLEAN(14, R.drawable.ic_junk_file, R.string.additional_clean, R.string.additional_clean, R.string.additional_clean_des, R.drawable.bg_func_appmanager, R.color.color_3f7af8, R.string.try_it, "restult_like.json", R.string.sm_edge_device_optimized, 0, 0),
        ALL_FUNCTIONS(15, R.drawable.menu_diamond, R.string.all_functions, R.string.all_functions, R.string.all_functions_des, R.drawable.bg_func_appmanager, R.color.color_3f7af8, R.string.try_it, "restult_like.json", R.string.sm_edge_device_optimized, 0, 0),
        ADDITIONAL_SPEED(16, R.drawable.ic_game_booster1, R.string.additional_speed, R.string.additional_speed, R.string.additional_speed_des, R.drawable.bg_func_appmanager, R.color.color_3f7af8, R.string.try_it, "restult_like.json", R.string.sm_edge_device_optimized, 0, 0),
        NO_ADS(17, R.drawable.menu_no_ads, R.string.no_ads, R.string.no_ads, R.string.no_ads_des, R.drawable.bg_func_appmanager, R.color.color_3f7af8, R.string.try_it, "restult_like.json", R.string.sm_edge_device_optimized, 0, 0),

        PREMIUM(18, R.drawable.award_icon, R.string.no_ads, R.string.premium_shield, R.string.additional_speed_des, R.drawable.bg_func_appmanager, R.color.color_3f7af8, R.string.start_14_day_trial, "restult_like.json", R.string.boost_result, R.string.start_14_day_trial, R.string.deep_clean_dialog);


        @Override
        public String toString() {
            return "FUNCTION{" +
                    "id=" + id +
                    ", icon=" + icon +
                    ", title=" + title +
                    ", title_slider=" + title_slider +
                    ", description=" + description +
                    ", color=" + color +
                    ", action=" + action +
                    ", background=" + background +
                    ", jsonResult='" + jsonResult + '\'' +
                    ", titleResult=" + titleResult +
                    ", btnText=" + btnText +
                    ", dialogQuestion=" + dialogQuestion +
                    ", needDot=" + needDot +
                    '}';
        }

        public int id;
        public int icon;
        public int title;
        public int title_slider;
        public int description;
        public int color;
        public int action;
        public int background;
        public String jsonResult;
        public int titleResult;
        public int btnText;
        public int dialogQuestion;
        public boolean needDot = false;

        FUNCTION() {
        }

        FUNCTION(int id, int icon, int title, int title_slider, int description, int background, int color, int action, String jsonResult, int titleResult, int btnText, int dialogQuestion) {
            this.id = id;
            this.icon = icon;
            this.title = title;
            this.title_slider = title_slider;
            this.description = description;
            this.color = color;
            this.action = action;
            this.background = background;
            this.jsonResult = jsonResult;
            this.titleResult = titleResult;
            this.btnText = btnText;
            this.dialogQuestion = dialogQuestion;
        }
    }

    public static final FUNCTION[] LST_HOME_HORIZONTAL = new FUNCTION[]{
//            FUNCTION.JUNK_FILES, FUNCTION.PHONE_BOOST, FUNCTION.CPU_COOLER, FUNCTION.ANTIVIRUS, FUNCTION.POWER_SAVING, FUNCTION.APP_UNINSTALL
    };

    public static final FUNCTION[] LST_HOME_VERTICAL = new FUNCTION[]{
            FUNCTION.JUNK_FILES, FUNCTION.APP_UNINSTALL, FUNCTION.NOTIFICATION_MANAGER
    };

    public static final FUNCTION[] LST_SLIDER = new FUNCTION[]{
//            FUNCTION.JUNK_FILES, FUNCTION.PHONE_BOOST, FUNCTION.CPU_COOLER, FUNCTION.ANTIVIRUS, FUNCTION.POWER_SAVING, FUNCTION.APP_UNINSTALL,
            FUNCTION.JUNK_FILES, FUNCTION.APP_UNINSTALL, FUNCTION.NOTIFICATION_MANAGER
    };

    public static final FUNCTION[] LST_PREMIUM = new FUNCTION[]{
            FUNCTION.NO_ADS,
            FUNCTION.PREMIUM_SECURITY,
            FUNCTION.ADDITIONAL_DEEP_CLEAN,
            FUNCTION.ALL_FUNCTIONS,
            FUNCTION.ADDITIONAL_SPEED
    };

    public static final String[] LIST_APP_NETWORK = {
            "com.zhiliaoapp.musically", "app.buzz.share", "in.mohalla.sharechat", "com.instagram.android", "com.twitter.android", "com.redefine.welike", "com.facebook.katana",
            "com.ss.android.ugc.boom", "com.facebook.lite", "com.vivashow.share.video.chat", "com.roposo.android", "com.ss.android.ugc.boomlite", "com.uc.vmlite", "com.snapchat.android",
            "com.asiainno.uplive", "sg.bigo.live", "com.nebula.mamu", "com.starmakerinteractive.starm", "com.thankyo.hwgame", "com.kryptolabs.android.speakerswire", "com.yy.hiyo",
            "com.whatsapp", "com.facebook.orca", "com.truecaller", "com.facebook.mlite", "org.telegram.messenger", "com.google.android.apps.tachyon", "com.imo.android.imoim", "com.whatsapp.w4b",
            "com.eyecon.global", "com.dstukalov.walocalstoragestickers", "com.bingo.livetalk", "com.jio.join", "com.bsb.hike", "com.azarlive.android", "com.jiochat.jiochatapp",
            "com.wastickers.wastickerapps", "im.thebot.messenger", "com.caller.id.mobile.phone.number.location.locator.live.track.tracker.callblocker", "com.michatapp.im", "jp.naver.line.android",
            "com.techirsh.islamicsticker", "com.tencent.mm", "com.bbm", "com.discord", "com.bluesoft.clonappmessenger", "com.google.android.talk", "com.linecorp.linelite", "com.google.android.gm",
            "com.breakdev.zapclone", "com.stickotext.main", "com.p1.mobile.putong", "app.zenly.locator", "com.pinterest", "com.michatapp.im.lite", "sg.bigo.hellotalk", "com.google.android.apps.plus",
            "com.beetalk", "com.thinkmobile.accountmaster", "com.badoo.mobile", "com.boo.boomoji", "com.linkedin.android", "com.doyo.game.live", "com.myyearbook.m", "com.easycodes.stickercreator",
            "stickermaker.android.stickermaker", "com.easycodes.memesbr", "org.telegram.messenger.erick.zapzap", "com.whatdir.stickers", "com.link.messages.sms", "com.skype.raider",
            "com.yahoo.mobile.client.android.mail", "com.enflick.android.tn2ndLine", "br.gov.caixa.bolsafamilia", "com.lazygeniouz.saveit", "messenger.messenger.messenger.messenger",
            "com.cmcm.live", "com.jaumo", "net.lovoo.android", "com.narvii.amino.master", "com.parallel.space.lite", "com.viber.voip", "com.goldmessenger.freefastchat", "com.imo.android.imoimbeta",
            "org.hakika.wwww", "com.juphoon.justalk", "com.video.chat.spark", "com.muper.radella", "com.meet.android", "com.dev.questionaskto33", "messenger.pro.messenger", "kaf.bl3arabi.com",
            "com.crush.gogo", "com.videochat.livu", "ps.AndroTeam.HeshamPoems", "com.tinder", "com.tencent.mobileqq", "com.tencent.mm", "com.sina.weibolite"};

    public enum PERMISSION_DANGEROUS implements Serializable {

        READ_PHONE_STATE(Manifest.permission.READ_PHONE_STATE, R.string.per_read_phone_title, R.string.per_read_phone_des),
        ACCESS_FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION, R.string.per_access_location_title, R.string.per_access_location_des),
        READ_SMS(Manifest.permission.READ_SMS, R.string.per_read_sms_title, R.string.per_read_sms_des),
        SEND_SMS(Manifest.permission.SEND_SMS, R.string.per_send_sms_title, R.string.per_send_sms_des),
        CALL_PHONE(Manifest.permission.CALL_PHONE, R.string.per_call_phone_title, R.string.per_call_phone_des),
        PROCESS_OUTGOING_CALLS(Manifest.permission.PROCESS_OUTGOING_CALLS, R.string.per_outgoing_call_title, R.string.per_outgoing_call_des),
        RECORD_AUDIO(Manifest.permission.RECORD_AUDIO, R.string.per_record_audio_title, R.string.per_record_audio_des),
        CAMERA(Manifest.permission.CAMERA, R.string.per_camera_title, R.string.per_camera_des);

        public String name;
        public int title;
        public int description;

        PERMISSION_DANGEROUS(String name, int title, int description) {
            this.name = name;
            this.title = title;
            this.description = description;
        }
    }

    public static Config.FUNCTION getFunctionById(int id) {
        for (Config.FUNCTION mFunction : Config.FUNCTION.values()) {
            if (mFunction.id == id)
                return mFunction;
        }
        return null;
    }
}
