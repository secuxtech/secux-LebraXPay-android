package com.secuxtech.libraxpay.Utility;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.journeyapps.barcodescanner.BarcodeView;
import com.journeyapps.barcodescanner.Size;

/**
 * Created by maochuns.sun@gmail.com on 2020-02-24
 */
public class SecuXBarcodePreview extends BarcodeView {

    public SecuXBarcodePreview(Context context) {
        super(context);

    }

    public SecuXBarcodePreview(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public SecuXBarcodePreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected Rect calculateFramingRect(Rect container, Rect surface) {
        // intersection is the part of the container that is used for the preview
        Rect intersection = new Rect(container);
        boolean intersects = intersection.intersect(surface);

        // [Custom] Get private variables by getter
        Size framingRectSize = getFramingRectSize();
        double marginFraction = getMarginFraction();

        if(framingRectSize != null) {
            // Specific size is specified. Make sure it's not larger than the container or surface.
            int horizontalMargin = Math.max(0, (intersection.width() - framingRectSize.width) / 2);
            int verticalMargin = Math.max(0, (intersection.height() - framingRectSize.height) / 2);
            intersection.inset(horizontalMargin, verticalMargin);

            // [Custom] Move down the framing rectangle
            intersection.offset(0, 0);

            return intersection;
        }
        // margin as 10% (default) of the smaller of width, height
        int margin = (int)Math.min(intersection.width() * marginFraction, intersection.height() * marginFraction);
        intersection.inset(margin, margin);
        if (intersection.height() > intersection.width()) {
            // We don't want a frame that is taller than wide.
            intersection.inset(0, (intersection.height() - intersection.width()) / 2);
        }
        return intersection;
    }
}
