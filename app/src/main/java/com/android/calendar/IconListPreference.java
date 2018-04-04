package com.android.calendar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.hct.gios.preference.RingtonePreference;

public class IconListPreference extends RingtonePreference {
    private Drawable mIcon;
    private Resources resources;

    public IconListPreference(final Context context, final AttributeSet attrs,
            final int defStyle) {
        super(context, attrs);
        resources = context.getResources();
        this.setLayoutResource(R.layout.list_preference_chevron_right);

        //
        Drawable chevron_right = context.getResources().getDrawable(
                R.drawable.chevron_right);
        chevron_right.setTint(context.getResources().getColor(
                R.color.hui_icon_text_color));
        this.mIcon = chevron_right;
    }

    public IconListPreference(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @Override
    protected void onBindView(final View view) {
        super.onBindView(view);
        final ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        if ((imageView != null) && (this.mIcon != null)) {
            imageView.setImageDrawable(this.mIcon);
        }
    }

    /**
     * Sets the icon for this Preference with a Drawable.
     * 
     * @param icon
     *            The icon for this Preference
     */
    public void setIcon(final Drawable icon) {
        if (((icon == null) && (this.mIcon != null))
                || ((icon != null) && (!icon.equals(this.mIcon)))) {
            this.mIcon = icon;
            this.notifyChanged();
        }
    }

    public void setIcon(int iconRes) {
        if (R.drawable.chevron_right != iconRes) {
            this.mIcon = resources.getDrawable(iconRes);
            this.notifyChanged();
        }
    }

    /**
     * Returns the icon of this Preference.
     * 
     * @return The icon.
     * @see #setIcon(Drawable)
     */
    public Drawable getIcon() {
        return this.mIcon;
    }
}
