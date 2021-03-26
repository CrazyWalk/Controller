package cn.luyinbros.valleyframework.controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.ArrayRes;
import androidx.annotation.BoolRes;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.IntegerRes;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;

public final class Controllers {
    /**
     * Permission result: The permission is granted.
     */
    public static final int PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED;

    /**
     * Permission result: The permission is denied.
     */
    public static final int PERMISSION_DENIED = PackageManager.PERMISSION_DENIED;

    /**
     * Permission result: The permission is denied because the app op is not allowed.
     */
    public static final int PERMISSION_DENIED_APP_OP = PackageManager.PERMISSION_DENIED - 1;

    private Controllers() {
    }

    private static volatile ControllerPlugin controllerPlugin;

    public static void setControllerPlugin(ControllerPlugin plugin) {
        if (controllerPlugin != null) {
            throw new IllegalStateException("plugins is set");
        }
        controllerPlugin = plugin;
    }

    private static ControllerPlugin getPlugin() {
        if (controllerPlugin == null) {
            controllerPlugin = new ControllerPluginImpl();
        }
        return controllerPlugin;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void liveDataObserve(LifecycleOwner owner, LiveData liveData, Observer observer) {
        liveData.observe(owner, observer);
    }

    public static boolean isGranted(int[] grantResults) {
        return getPlugin().isGranted(grantResults);
    }

    public static int getPermissionResult(@NonNull Activity activity, @NonNull String[] permissions, @NonNull int[] grantResults) {
        return getPlugin().getPermissionResult(activity, permissions, grantResults);
    }

    public static int getPermissionResult(@NonNull Fragment fragment, @NonNull String[] permissions, @NonNull int[] grantResults) {
        return getPlugin().getPermissionResult(fragment.requireActivity(), permissions, grantResults);
    }


    public static <T extends View> T findViewById(@NonNull View parent, @IdRes int id) {
        return getPlugin().findViewById(parent, id);
    }

    public static <T extends View> T requiredViewById(@NonNull View parent, @IdRes int id) {
        return getPlugin().requiredViewById(parent, id);
    }

    public static int getInteger(Context context, @IntegerRes int id) {
        return getPlugin().getInteger(context, id);
    }

    public static int getColor(Context context, @ColorRes int id) {
        return getPlugin().getColor(context, id);
    }

    public static ColorStateList getColorStateList(Context context, @ColorRes int id) {
        return getPlugin().getColorStateList(context, id);
    }

    public static Drawable getDrawable(Context context, @DrawableRes int id) {
        return getPlugin().getDrawable(context, id);
    }

    public static String getString(Context context, @StringRes int id) {
        return getPlugin().getString(context, id);
    }

    public static boolean getBoolean(Context context, @BoolRes int id) {
        return getPlugin().getBoolean(context, id);
    }

    public static float getFloat(Context context, @DimenRes int id) {
        return getPlugin().getFloat(context, id);
    }

    public static int[] getIntArray(Context context, @ArrayRes int id) {
        return getPlugin().getIntArray(context, id);
    }

    public static String[] getStringArray(Context context, @ArrayRes int id) {
        return getPlugin().getStringArray(context, id);
    }


    /**
     * 权限是否相同
     *
     * @param current
     * @param other
     * @return
     */
    public static boolean isEqualPermissions(String[] current, String[] other) {
        for (int index = 0; index < current.length; index++) {
            if (!current[index].equals(other[index])) {
                return false;
            }
        }
        return true;
    }


    private static class ControllerPluginImpl implements ControllerPlugin {

        @Override
        public boolean isGranted(int[] grantResults) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int getPermissionResult(@NonNull Activity activity, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (permissions.length == 0) {
                throw new IllegalArgumentException("permissions is not empty");
            }
            if (isGranted(grantResults)) {
                return PERMISSION_GRANTED;
            } else if (isDeniedAppOp(activity, permissions)) {
                return PERMISSION_DENIED_APP_OP;
            } else {
                return PERMISSION_DENIED;
            }
        }

        @Override
        public <T extends View> T findViewById(@NonNull View parent, int id) {
            return parent.findViewById(id);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends View> T requiredViewById(@NonNull View parent, int id) {
            final View view = findViewById(parent, id);
            if (view == null) {
                throw new IllegalStateException(getResourceEntryName(parent, id) + " not found");
            }
            return (T) view;
        }

        @Override
        public int getInteger(Context context, int id) {
            return context.getResources().getInteger(id);
        }

        @Override
        public int getColor(Context context, int id) {
            return ContextCompat.getColor(context, id);
        }

        @Override
        public ColorStateList getColorStateList(Context context, int id) {
            return ContextCompat.getColorStateList(context, id);
        }

        @Override
        public Drawable getDrawable(Context context, int id) {
            return ContextCompat.getDrawable(context, id);
        }

        @Override
        public String getString(Context context, int id) {
            return context.getString(id);
        }

        @Override
        public boolean getBoolean(Context context, int id) {
            return context.getResources().getBoolean(id);
        }

        @Override
        public float getFloat(Context context, int id) {
            return context.getResources().getFloat(id);
        }

        @Override
        public int[] getIntArray(Context context, int id) {
            return context.getResources().getIntArray(id);
        }

        @Override
        public String[] getStringArray(Context context, int id) {
            return context.getResources().getStringArray(id);
        }


        private boolean isDeniedAppOp(@NonNull Activity activity,
                                      String... permissions) {
            Objects.requireNonNull(activity);
            for (String permission : permissions) {
                if (isDeniedAppOp(activity, permission)) {
                    return true;
                }
            }
            return false;
        }

        private boolean isDeniedAppOp(@NonNull Activity activity,
                                      String permission) {
            Objects.requireNonNull(activity);

            return !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
        }

        private String getResourceEntryName(View view, @IdRes int id) {
            if (view.isInEditMode()) {
                return "<unavailable while editing>";
            }
            return view.getContext().getResources().getResourceEntryName(id);
        }
    }

}
