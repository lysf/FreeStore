package third.com.snail.trafficmonitor.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import com.github.mikephil.charting.charts.PieRadarChartBase;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.GlobalUtil;
import com.github.mikephil.charting.utils.Legend.LegendPosition;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

/**
 * View that represents a pie chart. Draws cake like slices.
 *
 * @author Philipp Jahoda
 */
public class SummaryPieChart extends PieRadarChartBase<PieData> {

    /**
     * rect object that represents the bounds of the piechart, needed for
     * drawing the circle
     */
    private RectF mCircleBox = new RectF();

    /**
     * array that holds the width of each pie-slice in degrees
     */
    private float[] mDrawAngles;

    /**
     * array that holds the absolute angle in degrees of each slice
     */
    private float[] mAbsoluteAngles;

    /**
     * if true, the white hole inside the chart will be drawn
     */
    private boolean mDrawHole = true;

    /**
     * variable for the text that is drawn in the center of the pie-chart. If
     * this value is null, the default is "Total Value\n + getYValueSum()"
     */
    private String mCenterText = null;

    /**
     * indicates the size of the hole in the center of the piechart, default:
     * radius / 2
     */
    private float mHoleRadiusPercent = 50f;

    /**
     * the radius of the transparent circle next to the chart-hole in the center
     */
    private float mTransparentCircleRadius = 55f;

    /**
     * if enabled, centertext is drawn
     */
    private boolean mDrawCenterText = true;

    /**
     * set this to true to draw the x-values next to the values in the pie
     * slices
     */
    private boolean mDrawXVals = true;

    /**
     * if set to true, all values show up in percent instead of their real value
     */
    private boolean mUsePercentValues = false;

    /**
     * paint for the hole in the center of the pie chart and the transparent
     * circle
     */
    private Paint mHolePaint;

    /**
     * paint object for the text that can be displayed in the center of the
     * chart
     */
    private Paint mCenterTextPaint;

    public static class ExtraInfo {
        public String value;
        public String extra;
        public float valueSize;
        public float extraSize;
        public int color;

        public ExtraInfo(String value, String extra, float valueSize, float extraSize, int color) {
            this.value = value;
            this.extra = extra;
            this.valueSize = valueSize;
            this.extraSize = extraSize;
            this.color = color;
        }
    }

    private Paint mLinePaint;
    private Paint mExtraPaint;
    private ArrayList<ExtraInfo> mExtraInfo;

    private boolean mDrawExtra;
    private float mExtraSize;

    public void setExtraSize(float size) {
        mExtraSize = size;
    }

    public float getExtraSize() {
        return mExtraSize;
    }

    public SummaryPieChart(Context context) {
        super(context);
    }

    public SummaryPieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SummaryPieChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        // // piechart has no offsets
        // mOffsetTop = 0;
        // mOffsetBottom = 0;
        // mOffsetLeft = 0;
        // mOffsetRight = 0;

        mHolePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHolePaint.setColor(Color.WHITE);

        mCenterTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCenterTextPaint.setColor(mColorDarkBlue);
        mCenterTextPaint.setTextSize(Utils.convertDpToPixel(12f));
        mCenterTextPaint.setTextAlign(Align.CENTER);

        mValuePaint.setTextSize(Utils.convertDpToPixel(13f));
        mValuePaint.setColor(Color.WHITE);
        mValuePaint.setTextAlign(Align.CENTER);

        // for the piechart, drawing values is enabled
        mDrawYValues = true;

        mExtraPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public void setValueColor(int color) {
        mValuePaint.setColor(color);
    }

    public void setExtraInfo(ArrayList<ExtraInfo> info) {
        mExtraInfo = new ArrayList<ExtraInfo>(info);
    }

    public void setLinePaint(float size, int color) {
        mLinePaint.setStrokeWidth(size);
        mLinePaint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mDataNotSet)
            return;

        long starttime = System.currentTimeMillis();

        drawHighlights();

        drawData();

        drawAdditional();

        drawValues();

        drawExtra();

        drawLegend();

        drawDescription();

        drawCenterText();

        canvas.drawBitmap(mDrawBitmap, 0, 0, mDrawPaint);

