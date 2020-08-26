package sp.android.fitnesstracker.play.util

enum class ActivityLevelConstant(val activityConstant: Float) {
    SEDENTARY(1.2f),
    LIGHTLY_ACTIVE(1.375f),
    MODERATELY_ACTIVE(1.55f),
    VERY_ACTIVE(1.725f),
    EXTRA_ACTIVE(1.9f),
    NONE(100f)
}