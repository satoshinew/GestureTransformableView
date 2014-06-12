#include <jni.h>
#include <math.h>
#include <android/log.h>
#include <float.h>

extern "C" {

#include "gesture.h"

static double to_degrees(float radians) {
	return radians * (180.0 / M_PI);
}

static double to_radians(float degrees) {
	return degrees / (180.0 / M_PI);
}

jfloatArray Java_jp_ogwork_gesturetransformableview_view_GestureTransformableImageView_nativeRotateXY(
		JNIEnv* env, jobject thisObject, float centerX, float centerY,
		float angle, float x, float y) {

	// declaration
	double rad = to_radians(angle);
	jfloatArray resultArray;
	jfloat result[2];
	jboolean success = true;

	// init
	resultArray = env->NewFloatArray(3);
	if (resultArray == NULL) {
		return NULL;
	}

	//__android_log_print(ANDROID_LOG_DEBUG, "Tag", "rad : [%f] angle : [%f]",
	//		rad, angle);

	result[0] = (float) ((x - centerX) * cos(rad) - (y - centerY) * sin(rad)
			+ centerX);
	result[1] = (float) ((x - centerX) * sin(rad) + (y - centerY) * cos(rad)
			+ centerY);

	env->SetFloatArrayRegion(resultArray, 0, 2, result);

	return resultArray;
}

jfloat Java_jp_ogwork_gesturetransformableview_gesture_RotateGestureDetector_nativeGetAngle(
		JNIEnv* env, jobject thisObject, float xi1, float yi1, float xm1,
		float ym1, float xi2, float yi2, float xm2, float ym2,
		jbooleanArray isSlopeZero, jfloatArray result) {

	// 2本の直線の傾き・y切片を算出
	float firstLinearSlope;
	if ((xm1 - xi1) != 0 && (ym1 - yi1) != 0) {
		firstLinearSlope = (xm1 - xi1) / (ym1 - yi1);
	} else {
		__android_log_write(ANDROID_LOG_DEBUG, "Tag", "[native] SLOPE 0");
		jboolean a[1] = { true };
		env->SetBooleanArrayRegion(isSlopeZero, 0, 1, a);
		return FLT_MAX;
	}

	float secondLinearSlope = (xm2 - xi2) / (ym2 - yi2);
	if ((xm2 - xi2) != 0 && (ym2 - yi2) != 0) {
		secondLinearSlope = (xm2 - xi2) / (ym2 - yi2);
	} else {
		__android_log_write(ANDROID_LOG_DEBUG, "Tag", "[native] SLOPE 0");
		jboolean a[1] = { true };
		env->SetBooleanArrayRegion(isSlopeZero, 0, 1, a);
		return FLT_MAX;
	}

	if (firstLinearSlope * secondLinearSlope == -1) {
		__android_log_write(ANDROID_LOG_DEBUG, "Tag", "[native] return -90.0f");
		return 90.0f;
	}

	float tan = (secondLinearSlope - firstLinearSlope)
			/ (1 + secondLinearSlope * firstLinearSlope);

	float degree = (float) atan(tan);

	jfloat resultDegree[] = { degree };
	env->SetFloatArrayRegion(result, 0, 1, resultDegree);

	return degree;
}

}
