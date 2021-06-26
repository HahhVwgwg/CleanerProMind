package promind.cleaner.app.ui.activities.cleanNotification;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import promind.cleaner.app.R;
import promind.cleaner.app.core.utils.BaseApp;
import promind.cleaner.app.core.utils.Config;
import promind.cleaner.app.ui.activities.BaseActivity;
import promind.cleaner.app.ui.dialog.DialogConfirmCancel;

public class NotificationCleanGuildActivity extends BaseActivity {

    private final List<ObjectAnimator> listAnimation = new ArrayList();
    @BindView(R.id.iv_start_0_0)
    View mIvStart00;
    @BindView(R.id.iv_start_0_1)
    View mIvStart01;
    @BindView(R.id.iv_start_0_2)
    View mIvStart02;
    @BindView(R.id.iv_start_1_0)
    View mIvStart10;
    @BindView(R.id.iv_start_1_1)
    View mIvStart11;
    @BindView(R.id.iv_start_1_2)
    View mIvStart12;
    @BindView(R.id.iv_start_1_3)
    View mIvStart13;
    @BindView(R.id.tv_label)
    TextView mTvLabel;
    @BindView(R.id.im_back_toolbar)
    ImageView imBackToolbar;
    @BindView(R.id.tv_toolbar)
    TextView tvToolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_notification_guild);
        ButterKnife.bind(this);
        imBackToolbar.setVisibility(View.VISIBLE);
        imBackToolbar.setOnClickListener(v ->
        {
            DialogConfirmCancel dialogConfirmCancel = new DialogConfirmCancel(this, Config.FUNCTION.NOTIFICATION_MANAGER.id);
            dialogConfirmCancel.setListener(new DialogConfirmCancel.OnClickListener() {
                @Override
                public void onDo() {
                }

                @Override
                public void onCancel() {
                    finish();
                }
            });
            dialogConfirmCancel.show();
        });

        tvToolbar.setText(getString(R.string.notification_manager));

    }

    public void u() {
        ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this.mTvLabel, View.ALPHA, 1.0f, Float.intBitsToFloat(1));
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new Animator.AnimatorListener() {
            public void onAnimationCancel(Animator animator) {
            }

            public void onAnimationRepeat(Animator animator) {
            }

            public void onAnimationStart(Animator animator) {
            }

            public void onAnimationEnd(Animator animator) {

            }
        });
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.start();
    }

    public void a(View view, final int i) {
        if (view != null) {
            view.setPivotX((float) (view.getWidth() / 2));
            view.setPivotY(Float.intBitsToFloat(1));
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, View.SCALE_X, 1.0f, Float.intBitsToFloat(1));
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1.0f, Float.intBitsToFloat(1));
            AnimatorSet.Builder with = animatorSet.play(ofFloat).with(ofFloat2).with(ObjectAnimator.ofFloat(view, View.ALPHA, 1.0f, Float.intBitsToFloat(1)));
            if (i == 0) {
            }
            if (i == 0 || i == 1) {
            }
            animatorSet.addListener(new Animator.AnimatorListener() {
                public void onAnimationCancel(Animator animator) {
                }

                public void onAnimationRepeat(Animator animator) {
                }

                public void onAnimationStart(Animator animator) {
                }

                public void onAnimationEnd(Animator animator) {
                    if (!NotificationCleanGuildActivity.this.isFinishing()) {
                        NotificationCleanGuildActivity.this.d(i);
                        int tmp = i + 1;
                        if (tmp > 2) {
                            BaseApp.a(NotificationCleanGuildActivity.this::v, 400);
                        }
                    }
                }
            });
            animatorSet.setDuration(650);
            animatorSet.setInterpolator(new LinearInterpolator());
            animatorSet.start();
        }
    }

    public void d(int i) {
        if (this.mTvLabel != null) {
            this.mTvLabel.setAlpha(1.0f);
            this.mTvLabel.setText(getString(R.string.Notificationbar_Spamnotification));
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(new LinearInterpolator());
            animatorSet.start();
        }
    }

    public void v() {
    }

    public void A() {
        animationModel(this.mIvStart00, 0).start();
        animationModel(this.mIvStart01, 0).start();
        animationModel(this.mIvStart02, 0).start();
        animationModel(this.mIvStart10, 800).start();
        animationModel(this.mIvStart11, 800).start();
        animationModel(this.mIvStart12, 800).start();
        animationModel(this.mIvStart13, 800).start();
    }

    private ObjectAnimator animationModel(final View view, long j) {
        ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat(View.SCALE_X, Float.intBitsToFloat(1), 1.0f, Float.intBitsToFloat(1)),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, Float.intBitsToFloat(1), 1.0f, Float.intBitsToFloat(1)),
                PropertyValuesHolder.ofFloat(View.ALPHA, Float.intBitsToFloat(1), 1.0f, Float.intBitsToFloat(1)));
        ofPropertyValuesHolder.addListener(new Animator.AnimatorListener() {
            public void onAnimationCancel(Animator animator) {
            }

            public void onAnimationEnd(Animator animator) {
            }

            public void onAnimationRepeat(Animator animator) {
            }

            public void onAnimationStart(Animator animator) {
                view.setVisibility(View.VISIBLE);
            }
        });
        ofPropertyValuesHolder.setRepeatCount(-1);
        ofPropertyValuesHolder.setStartDelay(j);
        ofPropertyValuesHolder.setInterpolator(new LinearInterpolator());
        ofPropertyValuesHolder.setDuration(1600);
        listAnimation.add(ofPropertyValuesHolder);
        return ofPropertyValuesHolder;
    }


    public void onStop() {
        if (isFinishing()) {
            for (ObjectAnimator cancel : listAnimation) {
                cancel.cancel();
            }
        }
        super.onStop();
    }

    @OnClick(R.id.btn_open)
    public void open() {
        try {
            askPermissionNotificaitonSetting(() -> {
                startActivity(new Intent(this, NotificationCleanSettingActivity.class));
                finish();
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
