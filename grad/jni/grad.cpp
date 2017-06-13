#include <jni.h>
#include <vector>
#include <algorithm>
#include <ctime>

#include <android/log.h>

#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <opencv2/nonfree/features2d.hpp>

using namespace std;
using namespace cv;

extern "C" {
	JNIEXPORT void JNICALL Java_com_ayildiz_grad_CamActivity_Detect(JNIEnv*, jobject, jlong, jlong);
	JNIEXPORT void JNICALL Java_com_ayildiz_grad_MainActivity_Match(JNIEnv*, jobject, jlong, jlong, jlong);
	bool compare_response(KeyPoint, KeyPoint);

	JNIEXPORT void JNICALL Java_com_ayildiz_grad_CamActivity_Detect(JNIEnv*, jobject, jlong grayAddr, jlong rgbaAddr){
		Mat& gray_frame = *(Mat*)grayAddr;
		Mat& rgba_frame = *(Mat*)rgbaAddr;
		vector<KeyPoint> keypoints;

		//SiftFeatureDetector detector;
		FastFeatureDetector detector;
		detector.detect(gray_frame, keypoints);

		//drawKeypoints(gray_frame, keypoints, rgba_frame, Scalar(255,0,0,255),DrawMatchesFlags::DRAW_RICH_KEYPOINTS);

		for(int i = 0; i < keypoints.size(); i++){
			KeyPoint& k = keypoints[i];
			circle(rgba_frame, Point(k.pt.x, k.pt.y), 10, Scalar(255,0,0,255));
		}
	}

	JNIEXPORT void JNICALL Java_com_ayildiz_grad_MainActivity_Match(JNIEnv*, jobject, jlong previewAddr, jlong baseAddr, jlong outputAddr){
		Mat& preview_frame = *(Mat*)previewAddr;
		Mat& base_frame = *(Mat*)baseAddr;
		Mat& output_frame = *(Mat*)outputAddr;
		Mat flipped_frame = output_frame;
		clock_t begin = clock();

		//OrbFeatureDetector detector;
		SurfFeatureDetector detector;
		//SiftFeatureDetector detector;
		vector<KeyPoint> preview_keypoints, base_keypoints;
		detector.detect(preview_frame, preview_keypoints);
		detector.detect(base_frame, base_keypoints);

		clock_t end = clock();
		double elapsed = double(end - begin) / CLOCKS_PER_SEC;
		__android_log_print(ANDROID_LOG_INFO, "NATIVE - step", "%s: %f", "detect completed", elapsed);

		sort(preview_keypoints.begin(), preview_keypoints.end(), compare_response);
		sort(base_keypoints.begin(), base_keypoints.end(), compare_response);
		preview_keypoints.resize(500);
		base_keypoints.resize(500);

		end = clock();
		elapsed = (double(end - begin) / CLOCKS_PER_SEC) - elapsed;
		__android_log_print(ANDROID_LOG_INFO, "NATIVE - step", "%s: %f", "sort completed", elapsed);

		//OrbDescriptorExtractor extractor;
		//SiftDescriptorExtractor extractor;
		SurfDescriptorExtractor extractor;
		Mat preview_descriptors, base_descriptors;
		extractor.compute(preview_frame, preview_keypoints, preview_descriptors);
		extractor.compute(base_frame, base_keypoints, base_descriptors);

		end = clock();
		elapsed = (double(end - begin) / CLOCKS_PER_SEC) - elapsed;
		__android_log_print(ANDROID_LOG_INFO, "NATIVE - step", "%s: %f", "extract completed", elapsed);

		BFMatcher matcher;
		vector<DMatch> matches, good_matches;
		matcher.match(preview_descriptors, base_descriptors, matches);

		end = clock();
		elapsed = (double(end - begin) / CLOCKS_PER_SEC) - elapsed;
		__android_log_print(ANDROID_LOG_INFO, "NATIVE - step", "%s: %f", "match completed", elapsed);

		double min_dist = 100;
		for(int i = 0; i < preview_descriptors.rows; i++){
			double dist = matches[i].distance;
			if(dist < min_dist){
				min_dist = dist;
			}
		}

		__android_log_print(ANDROID_LOG_INFO, "NATIVE - min_dist", "%f", min_dist);

		for(int i = 0; i < preview_descriptors.rows; i++){
			if(matches[i].distance < 3 * min_dist){
				good_matches.push_back(matches[i]);
			}
		}

		__android_log_print(ANDROID_LOG_INFO, "NATIVE - match count", "%d", good_matches.size());

		drawMatches(preview_frame, preview_keypoints, base_frame, base_keypoints, good_matches, flipped_frame, Scalar::all(-1), Scalar::all(-1), vector<char>(), DrawMatchesFlags::NOT_DRAW_SINGLE_POINTS);
		flip(flipped_frame.t(), output_frame, 1);
	}

	bool compare_response(KeyPoint first, KeyPoint second){
		return first.response > second.response;
	}
}
