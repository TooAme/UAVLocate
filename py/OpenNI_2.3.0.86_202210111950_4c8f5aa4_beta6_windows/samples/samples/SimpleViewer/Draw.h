

#ifndef DRAW_H
#define DRAW_H

#pragma once

#include <iostream>
#include <string>

//#include "opencv/highgui.h"

#include "opencv2/highgui.hpp"
#include "opencv2/opencv.hpp"
#include "opencv2/imgproc/imgproc_c.h"
#include "opencv2/core/core.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/calib3d/calib3d.hpp"
#include "opencv2/imgcodecs.hpp"
#include <vector>

using namespace std;
using namespace cv;

class Draw
{
public:
    Draw();
    virtual ~Draw();

public:
    void drawText(char* textToDraw, int x, int y);
    //void ShowImagesSideBySide(char* title, Vector<IplImage> images, char* text, int X_textOnWindow, int Y_textOnWindow);
	void ShowImagesSideBySide(char* title, vector<IplImage> images, char* text, int X_textOnWindow, int Y_textOnWindow);
    void GetDepthHistogram(cv::Mat &src, cv::Mat &dst);

private:
    IplImage IplRgb, IplDepth;
    IplImage* DisplayImage;

    CvFont font;
};


#endif