        if (GlobalUtil.LOG_DEBUG) {
            Log.i(LOG_TAG, "PieChart DrawTime: " + (System.currentTimeMillis() - starttime) + " ms");
        }
    }

    /**
     * does all necessary preparations, needed when data is changed or flags
     * that effect the data are changed
     */
    @Override
    public void prepare() {
        super.prepare();

        if (mCenterText == null)
            mCenterText = "Total Value\n" + (int) getYValueSum();
    }

    @Override
    protected void prepareContentRect() {
        super.prepareContentRect();

        // prevent nullpointer when no data set
        if (mDataNotSet)
            return;

        float diameter = getDiameter();
        float boxSize = diameter / 2f;
        float extraSize = 0;
        if (mDrawExtra) {
            extraSize = mExtraSize;
        }

        PointF c = getCenterOffsets();

        // create the circle box that will contain the pie-chart (the bounds of
        // the pie-chart)
        mCircleBox.set(c.x - boxSize + extraSize, c.y - boxSize + extraSize,
                c.x + boxSize - extraSize, c.y + boxSize - extraSize);
    }

    @Override
    protected void calcMinMax(boolean fixedValues) {
        super.calcMinMax(fixedValues);

        calcAngles();
    }

    @Override
    protected void calculateOffsets() {

        if (mLegend == null)
            return;

        // setup offsets for legend
        if (mLegend.getPosition() == LegendPosition.RIGHT_OF_CHART) {

            mLegend.setOffsetRight(mLegend.getMaximumEntryLength(mLegendLabelPaint));
            mLegendLabelPaint.setTextAlign(Align.LEFT);

        } else if (mLegend.getPosition() == LegendPosition.BELOW_CHART_LEFT
                || mLegend.getPosition() == LegendPosition.BELOW_CHART_RIGHT
                || mLegend.getPosition() == LegendPosition.BELOW_CHART_CENTER) {

            mLegend.setOffsetBottom(mLegendLabelPaint.getTextSize() * 4f);
        }

        if (mDrawLegend) {

            mOffsetBottom = Math.max(mOffsetBottom, mLegend.getOffsetBottom());
            mOffsetRight = Math.max(mOffsetRight, mLegend.getOffsetRight() / 3 * 2);
        }

        mLegend.setOffsetTop(mOffsetTop);
        mLegend.setOffsetLeft(mOffsetLeft);

        applyCalculatedOffsets();
    }

    /**
     * calculates the needed angles for the chart slices
     */
    private void calcAngles() {

        mDrawAngles = new float[mCurrentData.getYValCount()];
        mAbsoluteAngles = new float[mCurrentData.getYValCount()];

        ArrayList<PieDataSet> dataSets = mCurrentData.getDataSets();

        int cnt = 0;

        for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

            PieDataSet set = dataSets.get(i);
            ArrayList<Entry> entries = set.getYVals();

            for (int j = 0; j < entries.size(); j++) {

                mDrawAngles[cnt] = calcAngle(entries.get(j).getVal());

                if (cnt == 0) {
                    mAbsoluteAngles[cnt] = mDrawAngles[cnt];
                } else {
                    mAbsoluteAngles[cnt] = mAbsoluteAngles[cnt - 1] + mDrawAngles[cnt];
                }

                cnt++;
            }
        }

    }

    @Override
    protected void drawHighlights() {

        // if there are values to highlight and highlighnting is enabled, do it
        if (mHighlightEnabled && valuesToHighlight()) {

            float angle = 0f;

            for (int i = 0; i < mIndicesToHightlight.length; i++) {

                // get the index to highlight
                int xIndex = mIndicesToHightlight[i].getXIndex();
                if (xIndex >= mDrawAngles.length || xIndex > mDeltaX * mPhaseX)
                    continue;

                if (xIndex == 0)
                    angle = mRotationAngle;
                else
                    angle = mRotationAngle + mAbsoluteAngles[xIndex - 1];

                angle *= mPhaseY;

                float sliceDegrees = mDrawAngles[xIndex];

                float shiftangle = (float) Math.toRadians(angle + sliceDegrees / 2f);

                PieDataSet set = mCurrentData
                        .getDataSetByIndex(mIndicesToHightlight[i]
                                .getDataSetIndex());

                float shift = set.getSelectionShift();
                float xShift = shift * (float) Math.cos(shiftangle);
                float yShift = shift * (float) Math.sin(shiftangle);

                RectF highlighted = new RectF(mCircleBox.left + xShift, mCircleBox.top + yShift,
                        mCircleBox.right
                                + xShift, mCircleBox.bottom + yShift);

                mRenderPaint.setColor(set.getColor(xIndex));

                // redefine the rect that contains the arc so that the
                // highlighted pie is not cut off
                mDrawCanvas.drawArc(highlighted, angle + set.getSliceSpace() / 2f, sliceDegrees
                        - set.getSliceSpace() / 2f, true, mRenderPaint);
            }
        }
    }

    @Override
    protected void drawData() {

        float angle = mRotationAngle;

        ArrayList<PieDataSet> dataSets = mCurrentData.getDataSets();

        int cnt = 0;

        for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

            PieDataSet dataSet = dataSets.get(i);
            ArrayList<Entry> entries = dataSet.getYVals();

            for (int j = 0; j < entries.size(); j++) {

                float newangle = mDrawAngles[cnt];
                float sliceSpace = dataSet.getSliceSpace();

                if (!needsHighlight(entries.get(j).getXIndex(), i)) {

                    mRenderPaint.setColor(dataSet.getColor(j));
                    mDrawCanvas.drawArc(mCircleBox, angle + sliceSpace / 2f, newangle * mPhaseY
                            - sliceSpace / 2f, true, mRenderPaint);
                }

                angle += newangle * mPhaseX;
                cnt++;
            }
        }
    }

    /**
     * draws the hole in the center of the chart and the transparent circle /
     * hole
     */
    private void drawHole() {

        if (mDrawHole) {

            float radius = getRadius();

            PointF c = getCenterCircleBox();

            // draw the hole-circle
            mDrawCanvas.drawCircle(c.x, c.y,
                    radius / 100 * mHoleRadiusPercent, mHolePaint);

            // make transparent
            mHolePaint.setColor(mBackgroundColor);

            // draw the transparent-circle
            mDrawCanvas.drawCircle(c.x, c.y,
                    radius / 100 * mTransparentCircleRadius, mHolePaint);
        }
    }

    /**
     * draws the description text in the center of the pie chart makes most
     * sense when center-hole is enabled
     */
    private void drawCenterText() {

        if (mDrawCenterText) {

            PointF c = getCenterCircleBox();

            // get all lines from the text
            String[] lines = mCenterText.split("\n");

            // calculate the height for each line
            float lineHeight = Utils.calcTextHeight(mCenterTextPaint, lines[0]);
            float linespacing = lineHeight * 0.2f;

            float totalheight = lineHeight * lines.length - linespacing * (lines.length - 1);

            int cnt = lines.length;

            float y = c.y;

            for (int i = 0; i < lines.length; i++) {

                String line = lines[lines.length - i - 1];

                mDrawCanvas.drawText(line, c.x, y
                                + lineHeight * cnt - totalheight / 2f,
                        mCenterTextPaint);
                cnt--;
                y -= linespacing;
            }
        }
    }

    @Override
    protected void drawValues() {

        // if neither xvals nor yvals are drawn, return
        if (!mDrawXVals && !mDrawYValues)
            return;

        PointF center = getCenterCircleBox();

        // get whole the radius
        float r = getRadius();

        float off = r / 2f;

        if (mDrawHole) {
            off = (r - (r / 100f * mHoleRadiusPercent)) / 2f;
        }

        r -= off; // offset to keep things inside the chart

        ArrayList<PieDataSet> dataSets = mCurrentData.getDataSets();

        int cnt = 0;

        for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {

            PieDataSet dataSet = dataSets.get(i);
            ArrayList<Entry> entries = dataSet.getYVals();

            for (int j = 0; j < entries.size() * mPhaseX; j++) {

                // offset needed to center the drawn text in the slice
                float offset = mDrawAngles[cnt] / 2;

                // calculate the text position
                float x = (float) (r
                        * Math.cos(Math.toRadians((mRotationAngle + mAbsoluteAngles[cnt] - offset)
                        * mPhaseY)) + center.x);
                float y = (float) (r
                        * Math.sin(Math.toRadians((mRotationAngle + mAbsoluteAngles[cnt] - offset)
                        * mPhaseY)) + center.y);

                String val = "";
                float value = entries.get(j).getVal();

                if (mUsePercentValues)
                    val = mValueFormat.format(getPercentOfTotal(value)) + " %";
                else
                    val = mValueFormat.format(value);

                if (mDrawUnitInChart)
                    val = val + mUnit;

                // draw everything, depending on settings
                if (mDrawXVals && mDrawYValues) {

                    // use ascent and descent to calculate the new line
                    // position,
                    // 1.6f is the line spacing
                    float lineHeight = (mValuePaint.ascent() + mValuePaint.descent()) * 1.2f/*1.6f*/;
                    y -= lineHeight / 2;

                    mDrawCanvas.drawText(val, x, y, mValuePaint);
                    mDrawCanvas.drawText(mCurrentData.getXVals().get(j), x, y + lineHeight,
                            mValuePaint);

                } else if (mDrawXVals && !mDrawYValues) {
                    mDrawCanvas.drawText(mCurrentData.getXVals().get(j), x, y, mValuePaint);
                } else if (!mDrawXVals && mDrawYValues) {

                    mDrawCanvas.drawText(val, x, y, mValuePaint);
                }

                cnt++;
            }
        }
    }

    private void drawExtra() {
        if (!mDrawExtra) return;

        float r = getRadius();
        PointF center = getCenterCircleBox();

        ArrayList<PieDataSet> dataSets = mCurrentData.getDataSets();

        float minAngle = 360f;
        for (Float a : mDrawAngles) {
            if (minAngle > a) {
                minAngle = a;
            }
        }
        Log.d("DEBUG", "Minimum angle is " + minAngle);
        float angle1 = minAngle * 20f / 100f;
        float angle2 = minAngle / 2f - angle1;

        float[] anchors = new float[2];
        if (mRotationAngle > 180) {
            anchors[0] = mRotationAngle + angle1;
            anchors[1] = 90f + angle2;
        } else {
            anchors[0] = 270f - angle1;
            anchors[1] = mRotationAngle - angle2;
        }
        int cnt = 0;
        for (int i = 0; i < mCurrentData.getDataSetCount(); i++) {
            PieDataSet dataSet = dataSets.get(i);
            ArrayList<Entry> entries = dataSet.getYVals();

            for (int j = 0; j < entries.size() * mPhaseX; j++) {
                float x = (float) (r * Math.cos(Math.toRadians(anchors[cnt])) + center.x);
                float y = (float) (r * Math.sin(Math.toRadians(anchors[cnt])) + center.y);
                if (mDebug) {
                    mDrawCanvas.drawPoint(x, y, mDebugPaint);
                }

                float ex = (float) ((r + mExtraSize * 0.5f) * Math.cos(Math.toRadians(anchors[cnt])) + center.x);
                float ey = (float) ((r + mExtraSize * 0.5f) * Math.sin(Math.toRadians(anchors[cnt])) + center.y);
                if (mDebug) {
                    mDrawCanvas.drawPoint(ex, ey, mDebugPaint);
                }
                mDrawCanvas.drawLine(x, y, ex, ey, mLinePaint);

                mDrawCanvas.drawLine(ex, ey, mExtraSize, ey, mLinePaint);
                ExtraInfo extra = mExtraInfo.get(cnt);
                mExtraPaint.setColor(extra.color);
                // draw extra
                mExtraPaint.setFakeBoldText(false);
                mExtraPaint.setTextSize(extra.extraSize);
                mDrawCanvas.drawText(extra.extra, mExtraSize, ey - mExtraPaint.ascent(), mExtraPaint);
                // draw value
                mExtraPaint.setFakeBoldText(true);
                mExtraPaint.setTextSize(extra.valueSize);
                mDrawCanvas.drawText(extra.value, mExtraSize, ey - mExtraPaint.descent(), mExtraPaint);
                cnt++;
            }
        }
        if (mDebug) {
            mDrawCanvas.drawLine(0, mCircleBox.top - mExtraSize, 200, mCircleBox.top - mExtraSize, mDebugPaint);
            mDrawCanvas.drawLine(0, mCircleBox.bottom + mExtraSize, 200, mCircleBox.bottom + mExtraSize, mDebugPaint);
        }
    }

    @Override
    protected void drawAdditional() {
        drawHole();
    }

    /**
     * calculates the needed angle for a given value
     *
     * @param value
     * @return
     */
    private float calcAngle(float value) {
        return value / mCurrentData.getYValueSum() * 360f;
    }

    @Override
    public int getIndexForAngle(float angle) {

        // take the current angle of the chart into consideration
        float a = (angle - mRotationAngle + 360) % 360f;

        for (int i = 0; i < mAbsoluteAngles.length; i++) {
            if (mAbsoluteAngles[i] > a)
                return i;
        }

        return -1; // return -1 if no index found
    }

    /**
     * Returns the index of the DataSet this x-index belongs to.
     *
     * @param xIndex
     * @return
     */
    public int getDataSetIndexForIndex(int xIndex) {

        ArrayList<? extends DataSet<? extends Entry>> dataSets = mCurrentData.getDataSets();

        for (int i = 0; i < dataSets.size(); i++) {
            if (dataSets.get(i).getEntryForXIndex(xIndex) != null)
                return i;
        }

        return -1;
    }

    /**
     * returns an integer array of all the different angles the chart slices
     * have the angles in the returned array determine how much space (of 360°)
     * each slice takes
     *
     * @return
     */
    public float[] getDrawAngles() {
        return mDrawAngles;
    }

    /**
     * returns the absolute angles of the different chart slices (where the
     * slices end)
     *
     * @return
     */
    public float[] getAbsoluteAngles() {
        return mAbsoluteAngles;
    }

    /**
     * set this to true to draw the pie center empty
     *
     * @param enabled
     */
    public void setDrawHoleEnabled(boolean enabled) {
        this.mDrawHole = enabled;
    }

    /**
     * returns true if the hole in the center of the pie-chart is set to be
     * visible, false if not
     *
     * @return
     */
    public boolean isDrawHoleEnabled() {
        return mDrawHole;
    }

    /**
     * sets the text that is displayed in the center of the pie-chart. By
     * default, the text is "Total Value + sumofallvalues"
     *
     * @param text
     */
    public void setCenterText(String text) {
        mCenterText = text;
    }

    /**
     * returns the text that is drawn in the center of the pie-chart
     *
     * @return
     */
    public String getCenterText() {
        return mCenterText;
    }

    /**
     * set this to true to draw the text that is displayed in the center of the
     * pie chart
     *
     * @param enabled
     */
    public void setDrawCenterText(boolean enabled) {
        this.mDrawCenterText = enabled;
    }

    /**
     * returns true if drawing the center text is enabled
     *
     * @return
     */
    public boolean isDrawCenterTextEnabled() {
        return mDrawCenterText;
    }

    /**
     * set this to true to draw percent values instead of the actual values
     *
     * @param enabled
     */
    public void setUsePercentValues(boolean enabled) {
        mUsePercentValues = enabled;
    }

    /**
     * returns true if drawing percent values is enabled
     *
     * @return
     */
    public boolean isUsePercentValuesEnabled() {
        return mUsePercentValues;
    }

    /**
     * set this to true to draw the x-value text into the pie slices
     *
     * @param enabled
     */
    public void setDrawXValues(boolean enabled) {
        mDrawXVals = enabled;
    }

    /**
     * returns true if drawing x-values is enabled, false if not
     *
     * @return
     */
    public boolean isDrawXValuesEnabled() {
        return mDrawXVals;
    }

    @Override
    public float getRadius() {
        if (mCircleBox == null)
            return 0;
        else
            return Math.min(mCircleBox.width() / 2f, mCircleBox.height() / 2f);
    }

    /**
     * returns the circlebox, the boundingbox of the pie-chart slices
     *
     * @return
     */
    public RectF getCircleBox() {
        return mCircleBox;
    }

    /**
     * returns the center of the circlebox
     *
     * @return
     */
    public PointF getCenterCircleBox() {
        return new PointF(mCircleBox.centerX(), mCircleBox.centerY());
    }

    /**
     * sets the typeface for the center-text paint
     *
     * @param t
     */
    public void setCenterTextTypeface(Typeface t) {
        mCenterTextPaint.setTypeface(t);
    }

    /**
     * Sets the size of the center text of the piechart.
     *
     * @param size
     */
    public void setCenterTextSize(float size) {
        mCenterTextPaint.setTextSize(Utils.convertDpToPixel(size));
    }

    /**
     * sets the radius of the hole in the center of the piechart in percent of
     * the maximum radius (max = the radius of the whole chart), default 50%
     *
     * @param percent
     */
    public void setHoleRadius(final float percent) {
        mHoleRadiusPercent = percent;
    }

    /**
     * sets the radius of the transparent circle that is drawn next to the hole
     * in the piechart in percent of the maximum radius (max = the radius of the
     * whole chart), default 55% -> means 5% larger than the center-hole by
     * default
     *
     * @param percent
     */
    public void setTransparentCircleRadius(final float percent) {
        mTransparentCircleRadius = percent;
    }

    @Override
    public void setPaint(Paint p, int which) {
        super.setPaint(p, which);

        switch (which) {
            case PAINT_HOLE:
                mHolePaint = p;
                break;
            case PAINT_CENTER_TEXT:
                mCenterTextPaint = p;
                break;
        }
    }

    @Override
    public Paint getPaint(int which) {
        Paint p = super.getPaint(which);
        if (p != null)
            return p;

        switch (which) {
            case PAINT_HOLE:
                return mHolePaint;
            case PAINT_CENTER_TEXT:
                return mCenterTextPaint;
        }

        return null;
    }

    public boolean getDrawExtra() {
        return mDrawExtra;
    }

    public void setDrawExtra(boolean drawExtra) {
        this.mDrawExtra = drawExtra;
    }
}
